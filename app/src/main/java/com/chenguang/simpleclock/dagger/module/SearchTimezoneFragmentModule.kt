package com.chenguang.simpleclock.dagger.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.chenguang.simpleclock.dagger.annotation.FragmentScope
import com.chenguang.simpleclock.dagger.annotation.ViewModelKey
import com.chenguang.simpleclock.searchtimezone.SearchTimezoneFragment
import com.chenguang.simpleclock.searchtimezone.SearchTimezoneFragmentViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Dagger modules for [SearchTimezoneFragment]
 */
@Module(includes = [SearchTimezoneFragmentViewModelKeyModule::class])
abstract class SearchTimezoneFragmentModule {

    @ContributesAndroidInjector(modules = [SearchTimezoneFragmentViewModelProviderModule::class])
    @FragmentScope
    abstract fun inject(): SearchTimezoneFragment
}

@Module
abstract class SearchTimezoneFragmentViewModelKeyModule {

    @Binds
    @IntoMap
    @ViewModelKey(SearchTimezoneFragmentViewModel::class)
    abstract fun provideViewModelKey(viewModel: SearchTimezoneFragmentViewModel): ViewModel
}

@Module
class SearchTimezoneFragmentViewModelProviderModule {

    @Provides
    @FragmentScope
    fun provideViewModel(
        fragment: SearchTimezoneFragment,
        factory: ViewModelProvider.Factory
    ): SearchTimezoneFragmentViewModel {
        return ViewModelProviders.of(
            fragment,
            factory
        )[SearchTimezoneFragmentViewModel::class.java]
    }
}
