package com.chenguang.simpleclock.clock

import android.os.Handler
import androidx.fragment.app.Fragment

/**
 * Base class for clock fragment to handle update per second
 */
abstract class BaseClockFragment : Fragment() {

    private val handler by lazy { Handler() }
    private val updateTimeRunnable: Runnable by lazy {
        Runnable {
            onTimeUpdated()
            handler.postDelayed(updateTimeRunnable, 1000L)
        }
    }
    private var startedUpdatingTime = false

    fun startUpdatingTime() {
        if (!startedUpdatingTime) {
            startedUpdatingTime = true
            handler.post(updateTimeRunnable)
        }
    }

    fun stopUpdatingTime() {
        handler.removeCallbacks(updateTimeRunnable)
        startedUpdatingTime = false
    }

    /**
     * Children need to implement this to update time text, this will get triggered every second
     */
    abstract fun onTimeUpdated()
}
