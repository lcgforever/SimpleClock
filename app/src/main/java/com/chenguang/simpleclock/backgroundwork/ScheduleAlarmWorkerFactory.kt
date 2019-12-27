package com.chenguang.simpleclock.backgroundwork

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.chenguang.simpleclock.database.AlarmDao
import com.chenguang.simpleclock.util.AlarmHelper
import javax.inject.Inject

/**
 * Custom [WorkerProviderFactory] to provide [ScheduleAlarmWorker]
 */
class ScheduleAlarmWorkerFactory @Inject constructor(
    private val alarmDao: AlarmDao,
    private val alarmHelper: AlarmHelper
) : WorkerProviderFactory {

    override fun create(
        appContext: Context,
        workerParameters: WorkerParameters
    ): ListenableWorker {
        return ScheduleAlarmWorker(alarmDao, alarmHelper, appContext, workerParameters)
    }
}
