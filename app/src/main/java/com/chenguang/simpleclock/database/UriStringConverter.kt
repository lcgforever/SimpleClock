package com.chenguang.simpleclock.database

import android.net.Uri
import androidx.room.TypeConverter

/**
 * Custom type converter helper between [Uri] and string
 */
class UriStringConverter {

    @TypeConverter
    fun uriToString(uri: Uri?): String? {
        return uri?.toString()
    }

    @TypeConverter
    fun stringToUri(uriString: String?): Uri? {
        return if (uriString == null) {
            null
        } else {
            try {
                Uri.parse(uriString)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
