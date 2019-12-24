package com.chenguang.simpleclock.searchtimezone

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.chenguang.simpleclock.R
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_search_timezone.search_timezone_activity_done_fab
import kotlinx.android.synthetic.main.activity_search_timezone.search_timezone_activity_recycler_view
import kotlinx.android.synthetic.main.activity_search_timezone.search_timezone_activity_search_view
import kotlinx.android.synthetic.main.layout_progress_bar.progress_bar_dim_view_container
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Activity for search, select/deselect timezone
 */
class SearchTimezoneActivity : AppCompatActivity(), SearchView.OnQueryTextListener {

    @Inject
    lateinit var viewModel: SearchTimezoneActivityViewModel

    private lateinit var adapter: SearchTimezoneItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_timezone)

        search_timezone_activity_search_view.setOnQueryTextListener(this)

        search_timezone_activity_recycler_view.layoutManager = LinearLayoutManager(this)
        val dividerDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        search_timezone_activity_recycler_view.addItemDecoration(dividerDecoration)
        adapter = SearchTimezoneItemAdapter(this)
        search_timezone_activity_recycler_view.adapter = adapter

        search_timezone_activity_done_fab.setOnClickListener {
            val updatedTimezoneList = adapter.getUpdatedTimezoneList()
            if (updatedTimezoneList.isEmpty()) {
                finishAfterTransition()
            } else {
                progress_bar_dim_view_container.visibility = View.VISIBLE
                lifecycleScope.launch(Dispatchers.Main) {
                    viewModel.updateSelectedTimezoneList(updatedTimezoneList)
                    progress_bar_dim_view_container.visibility = View.GONE
                    finishAfterTransition()
                }
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            val allTimezoneList = viewModel.loadAllTimezones()
            adapter.updateSearchCityList(allTimezoneList)
        }
    }

    override fun onBackPressed() {
        val updatedTimezoneList = adapter.getUpdatedTimezoneList()
        if (updatedTimezoneList.isEmpty()) {
            super.onBackPressed()
        } else {
            progress_bar_dim_view_container.visibility = View.VISIBLE
            lifecycleScope.launch(Dispatchers.Main) {
                viewModel.updateSelectedTimezoneList(updatedTimezoneList)
                progress_bar_dim_view_container.visibility = View.GONE
                super.onBackPressed()
            }
        }
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        adapter.filterList(newText)
        return false
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }
}
