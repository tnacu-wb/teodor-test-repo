package com.whitbread.premierinn.domain.reservation.usecase

import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.common.Guest
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import com.whitbread.premierinn.domain.reservation.repository.AmendedReservationRepository
import io.reactivex.Observable
import java.util.*
import javax.inject.Inject

class StoreUpdatedAmendedRoom @Inject constructor(
        private val repository: AmendedReservationRepository,
        private val storeUpdatedAmendedReservation: StoreUpdatedAmendedReservation) {

    operator fun invoke(
        reservationId: String,
        updatedCriteria: RoomCriteria,
        updatedLeadGuest: Guest,
        roomIndex: Int,
        deviceLocale: Locale,
        bookingChannel: String,
        hotelBrand: String,
        selectedRatePlan: String,
        listOfBookingRooms: List<Booking.Room>,
        roomId: String,
        tempBasketRef: String,
        token: String,
        isEmployeeBooking: Boolean,
        isBusinessBooking: Boolean
    ): Observable<StoreUpdatedAmendedReservation.UpdateState> {
        return repository.getAmendedReservation(reservationId).take(2)
                .flatMap { reservation ->
                    storeUpdatedRoom(reservation, roomIndex,
                        updatedLeadGuest, updatedCriteria, deviceLocale, bookingChannel,
                        hotelBrand, selectedRatePlan, listOfBookingRooms,
                        roomId, tempBasketRef, token, isEmployeeBooking, isBusinessBooking)
                }
                .startWith(StoreUpdatedAmendedReservation.UpdateState.Loading)
    }

    private fun storeUpdatedRoom(
        originalReservation: Reservation, roomIndex: Int,
        updatedLeadGuest: Guest,
        updatedCriteria: RoomCriteria,
        deviceLocale: Locale,
        bookingChannel: String,
        hotelBrand: String,
        selectedRatePlan: String,
        listOfBookingRooms: List<Booking.Room>,
        roomId: String,
        tempBasketRef: String,
        token: String,
        isEmployeeBooking: Boolean,
        isBusinessBooking: Boolean
    ): Observable<StoreUpdatedAmendedReservation.UpdateState> {
        val updatedCriteriaList = originalReservation.roomsCriteria.mapIndexed { index, originalCriteria -> if (index + 1 == roomIndex) updatedCriteria else originalCriteria }
        val updatedGuestList = originalReservation.roomsLeadGuest.mapIndexed { index, originalLeadGuest -> if (index + 1 == roomIndex) updatedLeadGuest else originalLeadGuest }
        val updatedReservation = originalReservation.copy(roomsCriteria = updatedCriteriaList, roomsLeadGuest = updatedGuestList)
        return storeUpdatedAmendedReservation(originalReservation, updatedReservation, deviceLocale, bookingChannel,
            hotelBrand, selectedRatePlan, listOfBookingRooms, roomId, tempBasketRef, token, updatedLeadGuest, updatedCriteria, isEmployeeBooking, isBusinessBooking)
    }
}