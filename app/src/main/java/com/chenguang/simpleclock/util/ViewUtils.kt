package com.chenguang.simpleclock.util

import android.content.Context
import android.util.TypedValue

/**
 * Helper method to convert dp to pixel
 */
fun convertDpToPixel(dp: Float, context: Context): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        context.resources.displayMetrics
    )
}
