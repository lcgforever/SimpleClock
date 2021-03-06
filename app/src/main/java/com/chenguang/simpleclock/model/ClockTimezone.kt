package com.chenguang.simpleclock.model

import com.chenguang.simpleclock.database.CityTimezoneEntity

/**
 * Domain data model to provide timezone and clock details
 */
data class ClockTimezone(
    val timezoneId: String,
    val timezoneInfo: String,
    val hourDiff: Int,
    val addTimestamp: Long,
    val cityName: String,
    var isPrimary: Boolean
)

/**
 * Helper method to convert domain model [ClockTimezone] to db model [CityTimezoneEntity]
 */
fun ClockTimezone.toCityTimezone(): CityTimezoneEntity {
    return CityTimezoneEntity(
        timezoneId = timezoneId,
        timezoneInfo = timezoneInfo,
        hourDiff = hourDiff,
        addTimestamp = addTimestamp,
        cityName = cityName,
        isPrimary = isPrimary
    )
}
