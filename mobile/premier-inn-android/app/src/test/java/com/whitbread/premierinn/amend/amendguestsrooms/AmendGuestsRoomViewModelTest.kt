package com.whitbread.premierinn.amend.amendguestsrooms

import androidx.lifecycle.SavedStateHandle
import com.whitbread.premierinn.amend.AmendStringProvider
import com.whitbread.premierinn.amend.amendcalendar.IS_PROMO_BOOKING
import com.whitbread.premierinn.amend.amendguestsrooms.AmendAddRoomActivity.Companion.ADDED_ROOM_INDEX_KEY
import com.whitbread.premierinn.amend.amendguestsrooms.AmendAddRoomActivity.Companion.HOTEL_BRAND
import com.whitbread.premierinn.amend.amendguestsrooms.AmendAddRoomActivity.Companion.ROOM_INDEX_KEY
import com.whitbread.premierinn.amend.amendguestsrooms.AmendAddRoomActivity.Companion.SELECTED_RATE_PLAN
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.managebooking.ManageBookingInput
import com.whitbread.premierinn.common.model.RoomBreakdownFixture
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.BOOKING_CHANNEL_MOBILE
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.Guest
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.common.Upsell
import com.whitbread.premierinn.domain.graphql.amend.usecase.GraphQLAmendUseCase
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import com.whitbread.premierinn.domain.reservation.usecase.AddRoomAndUpsellToStorageUseCase
import com.whitbread.premierinn.domain.reservation.usecase.AddRoomUseCase
import com.whitbread.premierinn.domain.reservation.usecase.ObserveAmendedReservationUseCase
import com.whitbread.premierinn.domain.reservation.usecase.ObserveBookingUseCase
import com.whitbread.premierinn.domain.reservation.usecase.StoreUpdatedAmendedReservation
import com.whitbread.premierinn.domain.reservation.usecase.StoreUpdatedAmendedRoom
import com.whitbread.premierinn.utils.RxJavaTestRule
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Observable
import io.reactivex.subjects.SingleSubject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalDate
import java.util.Locale
import kotlin.test.assertEquals

private val ROOM_BREAKDOWN = RoomBreakdownFixture.aRoomBreakdown()

class AmendGuestsRoomViewModelTest {

    @get:Rule
    val rxRule = RxJavaTestRule()

    private val RESERVATION_ID = "BER32423423"

    private val stringProvider = mockk<AmendStringProvider> {
        every { getCheckAvailabilityText } returns "Check availability"
        every { updateButtonText } returns "Update"
        every { updateErrorMessage } returns "Update Error Message"
        every { getNoMoreAvailabilityText(any()) } returns "No availability message"
        every { titlesList } returns listOf("Mr", "Mrs", "Miss")
    }

    private val observeAmendedReservationUseCase: ObserveAmendedReservationUseCase = mockk()
    private val storeUpdatedAmendedRoom: StoreUpdatedAmendedRoom = mockk()
    private val storeUpdatedAmendedReservation: StoreUpdatedAmendedReservation = mockk()
    private val observeBookingUseCase: ObserveBookingUseCase = mockk()
    private val addRoomUseCase: AddRoomUseCase = mockk()
    private val addRoomAndUpsellToStorageUseCase: AddRoomAndUpsellToStorageUseCase = mockk()
    private val  graphQLAmendUseCase: GraphQLAmendUseCase = mockk()
    private val storage: SimplePersistenceManagerImpl = mockk()
    private val deviceLocaleProvider: DeviceLocaleProvider = mockk()
    private val crashlyticsLogger: LogService = mockk()
    private val trackingAnalytics: TrackingAnalytics = mockk()
    private lateinit var standardViewModel: AmendGuestsRoomViewModel
    private lateinit var standardViewModelWithPromo: AmendGuestsRoomViewModel
    private lateinit var addedRoomViewModel: AmendGuestsRoomViewModel
    private lateinit var standardViewModelBusiness: AmendGuestsRoomViewModel
    private val savedStateHandleMock: SavedStateHandle = mockk()
    private val publishBookingDetails = SingleSubject.create<Booking>()
    private val inputLeisure: ManageBookingInput = ManageBookingInput.builder()
        .hotelCode("LONBLA")
        .surname("SomeName")
        .arrivalDate(LocalDate.now())
        .departureDate(LocalDate.now().plusDays(2))
        .bookingReference(RESERVATION_ID)
        .selectedRatePlan("FLEXRATE")
        .amendable(true)
        .cancellable(true)
        .isEmployeeBooking(false)
        .isBusinessBooking(false)
        .dinnerAllowance(0f)
        .build()

