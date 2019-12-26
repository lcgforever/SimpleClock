package com.chenguang.simpleclock.addalarm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.core.app.NotificationCompat
import com.chenguang.simpleclock.R
import com.chenguang.simpleclock.clock.MainClockActivity
import com.chenguang.simpleclock.database.AlarmDao
import com.chenguang.simpleclock.database.toAlarmData
import com.chenguang.simpleclock.model.AlarmData
import com.chenguang.simpleclock.util.Constants
import dagger.android.AndroidInjection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Custom [BroadcastReceiver] to listen for alarm event
 */
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var alarmDao: AlarmDao

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) {
            return
        }
        AndroidInjection.inject(this, context)
        val alarmId = intent.getIntExtra(Constants.EXTRA_ALARM_ID, 0)
        showAlarmNotification(context, alarmId)
    }

    private fun showAlarmNotification(context: Context, alarmId: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            val alarm = getAlarmById(alarmId)
            if (alarm != null) {
                val intent = Intent(context, MainClockActivity::class.java)
                intent.putExtra(Constants.EXTRA_ALARM_TITLE, alarm.title)
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    Constants.ALARM_OPEN_MAIN_ACTIVITY_REQUEST_CODE,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                val channelId = Constants.ALARM_NOTIFICATION_CHANNEL_ID
                val alarmTitle = alarm.title
                val builder = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_clock_logo)
                    .setContentTitle(alarmTitle)
                    .setContentText(context.getString(R.string.alarm_notification_text, alarmTitle))
                    .setAutoCancel(true)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setLights(Color.BLUE, 500, 500)
                    .setContentIntent(pendingIntent)
                    .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                alarm.soundUri?.let { builder.setSound(it) }

                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                notificationManager?.notify(Constants.ALARM_NOTIFICATION_ID, builder.build())
            }
        }
    }

    private suspend fun getAlarmById(alarmId: Int): AlarmData? {
        return withContext(GlobalScope.coroutineContext + Dispatchers.IO) {
            alarmDao.getAlarmById(alarmId)?.toAlarmData()
        }
    }
}
