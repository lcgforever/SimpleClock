package com.chenguang.simpleclock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.chenguang.simpleclock.backgroundwork.ShowAlarmNotificationWorker
import com.chenguang.simpleclock.util.Constants

private const val TAG = "AlarmReceiver"

/**
 * Custom [BroadcastReceiver] to listen for alarm event
 */
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            return
        }
        Log.i(TAG, "Alarm intent received")
        val inputData = Data.Builder()
            .putString(
                Constants.EXTRA_ALARM_TITLE,
                intent.getStringExtra(Constants.EXTRA_ALARM_TITLE)
            )
            .putString(
                Constants.EXTRA_ALARM_SOUND_URI,
                intent.getStringExtra(Constants.EXTRA_ALARM_SOUND_URI)
            )
            .build()
        val showNotificationRequest = OneTimeWorkRequest
            .Builder(ShowAlarmNotificationWorker::class.java)
            .setInputData(inputData)
            .build()
        WorkManager.getInstance(context).enqueue(showNotificationRequest)
    }
}
