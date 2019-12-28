package com.chenguang.simpleclock.searchtimezone

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.chenguang.simpleclock.R
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_search_timezone.search_timezone_fragment_done_fab
import kotlinx.android.synthetic.main.fragment_search_timezone.search_timezone_fragment_recycler_view
import kotlinx.android.synthetic.main.fragment_search_timezone.search_timezone_fragment_search_view
import kotlinx.android.synthetic.main.layout_progress_bar.progress_bar_dim_view_container
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Fragment for search, select/deselect timezone
 */
class SearchTimezoneFragment : Fragment(), SearchView.OnQueryTextListener {

    @Inject
    lateinit var viewModel: SearchTimezoneFragmentViewModel

    private lateinit var adapter: SearchTimezoneItemAdapter

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_timezone, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        search_timezone_fragment_search_view.setOnQueryTextListener(this)

        search_timezone_fragment_recycler_view.layoutManager = LinearLayoutManager(context)
        val dividerDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        search_timezone_fragment_recycler_view.addItemDecoration(dividerDecoration)
        adapter = SearchTimezoneItemAdapter(context!!)
        search_timezone_fragment_recycler_view.adapter = adapter

        search_timezone_fragment_done_fab.setOnClickListener {
            saveSelectedTimezoneListAndFinish()
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            val allTimezoneList = viewModel.loadAllTimezones()
            adapter.updateSearchCityList(allTimezoneList)
        }

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                saveSelectedTimezoneListAndFinish()
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, onBackPressedCallback)
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        adapter.filterList(newText)
        return false
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    private fun saveSelectedTimezoneListAndFinish() {
        val updatedTimezoneList = adapter.getUpdatedTimezoneList()
        if (updatedTimezoneList.isEmpty()) {
            findNavController().navigateUp()
        } else {
            progress_bar_dim_view_container.visibility = View.VISIBLE
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
                viewModel.updateSelectedTimezoneList(updatedTimezoneList)
                progress_bar_dim_view_container.visibility = View.GONE
                findNavController().navigateUp()
            }
        }
    }
}
