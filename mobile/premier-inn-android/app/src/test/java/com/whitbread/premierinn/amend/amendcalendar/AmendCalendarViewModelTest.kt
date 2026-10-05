package com.whitbread.premierinn.amend.amendcalendar

import androidx.lifecycle.SavedStateHandle
import com.whitbread.premierinn.amend.AmendStringProvider
import com.whitbread.premierinn.amend.amendguestsrooms.EXTRA_AMEND_INPUT
import com.whitbread.premierinn.api.response.InstanceFactory
import com.whitbread.premierinn.common.analytics.AnalyticsData
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.managebooking.ManageBookingInput
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.graphql.mapper.mapToHotelAvailabilityGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToPromotionsInformationDomain
import com.whitbread.premierinn.data.remote.graphql.contracts.HotelAvailabilityGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.PromotionsInformationGraphQLContract
import com.whitbread.premierinn.domain.common.BOOKING_CHANNEL_MOBILE
import com.whitbread.premierinn.domain.graphql.amend.usecase.GraphQLAmendUseCase
import com.whitbread.premierinn.domain.graphql.hdp.entity.HotelAvailabilityDomain
import com.whitbread.premierinn.domain.graphql.promotions.entity.PromotionsInformationDomain
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import com.whitbread.premierinn.domain.reservation.usecase.ObserveAmendedReservationUseCase
import com.whitbread.premierinn.utils.TestSchedulerRule
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.Period
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

class AmendCalendarViewModelTest {
    @get:Rule
    val rxRule = TestSchedulerRule()

    private val publishReservationUpdates = PublishSubject.create<Reservation>()

    private val inputLeisure = ManageBookingInput.builder()
        .hotelCode("LONMON")
        .surname("SomeName")
        .arrivalDate(originalArrival)
        .departureDate(originalDeparture)
        .bookingReference(referenceNumber)
        .amendable(true)
        .cancellable(true)
        .isEmployeeBooking(false)
        .isBusinessBooking(false)
        .dinnerAllowance(0f)
        .build()

    private val inputBusiness = ManageBookingInput.builder()
        .hotelCode("LONMON")
        .surname("SomeName")
        .arrivalDate(originalArrival)
        .departureDate(originalDeparture)
        .bookingReference(referenceNumber)
        .amendable(true)
        .cancellable(true)
        .isEmployeeBooking(false)
        .isBusinessBooking(true)
        .dinnerAllowance(0f)
        .build()

    private val observeAmendedReservationUpdatesUseCase: ObserveAmendedReservationUseCase = mockk()
    private val stringResourceProvider: AmendStringProvider = mockk()
    private val graphQLAmendUseCase: GraphQLAmendUseCase = mockk()
    private val deviceLocaleProvider: DeviceLocaleProvider = mockk()
    private val crashlyticsLogger: LogService = mockk()
    private val trackingAnalytics: TrackingAnalytics = mockk()
    private val savedStateHandleMock = mockk<SavedStateHandle>()
    private lateinit var successfulOperaAvailability: HotelAvailabilityDomain
    private lateinit var promotionInfoDomain: PromotionsInformationDomain

    @Before
    fun setUp() {
        every { observeAmendedReservationUpdatesUseCase(any()) } returns publishReservationUpdates
        every { deviceLocaleProvider.getDeviceLocale() } returns Locale.UK
        every { deviceLocaleProvider.getBookingChannel() } returns BOOKING_CHANNEL_MOBILE
        successfulOperaAvailability =
            InstanceFactory.create<HotelAvailabilityGraphQLContract.HotelAvailabilityData>(
                HotelAvailabilityGraphQLContract.HotelAvailabilityData::class.java,
                "apiTest/graphql/hotel_availability_and_rates_info_and_roomtypes_gql.json"
            ).mapToHotelAvailabilityGQL()
        promotionInfoDomain = InstanceFactory.create<PromotionsInformationGraphQLContract.PromotionsInformationData>(
            PromotionsInformationGraphQLContract.PromotionsInformationData::class.java,
            "apiTest/graphql/amend_promotions_information_gql.json"
        ).mapToPromotionsInformationDomain()
        mockSavedStateHandle()
    }

    @Test
    fun `when restricted THEN return restricted nights for leisure booking`() {
        val viewModel = AmendCalendarViewModel(
            savedStateHandleMock, observeAmendedReservationUpdatesUseCase,
            stringResourceProvider, graphQLAmendUseCase, deviceLocaleProvider, crashlyticsLogger,
            trackingAnalytics
        )
        assertEquals(
            viewModel.getRestrictedNights(originalArrival, originalDeparture, true),
            restrictedNights
        )
    }

