package com.chenguang.simpleclock.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.chenguang.simpleclock.addalarm.AlarmReceiver
import com.chenguang.simpleclock.model.AlarmRepeatDay
import java.util.Calendar

/**
 * Helper method to convert dp to pixel
 */
fun convertDpToPixel(dp: Float, context: Context): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    )
}

/**
 * Helper method to create a [PendingIntent] for alarm use
 */
fun createAlarmPendingIntent(context: Context, alarmId: Int): PendingIntent {
    val intent = Intent(context, AlarmReceiver::class.java)
    intent.putExtra(Constants.EXTRA_ALARM_ID, alarmId)
    return PendingIntent.getBroadcast(
        context,
        alarmId,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
}

/**
 * Helper method to force close keyboard
 */
fun hideKeyboard(context: Context, view: View) {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * Helper method to schedule alarm with repeat option
 */
fun scheduleAlarm(
    applicationContext: Context,
    alarmId: Int,
    alarmTime: Long,
    repeating: Boolean
) {
    val alarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    if (repeating) {
        alarmManager?.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            alarmTime,
            AlarmManager.INTERVAL_DAY * 7,
            createAlarmPendingIntent(applicationContext, alarmId)
        )
    } else {
        // If not selected any repeat option, set for nearest time (today or tomorrow)
        alarmManager?.set(
            AlarmManager.RTC_WAKEUP,
            alarmTime,
            createAlarmPendingIntent(applicationContext, alarmId)
        )
    }
}

/**
 * Helper method to get calendar representing given alarm time and repeat option
 */
fun getAlarmCalendar(
    alarmHour: Int,
    alarmMinute: Int,
    alarmRepeatDay: AlarmRepeatDay? = null
): Calendar {
    val currentCal = Calendar.getInstance()
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, alarmHour)
    calendar.set(Calendar.MINUTE, alarmMinute)
    calendar.set(Calendar.SECOND, 0)

    if (alarmRepeatDay == null) {
        // If no repeat day available, set to nearest available time
        if (calendar.before(currentCal)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
    } else {
        // Set calendar day of week to given day
        while (calendar.get(Calendar.DAY_OF_WEEK) != alarmRepeatDay.id) {
            calendar.add(Calendar.DAY_OF_WEEK, 1)
        }
        // If current day is the given day and time set in past, set to the next week
        if (calendar.before(currentCal)) {
            calendar.add(Calendar.DAY_OF_YEAR, 7)
        }
    }
    return calendar
}

/**
 * Helper method to cancel an alarm with given id
 */
fun cancelAlarm(context: Context, alarmId: Int) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    alarmManager?.let {
        val pendingIntent = createAlarmPendingIntent(context, alarmId)
        it.cancel(pendingIntent)
    }
}