    private val inputBusiness: ManageBookingInput = ManageBookingInput.builder()
        .hotelCode("LONBLA")
        .surname("SomeName")
        .arrivalDate(LocalDate.now())
        .departureDate(LocalDate.now().plusDays(2))
        .bookingReference(RESERVATION_ID)
        .selectedRatePlan("FLEXRATE")
        .amendable(true)
        .cancellable(true)
        .isEmployeeBooking(false)
        .isBusinessBooking(true)
        .dinnerAllowance(0f)
        .build()

    @Before
    fun setup() {
        every { observeAmendedReservationUseCase(RESERVATION_ID) } returns Observable.just(baseReservation)
        every { observeBookingUseCase.invoke(RESERVATION_ID) } returns publishBookingDetails.toObservable()
        every { deviceLocaleProvider.getDeviceLocale() } returns Locale.UK
        every { deviceLocaleProvider.getBookingChannel() } returns BOOKING_CHANNEL_MOBILE

        every { savedStateHandleMock.get<String>(HOTEL_BRAND) } returns "PI"
        every { savedStateHandleMock.get<String>(SELECTED_RATE_PLAN) } returns "FLEX"
        every { savedStateHandleMock.get<Int>(ROOM_INDEX_KEY) } returns 1
        every { savedStateHandleMock.get<Int>(ADDED_ROOM_INDEX_KEY) } returns -1
        every { savedStateHandleMock.get<Boolean>(IS_PROMO_BOOKING) } returns false
        mockSavedStateHandle()

        standardViewModel = AmendGuestsRoomViewModel(savedStateHandleMock, stringProvider, observeAmendedReservationUseCase,
                observeBookingUseCase, crashlyticsLogger, storeUpdatedAmendedRoom, storeUpdatedAmendedReservation, addRoomUseCase,
                addRoomAndUpsellToStorageUseCase, graphQLAmendUseCase, storage, deviceLocaleProvider, trackingAnalytics)

        standardViewModelWithPromo = AmendGuestsRoomViewModel(savedStateHandleMock, stringProvider, observeAmendedReservationUseCase, observeBookingUseCase, crashlyticsLogger, storeUpdatedAmendedRoom,
            storeUpdatedAmendedReservation, addRoomUseCase,addRoomAndUpsellToStorageUseCase, graphQLAmendUseCase, storage,
            deviceLocaleProvider, trackingAnalytics)

        addedRoomViewModel = AmendGuestsRoomViewModel(savedStateHandleMock, stringProvider, observeAmendedReservationUseCase,
                observeBookingUseCase, crashlyticsLogger, storeUpdatedAmendedRoom, storeUpdatedAmendedReservation, addRoomUseCase,
                addRoomAndUpsellToStorageUseCase, graphQLAmendUseCase, storage, deviceLocaleProvider, trackingAnalytics)

        standardViewModelBusiness = AmendGuestsRoomViewModel(savedStateHandleMock, stringProvider, observeAmendedReservationUseCase,
            observeBookingUseCase, crashlyticsLogger, storeUpdatedAmendedRoom, storeUpdatedAmendedReservation, addRoomUseCase,
            addRoomAndUpsellToStorageUseCase, graphQLAmendUseCase, storage, deviceLocaleProvider, trackingAnalytics)
    }

    //TODO Flaky test need to be fixed
//    @Test
//    fun `WHEN roomcriteria has additional room THEN button will show the 'Check availability' text`() {
//        addedRoomViewModel.onUpdateLeadGuest(false)
//        baseRoomCriteria.copy(roomType = RoomType.SINGLE).let {
//            addedRoomViewModel.onCriteriaChanged(it)
//        }
//
//        assertEquals(expected = "Check availability", actual = addedRoomViewModel.currentState().buttonText)
//    }

    @Test
    fun `WHEN room type selected is different from original room type THEN the button will show the 'Check availability' text`() {
        baseRoomCriteria.copy(roomType = RoomType.SINGLE).let {
            standardViewModel.onCriteriaChanged(it)
        }

        assertEquals(expected = "Check availability", actual = standardViewModel.currentState().buttonText)
    }

    @Test
    fun `WHEN room type selected is different from original room type THEN the button will show the 'Check availability' text business`() {
        mockSavedStateHandle(true)
        baseRoomCriteria.copy(roomType = RoomType.SINGLE).let {
            standardViewModelBusiness.onCriteriaChanged(it)
        }

        assertEquals(expected = "Check availability", actual = standardViewModelBusiness.currentState().buttonText)
    }

