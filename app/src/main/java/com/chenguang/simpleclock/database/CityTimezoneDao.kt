package com.chenguang.simpleclock.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

/**
 * Room [Dao] class for [CityTimezone]
 */
@Dao
abstract class CityTimezoneDao {

    @Query("SELECT * FROM city_timezone")
    abstract fun getAllCityTimezoneListLiveData(): LiveData<List<CityTimezone>>

    @Query("SELECT * FROM city_timezone")
    abstract fun getAllCityTimezoneList(): List<CityTimezone>

    @Query("SELECT * FROM city_timezone WHERE is_primary = 1")
    abstract fun getPrimaryCityTimezoneLiveData(): LiveData<CityTimezone>

    @Query("UPDATE city_timezone SET is_primary = :isPrimary WHERE timezone_id = :id")
    abstract fun updateCityTimezonePrimaryStatus(id: String, isPrimary: Boolean)

    @Query("DELETE FROM city_timezone WHERE timezone_id = :id")
    abstract fun deleteByTimezoneId(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertCityTimezones(vararg cityTimezones: CityTimezone)

    @Delete
    abstract fun deleteCityTimezones(vararg cityTimezone: CityTimezone)

    @Transaction
    open fun updatePrimaryCityTimezone(
        prevPrimaryTimezoneId: String,
        newPrimaryTimezoneId: String
    ) {
        updateCityTimezonePrimaryStatus(prevPrimaryTimezoneId, false)
        updateCityTimezonePrimaryStatus(newPrimaryTimezoneId, true)
    }

    @Transaction
    open fun updateCityTimezones(toDelete: List<CityTimezone>, toInsert: List<CityTimezone>) {
        deleteCityTimezones(*toDelete.toTypedArray())
        insertCityTimezones(*toInsert.toTypedArray())
    }
}
