package com.whitbread.premierinn.domain.common.model

import com.whitbread.premierinn.domain.common.Guest
import com.whitbread.premierinn.domain.common.RoomBreakdown
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.Upsell
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import org.threeten.bp.LocalDate

object ReservationFixture {

    fun aReservation(
            referenceNumber: String = "BER2334242",
            arrival: LocalDate = LocalDate.of(2020, 8, 9),
            departure: LocalDate = arrival.plusDays(3),
            hotelCode: String = "LONMON",
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
}