package com.whitbread.premierinn.data.roomguest

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.whitbread.premierinn.data.reservation.entity.AmendedReservationEntity
import com.whitbread.premierinn.data.reservation.entity.ReservationEntity

@Entity(tableName = "room_guest",
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
data class RoomGuestEntity(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id") val id: Long = 0,
        @ColumnInfo(name = "room_id") val roomId: String,
        @ColumnInfo(name = "title") val title: String,
        @ColumnInfo(name = "first_name") val firstName: String,
        @ColumnInfo(name = "last_name") val lastName: String,
        @ColumnInfo(name = "guest_history_number") val guestHistoryNumber: String?,
        @ColumnInfo(name = "email_address") val emailAddress: String? = null,
        @ColumnInfo(name = "fk_reservation_reference") val reservationReference: String? = null,
        @ColumnInfo(name = "fk_amended_reservation_reference") val amendedReservationReference: String? = null
) {
    companion object {
        fun forReservation(roomId: String,
                           title: String,
                           firstName: String,
                           lastName: String,
                           guestHistoryNumber: String?,
                           reservationReference: String): RoomGuestEntity {
            return RoomGuestEntity(roomId = roomId, title = title, firstName = firstName, lastName = lastName,
                    guestHistoryNumber = guestHistoryNumber,
                    amendedReservationReference = null, reservationReference = reservationReference)

        }

        fun forAmendedReservation(roomId: String,
                                  title: String,
                                  firstName: String,
                                  lastName: String,
                                  guestHistoryNumber: String?,
                                  emailAddress: String? = null,
                                  amendedReservationReference: String): RoomGuestEntity {
            return RoomGuestEntity(roomId = roomId,
                    title = title, firstName = firstName, lastName = lastName,
                    guestHistoryNumber = guestHistoryNumber,
                    emailAddress = emailAddress,
                    amendedReservationReference = amendedReservationReference, reservationReference = null)

        }
    }
}