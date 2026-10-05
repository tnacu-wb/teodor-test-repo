package com.whitbread.premierinn.domain.reservation.usecase

import com.whitbread.premierinn.domain.common.*
import com.whitbread.premierinn.domain.common.model.RoomBreakdownFixture
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import com.whitbread.premierinn.domain.reservation.repository.AmendedReservationRepository
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate

private val ROOM_BREAKDOWN = RoomBreakdownFixture.aRoomBreakdown()

class SubmitAmendedReservationUseCaseTest {

    private val repository: AmendedReservationRepository = mockk()
    private lateinit var sut: SubmitAmendedReservationUseCase

    @Before
    fun setUp() {
        sut = SubmitAmendedReservationUseCase(repository)
    }

    @Test
    fun `WHEN submit updated reservation THEN single is returned`() {
        every {
            repository.submit(referenceNumber, updatedArrival, surname, amendedReservation, paymentDetails, cardSecurityCode, listOf())
        } returns Single.just(mockk())

        repository.submit(referenceNumber, updatedArrival, surname, amendedReservation, paymentDetails, cardSecurityCode, listOf())
            .test()
            .assertNoErrors()
    }

    @Test
    fun `WHEN submitting an updated reservation returns error`() {
        every {
            repository.submit(referenceNumber, updatedArrival, surname, amendedReservation, paymentDetails, cardSecurityCode, listOf())
        } returns Single.error(exception)

        repository.submit(referenceNumber, updatedArrival, surname, amendedReservation, paymentDetails, cardSecurityCode, listOf())
            .test()
            .assertError(exception)
    }

    @Test
    fun `WHEN update existing booking THEN completable is returned`() {
        every {
            repository.updateBooking(referenceNumber, updatedArrival, updatedDeparture, surname)
        } returns Completable.complete()

        sut.updateBooking(referenceNumber, updatedArrival, updatedDeparture, surname).test()
                .assertNoErrors()
    }

    @Test
    fun `WHEN update existing booking returns error`() {
        every {
            repository.updateBooking(referenceNumber, updatedArrival, updatedDeparture, surname)
        } returns Completable.error(exception)

        sut.updateBooking(referenceNumber, updatedArrival, updatedDeparture, surname).test()
                .assertError(exception)
    }

    companion object {
        private val exception = Exception()
        private const val referenceNumber = "BER2334242"
        private val updatedArrival = LocalDate.of(2020, 8, 11)
        private val updatedDeparture = LocalDate.of(2020, 8, 14)
        private const val surname = "Luci"
        private const val cardSecurityCode = "123"
        private val paymentDetails = PaymentDetails(
                billingAddress = Address(line1 = "120 Holborn"),
                cardType = "VI",
                cardNumber = "************1111",
                cardSecurityCode = "123",
                holdersFullName = "Nil",
                expiryDate = "12/22",
                issueNumber = null,
                startDate = null,
                prepaymentRequired = true,
                useExistingCard = true)

        private val roomCriteria1 = RoomCriteria(numberOfAdults = 1,
                numberOfChildren = 2,
                numberOfInfants = 0,
                includeCot = false,
                roomType = RoomType.DOUBLE,
                roomNumber = 1)

        private val roomCriteria2 = RoomCriteria(numberOfAdults = 1,
                numberOfChildren = 1,
                numberOfInfants = 0,
                includeCot = false,
                roomType = RoomType.FAMILY,
                roomNumber = 2)

        private val leadGuest1 = Guest(title = "Sir",
                firstName = "Dead",
                lastName = "Pool",
                guestHistoryNumber = null,
                address = Address(line1 = "120 Holborn"),
                emailAddress = null,
                phoneNumber = null
        )
        private val leadGuest2 = Guest(title = "Mrs",
                firstName = "Black",
                lastName = "Widow",
                guestHistoryNumber = null,
                address = Address(line1 = "Buckingham Palace"),
                emailAddress = null,
                phoneNumber = null
        )

        val amendedReservation = Reservation(
                bookingReference = "BER2334242",
                arrival = LocalDate.of(2020, 11, 6),
                departure = LocalDate.of(2020, 11, 8),
                hotelCode = "LONBLA",
                roomsCriteria = listOf(roomCriteria1, roomCriteria2),
                roomsLeadGuest = listOf(leadGuest1, leadGuest2),
                cancelable = false,
                upsells = emptyList(),
                roomsBreakdown = listOf(ROOM_BREAKDOWN)
        )
    }
}