    @Test
    fun `WHEN it is promoBooking then on criteria changed shows Update availability text`() {
        baseRoomCriteria.let {
            standardViewModelWithPromo.onCriteriaChanged(it)
        }

        assertEquals(expected = "Update", actual = standardViewModelWithPromo.currentState().buttonText)
    }

    @Test
    fun `WHEN adults is updated BUT room type remains the same THEN the button will show the 'Check availability' text`() {
        baseRoomCriteria.copy(numberOfAdults = 2).let {
            standardViewModel.onCriteriaChanged(it)
        }

        assertEquals(expected = "Check availability", actual = standardViewModel.currentState().buttonText)
    }

    @Test
    fun `WHEN adults is updated BUT room type remains the same THEN the button will show the 'Check availability' text business`() {
        mockSavedStateHandle(true)
        baseRoomCriteria.copy(numberOfAdults = 2).let {
            standardViewModelBusiness.onCriteriaChanged(it)
        }

        assertEquals(expected = "Check availability", actual = standardViewModelBusiness.currentState().buttonText)
    }

    @Test
    fun `WHEN a cot is added THEN the button shows the 'Update' text`() {
        baseRoomCriteria.copy(includeCot = true).let {
            standardViewModel.onCriteriaChanged(it)
        }

        assertEquals(expected = "Update", actual = standardViewModel.currentState().buttonText)
    }

    @Test
    fun `WHEN a cot is added THEN the button shows the 'Update' text business`() {
        mockSavedStateHandle(true)
        baseRoomCriteria.copy(includeCot = true).let {
            standardViewModelBusiness.onCriteriaChanged(it)
        }

        assertEquals(expected = "Update", actual = standardViewModelBusiness.currentState().buttonText)
    }

