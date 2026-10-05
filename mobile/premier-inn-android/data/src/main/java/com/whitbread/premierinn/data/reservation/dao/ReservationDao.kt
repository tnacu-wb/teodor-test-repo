package com.whitbread.premierinn.data.reservation.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.whitbread.premierinn.data.common.persistence.EntityDao
import com.whitbread.premierinn.data.reservation.entity.ReservationEntity

@Dao
abstract class ReservationDao : EntityDao<ReservationEntity> {

    @Query("DELETE FROM reservation WHERE reservation_reference = :reservationReference ")
    abstract fun deleteByReference(reservationReference: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract override fun insert(entity: ReservationEntity): Long

    @Query("SELECT COUNT(*) FROM reservation WHERE reservation_reference=:reservationReference")
    abstract fun count(reservationReference: String): Int
}