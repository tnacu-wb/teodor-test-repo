package com.whitbread.premierinn.data.booking.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import com.whitbread.premierinn.domain.common.RoomType

@Entity(tableName = "room", primaryKeys = ["booking_reference", "roomId"])
class RoomEntity(
        @ColumnInfo(name = "booking_reference") val bookingReference: String,
        @ColumnInfo(name = "roomId") val roomId: String,
        @ColumnInfo(name = "roomType") val roomType: RoomType,
        @ColumnInfo(name = "lettingType") val lettingType: String,
        @ColumnInfo(name = "numberOfAdults") val numberOfAdults: Int,
        @ColumnInfo(name = "numberOfChildren") val numberOfChildren: Int,
        @Embedded val leadGuest: RoomEntityGuest
)