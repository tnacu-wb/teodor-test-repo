package com.whitbread.premierinn.amend

import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.common.model.RoomBreakdownFixture
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.domain.common.*
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

private val ROOM_BREAKDOWN = RoomBreakdownFixture.aRoomBreakdown()

class AmendReservationStateTest {

    @MockK
    lateinit var amendStateLeisure: AmendReservationState

    @MockK
    lateinit var amendStateInnBusiness: AmendReservationState

    @MockK
    lateinit var simplePersistenceManager: SimplePersistenceManager

    @MockK
    lateinit var businessPersistenceManager: BusinessPersistenceManager

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { businessPersistenceManager.getBusinessCustomerEmail() } returns "someemail@email.com"
        amendStateLeisure = AmendReservationState("en", simplePersistenceManager, businessPersistenceManager)
    }

    @Test
    fun `when null upsells passed THEN empty list is returned`() {
        val from = baseReservation.arrival
        val to = baseReservation.departure
        val filteredUpsells = amendStateLeisure.filterUpsellsByDates(null, Pair(from, to))
        assertNotNull(filteredUpsells)
        assertEquals(0, filteredUpsells.count())
    }

    @Test
    fun `when empty upsells passed THEN empty list is returned`() {
        val from = baseReservation.arrival
        val to = baseReservation.departure
        val filteredUpsells = amendStateLeisure.filterUpsellsByDates(emptyList(), Pair(from, to))
        assertNotNull(filteredUpsells)
        assertEquals(0, filteredUpsells.count())
    }

    @Test
    fun `when nights reduced by one THEN reduce Upsells by one also`() {
        val from = baseReservation.arrival
        val to = baseReservation.departure.minusDays(1)
        doTestFilterUpsellsByDates(from, to, baseReservation, 2)
    }

    @Test
    fun `when nights increased by one THEN don't modify Upsells`() {
        val from = baseReservation.arrival
        val to = baseReservation.departure.plusDays(1)
        doTestFilterUpsellsByDates(from, to, baseReservation, 3)
    }

    @Test
    fun `when dates moved AND no of nights remain the same THEN don't modify Upsells`() {
        val from = baseReservation.arrival.plusMonths(1)
        val to = baseReservation.departure.plusMonths(1)
        doTestFilterUpsellsByDates(from, to, baseReservation, 3)
    }

    @Test
    fun `when dates moved AND no of nights reduced THEN reduce Upsells by difference in nights`() {
        val from = baseReservation.arrival.plusMonths(1)
        val to = baseReservation.departure.plusMonths(1).minusDays(2)
        doTestFilterUpsellsByDates(from, to, baseReservation, 1)
    }

    @Test
    fun `when two Adults in one Room reduce booking by one night THEN reduce Upsells by one`() {
        val from = threeNightTwoAdultsOneRoomWBreakfast.arrival
        val to = threeNightTwoAdultsOneRoomWBreakfast.departure.minusDays(1)
        val filteredUpsells = doTestFilterUpsellsByDates(from, to, threeNightTwoAdultsOneRoomWBreakfast, 2)
        filteredUpsells.forEach { upsell: Upsell -> assertEquals(2, upsell.quantity) }
    }

    @Test
    fun `when two Adults in one Room increase booking by one night THEN keep Upsells unchanged`() {
        val from = threeNightTwoAdultsOneRoomWBreakfast.arrival
        val to = threeNightTwoAdultsOneRoomWBreakfast.departure.plusDays(1)
        val filteredUpsells = doTestFilterUpsellsByDates(from, to, threeNightTwoAdultsOneRoomWBreakfast, 3)
        filteredUpsells.forEach { upsell: Upsell -> assertEquals(2, upsell.quantity) }
    }

    @Test
    fun `when two Adults in one Room move booking by one month THEN keep Upsells unchanged`() {
        val from = threeNightTwoAdultsOneRoomWBreakfast.arrival.plusMonths(1)
        val to = threeNightTwoAdultsOneRoomWBreakfast.departure.plusMonths(1)
        val filteredUpsells = doTestFilterUpsellsByDates(from, to, threeNightTwoAdultsOneRoomWBreakfast, 3)
        filteredUpsells.forEach { upsell: Upsell -> assertEquals(2, upsell.quantity) }
    }

    @Test
    fun `when two Adults in two Rooms reduce booking by one night THEN reduce Upsells by one per day per room`() {
        val from = threeNightTwoAdultsTwoRoomsAllWBreakfast.arrival
        val to = threeNightTwoAdultsTwoRoomsAllWBreakfast.departure.minusDays(1)
        val filteredUpsells = doTestFilterUpsellsByDates(from, to, threeNightTwoAdultsTwoRoomsAllWBreakfast, 4)
        filteredUpsells.forEach { upsell: Upsell -> assertEquals(1, upsell.quantity) }
    }

    @Test
    fun `when two Adults in two Rooms increase booking by one night THEN keep Upsells unchanged`() {
        val from = threeNightTwoAdultsTwoRoomsAllWBreakfast.arrival
        val to = threeNightTwoAdultsTwoRoomsAllWBreakfast.departure.plusDays(1)
        val filteredUpsells = doTestFilterUpsellsByDates(from, to, threeNightTwoAdultsTwoRoomsAllWBreakfast, 6)
        filteredUpsells.forEach { upsell: Upsell -> assertEquals(1, upsell.quantity) }
    }

    @Test
    fun `when two Adults in two Rooms move booking by one month THEN keep Upsells unchanged`() {
        val from = threeNightTwoAdultsTwoRoomsAllWBreakfast.arrival.plusMonths(1)
        val to = threeNightTwoAdultsTwoRoomsAllWBreakfast.departure.plusMonths(1)
        val filteredUpsells = doTestFilterUpsellsByDates(from, to, threeNightTwoAdultsTwoRoomsAllWBreakfast, 6)
        filteredUpsells.forEach { upsell: Upsell -> assertEquals(1, upsell.quantity) }
    }

    @Test
    fun `when two Adults in one Room with two Breakfast and One Special move booking by one month THEN keep Upsells unchanged`() {
        val from = threeNightTwoAdultsOneRoomWBreakfastAndOneSpecial.arrival.plusMonths(1)
        val to = threeNightTwoAdultsOneRoomWBreakfastAndOneSpecial.departure.plusMonths(1)
        val filteredUpsells = doTestFilterUpsellsByDates(from, to, threeNightTwoAdultsOneRoomWBreakfastAndOneSpecial, 6)
        doTestGroupedUpsells(filteredUpsells, 2, 1, 3, 3)
    }

    @Test
    fun `when two Adults in one Room two Breakfast and One Special reduce booking by one day THEN reduce all types of Upsells by one of each type per day`() {
        val from = threeNightTwoAdultsOneRoomWBreakfastAndOneSpecial.arrival
        val to = threeNightTwoAdultsOneRoomWBreakfastAndOneSpecial.departure.minusDays(1)
        val filteredUpsells = doTestFilterUpsellsByDates(from, to, threeNightTwoAdultsOneRoomWBreakfastAndOneSpecial, 4)
        doTestGroupedUpsells(filteredUpsells, 2, 1, 2, 2)
    }

    @Test
    fun `when two Adults in one Room both with Breakfast and Special reduce booking by one day THEN reduce all types of Upsells by one of each type per day`() {
        val from = threeNightTwoAdultsOneRoomWBreakfastAndSpecial.arrival
        val to = threeNightTwoAdultsOneRoomWBreakfastAndSpecial.departure.minusDays(1)
        val filteredUpsells = doTestFilterUpsellsByDates(from, to, threeNightTwoAdultsOneRoomWBreakfastAndSpecial, 4)
        doTestGroupedUpsells(filteredUpsells, 2, 2, 2, 2)
    }

    @Test
    fun `when two VIP Adults in two Rooms both with Breakfast and Special reduce booking by one day THEN reduce all types of Upsells by one of each type per day per room`() {
        val from = threeNightTwoAdultsTwoRoomWBreakfastAndBothSpecial.arrival
        val to = threeNightTwoAdultsTwoRoomWBreakfastAndBothSpecial.departure.minusDays(1)
        val filteredUpsells = doTestFilterUpsellsByDates(from, to, threeNightTwoAdultsTwoRoomWBreakfastAndBothSpecial, 8)
        doTestGroupedUpsells(filteredUpsells, 1, 1, 4, 4)
    }

    @Test
    fun `when two VIP Adults in two Rooms both with Breakfast, one with Special reduce booking by one day THEN reduce all types of Upsells by one of each type per day per room`() {
        val from = threeNightTwoAdultsTwoRoomsWBreakfastAndOneSpecial.arrival
        val to = threeNightTwoAdultsTwoRoomsWBreakfastAndOneSpecial.departure.minusDays(1)
        val filteredUpsells = doTestFilterUpsellsByDates(from, to, threeNightTwoAdultsTwoRoomsWBreakfastAndOneSpecial, 6)
        doTestGroupedUpsells(filteredUpsells, 1, 1, 4, 2)
    }

    @Test
    fun `when business user is logged in then return maxnight and maxarrival from business storage`() {
        amendStateInnBusiness = AmendReservationState("en", simplePersistenceManager, businessPersistenceManager, isBusinessBooking = true)

        every { businessPersistenceManager.getMaxNightsInnBusiness() } returns 1
        every { businessPersistenceManager.getMaxArrivalDateInnBusiness() } returns 365

        assertEquals(1, amendStateInnBusiness.maxNights())
        assertEquals(365, amendStateInnBusiness.maxArrivalDate())
    }

    @Test
    fun `when leisure user is logged in then return maxnight and maxarrival from simple storage`() {
        every { simplePersistenceManager.getMaxNightsLeisure() } returns 14
        every { simplePersistenceManager.getMaxArrivalDateLeisure() } returns 365

        assertEquals(14, amendStateLeisure.maxNights())
        assertEquals(365, amendStateLeisure.maxArrivalDate())
    }

    private fun doTestGroupedUpsells(filteredUpsells: List<Upsell>,
                                     expectedBreakfastQuantity: Int, expectedOtherQuantity: Int,
                                     expectedBreakfastUpsells: Int, expectedOtherUpsellsQuantity: Int) {
        val groupedUpsells = filteredUpsells.groupBy { upsell: Upsell -> upsell.category }

        groupedUpsells[Upsell.Category.BREAKFAST]?.forEach { upsell: Upsell ->
            assertEquals(
                    expectedBreakfastQuantity,
                    upsell.quantity
            )
        }
        groupedUpsells[Upsell.Category.OTHER]?.forEach { upsell: Upsell ->
            assertEquals(
                    expectedOtherQuantity,
                    upsell.quantity
            )
        }

        assertEquals(expectedBreakfastUpsells, groupedUpsells[Upsell.Category.BREAKFAST]?.count())
        assertEquals(expectedOtherUpsellsQuantity, groupedUpsells[Upsell.Category.OTHER]?.count())
    }

    private fun doTestFilterUpsellsByDates(from: LocalDate, to: LocalDate, reservation: Reservation, expectedUpsells: Int): List<Upsell> {
        val filteredUpsells = amendStateLeisure.filterUpsellsByDates(reservation.upsells, Pair(from, to))
        assertEquals(expectedUpsells, filteredUpsells.size)
        return filteredUpsells
    }

    companion object TestData {

        private val day11th = LocalDate.of(2020, 11, 11)
        private val day12th = LocalDate.of(2020, 11, 12)
        private val day13th = LocalDate.of(2020, 11, 13)
        private val day14th = LocalDate.of(2020, 11, 14)

        private val baseRoomCriteria = RoomCriteria(numberOfAdults = 1,
                numberOfChildren = 0,
                numberOfInfants = 0,
                includeCot = false,
                roomType = RoomType.DOUBLE,
                roomNumber = 1)

        private val baseGuest = Guest(title = "Mr",
                firstName = "Mark",
                lastName = "O'Meara",
                guestHistoryNumber = null,
                address = Address(line1 = "120 Holborn"),
                emailAddress = null,
                phoneNumber = null
        )

        private val baseUpsell = Upsell(
                roomId = "1",
                quantity = 1,
                category = Upsell.Category.BREAKFAST,
                legend = "Meal Deal",
                postingDate = LocalDate.now(),
                unitCost = PriceDomain(28.50.toFloat(), "GBP"),
                code = "17"
        )

        private val guestGill = baseGuest.copy(title = "Mrs", firstName = "Gill")
        private val guestTrump = baseGuest.copy(title = "Mrs", firstName = "Big", lastName = "Hands")

        private val upsellBreakfast12thFor1 = baseUpsell.copy(postingDate = day12th)
        private val upsellBreakfast13thFor1 = baseUpsell.copy(postingDate = day13th)
        private val upsellBreakfast14thFor1 = baseUpsell.copy(postingDate = day14th)

        private val upsellBreakfast12thFor2 = upsellBreakfast12thFor1.copy(quantity = 2)
        private val upsellBreakfast13thFor2 = upsellBreakfast13thFor1.copy(quantity = 2)
        private val upsellBreakfast14thFor2 = upsellBreakfast14thFor1.copy(quantity = 2)

        private val upsellSpecial12th = baseUpsell.copy(category = Upsell.Category.OTHER, postingDate = day12th)
        private val upsellSpecial13th = baseUpsell.copy(category = Upsell.Category.OTHER, postingDate = day13th)
        private val upsellSpecial14th = baseUpsell.copy(category = Upsell.Category.OTHER, postingDate = day14th)

        private val upsellSpecial12thFor2 = upsellSpecial12th.copy(quantity = 2)
        private val upsellSpecial13thFor2 = upsellSpecial13th.copy(quantity = 2)
        private val upsellSpecial14thFor2 = upsellSpecial14th.copy(quantity = 2)

        val baseReservation = Reservation(bookingReference = "BER32423423",
                arrival = day11th,
                departure = day14th,
                hotelCode = "LONBLA",
                roomsCriteria = listOf(baseRoomCriteria),
                roomsLeadGuest = listOf(guestGill),
                cancelable = true,
                upsells = listOf(upsellBreakfast12thFor1, upsellBreakfast13thFor1, upsellBreakfast14thFor1),
                roomsBreakdown = listOf(ROOM_BREAKDOWN)
        )

        val threeNightTwoAdultsTwoRoomsAllWBreakfast = baseReservation.copy(
                roomsCriteria = listOf(baseRoomCriteria, baseRoomCriteria),
                roomsLeadGuest = listOf(guestGill, guestTrump),
                upsells = listOf(upsellBreakfast12thFor1, upsellBreakfast13thFor1, upsellBreakfast14thFor1,
                        upsellBreakfast12thFor1, upsellBreakfast13thFor1, upsellBreakfast14thFor1)
        )

        val threeNightTwoAdultsOneRoomWBreakfast = baseReservation.copy(
                roomsCriteria = listOf(baseRoomCriteria),
                roomsLeadGuest = listOf(guestGill, guestTrump),
                upsells = listOf(upsellBreakfast12thFor2, upsellBreakfast13thFor2, upsellBreakfast14thFor2)
        )

        val threeNightTwoAdultsOneRoomWBreakfastAndOneSpecial = threeNightTwoAdultsOneRoomWBreakfast.copy(
                upsells = listOf(upsellBreakfast12thFor2, upsellBreakfast13thFor2, upsellBreakfast14thFor2,
                        upsellSpecial12th, upsellSpecial13th, upsellSpecial14th)
        )

        val threeNightTwoAdultsOneRoomWBreakfastAndSpecial = threeNightTwoAdultsOneRoomWBreakfast.copy(
                upsells = listOf(upsellBreakfast12thFor2, upsellBreakfast13thFor2, upsellBreakfast14thFor2,
                        upsellSpecial12thFor2, upsellSpecial13thFor2, upsellSpecial14thFor2)
        )

        val threeNightTwoAdultsTwoRoomsWBreakfastAndOneSpecial = threeNightTwoAdultsOneRoomWBreakfast.copy(
                upsells = listOf(upsellBreakfast12thFor1, upsellBreakfast13thFor1, upsellBreakfast14thFor1,
                        upsellBreakfast12thFor1, upsellBreakfast13thFor1, upsellBreakfast14thFor1,
                        upsellSpecial12th, upsellSpecial13th, upsellSpecial14th)
        )

        val threeNightTwoAdultsTwoRoomWBreakfastAndBothSpecial = threeNightTwoAdultsOneRoomWBreakfast.copy(
                upsells = listOf(upsellBreakfast12thFor1, upsellBreakfast13thFor1, upsellBreakfast14thFor1,
                        upsellBreakfast12thFor1, upsellBreakfast13thFor1, upsellBreakfast14thFor1,
                        upsellSpecial12th, upsellSpecial13th, upsellSpecial14th,
                        upsellSpecial12th, upsellSpecial13th, upsellSpecial14th)
        )
    }
}