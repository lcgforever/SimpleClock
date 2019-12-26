package com.chenguang.simpleclock.dagger.module

import com.chenguang.simpleclock.addalarm.AlarmReceiver
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Dagger module for [AlarmReceiver]
 */
@Module
abstract class AlarmReceiverModule {

    @ContributesAndroidInjector
    abstract fun inject(): AlarmReceiver
}
