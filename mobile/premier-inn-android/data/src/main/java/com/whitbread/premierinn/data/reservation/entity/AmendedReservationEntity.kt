package com.whitbread.premierinn.data.reservation.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import org.threeten.bp.LocalDate

@Entity(tableName = "amended_reservation", primaryKeys = ["amended_reservation_reference"])
data class AmendedReservationEntity(
        @Embedded(prefix = "amended_reservation_") val reservation: BaseReservationEntity,
        @ColumnInfo(name = "session_id") val basketReference: String //TODO: rename to basketReference on database db
) {
    companion object {
        fun create(
            bookingReference: String,
            basketReference: String,
            arrivalDate: LocalDate,
            departureDate: LocalDate,
            hotelCode: String,
            cancelable: Boolean
        ): AmendedReservationEntity {
            return AmendedReservationEntity(
                    basketReference = basketReference,
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