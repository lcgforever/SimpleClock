package com.chenguang.simpleclock.dagger.annotation

import javax.inject.Qualifier

/**
 * Qualifier annotation for application level dependencies
 */
@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ForApplication
