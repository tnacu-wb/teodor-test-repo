package com.whitbread.premierinn.bookingdetails

import com.google.common.truth.Truth.assertThat
import com.whitbread.premierinn.R
import com.whitbread.premierinn.account.AppFeedbackMessageProvider
import com.whitbread.premierinn.amend.amendguestsrooms.ParcelableAmendTotal
import com.whitbread.premierinn.api.response.InstanceFactory
import com.whitbread.premierinn.bookingdetails.BookingDetailsPresenterTest.TestData.EXPECTED_OPERA_BOOKING_1_WITH_DETAILS
import com.whitbread.premierinn.bookingdetails.BookingDetailsPresenterTest.TestData.EXPECTED_OPERA_BOOKING_1_WITH_DETAILS_AMENDABLE
import com.whitbread.premierinn.bookingdetails.BookingDetailsPresenterTest.TestData.OPERA_EXPECTED_BOOKING_1
import com.whitbread.premierinn.bookingdetails.BookingDetailsPresenterTest.TestData.OPERA_EXPECTED_BOOKING_1_AMENDABLE
import com.whitbread.premierinn.bookingdetails.BookingDetailsPresenterTest.TestData.OPERA_EXPECTED_BOOKING_2
import com.whitbread.premierinn.bookingdetails.BookingDetailsPresenterTest.TestData.expectedOperaUiModel
import com.whitbread.premierinn.bookingdetails.BookingDetailsPresenterTest.TestData.mockHotelPackagesRequestBody
import com.whitbread.premierinn.bookingdetails.BookingDetailsState.StateType.InFlight
import com.whitbread.premierinn.bookingdetails.BookingDetailsState.StateType.Success
import com.whitbread.premierinn.bookingdetails.analytics.BookingDetailsAnalyticsData
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManagerImpl
import com.whitbread.premierinn.common.CheckInCheckOutStringProvider
import com.whitbread.premierinn.common.StringResourceProvider
import com.whitbread.premierinn.common.analytics.AnalyticsData
import com.whitbread.premierinn.common.analytics.FirebaseLogger
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.data.common.LANGUAGE_ENGLISH
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl
import com.whitbread.premierinn.data.graphql.mapper.mapToBookingDomain
import com.whitbread.premierinn.data.graphql.mapper.mapToHotelInformationGQL
import com.whitbread.premierinn.data.graphql.mapper.mapToPackagesGraphQL
import com.whitbread.premierinn.data.graphql.mapper.mapToPromotionsInformationDomain
import com.whitbread.premierinn.data.remote.graphql.contracts.BookingConfirmationGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.HotelInfoGraphQLContract.HotelInfoData
import com.whitbread.premierinn.data.remote.graphql.contracts.PackagesGraphQLContract
import com.whitbread.premierinn.data.remote.graphql.contracts.PromotionsInformationGraphQLContract
import com.whitbread.premierinn.domain.apprating.usecase.AppRatingPromptIfNeeded
import com.whitbread.premierinn.domain.apprating.usecase.SaveAppAsRated
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.booking.usecase.GetBookingUpdates
import com.whitbread.premierinn.domain.ciol.usecase.IsCheckInOnlineEnabledUseCase
import com.whitbread.premierinn.domain.common.AmendRestrictions
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.hoteldetails.entity.HotelInformationDomain
import com.whitbread.premierinn.domain.common.hoteldetails.usecase.GraphQLHotelDetailsUseCase
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.graphql.bookingDetails.usecase.GraphQLBookingDetailsUseCase
import com.whitbread.premierinn.domain.graphql.findBooking.entity.FindBookingDomain
import com.whitbread.premierinn.domain.graphql.findBooking.usecase.GraphQLFindBookingUseCase
import com.whitbread.premierinn.domain.graphql.hdp.entity.DataPackagesDomain
import com.whitbread.premierinn.domain.graphql.promotions.entity.PromotionsInformationDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelPackagesRequestBody
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.ALL_CHECK_IN_CHECK_OUT_TIMES
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.AMEND_OPERA_BOOKING_INFO
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.AMEND_OPERA_BOOKING_INFO_BUSINESS
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.CUSTOMER_SERVICE_NUMBER
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.FEATURE_SHOULD_SHOW_AMEND_BANNER
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.FEATURE_SHOULD_SHOW_AMEND_BANNER_BUSINESS
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.NON_CHARGEABLE_PHONE_DESC
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import com.whitbread.premierinn.utils.TestSchedulerRule
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.ReplaySubject
import io.reactivex.subjects.SingleSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.threeten.bp.LocalDate
import java.util.Locale

@ExperimentalCoroutinesApi
class BookingDetailsPresenterTest {

    @get:Rule
    val rxRule = TestSchedulerRule()
    private val testDispatcher = UnconfinedTestDispatcher()

    private val getBookingUpdates = mock<GetBookingUpdates>()
    private val appRatingPromptIfNeeded = mock<AppRatingPromptIfNeeded>()
    private val saveAppAsRated = mock<SaveAppAsRated>()
    private val getStringResource = mock<GetStringResource>()
    private val contentRepository = mock<ContentManagedResourceRepository>()
    private val checkInCheckOutStringProvider = mock<CheckInCheckOutStringProvider>()
    private val logService = mock<LogService>()
    private val storage = mock<SimplePersistenceManagerImpl>()
    private val businessStorage = mock<BusinessPersistenceManagerImpl>()
    private val appFeedbackProvider = mock<AppFeedbackMessageProvider> {
        on { emailAddress } doReturn "email@test.com"
        on { emailFeedbackBody } doReturn "email Body"
        on { emailFeedbackSubject } doReturn "email Subject"
    }
    private val firebaseLogger = mock<FirebaseLogger>()

    private val stringResourceProvider = mock<StringResourceProvider>()
    private val graphQLBookingDetailsUseCase = mock<GraphQLBookingDetailsUseCase>()
    private val graphQLFindBookingUseCase = mock<GraphQLFindBookingUseCase>()
    private val view = mock<BookingDetailsPresenter.View>()
    private val isFeatureOn = mock<IsFeatureOn>()
    private val deviceLocaleProvider = mock<DeviceLocaleProvider> {
        on { this.getDeviceLocale()} doReturn Locale.UK
        on { this.getDeviceLanguage()} doReturn LANGUAGE_ENGLISH
    }
    private val amendTotalMock = ParcelableAmendTotal("Booking", "To be paid on " +
            "arrival", "21.90")

