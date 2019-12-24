package com.chenguang.simpleclock.searchtimezone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chenguang.simpleclock.database.CityTimezoneDao
import com.chenguang.simpleclock.database.toTimezoneInfo
import com.chenguang.simpleclock.model.TimezoneInfo
import com.chenguang.simpleclock.model.timezoneIdToTimezoneInfo
import com.chenguang.simpleclock.model.toCityTimezone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.TimeZone
import javax.inject.Inject

/**
 * [ViewModel] class for [SearchTimezoneActivity] to provide city and timezone information
 */
class SearchTimezoneActivityViewModel @Inject constructor(
    private val cityTimezoneDao: CityTimezoneDao
) : ViewModel() {

    suspend fun loadAllTimezones(): List<TimezoneInfo> {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            val selectedTimezoneList = cityTimezoneDao.getAllCityTimezoneList()
            val selectedTimezoneMap = selectedTimezoneList.map { it.timezoneId to it }.toMap()
            TimeZone
                .getAvailableIDs()
                .filter { it.contains('/') && !it.contains("GMT", ignoreCase = true) }
                .map {
                    selectedTimezoneMap[it]?.toTimezoneInfo() ?: timezoneIdToTimezoneInfo(it)
                }
                .sortedWith(TimezoneInfo.sortComparator)
        }
    }

    suspend fun updateSelectedTimezoneList(list: List<TimezoneInfo>) {
        withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            if (list.isNotEmpty()) {
                val toDelete = list.filter { !it.isSelected }.map { it.toCityTimezone() }
                val toInsert = list.filter { it.isSelected }.map { it.toCityTimezone() }
                cityTimezoneDao.updateCityTimezones(toDelete, toInsert)
            }
        }
    }
}
