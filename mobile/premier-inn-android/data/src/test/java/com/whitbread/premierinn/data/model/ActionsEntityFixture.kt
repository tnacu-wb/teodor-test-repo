package com.whitbread.premierinn.data.model

import com.whitbread.premierinn.data.remote.DashboardApiContract.Actions

object ActionsEntityFixture {
    private const val CHECK_IN_ONLINE_ACTION = "CIOL"
    private const val DIRECTIONS_ACTION = "DIRECTIONS"
    private const val BOOKING_DETAILS = "BOOKING_DETAILS"

    fun aCIOLAction(
            type: String = CHECK_IN_ONLINE_ACTION,
            title: String = "Check-in online"
    ): Actions {
        return Actions(
                type,
                title
        )
    }

    fun aBookingDetailsAction(
            type: String = DIRECTIONS_ACTION,
            title: String = "Hotel directions"
    ): Actions {
        return Actions (
                type,
                title
        )
    }

    fun aUpcomingBookingActions(
            type: String = BOOKING_DETAILS,
            title: String = "View booking details"
    ): Actions {
        return Actions(
                type,
                title
        )
    }
}