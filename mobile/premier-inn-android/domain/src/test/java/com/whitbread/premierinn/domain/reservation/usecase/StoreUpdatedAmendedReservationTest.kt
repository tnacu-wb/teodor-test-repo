package com.whitbread.premierinn.domain.reservation.usecase

import com.whitbread.premierinn.domain.authentication.repository.AuthenticationRepository
import com.whitbread.premierinn.domain.authentication.usecase.GetFreshIdTokenAndRetryOnce
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.Guest
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.common.hoteldetails.repository.GraphQLHotelDetailsRepository
import com.whitbread.premierinn.domain.common.model.RoomBreakdownFixture
import com.whitbread.premierinn.domain.graphql.amend.usecase.GraphQLAmendUseCase
import com.whitbread.premierinn.domain.graphql.hdp.entity.DailyPriceDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.HotelAvailabilityDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomOptionsDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomPriceBreakdownDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomRateDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomTypeDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelAvailabilityRequestBody
import com.whitbread.premierinn.domain.mockErrorHotelNoAvailabilityOpera
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import com.whitbread.premierinn.domain.reservation.repository.AmendedReservationRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate

class StoreUpdatedAmendedReservationTest {

    private val repository: AmendedReservationRepository = mockk()
    private val graphQLHotelDetailsRepository: GraphQLHotelDetailsRepository = mockk()
    private val graphQLAmendUseCase: GraphQLAmendUseCase = mockk()
    private lateinit var storeUpdatedAmendedReservationUseCase: StoreUpdatedAmendedReservation
    private val authenticationRepository = mockk<AuthenticationRepository>()
    private val getFreshIdTokenAndRetryOnce = mockk<GetFreshIdTokenAndRetryOnce>()

    @Before
    fun setup() {
        every { repository.updateReservation(any(), any()) } returns Completable.complete()

        every { graphQLHotelDetailsRepository.getHotelAvailability(any(), any()) } returns
                Single.just(availabilityFor(RoomType.DOUBLE, RoomType.SINGLE))

        every { graphQLAmendUseCase.amendEditRoom(any(), any()) } returns
                Single.just(mockk { every { tempBookingRef } returns "mockTempBookingRef" })

        storeUpdatedAmendedReservationUseCase = StoreUpdatedAmendedReservation(
            repository,
            graphQLHotelDetailsRepository,
            graphQLAmendUseCase,
            authenticationRepository,
            getFreshIdTokenAndRetryOnce
        )

    }

    @Test
    fun `WHEN room type updated THEN availability check is called`() {
        val updatedRoomCriteria = originalRoomCriteria2.copy(roomType = RoomType.SINGLE)
        val updatedReservation = updatedReservationWith(updatedRoomCriteria)

        // Override default stub: Model availability explicitly for DOUBLE + SINGLE
        every { graphQLHotelDetailsRepository.getHotelAvailability(any(), any()) } returns
                Single.just(availabilityFor(RoomType.DOUBLE, RoomType.SINGLE))

        val testObserver = runUseCase(
            originalReservation,
            updatedReservation,
            originalLeadGuest1,
            updatedRoomCriteria
        )

        testObserver.assertValueAt(0) { it is StoreUpdatedAmendedReservation.UpdateState.Loading }
        testObserver.assertValueAt(1) { state ->
            val s = state as? StoreUpdatedAmendedReservation.UpdateState.AvailabilityUpdated
                ?: return@assertValueAt false
            s.updatedReservation == updatedReservation
        }
        testObserver.assertValueCount(2)
        testObserver.assertComplete()

        verify(exactly = 1) {
            graphQLHotelDetailsRepository.getHotelAvailability(
                match<HotelAvailabilityRequestBody> {
                    it.arrival == updatedReservation.arrival.toString() &&
                            it.departure == updatedReservation.departure.toString() &&
                            it.hotel.identifier == updatedReservation.hotelCode &&
                            it.rooms.size == updatedReservation.roomsCriteria.size &&
                            it.ratePlanCodes == emptyList<String>()
                },
                null
            )
        }
    }

