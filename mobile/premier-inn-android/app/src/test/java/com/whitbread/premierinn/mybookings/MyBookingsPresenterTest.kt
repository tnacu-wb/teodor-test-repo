package com.whitbread.premierinn.mybookings

import com.google.common.truth.Truth.assertThat
import com.whitbread.premierinn.R
import com.whitbread.premierinn.api.response.InstanceFactory
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.common.StringResourceProvider
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.common.usecase.GetQrKioskHotelUseCase
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.LANGUAGE_ENGLISH
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.data.common.toDomain
import com.whitbread.premierinn.data.remote.AccountApiContract
import com.whitbread.premierinn.data.remote.ApiThrowable
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.booking.usecase.ListenToBookingsUpdates
import com.whitbread.premierinn.domain.booking.usecase.SyncCustomerFutureBookings
import com.whitbread.premierinn.domain.booking.usecase.UpdateStoredBookingData
import com.whitbread.premierinn.domain.ciol.usecase.IsCheckInOnlineEnabledUseCase
import com.whitbread.premierinn.domain.common.AmendRestrictions
import com.whitbread.premierinn.domain.common.COUNTRY_CODE_UK
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.common.OPERA_SOURCE
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.QrKioskHotelsDomain
import com.whitbread.premierinn.domain.customer.usecase.GetCustomer
import com.whitbread.premierinn.domain.graphql.bookingDetails.usecase.GraphQLBookingDetailsUseCase
import com.whitbread.premierinn.domain.graphql.findBooking.entity.FindBookingDomain
import com.whitbread.premierinn.domain.graphql.findBooking.usecase.GraphQLFindBookingUseCase
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.FindBookingRequestBody
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import com.whitbread.premierinn.mybookings.TestData.EXPECTED_BOOKINGS_SORTED_LIST
import com.whitbread.premierinn.mybookings.TestData.EXPECTED_BOOKING_1_FUTURE
import com.whitbread.premierinn.mybookings.TestData.EXPECTED_BOOKING_2_CANCELLED
import com.whitbread.premierinn.mybookings.TestData.EXPECTED_BOOKING_3_FUTURE
import com.whitbread.premierinn.mybookings.TestData.EXPECTED_BOOKING_4_PAST
import com.whitbread.premierinn.mybookings.TestData.EXPECTED_BOOKING_5_BUSINESS_FUTURE
import com.whitbread.premierinn.mybookings.TestData.EXPECTED_BOOKING_6_BUSINESS_CANCELLED
import com.whitbread.premierinn.mybookings.TestData.EXPECTED_BOOKING_7_BUSINESS_PAST
import com.whitbread.premierinn.utils.RxJavaTestRule
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.threeten.bp.LocalDate
import java.util.Locale


class MyBookingsPresenterTest {
    @Rule
    @JvmField
    val rxJavaTestRule = RxJavaTestRule()
    private val view = mock<MyBookingsPresenter.View>()
    private val stringProvider = mock<StringResourceProvider>()
    private val isCustomerLoggedIn = mock<IsCustomerLoggedIn>()
    private val getCustomer = mock<GetCustomer>()
    private val persistenceManager = mock<SimplePersistenceManager>()
    private val businessPersistenceManager = mock<BusinessPersistenceManager>()
    private val bookingDetailsUseCase = mock<GraphQLBookingDetailsUseCase>()
    private val analytics = mock<TrackingAnalytics>()
    private val listenToRecentBookingsUpdates = mock<ListenToBookingsUpdates>()
    private val syncCustomerFutureBookings = mock<SyncCustomerFutureBookings>()
    private val logService = mock<LogService>()
    private val updateStoredBookings = mock<UpdateStoredBookingData>()
    private val subscriptions = CompositeDisposable()
    private val deviceLocaleProvider = mock<DeviceLocaleProvider>()
    private val findBookingUseCase = mock<GraphQLFindBookingUseCase>()
    private val isCheckInOnlineEnabledUseCase = mock<IsCheckInOnlineEnabledUseCase>()
    private val getQrKioskHotelUseCase= mock<GetQrKioskHotelUseCase>()

    private var successCustomerResponse: AccountApiContract.CustomerResponse? = null
    private var emptyCustomerResponse: AccountApiContract.CustomerResponse? = null

    private val presenter = MyBookingsPresenter(subscriptions, stringProvider, isCustomerLoggedIn,
        getCustomer, persistenceManager, businessPersistenceManager, listenToRecentBookingsUpdates,
        syncCustomerFutureBookings, updateStoredBookings, findBookingUseCase, bookingDetailsUseCase,
        analytics, logService, deviceLocaleProvider, isCheckInOnlineEnabledUseCase, getQrKioskHotelUseCase)

