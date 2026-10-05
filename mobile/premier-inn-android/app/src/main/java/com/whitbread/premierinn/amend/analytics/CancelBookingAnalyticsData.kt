package com.whitbread.premierinn.amend.analytics

import com.whitbread.premierinn.common.analytics.AnalyticsData

data class CancelBookingAnalyticsData(
    val isCancelled: Boolean = false,
    val bookingRef: String,
    val cancelNights: String = "0",
    val cancelRooms: String = "0"
) : AnalyticsData {

    companion object {
        const val KEY_IS_CANCELLED = "analyticsData.cancel"
        const val KEY_CANCEL_BOOKING_REF = "analyticsData.cancel.bookingID"
        const val KEY_CANCEL_NIGHTS = "analyticsData.cancel.nights"
        const val KEY_CANCEL_ROOMS = "analyticsData.cancel.rooms"
    }

    override fun contextData(): MutableMap<String, String> {
        return mutableMapOf(
            KEY_IS_CANCELLED to isCancelled.toString(),
            KEY_CANCEL_BOOKING_REF to bookingRef,
            KEY_CANCEL_NIGHTS to cancelNights,
            KEY_CANCEL_ROOMS to cancelRooms
        )
    }
}