    @Test
    fun `WHEN cot updated THEN availability check is required`() {
        val updatedCot = originalRoomCriteria2.copy(includeCot = true)
        val updatedReservation = updatedReservationWith(updatedCot)

        every { graphQLHotelDetailsRepository.getHotelAvailability(any(), any()) } returns
                Single.just(availabilityFor(RoomType.DOUBLE, RoomType.DOUBLE))

        val testObserver =
            runUseCase(originalReservation, updatedReservation, originalLeadGuest1, updatedCot)

        testObserver.assertValueAt(0) { it is StoreUpdatedAmendedReservation.UpdateState.Loading }
        testObserver.assertValueAt(1) {
            val s = it as? StoreUpdatedAmendedReservation.UpdateState.AvailabilityUpdated
                ?: return@assertValueAt false
            s.updatedReservation == updatedReservation
        }
        testObserver.assertValueCount(2)
        testObserver.assertComplete()

        verify(exactly = 1) {
            graphQLHotelDetailsRepository.getHotelAvailability(
                match<HotelAvailabilityRequestBody> {
                    it.arrival == updatedReservation.arrival.toString() &&
                            it.departure == updatedReservation.departure.toString() &&
                            it.hotel.identifier == updatedReservation.hotelCode &&
                            it.rooms.size == updatedReservation.roomsCriteria.size &&
                            it.ratePlanCodes == emptyList<String>()
                },
                null
            )
        }
    }

    @Test
    fun `WHEN availability says no rooms THEN NoAvailability emitted`() {
        val updatedRoomType = originalRoomCriteria2.copy(roomType = RoomType.SINGLE)
        val updatedReservation = updatedReservationWith(updatedRoomType)

        every { graphQLHotelDetailsRepository.getHotelAvailability(any(), any()) } returns
                Single.just(mockErrorHotelNoAvailabilityOpera())

        val testObserver =
            runUseCase(originalReservation, updatedReservation, originalLeadGuest1, updatedRoomType)

        testObserver.assertValueAt(0) { it is StoreUpdatedAmendedReservation.UpdateState.Loading }
        testObserver.assertValueAt(1) {
            it == StoreUpdatedAmendedReservation.UpdateState.NoAvailability(RoomType.SINGLE)
        }
        testObserver.assertComplete()

        verify(exactly = 1) { graphQLHotelDetailsRepository.getHotelAvailability(any(), any()) }
        verify(exactly = 0) { repository.updateReservation(any(), any()) }
    }

    @Test
    fun `WHEN availability is running THEN emit Loading and do not complete`() {
        val updatedRoom = originalRoomCriteria2.copy(roomType = RoomType.SINGLE)
        val updatedReservation = updatedReservationWith(updatedRoom)

        every {
            graphQLHotelDetailsRepository.getHotelAvailability(
                any(),
                any()
            )
        } returns Single.never()
        every { repository.updateReservation(any(), any()) } returns Completable.never()

        val testObserver =
            runUseCase(originalReservation, updatedReservation, originalLeadGuest1, updatedRoom)

        testObserver.assertValueAt(0) { it is StoreUpdatedAmendedReservation.UpdateState.Loading }
        testObserver.assertNotComplete()
    }

    @Test
    fun `WHEN change does not need availability THEN emit Loading and stay open`() {
        val updatedReservation = originalReservation.copy(
            roomsLeadGuest = listOf(
                originalLeadGuest1.copy(firstName = "UpdatedName"),
                originalLeadGuest2
            )
        )

        every { repository.updateReservation(any(), any()) } returns Completable.never()

        val testObserver = runUseCase(
            originalReservation,
            updatedReservation,
            originalLeadGuest1.copy(firstName = "UpdatedName"),
            originalRoomCriteria1
        )

        testObserver.assertValueAt(0) { it is StoreUpdatedAmendedReservation.UpdateState.Loading }
        testObserver.assertValueCount(1)
        testObserver.assertNotComplete()
        verify(exactly = 0) { graphQLHotelDetailsRepository.getHotelAvailability(any(), any()) }
    }

