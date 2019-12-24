package com.chenguang.simpleclock.clock.clockdetail

import androidx.lifecycle.ViewModel
import com.chenguang.simpleclock.clock.BaseClockViewModel
import com.chenguang.simpleclock.database.CityTimezoneDao
import javax.inject.Inject

/**
 * [ViewModel] class for [ClockDetailFragment] to provide city and timezone information
 */
class ClockDetailFragmentViewModel @Inject constructor(
    cityTimezoneDao: CityTimezoneDao
) : BaseClockViewModel(cityTimezoneDao)
