package com.chenguang.simpleclock.model

import android.net.Uri
import com.chenguang.simpleclock.database.AlarmEntity
import com.chenguang.simpleclock.util.Constants

/**
 * Domain model for alarm information
 */
data class AlarmData(
    val id: Int,
    val title: String,
    val timeMillis: Long,
    val soundUri: Uri?,
    val repeatDays: List<AlarmRepeatDay>,
    var enabled: Boolean
)

/**
 * Helper method to convert domain model [AlarmData] to db model [AlarmEntity]
 */
fun AlarmData.toAlarmEntity(): AlarmEntity {
    return AlarmEntity(
        id = id,
        title = title,
        timeMillis = timeMillis,
        soundUri = soundUri,
        repeatDays = repeatDays.joinToString(Constants.COMMA_SEPARATOR) { it.name },
        enabled = enabled
    )
}
