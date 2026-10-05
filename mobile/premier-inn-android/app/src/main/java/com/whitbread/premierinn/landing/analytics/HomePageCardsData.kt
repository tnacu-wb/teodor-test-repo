package com.whitbread.premierinn.landing.analytics

import com.whitbread.premierinn.common.analytics.AnalyticsData

data class HomePageCardsData(val trackingId: String) : AnalyticsData {

    override fun contextData(): MutableMap<String, String> {
        return mutableMapOf(
            CARD_TRACKING_ID to trackingId,
            CARD_CLICK to "true"
        )
    }

    companion object {
        const val CARD_CLICK_ACTION = "Android: Homepage Content Cards"
        private const val CARD_TRACKING_ID = "analyticsData.homepage.cardTrackingId"
        private const val CARD_CLICK = "analyticsData.homepage.cardClick"
    }

}
