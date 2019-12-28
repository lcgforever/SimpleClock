package com.chenguang.simpleclock.addalarm

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chenguang.simpleclock.R
import kotlinx.android.synthetic.main.layout_alarm_repeat_item.view.alarm_repeat_item_button

/**
 * RecyclerView adapter for alarm repeat options
 */
class AlarmRepeatItemAdapter(
    private val context: Context
) : RecyclerView.Adapter<AlarmRepeatItemAdapter.AlarmRepeatViewHolder>() {

    // We use index + 1 to make it 1-based here to match day id within Calendar
    private val repeatDayNameList: List<Pair<Int, String>> =
        context.resources.getStringArray(R.array.repeat_day_options_array)
            .mapIndexed { index, name -> index + 1 to name }
            .toList()
    private val selectedDayIdSet = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmRepeatViewHolder {
        val view = LayoutInflater
            .from(context)
            .inflate(R.layout.layout_alarm_repeat_item, parent, false)
        return AlarmRepeatViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmRepeatViewHolder, position: Int) {
        holder.bind(repeatDayNameList[position])
    }

    override fun getItemCount(): Int {
        return repeatDayNameList.size
    }

    fun getSortedSelectedDayIdList(): List<Int> {
        return selectedDayIdSet.toList().sorted()
    }

    fun updateSelectedRepeatDayIdList(selectedRepeatDayIdList: List<Int>) {
        selectedDayIdSet.clear()
        selectedDayIdSet.addAll(selectedRepeatDayIdList)
        notifyDataSetChanged()
    }

    inner class AlarmRepeatViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(dayInfo: Pair<Int, String>) {
            val dayId = dayInfo.first
            val dayName = dayInfo.second
            itemView.alarm_repeat_item_button.text = dayName
            itemView.alarm_repeat_item_button.isSelected = selectedDayIdSet.contains(dayId)
            itemView.alarm_repeat_item_button.setOnClickListener {
                it.isSelected = !it.isSelected
                if (it.isSelected) {
                    selectedDayIdSet.add(dayId)
                } else {
                    selectedDayIdSet.remove(dayId)
                }
            }
        }
    }
}
