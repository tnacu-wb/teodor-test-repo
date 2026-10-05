package com.whitbread.premierinn.data.roomupsell

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.whitbread.premierinn.data.common.persistence.EntityDao

@Dao
abstract class RoomUpsellDao : EntityDao<RoomUpsellEntity> {
    @Query("DELETE FROM room_upsell WHERE room_id = :roomId AND fk_amended_reservation_reference = :reservationReference")
    abstract fun removeRoomUpsell(reservationReference: String, roomId: String)

    @Query("DELETE FROM room_upsell")
    abstract fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract override fun insert(entity: RoomUpsellEntity) : Long
}