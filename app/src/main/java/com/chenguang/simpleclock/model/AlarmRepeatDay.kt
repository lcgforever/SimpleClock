package com.chenguang.simpleclock.model

import java.util.Calendar

/**
 * Custom data model class for alarm repeat day option
 */
data class AlarmRepeatDay(
    val id: Int,
    val name: String
)

/**
 * Helper method to convert day name to [AlarmRepeatDay]
 */
fun fromDayName(dayName: String): AlarmRepeatDay {
    val id = when (dayName) {
        "Sun" -> Calendar.SUNDAY
        "Mon" -> Calendar.MONDAY
        "Tue" -> Calendar.TUESDAY
        "Wed" -> Calendar.WEDNESDAY
        "Thu" -> Calendar.THURSDAY
        "Fri" -> Calendar.FRIDAY
        "Sat" -> Calendar.SATURDAY
        else -> -1
    }
    return AlarmRepeatDay(
        id = id,
        name = dayName
    )
}
