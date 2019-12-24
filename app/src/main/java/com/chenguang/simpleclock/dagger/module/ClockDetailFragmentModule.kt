package com.chenguang.simpleclock.dagger.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.chenguang.simpleclock.clock.clockdetail.ClockDetailFragment
import com.chenguang.simpleclock.clock.clockdetail.ClockDetailFragmentViewModel
import com.chenguang.simpleclock.dagger.annotation.FragmentScope
import com.chenguang.simpleclock.dagger.annotation.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Dagger modules for [ClockDetailFragment]
 */
@Module(includes = [ClockDetailFragmentViewModelKeyModule::class])
abstract class ClockDetailFragmentModule {

    @ContributesAndroidInjector(modules = [ClockDetailFragmentViewModelProviderModule::class])
    @FragmentScope
    abstract fun inject(): ClockDetailFragment
}

@Module
abstract class ClockDetailFragmentViewModelKeyModule {

    @Binds
    @IntoMap
    @ViewModelKey(ClockDetailFragmentViewModel::class)
    abstract fun provideViewModelKey(viewModel: ClockDetailFragmentViewModel): ViewModel
}

@Module
class ClockDetailFragmentViewModelProviderModule {

    @Provides
    @FragmentScope
    fun provideViewModel(
        fragment: ClockDetailFragment,
        factory: ViewModelProvider.Factory
    ): ClockDetailFragmentViewModel {
        return ViewModelProviders.of(fragment, factory)[ClockDetailFragmentViewModel::class.java]
    }
}
