package com.whitbread.premierinn.data.roomcriteria

import androidx.room.Dao
import androidx.room.Query
import com.whitbread.premierinn.data.common.persistence.EntityDao

@Dao
abstract class RoomCriteriaDao : EntityDao<RoomCriteriaEntity> {

    @Query("SELECT COUNT(*) FROM room_criteria WHERE fk_amended_reservation_reference=:reservationReference")
    abstract fun countAmended(reservationReference: String): Int

    @Query("DELETE FROM room_criteria WHERE room_id = :roomId AND fk_amended_reservation_reference = :reservationReference")
    abstract fun removeRoomCriteria(reservationReference: String, roomId: String)

    @Query("DELETE FROM room_criteria")
    abstract fun deleteAll()

}