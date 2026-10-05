package com.whitbread.premierinn.common.model

import com.whitbread.premierinn.domain.dashboard.entity.Action

const val CHECK_IN_ONLINE_ACTION = "CIOL"
const val DIRECTIONS_ACTION = "DIRECTIONS"
const val BOOKING_DETAILS = "BOOKING_DETAILS"

object ActionsFixture {

    fun aCIOLAction(
            type: String = CHECK_IN_ONLINE_ACTION,
            title: String = "Check-in online"
    ): Action {
        return Action(
                type,
                title
        )
    }

    fun aBookingDetailsAction(
            type: String = DIRECTIONS_ACTION,
            title: String = "Hotel directions"
    ): Action {
        return Action(
                type,
                title
        )
    }

    fun aUpcomingBookingActions(
            type: String = BOOKING_DETAILS,
            title: String = "View booking details"
    ): Action {
        return Action(
                type,
                title
        )
    }
}