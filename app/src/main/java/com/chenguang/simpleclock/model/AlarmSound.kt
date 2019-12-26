package com.chenguang.simpleclock.model

import android.net.Uri

/**
 * Custom data model for alarm sound
 */
data class AlarmSound(
    val name: String,
    val uri: Uri?
)
