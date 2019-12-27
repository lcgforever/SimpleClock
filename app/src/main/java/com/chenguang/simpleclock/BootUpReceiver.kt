package com.chenguang.simpleclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.chenguang.simpleclock.backgroundwork.ScheduleAlarmWorker

/**
 * Custom [BroadcastReceiver] to listen for device boot up event and reschedule alarms
 */
class BootUpReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null || intent.action != Intent.ACTION_BOOT_COMPLETED) {
            return
        }
        val scheduleAlarmRequest =
            OneTimeWorkRequest.Builder(ScheduleAlarmWorker::class.java).build()
        WorkManager.getInstance(context).enqueue(scheduleAlarmRequest)
    }
}
