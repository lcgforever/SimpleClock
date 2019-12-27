package com.chenguang.simpleclock.backgroundwork

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.chenguang.simpleclock.R
import com.chenguang.simpleclock.clock.MainClockActivity
import com.chenguang.simpleclock.util.Constants

/**
 * [Worker] to show alarm notification
 */
class ShowAlarmNotificationWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {
        val alarmTitle = inputData.getString(Constants.EXTRA_ALARM_TITLE)
            ?: appContext.getString(R.string.default_alarm_title_text)
        val alarmSoundUriString = inputData.getString(Constants.EXTRA_ALARM_SOUND_URI)

        val intent = Intent(appContext, MainClockActivity::class.java)
        intent.putExtra(Constants.EXTRA_ALARM_TITLE, alarmTitle)
        val pendingIntent = PendingIntent.getActivity(
            appContext,
            Constants.ALARM_OPEN_MAIN_ACTIVITY_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = Constants.ALARM_NOTIFICATION_CHANNEL_ID
        val builder = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.ic_clock_logo)
            .setContentTitle(alarmTitle)
            .setContentText(appContext.getString(R.string.alarm_notification_text, alarmTitle))
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setLights(Color.BLUE, 500, 500)
            .setContentIntent(pendingIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
        alarmSoundUriString?.let { uriString ->
            val soundUri = try {
                Uri.parse(uriString)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
            soundUri?.let { builder.setSound(it) }
        }

        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.notify(Constants.ALARM_NOTIFICATION_ID, builder.build())

        return Result.success()
    }
}
