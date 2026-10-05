package com.whitbread.premierinn.data.roomupsell

import androidx.room.*
import com.whitbread.premierinn.data.booking.entity.PriceEntity
import com.whitbread.premierinn.data.reservation.entity.AmendedReservationEntity
import com.whitbread.premierinn.data.reservation.entity.ReservationEntity
import org.threeten.bp.LocalDate

@Entity(tableName = "room_upsell",
        indices = [
            Index(value = ["fk_reservation_reference"]),
            Index(value = ["fk_amended_reservation_reference"]),
            Index(value = ["fk_reservation_reference", "room_id", "code", "posting_date"], unique = true),
            Index(value = ["fk_amended_reservation_reference", "room_id", "code", "posting_date"], unique = true)
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
data class RoomUpsellEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id") val id: Long = 0,
        @ColumnInfo(name = "room_id") val roomId: String,
        @ColumnInfo(name = "legend") val legend: String,
        @ColumnInfo(name = "code") val code: String,
        @ColumnInfo(name = "category") val category: String,
        @ColumnInfo(name = "quantity") val quantity: Int,
        @ColumnInfo(name = "posting_date") val postingDate: LocalDate,
        @Embedded(prefix = "unit_") val unitCost: PriceEntity,
        @ColumnInfo(name = "fk_reservation_reference") val reservationReference: String? = null,
        @ColumnInfo(name = "fk_amended_reservation_reference") val amendedReservationReference: String? = null
) {
    companion object {
        fun forReservation(roomId: String,
                           legend: String,
                           category: String,
                           code: String,
                           quantity: Int,
                           postingDate: LocalDate,
                           unitCost: PriceEntity,
                           reservationReference: String): RoomUpsellEntity {
            return RoomUpsellEntity(roomId = roomId,
                    legend = legend,
                    category = category,
                    code = code,
                    quantity = quantity,
                    postingDate = postingDate,
                    unitCost = unitCost,
                    reservationReference = reservationReference)
        }

        fun forAmendedReservation(roomId: String,
                                  legend: String,
                                  category: String,
                                  code: String,
                                  quantity: Int,
                                  postingDate: LocalDate,
                                  unitCost: PriceEntity,
                                  amendedReservationReference: String?): RoomUpsellEntity {
            return RoomUpsellEntity(roomId = roomId,
                    legend = legend,
                    category = category,
                    code = code,
                    quantity = quantity,
                    postingDate = postingDate,
                    unitCost = unitCost,
                    amendedReservationReference = amendedReservationReference)
        }
    }
}