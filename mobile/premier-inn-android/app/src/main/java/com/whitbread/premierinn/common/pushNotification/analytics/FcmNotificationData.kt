package com.whitbread.premierinn.common.pushNotification.analytics

import com.whitbread.premierinn.common.analytics.AnalyticsData

data class FcmNotificationData(private val rawData: Map<String, String>) : AnalyticsData {
    override fun contextData() = rawData
}