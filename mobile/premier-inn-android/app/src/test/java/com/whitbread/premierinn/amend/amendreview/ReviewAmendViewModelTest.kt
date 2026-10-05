package com.whitbread.premierinn.amend.amendreview

import androidx.lifecycle.SavedStateHandle
import com.whitbread.premierinn.R
import com.whitbread.premierinn.amend.AmendStringProvider
import com.whitbread.premierinn.amend.amendAndPay.AmendAndPayActivity.Companion.AMEND_SUMMARY_DOMAIN
import com.whitbread.premierinn.amend.amendreview.ReviewAmendsActivity.Companion.EXTRA_AMEND_INPUT
import com.whitbread.premierinn.amend.amendreview.uimodel.AmendedTotal
import com.whitbread.premierinn.amend.amendreview.uimodel.BookingSummary
import com.whitbread.premierinn.amend.amendreview.uimodel.ReviewAmendTextItem
import com.whitbread.premierinn.amend.amendreview.uimodel.ReviewAmendTitleItem
import com.whitbread.premierinn.amend.analytics.AmendConfirmationData
import com.whitbread.premierinn.amend.analytics.AmendReviewData
import com.whitbread.premierinn.api.response.InstanceFactory
import com.whitbread.premierinn.common.AppConfiguration
import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.managebooking.ManageBookingInput
import com.whitbread.premierinn.common.model.BookingFixture
import com.whitbread.premierinn.common.model.GuestFixture
import com.whitbread.premierinn.common.model.ManageBookingInputFixture
import com.whitbread.premierinn.common.model.RoomBreakdownFixture
import com.whitbread.premierinn.common.model.RoomCriteriaFixture
import com.whitbread.premierinn.common.model.UpsellFixture
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.data.common.LANGUAGE_ENGLISH
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.data.graphql.mapper.mapToAmendSummaryGQL
import com.whitbread.premierinn.data.remote.graphql.contracts.AmendSummaryGraphQLContract
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.common.AmendRestrictions
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.common.Upsell
import com.whitbread.premierinn.domain.graphql.amend.entity.AmendSummaryDomain
import com.whitbread.premierinn.domain.graphql.amend.usecase.GraphQLAmendUseCase
import com.whitbread.premierinn.domain.graphql.findBooking.usecase.GraphQLFindBookingUseCase
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import com.whitbread.premierinn.domain.reservation.usecase.CompletePendingAmendUseCase
import com.whitbread.premierinn.domain.reservation.usecase.ObserveAmendedReservationUseCase
import com.whitbread.premierinn.domain.reservation.usecase.ObserveBookingUseCase
import com.whitbread.premierinn.domain.reservation.usecase.OriginalReservationUseCase
import com.whitbread.premierinn.domain.reservation.usecase.SubmitAmendedReservationUseCase
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.resource.usecase.GetLongResource
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import com.whitbread.premierinn.utils.RxJavaTestRule
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.SingleSubject
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalDate
import java.util.Locale

private val ROOM_BREAKDOWN_TEST_MODEL = RoomBreakdownFixture.aRoomBreakdown()
private val GUEST_TEST_MODEL = GuestFixture.aGuest()
private val ROOM_CRITERIA_TEST_MODEL = RoomCriteriaFixture.aRoomCriteria()
private val MANAGE_BOOKING_INPUT_TEST_MODEL = ManageBookingInputFixture.aManageBookingInput()
private val UPSELL_TEST_MODEL = UpsellFixture.aUpsell()
private val A_OPERA_BOOKING = BookingFixture.aOperaBooking()

class ReviewAmendViewModelTest {

    @get:Rule
    val rxRule = RxJavaTestRule()

