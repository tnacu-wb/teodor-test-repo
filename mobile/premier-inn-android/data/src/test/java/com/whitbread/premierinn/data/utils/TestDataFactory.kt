package com.whitbread.premierinn.data.utils

import com.whitbread.premierinn.data.reservation.entity.BaseReservationEntity
import org.threeten.bp.LocalDate

fun createBaseReservation(
    bookingReference: String = "BFKR96221",
    arrival: LocalDate = LocalDate.now(),
    departure: LocalDate = LocalDate.now(),
    hotelCode: String = "LONMON"
): BaseReservationEntity {
    return BaseReservationEntity(
            bookingReference = bookingReference,
            arrivalDate = arrival,
            departureDate = departure,
            hotelCode = hotelCode,
            cancelable = true)
}