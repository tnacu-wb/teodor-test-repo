package com.whitbread.premierinn.landing

import com.whitbread.premierinn.api.response.search.SearchItemInput
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.dashboard.entity.DashboardItem
import com.whitbread.premierinn.domain.graphql.common.entity.HomePageAppsContentDomain
import com.whitbread.premierinn.domain.recentsearch.entity.RecentSearch
import org.threeten.bp.LocalDate

data class State(
    val location: SearchItemInput?,
    val arrival: LocalDate,
    val departure: LocalDate,
    val roomCriteria: List<RoomCriteria>,
    val dateExplicitlySet: Boolean,
    val findingLocation: Boolean,
    val requestingPermission: Boolean,
    val findingHotelAvailability: Boolean,
    val covidBanner: String?,
    val leadGuestDetails: LeadGuestDetails,
    val dashboardItem: List<DashboardItem?>,
    val recentSearches: List<RecentSearch>,
    val recentSearchesCleared: Boolean,
    val companyName: String,
    val bottomSheetContent: HomePageAppsContentDomain?
)
