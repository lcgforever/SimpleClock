package com.chenguang.simpleclock.addalarm

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chenguang.simpleclock.R
import com.chenguang.simpleclock.model.AlarmRepeatDay
import com.chenguang.simpleclock.model.fromDayName
import kotlinx.android.synthetic.main.layout_alarm_repeat_item.view.alarm_repeat_item_button

/**
 * RecyclerView adapter for alarm repeat options
 */
class AlarmRepeatItemAdapter(
    private val context: Context
) : RecyclerView.Adapter<AlarmRepeatItemAdapter.AlarmRepeatViewHolder>() {

    private val repeatOptionList = mutableListOf<AlarmRepeatDay>().apply {
        val dayNames = context.resources.getStringArray(R.array.repeat_day_options_array)
        addAll(dayNames.map { fromDayName(it) })
    }
    private val selectedDaySet = mutableSetOf<AlarmRepeatDay>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmRepeatViewHolder {
        val view = LayoutInflater
            .from(context)
            .inflate(R.layout.layout_alarm_repeat_item, parent, false)
        return AlarmRepeatViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmRepeatViewHolder, position: Int) {
        holder.bind(repeatOptionList[position])
    }

    override fun getItemCount(): Int {
        return repeatOptionList.size
    }

    fun getSelectedDays(): List<AlarmRepeatDay> {
        return selectedDaySet.toList()
    }

    inner class AlarmRepeatViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(day: AlarmRepeatDay) {
            itemView.alarm_repeat_item_button.text = day.name
            itemView.alarm_repeat_item_button.setOnClickListener {
                it.isSelected = !it.isSelected
                if (it.isSelected) {
                    selectedDaySet.add(day)
                } else {
                    selectedDaySet.remove(day)
                }
            }
        }
    }
}
