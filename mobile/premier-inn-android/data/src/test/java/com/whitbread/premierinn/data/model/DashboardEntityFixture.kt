package com.whitbread.premierinn.data.model

import com.whitbread.premierinn.data.remote.DashboardApiContract
import com.whitbread.premierinn.data.remote.DashboardApiContract.DashboardResponse

object DashboardEntityFixture {
    private const val UPCOMING_BOOKING_TYPE = "UPCOMING_BOOKING"

    fun aDashboardItem(
            type : String = UPCOMING_BOOKING_TYPE,
            content: DashboardApiContract.Content = ContentEntityFixture.aUpsellBookingContent()
    ) : DashboardResponse {
        return DashboardResponse (
                type,
                content
        )
    }
}