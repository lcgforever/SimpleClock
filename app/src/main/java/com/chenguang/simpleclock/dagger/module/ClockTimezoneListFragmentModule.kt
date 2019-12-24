package com.chenguang.simpleclock.dagger.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.chenguang.simpleclock.clock.clocktimezone.ClockTimezoneListFragment
import com.chenguang.simpleclock.clock.clocktimezone.ClockTimezoneListFragmentViewModel
import com.chenguang.simpleclock.dagger.annotation.FragmentScope
import com.chenguang.simpleclock.dagger.annotation.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Dagger modules for [ClockTimezoneListFragment]
 */
@Module(includes = [ClockTimezoneListFragmentViewModelKeyModule::class])
abstract class ClockTimezoneListFragmentModule {

    @ContributesAndroidInjector(
        modules = [
            ClockTimezoneListFragmentViewModelProviderModule::class
        ]
    )
    @FragmentScope
    abstract fun inject(): ClockTimezoneListFragment
}

@Module
abstract class ClockTimezoneListFragmentViewModelKeyModule {

    @Binds
    @IntoMap
    @ViewModelKey(ClockTimezoneListFragmentViewModel::class)
    abstract fun provideViewModelKey(viewModel: ClockTimezoneListFragmentViewModel): ViewModel
}

@Module
class ClockTimezoneListFragmentViewModelProviderModule {

    @Provides
    @FragmentScope
    fun provideViewModel(
        fragment: ClockTimezoneListFragment,
        factory: ViewModelProvider.Factory
    ): ClockTimezoneListFragmentViewModel {
        return ViewModelProviders.of(
            fragment,
            factory
        )[ClockTimezoneListFragmentViewModel::class.java]
    }
}
