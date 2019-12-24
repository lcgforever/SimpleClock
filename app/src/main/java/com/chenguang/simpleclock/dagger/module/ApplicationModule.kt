package com.chenguang.simpleclock.dagger.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Dagger module for application level dependencies
 */
@Module(
    includes = [
        MainClockActivityModule::class,
        DatabaseModule::class
    ]
)
class ApplicationModule {

    @Suppress("UNCHECKED_CAST")
    @Provides
    @Singleton
    fun provideViewModelFactory(
        viewModelKeyMap: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
    ): ViewModelProvider.Factory {
        return object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                return requireNotNull(
                    viewModelKeyMap[modelClass as Class<out ViewModel>]
                ).get() as T
            }
        }
    }
}
