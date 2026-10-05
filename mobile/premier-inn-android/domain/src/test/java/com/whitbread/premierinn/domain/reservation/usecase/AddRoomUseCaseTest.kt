package com.whitbread.premierinn.domain.reservation.usecase

import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.Guest
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.common.convertToRatePlansOperaForAmend
import com.whitbread.premierinn.domain.common.hoteldetails.repository.GraphQLHotelDetailsRepository
import com.whitbread.premierinn.domain.common.model.RoomBreakdownFixture
import com.whitbread.premierinn.domain.mockErrorHotelAvailabilityOpera
import com.whitbread.premierinn.domain.mockErrorHotelNoAvailabilityOpera
import com.whitbread.premierinn.domain.mockSuccessfulHotelAvailability
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import com.whitbread.premierinn.domain.reservation.repository.AmendedReservationRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate
import java.util.Locale

private val ROOM_BREAKDOWN = RoomBreakdownFixture.aRoomBreakdown()

class AddRoomUseCaseTest {

    private val repository: AmendedReservationRepository = mockk()
    private val graphQLHotelDetailsRepository: GraphQLHotelDetailsRepository = mockk()
    private val listOfBookingRooms: List<Booking.Room> = mockk()
    private lateinit var updatedRoomCriteria: RoomCriteria
    private lateinit var sut: AddRoomUseCase

    @Before
    fun setUp() {
        sut = AddRoomUseCase(repository, graphQLHotelDetailsRepository)
        updatedRoomCriteria = RoomCriteria.createWithDefaults(adults = 1, children = 0)
    }

    @Test
    fun `WHEN room has been added THEN do an availability check`() {

        every { repository.getAmendedReservation(originalReservation.bookingReference) } returns Observable.just(amendedReservation)
        every { sut.addRoomUpdateWithAvailabilityCheck(amendedReservation, updatedRoomCriteria, listOfBookingRooms,
            Locale.UK, "PI", "FLEXRATE", emptyList()
        ) } returns
                Observable.just(AddRoomUseCase.AddARoomState.AvailabilityUpdated(mockSuccessfulHotelAvailability()
                    .convertToRatePlansOperaForAmend(listOfBookingRooms), amendedReservation))
        every { graphQLHotelDetailsRepository.getHotelAvailability(any(), any())} returns
                Single.just(mockSuccessfulHotelAvailability())

        sut.addRoomUpdateWithAvailabilityCheck(amendedReservation, updatedRoomCriteria, listOfBookingRooms,
            Locale.UK, "PI", "FLEXRATE", emptyList()
        ).test()

        verify { graphQLHotelDetailsRepository.getHotelAvailability(any(), any())}
    }

    @Test
    fun `WHEN availability check returns availability THEN AddARoomState Availability Updated is returned`() {
        every { repository.getAmendedReservation(originalReservation.bookingReference) } returns Observable.just(amendedReservation)
        every { sut.addRoomUpdateWithAvailabilityCheck(amendedReservation, updatedRoomCriteria, listOfBookingRooms, Locale.UK,
            "PI", "FLEXRATE", emptyList()
        ) } returns
                Observable.just(AddRoomUseCase.AddARoomState.AvailabilityUpdated(mockSuccessfulHotelAvailability()
                    .convertToRatePlansOperaForAmend(listOfBookingRooms), amendedReservation))
        every { graphQLHotelDetailsRepository.getHotelAvailability(any(), any())} returns
                Single.just(mockSuccessfulHotelAvailability())

        sut.addRoomUpdateWithAvailabilityCheck(amendedReservation,  updatedRoomCriteria, listOfBookingRooms, Locale.UK,
            "PI", "FLEXRATE", emptyList()
        ).test()
            .assertValueAt(0, AddRoomUseCase.AddARoomState.AvailabilityUpdated(mockSuccessfulHotelAvailability()
                .convertToRatePlansOperaForAmend(listOfBookingRooms), amendedReservation))
    }

    @Test
    fun `WHEN availability check returns no availability THEN no availability state is returned`() {
        every { repository.getAmendedReservation(originalReservation.bookingReference) } returns Observable.just(amendedReservation)
        every { sut.addRoomUpdateWithAvailabilityCheck(amendedReservation,  updatedRoomCriteria, listOfBookingRooms, Locale.UK,
            "PI", "FLEXRATE", emptyList()
        ) } returns
                Observable.just(AddRoomUseCase.AddARoomState.NoAvailability("NO Availability"))
        every { graphQLHotelDetailsRepository.getHotelAvailability(any(), any())} returns
                Single.just(mockErrorHotelNoAvailabilityOpera())

        sut.addRoomUpdateWithAvailabilityCheck(amendedReservation,  updatedRoomCriteria, listOfBookingRooms, Locale.UK,
            "PI", "FLEXRATE", emptyList()
        ).test()
            .assertValueAt(0, AddRoomUseCase.AddARoomState.NoAvailability("NO Availability"))
    }

    @Test
    fun `WHEN availability check returns no availability THEN error state is returned`() {
        every { repository.getAmendedReservation(originalReservation.bookingReference) } returns Observable.just(amendedReservation)
        every { sut.addRoomUpdateWithAvailabilityCheck(amendedReservation,  updatedRoomCriteria, listOfBookingRooms, Locale.UK,
            "PI", "FLEXRATE", emptyList()
        ) } returns
                Observable.just(AddRoomUseCase.AddARoomState.AvailabilityUpdated(mockSuccessfulHotelAvailability()
                    .convertToRatePlansOperaForAmend(listOfBookingRooms), amendedReservation))
        every { graphQLHotelDetailsRepository.getHotelAvailability(any(), any())} returns
                Single.just(mockErrorHotelAvailabilityOpera())

        sut.addRoomUpdateWithAvailabilityCheck(amendedReservation,  updatedRoomCriteria, listOfBookingRooms, Locale.UK,
            "PI", "FLEXRATE", emptyList()
        ).test()
            .assertValueAt(0, AddRoomUseCase.AddARoomState.Error())

    }

    companion object {
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
                roomsBreakdown = listOf(ROOM_BREAKDOWN, ROOM_BREAKDOWN.copy(roomId = roomCriteria1.roomId))
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
                roomsBreakdown = listOf(ROOM_BREAKDOWN)
        )
    }
}