    private val publishReservationUpdates = PublishSubject.create<Reservation>()
    private val publishBookingDetails = SingleSubject.create<Booking>()
    private val observeAmendedReservationUpdatesUseCaseMock: ObserveAmendedReservationUseCase = mockk()
    private val observeBookingUseCaseMock: ObserveBookingUseCase = mockk()
    private val submitUseCaseMock: SubmitAmendedReservationUseCase = mockk()
    private val completePendingAmendUseCase: CompletePendingAmendUseCase = mockk()
    private val originalReservationMock: OriginalReservationUseCase = mockk()
    private val amendStringProviderMock: AmendStringProvider = mockk()
    private val storageMock: SimplePersistenceManager = mockk()
    private val loggerMock: LogService = mockk()
    private val trackAnalyticsMock: TrackingAnalytics = mockk(relaxed = true)
    private val deviceLocaleProvider: DeviceLocaleProvider = mockk()
    private val getStringResource: GetStringResource = mockk()
    private val getLongResource: GetLongResource = mockk()
    private val amendUseCaseMock: GraphQLAmendUseCase = mockk()
    private val appConfiguration: AppConfiguration = mockk()
    private val findBookingUseCase: GraphQLFindBookingUseCase = mockk()
    private val amendSummaryDomain: AmendSummaryDomain = mockk()

    private lateinit var amendSummaryResponseDomain: AmendSummaryDomain
    private val savedStateHandleMock: SavedStateHandle = mockk()

    @InjectMockKs
    lateinit var viewModelOpera: ReviewAmendViewModel

    @Before
    fun setUp() {
        every { originalReservationMock() } returns getFakeReservation()
        every { observeAmendedReservationUpdatesUseCaseMock(any()) } returns publishReservationUpdates
        every { observeBookingUseCaseMock(any()) } returns publishBookingDetails.toObservable()
        every { deviceLocaleProvider.getDeviceLocale() } returns Locale.UK
        every { deviceLocaleProvider.getDeviceLocale().language } returns LANGUAGE_ENGLISH
        every { deviceLocaleProvider.getDeviceLocale().country } returns "gb"
        every { deviceLocaleProvider.getDeviceLanguage() } returns LANGUAGE_ENGLISH
        every { getStringResource.invoke(ContentManagedResourceRepository.Key.DONATION_PLEDGE) } returns "Online charitable pledge"
        every { observeBookingUseCaseMock.invoke(any()) } returns Observable.just(A_OPERA_BOOKING)
        every { observeBookingUseCaseMock(any()) } returns publishBookingDetails.toObservable()
        every { storageMock.getBillingAddress() } returns "120 Holborn, EC1 N2TD"
        every { savedStateHandleMock.get<ManageBookingInput>(EXTRA_AMEND_INPUT) } returns MANAGE_BOOKING_INPUT_TEST_MODEL
        every { savedStateHandleMock.get<AmendSummaryDomain>(AMEND_SUMMARY_DOMAIN) } returns amendSummaryDomain

        amendSummaryResponseDomain = InstanceFactory.create<AmendSummaryGraphQLContract.AmendSummaryData>(
            AmendSummaryGraphQLContract.AmendSummaryData::class.java, "apiTest/graphql/amend_summary_success_gql.json")
            .mapToAmendSummaryGQL()

        viewModelOpera = ReviewAmendViewModel(
            savedStateHandleMock,
            observeBookingUseCaseMock,
            observeAmendedReservationUpdatesUseCaseMock,
            originalReservationMock,
            amendStringProviderMock,
            storageMock,
            submitUseCaseMock,
            completePendingAmendUseCase,
            loggerMock,
            trackAnalyticsMock,
            storageMock,
            deviceLocaleProvider,
            getStringResource,
            getLongResource,
            amendUseCaseMock,
            appConfiguration,
            findBookingUseCase
        )
    }

    @Test
    fun `should load amended reservation successfully for opera`() {
        val testObserver = viewModelOpera.states().test()

        publishBookingDetails.onSuccess(getBookingDetailsOpera())

        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueAt(1) { it.originalBooking is AsyncResult.Success }
            .assertValueCount(2)
            .assertNoErrors()
    }

