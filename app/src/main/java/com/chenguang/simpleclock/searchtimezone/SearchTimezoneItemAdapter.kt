package com.chenguang.simpleclock.searchtimezone

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chenguang.simpleclock.R
import com.chenguang.simpleclock.model.TimezoneInfo
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.layout_search_timezone_item.view.search_timezone_id_text_view
import kotlinx.android.synthetic.main.layout_search_timezone_item.view.search_timezone_info_text_view
import kotlinx.android.synthetic.main.layout_search_timezone_item.view.search_timezone_select_image_view

/**
 * Recycler view adapter for search and select/deselect timezone info
 */
class SearchTimezoneItemAdapter(
    private val context: Context
) : RecyclerView.Adapter<SearchTimezoneItemAdapter.SearchTimezoneViewHolder>() {

    private val fullTimezoneItemMap = mutableMapOf<String, TimezoneInfo>()
    private val searchTimezoneItemList = mutableListOf<TimezoneInfo>()
    private val originalSelectionMap = mutableMapOf<String, Boolean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchTimezoneViewHolder {
        val view = LayoutInflater
            .from(context)
            .inflate(R.layout.layout_search_timezone_item, parent, false)
        return SearchTimezoneViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchTimezoneViewHolder, position: Int) {
        holder.bind(searchTimezoneItemList[position])
    }

    override fun getItemCount(): Int {
        return searchTimezoneItemList.size
    }

    fun updateSearchCityList(newList: List<TimezoneInfo>) {
        searchTimezoneItemList.clear()
        searchTimezoneItemList.addAll(newList)
        fullTimezoneItemMap.clear()
        fullTimezoneItemMap.putAll(newList.map { it.timezoneId to it })
        originalSelectionMap.clear()
        originalSelectionMap.putAll(newList.map { it.timezoneId to it.isSelected })
        notifyDataSetChanged()
    }

    fun filterList(filterText: String?) {
        searchTimezoneItemList.clear()
        if (filterText.isNullOrEmpty()) {
            val fullList = fullTimezoneItemMap.values.sortedWith(TimezoneInfo.sortComparator)
            searchTimezoneItemList.addAll(fullList)
        } else {
            searchTimezoneItemList.addAll(
                fullTimezoneItemMap.values.filter {
                    it.timezoneId.contains(filterText, ignoreCase = true)
                }
            )
        }
        notifyDataSetChanged()
    }

    fun getUpdatedTimezoneList(): List<TimezoneInfo> {
        return fullTimezoneItemMap.values.filter {
            it.isSelected != originalSelectionMap[it.timezoneId]
        }
    }

    inner class SearchTimezoneViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(model: TimezoneInfo) {
            itemView.search_timezone_id_text_view.text = model.timezoneId
            itemView.search_timezone_info_text_view.text = model.timezoneInfo
            itemView.search_timezone_select_image_view.visibility =
                if (model.isSelected) View.VISIBLE else View.GONE
            itemView.setOnClickListener {
                if (model.isPrimary) {
                    Snackbar.make(
                        itemView,
                        R.string.cannot_deselect_primary_message,
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    model.isSelected = !model.isSelected
                    fullTimezoneItemMap[model.timezoneId]?.isSelected = model.isSelected
                    itemView.search_timezone_select_image_view.visibility =
                        if (model.isSelected) View.VISIBLE else View.GONE
                }
            }
        }
    }
}
