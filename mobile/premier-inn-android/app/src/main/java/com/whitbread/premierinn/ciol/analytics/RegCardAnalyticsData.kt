package com.whitbread.premierinn.ciol.analytics

import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsData.Companion.CIOL_REG_CARD_ACTION
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_ACTION_KEY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_FLOW_KEY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.SCREEN_TYPE
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type.CIOL_FLOW
import com.whitbread.premierinn.common.analytics.AnalyticsData

data class RegCardAnalyticsData(val data: Map<String, String>): AnalyticsData {
    override fun contextData(): MutableMap<String, String> {
        val basicMap = mutableMapOf(
            CIOL_FLOW_KEY to "true",
            CIOL_ACTION_KEY to CIOL_REG_CARD_ACTION,
            SCREEN_TYPE to CIOL_FLOW
        )

        return basicMap.apply { putAll(data) }
    }
}
