package com.whitbread.premierinn.common.model

import com.whitbread.premierinn.domain.common.Guest
import com.whitbread.premierinn.domain.common.RoomBreakdown
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.Upsell
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import org.threeten.bp.LocalDate

object ReservationFixture {

    fun aReservation(
            referenceNumber: String = BookingFixture.aBooking().bookingReference,
            arrival: LocalDate = LocalDate.now(),
            departure: LocalDate = arrival.plusDays(1),
            hotelCode: String = HotelFixture.aHotel().code,
            roomsCriteria: List<RoomCriteria> = listOf(RoomCriteriaFixture.aRoomCriteria()),
            roomsLeadGuest: List<Guest> = listOf(GuestFixture.aGuest()),
            roomBreakdown: List<RoomBreakdown> = listOf(RoomBreakdownFixture.aRoomBreakdown()),
            cancelable: Boolean = true,
            upsells: List<Upsell> = listOf(UpsellFixture.aUpsell())): Reservation {
        return Reservation(
                referenceNumber,
                arrival,
                departure,
                hotelCode,
                roomsCriteria,
                roomsLeadGuest,
                roomBreakdown,
                cancelable,
                upsells
        )
    }

    fun anAmendedReservation(
            referenceNumber: String = BookingFixture.aBooking().bookingReference,
            arrival: LocalDate = LocalDate.now(),
            departure: LocalDate = arrival.plusDays(5),
            hotelCode: String = HotelFixture.aHotel().code,
            roomsCriteria: List<RoomCriteria> = listOf(RoomCriteriaFixture.aRoomCriteria(), RoomCriteriaFixture.aSecondRoomCriteria()),
            roomsLeadGuest: List<Guest> = listOf(GuestFixture.aGuest(), GuestFixture.aGuest().copy(roomNumber = 2, firstName = "Batman" )),
            roomBreakdown: List<RoomBreakdown> = listOf(RoomBreakdownFixture.aRoomBreakdown(), RoomBreakdownFixture.aRoomBreakdown()),
            cancelable: Boolean = true,
            upsells: List<Upsell> = listOf(UpsellFixture.aUpsell(), UpsellFixture.aUpsell())): Reservation {
        return Reservation(
                referenceNumber,
                arrival,
                departure,
                hotelCode,
                roomsCriteria,
                roomsLeadGuest,
                roomBreakdown,
                cancelable,
                upsells
        )
    }
}