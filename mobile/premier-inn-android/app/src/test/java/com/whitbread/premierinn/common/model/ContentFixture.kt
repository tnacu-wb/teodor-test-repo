package com.whitbread.premierinn.common.model

import com.whitbread.premierinn.domain.dashboard.entity.*
import com.whitbread.premierinn.domain.dashboard.entity.Map
import org.threeten.bp.LocalDate

object ContentFixture {

    fun aUpsellBookingContent(
            hotelImage: String = "hotelimage.jpg",
            hotelName: String = "Stark Industries",
            hotelCode: String = "LONBLA",
            map: Map = DashboardMapFixture.aMap(),
            checkedIn: Boolean = false,
            confirmationNumber: String  = "BER2334242",
            arrivalDate: LocalDate = LocalDate.now(),
            departureDate: LocalDate = LocalDate.now().plusDays(5),
            room: List<Room> = listOf(DashboardRoomFixture.aRoom()),
            guests: Int = 1,
            actions : List<Action> = listOf(ActionsFixture.aCIOLAction(), ActionsFixture.aBookingDetailsAction(), ActionsFixture.aUpcomingBookingActions()),
            frequentBookings: List<FrequentBooking> = emptyList()
    ) : Content {
        return Content(
                hotelImage,
                hotelName,
                hotelCode,
                map,
                checkedIn,
                confirmationNumber,
                arrivalDate,
                departureDate,
                room,
                guests,
                actions,
                frequentBookings
        )
    }
}