    @Test
    fun `WHEN change does not need availability AND repo succeeds THEN Loading then Updated`() {
        val updatedReservation = originalReservation.copy(
            roomsLeadGuest = listOf(
                originalLeadGuest1.copy(firstName = "UpdatedName"),
                originalLeadGuest2
            )
        )

        every { repository.updateReservation(any(), any()) } returns Completable.complete()

        val testObserver = runUseCase(
            originalReservation,
            updatedReservation,
            originalLeadGuest1.copy(firstName = "UpdatedName"),
            originalRoomCriteria1
        )

        testObserver.assertValueAt(0) { it is StoreUpdatedAmendedReservation.UpdateState.Loading }
        testObserver.assertValueAt(1) { it is StoreUpdatedAmendedReservation.UpdateState.Updated }
        testObserver.assertComplete()
        testObserver.assertValueCount(2)

        verify(exactly = 1) { repository.updateReservation(any(), any()) }
        verify(exactly = 0) { graphQLHotelDetailsRepository.getHotelAvailability(any(), any()) }
    }

    @Test
    fun `WHEN repo update errors THEN emit Loading then Error`() {
        val updatedReservation = originalReservation.copy(
            roomsLeadGuest = listOf(
                originalLeadGuest1.copy(firstName = "UpdatedName"),
                originalLeadGuest2
            )
        )

        val exception = RuntimeException("update failed")
        every { repository.updateReservation(any(), any()) } returns Completable.error(exception)

        val testObserver = runUseCase(
            originalReservation,
            updatedReservation,
            originalLeadGuest1.copy(firstName = "UpdatedName"),
            originalRoomCriteria1
        )

        testObserver.assertValueAt(0) { it is StoreUpdatedAmendedReservation.UpdateState.Loading }
        testObserver.assertValueAt(1) { it is StoreUpdatedAmendedReservation.UpdateState.Error }
        testObserver.assertComplete()
        testObserver.assertValueCount(2)

        verify(exactly = 1) { repository.updateReservation(any(), any()) }
    }

    @Test
    fun `WHEN adults increase without room type change THEN update stored`() {
        val updatedCriteria = originalRoomCriteria2.copy(numberOfAdults = 2)
        val updatedReservation = updatedReservationWith(updatedCriteria)

        every { graphQLHotelDetailsRepository.getHotelAvailability(any(), any()) } returns
                Single.just(availabilityFor(RoomType.DOUBLE, RoomType.DOUBLE))

        val testObserver =
            runUseCase(originalReservation, updatedReservation, originalLeadGuest1, updatedCriteria)

        testObserver.assertValueAt(0) { it is StoreUpdatedAmendedReservation.UpdateState.Loading }
        testObserver.assertValueAt(1) {
            val s = it as? StoreUpdatedAmendedReservation.UpdateState.AvailabilityUpdated
                ?: return@assertValueAt false
            s.updatedReservation == updatedReservation
        }
        testObserver.assertValueCount(2)
        testObserver.assertComplete()

        verify(exactly = 1) { graphQLHotelDetailsRepository.getHotelAvailability(any(), any()) }
    }

    @Test
    fun `WHEN adults decrease without room type change THEN update stored`() {
        val updatedCriteria = originalRoomCriteria2.copy(numberOfAdults = 0)
        val updatedReservation = updatedReservationWith(updatedCriteria)

        every { graphQLHotelDetailsRepository.getHotelAvailability(any(), any()) } returns
                Single.just(availabilityFor(RoomType.DOUBLE, RoomType.DOUBLE))

        val testObserver =
            runUseCase(originalReservation, updatedReservation, originalLeadGuest1, updatedCriteria)

        testObserver.assertValueAt(0) { it is StoreUpdatedAmendedReservation.UpdateState.Loading }
        testObserver.assertValueAt(1) {
            val s = it as? StoreUpdatedAmendedReservation.UpdateState.AvailabilityUpdated
                ?: return@assertValueAt false
            s.updatedReservation == updatedReservation
        }
        testObserver.assertValueCount(2)
        testObserver.assertComplete()

        verify(exactly = 1) { graphQLHotelDetailsRepository.getHotelAvailability(any(), any()) }
    }

