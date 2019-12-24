package com.chenguang.simpleclock.clock

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chenguang.simpleclock.database.CityTimezoneDao
import com.chenguang.simpleclock.database.toClockTimezone
import com.chenguang.simpleclock.model.ClockTimezone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Base [ViewModel] for clock fragment
 */
open class BaseClockViewModel(
    private val cityTimezoneDao: CityTimezoneDao
) : ViewModel() {

    suspend fun loadPrimaryClockTimezone(): LiveData<ClockTimezone> {
        return withContext(viewModelScope.coroutineContext + Dispatchers.IO) {
            Transformations.map(cityTimezoneDao.getPrimaryCityTimezoneLiveData()) {
                it.toClockTimezone()
            }
        }
    }
}
