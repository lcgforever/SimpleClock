package com.chenguang.simpleclock.clock.clocktimezone

import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chenguang.simpleclock.R
import com.chenguang.simpleclock.model.ClockTimezone
import com.chenguang.simpleclock.util.TimeFormatHelper
import kotlinx.android.synthetic.main.layout_timezone_item.view.timezone_item_city_text_view
import kotlinx.android.synthetic.main.layout_timezone_item.view.timezone_item_detail_text_view
import kotlinx.android.synthetic.main.layout_timezone_item.view.timezone_item_image_view
import kotlinx.android.synthetic.main.layout_timezone_item.view.timezone_item_time_text_view
import java.util.Date
import kotlin.math.abs

/**
 * Recycler view adapter for selected timezone list
 */
class ClockTimezoneItemAdapter(
    private val context: Context,
    private val timeFormatHelper: TimeFormatHelper
) : RecyclerView.Adapter<ClockTimezoneItemAdapter.TimezoneViewHolder>() {

    private val clockTimezoneItemList = mutableListOf<ClockTimezone>()
    private val yourLocationText by lazy { context.getString(R.string.your_location_text) }
    private val handler = Handler()
    private val updateTimeRunnable: Runnable by lazy {
        Runnable {
            updateTimeText()
            handler.postDelayed(updateTimeRunnable, 1000L)
        }
    }
    private var timezoneItemClickListener: TimezoneItemClickListener? = null
    private var primaryTimezoneId: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimezoneViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.layout_timezone_item, parent, false)
        return TimezoneViewHolder(view)
    }

    override fun onBindViewHolder(holder: TimezoneViewHolder, position: Int) {
        holder.bind(clockTimezoneItemList[position])
    }

    override fun onBindViewHolder(
        holder: TimezoneViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            val date = (payloads[0] as? Date) ?: Date()
            holder.updateTimeText(clockTimezoneItemList[position], date)
        } else {
            holder.bind(clockTimezoneItemList[position])
        }
    }

    override fun getItemCount(): Int {
        return clockTimezoneItemList.size
    }

    fun updateClockTimezoneList(newList: List<ClockTimezone>) {
        clockTimezoneItemList.clear()
        clockTimezoneItemList.addAll(newList)
        primaryTimezoneId = clockTimezoneItemList.firstOrNull { it.isPrimary }?.timezoneId ?: ""
        notifyDataSetChanged()
    }

    fun initialize(listener: TimezoneItemClickListener) {
        timezoneItemClickListener = listener
        handler.post(updateTimeRunnable)
    }

    fun cleanup() {
        timezoneItemClickListener = null
        handler.removeCallbacks(updateTimeRunnable)
    }

    fun removeTimezoneAt(position: Int): ClockTimezone {
        val deleted = clockTimezoneItemList.removeAt(position)
        notifyItemRemoved(position)
        return deleted
    }

    fun insertTimezoneAt(position: Int, clockTimezone: ClockTimezone) {
        clockTimezoneItemList.add(position, clockTimezone)
        notifyItemInserted(position)
    }

    fun getTimezoneAt(position: Int): ClockTimezone {
        return clockTimezoneItemList[position]
    }

    private fun updateTimeText() {
        notifyItemRangeChanged(0, itemCount, Date())
    }

    private fun updatePrimaryStatus(newPrimaryTimezoneId: String) {
        val oldPrimaryTimezoneId = primaryTimezoneId
        primaryTimezoneId = newPrimaryTimezoneId
        clockTimezoneItemList.forEachIndexed { index, clockTimezone ->
            if (clockTimezone.timezoneId == newPrimaryTimezoneId) {
                clockTimezone.isPrimary = true
                notifyItemChanged(index)
            } else if (clockTimezone.timezoneId == oldPrimaryTimezoneId) {
                clockTimezone.isPrimary = false
                notifyItemChanged(index)
            }
        }
    }

    inner class TimezoneViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(model: ClockTimezone) {
            itemView.timezone_item_city_text_view.text = model.cityName
            updateTimeText(model)
            if (model.isPrimary) {
                itemView.timezone_item_detail_text_view.text = yourLocationText
                itemView.timezone_item_image_view.visibility = View.VISIBLE
            } else {
                val pluralRes = if (model.hourDiff >= 0) {
                    R.plurals.hour_ahead_text
                } else {
                    R.plurals.hour_behind_text
                }
                val hourDiff = abs(model.hourDiff)
                itemView.timezone_item_detail_text_view.text =
                    context.resources.getQuantityString(
                        pluralRes,
                        hourDiff,
                        hourDiff
                    )
                itemView.timezone_item_image_view.visibility = View.GONE
            }
            itemView.setOnClickListener {
                if (!model.isPrimary) {
                    timezoneItemClickListener?.onTimezoneClicked(model)
                    updatePrimaryStatus(model.timezoneId)
                }
            }
        }

        fun updateTimeText(model: ClockTimezone, date: Date = Date()) {
            timeFormatHelper.updateTimezone(model.timezoneId)
            itemView.timezone_item_time_text_view.text =
                timeFormatHelper.getTimeSpannableString(date)
        }
    }
}

interface TimezoneItemClickListener {

    fun onTimezoneClicked(clockTimezone: ClockTimezone)
}
