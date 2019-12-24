package com.chenguang.simpleclock.dagger.component

import android.content.Context
import com.chenguang.simpleclock.SimpleClockApplication
import com.chenguang.simpleclock.dagger.annotation.ForApplication
import com.chenguang.simpleclock.dagger.module.ApplicationModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Application component wrapping application scope dependencies
 */
@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        AndroidSupportInjectionModule::class,
        ApplicationModule::class
    ]
)
interface ApplicationComponent : AndroidInjector<SimpleClockApplication> {

    @ForApplication
    fun provideApplicationContext(): Context

    @Component.Factory
    interface Factory {

        fun create(@BindsInstance @ForApplication context: Context): ApplicationComponent
    }
}
