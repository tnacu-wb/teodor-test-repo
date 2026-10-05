package com.whitbread.premierinn.mybookings

import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.*
import com.whitbread.premierinn.common.analytics.AnalyticsData

data class MyBookingsAnalyticsData(val totalBookings: String,
                                   val cancelledBookings: String,
                                   val futureBookings: String,
                                   val stayedBookings: String,
                                   val checkedInBookings: String
): AnalyticsData {

    override fun contextData(): MutableMap<String, String> {

        return mutableMapOf(
            BUSINESS_TOTAL_BOOKINGS to totalBookings,
            BUSINESS_CANCELLED_BOOKINGS to cancelledBookings,
            BUSINESS_FUTURE_BOOKINGS to futureBookings,
            BUSINESS_STAYED_BOOKINGS to stayedBookings,
            BUSINESS_CHECKED_IN_BOOKINGS to checkedInBookings
        )
    }
}
