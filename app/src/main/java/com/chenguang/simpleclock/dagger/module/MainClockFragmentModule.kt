package com.chenguang.simpleclock.dagger.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.chenguang.simpleclock.clock.mainclock.MainClockFragment
import com.chenguang.simpleclock.clock.mainclock.MainClockFragmentViewModel
import com.chenguang.simpleclock.dagger.annotation.FragmentScope
import com.chenguang.simpleclock.dagger.annotation.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Dagger modules for [MainClockFragment]
 */
@Module(
    includes = [
        ClockDetailFragmentModule::class,
        ClockTimezoneListFragmentModule::class,
        SearchTimezoneFragmentModule::class,
        AddAlarmFragmentModule::class,
        MainClockFragmentViewModelKeyModule::class
    ]
)
abstract class MainClockFragmentModule {

    @ContributesAndroidInjector(modules = [MainClockFragmentViewModelProviderModule::class])
    @FragmentScope
    abstract fun inject(): MainClockFragment
}

@Module
abstract class MainClockFragmentViewModelKeyModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainClockFragmentViewModel::class)
    abstract fun provideViewModelKey(viewModel: MainClockFragmentViewModel): ViewModel
}

@Module
class MainClockFragmentViewModelProviderModule {

    @Provides
    @FragmentScope
    fun provideViewModel(
        fragment: MainClockFragment,
        factory: ViewModelProvider.Factory
    ): MainClockFragmentViewModel {
        return ViewModelProviders.of(fragment, factory)[MainClockFragmentViewModel::class.java]
    }
}
