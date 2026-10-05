package com.whitbread.premierinn.data.booking.entity

import androidx.room.Embedded
import androidx.room.Relation

class BookingWithRooms {
    @Embedded
    lateinit var booking: BookingEntity
    @Relation(
            parentColumn = "booking_reference",
            entityColumn = "booking_reference",
            entity = RoomEntity::class)
    lateinit var rooms: List<RoomEntity>
}