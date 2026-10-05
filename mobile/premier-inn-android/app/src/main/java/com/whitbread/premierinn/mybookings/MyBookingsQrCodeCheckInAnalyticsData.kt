package com.whitbread.premierinn.mybookings

import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.AnalyticsData
import java.util.HashMap

data class MyBookingsQrCodeCheckInAnalyticsData(val cid: String): AnalyticsData {
    override fun contextData(): MutableMap<String, String> {
        val contextData: MutableMap<String, String> = HashMap()
        contextData[AnalyticsConstants.Key.QRCODE_CID] = cid

        return contextData
    }
}
