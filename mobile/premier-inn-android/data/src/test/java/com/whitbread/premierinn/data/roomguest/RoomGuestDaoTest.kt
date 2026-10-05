package com.whitbread.premierinn.data.roomguest

import android.database.sqlite.SQLiteConstraintException
import com.google.common.truth.Truth.*
import com.whitbread.premierinn.data.reservation.dao.AmendedReservationDao
import com.whitbread.premierinn.data.reservation.dao.ReservationDao
import com.whitbread.premierinn.data.reservation.entity.AmendedReservationEntity
import com.whitbread.premierinn.data.reservation.entity.ReservationEntity
import com.whitbread.premierinn.data.utils.BaseDatabaseTest
import org.junit.Test
import org.threeten.bp.LocalDate

class RoomGuestDaoTest : BaseDatabaseTest() {
    private lateinit var reservationDao: ReservationDao
    private lateinit var amendedReservationDao: AmendedReservationDao
    private lateinit var roomGuestDao: RoomGuestDao

    override fun setup() {
        super.setup()
        reservationDao = db.reservationDao()
        amendedReservationDao = db.amendReservationDao()
        roomGuestDao = db.roomGuestDao()
    }

    @Test(expected = SQLiteConstraintException::class)
    fun `should allow only single room guest entity of the same reservation - Reservation`() {
        val reservation = "BKF1234"

        reservationDao.insert(ReservationEntity.create(
                bookingReference = reservation,
                hotelCode = "LONMON",
                arrivalDate = LocalDate.now(),
                departureDate = LocalDate.now(),
                cancelable = false
        ))

        val roomLeadGuest = RoomGuestEntity.forReservation(
                roomId = "DBS,1",
                title = "Mr",
                firstName = "John",
                lastName = "Smith",
                guestHistoryNumber = null,
                reservationReference = reservation
        )

        roomGuestDao.insert(roomLeadGuest.copy(id = 1L))

        assertThat(roomGuestDao.count(reservation)).isEqualTo(1)

        roomGuestDao.insert(roomLeadGuest.copy(id = 2L))
    }


    @Test(expected = SQLiteConstraintException::class)
    fun `should allow only single room guest entity of the same reservation - AmendedReservation`() {
        val bookingReference = "BKF1234"

        amendedReservationDao.insert(AmendedReservationEntity.create(
                basketReference = "123456",
                bookingReference = bookingReference,
                hotelCode = "LONMON",
                arrivalDate = LocalDate.now(),
                departureDate = LocalDate.now(),
                cancelable = false
        ))

        val roomLeadGuest = RoomGuestEntity.forAmendedReservation(
                roomId = "DBS,1",
                title = "Mr",
                firstName = "John",
                lastName = "Smith",
                guestHistoryNumber = null,
                amendedReservationReference = bookingReference
        )

        roomGuestDao.insert(roomLeadGuest.copy(id = 1L))

        assertThat(roomGuestDao.countAmended(bookingReference)).isEqualTo(1)

        roomGuestDao.insert(roomLeadGuest.copy(id = 2L))
    }
}