package com.chenguang.simpleclock.addalarm

import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.chenguang.simpleclock.R
import com.chenguang.simpleclock.model.AlarmData
import com.chenguang.simpleclock.model.AlarmSound
import com.chenguang.simpleclock.util.AlarmHelper
import com.chenguang.simpleclock.util.Constants
import com.chenguang.simpleclock.util.hideKeyboard
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_add_alarm.add_alarm_activity_alarm_title_edit_text
import kotlinx.android.synthetic.main.activity_add_alarm.add_alarm_activity_cancel_button
import kotlinx.android.synthetic.main.activity_add_alarm.add_alarm_activity_done_fab
import kotlinx.android.synthetic.main.activity_add_alarm.add_alarm_activity_load_sound_progress_bar
import kotlinx.android.synthetic.main.activity_add_alarm.add_alarm_activity_no_sound_text
import kotlinx.android.synthetic.main.activity_add_alarm.add_alarm_activity_repeat_recycler_view
import kotlinx.android.synthetic.main.activity_add_alarm.add_alarm_activity_sound_spinner
import kotlinx.android.synthetic.main.activity_add_alarm.add_alarm_activity_time_picker
import kotlinx.android.synthetic.main.layout_progress_bar.progress_bar_dim_view_container
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Random
import javax.inject.Inject

/**
 * Activity class for adding new alarm
 */
class AddAlarmActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModel: AddAlarmActivityViewModel

    @Inject
    lateinit var alarmHelper: AlarmHelper

    private lateinit var repeatDayAdapter: AlarmRepeatItemAdapter
    private lateinit var spinnerAdapter: ArrayAdapter<String>

    private val random by lazy { Random() }
    private val defaultAlarmTitle by lazy { getString(R.string.default_alarm_title_text) }
    private val alarmSoundMap = mutableMapOf<String, AlarmSound>()
    private var alarmSoundList = listOf<AlarmSound>()
    private var editAlarmData: AlarmData? = null
    private var selectedAlarmSoundUri: Uri? = null
    private var playingRingtone: Ringtone? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_alarm)

        // Custom linear layout manager to disable scrolling
        val layoutManager = object : LinearLayoutManager(
            this, HORIZONTAL, false
        ) {
            override fun canScrollHorizontally(): Boolean {
                return false
            }
        }
        add_alarm_activity_repeat_recycler_view.layoutManager = layoutManager
        repeatDayAdapter = AlarmRepeatItemAdapter(this)
        add_alarm_activity_repeat_recycler_view.adapter = repeatDayAdapter
        add_alarm_activity_repeat_recycler_view.addItemDecoration(
            RepeatOptionEvenSpaceItemDecoration(this)
        )

        add_alarm_activity_done_fab.setOnClickListener {
            addAlarmAndFinish()
        }

        add_alarm_activity_alarm_title_edit_text.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(this, add_alarm_activity_alarm_title_edit_text)
                add_alarm_activity_alarm_title_edit_text.clearFocus()
                true
            } else false
        }

        add_alarm_activity_cancel_button.setOnClickListener {
            playingRingtone?.stop()
            finishAfterTransition()
        }

        lifecycleScope.launch(Dispatchers.Main) {
            if (intent.hasExtra(Constants.EXTRA_ALARM_ID)) {
                val editAlarmId = intent.getIntExtra(Constants.EXTRA_ALARM_ID, 0)
                val editAlarmData = viewModel.getAlarmById(editAlarmId)
                editAlarmData?.let {
                    this@AddAlarmActivity.editAlarmData = it
                    add_alarm_activity_time_picker.currentHour = it.alarmHour
                    add_alarm_activity_time_picker.currentMinute = it.alarmMinute
                    add_alarm_activity_alarm_title_edit_text.setText(it.title)
                    add_alarm_activity_done_fab.setImageResource(R.drawable.ic_check)
                    repeatDayAdapter.updateSelectedRepeatDayIdList(it.repeatDayIdList)
                }
            }
            val context = this@AddAlarmActivity
            alarmSoundList = viewModel.fetchAllAlarmSounds(context)
            add_alarm_activity_load_sound_progress_bar.visibility = View.GONE
            if (alarmSoundList.isEmpty()) {
                add_alarm_activity_no_sound_text.visibility = View.VISIBLE
            } else {
                alarmSoundMap.putAll(alarmSoundList.map { it.name to it })
                setupSoundSpinner(alarmSoundList)
            }
        }
    }

    private fun setupSoundSpinner(alarmSoundList: List<AlarmSound>) {
        spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        add_alarm_activity_sound_spinner.adapter = spinnerAdapter
        spinnerAdapter.addAll(alarmSoundList.map { it.name })
        editAlarmData?.soundUri?.let { editAlarmSoundUri ->
            selectedAlarmSoundUri = editAlarmSoundUri
            val position = alarmSoundList.indexOfFirst { editAlarmSoundUri == it.uri }
            add_alarm_activity_sound_spinner.setSelection(position, false)
        }
        add_alarm_activity_sound_spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // No-op
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    spinnerAdapter.getItem(position)?.let {
                        playingRingtone?.stop()
                        selectedAlarmSoundUri = alarmSoundMap[it]?.uri
                        selectedAlarmSoundUri?.let { uri ->
                            val ringtone = RingtoneManager.getRingtone(this@AddAlarmActivity, uri)
                            playingRingtone = ringtone
                            ringtone.play()
                        }
                    }
                }

            }
        add_alarm_activity_sound_spinner.visibility = View.VISIBLE
    }

    private fun addAlarmAndFinish() {
        playingRingtone?.stop()
        progress_bar_dim_view_container.visibility = View.VISIBLE

        // Create alarm data with selected time, repeat option and sound
        val alarmTitle = add_alarm_activity_alarm_title_edit_text.text?.toString()
        val alarmHour = add_alarm_activity_time_picker.currentHour
        val alarmMinute = add_alarm_activity_time_picker.currentMinute
        val selectedRepeatDays = repeatDayAdapter.getSortedSelectedDayIdList()
        val alarmTime = alarmHelper.getAvailableAlarmTime(alarmHour, alarmMinute)

        editAlarmData?.let {
            if (alarmTitle == it.title &&
                alarmTime == it.timeMillis &&
                alarmHour == it.alarmHour &&
                alarmMinute == it.alarmMinute &&
                selectedAlarmSoundUri == it.soundUri &&
                selectedRepeatDays.toTypedArray().contentDeepEquals(
                    it.repeatDayIdList.toTypedArray()
                )
            ) {
                // If nothing changed, directly return
                finishAfterTransition()
                return
            } else {
                // Cancel previously set alarm
                alarmHelper.cancelAlarmInBackground(applicationContext, it)
            }
        }

        val alarmData = createAlarmData(
            id = editAlarmData?.id,
            title = alarmTitle,
            timeMillis = alarmTime,
            alarmHour = alarmHour,
            alarmMinute = alarmMinute,
            repeatDayIdList = selectedRepeatDays,
            soundUri = selectedAlarmSoundUri
        )

        // Schedule alarm properly
        alarmHelper.scheduleAlarmInBackground(applicationContext, alarmData)

        // Save alarm data to database
        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.insertOrUpdateAlarm(alarmData)
            finishAfterTransition()
        }
    }

    private fun createAlarmData(
        id: Int?,
        title: String?,
        timeMillis: Long,
        alarmHour: Int,
        alarmMinute: Int,
        repeatDayIdList: List<Int>,
        soundUri: Uri?
    ): AlarmData {
        val alarmId = id ?: random.nextInt()
        val alarmTitle = if (title.isNullOrEmpty()) defaultAlarmTitle else title
        return AlarmData(
            id = alarmId,
            title = alarmTitle,
            createTimestamp = System.currentTimeMillis(),
            timeMillis = timeMillis,
            alarmHour = alarmHour,
            alarmMinute = alarmMinute,
            soundUri = soundUri,
            repeatDayIdList = repeatDayIdList,
            enabled = true
        )
    }
}
