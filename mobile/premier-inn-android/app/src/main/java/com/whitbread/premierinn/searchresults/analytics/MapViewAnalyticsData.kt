package com.whitbread.premierinn.searchresults.analytics

import com.whitbread.premierinn.common.analytics.AnalyticsData

private const val KEY_ANALYTICS_MAP_VIEW = "analyticsData.search.mapView"
class MapViewAnalyticsData(val isMapView: Boolean): AnalyticsData {

    override fun contextData(): Map<String?, String?> {
        return mapOf(KEY_ANALYTICS_MAP_VIEW to isMapView.toString())
    }
}
