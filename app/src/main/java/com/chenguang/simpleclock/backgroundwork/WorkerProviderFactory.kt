package com.chenguang.simpleclock.backgroundwork

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters

/**
 * Factory interface to create worker, used by dagger module
 */
interface WorkerProviderFactory {

    fun create(
        appContext: Context,
        workerParameters: WorkerParameters
    ): ListenableWorker
}
