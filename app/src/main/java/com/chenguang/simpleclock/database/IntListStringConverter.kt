package com.chenguang.simpleclock.database

import androidx.room.TypeConverter

/**
 * Custom type converter helper between Int List and string
 */
class IntListStringConverter {

    @TypeConverter
    fun listToString(list: List<Int>): String {
        return if (list.isEmpty()) {
            ""
        } else {
            list.joinToString()
        }
    }

    @TypeConverter
    fun stringToList(string: String): List<Int> {
        return if (string.isEmpty()) {
            emptyList()
        } else {
            string.split(",").map { it.trim().toInt() }
        }
    }
}