    @Test
    fun `when restricted THEN return restricted nights for business booking`() {
        mockSavedStateHandle(true)
        val viewModel = AmendCalendarViewModel(
            savedStateHandleMock, observeAmendedReservationUpdatesUseCase,
            stringResourceProvider, graphQLAmendUseCase, deviceLocaleProvider, crashlyticsLogger,
            trackingAnalytics
        )
        assertEquals(
            viewModel.getRestrictedNights(originalArrival, originalDeparture, true),
            restrictedNights
        )
    }

    @Test
    fun `when not restricted THEN return no restricted nights for leisure booking`() {
        val viewModel = AmendCalendarViewModel(
            savedStateHandleMock, observeAmendedReservationUpdatesUseCase,
            stringResourceProvider, graphQLAmendUseCase, deviceLocaleProvider, crashlyticsLogger,
            trackingAnalytics
        )
        assertEquals(
            viewModel.getRestrictedNights(originalArrival, originalDeparture, false),
            noRestrictedNights
        )
    }

    @Test
    fun `when isPromoBooking is true THEN promo info call is made before changing dates`() {
        every { deviceLocaleProvider.getDeviceLanguage() } returns "en"
        every {
            graphQLAmendUseCase.getPromotionInformation(
                any(), any(), any(), any(),
                any(), any(), any())
        } returns Observable.just(promotionInfoDomain)
        every {
            graphQLAmendUseCase.changeBookingDates(any())
        } returns Single.just(mockk(relaxed = true))

        val viewModel = AmendCalendarViewModel(
            savedStateHandleMock, observeAmendedReservationUpdatesUseCase,
            stringResourceProvider, graphQLAmendUseCase, deviceLocaleProvider, crashlyticsLogger,
            trackingAnalytics
        )

        val updatedDates = Pair(originalArrival.plusDays(1), originalDeparture.plusDays(1))

        viewModel.makePromoInfoCallAndChangeDates(
            token = "token123",
            tempBasketRef = "tempbasket",
            updatedDates = updatedDates,
            isBusinessBooking = false,
            isPromoBooking = true,
            originalBasketReference = "basketRef123",
            brand = "PI"
        )

        verify {
            graphQLAmendUseCase.getPromotionInformation(
                country = "gb",
                language = "en",
                channel = "PI",
                brand = "PI",
                stayStartDate = updatedDates.first.toString(),
                stayEndDate = updatedDates.second.toString(),
                basketReference = "basketRef123"
            )
        }
    }

    @Test
    fun `when isPromoBooking is false THEN changeBookingDates is called directly without promo info call`() {
        every { deviceLocaleProvider.getDeviceLanguage() } returns "en"
        every {
            graphQLAmendUseCase.changeBookingDates(any())
        } returns Single.just(mockk(relaxed = true))

        val viewModel = AmendCalendarViewModel(
            savedStateHandleMock, observeAmendedReservationUpdatesUseCase,
            stringResourceProvider, graphQLAmendUseCase, deviceLocaleProvider, crashlyticsLogger,
            trackingAnalytics
        )

        val updatedDates = Pair(originalArrival.plusDays(1), originalDeparture.plusDays(1))

        viewModel.makePromoInfoCallAndChangeDates(
            token = "token123",
            tempBasketRef = "tempbasket",
            updatedDates = updatedDates,
            isBusinessBooking = false,
            isPromoBooking = false,
            originalBasketReference = "basketRef123",
            brand = "PI"
        )

        verify(exactly = 0) {
            graphQLAmendUseCase.getPromotionInformation(any(), any(), any(), any(), any(), any(), any())
        }
        verify {
            graphQLAmendUseCase.changeBookingDates(any())
        }
    }

    @Test
    fun `when isPromoBooking is true AND isWithinPromoWindow is true THEN changeBookingDates is called`() {
        every { deviceLocaleProvider.getDeviceLanguage() } returns "en"
        every {
            graphQLAmendUseCase.getPromotionInformation(
                any(), any(), any(), any(), any(), any(), any()
            )
        } returns Observable.just(promotionInfoDomain)
        every {
            graphQLAmendUseCase.changeBookingDates(any())
        } returns Single.just(mockk(relaxed = true))

        val viewModel = AmendCalendarViewModel(
            savedStateHandleMock, observeAmendedReservationUpdatesUseCase,
            stringResourceProvider, graphQLAmendUseCase, deviceLocaleProvider, crashlyticsLogger,
            trackingAnalytics
        )

        val updatedDates = Pair(originalArrival.plusDays(1), originalDeparture.plusDays(1))

        viewModel.makePromoInfoCallAndChangeDates(
            token = "token123",
            tempBasketRef = "tempbasket",
            updatedDates = updatedDates,
            isBusinessBooking = false,
            isPromoBooking = true,
            originalBasketReference = "basketRef123",
            brand = "PI"
        )

        rxRule.testScheduler.advanceTimeBy(10, TimeUnit.SECONDS)

        verify {
            graphQLAmendUseCase.changeBookingDates(any())
        }
    }

