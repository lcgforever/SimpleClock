package com.chenguang.simpleclock.clock.clocktimezone

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chenguang.simpleclock.R
import com.chenguang.simpleclock.clock.BaseClockFragment
import com.chenguang.simpleclock.model.ClockTimezone
import com.chenguang.simpleclock.util.SwipeToDeleteCallback
import com.chenguang.simpleclock.util.TimeFormatHelper
import com.google.android.material.snackbar.Snackbar
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_clock_timezone_list.clock_timezone_list_fragment_add_city_button
import kotlinx.android.synthetic.main.fragment_clock_timezone_list.clock_timezone_list_fragment_recycler_view
import kotlinx.android.synthetic.main.layout_time_detail.clock_detail_date_text_view
import kotlinx.android.synthetic.main.layout_time_detail.clock_detail_time_text_view
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * Fragment class to display city selection fragment
 */
class ClockTimezoneListFragment(
    private val timeFormatHelper: TimeFormatHelper
) : BaseClockFragment(), TimezoneItemClickListener {

    companion object {

        fun newInstance(timeFormatHelper: TimeFormatHelper): ClockTimezoneListFragment {
            val fragment = ClockTimezoneListFragment(timeFormatHelper)
            fragment.retainInstance = true
            return fragment
        }
    }

    @Inject
    lateinit var viewModel: ClockTimezoneListFragmentViewModel

    private lateinit var primaryClockTimezone: ClockTimezone
    private lateinit var adapter: ClockTimezoneItemAdapter

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_clock_timezone_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clock_timezone_list_fragment_recycler_view.layoutManager = LinearLayoutManager(context)
        // Create new time format helper here so it does not confuse timezone with existing one
        adapter = ClockTimezoneItemAdapter(context!!, TimeFormatHelper())
        clock_timezone_list_fragment_recycler_view.adapter = adapter
        val swipeToDeleteCallback = TimezoneSwipeToDeleteCallback(context!!) { viewHolder ->
            viewHolder.adapterPosition >= 0 &&
                    !adapter.getTimezoneAt(viewHolder.adapterPosition).isPrimary
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(clock_timezone_list_fragment_recycler_view)

        clock_timezone_list_fragment_add_city_button.setOnClickListener {
            findNavController().navigate(R.id.action_mainClockFragment_to_searchTimezoneFragment)
        }

        lifecycleScope.launch(Dispatchers.Main) {
            viewModel.loadPrimaryClockTimezone().observe(
                this@ClockTimezoneListFragment,
                Observer {
                    primaryClockTimezone = it
                    timeFormatHelper.updateTimezone(primaryClockTimezone.timezoneId)
                    onTimeUpdated()
                    startUpdatingTime()
                }
            )
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.initialize(this)
        startUpdatingTime()
        lifecycleScope.launch(Dispatchers.Main) {
            val selectedTimezoneList = viewModel.loadSelectedTimezoneList()
            adapter.updateClockTimezoneList(selectedTimezoneList)
        }
    }

    override fun onStop() {
        super.onStop()
        adapter.cleanup()
        stopUpdatingTime()
    }

    override fun onTimeUpdated() {
        val date = Date()
        clock_detail_date_text_view.text = timeFormatHelper.getDateString(date)
        clock_detail_time_text_view.text = timeFormatHelper.getTimeSpannableString(date)
    }

    override fun onTimezoneClicked(clockTimezone: ClockTimezone) {
        timeFormatHelper.updateTimezone(clockTimezone.timezoneId)
        onTimeUpdated()
        // Update primary timezone info
        viewModel.updatePrimaryTimezone(
            primaryClockTimezone.timezoneId,
            clockTimezone.timezoneId
        )
    }

    inner class TimezoneSwipeToDeleteCallback(
        context: Context,
        private val enableSwipePredicate: (RecyclerView.ViewHolder) -> Boolean
    ) : SwipeToDeleteCallback(context, enableSwipePredicate) {

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            if (enableSwipePredicate.invoke(viewHolder)) {
                val deletePosition = viewHolder.adapterPosition
                val deleted = adapter.removeTimezoneAt(deletePosition)
                viewModel.deleteClockTimezone(deleted)
                Snackbar.make(
                    clock_timezone_list_fragment_recycler_view,
                    R.string.timezone_deleted_message,
                    Snackbar.LENGTH_LONG
                ).setAction(R.string.undo_deletion_text) {
                    adapter.insertTimezoneAt(deletePosition, deleted)
                    viewModel.insertClockTimezone(deleted)
                }.show()
            } else {
                adapter.notifyItemChanged(viewHolder.adapterPosition)
                Snackbar.make(
                    clock_timezone_list_fragment_recycler_view,
                    R.string.cannot_delete_primary_message,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }
}