    @Before
    fun setUp() {
        successCustomerResponse = InstanceFactory.create(
            AccountApiContract.CustomerResponse::class.java, "apiTest/customer-success.json"
        )
        emptyCustomerResponse = InstanceFactory.create(
            AccountApiContract.CustomerResponse::class.java, "apiTest/customer-empty-sp-response.json"
        )
        mock<MyBookingsPresenter.View> {
            on { view.onFindBookingClick() } doReturn Observable.never()
            on { view.onImportedBooking() } doReturn Observable.never()
            on { view.onBookingListClicks() } doReturn Observable.never()
            on { view.onSearchClick() } doReturn Observable.never()
            on { view.onLogInClick() } doReturn Observable.never()
            on { view.onLoginSuccessful() } doReturn Observable.never()
            on { view.onBookingCancelled() } doReturn Observable.never()
        }
        whenever(deviceLocaleProvider.getDeviceLanguage()).thenReturn(LANGUAGE_ENGLISH)
        whenever(deviceLocaleProvider.getCountryIfRegion(any())).thenReturn(COUNTRY_CODE_UK)
        whenever(deviceLocaleProvider.getDeviceLocale()).thenReturn(Locale.UK)
        whenever(getCustomer.invoke()).thenReturn(Single.never())
        whenever(getQrKioskHotelUseCase.invoke()).thenReturn(qrKioskHotel)


        mock<StringResourceProvider> {
            on { stringProvider.getString(R.string.my_bookings_active_bookings) } doReturn "Active Bookings"
            on { stringProvider.getString(R.string.my_bookings_cancelled_bookings) } doReturn "Cancelled Bookings"
            on { stringProvider.getString(R.string.my_bookings_past_bookings) } doReturn "Past Bookings"
        }
        mock<ListenToBookingsUpdates> { on { listenToRecentBookingsUpdates.execute() } doReturn Observable.just(emptyList()) }
        mock<SyncCustomerFutureBookings> { on { syncCustomerFutureBookings.execute(any()) } doReturn Completable.complete() }
        mock<UpdateStoredBookingData> { on { updateStoredBookings.execute(any(), any(), any()) } doReturn Completable.complete() }
        mock<IsCustomerLoggedIn> { on { isCustomerLoggedIn.invoke() } doReturn Single.never() }
        mock<GetStringResource> { on { getQrKioskHotelUseCase.invoke() } doReturn qrKioskHotel }
        whenever(businessPersistenceManager.getBusinessCustomerEmail()).thenReturn(EMPTY_STRING)
    }

