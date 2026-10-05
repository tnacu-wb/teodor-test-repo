package com.whitbread.premierinn.data.reservation.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.whitbread.premierinn.data.roombreakdown.RoomBreakdownEntity
import com.whitbread.premierinn.data.roomcriteria.RoomCriteriaEntity
import com.whitbread.premierinn.data.roomguest.RoomGuestEntity
import com.whitbread.premierinn.data.roomupsell.RoomUpsellEntity

data class AmendedReservationWithDetails(
        @Embedded val reservationEntity: AmendedReservationEntity,
        @Relation(
                parentColumn = "amended_reservation_reference",
                entity = RoomCriteriaEntity::class,
                entityColumn = "fk_amended_reservation_reference"
        ) val roomCriteria: List<RoomCriteriaEntity>,
        @Relation(
                parentColumn = "amended_reservation_reference",
                entity = RoomGuestEntity::class,
                entityColumn = "fk_amended_reservation_reference"
        ) val roomGuests: List<RoomGuestEntity>,
        @Relation(
                parentColumn = "amended_reservation_reference",
                entity = RoomUpsellEntity::class,
                entityColumn = "fk_amended_reservation_reference"
        ) val roomUpsells: List<RoomUpsellEntity>,
        @Relation(
                parentColumn = "amended_reservation_reference",
                entity = RoomBreakdownEntity::class,
                entityColumn = "fk_amended_reservation_reference"
        ) val roomBreakdown: List<RoomBreakdownEntity>

)