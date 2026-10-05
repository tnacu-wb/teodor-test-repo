package com.whitbread.premierinn.data.reservation.dao

import com.google.common.truth.Truth.assertThat
import com.whitbread.premierinn.data.reservation.entity.AmendedReservationEntity
import com.whitbread.premierinn.data.roomcriteria.RoomCriteriaDao
import com.whitbread.premierinn.data.roomcriteria.RoomCriteriaEntity
import com.whitbread.premierinn.data.roomguest.RoomGuestDao
import com.whitbread.premierinn.data.roomguest.RoomGuestEntity
import com.whitbread.premierinn.data.utils.BaseDatabaseTest
import com.whitbread.premierinn.data.utils.createBaseReservation
import com.whitbread.premierinn.domain.common.RoomType
import org.junit.Test

class AmendedReservationDaoTest : BaseDatabaseTest() {
    private lateinit var amendedReservationDao: AmendedReservationDao
    private lateinit var roomCriteriaDao: RoomCriteriaDao
    private lateinit var roomGuestDao: RoomGuestDao

    override fun setup() {
        super.setup()
        amendedReservationDao = db.amendReservationDao()
        roomCriteriaDao = db.roomCriteriaDao()
        roomGuestDao = db.roomGuestDao()
    }

    @Test
    fun `Insert amended reservation`() {
        amendedReservationDao.insert(AmendedReservationEntity(reservation = createBaseReservation(bookingReference = "BFKR12124"), basketReference = "1234"))
        amendedReservationDao.insert(AmendedReservationEntity(reservation = createBaseReservation(bookingReference = "BFKR12125"), basketReference = "4321"))

        val reservationEntity = amendedReservationDao.getByReference("BFKR12124")!!
        val reservationEntity2 = amendedReservationDao.getByReference("BFKR12125")!!

        assertThat(reservationEntity.reservation.bookingReference).isEqualTo("BFKR12124")
        assertThat(reservationEntity2.reservation.bookingReference).isEqualTo("BFKR12125")
    }

    @Test
    fun `Allow replacing same reservation entity `() {
        amendedReservationDao.insert(AmendedReservationEntity(reservation = createBaseReservation(bookingReference = "BFKR12124"), basketReference = "1234"))
        amendedReservationDao.insert(AmendedReservationEntity(reservation = createBaseReservation(bookingReference = "BFKR12124"), basketReference = "5678"))

        val count = amendedReservationDao.count("BFKR12124")
        assertThat(count).isEqualTo(1)
    }

    @Test
    fun `Delete amended reservation and its relations`() {
        amendedReservationDao.insert(AmendedReservationEntity(reservation = createBaseReservation(bookingReference = "BFKR12124"), basketReference = "1234"))
        roomCriteriaDao.insert(RoomCriteriaEntity.forAmendedReservation(numberOfAdults = 1, numberOfChildren = 0, roomId = "DBS,1", cot = false, roomType = RoomType.DOUBLE, amendedReservationReference = "BFKR12124"))
        roomGuestDao.insert(RoomGuestEntity.forAmendedReservation(roomId = "DBS,1", title = "Mr", firstName = "John", lastName = "Smith", amendedReservationReference = "BFKR12124", guestHistoryNumber = null))

        val deleteResult = amendedReservationDao.delete(amendedReservationDao.getByReference("BFKR12124")!!)

        assertThat(deleteResult).isEqualTo(1)
        assertThat(roomCriteriaDao.countAmended("BFKR12124")).isEqualTo(0)
        assertThat(roomGuestDao.countAmended("BFKR12124")).isEqualTo(0)
    }

    @Test
    fun `Observe updates for an amended reservation`() {
        val testObserver = amendedReservationDao.getAmendedReservationUpdates("BFKR12124").test()

        val amendedReservationEntity = AmendedReservationEntity(reservation = createBaseReservation(bookingReference = "BFKR12124"), basketReference = "1234")

        amendedReservationDao.insert(amendedReservationEntity)
        val reservationEntity = amendedReservationDao.getByReference("BFKR12124")!!

        amendedReservationDao.update(reservationEntity.copy(reservation = reservationEntity.reservation.copy(hotelCode = "LONKIN")))

        testObserver
                .assertValueCount(2)
                .assertValueAt(1) { it.reservation.hotelCode == "LONKIN" }

    }

    @Test
    fun `Observe updates for an amended reservation with Relations`() {
        val testObserver = amendedReservationDao.getAmendedReservationUpdatesWithRelationsUpdates("BFKR12124")
                .doOnNext {
                    println(it)
                }
                .test()

        val amendedReservationEntity = AmendedReservationEntity(reservation = createBaseReservation(bookingReference = "BFKR12124"), basketReference = "1234")

        amendedReservationDao.insert(amendedReservationEntity)

        roomCriteriaDao.insert(RoomCriteriaEntity.forAmendedReservation(numberOfAdults = 1, numberOfChildren = 0, roomId = "DBS,1", cot = false, roomType = RoomType.DOUBLE, amendedReservationReference = "BFKR12124"))
        roomCriteriaDao.insert(RoomCriteriaEntity.forAmendedReservation(numberOfAdults = 2, numberOfChildren = 1, roomId = "FAM,1", cot = false, roomType = RoomType.FAMILY, amendedReservationReference = "BFKR12124"))
        roomGuestDao.insert(RoomGuestEntity.forAmendedReservation(roomId = "DBS,1", title = "Mr", firstName = "John", lastName = "Smith", amendedReservationReference = "BFKR12124", guestHistoryNumber = null))
        roomGuestDao.insert(RoomGuestEntity.forAmendedReservation(roomId = "FAM,1", title = "Mr", firstName = "Maria", lastName = "Smith", amendedReservationReference = "BFKR12124", guestHistoryNumber = null))


        testObserver
                .assertValueCount(5)
                .assertValueAt(4) {
                    it.roomGuests.size == 2
                            && it.roomCriteria.size == 2
                }
    }
}