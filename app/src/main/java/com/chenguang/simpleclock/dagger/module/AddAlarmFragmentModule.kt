package com.chenguang.simpleclock.dagger.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.chenguang.simpleclock.addalarm.AddAlarmFragment
import com.chenguang.simpleclock.addalarm.AddAlarmFragmentViewModel
import com.chenguang.simpleclock.dagger.annotation.FragmentScope
import com.chenguang.simpleclock.dagger.annotation.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Dagger modules for [AddAlarmFragment]
 */
@Module(includes = [AddAlarmFragmentViewModelKeyModule::class])
abstract class AddAlarmFragmentModule {

    @ContributesAndroidInjector(modules = [AddAlarmFragmentViewModelProviderModule::class])
    @FragmentScope
    abstract fun inject(): AddAlarmFragment
}

@Module
abstract class AddAlarmFragmentViewModelKeyModule {

    @Binds
    @IntoMap
    @ViewModelKey(AddAlarmFragmentViewModel::class)
    abstract fun provideViewModelKey(viewModel: AddAlarmFragmentViewModel): ViewModel
}

@Module
class AddAlarmFragmentViewModelProviderModule {

    @Provides
    @FragmentScope
    fun provideViewModel(
        fragment: AddAlarmFragment,
        factory: ViewModelProvider.Factory
    ): AddAlarmFragmentViewModel {
        return ViewModelProviders.of(
            fragment,
            factory
        )[AddAlarmFragmentViewModel::class.java]
    }
}
