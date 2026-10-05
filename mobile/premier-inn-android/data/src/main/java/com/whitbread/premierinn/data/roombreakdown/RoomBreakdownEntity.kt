package com.whitbread.premierinn.data.roombreakdown

import androidx.room.*
import com.whitbread.premierinn.data.booking.entity.PriceEntity
import com.whitbread.premierinn.data.reservation.entity.AmendedReservationEntity
import com.whitbread.premierinn.data.reservation.entity.ReservationEntity

@Entity(tableName = "room_breakdown",
        indices = [
            Index(value = ["fk_reservation_reference"]),
            Index(value = ["fk_amended_reservation_reference"]),
            Index(value = ["fk_reservation_reference", "room_id"], unique = true),
            Index(value = ["fk_amended_reservation_reference", "room_id"], unique = true)
        ],
        foreignKeys = [
            ForeignKey(entity = AmendedReservationEntity::class,
                    parentColumns = arrayOf("amended_reservation_reference"),
                    childColumns = arrayOf("fk_amended_reservation_reference"),
                    onUpdate = ForeignKey.CASCADE,
                    onDelete = ForeignKey.CASCADE),
            ForeignKey(
                    entity = ReservationEntity::class,
                    parentColumns = arrayOf("reservation_reference"),
                    childColumns = arrayOf("fk_reservation_reference"),
                    onUpdate = ForeignKey.CASCADE,
                    onDelete = ForeignKey.CASCADE

            )
        ]
)
data class RoomBreakdownEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id") val id: Long = 0,
        @ColumnInfo(name = "room_id") val roomId: String,
        @Embedded(prefix = "unit_") val unitCost: PriceEntity,
        @ColumnInfo(name = "fk_reservation_reference") val reservationReference: String? = null,
        @ColumnInfo(name = "fk_amended_reservation_reference") val amendedReservationReference: String? = null
) {
    companion object {
        fun forAmendedReservation(roomId: String,
                                  unitCost: PriceEntity,
                                  amendedReservationReference: String): RoomBreakdownEntity {
            return RoomBreakdownEntity(
                    roomId = roomId,
                    unitCost = unitCost,
                    amendedReservationReference = amendedReservationReference
            )
        }
    }
}