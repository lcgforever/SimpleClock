package com.chenguang.simpleclock

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.chenguang.simpleclock.dagger.component.DaggerApplicationComponent
import com.chenguang.simpleclock.util.Constants
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

/**
 * Custom application class to enable dagger injection
 */
class SimpleClockApplication : Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var workerFactory: WorkerFactory

    override fun onCreate() {
        super.onCreate()
        DaggerApplicationComponent
            .factory()
            .create(this)
            .inject(this)

        // Initialize worker manager with custom factory to enable worker dagger injection
        WorkManager.initialize(
            this,
            Configuration.Builder().setWorkerFactory(workerFactory).build()
        )

        // For API 26+, setup alarm notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            val alarmChannel = NotificationChannel(
                Constants.ALARM_NOTIFICATION_CHANNEL_ID,
                Constants.ALARM_NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            alarmChannel.enableLights(true)
            alarmChannel.enableVibration(true)
            alarmChannel.lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE
            alarmChannel.lightColor = Color.BLUE
            alarmChannel.vibrationPattern = longArrayOf(1000L, 1000L, 1000L, 1000L, 1000L)
            notificationManager?.createNotificationChannel(alarmChannel)
        }
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return dispatchingAndroidInjector
    }
}
