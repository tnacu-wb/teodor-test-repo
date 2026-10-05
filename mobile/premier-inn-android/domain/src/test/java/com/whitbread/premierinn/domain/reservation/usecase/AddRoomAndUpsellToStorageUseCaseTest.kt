package com.whitbread.premierinn.domain.reservation.usecase

import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.common.Guest
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.common.Upsell
import com.whitbread.premierinn.domain.common.UpsellAvailable
import com.whitbread.premierinn.domain.common.model.RoomBreakdownFixture
import com.whitbread.premierinn.domain.common.model.UpsellFixture
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import com.whitbread.premierinn.domain.reservation.repository.AmendedReservationRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Observable
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate

private val roomBreakdown = RoomBreakdownFixture.aRoomBreakdown()

class AddRoomAndUpsellToStorageUseCaseTest {

    private val repository: AmendedReservationRepository = mockk()
    private lateinit var useCase: AddRoomAndUpsellToStorageUseCase

    @Before
    fun setUp() {
        useCase = AddRoomAndUpsellToStorageUseCase(repository)
    }

    @Test
    fun `WHEN room has been added THEN removes breakfast upsells and stores new room in DB`() {
        // Add a breakfast upsell to actually test removal
        val reservationWithBreakfast = originalReservation.copy(
            upsells = listOf(
                Upsell(
                    quantity = 1,
                    category = Upsell.Category.BREAKFAST,
                    legend = "Premier Inn Breakfast",
                    postingDate = LocalDate.of(2020, 11, 7),
                    unitCost = PriceDomain(9.99f, GBP),
                    code = "15",
                    roomId = ""
                ),
                UpsellFixture.aUpsell(
                    quantity = 1,
                    category = Upsell.Category.OTHER,
                    postingDate = LocalDate.of(2020, 11, 7)
                )
            )
        )
        every { repository.getAmendedReservation(reservationWithBreakfast.bookingReference) } returns Observable.just(
            reservationWithBreakfast
        )
        every {
            repository.updateReservationAddRoom(
                reservationWithBreakfast.bookingReference,
                any()
            )
        } returns Completable.complete()

        val testObserver = useCase.invoke(reservationWithBreakfast.bookingReference).test()
        testObserver.assertComplete()
        testObserver.assertNoErrors()

        val reservationSlot = slot<Reservation>()
        verify {
            repository.updateReservationAddRoom(
                eq(reservationWithBreakfast.bookingReference),
                capture(reservationSlot)
            )
        }
        val actualUpsells = reservationSlot.captured.upsells
        val expectedUpsell = UpsellFixture.aUpsell(
            quantity = 1,
            category = Upsell.Category.OTHER,
            postingDate = LocalDate.of(2020, 11, 7)
        )
        assertEquals(1, actualUpsells.size)
        assertEquals(expectedUpsell, actualUpsells[0])
    }

    @Test
    fun `WHEN selected upsell is present THEN store it in DB`() {
        every { repository.getAmendedReservation(originalReservation.bookingReference) } returns Observable.just(
            originalReservation
        )
        every {
            repository.updateReservationAddRoom(
                originalReservation.bookingReference,
                any()
            )
        } returns Completable.complete()

        val testObserver =
            useCase.invoke(originalReservation.bookingReference, selectedUpsell).test()
        testObserver.assertComplete()
        testObserver.assertNoErrors()

        val reservationSlot = slot<Reservation>()
        verify(exactly = 1) {
            repository.updateReservationAddRoom(
                eq(originalReservation.bookingReference),
                capture(reservationSlot)
            )
        }
        val actualUpsells = reservationSlot.captured.upsells
        assertEquals(2, actualUpsells.size)
        val expectedFreeChild = Upsell(
            quantity = 2,
            category = Upsell.Category.BREAKFAST,
            legend = "Free Child Breakfast",
            postingDate = LocalDate.of(2020, 11, 8),
            unitCost = PriceDomain(0f, GBP),
            code = "11",
            roomId = ""
        )
        val expectedBreakfast = Upsell(
            quantity = 1,
            category = Upsell.Category.BREAKFAST,
            legend = "Premier Inn Breakfast",
            postingDate = LocalDate.of(2020, 11, 8),
            unitCost = PriceDomain(9.99f, GBP),
            code = "15",
            roomId = ""
        )
        // Find by code for clearer diagnostics
        val actualFreeChild = actualUpsells.find { it.code == "11" }
        val actualBreakfast = actualUpsells.find { it.code == "15" }
        assertEquals(expectedFreeChild, actualFreeChild)
        assertEquals(expectedBreakfast, actualBreakfast)
    }

    @Test
    fun `WHEN update is stored in repository successfully THEN Updated state is returned`() {
        every { repository.getAmendedReservation(originalReservation.bookingReference) } returns Observable.just(
            originalReservation
        )
        every {
            repository.updateReservationAddRoom(
                originalReservation.bookingReference,
                originalReservation
            )
        } returns Completable.complete()
        every { repository.updateReservation(any(), any()) } returns Completable.complete()

        val testObserver =
            useCase.performUpdateAddRoom(originalReservation.bookingReference, originalReservation)
                .test()
        testObserver.assertValueAt(0, StoreUpdatedAmendedReservation.UpdateState.Updated)
        testObserver.assertComplete()
        testObserver.assertNoErrors()
    }

    @Test
    fun `WHEN update fails in repository THEN Error state is returned`() {
        val exception = Exception()
        every { repository.getAmendedReservation(originalReservation.bookingReference) } returns Observable.just(
            originalReservation
        )
        every { repository.updateReservationAddRoom(any(), any()) } returns Completable.error(
            exception
        )

        val testObserver =
            useCase.performUpdateAddRoom(originalReservation.bookingReference, originalReservation)
                .test()
        testObserver.assertValueAt(
            0,
            StoreUpdatedAmendedReservation.UpdateState.Error(exception = exception)
        )
        testObserver.assertComplete()
        testObserver.assertNoErrors()
    }

    companion object {
        private val roomCriteria1 = RoomCriteria(
            numberOfAdults = 1,
            numberOfChildren = 2,
            numberOfInfants = 0,
            includeCot = false,
            roomType = RoomType.DOUBLE,
            roomNumber = 1
        )

        private val leadGuest1 = Guest(
            title = "Sir",
            firstName = "Dead",
            lastName = "Pool",
            guestHistoryNumber = null,
            address = Address(line1 = "120 Holborn"),
            emailAddress = null,
            phoneNumber = null
        )


        private val selectedUpsell = UpsellAvailable(
            description = "",
            foodUpsell = true,
            freeBreakfastTrigger = true,
            availableForChildren = true,
            code = "15",
            freeBreakfastCode = "11",
            legend = "Premier Inn Breakfast",
            unitCost = PriceDomain(9.99f, GBP),
            freeBreakfastOption = true,
            attachments = emptyList()
        )

        val originalReservation = Reservation(
            bookingReference = "BER2334242",
            arrival = LocalDate.of(2020, 11, 6),
            departure = LocalDate.of(2020, 11, 8),
            hotelCode = "LONBLA",
            roomsCriteria = listOf(roomCriteria1),
            roomsLeadGuest = listOf(leadGuest1),
            cancelable = false,
            upsells = emptyList(),
            roomsBreakdown = listOf(roomBreakdown)
        )

    }
}