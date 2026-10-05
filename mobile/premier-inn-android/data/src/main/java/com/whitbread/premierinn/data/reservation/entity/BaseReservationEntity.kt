package com.whitbread.premierinn.data.reservation.entity

import androidx.room.ColumnInfo
import org.threeten.bp.LocalDate

data class BaseReservationEntity(
    @ColumnInfo(name = "reference") val bookingReference: String, //TODO: rename to bookingReference in Database db
    @ColumnInfo(name = "arrival_date") val arrivalDate: LocalDate,
    @ColumnInfo(name = "departure_date") val departureDate: LocalDate,
    @ColumnInfo(name = "hotel_code") val hotelCode: String,
    @ColumnInfo(name = "cancelable") val cancelable: Boolean
)