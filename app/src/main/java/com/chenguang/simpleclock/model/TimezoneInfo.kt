package com.chenguang.simpleclock.model

import com.chenguang.simpleclock.database.CityTimezone
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/**
 * Domain data model for displaying city and timezone info
 */
data class TimezoneInfo(
    val timezoneId: String,
    val timezoneInfo: String,
    val isPrimary: Boolean,
    var isSelected: Boolean
) {

    companion object {
        val sortComparator by lazy {
            compareByDescending(TimezoneInfo::isPrimary)
                .thenByDescending(TimezoneInfo::isSelected)
                .thenBy(TimezoneInfo::timezoneId)
        }
    }
}

/**
 * Helper method to create [TimezoneInfo] from a timezone id
 */
fun timezoneIdToTimezoneInfo(timezoneId: String): TimezoneInfo {
    val timezone = TimeZone.getTimeZone(timezoneId)
    val hours = TimeUnit.MILLISECONDS.toHours(timezone.rawOffset.toLong())
    val minutes = TimeUnit.MILLISECONDS.toMinutes(timezone.rawOffset.toLong())
    val adjustedMinutes = abs(minutes - TimeUnit.HOURS.toMinutes(hours))
    val formatMinutes = "%02d".format(adjustedMinutes)
    val timezoneInfo = if (hours >= 0) {
        "GMT +$hours:$formatMinutes"
    } else {
        "GMT $hours:$formatMinutes"
    }
    return TimezoneInfo(
        timezoneId = timezoneId,
        timezoneInfo = timezoneInfo,
        isPrimary = false,
        isSelected = false
    )
}

/**
 * Helper method to convert domain model [TimezoneInfo] to db model [CityTimezone]
 */
fun TimezoneInfo.toCityTimezone(): CityTimezone {
    val timezone = TimeZone.getTimeZone(timezoneId)
    val hours = TimeUnit.MILLISECONDS.toHours(timezone.rawOffset.toLong())
    val slashIndex = timezoneId.indexOf('/')
    val cityName = timezoneId.substring(slashIndex + 1).replace('_', ' ')
    return CityTimezone(
        timezoneId = timezoneId,
        timezoneInfo = timezoneInfo,
        hourDiff = hours.toInt(),
        addTimestamp = System.currentTimeMillis(),
        cityName = cityName,
        isPrimary = isPrimary
    )
}
