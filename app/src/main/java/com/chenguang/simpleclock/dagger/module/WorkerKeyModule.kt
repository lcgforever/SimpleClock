package com.chenguang.simpleclock.dagger.module

import com.chenguang.simpleclock.backgroundwork.ScheduleAlarmWorker
import com.chenguang.simpleclock.backgroundwork.ScheduleAlarmWorkerFactory
import com.chenguang.simpleclock.backgroundwork.WorkerProviderFactory
import com.chenguang.simpleclock.dagger.annotation.WorkerKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

/**
 * Dagger module to provide worker provider factory bindings
 */
@Module
abstract class WorkerKeyModule {

    @Binds
    @IntoMap
    @WorkerKey(ScheduleAlarmWorker::class)
    abstract fun provideScheduleAlarmWorkerFactory(
        factory: ScheduleAlarmWorkerFactory
    ): WorkerProviderFactory
}