    @Test
    fun `when arrival date is changed then DateChanged state is applied Opera`() {
        val testObserver = viewModelOpera.states().test()
        every { amendStringProviderMock.dateChangedTitle } returns "Dates changed"
        every { amendStringProviderMock.dateChangeDescription(any()) } returns "You can stay in the hotel for your entire life!"

        viewModelOpera.findDatesChanged(getFakeReservation().copy(arrival = LocalDate.now().plusDays(1)), getFakeReservation())

        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueAt(1) {
                it.onDateChanged == ReviewAmendViewModel.ReviewAmendState.DateChanged(
                    ReviewAmendTextItem().mapToReviewAmendTextItem(
                        title = "Dates changed",
                        description = "You can stay in the hotel for your entire life!"))
            }
            .assertValueCount(2)
            .assertNoErrors()
    }

    @Test
    fun `when departure date is changed then DateChanged state is applied Opera`() {
        val testObserver = viewModelOpera.states().test()
        every { amendStringProviderMock.dateChangedTitle } returns "Dates changed"
        every { amendStringProviderMock.dateChangeDescription(any()) } returns "You can stay in the hotel for your entire life!"

        viewModelOpera.findDatesChanged(getFakeReservation().copy(departure = LocalDate.now().plusDays(5)), getFakeReservation())

        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueAt(1) {
                it.onDateChanged == ReviewAmendViewModel.ReviewAmendState.DateChanged(
                    ReviewAmendTextItem().mapToReviewAmendTextItem(
                        title = "Dates changed",
                        description = "You can stay in the hotel for your entire life!"))
            }
            .assertValueCount(2)
            .assertNoErrors()
    }

    @Test
    fun `when both arrival and departure date are same then DateChanged state is not applied Opera`() {
        val testObserver = viewModelOpera.states().test()

        viewModelOpera.findDatesChanged(getFakeReservation(), getFakeReservation())

        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueCount(1)
            .assertNoErrors()
    }

    @Test
    fun `when both original and amended meals are empty then MealsChanged state is not applied Opera`() {
        val testObserver = viewModelOpera.states().test()

        viewModelOpera.findMealsChanged(getFakeReservation(), getFakeReservation())

        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueCount(1)
            .assertNoErrors()
    }

    @Test
    fun `when meal is added then MealsChanged state is applied Opera`() {
        val testObserver = viewModelOpera.states().test()
        every { amendStringProviderMock.mealChangedTitle } returns "Meals changed"
        every { amendStringProviderMock.mealChangeDescription(any(), any(), any()) } returns "Meal 1"
        every { amendStringProviderMock.priceChangeText(any(), any()) } returns "Price difference"

        viewModelOpera.findMealsChanged(getFakeReservation().copy(departure = LocalDate.now().plusDays(2),
            upsells = listOf(UPSELL_TEST_MODEL, UPSELL_TEST_MODEL.copy(legend = "NFC", code = "11"))), getFakeReservation())

        verify(exactly = 1) { amendStringProviderMock.mealChangeDescription("KFC", 1, 2) }
        verify(exactly = 1) { amendStringProviderMock.mealChangeDescription("NFC", 1, 2) }
        verify(exactly = 2) { amendStringProviderMock.priceChangeText("£35.96", R.string.amend_positive_price_difference) }

        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueAt(1) {
                it.onMealsChanged == ReviewAmendViewModel.ReviewAmendState.MealsChanged(
                    ReviewAmendTextItem().mapToReviewAmendTextItem(title = "Meals changed",
                        description = "Meal 1\nMeal 1",
                        priceChange = "Price difference")
                )
            }
            .assertValueCount(2)
            .assertNoErrors()
    }

