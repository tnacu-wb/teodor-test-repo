package com.whitbread.premierinn.data.roomcriteria

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.whitbread.premierinn.data.reservation.entity.AmendedReservationEntity
import com.whitbread.premierinn.data.reservation.entity.ReservationEntity
import com.whitbread.premierinn.domain.common.RoomType

/**
 * DB Entity to accommodate storing reservation RoomCriteria data, as well as the Amended ones if any
 */
@Entity(tableName = "room_criteria",
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
data class RoomCriteriaEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id") val id: Long = 0,
        @ColumnInfo(name = "room_id") val roomId: String,
        @ColumnInfo(name = "room_type") val roomType: RoomType,
        @ColumnInfo(name = "adults_count") val numberOfAdults: Int,
        @ColumnInfo(name = "children_count") val numberOfChildren: Int,
        @ColumnInfo(name = "cot") val cot: Boolean,
        @ColumnInfo(name = "fk_reservation_reference") val reservationReference: String? = null,
        @ColumnInfo(name = "fk_amended_reservation_reference") val amendedReservationReference: String? = null
) {
    companion object {
        fun forReservation(roomId: String,
                           roomType: RoomType,
                           numberOfAdults: Int,
                           numberOfChildren: Int,
                           cot: Boolean,
                           reservationReference: String): RoomCriteriaEntity {
            return RoomCriteriaEntity(
                    reservationReference = reservationReference,
                    roomId = roomId,
                    roomType = roomType,
                    numberOfAdults = numberOfAdults,
                    numberOfChildren = numberOfChildren,
                    cot = cot
            )
        }

        fun forAmendedReservation(roomId: String,
                                  roomType: RoomType,
                                  numberOfAdults: Int,
                                  numberOfChildren: Int,
                                  cot: Boolean,
                                  amendedReservationReference: String): RoomCriteriaEntity {
            return RoomCriteriaEntity(
                    amendedReservationReference = amendedReservationReference,
                    roomId = roomId,
                    roomType = roomType,
                    numberOfAdults = numberOfAdults,
                    numberOfChildren = numberOfChildren,
                    cot = cot
            )
        }
    }
}