package com.chenguang.simpleclock.clock.clocktimezone

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chenguang.simpleclock.clock.BaseClockViewModel
import com.chenguang.simpleclock.database.CityTimezoneDao
import com.chenguang.simpleclock.database.toClockTimezone
import com.chenguang.simpleclock.model.ClockTimezone
import com.chenguang.simpleclock.model.toCityTimezone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * [ViewModel] class for [ClockTimezoneListFragment] to provide city and timezone information
 */
class ClockTimezoneListFragmentViewModel @Inject constructor(
    private val cityTimezoneDao: CityTimezoneDao
) : BaseClockViewModel(cityTimezoneDao) {

//    suspend fun loadSelectedTimezoneList(): LiveData<List<CityTimezone>> {
//        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
//            val selectedTimezoneList = cityTimezoneDao.getAllCityTimezoneListLiveData()
//            Transformations.map(selectedTimezoneList) { timezoneList ->
//                timezoneList
//                    .sortedWith(compareByDescending(CityTimezone::addTimestamp))
//            }
//        }
//    }

    suspend fun loadSelectedTimezoneList(): List<ClockTimezone> {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            cityTimezoneDao.getAllCityTimezoneList()
                .map { it.toClockTimezone() }
                .sortedWith(compareByDescending(ClockTimezone::addTimestamp))
        }
    }

    fun updatePrimaryTimezone(prevPrimaryTimezoneId: String, newPrimaryTimezoneId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            cityTimezoneDao.updatePrimaryCityTimezone(prevPrimaryTimezoneId, newPrimaryTimezoneId)
        }
    }

    fun deleteClockTimezone(toDelete: ClockTimezone) {
        viewModelScope.launch(Dispatchers.IO) {
            cityTimezoneDao.deleteByTimezoneId(toDelete.timezoneId)
        }
    }

    fun insertClockTimezone(toInsert: ClockTimezone) {
        viewModelScope.launch(Dispatchers.IO) {
            cityTimezoneDao.insertCityTimezones(toInsert.toCityTimezone())
        }
    }
}
