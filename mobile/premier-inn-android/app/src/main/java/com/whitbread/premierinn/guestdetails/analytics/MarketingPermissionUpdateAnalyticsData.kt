package com.whitbread.premierinn.guestdetails.analytics

import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.AnalyticsData
import java.util.*

data class MarketingPermissionUpdateAnalyticsData(val marketingOptIn: Boolean) : AnalyticsData {
    override fun contextData(): MutableMap<String, String> {
        val contextData: MutableMap<String, String> = HashMap()
        contextData[AnalyticsConstants.Key.MARKETING_OPTIN] = marketingOptIn.toString()
        return contextData
    }
}