    @Test
    fun `when meal is removed then MealsChanged state is applied Opera`() {
        val testObserver = viewModelOpera.states().test()
        every { amendStringProviderMock.mealChangedTitle } returns "Meals changed"
        every { amendStringProviderMock.mealChangeDescription(any(), any(), any()) } returns "List of changed meals"
        every { amendStringProviderMock.priceChangeText(any(), any()) } returns "Price difference"

        viewModelOpera.findMealsChanged(getFakeReservation(), getFakeReservation().copy(upsells = listOf(UPSELL_TEST_MODEL.copy(quantity = 2))))

        verify(exactly = 1) { amendStringProviderMock.mealChangeDescription("KFC", 2, 1) }
        verify(exactly = 2) { amendStringProviderMock.priceChangeText("£17.98", R.string.amend_negative_price_difference) }

        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueAt(1) {
                it.onMealsChanged == ReviewAmendViewModel.ReviewAmendState.MealsChanged(
                    ReviewAmendTextItem().mapToReviewAmendTextItem(title = "Meals changed",
                        description = "List of changed meals",
                        priceChange = "Price difference")
                )
            }
            .assertValueCount(2)
            .assertNoErrors()
    }


    private fun getFakeReservation(): Reservation {
        val now = LocalDate.now()
        return Reservation(
                bookingReference = "BKF1234",
                arrival = now,
                departure = now.plusDays(1),
                hotelCode = "LONMON",
                roomsCriteria = listOf(ROOM_CRITERIA_TEST_MODEL),
                roomsLeadGuest = listOf(GUEST_TEST_MODEL),
                cancelable = true,
                upsells = emptyList(),
                roomsBreakdown = listOf(ROOM_BREAKDOWN_TEST_MODEL)
        )
    }

    @Test
    fun `when meal is changed then MealsChanged state is applied Opera`() {
        val testObserver = viewModelOpera.states().test()
        every { amendStringProviderMock.mealChangedTitle } returns "Meals changed"
        every { amendStringProviderMock.mealChangeDescription(any(), any(), any()) } returns "List of changed meals"
        every { amendStringProviderMock.priceChangeText(any(), any()) } returns "Price difference"

        viewModelOpera.findMealsChanged(getFakeReservation().copy(upsells = listOf(UPSELL_TEST_MODEL.copy(
            legend = "McDonalds", code = "11", unitCost = PriceDomain(5.99f, GBP)))),
            getFakeReservation().copy(upsells = listOf(UPSELL_TEST_MODEL)))

        verify(exactly = 1) { amendStringProviderMock.mealChangeDescription("McDonalds", 1, 1) }
        verify(exactly = 2) { amendStringProviderMock.priceChangeText("£3.00", R.string.amend_negative_price_difference) }

        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueAt(1) {
                it.onMealsChanged == ReviewAmendViewModel.ReviewAmendState.MealsChanged(
                    ReviewAmendTextItem().mapToReviewAmendTextItem(title = "Meals changed",
                        description = "List of changed meals",
                        priceChange = "Price difference")
                )
            }
            .assertValueCount(2)
            .assertNoErrors()
    }

    @Test
    fun `when room type or guest details are changed then RoomChanged state is applied Opera`() {
        val testObserver = viewModelOpera.states().test()
        every { amendStringProviderMock.guestChangeText(any()) } returns "Lead guest name changed to Pi"
        every { amendStringProviderMock.roomChangedTitleText(any()) } returns "Room changed Pi"
        every { amendStringProviderMock.roomTypeChangedText(any()) } returns "Room type changed to escape room"

        viewModelOpera.findRoomChanged(getFakeReservation().copy(
            roomsCriteria = listOf(ROOM_CRITERIA_TEST_MODEL.copy(roomType = RoomType.FAMILY)),
            roomsLeadGuest = listOf(GUEST_TEST_MODEL.copy(title = "Lord", firstName = "Poke", lastName = "Mon"))),
            getFakeReservation())

        verify(exactly = 1) { amendStringProviderMock.roomChangedTitleText("Lord Poke Mon") }
        verify(exactly = 1) { amendStringProviderMock.guestChangeText("Lord Poke Mon") }
        verify(exactly = 1) { amendStringProviderMock.roomTypeChangedText(RoomType.FAMILY) }

        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueAt(1) {
                it.onRoomChanged == ReviewAmendViewModel.ReviewAmendState.RoomChanged(
                    listOf(ReviewAmendTextItem().mapToReviewAmendTextItem(title = "Room changed Pi"),
                        ReviewAmendTextItem().mapToReviewAmendTextItem(description = "Lead guest name changed to Pi"),
                        ReviewAmendTextItem().mapToReviewAmendTextItem(description = "Room type changed to escape room"))
                )
            }
            .assertValueCount(2)
            .assertNoErrors()
    }

