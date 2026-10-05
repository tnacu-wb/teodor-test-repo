package com.whitbread.premierinn.data.model

import com.whitbread.premierinn.data.remote.DashboardApiContract.*
import com.whitbread.premierinn.data.remote.DashboardApiContract.Map
import org.threeten.bp.LocalDate

object ContentEntityFixture {

    fun aUpsellBookingContent(
            hotelImage: String = "hotelimage.jpg",
            hotelName: String = "Stark Industries",
            hotelCode: String = "LONBLA",
            map: Map = DashboardMapEntityFixture.aMap(),
            checkedIn: Boolean = false,
            confirmationNumber: String  = "BER2334242",
            arrivalDate: LocalDate = LocalDate.now(),
            departureDate: LocalDate = LocalDate.now().plusDays(5),
            room: List<Room> = listOf(DashboardRoomFixture.aRoom()),
            guests: Int = 1,
            actions : List<Actions> = listOf(ActionsEntityFixture.aCIOLAction(), ActionsEntityFixture.aBookingDetailsAction(), ActionsEntityFixture.aUpcomingBookingActions()),
            frequentBookings: List<FrequentBooking> = emptyList()
    ) : Content  {
        return Content (
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