package com.whitbread.premierinn.landing.model

import com.whitbread.premierinn.domain.graphql.common.entity.HomePageAppsContentDomain
import com.whitbread.premierinn.domain.recentsearch.entity.RecentSearch

data class BottomSheetState(
    var contentDomain: HomePageAppsContentDomain? = null,
    var recentSearches: List<RecentSearch> = emptyList()
)
