package com.whitbread.premierinn.landing.analytics

import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_ADOBE_CAMPAIGN_KEY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.GOOGLE_ID
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.MICROSOFT_ID
import com.whitbread.premierinn.common.analytics.AnalyticsData
import com.whitbread.premierinn.common.analytics.CampaignDataModel

data class LandingAnalyticsData(val content: String, val campaignModel: CampaignDataModel? = null) : AnalyticsData {
    companion object {
        const val DASHBOARD_CONTENT = "analyticsData.home.content"
    }

    override fun contextData(): MutableMap<String, String> {
        return mutableMapOf(DASHBOARD_CONTENT to content).apply {
            campaignModel?.let {
                if (it.campaignId.isNotEmpty()) {
                    put(CIOL_ADOBE_CAMPAIGN_KEY, it.campaignId)
                }

                if (it.googleId.isNotEmpty()) {
                    put(GOOGLE_ID, it.googleId)
                }

                if (it.microsoftId.isNotEmpty()) {
                    put(MICROSOFT_ID, it.microsoftId)
                }
            }
        }
    }
}
