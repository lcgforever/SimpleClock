package com.chenguang.simpleclock.clock.mainclock

import android.content.Context
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chenguang.simpleclock.R
import com.chenguang.simpleclock.model.AlarmData
import kotlinx.android.synthetic.main.layout_alarm_item.view.alarm_item_repeat_day_text_view
import kotlinx.android.synthetic.main.layout_alarm_item.view.alarm_item_switch
import kotlinx.android.synthetic.main.layout_alarm_item.view.alarm_item_time_text_view
import kotlinx.android.synthetic.main.layout_alarm_item.view.alarm_item_title_text_view
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
    private val simpleTimeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    private val repeatDayText by lazy { context.getString(R.string.alarm_repeat_day_text) }
    private val colorAccent by lazy { context.resources.getColor(R.color.color_accent) }
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
            itemView.alarm_item_title_text_view.text = model.title
            val repeatDayText = getRepeatDayText(model.repeatDayIdList)
            itemView.alarm_item_repeat_day_text_view.text = repeatDayText
            itemView.alarm_item_switch.isChecked = model.enabled
            itemView.alarm_item_switch.setOnCheckedChangeListener { _, isChecked ->
                alarmItemListener?.onAlarmStatusChanged(model, isChecked)
            }
        }

        private fun getRepeatDayText(repeatDayIdList: List<Int>): SpannableString {
            val spannableString = SpannableString(repeatDayText)
            repeatDayIdList.forEach { id ->
                val index = 2 * (id - 1)
                if (index >= 0 && index < spannableString.length) {
                    spannableString.setSpan(
                        ForegroundColorSpan(colorAccent),
                        index,
                        index + 1,
                        SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
            return spannableString
        }
    }

    interface AlarmItemListener {

        fun onAlarmStatusChanged(alarmData: AlarmData, enabled: Boolean)
    }
}
