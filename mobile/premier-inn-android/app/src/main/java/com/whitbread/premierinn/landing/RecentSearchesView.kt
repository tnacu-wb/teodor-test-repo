package com.whitbread.premierinn.landing

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.whitbread.premierinn.common.view.RecentSearchItemView
import com.whitbread.premierinn.databinding.DashboardRecentSearchesBinding
import com.whitbread.premierinn.domain.recentsearch.entity.RecentSearch

class RecentSearchesView(val context: Context, searchList: List<RecentSearch>,
                         val onRecentSearchesOnclick: (recentSearchItem: RecentSearch) -> Unit,
                         private val onClearRecentSearchesClick: () -> Unit) {

    private val binding = DashboardRecentSearchesBinding.inflate(LayoutInflater.from(context))
    val view: View = binding.root

    init {
        listRecentSearches(searchList)
        setClearAction()
    }

    private fun listRecentSearches(recentSearches: List<RecentSearch>) {
        recentSearches.forEachIndexed { index, searchPayload ->
            val recentSearchViewItem = RecentSearchItemView(context, searchPayload)
            if (index == 0)
                recentSearchViewItem.setRoundedTopCornersBackGround()
            else
                recentSearchViewItem.setGreyBorderBackGround()

            setOnClickEvent(recentSearchViewItem, searchPayload)
            binding.recentSearchContainer.addView(recentSearchViewItem.view)
        }
    }

    private fun setOnClickEvent(recentSearchView: RecentSearchItemView, recentSearch: RecentSearch) {
        recentSearchView.view.setOnClickListener {
            onRecentSearchesOnclick(recentSearch)
        }
    }

    private fun setClearAction() {
        binding.recentSearchesClear.setOnClickListener { onClearRecentSearchesClick() }
    }
}