    private val trackingAnalytics = mock<TrackingAnalytics>()

    private val isCheckInOnlineEnabledUseCase = mock<IsCheckInOnlineEnabledUseCase>()
    private val bookingUiMapper = BookingUiModelMapper(stringResourceProvider,
        deviceLocaleProvider, isCheckInOnlineEnabledUseCase)
    private val operaBookingDetailsUiMapper = OperaBookingDetailsUiModelMapper(stringResourceProvider, getStringResource)
    private lateinit var hotelInfoDomain: HotelInformationDomain
    private lateinit var dataPackagesDomain: DataPackagesDomain
    private lateinit var bookingConfirmationAndManageBooking: Booking
    private lateinit var promotionInfoDomain: PromotionsInformationDomain
    private val subscriptions = CompositeDisposable()
    private val operaBookingReferenceStub = "BANBRI0379120"
    private val operaUuidBasketReferenceStub = "BAN-7bfbd1bb-1b98-4be8-acc2-7a14f8ee9787"
    private val balanceOutstanding = PriceDomain(145f, "GBP")
    private val addToCalendarAction = PublishSubject.create<AddToCalendarAction>()
    private val sendInvoiceAction = PublishSubject.create<SendInvoiceAction>()
    private val checkInOnlineButtonAction = PublishSubject.create<CheckInOnlineAction>()
    private val readyToLeaveButtonAction = PublishSubject.create<ReadyToLeaveAction>()
    private val mapDirectionsAction = PublishSubject.create<MapDirectionsAction>()
    private val hotelDetailsAction = PublishSubject.create<HotelDetailsAction>()
    private val manageBookingAction = PublishSubject.create<Unit>()
    private val appRatingFeedbackAction = PublishSubject.create<Any>()
    private val appRatingOkAction = PublishSubject.create<Any>()
    private val appRatingCancelAction = PublishSubject.create<Any>()
    private val appRatingTrigger = SingleSubject.create<Boolean>()
    private val faqAction = PublishSubject.create<Unit>()
    private val roomKeyInstructionsAction = PublishSubject.create<RoomKeyInstructionsAction>()
    private val priceBreakDownAction = PublishSubject.create<Unit>()
    private val bookingUpdates = ReplaySubject.create<Booking>()
    private val parkingDetailsAction = PublishSubject.create<ParkingAction>()
    private val sendInvoiceDialogAction = PublishSubject.create<Any>()

    private val operaPresenter = BookingDetailsPresenter(
        subscriptions,
        getBookingUpdates,
        bookingUiMapper,
        operaBookingDetailsUiMapper,
        appRatingPromptIfNeeded,
        saveAppAsRated,
        logService,
        appFeedbackProvider,
        firebaseLogger,
        isFeatureOn,
        storage,
        businessStorage,
        contentRepository,
        checkInCheckOutStringProvider,
        deviceLocaleProvider,
        stringResourceProvider,
        getStringResource,
        graphQLBookingDetailsUseCase,
        graphQLFindBookingUseCase,
        trackingAnalytics,
    )


