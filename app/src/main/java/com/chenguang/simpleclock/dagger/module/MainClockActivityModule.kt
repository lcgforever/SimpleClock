package com.chenguang.simpleclock.dagger.module

import com.chenguang.simpleclock.clock.MainClockActivity
import com.chenguang.simpleclock.dagger.annotation.ActivityScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Dagger modules for [MainClockActivity]
 */
@Module(
    includes = [
        MainClockFragmentModule::class
    ]
)
abstract class MainClockActivityModule {

    @ContributesAndroidInjector
    @ActivityScope
    abstract fun inject(): MainClockActivity
}
