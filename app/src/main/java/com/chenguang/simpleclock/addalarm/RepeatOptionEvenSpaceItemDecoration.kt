package com.chenguang.simpleclock.addalarm

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.chenguang.simpleclock.R

private const val REPEAT_OPTION_COUNT = 7

/**
 * Custom item decoration to provide even space between alarm repeat options
 */
class RepeatOptionEvenSpaceItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val screenWidth by lazy {
        context.resources.displayMetrics.widthPixels
    }
    private val itemWidth by lazy {
        context.resources.getDimension(R.dimen.alarm_repeat_option_item_size)
    }
    private val itemMargin by lazy {
        context.resources.getDimension(R.dimen.alarm_repeat_option_item_margin)
    }
    private val parentMargin by lazy {
        context.resources.getDimension(R.dimen.alarm_repeat_option_parent_padding)
    }
    private val space by lazy {
        val availableSpace =
            screenWidth - 2 * parentMargin - REPEAT_OPTION_COUNT * (2 * itemMargin + itemWidth)
        (availableSpace / (2 * REPEAT_OPTION_COUNT)).toInt()
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = space
        outRect.right = space
    }
}
