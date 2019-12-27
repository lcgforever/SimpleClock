package com.chenguang.simpleclock.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.chenguang.simpleclock.AlarmReceiver
import com.chenguang.simpleclock.model.AlarmData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class to schedule or cancel alarm
 */
@Singleton
class AlarmHelper @Inject constructor() {

    /**
     * Helper method to schedule alarm in background thread
     */
    fun scheduleAlarmInBackground(appContext: Context, alarmData: AlarmData) {
        GlobalScope.launch(Dispatchers.IO) { scheduleAlarm(appContext, alarmData) }
    }

    /**
     * Helper method to schedule alarm with repeat option
     */
    fun scheduleAlarm(appContext: Context, alarmData: AlarmData) {
        val alarmManager =
            appContext.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val repeatDayIdList = alarmData.repeatDayIdList
        val alarmPendingIntent = createAlarmPendingIntent(appContext, alarmData)
        if (repeatDayIdList.isEmpty()) {
            val alarmCalendar = getAlarmCalendar(alarmData.alarmHour, alarmData.alarmMinute)
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                alarmCalendar.timeInMillis,
                alarmPendingIntent
            )
        } else {
            repeatDayIdList.forEach {
                val alarmCalendar = getAlarmCalendar(alarmData.alarmHour, alarmData.alarmMinute, it)
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    alarmCalendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY * 7,
                    alarmPendingIntent
                )
            }
        }
    }

    /**
     * Helper method to cancel an alarm with given id
     */
    fun cancelAlarmInBackground(appContext: Context, alarmData: AlarmData) {
        GlobalScope.launch(Dispatchers.IO) {
            val alarmManager =
                appContext.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return@launch
            alarmManager.let {
                val pendingIntent = createAlarmPendingIntent(appContext, alarmData)
                it.cancel(pendingIntent)
            }
        }
    }

    /**
     * Helper method to get next available alarm time based on given hour and minute
     */
    fun getAvailableAlarmTime(alarmHour: Int, alarmMinute: Int): Long {
        val currentCal = Calendar.getInstance()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, alarmHour)
        calendar.set(Calendar.MINUTE, alarmMinute)
        calendar.set(Calendar.SECOND, 0)
        // If given hour and minute is in the past for today, set time for tomorrow
        if (calendar.before(currentCal)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return calendar.timeInMillis
    }

    /**
     * Helper method to create a [PendingIntent] for alarm use
     */
    private fun createAlarmPendingIntent(appContext: Context, alarmData: AlarmData): PendingIntent {
        val intent = Intent(appContext, AlarmReceiver::class.java)
        val alarmId = alarmData.id
        intent.putExtra(Constants.EXTRA_ALARM_ID, alarmId)
        intent.putExtra(Constants.EXTRA_ALARM_TITLE, alarmData.title)
        alarmData.soundUri?.let { intent.putExtra(Constants.EXTRA_ALARM_SOUND_URI, it.toString()) }
        return PendingIntent.getBroadcast(
            appContext,
            alarmId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    /**
     * Helper method to get calendar representing given alarm time and repeat option
     */
    private fun getAlarmCalendar(
        alarmHour: Int,
        alarmMinute: Int,
        repeatDayId: Int? = null
    ): Calendar {
        val currentCal = Calendar.getInstance()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, alarmHour)
        calendar.set(Calendar.MINUTE, alarmMinute)
        calendar.set(Calendar.SECOND, 0)

        if (repeatDayId == null) {
            // If no repeat day available, set to nearest available time (today or tomorrow)
            if (calendar.before(currentCal)) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }
        } else {
            // Set calendar day of week to given day
            while (calendar.get(Calendar.DAY_OF_WEEK) != repeatDayId) {
                calendar.add(Calendar.DAY_OF_WEEK, 1)
            }
            // If current day is the given day and time set in past, set to the next week
            if (calendar.before(currentCal)) {
                calendar.add(Calendar.DAY_OF_YEAR, 7)
            }
        }
        return calendar
    }
}
