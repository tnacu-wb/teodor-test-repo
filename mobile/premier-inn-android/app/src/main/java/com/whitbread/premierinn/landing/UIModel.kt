package com.whitbread.premierinn.landing

import com.whitbread.premierinn.domain.dashboard.entity.DashboardItem
import com.whitbread.premierinn.domain.graphql.common.entity.HomePageAppsContentDomain
import com.whitbread.premierinn.domain.recentsearch.entity.RecentSearch

data class UIModel(val location: String,
                   val dates: String,
                   val rooms: String,
                   val showSubmitSpinner: Boolean,
                   val showCovidBanner: Boolean,
                   val covidBannerMessage: String?,
                   val leadGuestDetails : LeadGuestDetails,
                   val dashboardItem: List<DashboardItem?>,
                   val recentSearches: List<RecentSearch>,
                   val recentSearchesCleared: Boolean,
                   val companyName: String,
                   val language: String,
                   val bottomSheetContent: HomePageAppsContentDomain?,
                   val isLoading: Boolean)

data class LeadGuestDetails(val surname: String, val email : String)
