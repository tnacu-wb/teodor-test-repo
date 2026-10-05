package com.whitbread.premierinn.data.reservation.entity

import androidx.room.Embedded
import androidx.room.Entity
import org.threeten.bp.LocalDate

@Entity(tableName = "reservation", primaryKeys = ["reservation_reference"])
data class ReservationEntity(
        @Embedded(prefix = "reservation_") val reservation: BaseReservationEntity) {
    companion object {
        fun create(
            bookingReference: String,
            arrivalDate: LocalDate,
            departureDate: LocalDate,
            hotelCode: String,
            cancelable: Boolean
        ): ReservationEntity {
            return ReservationEntity(
                    reservation = BaseReservationEntity(
                            bookingReference = bookingReference,
                            arrivalDate = arrivalDate,
                            departureDate = departureDate,
                            hotelCode = hotelCode,
                            cancelable = cancelable
                    )
            )
        }
    }
}
