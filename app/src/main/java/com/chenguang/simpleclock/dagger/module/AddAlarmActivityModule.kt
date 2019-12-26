package com.chenguang.simpleclock.dagger.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.chenguang.simpleclock.addalarm.AddAlarmActivity
import com.chenguang.simpleclock.addalarm.AddAlarmActivityViewModel
import com.chenguang.simpleclock.dagger.annotation.ActivityScope
import com.chenguang.simpleclock.dagger.annotation.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Dagger modules for [AddAlarmActivity]
 */
@Module(includes = [AddAlarmActivityViewModelKeyModule::class])
abstract class AddAlarmActivityModule {

    @ContributesAndroidInjector(modules = [AddAlarmActivityViewModelProviderModule::class])
    @ActivityScope
    abstract fun inject(): AddAlarmActivity
}

@Module
abstract class AddAlarmActivityViewModelKeyModule {

    @Binds
    @IntoMap
    @ViewModelKey(AddAlarmActivityViewModel::class)
    abstract fun provideViewModelKey(viewModel: AddAlarmActivityViewModel): ViewModel
}

@Module
class AddAlarmActivityViewModelProviderModule {

    @Provides
    @ActivityScope
    fun provideViewModel(
        activity: AddAlarmActivity,
        factory: ViewModelProvider.Factory
    ): AddAlarmActivityViewModel {
        return ViewModelProviders.of(
            activity,
            factory
        )[AddAlarmActivityViewModel::class.java]
    }
}
