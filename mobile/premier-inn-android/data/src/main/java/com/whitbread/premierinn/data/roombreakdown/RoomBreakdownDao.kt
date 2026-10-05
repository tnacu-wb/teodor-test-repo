package com.whitbread.premierinn.data.roombreakdown

import androidx.room.Dao
import androidx.room.Query
import com.whitbread.premierinn.data.common.persistence.EntityDao

@Dao
abstract class RoomBreakdownDao : EntityDao<RoomBreakdownEntity> {

    @Query("DELETE FROM room_breakdown WHERE room_id = :roomId AND fk_amended_reservation_reference = :reservationReference")
    abstract fun removeRoomBreakdown(reservationReference: String, roomId: String)

    @Query("DELETE FROM room_breakdown")
    abstract fun deleteAll()

}