    @Before
    fun setUp() {

        Dispatchers.setMain(testDispatcher)

        whenever(deviceLocaleProvider.getDeviceLocale()).thenReturn(Locale.UK)

        mock<BookingDetailsPresenter.View> {
            on { view.onAddToCalendarClick() } doReturn addToCalendarAction
            on { view.onSendInvoiceClicked() } doReturn sendInvoiceAction
            on { view.onCheckInOnlineButtonClick() } doReturn checkInOnlineButtonAction
            on { view.onReadyToLeaveButtonClick() } doReturn readyToLeaveButtonAction
            on { view.onDirectionClicked() } doReturn mapDirectionsAction
            on { view.onFaqButtonClicked() } doReturn faqAction
            on { view.onRoomKeyInstructionsButtonClicked() } doReturn roomKeyInstructionsAction
            on { view.onHotelNameClick() } doReturn hotelDetailsAction
            on { view.onManageBookingClicked() } doReturn manageBookingAction
            on { view.onPriceBreakdownButtonClicked() } doReturn priceBreakDownAction
            on { view.onAppRatingFeedbackClicked() } doReturn appRatingFeedbackAction
            on { view.onAppRatingPromptCancelClicked() } doReturn appRatingCancelAction
            on { view.onAppRatingPromptOkClicked() } doReturn appRatingOkAction
            on { view.onParkingButtonClicked() } doReturn parkingDetailsAction
            on { view.onSendInvoiceDialogClicked() } doReturn sendInvoiceDialogAction
        }

        mock<StringResourceProvider> {
            on { stringResourceProvider.getNights(any()) } doReturn "any"
            on { stringResourceProvider.getRooms(any(), any()) } doReturn "any"
            on { stringResourceProvider.getString(any()) } doReturn "any"
            on { stringResourceProvider.getString(R.string.booking_details_fully_paid) } doReturn "Pre-paid message"
            on { stringResourceProvider.getString(any(), any()) } doReturn "any"
            on {
                stringResourceProvider.getString(
                    eq(R.string.booking_details_paid_on_arrival),
                    any()
                )
            } doReturn "Amount to be paid on arrival"
            on { stringResourceProvider.getString((R.string.flex_value)) } doReturn "Flex"
            on { stringResourceProvider.getString(R.string.call_hotel) } doReturn "Call hotel"
            on { stringResourceProvider.getString(R.string.call_us) } doReturn "Call us"
        }

        mock<IsFeatureOn> { on { isFeatureOn(FEATURE_SHOULD_SHOW_AMEND_BANNER) } doReturn false }
        mock<IsFeatureOn> { on { isFeatureOn(FEATURE_SHOULD_SHOW_AMEND_BANNER_BUSINESS) } doReturn false }
        mock<AppRatingPromptIfNeeded> { on { appRatingPromptIfNeeded.execute(true) } doReturn appRatingTrigger }
        mock<AppRatingPromptIfNeeded> { on { appRatingPromptIfNeeded.execute(false) } doReturn appRatingTrigger }
        mock<GetStringResource> { on { getStringResource.invoke(CUSTOMER_SERVICE_NUMBER) } doReturn "313131213" }
        mock<GetStringResource> { on { getStringResource.invoke(NON_CHARGEABLE_PHONE_DESC) } doReturn "some other desc" }
        mock<GetStringResource> { on { getStringResource.invoke(AMEND_OPERA_BOOKING_INFO) } doReturn "If you need to amend this booking" }
        mock<GetStringResource> { on { getStringResource.invoke(AMEND_OPERA_BOOKING_INFO_BUSINESS) } doReturn "If you need to amend this booking" }
        whenever(businessStorage.getBusinessCustomerEmail()).thenReturn("")

        mock<ContentManagedResourceRepository> {
            on {
                contentRepository
                    .getStringSingle(ALL_CHECK_IN_CHECK_OUT_TIMES.value)
            } doReturn Single.just(
                "{\"ukCheckInTimes\":{\"bookingDetailsCheckInInfo\": \"from 3pm\",\"bookingDetailsCheckOutInfo\": \"before 12pm\","
                        + "\"summaryOrPaymentBreakdownCheckInInfo\": \"Check-in at 3:00pm\","
                        + "\"summaryOrPaymentBreakdownCheckOutInfo\": \"Check-out by 12:00pm\"},"
                        + "\"germanyCheckInTimes\": {\"bookingDetailsCheckInInfo\": \"after 6pm\","
                        + "\"bookingDetailsCheckOutInfo\": \"before 3pm\","
                        + "\"summaryOrPaymentBreakdownCheckInInfo\": \"Check-in at 6:00pm\","
                        + "\"summaryOrPaymentBreakdownCheckOutInfo\": \"Check-out by 3:00pm\"}}"
            )
        }
        dataPackagesDomain = InstanceFactory.create<PackagesGraphQLContract.PackagesData>(
            PackagesGraphQLContract.PackagesData::class.java,
            "apiTest/graphql/packages_success_gql.json"
        ).data?.packages.mapToPackagesGraphQL(1)
        hotelInfoDomain = InstanceFactory.create<HotelInfoData>(
            HotelInfoData::class.java,
            "apiTest/graphql/hotel_info_gql.json"
        ).mapToHotelInformationGQL()
        promotionInfoDomain = InstanceFactory.create<PromotionsInformationGraphQLContract.PromotionsInformationData>(
            PromotionsInformationGraphQLContract.PromotionsInformationData::class.java,
            "apiTest/graphql/amend_promotions_information_gql.json"
        ).mapToPromotionsInformationDomain()
        bookingConfirmationAndManageBooking = InstanceFactory
            .create<BookingConfirmationGraphQLContract.BookingConfirmationData>(
                BookingConfirmationGraphQLContract.BookingConfirmationData::class.java,
                "apiTest/graphql/hotel_info_gql.json"
            ).mapToBookingDomain("Bangor (Gwynedd, North Wales)", countries = listOf())
        operaPresenter.view = view

        whenever(isCheckInOnlineEnabledUseCase()).thenReturn(false)
        operaPresenter.initParams(
            bookingReference = operaBookingReferenceStub,
            uuidBasketReference = operaUuidBasketReferenceStub,
            token = "someToken",
            "Steven",
"2025-03-06",
false,
"ciol",
            justBookedEmail = null,
            accountResponse = null,
            amendTotalMock,
            EMPTY_STRING_DOMAIN
        )
    }

    @Test
    fun `Given 2 Booking Updates in different time And Hotel Info Ok When Presenter is active Then States are Returned Successfully`() {
        mock<GetBookingUpdates> {
            on { getBookingUpdates.execute(operaBookingReferenceStub) } doReturn bookingUpdates.toFlowable(BackpressureStrategy.BUFFER)
        }

        mock<GraphQLFindBookingUseCase> {
            on { graphQLBookingDetailsUseCase.getHotelInfo(
                deviceLocaleProvider.getDeviceLocale().country.lowercase(),
                deviceLocaleProvider.getDeviceLanguage(),
                OPERA_EXPECTED_BOOKING_1.hotelCode) } doReturn Observable.just(hotelInfoDomain) }


        whenever(graphQLBookingDetailsUseCase.getHotelInfo(
            deviceLocaleProvider.getDeviceLocale().country.lowercase(),
            deviceLocaleProvider.getDeviceLanguage(),
            OPERA_EXPECTED_BOOKING_1.hotelCode)).thenReturn(Observable.just(hotelInfoDomain)
        )

        mock<GraphQLBookingDetailsUseCase> {
            on { graphQLBookingDetailsUseCase.bookingConfirmationAndManageBooking(
                any(),
                any(),
                any(),
                anyOrNull(),
                any(),
                any()) } doReturn Observable.just(
                EXPECTED_OPERA_BOOKING_1_WITH_DETAILS) }

        mock<GraphQLHotelDetailsUseCase> {
            on {
                graphQLBookingDetailsUseCase.getPackages(
                    mockHotelPackagesRequestBody
                )
            } doReturn Single.just(dataPackagesDomain)
        }

        operaPresenter.attachView(view)
        bookingUpdates.onNext(OPERA_EXPECTED_BOOKING_1)

        runWithTestScheduler(rxRule)
        verify(view).shouldShowLoadingSpinner(true)
        verifyAllViewInitialInteractionsAndSubscriptionsForOpera()

        verify(view, times(0)).showAmendBookingInfoBanner(anyString(), any())

        verify(view).bindBookingState(
            BookingDetailsState(Success, bookingUiModel =
                expectedOperaUiModel(stringResourceProvider, deviceLocaleProvider, isCheckInOnlineEnabledUseCase)
            ), storage, businessStorage, amendTotalMock, "",
            "",
            PriceDomain.createDefault(),
            deviceLocaleProvider,
            EMPTY_STRING_DOMAIN,
            emptyList(),
            false
        )
        verify(view).bindBookingState(
            BookingDetailsState(InFlight, bookingUiModel =
                expectedOperaUiModel(stringResourceProvider, deviceLocaleProvider, isCheckInOnlineEnabledUseCase)),
            storage,
            businessStorage,
            amendTotalMock,
            "",
            "",
            PriceDomain.createDefault(),
            deviceLocaleProvider,
            EMPTY_STRING_DOMAIN,
            emptyList(),
            false
        )

        verify(view).bindBookingState(
            BookingDetailsState(Success,
                bookingUiModel = expectedOperaUiModel(stringResourceProvider, deviceLocaleProvider, isCheckInOnlineEnabledUseCase),
                bookingDetails = operaBookingDetailsUiMapper.apply(Pair
                    (OPERA_EXPECTED_BOOKING_1, hotelInfoDomain))),
            storage,
            businessStorage,
            amendTotalMock,
            "from 3pm",
            "before 12pm",
            PriceDomain.createDefault(),
            deviceLocaleProvider,
            EMPTY_STRING_DOMAIN,
            emptyList(),
            false
        )
        verify(graphQLBookingDetailsUseCase, times(1)).bookingConfirmationAndManageBooking(
            any(), any(), any(), any(), any(), any())

        verify(graphQLBookingDetailsUseCase, times(1)).getHotelInfo(
            "gb", "en",
            OPERA_EXPECTED_BOOKING_1.hotelCode)
        verify(view).shouldShowLoadingSpinner(false)

        bookingUpdates.onNext(OPERA_EXPECTED_BOOKING_2)

        verify(view).bindBookingState(
            BookingDetailsState(Success, bookingUiModel =
                BookingUiModelMapper(
                    stringResourceProvider,
                    deviceLocaleProvider,
                    isCheckInOnlineEnabledUseCase
                ).apply(OPERA_EXPECTED_BOOKING_2)),
            storage, businessStorage,
            amendTotalMock, "from 3pm",
            "before 12pm",
            balanceOutstanding,
            deviceLocaleProvider,
            EMPTY_STRING_DOMAIN,
            emptyList(),
            false
        )
        verify(view).shouldShowLoadingSpinner(false)
        verifyNoMoreInteractions(view)
    }