    @Test
    fun `when isPromoBooking is true AND isWithinPromoWindow is false THEN StayDatesErrorEvent is published`() {
        val promoInfoOutsideWindow = promotionInfoDomain.copy(isWithinPromoWindow = false, appPromoAmendMessage = "Promo not available")
        every { deviceLocaleProvider.getDeviceLanguage() } returns "en"
        every {
            graphQLAmendUseCase.getPromotionInformation(
                any(), any(), any(), any(), any(), any(), any()
            )
        } returns Observable.just(promoInfoOutsideWindow)

        val viewModel = AmendCalendarViewModel(
            savedStateHandleMock, observeAmendedReservationUpdatesUseCase,
            stringResourceProvider, graphQLAmendUseCase, deviceLocaleProvider, crashlyticsLogger,
            trackingAnalytics
        )

        val testObserver = viewModel.events().test()
        val updatedDates = Pair(originalArrival.plusDays(1), originalDeparture.plusDays(1))

        viewModel.makePromoInfoCallAndChangeDates(
            token = "token123",
            tempBasketRef = "tempbasket",
            updatedDates = updatedDates,
            isBusinessBooking = false,
            isPromoBooking = true,
            originalBasketReference = "basketRef123",
            brand = "PI"
        )

        rxRule.testScheduler.advanceTimeBy(10, TimeUnit.SECONDS)

        verify(exactly = 0) {
            graphQLAmendUseCase.changeBookingDates(any())
        }
        testObserver.assertValue { event ->
            event is AmendCalendarViewModel.StayDatesErrorEvent && event.error.message == "Promo not available"
        }
    }

    @Test
    fun `when isPromoBooking is true AND isWithinPromoWindow is null THEN StayDatesErrorEvent is published with appPromoMessage message`() {
        val promoInfoNullWindow = promotionInfoDomain.copy(isWithinPromoWindow = null)
        every { deviceLocaleProvider.getDeviceLanguage() } returns "en"
        every {
            graphQLAmendUseCase.getPromotionInformation(
                any(), any(), any(), any(), any(), any(), any()
            )
        } returns Observable.just(promoInfoNullWindow)

        every { crashlyticsLogger.logException(any(), any()) } returns Unit
        every { trackingAnalytics.track(any(), any<AnalyticsData>()) } just runs

        val viewModel = AmendCalendarViewModel(
            savedStateHandleMock, observeAmendedReservationUpdatesUseCase,
            stringResourceProvider, graphQLAmendUseCase, deviceLocaleProvider, crashlyticsLogger,
            trackingAnalytics
        )

        val testObserver = viewModel.events().test()
        val updatedDates = Pair(originalArrival.plusDays(1), originalDeparture.plusDays(1))

        viewModel.makePromoInfoCallAndChangeDates(
            token = "token123",
            tempBasketRef = "tempbasket",
            updatedDates = updatedDates,
            isBusinessBooking = false,
            isPromoBooking = true,
            originalBasketReference = "basketRef123",
            brand = "PI"
        )

        verify(exactly = 0) {
            graphQLAmendUseCase.changeBookingDates(any())
        }
        rxRule.testScheduler.advanceTimeBy(10, TimeUnit.SECONDS)

        testObserver.assertValue { event ->
            event is AmendCalendarViewModel.StayDatesErrorEvent && event.error.message == promotionInfoDomain.appPromoAmendMessage
        }
    }

    @Test
    fun `when isPromoBooking is true AND isWithinPromoWindow is null and appPromoMessage is null THEN StayDatesErrorEvent is published with fallback local string`() {
        val promoInfoNullWindow = promotionInfoDomain.copy(isWithinPromoWindow = null, appPromoAmendMessage = null)
        val fallbackErrorMessage = "Something went wrong"
        every { deviceLocaleProvider.getDeviceLanguage() } returns "en"
        every { stringResourceProvider.availabilityChangeDatesErrorMessage } returns fallbackErrorMessage
        every {
            graphQLAmendUseCase.getPromotionInformation(
                any(), any(), any(), any(), any(), any(), any()
            )
        } returns Observable.just(promoInfoNullWindow)

        every { crashlyticsLogger.logException(any(), any()) } returns Unit
        every { trackingAnalytics.track(any(), any<AnalyticsData>()) } just runs

        val viewModel = AmendCalendarViewModel(
            savedStateHandleMock, observeAmendedReservationUpdatesUseCase,
            stringResourceProvider, graphQLAmendUseCase, deviceLocaleProvider, crashlyticsLogger,
            trackingAnalytics
        )

        val testObserver = viewModel.events().test()
        val updatedDates = Pair(originalArrival.plusDays(1), originalDeparture.plusDays(1))

        viewModel.makePromoInfoCallAndChangeDates(
            token = "token123",
            tempBasketRef = "tempbasket",
            updatedDates = updatedDates,
            isBusinessBooking = false,
            isPromoBooking = true,
            originalBasketReference = "basketRef123",
            brand = "PI"
        )

        verify(exactly = 0) {
            graphQLAmendUseCase.changeBookingDates(any())
        }
        rxRule.testScheduler.advanceTimeBy(10, TimeUnit.SECONDS)

        testObserver.assertValue { event ->
            event is AmendCalendarViewModel.StayDatesErrorEvent && event.error.message == fallbackErrorMessage
        }
    }

