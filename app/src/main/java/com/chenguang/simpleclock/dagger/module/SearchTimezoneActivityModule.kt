package com.chenguang.simpleclock.dagger.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.chenguang.simpleclock.dagger.annotation.ActivityScope
import com.chenguang.simpleclock.dagger.annotation.ViewModelKey
import com.chenguang.simpleclock.searchtimezone.SearchTimezoneActivity
import com.chenguang.simpleclock.searchtimezone.SearchTimezoneActivityViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Dagger modules for [SearchTimezoneActivity]
 */
@Module(includes = [SearchTimezoneActivityViewModelKeyModule::class])
abstract class SearchTimezoneActivityModule {

    @ContributesAndroidInjector(modules = [SearchTimezoneActivityViewModelProviderModule::class])
    @ActivityScope
    abstract fun inject(): SearchTimezoneActivity
}

@Module
abstract class SearchTimezoneActivityViewModelKeyModule {

    @Binds
    @IntoMap
    @ViewModelKey(SearchTimezoneActivityViewModel::class)
    abstract fun provideViewModelKey(viewModel: SearchTimezoneActivityViewModel): ViewModel
}

@Module
class SearchTimezoneActivityViewModelProviderModule {

    @Provides
    @ActivityScope
    fun provideViewModel(
        activity: SearchTimezoneActivity,
        factory: ViewModelProvider.Factory
    ): SearchTimezoneActivityViewModel {
        return ViewModelProviders.of(
            activity,
            factory
        )[SearchTimezoneActivityViewModel::class.java]
    }
}