    @Test
    fun `Given user just LoggedIn When Presenter is active Then check for guestHistoryNumber`() {
        whenever(persistenceManager.getCustomer()).thenReturn(emptyCustomerResponse?.toDomain())
        mock<IsCustomerLoggedIn> { on { isCustomerLoggedIn.invoke() } doReturn Single.just(true) }

        presenter.attachView(view)

        verifyAllViewInitialInteractionsAndSubscriptionsIncludingGuestDetails()
        verifyAnalytics(ScreenState.NO_BOOKINGS)

        verify(view).showLoadingInToolbar(true)
        verify(view).showLoading(true)
        verify(view).showLoadingInToolbar(false)
        verify(view).showLoading(false)
        verify(view).showNoBookingsScreen()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `Given No Bookings Updates And User is LoggedIn When Presenter is active Then Empty Booking State Is Shown`() {
        whenever(persistenceManager.getCustomer()).thenReturn(successCustomerResponse?.toDomain())
        mock<IsCustomerLoggedIn> { on { isCustomerLoggedIn.invoke() } doReturn Single.just(true) }

        presenter.attachView(view)

        verifyAllViewInitialInteractionsAndSubscriptions()
        verifyAnalytics(ScreenState.NO_BOOKINGS)

        verify(view).showLoadingInToolbar(true)
        verify(view).showLoading(true)
        verify(view).showLoadingInToolbar(false)
        verify(view).showLoading(false)
        verify(view).showNoBookingsScreen()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `Update error shown if sync fails`() {
        mock<UpdateStoredBookingData> {
            on { updateStoredBookings.execute(any(), any(), any()) } doReturn Completable.error(Exception(""))
        }

        presenter.attachView(view)

        verify(view).showUpdateError()
    }

    @Test
    fun `Given Error on Bookings Updates And Error on Syncing Bookings When Presenter is active Then Error is Logged and Shown`() {
        val bookingUpdatesThrowable = IllegalArgumentException()
        val syncBookingsThrowable = ApiThrowable.Http(404)

        whenever(persistenceManager.getCustomer()).thenReturn(successCustomerResponse?.toDomain())
        mock<ListenToBookingsUpdates> { on { listenToRecentBookingsUpdates.execute() } doReturn Observable.error(bookingUpdatesThrowable) }
        mock<IsCustomerLoggedIn> {
            on { isCustomerLoggedIn() } doReturn Single.just(true)
        }
        mock<SyncCustomerFutureBookings> { on { syncCustomerFutureBookings.execute(any()) } doReturn Completable.error(syncBookingsThrowable) }

        presenter.attachView(view)

        verifyAllViewInitialInteractionsAndSubscriptions()

        verify(view).showLoadingInToolbar(true)
        verify(view).showLoading(true)
        verify(view).showLoadingInToolbar(false)
        verify(view).showLoading(false)

        verify(logService).logException(bookingUpdatesThrowable)
        verify(logService).logException(syncBookingsThrowable)

        verify(view, times(2)).showLoadingError()

        verifyNoMoreInteractions(view)
    }

    @Test
    fun `Given 3 Bookings Updates And User is LoggedIn When Presenter is active Then Booking List Is Shown`() {
        whenever(persistenceManager.getCustomer()).thenReturn(successCustomerResponse?.toDomain())
        mock<ListenToBookingsUpdates> {
            on { listenToRecentBookingsUpdates.execute() } doReturn Observable.just(EXPECTED_BOOKINGS_SORTED_LIST)
        }
        mock<IsCustomerLoggedIn> {
            on { isCustomerLoggedIn() } doReturn Single.just(true)
        }

        presenter.attachView(view)

        verifyAllViewInitialInteractionsAndSubscriptions()
        verifyAnalytics(ScreenState.MY_BOOKINGS)

        verify(view).showLoadingInToolbar(true)
        verify(view).showLoading(true)
        verify(view).showLoadingInToolbar(false)
        verify(view).showLoading(false)
        verify(view).showBookings(
            listOf(
                HeaderUiModel("Active Bookings"),
                MyBookingsMapper("en", false, myList).apply(EXPECTED_BOOKING_3_FUTURE),
                MyBookingsMapper("en", false, myList).apply(EXPECTED_BOOKING_1_FUTURE),
                MyBookingsMapper("en", false, myList).apply(EXPECTED_BOOKING_5_BUSINESS_FUTURE),
                HeaderUiModel("Past Bookings"),
                MyBookingsMapper("en", false, myList).apply(EXPECTED_BOOKING_4_PAST),
                MyBookingsMapper("en", false, myList).apply(EXPECTED_BOOKING_7_BUSINESS_PAST),
                HeaderUiModel("Cancelled Bookings"),
                MyBookingsMapper("en", false, myList).apply(EXPECTED_BOOKING_2_CANCELLED),
                MyBookingsMapper("en", false, myList).apply(EXPECTED_BOOKING_6_BUSINESS_CANCELLED),
                GDPRUiModel()
            )
        )
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `Given 3 Bookings Updates And User is LoggedIn And Checkin flag is off When Presenterisactive Then Booking List Is Shown`() {
        whenever(persistenceManager.getCustomer()).thenReturn(successCustomerResponse?.toDomain())
        mock<ListenToBookingsUpdates> {
            on { listenToRecentBookingsUpdates.execute() } doReturn Observable.just(EXPECTED_BOOKINGS_SORTED_LIST)
        }
        mock<IsCustomerLoggedIn> {
            on { isCustomerLoggedIn() } doReturn Single.just(true)
        }

        presenter.attachView(view)

        verifyAllViewInitialInteractionsAndSubscriptions()
        verifyAnalytics(ScreenState.MY_BOOKINGS)

        verify(view).showLoadingInToolbar(true)
        verify(view).showLoading(true)
        verify(view).showLoadingInToolbar(false)
        verify(view).showLoading(false)
        verify(view).showBookings(
            listOf(
                HeaderUiModel("Active Bookings"),
                MyBookingsMapper("en", false, myList).apply(EXPECTED_BOOKING_3_FUTURE),
                MyBookingsMapper("en", false, myList).apply(EXPECTED_BOOKING_1_FUTURE),
                MyBookingsMapper("en", false, myList).apply(EXPECTED_BOOKING_5_BUSINESS_FUTURE),
                HeaderUiModel("Past Bookings"),
                MyBookingsMapper("en", false, myList).apply(EXPECTED_BOOKING_4_PAST),
                MyBookingsMapper("en", false, myList).apply(EXPECTED_BOOKING_7_BUSINESS_PAST),
                HeaderUiModel("Cancelled Bookings"),
                MyBookingsMapper("en", false, myList).apply(EXPECTED_BOOKING_2_CANCELLED),
                MyBookingsMapper("en", false, myList).apply(EXPECTED_BOOKING_6_BUSINESS_CANCELLED),
                GDPRUiModel()
            )
        )
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `Given No Bookings Updates And User is NOT LoggedIn When Presenter is active Then Empty Booking State Is Shown`() {

        mock<IsCustomerLoggedIn> { on { isCustomerLoggedIn.invoke() } doReturn Single.just(false) }

        presenter.attachView(view)

        verifyAllViewInitialInteractionsAndSubscriptions()
        verifyAnalytics(ScreenState.NO_BOOKINGS)

        verify(view).showNoBookingsScreenWithLoginPrompt()

        verifyNoMoreInteractions(view)
    }

    @Test
    fun `When Presenter is destroyed Then No Active subscriptions`() {
        presenter.destroy()

        assertThat(subscriptions.size()).isEqualTo(0)

        verifyNoMoreInteractions(view)
    }

    @Test
    fun `Given Presenter is active When Login Action occurs Then View Launches LoginActivity`() {
        whenever(view.onLogInClick()).thenReturn(Observable.just(Unit))
        mock<IsCustomerLoggedIn> { on { isCustomerLoggedIn.invoke() } doReturn Single.just(false) }
        presenter.attachView(view)

        verifyAllViewInitialInteractionsAndSubscriptions()
        verifyAnalytics(ScreenState.NO_BOOKINGS)
        verify(view).showNoBookingsScreenWithLoginPrompt()

        verify(view).startLogInActivity()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `Given Presenter is active When BookingItem Action occurs Then View Launches BookingDetailsActivity for Opera hotel`() {
        val arrivalDate = LocalDate.now()
        whenever(view.onBookingListClicks()).thenReturn(Observable.just(OnClickItemAction.BookingItem(0, "12345F",
                false, "operatester", arrivalDate)))
        mock<IsCustomerLoggedIn> { on { isCustomerLoggedIn.invoke() } doReturn Single.just(false) }
        mock<GraphQLFindBookingUseCase> {
            on { findBookingUseCase.findBooking(FindBookingRequestBody("12345F", "operatester",
                    arrivalDate.toString(), "en", "gb", BookingChannelDetails("PI", "MOBILE", "en"))) } doReturn
                    Single.just(FindBookingDomain(OPERA_SOURCE, "12345F","MANOLD-AJJA-NSJJSJ", "2877gdhjhdjjd", "LONEUS", false))
        }

        presenter.attachView(view)

        verifyAllViewInitialInteractionsAndSubscriptionsIncludingFindBooking()
        verifyAnalytics(ScreenState.NO_BOOKINGS)
        verify(view).showNoBookingsScreenWithLoginPrompt()
        verify(view, times(2)).showLoading(any())

        verify(view).startBookingDetailsActivity("12345F", "MANOLD-AJJA-NSJJSJ", "2877gdhjhdjjd")
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `Given Presenter is active When PlanYourTrip Action occurs Then View Launches PlanTripActivity for Opera hotel`() {
        val arrivalDate = LocalDate.now()
        whenever(view.onBookingListClicks()).thenReturn(Observable.just(OnClickItemAction.PlanYourTrip(0, "LONMON", "12345F",
                false, "operatester", arrivalDate)))
        mock<IsCustomerLoggedIn> { on { isCustomerLoggedIn.invoke() } doReturn Single.just(false) }
        mock<GraphQLFindBookingUseCase> {
            on { findBookingUseCase.findBooking(FindBookingRequestBody("12345F", "operatester",
                    arrivalDate.toString(), "en", "gb", BookingChannelDetails("PI", "MOBILE", "en"))) } doReturn
                    Single.just(FindBookingDomain(OPERA_SOURCE, "12345F","MANOLD-AJJA-NSJJSJ", "2877gdhjhdjjd", "LONEUS", false))
        }

        presenter.attachView(view)

        verifyAllViewInitialInteractionsAndSubscriptionsIncludingFindBooking()
        verifyAnalytics(ScreenState.NO_BOOKINGS)
        verify(view).showNoBookingsScreenWithLoginPrompt()
        verify(view, times(2)).showLoading(any())

        verify(view).startPlanTripActivity("LONMON")
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `trackAnalytics counts business bookings correctly`() {
        whenever(businessPersistenceManager.getBusinessCustomerEmail()).thenReturn("someemail@email.com")
        presenter.attachView(view)
        val booking1 = mock<BookingUiModel> {
            on { isBusinessBooking } doReturn true
            on { isCancelled } doReturn false
            on { bookingStatus } doReturn "FUTURE"
        }
        val booking2 = mock<BookingUiModel> {
            on { isBusinessBooking } doReturn true
            on { isCancelled } doReturn true
            on { bookingStatus } doReturn "CANCELLED"
        }
        val booking3 = mock<BookingUiModel> {
            on { isBusinessBooking } doReturn false
        }
        presenter.uiModels = mutableListOf(booking1, booking2, booking3)
        presenter.trackAnalytics("MY_BOOKINGS", "MY_PREMIER_INN")
        verify(analytics).track(eq("MY_BOOKINGS"), any<MyBookingsAnalyticsData>())
    }

    private fun verifyAllViewInitialInteractionsAndSubscriptions() {
        verify(view).onFindBookingClick()
        verify(view).onImportedBooking()
        verify(view).onBookingListClicks()
        verify(view).onSearchClick()
        verify(view).onLogInClick()
        verify(view).onLoginSuccessful()

        assertThat(subscriptions.size()).isEqualTo(8)
    }

    private fun verifyAllViewInitialInteractionsAndSubscriptionsIncludingFindBooking() {
        verify(view).onFindBookingClick()
        verify(view).onImportedBooking()
        verify(view).onBookingListClicks()
        verify(view).onSearchClick()
        verify(view).onLogInClick()
        verify(view).onLoginSuccessful()

        assertThat(subscriptions.size()).isEqualTo(9)
    }

    private fun verifyAllViewInitialInteractionsAndSubscriptionsIncludingGuestDetails() {
        verify(view).onFindBookingClick()
        verify(view).onImportedBooking()
        verify(view).onBookingListClicks()
        verify(view).onSearchClick()
        verify(view).onLogInClick()
        verify(view).onLoginSuccessful()

        assertThat(subscriptions.size()).isEqualTo(9)
    }

    private fun verifyAnalytics(screenState: String) {
        verify(analytics).track(screenState, Type.MY_PREMIER_INN)
    }

    private val myList: List<QrKioskHotelsDomain> = listOf(QrKioskHotelsDomain("NEWDRO"))
    private val qrKioskHotel: List<QrKioskHotelsDomain> = listOf(QrKioskHotelsDomain("NEWDRO"))

    // Third-Party Booking Tests

    @Test
    fun `Given booking list with third party booking When presenter is active Then third party booking is correctly mapped with flag`() {
        whenever(persistenceManager.getCustomer()).thenReturn(successCustomerResponse?.toDomain())
        mock<ListenToBookingsUpdates> {
            on { listenToRecentBookingsUpdates.execute() } doReturn Observable.just(
                listOf(TestData.EXPECTED_BOOKING_8_THIRD_PARTY_FUTURE)
            )
        }
        mock<IsCustomerLoggedIn> {
            on { isCustomerLoggedIn() } doReturn Single.just(true)
        }

        presenter.attachView(view)

        verifyAllViewInitialInteractionsAndSubscriptions()
        verifyAnalytics(ScreenState.MY_BOOKINGS)

        verify(view).showLoadingInToolbar(true)
        verify(view).showLoading(true)
        verify(view).showLoadingInToolbar(false)
        verify(view).showLoading(false)

        val expectedUiModel = MyBookingsMapper("en", false, myList).apply(TestData.EXPECTED_BOOKING_8_THIRD_PARTY_FUTURE)
        assertThat(expectedUiModel.isThirdPartyBooking).isTrue()

        verify(view).showBookings(
            listOf(
                HeaderUiModel("Active Bookings"),
                expectedUiModel,
                GDPRUiModel()
            )
        )
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `Given booking list with mix of third party and regular bookings When presenter is active Then all bookings are displayed correctly`() {
        val mixedBookingsList = listOf(
            EXPECTED_BOOKING_1_FUTURE, // Regular booking
            TestData.EXPECTED_BOOKING_8_THIRD_PARTY_FUTURE, // Third party booking
            EXPECTED_BOOKING_4_PAST, // Regular past
            TestData.EXPECTED_BOOKING_10_THIRD_PARTY_PAST // Third party past
        )

        whenever(persistenceManager.getCustomer()).thenReturn(successCustomerResponse?.toDomain())
        mock<ListenToBookingsUpdates> {
            on { listenToRecentBookingsUpdates.execute() } doReturn Observable.just(mixedBookingsList)
        }
        mock<IsCustomerLoggedIn> {
            on { isCustomerLoggedIn() } doReturn Single.just(true)
        }

        presenter.attachView(view)

        verifyAllViewInitialInteractionsAndSubscriptions()
        verifyAnalytics(ScreenState.MY_BOOKINGS)

        verify(view).showLoadingInToolbar(true)
        verify(view).showLoading(true)
        verify(view).showLoadingInToolbar(false)
        verify(view).showLoading(false)

        val regularBookingUiModel = MyBookingsMapper("en", false, myList).apply(EXPECTED_BOOKING_1_FUTURE)
        val thirdPartyBookingUiModel = MyBookingsMapper("en", false, myList).apply(TestData.EXPECTED_BOOKING_8_THIRD_PARTY_FUTURE)

        assertThat(regularBookingUiModel.isThirdPartyBooking).isFalse()
        assertThat(thirdPartyBookingUiModel.isThirdPartyBooking).isTrue()

        verify(view).showBookings(
            listOf(
                HeaderUiModel("Active Bookings"),
                MyBookingsMapper("en", false, myList).apply(EXPECTED_BOOKING_1_FUTURE),
                MyBookingsMapper("en", false, myList).apply(TestData.EXPECTED_BOOKING_8_THIRD_PARTY_FUTURE),
                HeaderUiModel("Past Bookings"),
                MyBookingsMapper("en", false, myList).apply(EXPECTED_BOOKING_4_PAST),
                MyBookingsMapper("en", false, myList).apply(TestData.EXPECTED_BOOKING_10_THIRD_PARTY_PAST),
                GDPRUiModel()
            )
        )
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `Given third party bookings with different statuses When sorted Then sorting follows same rules as regular bookings`() {
        val thirdPartyBookingsList = listOf(
            TestData.EXPECTED_BOOKING_9_THIRD_PARTY_CANCELLED, // Cancelled
            TestData.EXPECTED_BOOKING_10_THIRD_PARTY_PAST, // Past
            TestData.EXPECTED_BOOKING_8_THIRD_PARTY_FUTURE // Future
        )

        whenever(persistenceManager.getCustomer()).thenReturn(successCustomerResponse?.toDomain())
        mock<ListenToBookingsUpdates> {
            on { listenToRecentBookingsUpdates.execute() } doReturn Observable.just(thirdPartyBookingsList)
        }
        mock<IsCustomerLoggedIn> {
            on { isCustomerLoggedIn() } doReturn Single.just(true)
        }

        presenter.attachView(view)

        verifyAllViewInitialInteractionsAndSubscriptions()
        verifyAnalytics(ScreenState.MY_BOOKINGS)

        verify(view).showLoadingInToolbar(true)
        verify(view).showLoading(true)
        verify(view).showLoadingInToolbar(false)
        verify(view).showLoading(false)

        // Verify they're sorted: FUTURE -> PAST -> CANCELLED
        verify(view).showBookings(
            listOf(
                HeaderUiModel("Active Bookings"),
                MyBookingsMapper("en", false, myList).apply(TestData.EXPECTED_BOOKING_8_THIRD_PARTY_FUTURE),
                HeaderUiModel("Past Bookings"),
                MyBookingsMapper("en", false, myList).apply(TestData.EXPECTED_BOOKING_10_THIRD_PARTY_PAST),
                HeaderUiModel("Cancelled Bookings"),
                MyBookingsMapper("en", false, myList).apply(TestData.EXPECTED_BOOKING_9_THIRD_PARTY_CANCELLED),
                GDPRUiModel()
            )
        )
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `Given bookings with different third party flags When mapped to UI model Then UI preserves the third party state`() {
        val mapper = MyBookingsMapper("en", false, myList)

        val thirdPartyBooking = TestData.EXPECTED_BOOKING_8_THIRD_PARTY_FUTURE
        val regularBooking = EXPECTED_BOOKING_1_FUTURE

        val thirdPartyUiModel = mapper.apply(thirdPartyBooking)
        val regularUiModel = mapper.apply(regularBooking)

        assertThat(thirdPartyUiModel.isThirdPartyBooking).isTrue()
        assertThat(thirdPartyUiModel.id).isEqualTo(thirdPartyBooking.bookingReference)
        assertThat(thirdPartyUiModel.leadGuestSurname).isEqualTo(thirdPartyBooking.leadGuestSurname)

        assertThat(regularUiModel.isThirdPartyBooking).isFalse()
        assertThat(regularUiModel.id).isEqualTo(regularBooking.bookingReference)
    }

}

object TestData {

    val EXPECTED_BOOKING_1_FUTURE = Booking(
        bookingReference = "BBGR103024",
        leadGuestFullName = "Mr Christos Nopeppas",
        arrivalDate = LocalDate.now(),
        departureDate = LocalDate.now().plusDays(2),
        hotelName = "Alpha Edinburgh City Centre (Princes Street)",
        hotelCode = "EDIPRI",
        numberOfRooms = 1,
        totalCost = PriceDomain(145f, "GBP"),
        balanceOutstanding = PriceDomain(45f, GBP),
        rateType = "Flex",
        isLinkedToAccount = true,
        isCancelled = false,
        leadGuestSurname = "NoPeppas",
        amendRestrictions = AmendRestrictions.createWithDefaults(),
        bookingStatus = "FUTURE",
        isBusinessBooking = false
    )

    val EXPECTED_BOOKING_2_CANCELLED = Booking(
        bookingReference = "AYHR128567",
        leadGuestFullName = "Mr John Smith",
        arrivalDate = LocalDate.now(),
        departureDate = LocalDate.now().plusDays(2),
        hotelName = "Alpha London Kensington (Olympia)",
        hotelCode = "LONOLY",
        numberOfRooms = 1,
        isCancelled = true,
        totalCost = PriceDomain(90f, "GBP"),
        balanceOutstanding = PriceDomain(45f, GBP),
        rateType = "Flex",
        isLinkedToAccount = true,
        leadGuestSurname = "Smith",
        amendRestrictions = AmendRestrictions.createWithDefaults(),
        bookingStatus = "CANCELLED",
        isBusinessBooking = false
    )

    val EXPECTED_BOOKING_3_FUTURE = Booking(
        bookingReference = "BHER18216",
        leadGuestFullName = "Mr Christos Smith",
        arrivalDate = LocalDate.now(),
        departureDate = LocalDate.now().plusDays(2),
        hotelName = "Alpha London Chiswick",
        hotelCode = "LONCHI",
        numberOfRooms = 1,
        totalCost = PriceDomain(61f, "GBP"),
        balanceOutstanding = PriceDomain(45f, GBP),
        rateType = "Flex",
        isLinkedToAccount = true,
        isCancelled = false,
        leadGuestSurname = "Smith",
        amendRestrictions = AmendRestrictions.createWithDefaults(),
        bookingStatus = "FUTURE",
        isBusinessBooking = false
    )

    val EXPECTED_BOOKING_4_PAST = Booking(
        bookingReference = "BHER183256",
        leadGuestFullName = "Mr Christos Smith",
        arrivalDate = LocalDate.of(2017, 5, 15),
        departureDate = LocalDate.of(2017, 5, 16),
        hotelName = "Alpha London Chiswick",
        hotelCode = "LONCHI",
        numberOfRooms = 1,
        totalCost = PriceDomain(61f, "GBP"),
        balanceOutstanding = PriceDomain(45f, GBP),
        rateType = "Flex",
        isLinkedToAccount = true,
        isCancelled = false,
        leadGuestSurname = "Smith",
        amendRestrictions = AmendRestrictions.createWithDefaults(),
        bookingStatus = "PAST"
    )

    val EXPECTED_BOOKING_5_BUSINESS_FUTURE = Booking(
        bookingReference = "BBGR999999",
        leadGuestFullName = "Business User",
        arrivalDate = LocalDate.now().plusDays(1),
        departureDate = LocalDate.now().plusDays(2),
        hotelName = "Alpha London Chiswick",
        hotelCode = "LONCHI",
        numberOfRooms = 1,
        totalCost = PriceDomain(200f, "GBP"),
        balanceOutstanding = PriceDomain(0f, GBP),
        rateType = "Business",
        isLinkedToAccount = true,
        isCancelled = false,
        leadGuestSurname = "User",
        amendRestrictions = AmendRestrictions.createWithDefaults(),
        bookingStatus = "FUTURE",
        isBusinessBooking = true
    )

    val EXPECTED_BOOKING_6_BUSINESS_CANCELLED = Booking(
        bookingReference = "BBGR888888",
        leadGuestFullName = "Lead Guest",
        arrivalDate = LocalDate.now().plusDays(1),
        departureDate = LocalDate.now().plusDays(2),
        hotelName = "Alpha London Chiswick",
        hotelCode = "LONCHI",
        numberOfRooms = 1,
        totalCost = PriceDomain(100f, "GBP"),
        balanceOutstanding = PriceDomain(0f, GBP),
        rateType = "Business Flex",
        isLinkedToAccount = true,
        isCancelled = true,
        leadGuestSurname = "Guest ",
        amendRestrictions = AmendRestrictions.createWithDefaults(),
        bookingStatus = "CANCELLED",
        isBusinessBooking = true
    )

    val EXPECTED_BOOKING_7_BUSINESS_PAST = Booking(
        bookingReference = "BBGR85451",
        leadGuestFullName = "Lead Guest",
        arrivalDate = LocalDate.of(2017, 5, 15),
        departureDate = LocalDate.of(2017, 5, 16),
        hotelName = "Alpha London Chiswick",
        hotelCode = "LONCHI",
        numberOfRooms = 1,
        totalCost = PriceDomain(100f, "GBP"),
        balanceOutstanding = PriceDomain(0f, GBP),
        rateType = "Business Flex",
        isLinkedToAccount = true,
        isCancelled = false,
        leadGuestSurname = "Guest ",
        amendRestrictions = AmendRestrictions.createWithDefaults(),
        bookingStatus = "PAST",
        isBusinessBooking = true
    )

    val EXPECTED_BOOKINGS_SORTED_LIST = listOf(
        EXPECTED_BOOKING_3_FUTURE,
        EXPECTED_BOOKING_1_FUTURE,
        EXPECTED_BOOKING_4_PAST,
        EXPECTED_BOOKING_2_CANCELLED,
        EXPECTED_BOOKING_5_BUSINESS_FUTURE,
        EXPECTED_BOOKING_6_BUSINESS_CANCELLED,
        EXPECTED_BOOKING_7_BUSINESS_PAST
    )

    val EXPECTED_BOOKING_8_THIRD_PARTY_FUTURE = Booking(
        bookingReference = "TPBK103024",
        leadGuestFullName = "Mr Third Party Guest",
        arrivalDate = LocalDate.now().plusDays(1),
        departureDate = LocalDate.now().plusDays(3),
        hotelName = "Premier Inn London Tower Bridge",
        hotelCode = "LONTOW",
        numberOfRooms = 1,
        totalCost = PriceDomain(120f, "GBP"),
        balanceOutstanding = PriceDomain(0f, GBP),
        rateType = "Flex",
        isLinkedToAccount = true,
        isCancelled = false,
        leadGuestSurname = "Guest",
        amendRestrictions = AmendRestrictions.createWithDefaults(),
        bookingStatus = "FUTURE",
        isBusinessBooking = false,
        isThirdPartyBooking = true
    )

    val EXPECTED_BOOKING_9_THIRD_PARTY_CANCELLED = Booking(
        bookingReference = "TPBK203045",
        leadGuestFullName = "Mrs Jane Doe",
        arrivalDate = LocalDate.now(),
        departureDate = LocalDate.now().plusDays(1),
        hotelName = "Premier Inn Manchester City Centre",
        hotelCode = "MANCIT",
        numberOfRooms = 1,
        totalCost = PriceDomain(85f, "GBP"),
        balanceOutstanding = PriceDomain(0f, GBP),
        rateType = "Saver",
        isLinkedToAccount = true,
        isCancelled = true,
        leadGuestSurname = "Doe",
        amendRestrictions = AmendRestrictions.createWithDefaults(),
        bookingStatus = "CANCELLED",
        isBusinessBooking = false,
        isThirdPartyBooking = true
    )

    val EXPECTED_BOOKING_10_THIRD_PARTY_PAST = Booking(
        bookingReference = "TPBK389012",
        leadGuestFullName = "Mr Past Guest",
        arrivalDate = LocalDate.of(2024, 1, 10),
        departureDate = LocalDate.of(2024, 1, 12),
        hotelName = "Premier Inn Edinburgh City Centre",
        hotelCode = "EDIPRI",
        numberOfRooms = 1,
        totalCost = PriceDomain(95f, "GBP"),
        balanceOutstanding = PriceDomain(0f, GBP),
        rateType = "Flex",
        isLinkedToAccount = true,
        isCancelled = false,
        leadGuestSurname = "Guest",
        amendRestrictions = AmendRestrictions.createWithDefaults(),
        bookingStatus = "PAST",
        isBusinessBooking = false,
        isThirdPartyBooking = true
    )
}