package com.chenguang.simpleclock.clock.mainclock

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chenguang.simpleclock.R
import com.chenguang.simpleclock.model.AlarmData
import com.chenguang.simpleclock.model.AlarmRepeatDay
import kotlinx.android.synthetic.main.layout_alarm_item.view.alarm_item_option_text_view
import kotlinx.android.synthetic.main.layout_alarm_item.view.alarm_item_switch
import kotlinx.android.synthetic.main.layout_alarm_item.view.alarm_item_time_text_view
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * RecyclerView adapter to show alarms
 */
class MainClockAlarmItemAdapter(
    private val context: Context
) : RecyclerView.Adapter<MainClockAlarmItemAdapter.AlarmViewHolder>() {

    private val alarmDataList = mutableListOf<AlarmData>()
    private val repeatEverydayText by lazy {
        context.getString(R.string.alarm_repeat_everyday_text)
    }
    private val repeatWorkdayText by lazy {
        context.getString(R.string.alarm_repeat_workday_text)
    }
    private val repeatWeekendText by lazy {
        context.getString(R.string.alarm_repeat_weekend_text)
    }
    private val nonRepeatText by lazy {
        context.getString(R.string.alarm_non_repeat_text)
    }
    private val repeatWorkday by lazy {
        listOf(
            Calendar.MONDAY,
            Calendar.TUESDAY,
            Calendar.WEDNESDAY,
            Calendar.THURSDAY,
            Calendar.FRIDAY
        ).joinToString()
    }
    private val repeatWeekend by lazy {
        listOf(Calendar.SUNDAY, Calendar.SATURDAY).joinToString()
    }
    private val simpleTimeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private var alarmItemListener: AlarmItemListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_alarm_item, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        holder.bind(alarmDataList[position])
    }

    override fun getItemCount(): Int {
        return alarmDataList.size
    }

    fun initialize(alarmItemListener: AlarmItemListener) {
        this.alarmItemListener = alarmItemListener
    }

    fun cleanup() {
        alarmItemListener = null
    }

    fun updateAlarmDataList(newList: List<AlarmData>) {
        alarmDataList.clear()
        alarmDataList.addAll(newList)
        notifyDataSetChanged()
    }

    fun removeAlarmAt(position: Int): AlarmData {
        val deleted = alarmDataList.removeAt(position)
        notifyItemRemoved(position)
        return deleted
    }

    fun insertAlarmAt(position: Int, alarmData: AlarmData) {
        alarmDataList.add(position, alarmData)
        notifyItemInserted(position)
    }

    inner class AlarmViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(model: AlarmData) {
            itemView.alarm_item_switch.isEnabled = model.enabled
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = model.timeMillis
            val alarmTimeText = simpleTimeFormatter.format(calendar.time)
            itemView.alarm_item_time_text_view.text = alarmTimeText
            itemView.alarm_item_switch.isChecked = model.enabled
            itemView.alarm_item_switch.setOnCheckedChangeListener { _, isChecked ->
                alarmItemListener?.onAlarmStatusChanged(model, isChecked)
            }
            val repeatOptionText = getRepeatOptionText(model.repeatDays)
            itemView.alarm_item_option_text_view.text = repeatOptionText
        }

        private fun getRepeatOptionText(repeatDays: List<AlarmRepeatDay>): String {
            val repeatOption = repeatDays.joinToString { it.id.toString() }
            return when {
                repeatDays.size == 7 -> repeatEverydayText
                repeatOption == repeatWorkday -> repeatWorkdayText
                repeatOption == repeatWeekend -> repeatWeekendText
                else -> nonRepeatText
            }
        }
    }

    interface AlarmItemListener {

        fun onAlarmStatusChanged(alarmData: AlarmData, enabled: Boolean)
    }
}
