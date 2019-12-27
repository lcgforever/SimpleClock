package com.chenguang.simpleclock.dagger.module

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.chenguang.simpleclock.backgroundwork.WorkerProviderFactory
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
        DatabaseModule::class,
        WorkerKeyModule::class
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

    @Provides
    @Singleton
    fun provideWorkerFactory(
        factoryMap: Map<Class<out Worker>, @JvmSuppressWildcards WorkerProviderFactory>
    ): WorkerFactory {
        return object : WorkerFactory() {

            override fun createWorker(
                appContext: Context,
                workerClassName: String,
                workerParameters: WorkerParameters
            ): ListenableWorker? {
                val factory = factoryMap
                    .entries
                    .find { Class.forName(workerClassName).isAssignableFrom(it.key) }
                    ?.value
                return factory?.create(appContext, workerParameters)
            }
        }
    }
}
