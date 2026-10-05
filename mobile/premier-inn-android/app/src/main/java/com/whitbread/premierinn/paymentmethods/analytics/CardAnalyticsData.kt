package com.whitbread.premierinn.paymentmethods.analytics

import com.whitbread.premierinn.common.analytics.AnalyticsData

data class CardAnalyticsData(val cardType: String) : AnalyticsData {

    companion object {
        const val KEY_CARD_TYPE = "analyticsData.cardType"
    }

    override fun contextData(): MutableMap<String, String> {
        return mutableMapOf(KEY_CARD_TYPE to cardType)
    }
}