    @Test
    fun `Given 2 Booking Updates with promotion in different time And Hotel Info Ok When Presenter is active Then States are Returned Successfully`() {
        mock<GetBookingUpdates> {
            on { getBookingUpdates.execute(operaBookingReferenceStub) } doReturn bookingUpdates.toFlowable(BackpressureStrategy.BUFFER)
        }

        mock<GraphQLFindBookingUseCase> {
            on { graphQLBookingDetailsUseCase.getHotelInfo(
                deviceLocaleProvider.getDeviceLocale().country.lowercase(),
                deviceLocaleProvider.getDeviceLanguage(),
                OPERA_EXPECTED_BOOKING_1.hotelCode) } doReturn Observable.just(hotelInfoDomain) }


        whenever(graphQLBookingDetailsUseCase.getHotelInfo(
            deviceLocaleProvider.getDeviceLocale().country.lowercase(),
            deviceLocaleProvider.getDeviceLanguage(),
            OPERA_EXPECTED_BOOKING_1.hotelCode)).thenReturn(Observable.just(hotelInfoDomain)
        )

        mock<GraphQLBookingDetailsUseCase> {
            on { graphQLBookingDetailsUseCase.bookingConfirmationAndManageBooking(
                any(),
                any(),
                any(),
                anyOrNull(),
                any(),
                any()) } doReturn Observable.just(
                EXPECTED_OPERA_BOOKING_1_WITH_DETAILS) }

        mock<GraphQLHotelDetailsUseCase> {
            on {
                graphQLBookingDetailsUseCase.getPackages(
                    mockHotelPackagesRequestBody
                )
            } doReturn Single.just(dataPackagesDomain)
        }

        operaPresenter.attachView(view)
        bookingUpdates.onNext(OPERA_EXPECTED_BOOKING_1)

        runWithTestScheduler(rxRule)
        verify(view).shouldShowLoadingSpinner(true)
        verifyAllViewInitialInteractionsAndSubscriptionsForOpera()

        verify(view, times(0)).showAmendBookingInfoBanner(anyString(), any())

        verify(view).bindBookingState(
            BookingDetailsState(Success, bookingUiModel =
                expectedOperaUiModel(stringResourceProvider, deviceLocaleProvider, isCheckInOnlineEnabledUseCase)
            ), storage, businessStorage, amendTotalMock, "",
            "",
            PriceDomain.createDefault(),
            deviceLocaleProvider,
            EMPTY_STRING_DOMAIN,
            emptyList(),
            false
        )
        verify(view).bindBookingState(
            BookingDetailsState(InFlight, bookingUiModel =
                expectedOperaUiModel(stringResourceProvider, deviceLocaleProvider, isCheckInOnlineEnabledUseCase)),
            storage,
            businessStorage,
            amendTotalMock,
            "",
            "",
            PriceDomain.createDefault(),
            deviceLocaleProvider,
            EMPTY_STRING_DOMAIN,
            emptyList(),
            false
        )

        verify(view).bindBookingState(
            BookingDetailsState(Success,
                bookingUiModel = expectedOperaUiModel(stringResourceProvider, deviceLocaleProvider, isCheckInOnlineEnabledUseCase),
                bookingDetails = operaBookingDetailsUiMapper.apply(Pair
                    (OPERA_EXPECTED_BOOKING_1, hotelInfoDomain))),
            storage,
            businessStorage,
            amendTotalMock,
            "from 3pm",
            "before 12pm",
            PriceDomain.createDefault(),
            deviceLocaleProvider,
            EMPTY_STRING_DOMAIN,
            emptyList(),
            false
        )
        verify(graphQLBookingDetailsUseCase, times(1)).bookingConfirmationAndManageBooking(
            any(), any(), any(), any(), any(), any())

        verify(graphQLBookingDetailsUseCase, times(1)).getHotelInfo(
            "gb", "en",
            OPERA_EXPECTED_BOOKING_1.hotelCode)
        verify(view).shouldShowLoadingSpinner(false)

        bookingUpdates.onNext(OPERA_EXPECTED_BOOKING_2)

        verify(view).bindBookingState(
            BookingDetailsState(Success, bookingUiModel =
                BookingUiModelMapper(
                    stringResourceProvider,
                    deviceLocaleProvider,
                    isCheckInOnlineEnabledUseCase
                ).apply(OPERA_EXPECTED_BOOKING_2)),
            storage, businessStorage,
            amendTotalMock, "from 3pm",
            "before 12pm",
            balanceOutstanding,
            deviceLocaleProvider,
            EMPTY_STRING_DOMAIN,
            emptyList(),
            false
        )
        verify(view).shouldShowLoadingSpinner(false)
        verifyNoMoreInteractions(view)
    }


