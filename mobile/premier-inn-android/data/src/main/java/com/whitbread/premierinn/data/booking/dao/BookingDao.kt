package com.whitbread.premierinn.data.booking.dao


import android.database.sqlite.SQLiteConstraintException
import androidx.room.*
import com.whitbread.premierinn.data.booking.entity.BookingEntity
import com.whitbread.premierinn.data.booking.entity.BookingWithRooms
import com.whitbread.premierinn.data.booking.entity.RoomEntity
import com.whitbread.premierinn.data.common.ErrorLogger
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import org.threeten.bp.LocalDate

@Dao
abstract class BookingDao {

    @Query("SELECT * FROM booking ORDER BY canceled, arrival_date DESC ")
    abstract fun getSortedBookings(): Observable<List<BookingEntity>>

    @Query("SELECT COUNT(*) FROM booking WHERE (:epochDay > departure_date) = 0 ORDER BY canceled, arrival_date ")
    abstract fun countActiveBookings(epochDay: Long = LocalDate.now().toEpochDay()): Int

    // Returns a list with single Elements cause bookingReference is PrimaryKey
    // This is a Rooms workaround when we want to identify if an element that should exist doesn't
    @Query("SELECT * FROM booking WHERE booking_reference = :bookingReference ")
    abstract fun getBookingUpdatesById(bookingReference: String): Flowable<List<BookingEntity>>

    @Query("SELECT * FROM booking WHERE booking_reference = :bookingReference ")
    abstract fun getBookingById(bookingReference: String): BookingEntity?

    @Query("SELECT * FROM booking WHERE last_modified < (strftime('%s','now') - :bookingRefreshIntervalSeconds) AND (date('now') > date(datetime(departure_date * 86400, 'unixepoch', 'localtime'))) = 0")
    abstract fun getStaleBookings(bookingRefreshIntervalSeconds: Int): Single<List<BookingEntity>>

    @Query("SELECT COUNT(*) FROM booking")
    abstract fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertOrReplace(entity: BookingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertOrReplace(entity: List<BookingEntity>): List<Long>

    @Transaction
    open fun updateLinkedAccountBookings(bookings: List<BookingEntity>) {
        deleteAllWithLinkedAccountRetainingCancelled()
        insertOrReplace(bookings)
    }

    @Query("UPDATE booking SET canceled = 1 WHERE booking_reference = :bookingReference ")
    abstract fun updateAsCanceled(bookingReference: String)

    @Query("DELETE FROM booking")
    abstract fun deleteAll()

    @Query("DELETE FROM booking WHERE linked_to_account = 1 AND canceled = 0")
    abstract fun deleteAllWithLinkedAccountRetainingCancelled()

    @Query("UPDATE booking SET checked_in = 1 WHERE booking_reference = :bookingReference")
    abstract fun markAsCheckedIn(bookingReference: String)

    @Query("UPDATE booking SET arrival_date = :arrivalDate, departure_date = :departureDate WHERE booking_reference = :bookingReference")
    abstract fun updateBookingDates(bookingReference: String, arrivalDate: LocalDate, departureDate: LocalDate)

    @Query("UPDATE booking SET arrival_date = :arrivalDate, departure_date = :departureDate, lead_guest_surname = :leadGuestSurname WHERE booking_reference = :bookingReference")
    abstract fun updateBooking(bookingReference: String, arrivalDate: LocalDate, departureDate: LocalDate, leadGuestSurname: String)

    @Transaction
    @Query("SELECT * FROM booking WHERE booking_reference = :bookingReference")
    abstract fun getBookingWithRoomsUpdatesById(bookingReference: String): Flowable<List<BookingWithRooms>>

    @Transaction
    open fun insertOrReplaceBookingWithRooms(booking: BookingWithRooms, logger: ErrorLogger) {
        insertOrReplace(booking.booking)
        deleteAllRooms(booking.booking.bookingReference)
        try {
            insertRooms(booking.rooms)
        } catch (exception: SQLiteConstraintException) {
            logger.logException(exception, "Room insertion failed due to $exception")
        }
    }

    @Query("DELETE FROM room WHERE booking_reference = :bookingReference")
    abstract fun deleteAllRooms(bookingReference: String)

    @Query("DELETE FROM room")
    abstract fun deleteAllRooms()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRooms(rooms: List<RoomEntity>)

    @Query("SELECT * FROM booking WHERE (:epochDay >= arrival_date) = 0 AND canceled = :canceled ORDER BY arrival_date LIMIT 1")
    abstract fun getUpcomingBooking(epochDay: Long = LocalDate.now().toEpochDay(), canceled: Boolean = false): Maybe<BookingEntity>

}
