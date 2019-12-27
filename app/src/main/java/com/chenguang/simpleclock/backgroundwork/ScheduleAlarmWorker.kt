package com.chenguang.simpleclock.backgroundwork

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.chenguang.simpleclock.database.AlarmDao
import com.chenguang.simpleclock.database.toAlarmData
import com.chenguang.simpleclock.util.AlarmHelper

private const val TAG = "ScheduleAlarmWorker"

/**
 * [Worker] to schedule alarms stored in database after device boot up
 */
class ScheduleAlarmWorker(
    private val alarmDao: AlarmDao,
    private val alarmHelper: AlarmHelper,
    private val appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        Log.i(TAG, "Rescheduling valid alarms from database after boot up")
        val allAlarms = alarmDao.getAllAlarms().map { it.toAlarmData() }
        val currentTime = System.currentTimeMillis()
        val enabledValidAlarms = allAlarms.filter {
            it.enabled && (it.repeatDayIdList.isNotEmpty() || it.timeMillis > currentTime)
        }
        enabledValidAlarms.forEach {
            alarmHelper.scheduleAlarm(appContext, it)
        }
        Log.i(TAG, "Rescheduled ${enabledValidAlarms.size} alarms")
        return Result.success()
    }
}
