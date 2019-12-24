package com.chenguang.simpleclock.dagger.module

import com.chenguang.simpleclock.clock.MainClockFragment
import com.chenguang.simpleclock.dagger.annotation.FragmentScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Dagger modules for [MainClockFragment]
 */
@Module(
    includes = [
        ClockDetailFragmentModule::class,
        ClockTimezoneListFragmentModule::class
    ]
)
abstract class MainClockFragmentModule {

    @ContributesAndroidInjector
    @FragmentScope
    abstract fun inject(): MainClockFragment
}