    @Test
    fun `Given An Booking Update And Error In Hotel Info When Presenter is active Then State with Data is Returned`() {
        mock<GetBookingUpdates> {
            on { getBookingUpdates.execute(operaBookingReferenceStub) } doReturn Flowable.just(OPERA_EXPECTED_BOOKING_1)
        }

        mock<GraphQLBookingDetailsUseCase> {
            on { graphQLBookingDetailsUseCase.getHotelInfo(any(), any(), any()) } doReturn Observable.error(Throwable("Hotel info error"))
        }

        operaPresenter.attachView(view)
        runWithTestScheduler(rxRule)
        verify(view).shouldShowLoadingSpinner(true)
        verifyAllViewInitialInteractionsAndSubscriptionsOnFailureOfHotelInfo()

        verify(view).bindBookingState(
            BookingDetailsState(Success, bookingUiModel =
                expectedOperaUiModel(stringResourceProvider, deviceLocaleProvider, isCheckInOnlineEnabledUseCase)),
            storage,
            businessStorage,
            amendTotalMock,
            "",
            "",
            PriceDomain.createDefault(),
            deviceLocaleProvider,
            EMPTY_STRING_DOMAIN,
            emptyList(),
            false
        )
        verify(view).bindBookingState(
            BookingDetailsState(InFlight, bookingUiModel =
                expectedOperaUiModel(stringResourceProvider, deviceLocaleProvider, isCheckInOnlineEnabledUseCase)),
            storage,
            businessStorage,
            amendTotalMock,
            "",
            "",
            PriceDomain.createDefault(),
            deviceLocaleProvider,
            EMPTY_STRING_DOMAIN,
            emptyList(),
            false
        )
        verify(view).bindBookingState(
            BookingDetailsState.error(), storage, businessStorage, amendTotalMock, "",
            "",
            PriceDomain.createDefault(),
            deviceLocaleProvider,
            EMPTY_STRING_DOMAIN,
            emptyList(),
            false
        )
        verify(view).shouldShowLoadingSpinner(false)
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `Given No Booking Updates(unExpected) When Presenter is active Then Error Is Logged`() {
        val noSuchElementException = NoSuchElementException()
        mock<GetBookingUpdates> {
            on { getBookingUpdates.execute(operaBookingReferenceStub) } doReturn Flowable.error(noSuchElementException)
        }

        operaPresenter.attachView(view)
        runWithTestScheduler(rxRule)
        verify(view).shouldShowLoadingSpinner(true)

        verifyAllViewInitialInteractionsAndSubscriptionsOnFailureOfBookingUpdate()
        verify(logService).logException(noSuchElementException)
        verify(view).bindBookingState(
            BookingDetailsState.inFlight(null),
            storage, businessStorage,
            amendTotalMock, "",
            "",
            PriceDomain.createDefault(),
            deviceLocaleProvider,
            EMPTY_STRING_DOMAIN,
            emptyList(),
            false
        )
        verify(view).bindBookingState(
            BookingDetailsState.error(), storage, businessStorage,
            amendTotalMock, "",
            "",
            PriceDomain.createDefault(),
            deviceLocaleProvider,
            EMPTY_STRING_DOMAIN,
            emptyList(),
            false
        )
        verify(view).shouldShowLoadingSpinner(false)
        verifyNoMoreInteractions(view)
    }


    @Test
    fun `Given Booking was prepaid THEN pre-pay information message is not shown`() {
        val bookingReturned = EXPECTED_OPERA_BOOKING_1_WITH_DETAILS.copy(bookingReference = operaBookingReferenceStub,
            prepaidAmount = OPERA_EXPECTED_BOOKING_1.totalCost)
        mock<GetBookingUpdates> {
            on { getBookingUpdates.execute(operaBookingReferenceStub) } doReturn Flowable.just(bookingReturned)
        }

        mock<GraphQLBookingDetailsUseCase> {
            on { graphQLBookingDetailsUseCase.getHotelInfo(any(), any(), any()) } doReturn Observable.just(hotelInfoDomain) }


        mock<GraphQLBookingDetailsUseCase> {
            on { graphQLBookingDetailsUseCase.bookingConfirmationAndManageBooking(
                any(),
                any(),
                any(),
                anyOrNull(),
                any(),
                any()) } doReturn Observable.just(
                EXPECTED_OPERA_BOOKING_1_WITH_DETAILS) }

        operaPresenter.attachView(view)
        runWithTestScheduler(rxRule)

        argumentCaptor<BookingDetailsState>().apply {
            verify(view, times(1)).bindBookingState(
                capture(), eq(storage), eq(businessStorage),
                eq(amendTotalMock), eq("from 3pm"),
                eq("before 12pm"),
                eq(PriceDomain.createDefault()),
                eq(deviceLocaleProvider),
                eq(EMPTY_STRING_DOMAIN),
                eq(emptyList()),
                eq(false)
            )
            verify(view, times(2)).bindBookingState(
                capture(), eq(storage), eq(businessStorage),
                eq(amendTotalMock), eq(""),
                eq(""),
                eq(PriceDomain.createDefault()),
                eq(deviceLocaleProvider),
                eq(EMPTY_STRING_DOMAIN),
                eq(emptyList()),
                eq(false)
            )
            assertEquals(allValues.last().bookingUiModel!!.infoMessage, null)
        }
    }


    @Test
    fun `Given An Booking Update And Hotel Info Ok When Presenter is active Then 3 States with Data is Returned`() {
        setupAndVerifySuccessfulBookingUpdate()
    }

    @Test
    fun `Given An Booking Update with promo And Hotel Info Ok When Presenter is active Then 3 States with Data is Returned`() {
        setupAndVerifySuccessfulBookingUpdate()
    }

    @Test
    fun `Given An Booking with promo , when clicking on manage booking makes promo call and launches activity`() {
        mock<GetBookingUpdates> {
            on { getBookingUpdates.execute(operaBookingReferenceStub) } doReturn bookingUpdates.toFlowable(BackpressureStrategy.BUFFER)
        }

        mock<GraphQLFindBookingUseCase> {
            on { graphQLBookingDetailsUseCase.getHotelInfo(
                deviceLocaleProvider.getDeviceLocale().country.lowercase(),
                deviceLocaleProvider.getDeviceLanguage(),
                OPERA_EXPECTED_BOOKING_1.hotelCode) } doReturn Observable.just(hotelInfoDomain) }


        whenever(graphQLBookingDetailsUseCase.getHotelInfo(
            deviceLocaleProvider.getDeviceLocale().country.lowercase(),
            deviceLocaleProvider.getDeviceLanguage(),
            OPERA_EXPECTED_BOOKING_1.hotelCode)).thenReturn(Observable.just(hotelInfoDomain)
        )

        mock<GraphQLBookingDetailsUseCase> {
            on { graphQLBookingDetailsUseCase.bookingConfirmationAndManageBooking(
                any(),
                any(),
                any(),
                anyOrNull(),
                any(),
                any()) } doReturn Observable.just(
                EXPECTED_OPERA_BOOKING_1_WITH_DETAILS_AMENDABLE) }

        val findBookingDomain = FindBookingDomain(
            sourcePms = "OPERA",
            bookingReference = operaBookingReferenceStub,
            uuidBasketReference = operaUuidBasketReferenceStub,
            token = "test-token-123",
            hotelId = OPERA_EXPECTED_BOOKING_1.hotelCode,
            false
        )

        whenever(graphQLBookingDetailsUseCase.findBookingAndPromoInfoCall(
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            anyOrNull()
        )).thenReturn(Single.just(Pair(findBookingDomain, promotionInfoDomain)))

        operaPresenter.attachView(view)
        bookingUpdates.onNext(OPERA_EXPECTED_BOOKING_1_AMENDABLE)
        runWithTestScheduler(rxRule)

        manageBookingAction.onNext(Unit)
        rxRule.testScheduler.triggerActions()

        verify(graphQLBookingDetailsUseCase).findBookingAndPromoInfoCall(
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            anyOrNull()
        )
        verify(view).startAmendBookingActivity(
            any(),
            eq(operaUuidBasketReferenceStub),
            eq("test-token-123"),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any()
        )
    }

    @Test
    fun `Given Presenter is active When Faq Action occurs Then View Launches UriActivity`() {
        setupAndVerifySuccessfulBookingUpdate()

        faqAction.onNext(Unit)

        verify(view).startUriActivity(any())
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `Given Presenter is active When Direction Action occurs Then View Launches UriActivity`() {
        setupAndVerifySuccessfulBookingUpdate()

        mapDirectionsAction.onNext(MapDirectionsAction("URI"))

        verify(view).startUriActivity("URI")
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `Given Presenter is active When Hotel Action occurs Then View Launches HotelDetails`() {
        setupAndVerifySuccessfulBookingUpdate()

        hotelDetailsAction.onNext(HotelDetailsAction("BANBRI"))

        verify(view).startHotelDetailsActivity("BANBRI", "PI")
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `Given Presenter is active, When ParkingAction occurs, Then View will display ParkingBottomSheet`() {
        setupAndVerifySuccessfulBookingUpdate()

        parkingDetailsAction.onNext(ParkingAction("Hotel parking description"))

        verify(view).showParkingDetailsBottomSheet("Hotel parking description")
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `Given Presenter is active When AddToCalendar Action occurs Then View Launches Calendar`() {
        setupAndVerifySuccessfulBookingUpdate()

        addToCalendarAction.onNext(AddToCalendarAction(LocalDate.of(2019, 9, 11), LocalDate.of(2019, 9, 15), "title", "description", "location"))

        verify(view).startCalendarApp(any())
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `Given Presenter is active When Presenter is destroyed Then No Active subscriptions`() {
        setupAndVerifySuccessfulBookingUpdate()

        operaPresenter.destroy()

        assertThat(subscriptions.size()).isEqualTo(0)

        verifyNoMoreInteractions(view)
    }

    @Test
    fun `Given Presenter is active When User selects rateUs option Then app navigates to Playstore`() {
        setupAndVerifySuccessfulBookingUpdate()

        appRatingOkAction.onNext("")

        verify(firebaseLogger).logEvent(eq(FirebaseLogger.Event.APP_RATING), any())

        verify(view).navigateToPlaystore(any())
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `Given Presenter is active When User selects feedback option Then app opens email feedback prompt`() {
        setupAndVerifySuccessfulBookingUpdate()

        appRatingFeedbackAction.onNext("")

        verify(view).showFeedbackPrompt(any(), any(), any())
        verify(firebaseLogger).logEvent(eq(FirebaseLogger.Event.APP_RATING), any())
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `Given Presenter is active When User cancels appRating Then action is tracked`() {
        setupAndVerifySuccessfulBookingUpdate()

        appRatingCancelAction.onNext("")

        verify(firebaseLogger).logEvent(FirebaseLogger.Event.APP_RATING_CANCELLATION)
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `Given Presenter is active When AppRating condition is met Then AppRating modal is shown`() {
        setupAndVerifySuccessfulBookingUpdate()

        appRatingTrigger.onSuccess(true)
        rxRule.testScheduler.advanceTimeBy(3, java.util.concurrent.TimeUnit.SECONDS)
        verify(view).showAppRatingPrompt()
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `Given Presenter is active When AppRating condition is not met Then AppRating modal is never shown`() {
        setupAndVerifySuccessfulBookingUpdate()

        appRatingTrigger.onSuccess(false)
        rxRule.testScheduler.advanceTimeBy(3, java.util.concurrent.TimeUnit.SECONDS)

        verify(view, org.mockito.kotlin.never()).showAppRatingPrompt()
        verifyNoMoreInteractions(view)
    }


    private fun verifyAllViewInitialInteractionsAndSubscriptionsForOpera() {
        verify(view).onAddToCalendarClick()
        verify(view).onSendInvoiceClicked()
        verify(view).onCheckInOnlineButtonClick()
        verify(view).onReadyToLeaveButtonClick()
        verify(view).onDirectionClicked()
        verify(view).onFaqButtonClicked()
        verify(view).onRoomKeyInstructionsButtonClicked()
        verify(view).onHotelNameClick()
        verify(view).onManageBookingClicked()
        verify(view).onAppRatingFeedbackClicked()
        verify(view).onAppRatingPromptCancelClicked()
        verify(view).onAppRatingPromptOkClicked()
        verify(view).onParkingButtonClicked()
        verify(view).onSendInvoiceDialogClicked()
        assertThat(subscriptions.size()).isEqualTo(18)
    }

    private fun verifyAllViewInitialInteractionsAndSubscriptionsOnFailureOfBookingUpdate() {
        verify(view).onAddToCalendarClick()
        verify(view).onSendInvoiceClicked()
        verify(view).onCheckInOnlineButtonClick()
        verify(view).onReadyToLeaveButtonClick()
        verify(view).onDirectionClicked()
        verify(view).onFaqButtonClicked()
        verify(view).onRoomKeyInstructionsButtonClicked()
        verify(view).onManageBookingClicked()
        verify(view).onAppRatingFeedbackClicked()
        verify(view).onAppRatingPromptCancelClicked()
        verify(view).onAppRatingPromptOkClicked()
        verify(view).onParkingButtonClicked()
        verify(view).onSendInvoiceDialogClicked()
        assertThat(subscriptions.size()).isEqualTo(16)
    }

    private fun verifyAllViewInitialInteractionsAndSubscriptionsOnFailureOfHotelInfo() {
        verify(view).onAddToCalendarClick()
        verify(view).onSendInvoiceClicked()
        verify(view).onCheckInOnlineButtonClick()
        verify(view).onReadyToLeaveButtonClick()
        verify(view).onDirectionClicked()
        verify(view).onFaqButtonClicked()
        verify(view).onRoomKeyInstructionsButtonClicked()
        verify(view).onManageBookingClicked()
        verify(view).onAppRatingFeedbackClicked()
        verify(view).onAppRatingPromptCancelClicked()
        verify(view).onAppRatingPromptOkClicked()
        verify(view).onParkingButtonClicked()
        verify(view).onSendInvoiceDialogClicked()
        assertThat(subscriptions.size()).isEqualTo(16)
    }

    private fun setupAndVerifySuccessfulBookingUpdate() {
        mock<GetBookingUpdates> {
            on { getBookingUpdates.execute(operaBookingReferenceStub) } doReturn Flowable.just(
                OPERA_EXPECTED_BOOKING_1)
        }

        mock<GraphQLFindBookingUseCase> {
            on { graphQLBookingDetailsUseCase.getHotelInfo(
                deviceLocaleProvider.getDeviceLocale().country.lowercase(),
                deviceLocaleProvider.getDeviceLanguage(),
                OPERA_EXPECTED_BOOKING_1.hotelCode) } doReturn Observable.just(hotelInfoDomain) }


        whenever(graphQLBookingDetailsUseCase.getHotelInfo(
            deviceLocaleProvider.getDeviceLocale().country.lowercase(),
            deviceLocaleProvider.getDeviceLanguage(),
            OPERA_EXPECTED_BOOKING_1.hotelCode)).thenReturn(Observable.just(hotelInfoDomain)
        )

        mock<GraphQLBookingDetailsUseCase> {
            on { graphQLBookingDetailsUseCase.bookingConfirmationAndManageBooking(
                any(),
                any(),
                any(),
                anyOrNull(),
                any(),
                any()) } doReturn Observable.just(
                EXPECTED_OPERA_BOOKING_1_WITH_DETAILS) }

        operaPresenter.attachView(view)
        runWithTestScheduler(rxRule)
        verify(view).shouldShowLoadingSpinner(true)

        verifyAllViewInitialInteractionsAndSubscriptionsForOpera()

        verify(view, times(0)).showAmendBookingInfoBanner(anyString(), any())

        verify(view).bindBookingState(
            BookingDetailsState(Success, bookingUiModel =
                expectedOperaUiModel(stringResourceProvider, deviceLocaleProvider, isCheckInOnlineEnabledUseCase)
            ), storage, businessStorage, amendTotalMock, "",
            "",
            PriceDomain.createDefault(),
            deviceLocaleProvider,
            EMPTY_STRING_DOMAIN,
            emptyList(),
            false
        )

        verify(view).bindBookingState(
            BookingDetailsState(InFlight, bookingUiModel =
                expectedOperaUiModel(stringResourceProvider, deviceLocaleProvider, isCheckInOnlineEnabledUseCase)),
            storage,
            businessStorage,
            amendTotalMock,
            "",
            "",
            PriceDomain.createDefault(),
            deviceLocaleProvider,
            EMPTY_STRING_DOMAIN,
            emptyList(),
            false
        )

        verify(view).bindBookingState(
            BookingDetailsState(Success,
                bookingUiModel = expectedOperaUiModel(stringResourceProvider, deviceLocaleProvider, isCheckInOnlineEnabledUseCase),
                bookingDetails = operaBookingDetailsUiMapper.apply(Pair
                    (OPERA_EXPECTED_BOOKING_1, hotelInfoDomain))),
            storage,
            businessStorage,
            amendTotalMock,
            "from 3pm",
            "before 12pm",
            PriceDomain.createDefault(),
            deviceLocaleProvider,
            EMPTY_STRING_DOMAIN,
            emptyList(),
            false
        )

        verify(view, times(0)).showAmendBookingInfoBanner(anyString(), any())
        verify(view).shouldShowLoadingSpinner(false)
        verifyNoMoreInteractions(view)
    }

    @Test
    fun `Given third party booking When booking details loaded Then analytics includes third party flag and booking reference`() {
        val thirdPartyBooking = TestData.OPERA_EXPECTED_THIRD_PARTY_BOOKING

        mock<GetBookingUpdates> {
            on { getBookingUpdates.execute(operaBookingReferenceStub) } doReturn Flowable.just(thirdPartyBooking)
        }

        mock<GraphQLBookingDetailsUseCase> {
            on { graphQLBookingDetailsUseCase.getHotelInfo(
                deviceLocaleProvider.getDeviceLocale().country.lowercase(),
                deviceLocaleProvider.getDeviceLanguage(),
                thirdPartyBooking.hotelCode) } doReturn Observable.just(hotelInfoDomain)
            on { graphQLBookingDetailsUseCase.bookingConfirmationAndManageBooking(
                any(), any(), any(), anyOrNull(), any(), any())
            } doReturn Observable.just(TestData.EXPECTED_THIRD_PARTY_BOOKING_WITH_DETAILS)
        }

        operaPresenter.attachView(view)
        runWithTestScheduler(rxRule)

        val analyticsCaptor = argumentCaptor<AnalyticsData>()
        verify(trackingAnalytics).track(eq("Booking Details"), analyticsCaptor.capture())

        val analyticsData = analyticsCaptor.firstValue as BookingDetailsAnalyticsData
        assertThat(analyticsData.isThirdPartyBooking).isTrue()
        assertThat(analyticsData.thirdPartyBookingId).isEqualTo(thirdPartyBooking.bookingReference)
    }

    @Test
    fun `Given regular non-third-party booking When booking details loaded Then analytics shows no third party booking ID`() {
        val regularBooking = OPERA_EXPECTED_BOOKING_1.copy(isThirdPartyBooking = false)

        mock<GetBookingUpdates> {
            on { getBookingUpdates.execute(operaBookingReferenceStub) } doReturn Flowable.just(regularBooking)
        }

        mock<GraphQLBookingDetailsUseCase> {
            on { graphQLBookingDetailsUseCase.getHotelInfo(
                deviceLocaleProvider.getDeviceLocale().country.lowercase(),
                deviceLocaleProvider.getDeviceLanguage(),
                regularBooking.hotelCode) } doReturn Observable.just(hotelInfoDomain)
            on { graphQLBookingDetailsUseCase.bookingConfirmationAndManageBooking(
                any(), any(), any(), anyOrNull(), any(), any())
            } doReturn Observable.just(EXPECTED_OPERA_BOOKING_1_WITH_DETAILS)
        }

        operaPresenter.attachView(view)
        runWithTestScheduler(rxRule)

        val analyticsCaptor = argumentCaptor<AnalyticsData>()
        verify(trackingAnalytics).track(eq("Booking Details"), analyticsCaptor.capture())

        val analyticsData = analyticsCaptor.firstValue as BookingDetailsAnalyticsData
        assertThat(analyticsData.isThirdPartyBooking).isFalse()
        assertThat(analyticsData.thirdPartyBookingId).isNull()
    }

    internal object TestData {
        val OPERA_EXPECTED_BOOKING_1 = Booking(
            bookingReference = "BANBRI0379120",
            leadGuestFullName = "Mrs Tester Testerson",
            arrivalDate = LocalDate.of(2018, 6, 6),
            departureDate = LocalDate.of(2018, 6, 8),
            hotelName = "Bangor (Gwynedd, North Wales)",
            isBusinessBooking = false,
            hotelCode = "BANBRI",
            numberOfRooms = 1,
            totalCost = PriceDomain(145f, "GBP"),
            balanceOutstanding = null,
            rateType = "Saver",
            leadGuestSurname = "Testerson",
            amendRestrictions = AmendRestrictions.createWithDefaults())

        val EXPECTED_OPERA_BOOKING_1_WITH_DETAILS = OPERA_EXPECTED_BOOKING_1
            .copy(isBusinessBooking = false, details = Booking.Details(business = false, cityTax = null, rateName = "Non-FLex"),
                balanceOutstanding = PriceDomain(145f, GBP))

        val OPERA_EXPECTED_BOOKING_1_AMENDABLE = OPERA_EXPECTED_BOOKING_1.copy(amendable = true)
        val EXPECTED_OPERA_BOOKING_1_WITH_DETAILS_AMENDABLE = EXPECTED_OPERA_BOOKING_1_WITH_DETAILS.copy(amendable = true)

        val OPERA_EXPECTED_BOOKING_2 = OPERA_EXPECTED_BOOKING_1.copy(leadGuestSurname = "NewSurname")

        val OPERA_EXPECTED_THIRD_PARTY_BOOKING = OPERA_EXPECTED_BOOKING_1.copy(
            bookingReference = "TPBK123456",
            isThirdPartyBooking = true
        )

        val EXPECTED_THIRD_PARTY_BOOKING_WITH_DETAILS = OPERA_EXPECTED_THIRD_PARTY_BOOKING.copy(
            details = Booking.Details(business = false, cityTax = null, rateName = "Flex"),
            balanceOutstanding = PriceDomain(145f, GBP)
        )

        fun expectedOperaUiModel(stringProvider: StringResourceProvider,
                                 deviceLocaleProvider: DeviceLocaleProvider,
                                 isCheckInOnlineEnabledUseCase: IsCheckInOnlineEnabledUseCase): BookingBasicsUiModel =
            BookingUiModelMapper(
                stringProvider,
                deviceLocaleProvider,
                isCheckInOnlineEnabledUseCase
            ).apply(OPERA_EXPECTED_BOOKING_1)


        val mockHotelPackagesRequestBody = HotelPackagesRequestBody(
            "BANBRI", "2022-06-05", "2022-06-07", 1, 0, 1,
            "en", "gb", "booking-nm-a1", channel = Channel.PI)
    }

    @After
    fun clean(){
        Dispatchers.resetMain()
        testDispatcher.cancel()
    }
}

private fun runWithTestScheduler(rule: TestSchedulerRule) {
    rule.testScheduler.triggerActions()
}