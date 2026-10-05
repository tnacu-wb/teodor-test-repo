package com.whitbread.premierinn.data.roomupsell

import com.google.common.truth.Truth.assertThat
import com.whitbread.premierinn.data.booking.entity.PriceEntity
import com.whitbread.premierinn.data.reservation.dao.AmendedReservationDao
import com.whitbread.premierinn.data.reservation.dao.ReservationDao
import com.whitbread.premierinn.data.reservation.entity.AmendedReservationEntity
import com.whitbread.premierinn.data.reservation.entity.ReservationEntity
import com.whitbread.premierinn.data.utils.BaseDatabaseTest
import org.junit.Test
import org.threeten.bp.LocalDate

class RoomUpsellDaoTest : BaseDatabaseTest() {

    private lateinit var reservationDao: ReservationDao
    private lateinit var amendedReservationDao: AmendedReservationDao
    private lateinit var roomUpsellDao: RoomUpsellDao

    override fun setup() {
        super.setup()
        reservationDao = db.reservationDao()
        amendedReservationDao = db.amendReservationDao()
        roomUpsellDao = db.roomUpsellDao()
    }

    @Test
    fun `should allow multiple room upsell entities of the same reservation - Reservation`() {
        val reservation = "BKF1234"

        reservationDao.insert(ReservationEntity.create(
                bookingReference = reservation,
                hotelCode = "LONMON",
                arrivalDate = LocalDate.now(),
                departureDate = LocalDate.now(), 
                cancelable = false
        ))

        val room1UpsellToday = RoomUpsellEntity.forReservation(
                roomId = "DBS,1",
                legend = "Meal Deal",
                code = "123",
                category = "F",
                postingDate = LocalDate.now(),
                unitCost = PriceEntity(amount = 10f, currency = "GBP"),
                quantity = 1,
                reservationReference = reservation
        )

        val room2UpsellToday = room1UpsellToday.copy(id = 2, roomId = "DBS,2")

        val room1UpsellTomorrow = RoomUpsellEntity.forReservation(
                roomId = "DBS,1",
                legend = "Meal Deal",
                code = "123",
                category = "F",
                postingDate = LocalDate.now().plusDays(1),
                unitCost = PriceEntity(amount = 10f, currency = "GBP"),
                quantity = 1,
                reservationReference = reservation
        )

        assertThat(roomUpsellDao.insert(room1UpsellToday)).isNotEqualTo(-1)
        assertThat(roomUpsellDao.insert(room2UpsellToday)).isNotEqualTo(-1)
        assertThat(roomUpsellDao.insert(room1UpsellTomorrow.copy(id = 3L))).isNotEqualTo(-1)
    }

    @Test
    fun `should allow multiple room upsell entities of the same reservation - AmendedReservation`() {
        val bookingReference = "BKF1234"

        amendedReservationDao.insert(AmendedReservationEntity.create(
                bookingReference = bookingReference,
                basketReference = "123",
                hotelCode = "LONMON",
                arrivalDate = LocalDate.now(),
                departureDate = LocalDate.now(), 
                cancelable = false
        ))

        val room1UpsellToday = RoomUpsellEntity.forAmendedReservation(
                roomId = "DBS,1",
                legend = "Meal Deal",
                code = "123",
                category = "F",
                postingDate = LocalDate.now(),
                unitCost = PriceEntity(amount = 10f, currency = "GBP"),
                quantity = 1,
                amendedReservationReference = bookingReference
        )

        val room2UpsellToday = room1UpsellToday.copy(id = 2, roomId = "DBS,2")

        val room1UpsellTomorrow = RoomUpsellEntity.forAmendedReservation(
                roomId = "DBS,1",
                legend = "Meal Deal",
                code = "123",
                category = "F",
                postingDate = LocalDate.now().plusDays(1),
                unitCost = PriceEntity(amount = 10f, currency = "GBP"),
                quantity = 1,
                amendedReservationReference = bookingReference
        )

        assertThat(roomUpsellDao.insert(room1UpsellToday)).isNotEqualTo(-1)
        assertThat(roomUpsellDao.insert(room2UpsellToday)).isNotEqualTo(-1)
        assertThat(roomUpsellDao.insert(room1UpsellTomorrow.copy(id = 3L))).isNotEqualTo(-1)
    }

}