    @Test
    fun `when guests or cot are added then RoomChanged state is applied Opera`() {
        val testObserver = viewModelOpera.states().test()
        every { amendStringProviderMock.roomChangedTitleText(any()) } returns "Room changed Pi"
        every { amendStringProviderMock.adultAddedText(any()) } returns "1 adult added"
        every { amendStringProviderMock.childrenAddedText(any()) } returns "1 child added"
        every { amendStringProviderMock.infantAddedText(any()) } returns "1 infant added"
        every { amendStringProviderMock.cotAdded } returns "Cot added"

        viewModelOpera.findRoomChanged(getFakeReservation().copy(roomsCriteria = listOf(ROOM_CRITERIA_TEST_MODEL.copy(
            numberOfAdults = 2, numberOfChildren = 1, numberOfInfants = 1, includeCot = true))),
            getFakeReservation())

        verify(exactly = 1) { amendStringProviderMock.roomChangedTitleText("Sir Joshua Onabanjo") }
        verify(exactly = 1) { amendStringProviderMock.adultAddedText(1) }
        verify(exactly = 1) { amendStringProviderMock.childrenAddedText(1) }
        verify(exactly = 1) { amendStringProviderMock.infantAddedText(1) }

        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueAt(1) {
                it.onRoomChanged == ReviewAmendViewModel.ReviewAmendState.RoomChanged(
                    listOf(ReviewAmendTextItem().mapToReviewAmendTextItem(title = "Room changed Pi"),
                        ReviewAmendTextItem().mapToReviewAmendTextItem(description = "1 adult added"),
                        ReviewAmendTextItem().mapToReviewAmendTextItem(description = "1 child added"),
                        ReviewAmendTextItem().mapToReviewAmendTextItem(description = "1 infant added"),
                        ReviewAmendTextItem().mapToReviewAmendTextItem(description = "Cot added"))
                )
            }
            .assertValueCount(2)
            .assertNoErrors()
    }

    @Test
    fun `when guests or cot are removed then RoomChanged state is applied Opera`() {
        val testObserver = viewModelOpera.states().test()
        every { amendStringProviderMock.roomChangedTitleText(any()) } returns "Room changed Pi"
        every { amendStringProviderMock.adultRemovedText(any()) } returns "1 adult removed"
        every { amendStringProviderMock.childrenRemovedText(any()) } returns "1 child removed"
        every { amendStringProviderMock.infantRemovedText(any()) } returns "1 infant removed"
        every { amendStringProviderMock.cotRemoved } returns "Cot removed"

        viewModelOpera.findRoomChanged(getFakeReservation(), getFakeReservation().copy(roomsCriteria = listOf(ROOM_CRITERIA_TEST_MODEL.copy(
            numberOfAdults = 2, numberOfChildren = 1, numberOfInfants = 1, includeCot = true))))

        verify(exactly = 1) { amendStringProviderMock.roomChangedTitleText("Sir Joshua Onabanjo") }
        verify(exactly = 1) { amendStringProviderMock.adultRemovedText(1) }
        verify(exactly = 1) { amendStringProviderMock.childrenRemovedText(1) }
        verify(exactly = 1) { amendStringProviderMock.infantRemovedText(1) }

        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueAt(1) {
                it.onRoomChanged == ReviewAmendViewModel.ReviewAmendState.RoomChanged(
                    listOf(ReviewAmendTextItem().mapToReviewAmendTextItem(title = "Room changed Pi"),
                        ReviewAmendTextItem().mapToReviewAmendTextItem(description = "1 adult removed"),
                        ReviewAmendTextItem().mapToReviewAmendTextItem(description = "1 child removed"),
                        ReviewAmendTextItem().mapToReviewAmendTextItem(description = "1 infant removed"),
                        ReviewAmendTextItem().mapToReviewAmendTextItem(description = "Cot removed"))
                )
            }
            .assertValueCount(2)
            .assertNoErrors()
    }

