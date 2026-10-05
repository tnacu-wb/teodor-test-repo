package com.whitbread.premierinn.data.roomguest

import androidx.room.Dao
import androidx.room.Query
import com.whitbread.premierinn.data.common.persistence.EntityDao

@Dao
abstract class RoomGuestDao : EntityDao<RoomGuestEntity> {

    @Query("SELECT COUNT(*) FROM room_guest WHERE fk_amended_reservation_reference=:reservationReference")
    abstract fun countAmended(reservationReference: String): Int

    @Query("SELECT COUNT(*) FROM room_guest WHERE fk_reservation_reference=:reservationReference")
    abstract fun count(reservationReference: String): Int

    @Query("DELETE FROM room_guest WHERE room_id = :roomId AND fk_amended_reservation_reference = :reservationReference")
    abstract fun removeGuest(reservationReference: String, roomId: String)

    @Query("DELETE FROM room_guest")
    abstract fun deleteAll()
}