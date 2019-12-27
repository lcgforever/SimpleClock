package com.chenguang.simpleclock.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        CityTimezoneEntity::class,
        AlarmEntity::class
    ],
    version = 1
)
@TypeConverters(
    value = [
        UriStringConverter::class,
        IntListStringConverter::class
    ]
)
abstract class ClockDatabase : RoomDatabase() {

    abstract fun getCityTimezoneDao(): CityTimezoneDao

    abstract fun getAlarmDao(): AlarmDao
}
