package com.chenguang.simpleclock.clock.clockdetail

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.chenguang.simpleclock.R
import com.chenguang.simpleclock.clock.BaseClockFragment
import com.chenguang.simpleclock.util.TimeFormatHelper
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_clock_detail.clock_detail_fragment_city_text_view
import kotlinx.android.synthetic.main.layout_time_detail.clock_detail_date_text_view
import kotlinx.android.synthetic.main.layout_time_detail.clock_detail_time_text_view
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * Fragment class to display current selected time zone clock details
 */
class ClockDetailFragment(private val timeFormatHelper: TimeFormatHelper) : BaseClockFragment() {

    companion object {

        fun newInstance(timeFormatHelper: TimeFormatHelper): ClockDetailFragment {
            val fragment = ClockDetailFragment(timeFormatHelper)
            fragment.retainInstance = true
            return fragment
        }
    }

    @Inject
    lateinit var viewModel: ClockDetailFragmentViewModel

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_clock_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            viewModel.loadPrimaryClockTimezone().observe(
                this@ClockDetailFragment,
                Observer {
                    timeFormatHelper.updateTimezone(it.timezoneId)
                    clock_detail_fragment_city_text_view.text = it.cityName
                    onTimeUpdated()
                    startUpdatingTime()
                }
            )
        }
    }

    override fun onStart() {
        super.onStart()
        startUpdatingTime()
    }

    override fun onStop() {
        super.onStop()
        stopUpdatingTime()
    }

    override fun onTimeUpdated() {
        val date = Date()
        clock_detail_date_text_view.text = timeFormatHelper.getDateString(date)
        clock_detail_time_text_view.text = timeFormatHelper.getTimeSpannableString(date)
    }
}