    @Test
    fun `when room is added then RoomChanged state is applied Opera`() {
        val testObserver = viewModelOpera.states().test()
        every { amendStringProviderMock.roomAddedText(any()) } returns "Room added Goku"
        every { amendStringProviderMock.priceChangeText(any(), any()) } returns "Price increased"
        every { amendStringProviderMock.priceChangeText(any(), any()) } returns "Price increased"

        viewModelOpera.findRoomAdded(getFakeReservation().copy(
            roomsCriteria = listOf(ROOM_CRITERIA_TEST_MODEL, ROOM_CRITERIA_TEST_MODEL.copy(roomId = "ZCR123")),
            roomsBreakdown = listOf(ROOM_BREAKDOWN_TEST_MODEL, ROOM_BREAKDOWN_TEST_MODEL.copy(roomId = "ZCR123", totalRoomCost = PriceDomain(150.00f, GBP))),
            roomsLeadGuest = listOf(GUEST_TEST_MODEL, GUEST_TEST_MODEL.copy(roomId = "ZCR123", title = "Saiyan", firstName = "Goku", lastName = "Son"))),
            getFakeReservation())

        verify(exactly = 1) { amendStringProviderMock.roomAddedText("Saiyan Goku Son") }
        verify(exactly = 1) { amendStringProviderMock.priceChangeText("£150.00", R.string.amend_positive_price_difference) }

        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueAt(1) {
                it.onRoomAdded == ReviewAmendViewModel.ReviewAmendState.RoomAdded(
                    listOf(ReviewAmendTextItem().mapToReviewAmendTextItem(
                        title = "Room added Goku", priceChange = "Price increased")
                    ))
            }
    }

    @Test
    fun `when user confirms changes in review screen Analytics is Sent Opera`() {
        every {
            submitUseCaseMock.updateBooking("BKF1234", LocalDate.now(),
                LocalDate.now().plusDays(1), GUEST_TEST_MODEL.lastName)
        } returns Completable.complete()
        every { storageMock.getCustomerEmail() } returns "audeatluci6@gmail.com"
        every { amendStringProviderMock.bookingSummaryTitle } returns "BOOKING_TITLE"
        every { amendStringProviderMock.dateChangeDescription(getFakeDates()) } returns "BOOKING_DATES"
        every { amendStringProviderMock.guestAndRoomChangeDescription(1, 1) } returns "BOOKING_GUESTS"
        every { amendStringProviderMock.mealChangeDescription("KFC", 1, 6) } returns "BOOKING_MEALS"
        every { amendStringProviderMock.mealChangeDescription("NFC", 1, 6) } returns "BOOKING_MEALS_2"
        every {
            trackAnalyticsMock.track(AnalyticsConstants.ScreenState.AMEND_CONFIRMATION,
                AmendConfirmationData(bookingRef = "BER2334242", nightsChanged = "0", roomsChanged = "0",
                    roomTypeChanged = false, amendChangesDescription = "",
                    foodRevenueChange = "0", roomRevenueChange = "0",
                    extrasRevenueChange = "0", totalRevenueChange = "0",
                    totalRevenue = "0"))
        } just Runs

        val testObserver = viewModelOpera.states().test()
        viewModelOpera.updateBooking(getFakeReservation(), AmendedTotal("AMENDED", "ALL CHANGES", "+£200.09"))

        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueCount(1)
            .assertNoErrors()

        verify {
            trackAnalyticsMock.track(AnalyticsConstants.ScreenState.AMEND_CONFIRMATION,
                AmendConfirmationData(bookingRef = "BER2334242", nightsChanged = "0", roomsChanged = "0",
                    roomTypeChanged = false, amendChangesDescription = "",
                    foodRevenueChange = "0", roomRevenueChange = "0",
                    extrasRevenueChange = "0", totalRevenueChange = "0",
                    totalRevenue = "0"))
        }
    }