    @Test
    fun `WHEN an update is stored AND the update request is successful THEN the update success state is set`() {
        every {
            storeUpdatedAmendedRoom(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns Observable.just(StoreUpdatedAmendedReservation.UpdateState.Updated)
        baseRoomCriteria.copy(numberOfAdults = 2).let {
            standardViewModel.onSubmitButtonPressed(it, NameModel("Mr", "Mark", "O'Meara"),
                EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN)
        }
        assertEquals(expected = true, actual = standardViewModel.currentState().updateCompleted)
    }

    @Test
    fun `WHEN an update is stored AND the update request is successful THEN the update success state is set business`() {
        mockSavedStateHandle(true)
        every {
            storeUpdatedAmendedRoom(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns Observable.just(StoreUpdatedAmendedReservation.UpdateState.Updated)
        baseRoomCriteria.copy(numberOfAdults = 2).let {
            standardViewModelBusiness.onSubmitButtonPressed(it, NameModel("Mr", "Mark", "O'Meara"),
                EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN)
        }
        assertEquals(expected = true, actual = standardViewModelBusiness.currentState().updateCompleted)
    }

    @Test
    fun `WHEN an update is stored AND the update returns an error THEN an error message is shfown`() {
        every {
            storeUpdatedAmendedRoom(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns Observable.just(StoreUpdatedAmendedReservation.UpdateState.Error())
        baseRoomCriteria.copy(numberOfAdults = 2).let {
            standardViewModel.onSubmitButtonPressed(it, NameModel("Mr", "Mark", "O'Meara"),
                EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN)
        }
        assertEquals(expected = "Update Error Message", actual = standardViewModel.currentState().errorMessage)
    }

    @Test
    fun `WHEN an update is stored AND the update returns an error THEN an error message is shown business`() {
        mockSavedStateHandle(true)
        every {
            storeUpdatedAmendedRoom(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns Observable.just(StoreUpdatedAmendedReservation.UpdateState.Error())
        baseRoomCriteria.copy(numberOfAdults = 2).let {
            standardViewModelBusiness.onSubmitButtonPressed(it, NameModel("Mr", "Mark", "O'Meara"),
                EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN)
        }
        assertEquals(expected = "Update Error Message", actual = standardViewModelBusiness.currentState().errorMessage)
    }

    @Test
    fun `WHEN an update is stored AND the update returns a No Availability error then the No Availability message is shown`() {
        every {
            storeUpdatedAmendedRoom(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns Observable.just(StoreUpdatedAmendedReservation.UpdateState.NoAvailability(RoomType.DOUBLE))
        baseRoomCriteria.copy(numberOfAdults = 2).let {
            standardViewModel.onSubmitButtonPressed(it, NameModel("Mr", "Mark", "O'Meara"),
                EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN)
        }
        assertEquals(expected = "No availability message", actual = standardViewModel.currentState().errorMessage)
    }

    @Test
    fun `WHEN an update is stored AND the update returns a No Availability error then the No Availability message is shown business`() {
        mockSavedStateHandle(true)
        every {
            storeUpdatedAmendedRoom(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns Observable.just(StoreUpdatedAmendedReservation.UpdateState.NoAvailability(RoomType.DOUBLE))
        baseRoomCriteria.copy(numberOfAdults = 2).let {
            standardViewModelBusiness.onSubmitButtonPressed(it, NameModel("Mr", "Mark", "O'Meara"),
                EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN)
        }
        assertEquals(expected = "No availability message", actual = standardViewModelBusiness.currentState().errorMessage)
    }

    @Test
    fun `WHEN an update is stored AND the loading state is received then the button loading state is set`() {
        every {
            storeUpdatedAmendedRoom(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns Observable.just(StoreUpdatedAmendedReservation.UpdateState.Loading)
        baseRoomCriteria.copy(numberOfAdults = 2).let {
            standardViewModel.onSubmitButtonPressed(it, NameModel("Mr", "Mark", "O'Meara"),
                EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN)
        }
        assertEquals(expected = true, actual = standardViewModel.currentState().buttonLoading)
    }

    @Test
    fun `WHEN an update is stored AND the loading state is received then the button loading state is set business`() {
        mockSavedStateHandle(true)
        every {
            storeUpdatedAmendedRoom(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns Observable.just(StoreUpdatedAmendedReservation.UpdateState.Loading)
        baseRoomCriteria.copy(numberOfAdults = 2).let {
            standardViewModelBusiness.onSubmitButtonPressed(it, NameModel("Mr", "Mark", "O'Meara"),
                EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN)
        }
        assertEquals(expected = true, actual = standardViewModelBusiness.currentState().buttonLoading)
    }

    @Test
    fun `WHEN a room is added AND the update returns a No Availability error then the NoAvailability message is shown`() {
        every {
            storeUpdatedAmendedRoom(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns Observable.just(StoreUpdatedAmendedReservation.UpdateState.Error())

        baseRoomCriteria.copy(numberOfAdults = 2).let {
            addedRoomViewModel.onSubmitButtonPressed(it, NameModel("Mr", "Mark", "O'Meara"),
                EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN, EMPTY_STRING_DOMAIN)
        }
        assertEquals(expected = "Update Error Message", actual = addedRoomViewModel.currentState().errorMessage)
    }

    fun mockSavedStateHandle(isBusiness: Boolean = false) {
        if(isBusiness) {
            every { savedStateHandleMock.get<ManageBookingInput>(EXTRA_AMEND_INPUT) } returns inputBusiness
        } else {
            every { savedStateHandleMock.get<ManageBookingInput>(EXTRA_AMEND_INPUT) } returns inputLeisure
        }
    }

    companion object TestData {
        val baseRoomCriteria = RoomCriteria(numberOfAdults = 1,
                numberOfChildren = 0,
                numberOfInfants = 0,
                includeCot = false,
                roomType = RoomType.DOUBLE,
                roomNumber = 1)

        val baseGuest = Guest(title = "Mr",
                firstName = "Mark",
                lastName = "O'Meara",
                guestHistoryNumber = null,
                address = Address(line1 = "120 Holborn"),
                emailAddress = null,
                phoneNumber = null
        )

        val baseUpsell = Upsell(
                roomId = "1",
                quantity = 1,
                category = Upsell.Category.BREAKFAST,
                legend = "Meal Deal",
                postingDate = LocalDate.MIN,
                unitCost = PriceDomain(28.50.toFloat(), "GBP"),
                code = "17"
        )

        val baseReservation = Reservation(bookingReference = "BER32423423",
                arrival = LocalDate.of(2020, 11, 11),
                departure = LocalDate.of(2020, 11, 12),
                hotelCode = "LONBLA",
                roomsCriteria = listOf(baseRoomCriteria),
                roomsLeadGuest = listOf(baseGuest),
                cancelable = true,
                upsells = listOf(baseUpsell),
                roomsBreakdown = listOf(ROOM_BREAKDOWN)
        )
    }
}