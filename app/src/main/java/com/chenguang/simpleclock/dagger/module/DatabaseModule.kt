package com.chenguang.simpleclock.dagger.module

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase.CONFLICT_ABORT
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.chenguang.simpleclock.dagger.annotation.ForApplication
import com.chenguang.simpleclock.database.CityTimezoneDao
import com.chenguang.simpleclock.database.CityTimezoneDatabase
import com.chenguang.simpleclock.database.timezoneToCityTimezone
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.TimeZone
import javax.inject.Singleton

/**
 * Dagger module for local database
 */
@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ForApplication context: Context): CityTimezoneDatabase {
        return Room.databaseBuilder(
            context,
            CityTimezoneDatabase::class.java,
            "city_timezone_db"
        ).addCallback(
            object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    GlobalScope.launch(Dispatchers.IO) {
                        val contentValues = ContentValues()
                        val cityTimeZone = timezoneToCityTimezone(TimeZone.getDefault())
                        contentValues.put("timezone_id", cityTimeZone.timezoneId)
                        contentValues.put("timezone_info", cityTimeZone.timezoneInfo)
                        contentValues.put("hour_diff", cityTimeZone.hourDiff)
                        contentValues.put("add_timestamp", cityTimeZone.addTimestamp)
                        contentValues.put("city_name", cityTimeZone.cityName)
                        contentValues.put("is_Primary", true)
                        db.insert("city_timezone", CONFLICT_ABORT, contentValues)
                    }
                }
            }
        ).build()
    }

    @Provides
    @Singleton
    fun provideDatabaseDao(database: CityTimezoneDatabase): CityTimezoneDao {
        return database.getCityTimezoneDao()
    }
}
