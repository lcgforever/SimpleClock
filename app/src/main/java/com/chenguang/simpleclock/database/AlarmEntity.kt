package com.chenguang.simpleclock.database

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.chenguang.simpleclock.model.AlarmData
import com.chenguang.simpleclock.model.fromDayName
import com.chenguang.simpleclock.util.Constants

/**
 * Data model entity class for alarms
 */
@Entity(tableName = "alarm")
data class AlarmEntity(
    @PrimaryKey val id: Int,
    val title: String,
    @ColumnInfo(name = "time_millis") val timeMillis: Long,
    @ColumnInfo(name = "sound_uri") val soundUri: Uri?,
    @ColumnInfo(name = "repeat_days") val repeatDays: String,
    val enabled: Boolean
)

/**
 * Helper method to convert db model [AlarmEntity] to domain model [AlarmData]
 */
fun AlarmEntity.toAlarmData(): AlarmData {
    return AlarmData(
        id = id,
        title = title,
        timeMillis = timeMillis,
        soundUri = soundUri,
        repeatDays = repeatDays.split(Constants.COMMA_SEPARATOR).map { fromDayName(it) },
        enabled = enabled
    )
}