    @Test
    fun `when room is removed then RoomChanged state is applied Opera`() {
        val testObserver = viewModelOpera.states().test()
        every { amendStringProviderMock.roomRemovedText(any()) } returns "Room removed Goku"
        every { amendStringProviderMock.priceChangeText(any(), any()) } returns "Price decreased"

        viewModelOpera.findRoomRemoved(getFakeReservation(),
            getFakeReservation().copy(
                roomsCriteria = listOf(ROOM_CRITERIA_TEST_MODEL, ROOM_CRITERIA_TEST_MODEL.copy(roomId = "ZCR123")),
                roomsBreakdown = listOf(ROOM_BREAKDOWN_TEST_MODEL, ROOM_BREAKDOWN_TEST_MODEL.copy(roomId = "ZCR123", totalRoomCost = PriceDomain(150.00f, GBP))),
                roomsLeadGuest = listOf(GUEST_TEST_MODEL, GUEST_TEST_MODEL.copy(roomId = "ZCR123", title = "Saiyan", firstName = "Goku", lastName = "Son"))))

        verify(exactly = 1) { amendStringProviderMock.roomRemovedText("Saiyan Goku Son") }
        verify(exactly = 1) { amendStringProviderMock.priceChangeText("£150.00", R.string.amend_negative_price_difference) }

        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueAt(1) {
                it.onRoomRemoved == ReviewAmendViewModel.ReviewAmendState.RoomRemoved(
                    mutableListOf(ReviewAmendTextItem().mapToReviewAmendTextItem(
                        title = "Room removed Goku", priceChange = "Price decreased")
                    ))
            }
            .assertValueCount(2)
            .assertNoErrors()
    }

    @Test
    fun `when booking is amended then BookingSummaryCreated state is applied Opera`() {
        val testObserver = viewModelOpera.states().test()
        every { amendStringProviderMock.bookingSummaryTitle } returns "New booking summary"
        every { amendStringProviderMock.dateChangeDescription(any()) } returns "Mon 4 Jan - Thu 7 Jan (3 nights)"
        every { amendStringProviderMock.guestAndRoomChangeDescription(any(), any()) } returns "2 guests 2 rooms"
        every { amendStringProviderMock.mealChangeDescription(any(), any(), any()) } returns "Continental Breakfast (1 guest, 3 nights)"
        every { amendStringProviderMock.priceChangeText(any(), any()) } returns "-£100.00"

        viewModelOpera.createBookingSummary(getFakeBookingSummary())

        verify(exactly = 1) { amendStringProviderMock.dateChangeDescription(LocalDate.now() to LocalDate.now().plusDays(6)) }
        verify(exactly = 1) { amendStringProviderMock.guestAndRoomChangeDescription(numberOfGuests = 1, numberOfRooms = 1) }
        verify(exactly = 1) { amendStringProviderMock.mealChangeDescription(meal = "KFC", guests = 1, nights = 6) }
        verify(exactly = 1) { amendStringProviderMock.mealChangeDescription(meal = "NFC", guests = 1, nights = 6) }

        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueAt(1) {
                it.onBookingSummaryCreated == ReviewAmendViewModel.ReviewAmendState.BookingSummaryCreated(
                    ReviewAmendTitleItem().mapToReviewAmendTitle(title = "New booking summary", previousTotal = "£200.00"),
                    ReviewAmendTextItem().mapToReviewAmendTextItem(description = "Mon 4 Jan - Thu 7 Jan (3 nights)"),
                    ReviewAmendTextItem().mapToReviewAmendTextItem(description = "2 guests 2 rooms"),
                    ReviewAmendTextItem().mapToReviewAmendTextItem(description = "Continental Breakfast (1 guest, 3 nights)\nContinental Breakfast (1 guest, 3 nights)"),
                    ReviewAmendTextItem().mapToReviewAmendTextItem(description = "Premium Wifi\nOnline charitable pledge")
                )
            }
            .assertValueCount(2)
            .assertNoErrors()
    }