    @Test
    fun `when isPromoBooking is true AND getPromotionInformation fails THEN StayDatesErrorEvent is published and error is logged`() {
        val apiError = RuntimeException("Network error")
        val fallbackErrorMessage = "Something went wrong"
        every { deviceLocaleProvider.getDeviceLanguage() } returns "en"
        every { stringResourceProvider.availabilityChangeDatesErrorMessage } returns fallbackErrorMessage
        every { crashlyticsLogger.logException(any(), any()) } returns Unit
        every {
            graphQLAmendUseCase.getPromotionInformation(
                any(), any(), any(), any(), any(), any(), any()
            )
        } returns Observable.error(apiError)

        val viewModel = AmendCalendarViewModel(
            savedStateHandleMock, observeAmendedReservationUpdatesUseCase,
            stringResourceProvider, graphQLAmendUseCase, deviceLocaleProvider, crashlyticsLogger,
            trackingAnalytics
        )

        val testObserver = viewModel.events().test()
        val updatedDates = Pair(originalArrival.plusDays(1), originalDeparture.plusDays(1))

        viewModel.makePromoInfoCallAndChangeDates(
            token = "token123",
            tempBasketRef = "tempbasket",
            updatedDates = updatedDates,
            isBusinessBooking = false,
            isPromoBooking = true,
            originalBasketReference = "basketRef123",
            brand = "PI"
        )

        rxRule.testScheduler.advanceTimeBy(10, TimeUnit.SECONDS)

        verify(exactly = 0) {
            graphQLAmendUseCase.changeBookingDates(any())
        }
        verify {
            crashlyticsLogger.logException(apiError, "getPromotionInformation() Error")
        }
        testObserver.assertValue { event ->
            event is AmendCalendarViewModel.StayDatesErrorEvent && event.error.message == fallbackErrorMessage
        }
    }

    @Test
    fun `when not restricted THEN return no restricted nights for business booking`() {
        val viewModel = AmendCalendarViewModel(
            savedStateHandleMock, observeAmendedReservationUpdatesUseCase,
            stringResourceProvider, graphQLAmendUseCase, deviceLocaleProvider, crashlyticsLogger,
            trackingAnalytics
        )
        assertEquals(
            viewModel.getRestrictedNights(originalArrival, originalDeparture, false),
            noRestrictedNights
        )
    }

    @Test
    fun `when arrival and departure dates are null THEN return no restricted nights for leisure booking`() {
        val viewModel = AmendCalendarViewModel(
            savedStateHandleMock, observeAmendedReservationUpdatesUseCase,
            stringResourceProvider, graphQLAmendUseCase, deviceLocaleProvider, crashlyticsLogger,
            trackingAnalytics
        )
        assertEquals(viewModel.getRestrictedNights(null, null, true), noRestrictedNights)
    }

    @Test
    fun `when arrival and departure dates are null THEN return no restricted nights for business booking`() {
        val viewModel = AmendCalendarViewModel(
            savedStateHandleMock, observeAmendedReservationUpdatesUseCase,
            stringResourceProvider, graphQLAmendUseCase, deviceLocaleProvider, crashlyticsLogger,
            trackingAnalytics
        )
        assertEquals(viewModel.getRestrictedNights(null, null, true), noRestrictedNights)
    }

    fun mockSavedStateHandle(isBusiness: Boolean = false) {
        if(isBusiness) {
            every { savedStateHandleMock.get<ManageBookingInput>(EXTRA_AMEND_INPUT) } returns inputBusiness
        } else {
            every { savedStateHandleMock.get<ManageBookingInput>(EXTRA_AMEND_INPUT) } returns inputLeisure
        }
    }

    companion object {
        private const val referenceNumber = "BER2334242"
        private const val noRestrictedNights = 0L
        private val originalArrival = LocalDate.now()
        private val originalDeparture = originalArrival.plusDays(5)
        private val restrictedNights = Period.between(originalArrival, originalDeparture).days.toLong()
    }
}