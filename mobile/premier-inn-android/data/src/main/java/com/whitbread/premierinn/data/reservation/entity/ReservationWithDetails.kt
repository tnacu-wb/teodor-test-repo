package com.whitbread.premierinn.data.reservation.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.whitbread.premierinn.data.roomcriteria.RoomCriteriaEntity
import com.whitbread.premierinn.data.roomguest.RoomGuestEntity

data class ReservationWithDetails(
        @Embedded val reservation: ReservationEntity,
        @Relation(
                parentColumn = "reservation_reference",
                entity = RoomCriteriaEntity::class,
                entityColumn = "fk_reservation_reference"
        ) val roomCriteria: List<RoomCriteriaEntity>,
        @Relation(
                parentColumn = "reservation_reference",
                entity = RoomGuestEntity::class,
                entityColumn = "fk_reservation_reference"
        ) val roomGuests: List<RoomGuestEntity>
)