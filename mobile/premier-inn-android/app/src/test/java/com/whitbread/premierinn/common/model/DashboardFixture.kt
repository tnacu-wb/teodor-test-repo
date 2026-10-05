package com.whitbread.premierinn.common.model

import com.whitbread.premierinn.domain.dashboard.entity.Content
import com.whitbread.premierinn.domain.dashboard.entity.DashboardItem

const val UPCOMING_BOOKING_TYPE = "UPCOMING_BOOKING"

object DashboardFixture {

    fun aDashboardItem(
        type : String = UPCOMING_BOOKING_TYPE,
        content: Content = ContentFixture.aUpsellBookingContent()
    ) : DashboardItem {
        return DashboardItem(
                type,
                content
        )
    }
}