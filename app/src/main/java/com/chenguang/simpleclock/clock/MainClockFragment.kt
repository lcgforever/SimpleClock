package com.chenguang.simpleclock.clock

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.ORIENTATION_HORIZONTAL
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.chenguang.simpleclock.R
import com.chenguang.simpleclock.clock.clocktimezone.ClockTimezoneListFragment
import com.chenguang.simpleclock.clock.clockdetail.ClockDetailFragment
import com.chenguang.simpleclock.util.TimeFormatHelper
import com.chenguang.simpleclock.util.convertDpToPixel
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_main_clock.main_clock_fragment_card_view
import kotlinx.android.synthetic.main.fragment_main_clock.main_clock_fragment_globe_image_view
import kotlinx.android.synthetic.main.fragment_main_clock.main_clock_fragment_indicator_1
import kotlinx.android.synthetic.main.fragment_main_clock.main_clock_fragment_indicator_2
import kotlinx.android.synthetic.main.fragment_main_clock.main_clock_fragment_view_pager
import javax.inject.Inject

/**
 * Fragment class handling main clock page content
 */
class MainClockFragment : Fragment() {

    companion object {
        private const val TRANSLATION_Y = 2000F
    }

    @Inject
    lateinit var timeFormatHelper: TimeFormatHelper

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_clock, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val screenWidth = context!!.resources.displayMetrics.widthPixels
        val distanceX = screenWidth - convertDpToPixel(140F, context!!)
        main_clock_fragment_view_pager.orientation = ORIENTATION_HORIZONTAL
        main_clock_fragment_view_pager.adapter = ClockFragmentStateAdapter(this)
        main_clock_fragment_view_pager.registerOnPageChangeCallback(
            object : OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    if (positionOffset == 0F || positionOffset == 1F) {
                        return
                    }
                    main_clock_fragment_globe_image_view.translationX =
                        -distanceX * positionOffset
                    main_clock_fragment_globe_image_view.scaleX = 1F - 0.4F * positionOffset
                    main_clock_fragment_globe_image_view.scaleY = 1F - 0.4F * positionOffset
                    main_clock_fragment_card_view.translationY = TRANSLATION_Y * positionOffset
                }

                override fun onPageSelected(position: Int) {
                    when (position) {
                        0 -> {
                            main_clock_fragment_indicator_1.isSelected = true
                            main_clock_fragment_indicator_2.isSelected = false
                            main_clock_fragment_globe_image_view.translationX = 0F
                            main_clock_fragment_globe_image_view.scaleX = 1F
                            main_clock_fragment_globe_image_view.scaleY = 1F
                            main_clock_fragment_card_view.translationY = 0F
                        }
                        else -> {
                            main_clock_fragment_indicator_1.isSelected = false
                            main_clock_fragment_indicator_2.isSelected = true
                            main_clock_fragment_globe_image_view.translationX = -distanceX
                            main_clock_fragment_globe_image_view.scaleX = 0.6F
                            main_clock_fragment_globe_image_view.scaleY = 0.6F
                            main_clock_fragment_card_view.translationY = TRANSLATION_Y
                        }
                    }
                }
            }
        )
        main_clock_fragment_view_pager.currentItem = 0
    }

    inner class ClockFragmentStateAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int {
            return 2
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> {
                    ClockDetailFragment.newInstance(timeFormatHelper)
                }
                else -> {
                    ClockTimezoneListFragment.newInstance(timeFormatHelper)
                }
            }
        }
    }
}
