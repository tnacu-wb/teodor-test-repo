package com.whitbread.premierinn.bookingdetails.analytics

import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.ANCILLARIES_BOOKED
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_ADOBE_CAMPAIGN_KEY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.THIRD_PARTY_BOOKING
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.THIRD_PARTY_ID
import com.whitbread.premierinn.common.analytics.AnalyticsData

data class BookingDetailsAnalyticsData(
    val isThirdPartyBooking: Boolean,
    val thirdPartyBookingId: String?,
    val upsells: Boolean,
    val trackingCode: String?
) : AnalyticsData {

    override fun contextData(): MutableMap<String, String> {
        return mutableMapOf<String, String>().apply {
            put(THIRD_PARTY_BOOKING, isThirdPartyBooking.toString())
            thirdPartyBookingId?.let { put(THIRD_PARTY_ID, it) }
            put(ANCILLARIES_BOOKED, upsells.toString())

            if (!trackingCode.isNullOrEmpty()) {
                put(CIOL_ADOBE_CAMPAIGN_KEY, trackingCode)
            }
        }
    }
}

