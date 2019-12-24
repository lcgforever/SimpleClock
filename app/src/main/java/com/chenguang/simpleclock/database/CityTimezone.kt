package com.chenguang.simpleclock.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chenguang.simpleclock.model.ClockTimezone
import com.chenguang.simpleclock.model.TimezoneInfo
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import kotlin.math.abs

/**
 * Data model entity class for different city with timezone info
 */
@Entity(tableName = "city_timezone")
data class CityTimezone(
    @PrimaryKey @ColumnInfo(name = "timezone_id") val timezoneId: String,
    @ColumnInfo(name = "timezone_info") val timezoneInfo: String,
    @ColumnInfo(name = "hour_diff") val hourDiff: Int,
    @ColumnInfo(name = "add_timestamp") val addTimestamp: Long,
    @ColumnInfo(name = "city_name") val cityName: String,
    @ColumnInfo(name = "is_primary") val isPrimary: Boolean
)

/**
 * Helper method to convert db model [CityTimezone] to domain model [TimezoneInfo]
 */
fun CityTimezone.toTimezoneInfo(): TimezoneInfo {
    return TimezoneInfo(
        timezoneId = timezoneId,
        timezoneInfo = timezoneInfo,
        isPrimary = isPrimary,
        isSelected = true
    )
}

/**
 * Helper method to convert db model [CityTimezone] to domain model [ClockTimezone]
 */
fun CityTimezone.toClockTimezone(): ClockTimezone {
    return ClockTimezone(
        timezoneId = timezoneId,
        timezoneInfo = timezoneInfo,
        hourDiff = hourDiff,
        addTimestamp = addTimestamp,
        cityName = cityName,
        isPrimary = isPrimary
    )
}

fun timezoneToCityTimezone(timezone: TimeZone): CityTimezone {
    val timezoneId = timezone.id
    val hours = TimeUnit.MILLISECONDS.toHours(timezone.rawOffset.toLong())
    val minutes = TimeUnit.MILLISECONDS.toMinutes(timezone.rawOffset.toLong())
    val adjustedMinutes = abs(minutes - TimeUnit.HOURS.toMinutes(hours))
    val formatMinutes = "%02d".format(adjustedMinutes)
    val timezoneInfo = if (hours >= 0) {
        "GMT +$hours:$formatMinutes"
    } else {
        "GMT $hours:$formatMinutes"
    }
    val slashIndex = timezoneId.indexOf('/')
    val cityName = timezoneId.substring(slashIndex + 1).replace('_', ' ')
    return CityTimezone(
        timezoneId = timezoneId,
        timezoneInfo = timezoneInfo,
        hourDiff = hours.toInt(),
        addTimestamp = System.currentTimeMillis(),
        cityName = cityName,
        isPrimary = false
    )
}
