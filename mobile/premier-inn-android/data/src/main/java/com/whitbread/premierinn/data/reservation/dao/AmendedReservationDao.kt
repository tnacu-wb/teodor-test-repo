package com.whitbread.premierinn.data.reservation.dao

import androidx.room.*
import com.whitbread.premierinn.data.common.persistence.EntityDao
import com.whitbread.premierinn.data.reservation.entity.AmendedReservationEntity
import com.whitbread.premierinn.data.reservation.entity.AmendedReservationWithDetails
import io.reactivex.Observable
import io.reactivex.Single
import org.threeten.bp.LocalDate

@Dao
abstract class AmendedReservationDao : EntityDao<AmendedReservationEntity> {
    @Query("SELECT * FROM amended_reservation WHERE amended_reservation_reference = :reservationReference ")
    abstract fun getAmendedReservationUpdates(reservationReference: String): Observable<AmendedReservationEntity>

    @Query("SELECT * FROM amended_reservation WHERE amended_reservation_reference = :reservationReference ")
    abstract fun getByReference(reservationReference: String): AmendedReservationEntity?

    @Query(""" UPDATE amended_reservation 
                     SET amended_reservation_arrival_date = :arrivalDate,amended_reservation_departure_date = :departureDate 
                     WHERE amended_reservation_reference = :reservationReference """)
    abstract fun updateDates(reservationReference: String, arrivalDate: LocalDate, departureDate: LocalDate): Int

    @Query("DELETE FROM amended_reservation WHERE amended_reservation_reference = :reservationReference ")
    abstract fun deleteByReference(reservationReference: String)

    @Query("DELETE FROM amended_reservation")
    abstract fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract override fun insert(entity: AmendedReservationEntity): Long

    @Transaction
    @Query("SELECT * FROM amended_reservation WHERE amended_reservation_reference = :reservationReference ")
    abstract fun getAmendedReservationUpdatesWithRelationsUpdates(reservationReference: String): Observable<AmendedReservationWithDetails>

    @Transaction
    @Query("SELECT * FROM amended_reservation WHERE amended_reservation_reference = :reservationReference ")
    abstract fun getAmendedReservationWithRelations(reservationReference: String): Single<AmendedReservationWithDetails>

    @Query("SELECT COUNT(*) FROM amended_reservation WHERE amended_reservation_reference=:reservationReference")
    abstract fun count(reservationReference: String): Int
}