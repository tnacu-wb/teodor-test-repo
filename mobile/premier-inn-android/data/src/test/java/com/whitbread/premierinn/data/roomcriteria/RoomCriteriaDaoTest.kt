package com.whitbread.premierinn.data.roomcriteria

import android.database.sqlite.SQLiteConstraintException
import com.google.common.truth.Truth.assertThat
import com.whitbread.premierinn.data.reservation.dao.AmendedReservationDao
import com.whitbread.premierinn.data.reservation.dao.ReservationDao
import com.whitbread.premierinn.data.reservation.entity.AmendedReservationEntity
import com.whitbread.premierinn.data.reservation.entity.ReservationEntity
import com.whitbread.premierinn.data.roomguest.RoomGuestDao
import com.whitbread.premierinn.data.utils.BaseDatabaseTest
import com.whitbread.premierinn.domain.common.RoomType
import org.junit.Test
import org.threeten.bp.LocalDate

class RoomCriteriaDaoTest : BaseDatabaseTest() {
    private lateinit var reservationDao: ReservationDao
    private lateinit var amendedReservationDao: AmendedReservationDao
    private lateinit var roomCriteriaDao: RoomCriteriaDao
    private lateinit var roomGuestDao: RoomGuestDao

    override fun setup() {
        super.setup()
        reservationDao = db.reservationDao()
        amendedReservationDao = db.amendReservationDao()
        roomCriteriaDao = db.roomCriteriaDao()
        roomGuestDao = db.roomGuestDao()
    }

    @Test
    fun `should allow multiple room criteria entities of the same reservation - Reservation`() {
        val reservation = "BKF1234"

        reservationDao.insert(ReservationEntity.create(
                bookingReference = reservation,
                hotelCode = "LONMON",
                arrivalDate = LocalDate.now(),
                departureDate = LocalDate.now(),
                cancelable = false
        ))

        val room1Criteria = RoomCriteriaEntity.forReservation(
                roomId = "DBS,1",
                numberOfAdults = 2,
                numberOfChildren = 0,
                cot = false,
                roomType = RoomType.DOUBLE,
                reservationReference = reservation
        )

        assertThat(roomCriteriaDao.insert(room1Criteria.copy(id = 1222L))).isNotEqualTo(-1)
        assertThat(roomCriteriaDao.insert(room1Criteria.copy(id = 2L, roomId = "TB,2", reservationReference = reservation))).isNotEqualTo(-1)
    }

    @Test
    fun `should allow multiple room criteria entities of the same reservation - AmendedReservation`() {
        val bookingReference = "BKF1234"

        amendedReservationDao.insert(AmendedReservationEntity.create(
                bookingReference = bookingReference,
                basketReference = "12345",
                hotelCode = "LONMON",
                arrivalDate = LocalDate.now(),
                departureDate = LocalDate.now(),
                cancelable = false
        ))

        val room1Criteria = RoomCriteriaEntity.forAmendedReservation(
                roomId = "DBS,1",
                numberOfAdults = 2,
                numberOfChildren = 0,
                cot = false,
                roomType = RoomType.DOUBLE,
                amendedReservationReference = bookingReference
        )

        assertThat(roomCriteriaDao.insert(room1Criteria.copy(id = 1L))).isNotEqualTo(-1)
        assertThat(roomCriteriaDao.insert(room1Criteria.copy(id = 2L, roomId = "TB,2", amendedReservationReference = bookingReference))).isNotEqualTo(-1)
        assertThat(roomCriteriaDao.insert(room1Criteria.copy(id = 3L, roomId = "TB,3", amendedReservationReference = bookingReference))).isNotEqualTo(-1)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun `should throw an SQLiteConstraintException on duplicate insertion - Room1 and Reference are unique - AmendedReservation`() {
        val reservation = "BKF1234"

        amendedReservationDao.insert(AmendedReservationEntity.create(
                bookingReference = reservation,
                basketReference = "12345",
                hotelCode = "LONMON",
                arrivalDate = LocalDate.now(),
                departureDate = LocalDate.now(),
                cancelable = false
        ))

        val room1Criteria = RoomCriteriaEntity.forAmendedReservation(
                roomId = "DBS,1",
                numberOfAdults = 2,
                numberOfChildren = 0,
                cot = false,
                roomType = RoomType.DOUBLE,
                amendedReservationReference = reservation
        )

        roomCriteriaDao.insert(room1Criteria)

        val roomCriteriaWithSameRoomId = room1Criteria.copy(roomId = "DBS,1", numberOfAdults = 2)

        assertThat(roomCriteriaDao.insert(roomCriteriaWithSameRoomId)).isNotEqualTo(-1)
    }

    @Test(expected = SQLiteConstraintException::class)
    fun `should throw an SQLiteConstraintException on duplicate insertion - Room1 and Reference are unique - Reservation`() {
        val reservation = "BKF1234"

        reservationDao.insert(ReservationEntity.create(
                bookingReference = reservation,
                hotelCode = "LONMON",
                arrivalDate = LocalDate.now(),
                departureDate = LocalDate.now(),
                cancelable = false
        ))

        val room1Criteria = RoomCriteriaEntity.forReservation(
                roomId = "DBS,1",
                numberOfAdults = 2,
                numberOfChildren = 0,
                cot = false,
                roomType = RoomType.DOUBLE,
                reservationReference = reservation
        )

        roomCriteriaDao.insert(room1Criteria)

        val roomCriteriaWithSameRoom = room1Criteria.copy(roomId = "DBS,1", numberOfAdults = 2)

        assertThat(roomCriteriaDao.insert(roomCriteriaWithSameRoom)).isNotEqualTo(-1)
    }
}