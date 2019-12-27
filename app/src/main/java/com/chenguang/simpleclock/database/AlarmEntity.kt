package com.chenguang.simpleclock.database

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chenguang.simpleclock.model.AlarmData

/**
 * Data model entity class for alarms
 */
@Entity(tableName = "alarm")
data class AlarmEntity(
    @PrimaryKey val id: Int,
    val title: String,
    @ColumnInfo(name = "create_timestamp") val createTimestamp: Long,
    @ColumnInfo(name = "time_millis") val timeMillis: Long,
    @ColumnInfo(name = "alarm_hour") val alarmHour: Int,
    @ColumnInfo(name = "alarm_minute") val alarmMinute: Int,
    @ColumnInfo(name = "sound_uri") val soundUri: Uri?,
    @ColumnInfo(name = "repeat_days") val repeatDays: List<Int>,
    val enabled: Boolean
)

/**
 * Helper method to convert db model [AlarmEntity] to domain model [AlarmData]
 */
fun AlarmEntity.toAlarmData(): AlarmData {
    return AlarmData(
        id = id,
        title = title,
        createTimestamp = createTimestamp,
        timeMillis = timeMillis,
        alarmHour = alarmHour,
        alarmMinute = alarmMinute,
        soundUri = soundUri,
        repeatDayIdList = repeatDays,
        enabled = enabled
    )
}
