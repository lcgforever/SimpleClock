package com.chenguang.simpleclock.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CityTimezone::class], version = 1)
abstract class CityTimezoneDatabase : RoomDatabase() {

    abstract fun getCityTimezoneDao(): CityTimezoneDao
}
