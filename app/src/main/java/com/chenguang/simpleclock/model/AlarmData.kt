package com.chenguang.simpleclock.model

import android.net.Uri
import com.chenguang.simpleclock.database.AlarmEntity

/**
 * Domain model for alarm information
 */
data class AlarmData(
    val id: Int,
    val title: String,
    val createTimestamp: Long,
    val timeMillis: Long,
    val alarmHour: Int,
    val alarmMinute: Int,
    val soundUri: Uri?,
    val repeatDayIdList: List<Int>,
    var enabled: Boolean
)

/**
 * Helper method to convert domain model [AlarmData] to db model [AlarmEntity]
 */
fun AlarmData.toAlarmEntity(): AlarmEntity {
    return AlarmEntity(
        id = id,
        title = title,
        createTimestamp = createTimestamp,
        timeMillis = timeMillis,
        alarmHour = alarmHour,
        alarmMinute = alarmMinute,
        soundUri = soundUri,
        repeatDays = repeatDayIdList,
        enabled = enabled
    )
}
