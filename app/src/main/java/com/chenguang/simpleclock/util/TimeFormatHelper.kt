package com.chenguang.simpleclock.util

import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

/**
 * Helper class to format time based on timezone information
 */
class TimeFormatHelper @Inject constructor() {

    private val simpleDateFormat by lazy {
        SimpleDateFormat("E, MMM dd", Locale.getDefault())
    }
    private val simpleTimeFormat by lazy {
        SimpleDateFormat("hh:mm a", Locale.getDefault())
    }

    /**
     * Helper method to get date string
     */
    fun getDateString(date: Date): String {
        return simpleDateFormat.format(date)
    }

    /**
     * Helper method to change text size of am/pm string
     */
    fun getTimeSpannableString(date: Date): SpannableString {
        val spannableString = SpannableString(simpleTimeFormat.format(date))
        val spaceIndex = spannableString.indexOf(' ')
        spannableString.setSpan(
            AbsoluteSizeSpan(20, true),
            spaceIndex,
            spannableString.length,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )
        return spannableString
    }

    /**
     * Update formatter timezone
     */
    fun updateTimezone(newTimezoneId: String) {
        val newTimezone = TimeZone.getTimeZone(newTimezoneId)
        simpleDateFormat.timeZone = newTimezone
        simpleTimeFormat.timeZone = newTimezone
    }
}