    companion object {
        private val roomBreakdown = RoomBreakdownFixture.aRoomBreakdown()

        val originalRoomCriteria1 = RoomCriteria(1, 0, 0, false, RoomType.DOUBLE, 0)
        val originalRoomCriteria2 = RoomCriteria(1, 0, 0, false, RoomType.DOUBLE, 1)

        val originalLeadGuest1 = Guest(
            roomNumber = 0,
            roomId = "",
            title = "Mr",
            firstName = "Mark",
            lastName = "O'Meara",
            guestHistoryNumber = null,
            address = Address(line1 = "120 Holborn"),
            emailAddress = null,
            phoneNumber = null
        )

        val originalLeadGuest2 = Guest(
            roomNumber = 1,
            roomId = "",
            title = "Ms",
            firstName = "Jane",
            lastName = "Bloggs",
            guestHistoryNumber = null,
            address = Address(line1 = "Buckingham Palace"),
            emailAddress = null,
            phoneNumber = null
        )

        val originalReservation = Reservation(
            bookingReference = "BER2334242",
            arrival = LocalDate.of(2020, 11, 11),
            departure = LocalDate.of(2020, 11, 12),
            hotelCode = "LONBLA",
            roomsCriteria = listOf(originalRoomCriteria1, originalRoomCriteria2),
            roomsLeadGuest = listOf(originalLeadGuest1, originalLeadGuest2),
            roomsBreakdown = listOf(roomBreakdown),
            cancelable = false,
            upsells = emptyList()
        )

        val deviceLocale = java.util.Locale.UK
        const val bookingChannel = "MOBILE"
        const val hotelBrand = "PI"
        const val selectedRatePlan = "FLEX"
        val listOfBookingRooms = emptyList<Booking.Room>()
        const val roomId = "roomId"
        const val tempBasketRef = "basketRef"
        const val token = "token"
        const val isEmployeeBooking = false
        const val isBusinessBooking = false
    }

    private fun updatedReservationWith(updatedSecond: RoomCriteria): Reservation =
        originalReservation.copy(roomsCriteria = listOf(originalRoomCriteria1, updatedSecond))

    private fun runUseCase(
        original: Reservation,
        updated: Reservation,
        updatedLeadGuest: Guest,
        updatedRoomCriteria: RoomCriteria
    ) = storeUpdatedAmendedReservationUseCase(
        original,
        updated,
        deviceLocale,
        bookingChannel,
        hotelBrand,
        selectedRatePlan,
        listOfBookingRooms,
        roomId,
        tempBasketRef,
        token,
        updatedLeadGuest,
        updatedRoomCriteria,
        isEmployeeBooking,
        isBusinessBooking
    ).test()

    private fun availabilityFor(vararg roomTypes: RoomType): HotelAvailabilityDomain {
        val types =
            if (roomTypes.isNotEmpty()) roomTypes else arrayOf(RoomType.DOUBLE, RoomType.SINGLE)
        return HotelAvailabilityDomain(
            available = true,
            hotelId = "LONBLA",
            endDate = "2020-11-12",
            startDate = "2020-11-11",
            limitedAvailability = false,
            roomRateDomainList = listOf(
                RoomRateDomain(
                    ratePlanCode = selectedRatePlan,
                    cellCode = "",
                    promotionCode = null,
                    roomTypesDomainList = types.map { roomType ->
                        RoomTypeDomain(
                            adults = 1,
                            children = 0,
                            cotRequested = false,
                            roomType = roomType.code,
                            roomOptionsDomainList = listOf(
                                RoomOptionsDomain(
                                    cotAvailable = true,
                                    pmsRoomType = roomType.code,
                                    roomPriceBreakdownDomain = RoomPriceBreakdownDomain(
                                        currencyCode = "GBP",
                                        dailyPricesDomainList = listOf(
                                            DailyPriceDomain(
                                                date = "2020-11-11",
                                                netPrice = 50.0
                                            )
                                        ),
                                        totalNetAmount = 50.0,
                                        packageCode = null,
                                        packageAmount = null,
                                        baseRateAmount = null
                                    ),
                                    roomClass = "STANDARD",
                                    silentSubstitution = false,
                                    specialRequests = null
                                )
                            )
                        )
                    }
                )
            ),
            packages = null,
            listOfRatesClassification = emptyList(),
            listOfRoomTypeInfo = emptyList(),
            error = null
        )
    }
}