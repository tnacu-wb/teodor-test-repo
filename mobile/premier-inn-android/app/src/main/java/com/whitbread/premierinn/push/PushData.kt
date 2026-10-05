package com.whitbread.premierinn.push

import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.AnalyticsData
import java.util.*

data class PushData(val pushToken: String) : AnalyticsData {
    override fun contextData(): MutableMap<String, String> {
        val contextData: MutableMap<String, String> = HashMap()
        contextData[AnalyticsConstants.Key.PUSH_ID] = pushToken

        return contextData
    }
}