    @Test
    fun `when screen Loads Analytics is Sent Opera`() {
        every { amendStringProviderMock.bookingSummaryTitle } returns "BOOKING_TITLE"
        every { amendStringProviderMock.dateChangeDescription(getFakeDates()) } returns "BOOKING_DATES"
        every { amendStringProviderMock.guestAndRoomChangeDescription(1, 1) } returns "BOOKING_GUESTS"
        every { amendStringProviderMock.mealChangeDescription("KFC", 1, 6) } returns "BOOKING_MEALS"
        every { amendStringProviderMock.mealChangeDescription("NFC", 1, 6) } returns "BOOKING_MEALS_2"
        every { amendStringProviderMock.priceChangeText("£150.00", R.string.amend_positive_price_difference) } returns "£150.00"
        every { amendStringProviderMock.priceChangeText("£200.00", R.string.amend_positive_price_difference) } returns "+£200.00"
        every { amendStringProviderMock.priceChangeText("£0.00", R.string.amend_difference) } returns "£0.00"

        every {
            trackAnalyticsMock.track(AnalyticsConstants.ScreenState.AMEND_REVIEW,
                AmendReviewData(bookingRef = "BER2334242", nightsChanged = "0", roomsChanged = "0",
                    roomTypeChanged = false, amendChangesDescription = "BOOKING_DATES;BOOKING_GUESTS;BOOKING_MEALS\nBOOKING_MEALS_2;Premium Wifi\nOnline charitable pledge",
                    payNow = false))
        } just Runs

        val testObserver = viewModelOpera.states().test()
        viewModelOpera.createBookingSummary(getFakeBookingSummary())

        testObserver
            .assertValueAt(0) { it.isLoading }
            .assertValueCount(2)
            .assertNoErrors()

        verify {
            trackAnalyticsMock.track(AnalyticsConstants.ScreenState.AMEND_REVIEW,
                AmendReviewData(bookingRef = "BER2334242", nightsChanged = "0", roomsChanged = "0",
                    roomTypeChanged = false, amendChangesDescription = "BOOKING_DATES;BOOKING_GUESTS;BOOKING_MEALS\n" +
                            "BOOKING_MEALS_2;Premium Wifi\n" +
                            "Online charitable pledge", payNow = false))
        }
    }

    private fun getFakeDates(): Pair<LocalDate, LocalDate> {
        return Pair(LocalDate.now(), LocalDate.now().plusDays(6))
    }

    private fun getFakeBookingSummary(): BookingSummary {
        return BookingSummary(
                roomCriteria = listOf(ROOM_CRITERIA_TEST_MODEL),
                upsells = listOf(UPSELL_TEST_MODEL,
                        UPSELL_TEST_MODEL.copy(legend = "NFC", code = "11"),
                        UPSELL_TEST_MODEL.copy(category = Upsell.Category.OTHER, legend = "Premium Wifi"),
                        UPSELL_TEST_MODEL.copy(category = Upsell.Category.OTHER, legend = "On-line Charitable Pledge")),
                amendedDates = LocalDate.now() to LocalDate.now().plusDays(6),
                amendedTotal = PriceDomain(150.00f, GBP),
                originalTotalCost = PriceDomain(50.00f, GBP)
        )
    }

    private fun getBookingDetailsOpera(): Booking = Booking(
        "AJK9232575",
        "Test",
        LocalDate.now(),
        LocalDate.now(),
        "LONMON",
        "London Gatwick Airport",
        1,
        1,
        "Mr First Surname",
        "Flex",
        null,
        null,
        amendable = true,
        cancellable = true,
        isLinkedToAccount = false,
        isCancelled = false,
        cardFeeApplies = false,
        details = null,
        amendRestrictions = AmendRestrictions.createWithDefaults()
    )
}