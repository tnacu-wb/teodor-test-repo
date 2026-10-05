package com.whitbread.premierinn.amend

import androidx.lifecycle.SavedStateHandle
import com.whitbread.premierinn.amend.NonAmendableReservationActivity.Companion.EXTRA_HOTEL_NAME
import com.whitbread.premierinn.amend.NonAmendableReservationActivity.Companion.EXTRA_NON_AMENDABLE_INPUT
import com.whitbread.premierinn.amend.NonAmendableReservationActivity.Companion.NIGHTS_COUNT
import com.whitbread.premierinn.amend.NonAmendableReservationActivity.Companion.ROOM_CRITERIA_SIZE
import com.whitbread.premierinn.amend.analytics.CancelBookingAnalyticsData
import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.managebooking.ManageBookingInput
import com.whitbread.premierinn.data.common.LANGUAGE_ENGLISH
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.common.AmendRestrictions
import com.whitbread.premierinn.domain.graphql.bookingDetails.entity.CancelReservationDomain
import com.whitbread.premierinn.domain.graphql.bookingDetails.usecase.GraphQLBookingDetailsUseCase
import com.whitbread.premierinn.utils.RxJavaTestRule
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import io.reactivex.subjects.SingleSubject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalDate
import java.util.Locale

class NonAmendableReservationViewModelTest {

    @get:Rule
    val rxRule = RxJavaTestRule()

    private val publishBookingDetails = SingleSubject.create<Booking>()
    private val storageMock: SimplePersistenceManager = mockk()

    private val manageBookingInputLeisure: ManageBookingInput = ManageBookingInput.builder()
            .hotelCode("LONMON")
            .surname("SomeName")
            .arrivalDate(LocalDate.now())
            .departureDate(LocalDate.now().plusDays(2))
            .bookingReference("123")
            .amendable(false)
            .cancellable(true)
            .isEmployeeBooking(false)
            .isBusinessBooking(false)
            .dinnerAllowance(0f)
            .build()

    private val manageBookingInputBusiness = ManageBookingInput.builder()
        .hotelCode("LONMON")
        .surname("SomeName")
        .arrivalDate(LocalDate.now())
        .departureDate(LocalDate.now().plusDays(2))
        .bookingReference("123")
        .amendable(false)
        .cancellable(true)
        .isEmployeeBooking(false)
        .isBusinessBooking(true)
        .dinnerAllowance(0f)
        .build()

    private val graphQLBookingDetailsUseCase: GraphQLBookingDetailsUseCase = mockk()
    private val getDeviceLocaleProvider: DeviceLocaleProvider = mockk()

    private lateinit var viewmodelLeisure : NonAmendableReservationViewModel
    private lateinit var viewModelBusiness : NonAmendableReservationViewModel
    private val savedStateHandleMock: SavedStateHandle = mockk()
    private val trackAnalyticsMock: TrackingAnalytics = mockk(relaxed = true)

    @Before
    fun setUp() {
        every { getDeviceLocaleProvider.getDeviceLocale() } returns Locale.UK
        every { getDeviceLocaleProvider.getDeviceLanguage() } returns LANGUAGE_ENGLISH
        every {
            trackAnalyticsMock.trackAction(
                AnalyticsConstants.Action.CANCEL_BOOKING,
                CancelBookingAnalyticsData(
                    isCancelled = true,
                    bookingRef = "123",
                    cancelNights = "1",
                    cancelRooms = "1"
                )
            )
        } just Runs
        every { graphQLBookingDetailsUseCase
            .bookingConfirmationAndManageBooking(any(), any(), any(), any(), any(), any()) } returns publishBookingDetails.toObservable()

        every { savedStateHandleMock.get<Int>(NIGHTS_COUNT) } returns -1
        every { savedStateHandleMock.get<Int>(ROOM_CRITERIA_SIZE) } returns -1
        every { savedStateHandleMock.get<String>(UUID_BASKET_REFERENCE) } returns "16635-72322asd32-777sggsg"
        every { savedStateHandleMock.get<String>(TOKEN) } returns "someToken"
        every { savedStateHandleMock.get<String>(EXTRA_HOTEL_NAME) } returns "Manchester old trafford"

        mockSavedStateHandle()

        viewmodelLeisure = NonAmendableReservationViewModel(
            savedStateHandleMock,
            graphQLBookingDetailsUseCase,
            getDeviceLocaleProvider,
            storageMock,
            trackAnalyticsMock
        )

        viewModelBusiness = NonAmendableReservationViewModel(
            savedStateHandleMock,
            graphQLBookingDetailsUseCase,
            getDeviceLocaleProvider,
            storageMock,
            trackAnalyticsMock
        )
    }

    @Test
    fun `should get booking Opera hotel details successfully`() {
        val testObserver = viewmodelLeisure.states().test()
        publishBookingDetails.onSuccess(getBookingDetails())
        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueAt(1) { it.reservation is AsyncResult.Success }
            .assertValueCount(2)
            .assertNoErrors()
    }

    @Test
    fun `when cancel booking confirmed on Opera hotel invoke cancel booking`() {
        every { graphQLBookingDetailsUseCase.cancelReservation(any(), any()) } returns Single.just(cancelBookingResponse)
        viewmodelLeisure.onCancelBookingConfirmed()
        verify { graphQLBookingDetailsUseCase.cancelReservation(any(), any())
        }
    }

    @Test
    fun `should get booking Opera hotel details successfully for business booking`() {
        mockSavedStateHandle(true)
        val testObserver = viewModelBusiness.states().test()
        publishBookingDetails.onSuccess(getBookingDetails())
        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueAt(1) { it.reservation is AsyncResult.Success }
            .assertValueCount(2)
            .assertNoErrors()
    }

    @Test
    fun `when cancel booking confirmed on Opera hotel invoke cancel booking for business booking`() {
        mockSavedStateHandle(true)
        every { graphQLBookingDetailsUseCase.cancelReservation(any(), any()) } returns Single.just(cancelBookingResponse)
        viewModelBusiness.onCancelBookingConfirmed()
        verify { graphQLBookingDetailsUseCase.cancelReservation(any(), any()) }
    }

    fun mockSavedStateHandle(isBusiness: Boolean = false) {
        if(isBusiness) {
            every { savedStateHandleMock.get<ManageBookingInput>(EXTRA_NON_AMENDABLE_INPUT) } returns manageBookingInputBusiness
        } else {
            every { savedStateHandleMock.get<ManageBookingInput>(EXTRA_NON_AMENDABLE_INPUT) } returns manageBookingInputLeisure
        }
    }

    private fun getBookingDetails(): Booking = Booking("BAP9999", "Surname", LocalDate.now(), LocalDate.now(), "LONMON",
            "Elgin Hotel", 1, 1, "Mr First Surname", "FLEX", null, null,
        amendable = true,
        cancellable = true,
        isCancelled = false,
        isLinkedToAccount = false,
        cardFeeApplies = false,
        details = null, amendRestrictions = AmendRestrictions.createWithDefaults())

    private val cancelBookingResponse = CancelReservationDomain(
        "BAP9999"
    )
}