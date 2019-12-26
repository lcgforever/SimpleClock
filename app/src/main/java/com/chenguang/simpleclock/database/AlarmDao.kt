package com.chenguang.simpleclock.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

/**
 * Room [Dao] class for [AlarmEntity]
 */
@Dao
interface AlarmDao {

    @Insert
    fun insertAlarms(vararg alarmEntity: AlarmEntity)

    @Query("SELECT * FROM alarm WHERE id = :id")
    fun getAlarmById(id: Int): AlarmEntity?

    @Query("SELECT * FROM alarm")
    fun getAllAlarms(): List<AlarmEntity>

    @Query("DELETE FROM alarm WHERE id = :id")
    fun deleteAlarmById(id: Int)

    @Query("UPDATE alarm SET enabled = :enabled WHERE id = :id")
    fun updateAlarmEnableStatus(id: Int, enabled: Boolean)
}
