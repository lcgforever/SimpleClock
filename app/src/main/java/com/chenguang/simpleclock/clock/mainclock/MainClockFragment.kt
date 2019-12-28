package com.chenguang.simpleclock.clock.mainclock

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.chenguang.simpleclock.R
import com.chenguang.simpleclock.addalarm.AddAlarmActivity
import com.chenguang.simpleclock.clock.clockdetail.ClockDetailFragment
import com.chenguang.simpleclock.clock.clocktimezone.ClockTimezoneListFragment
import com.chenguang.simpleclock.model.AlarmData
import com.chenguang.simpleclock.util.AlarmHelper
import com.chenguang.simpleclock.util.Constants
import com.chenguang.simpleclock.util.SwipeToDeleteCallback
import com.chenguang.simpleclock.util.TimeFormatHelper
import com.chenguang.simpleclock.util.convertDpToPixel
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_main_clock.main_clock_fragment_add_alarm_button
import kotlinx.android.synthetic.main.fragment_main_clock.main_clock_fragment_alarm_recycler_view
import kotlinx.android.synthetic.main.fragment_main_clock.main_clock_fragment_card_view
import kotlinx.android.synthetic.main.fragment_main_clock.main_clock_fragment_globe_image_view
import kotlinx.android.synthetic.main.fragment_main_clock.main_clock_fragment_indicator_1
import kotlinx.android.synthetic.main.fragment_main_clock.main_clock_fragment_indicator_2
import kotlinx.android.synthetic.main.fragment_main_clock.main_clock_fragment_view_pager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Fragment class handling main clock page content
 */
class MainClockFragment : Fragment(), MainClockAlarmItemAdapter.AlarmItemListener {

    companion object {
        private const val TRANSLATION_Y = 2000F
    }

    @Inject
    lateinit var viewModel: MainClockFragmentViewModel

    @Inject
    lateinit var timeFormatHelper: TimeFormatHelper

    @Inject
    lateinit var alarmHelper: AlarmHelper

    private lateinit var alarmAdapter: MainClockAlarmItemAdapter

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_clock, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val screenWidth = context!!.resources.displayMetrics.widthPixels
        val distanceX = screenWidth - convertDpToPixel(140F, context!!)
        main_clock_fragment_view_pager.orientation = ORIENTATION_HORIZONTAL
        main_clock_fragment_view_pager.adapter = ClockFragmentStateAdapter(this)
        main_clock_fragment_view_pager.registerOnPageChangeCallback(
            object : OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    if (positionOffset == 0F || positionOffset == 1F) {
                        return
                    }
                    main_clock_fragment_globe_image_view.translationX =
                        -distanceX * positionOffset
                    main_clock_fragment_globe_image_view.scaleX = 1F - 0.4F * positionOffset
                    main_clock_fragment_globe_image_view.scaleY = 1F - 0.4F * positionOffset
                    main_clock_fragment_card_view.translationY = TRANSLATION_Y * positionOffset
                }

                override fun onPageSelected(position: Int) {
                    when (position) {
                        0 -> {
                            main_clock_fragment_indicator_1.isSelected = true
                            main_clock_fragment_indicator_2.isSelected = false
                            main_clock_fragment_globe_image_view.translationX = 0F
                            main_clock_fragment_globe_image_view.scaleX = 1F
                            main_clock_fragment_globe_image_view.scaleY = 1F
                            main_clock_fragment_card_view.translationY = 0F
                        }
                        else -> {
                            main_clock_fragment_indicator_1.isSelected = false
                            main_clock_fragment_indicator_2.isSelected = true
                            main_clock_fragment_globe_image_view.translationX = -distanceX
                            main_clock_fragment_globe_image_view.scaleX = 0.6F
                            main_clock_fragment_globe_image_view.scaleY = 0.6F
                            main_clock_fragment_card_view.translationY = TRANSLATION_Y
                        }
                    }
                }
            }
        )
        main_clock_fragment_view_pager.currentItem = 0

        main_clock_fragment_add_alarm_button.setOnClickListener {
            startAddAlarmActivity()
        }

        main_clock_fragment_alarm_recycler_view.layoutManager = LinearLayoutManager(context!!)
        alarmAdapter = MainClockAlarmItemAdapter(context!!)
        main_clock_fragment_alarm_recycler_view.adapter = alarmAdapter
        val itemTouchHelper = ItemTouchHelper(AlarmSwipeToDeleteCallback(context!!))
        itemTouchHelper.attachToRecyclerView(main_clock_fragment_alarm_recycler_view)
    }

    override fun onStart() {
        super.onStart()
        alarmAdapter.initialize(this)
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch(Dispatchers.Main) {
            val alarmDataList = viewModel.loadAllAlarms()
            alarmAdapter.updateAlarmDataList(alarmDataList)
        }
    }

    override fun onStop() {
        super.onStop()
        alarmAdapter.cleanup()
    }

    override fun onAlarmClicked(alarmData: AlarmData, enabled: Boolean) {
        if (enabled) {
            startAddAlarmActivity(alarmId = alarmData.id)
        } else {
            Snackbar.make(
                main_clock_fragment_alarm_recycler_view,
                R.string.cannot_edit_alarm_message,
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    override fun onAlarmStatusChanged(alarmData: AlarmData, enabled: Boolean) {
        viewModel.updateAlarmEnableStatus(alarmData.id, enabled)
        if (enabled) {
            alarmHelper.scheduleAlarmInBackground(context!!.applicationContext, alarmData)
        } else {
            alarmHelper.cancelAlarmInBackground(context!!.applicationContext, alarmData)
        }
    }

    private fun startAddAlarmActivity(alarmId: Int? = null) {
        val intent = Intent(context, AddAlarmActivity::class.java)
        alarmId?.let { intent.putExtra(Constants.EXTRA_ALARM_ID, it) }
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
    }

    inner class ClockFragmentStateAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    ClockDetailFragment.newInstance(timeFormatHelper)
                }
                else -> {
                    ClockTimezoneListFragment.newInstance(timeFormatHelper)
                }
            }
        }
    }

    inner class AlarmSwipeToDeleteCallback(
        private val context: Context
    ) : SwipeToDeleteCallback(context) {

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val deletePosition = viewHolder.adapterPosition
            val deleted = alarmAdapter.removeAlarmAt(deletePosition)
            viewModel.deleteAlarmById(deleted.id)
            if (deleted.enabled) {
                alarmHelper.cancelAlarmInBackground(context.applicationContext, deleted)
            }
            Snackbar.make(
                main_clock_fragment_alarm_recycler_view,
                R.string.alarm_deleted_message,
                Snackbar.LENGTH_LONG
            ).setAction(R.string.undo_deletion_text) {
                alarmAdapter.insertAlarmAt(deletePosition, deleted)
                viewModel.insertAlarm(deleted)
                if (deleted.enabled) {
                    alarmHelper.scheduleAlarmInBackground(context.applicationContext, deleted)
                }
            }.show()
        }
    }
}
