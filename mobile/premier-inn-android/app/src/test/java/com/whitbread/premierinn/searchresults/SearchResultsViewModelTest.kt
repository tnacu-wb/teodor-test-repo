package com.whitbread.premierinn.searchresults

import androidx.lifecycle.SavedStateHandle
import com.google.common.truth.Truth.assertThat
import com.whitbread.premierinn.R
import com.whitbread.premierinn.api.response.ErrorBody
import com.whitbread.premierinn.api.response.InstanceFactory
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.common.AppConfiguration
import com.whitbread.premierinn.common.CoronavirusFlags
import com.whitbread.premierinn.common.StringResourceProvider
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.SEARCH_RESULTS_LIST_NAME
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.SEARCH_RESULTS_MAP_NAME
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.SEARCH_RESULTS_NO_RESULTS_FOUND
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type.LOOK_TO_BOOK
import com.whitbread.premierinn.common.analytics.CampaignDataModel
import com.whitbread.premierinn.common.analytics.FirebaseLogger
import com.whitbread.premierinn.common.analytics.FirebaseParams
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.data.graphql.mapper.mapToHotelAvailabilitiesGQL
import com.whitbread.premierinn.data.remote.graphql.contracts.HotelAvailabilitiesGraphQLContract
import com.whitbread.premierinn.domain.common.Availability.OK
import com.whitbread.premierinn.domain.common.Availability.SOLD_OUT
import com.whitbread.premierinn.domain.common.BOOKING_CHANNEL_MOBILE
import com.whitbread.premierinn.domain.common.BookingCriteria
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.HomepageBannerDomain
import com.whitbread.premierinn.domain.common.OPERA_SOURCE
import com.whitbread.premierinn.domain.common.PromoContentDomain
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.common.SUB_CHANNEL
import com.whitbread.premierinn.domain.common.SrpBannerDomain
import com.whitbread.premierinn.domain.common.TermsDomain
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.graphql.promotions.entity.PromotionsInformationDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelAvailabilitiesRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Place
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Room
import com.whitbread.premierinn.domain.graphql.srp.entity.HotelAvailabilitiesWithPromotionsDomain
import com.whitbread.premierinn.domain.graphql.srp.usecase.GraphQLSRPUseCase
import com.whitbread.premierinn.domain.hotel.entity.Hotel.Brand.PI
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import com.whitbread.premierinn.domain.search.entity.Location
import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem
import com.whitbread.premierinn.landing.model.CODE_FREE_BREAKFAST_INCENTIVE
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.ChangeModeStateEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.ScreenFirstLaunchEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.ShowFallbackOrHDPEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewState.Mode.LIST_AND_MAP
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData
import com.whitbread.premierinn.utils.TestDataFactory.randomDouble
import com.whitbread.premierinn.utils.TestDataFactory.randomInt
import com.whitbread.premierinn.utils.TestDataFactory.randomUuid
import com.whitbread.premierinn.utils.TestSchedulerRule
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.threeten.bp.LocalDate
import java.util.Collections
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 *
 */
class SearchResultsViewModelTest {
    @get:Rule
    val rxRule = TestSchedulerRule()
    private val adobeAnalytics = mock(TrackingAnalytics::class.java)
    private val firebaseAnalytics = mock(FirebaseLogger::class.java)
    private val logger = mock(LogService::class.java)
    private val isFeatureOn = mock(IsFeatureOn::class.java)
    private val businessPersistenceManager = mock(BusinessPersistenceManager::class.java)
    private val simplePersistenceManager = mock(SimplePersistenceManager::class.java)
    private val coronaVirusFlags = CoronavirusFlags
    private val getStringResource = mock(GetStringResource::class.java)
    private val viewEvents: PublishSubject<SearchResultsViewEvent> = PublishSubject.create()
    private val resourceProvider = mock(StringResourceProvider::class.java)
    private val deviceLocaleProvider = mock(DeviceLocaleProvider::class.java)
    private val appConfiguration = mock(AppConfiguration::class.java)
    lateinit var searchResultInputForNoHotels: SearchResultsInput
    lateinit var searchResultInputForError: SearchResultsInput
    lateinit var searchResultInputForSuccess: SearchResultsInput
    lateinit var searchResultInputForSoldOut: SearchResultsInput
    private val savedStateHandleMock1: SavedStateHandle = mockk()
    private val savedStateHandleMock2: SavedStateHandle = mockk()

    private val viewModelOldSrp by lazy {
        SearchResultsViewModel(
            savedStateHandleMock1,
            bookingCriteriaMapper,
            hotelItemMapper,
            adobeAnalytics,
            firebaseAnalytics,
            logger,
            isFeatureOn,
            simplePersistenceManager,
            businessPersistenceManager,
            getStringResource,
            resourceProvider,
            deviceLocaleProvider,
            graphQLSRPUseCase,
            appConfiguration
        )
    }

    private val viewModelWithPlaceId by lazy {
        SearchResultsViewModel(
            savedStateHandleMock1,
            bookingCriteriaMapper,
            hotelItemMapper,
            adobeAnalytics,
            firebaseAnalytics,
            logger,
            isFeatureOn,
            simplePersistenceManager,
            businessPersistenceManager,
            getStringResource,
            resourceProvider,
            deviceLocaleProvider,
            graphQLSRPUseCase,
            appConfiguration
        )
    }

    private val viewModelWithLatLong by lazy {
        SearchResultsViewModel(
            savedStateHandleMock2,
            bookingCriteriaMapper,
            hotelItemMapper,
            adobeAnalytics,
            firebaseAnalytics,
            logger,
            isFeatureOn,
            simplePersistenceManager,
            businessPersistenceManager,
            getStringResource,
            resourceProvider,
            deviceLocaleProvider,
            graphQLSRPUseCase,
            appConfiguration
        )
    }

    @Before
    fun setUp() {
        whenever(businessPersistenceManager.getOperaCompanyId()).thenReturn(null)
        whenever(deviceLocaleProvider.getDeviceLocale()).thenReturn(Locale.UK)
        whenever(deviceLocaleProvider.getBookingChannel()).thenReturn(BOOKING_CHANNEL_MOBILE)
        whenever(deviceLocaleProvider.getDeviceLanguage()).thenReturn("en")
        whenever(getStringResource.invoke(ContentManagedResourceRepository.Key.OPERA_FALLBACK_POPUP_DETAILS))
            .thenReturn("{" +
                    "\"operaFallbackAlertTitle\": \"Dieses Hotel ist nur auf unserer Website aufrufbar\"," +
                    "\"operaFallbackAlertMessage\": \"Möchten Sie die Hotelinformationen auf premierinn.com/ premierinn.de ansehen?\"," +
                    "\"operaFallbackAlertClose\": \"Schließen\"," +
                    "\"operaFallbackAlertContinue\": \"Fortfahren\"}")

        every { savedStateHandleMock1.get<String>(PLACE_ID) } returns "ChIJIyaYpQC4h0gRJxfnfHsU8mQ"
        every { savedStateHandleMock1.get<SearchResultsInput>(EXTRA_SRP_INPUT)?.campaignModel() } returns CampaignDataModel("CID_TEST", "GID_TEST","MID_TEST")
        every { savedStateHandleMock1.get<SearchResultsInput>(EXTRA_SRP_INPUT)?.trackingCode() } returns "TRACKING_CODE_TEST"

        every { savedStateHandleMock2.get<String>(PLACE_ID) } returns ""
        every { savedStateHandleMock2.get<SearchResultsInput>(EXTRA_SRP_INPUT)?.campaignModel() } returns CampaignDataModel("CID_TEST", "GID_TEST","MID_TEST")
        every { savedStateHandleMock2.get<SearchResultsInput>(EXTRA_SRP_INPUT)?.trackingCode() } returns ""

        searchResultInputForNoHotels = SearchResultsInput.builder()
            .adults(listOf(1))
            .infants(emptyList())
            .children(listOf(1))
            .infants(listOf(1))
            .roomTypeCodes(listOf("DB"))
            .cots(listOf(false))
            .placeName("London")
            .numRooms(1)
            .arrivalDate(LocalDate.of(2023, 11, 20))
            .departureDate(LocalDate.of(2023, 11, 22))
            .latitude(51.527736f)
            .longitude(-0.129068f)
            .build()

        searchResultInputForError = SearchResultsInput.builder()
            .adults(listOf(1))
            .infants(emptyList())
            .children(listOf(1))
            .infants(listOf(1))
            .roomTypeCodes(listOf("DB"))
            .cots(listOf(false))
            .placeName("London")
            .numRooms(1)
            .arrivalDate(LocalDate.of(2024, 3, 20))
            .departureDate(LocalDate.of(2024, 3, 22))
            .latitude(51.324646f)
            .longitude(-0.1077176f)
            .build()

        searchResultInputForSuccess = SearchResultsInput.builder()
            .adults(listOf(1))
            .infants(emptyList())
            .children(listOf(1))
            .infants(listOf(1))
            .roomTypeCodes(listOf("DB"))
            .cots(listOf(false))
            .placeName("Edinburgh")
            .numRooms(1)
            .arrivalDate(LocalDate.of(2023, 11, 20))
            .departureDate(LocalDate.of(2023, 11, 22))
            .latitude(51.527736f)
            .longitude(-0.129068f)
            .build()

        searchResultInputForSoldOut = SearchResultsInput.builder()
            .adults(listOf(1))
            .infants(emptyList())
            .children(listOf(1))
            .infants(listOf(1))
            .roomTypeCodes(listOf("DB"))
            .cots(listOf(false))
            .placeName("London holborn")
            .numRooms(1)
            .arrivalDate(LocalDate.of(2023, 11, 20))
            .departureDate(LocalDate.of(2023, 11, 22))
            .latitude(51.527736f)
            .longitude(-0.129068f)
            .build()

        viewModelOldSrp.bind(viewEvents)
    }

    @Ignore
    @Test
    fun `Given a FirstLaunchEvent When Availabilities OK Then 4 ViewStates are emitted`() {
        val testObserver = viewModelOldSrp.viewStates().test()
        viewModelOldSrp.bind(viewEvents)

        viewEvents.onNext(ScreenFirstLaunchEvent(availabilitiesOkAvailability, searchResultInputForSuccess, null))

        rxRule.testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        viewEvents.onNext(SearchResultsViewEvent.ChangeMapCentreEvent(Location(1.0, 1.0)))
        viewEvents.onNext(ScreenFirstLaunchEvent(availabilitiesOkAvailability, searchResultInputForSuccess, null))

        testObserver.assertValueCount(5).assertNoErrors()

        val (initState, loadingState, loadToolbarState, loadAvailabilitiesState) = testObserver.values()

        assertThat(initState.mode).isEqualTo(LIST_AND_MAP)
        assertThat(initState.applyNewUiMode).isTrue()
        assertThat(initState.sortingFiltersShown).isTrue()
        assertThat(initState.applyMapBoundsChange).isTrue()
        assertThat(initState.isLayoutManagerVertical).isTrue()
        assertThat(initState.applyChangesOnRecyclerView).isTrue()
        assertThat(initState.applyNewMapCenter).isTrue()

        assertThat(loadToolbarState.isLoading).isEqualTo(true)
        assertThat(loadingState.applyVerticalListItemChange).isTrue()
        assertThat(loadingState.isMapLoading).isFalse()
        assertThat(loadingState.verticalListViewItems).containsExactly(LoadingListItem)

        assertThat(loadToolbarState.verticalListViewItems).containsExactly(LoadingListItem)
        assertThat(loadToolbarState.applyToolbarChanges).isEqualTo(true)
        assertThat(loadToolbarState.toolbarTitle).isEqualTo(expectedCriteriaUiItem.searchItemName)
        assertThat(loadToolbarState.toolbarSubTitle).isEqualTo(expectedCriteriaUiItem.bookingCriteriaFormatted)

        assertThat(loadAvailabilitiesState.applyVerticalListItemChange).isEqualTo(true)
        assertThat(loadAvailabilitiesState.verticalListViewItems).isEqualTo(listOf(expectedHotelListItem, expectedHotelListItem2))

        verify(adobeAnalytics).track(eq(SEARCH_RESULTS_LIST_NAME), any<SearchResultsAnalyticsData>())
    }

    @Test
    fun `Given 2 Events And view re-subscribes Then the last ViewState is returned`() {

        viewModelOldSrp.bind(viewEvents)
        val viewSubscriber = viewModelOldSrp.viewStates().test()

        viewEvents.onNext(SearchResultsViewEvent.ChangeMapCentreEvent(Location(1.0, 1.0)))

        viewSubscriber.assertValueCount(2).assertNoErrors()
        viewSubscriber.assertValueAt(0){ it.mapCenter == Location(51.508101, -0.1270057)}
        viewSubscriber.assertValueAt(1){ it.mapCenter == Location(1.0, 1.0)}

        viewSubscriber.dispose()

        val freshViewSubscription = viewModelOldSrp.viewStates().test()
        freshViewSubscription.assertValueCount(1).assertNoErrors()
        freshViewSubscription.assertValueAt(0){ it.mapCenter == Location(1.0, 1.0)}

    }

    @Ignore //TODO: Fails with 3, needs to be checked / refactored
    @Test
    fun `Given 3 Consecutive RequestAvailability events with same params When processed Then Only One gets Consumed`() {
        val testObserver = viewModelOldSrp.viewStates()
            .doOnNext { println(it) }
            .test()

        repeat(3){
            viewEvents.onNext(SearchResultsViewEvent.RequestAvailabilityEvent(availabilitiesOkAvailability, searchResultInputForSuccess))
        }

        testObserver.assertValueCount(3).assertNoErrors()
    }

    @Ignore //TODO: Analytics triggered twice com.whitbread.premierinn.searchresults.SearchResultsViewModel.trackOnErrorIfNeeded
    @Test
    fun `Given a FirstLaunchEvent When No Availabilities Then last ViewState has No Results Error`() {
        viewModelWithPlaceId.bind(viewEvents)

        val testObserver = viewModelWithPlaceId.viewStates().test()

        viewEvents.onNext(ScreenFirstLaunchEvent(availabilitiesNoHotels, searchResultInputForNoHotels, null))

        rxRule.testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        testObserver.assertValueCount(4).assertNoErrors()

        val loadAvailabilitiesState = testObserver.values()[3]
        assertThat(loadAvailabilitiesState.applyVerticalListItemChange).isEqualTo(true)
        assertThat(loadAvailabilitiesState.verticalListViewItems).containsExactly(ErrorItem(errorResId = R.string.search_results_no_hotel_available))

        verify(adobeAnalytics).track(eq(SEARCH_RESULTS_NO_RESULTS_FOUND), eq(LOOK_TO_BOOK))
    }

    @Test
    fun `Given Several RequestAvailabilities Events When Different Type of Error occurs Then ViewStates are emitted with respective messaging`() {
        viewModelWithLatLong.bind(viewEvents)

        val testObserver = viewModelWithLatLong.viewStates()
            .doOnNext { println(it) }
            .test()

        repeat(4){
            viewEvents.onNext(SearchResultsViewEvent.RequestAvailabilityEvent(availabilitiesLatLongError, searchResultInputForError))
            rxRule.testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)
        }

        testObserver.assertValueCount(9).assertNoErrors()

        val (firstErrorViewState, secondErrorViewState, thirdErrorViewState, fourthErrorViewState) = listOf(testObserver.values()[2], testObserver.values()[4], testObserver.values()[6], testObserver.values()[8])

        assertThat(firstErrorViewState.applyVerticalListItemChange).isTrue()
        assertThat(firstErrorViewState.verticalListViewItems).containsExactly(ErrorItem(errorResId = R.string.search_results_error))

        assertThat(secondErrorViewState.applyVerticalListItemChange).isTrue()
        assertThat(secondErrorViewState.verticalListViewItems).containsExactly(ErrorItem(errorResId = R.string.search_results_error))

        assertThat(thirdErrorViewState.applyVerticalListItemChange).isTrue()
        assertThat(thirdErrorViewState.verticalListViewItems).containsExactly(ErrorItem(errorResId = R.string.search_results_error))

        assertThat(fourthErrorViewState.applyVerticalListItemChange).isTrue()
        assertThat(fourthErrorViewState.verticalListViewItems).containsExactly(ErrorItem(errorResId = R.string.search_results_error))

        verify(adobeAnalytics, times(4)).trackError(ErrorBody.create(-1, "Error showing search results"))

        verify(firebaseAnalytics, times(4)).logEvent(FirebaseLogger.Event.GRAPHQL_AVAILABILITIES_CALL, FirebaseParams().apply {
            putString(FirebaseParams.ParamName.SRP_SUCCESS_STATUS, "false")})
    }

    @Ignore //TODO: Analytics triggered twice com.whitbread.premierinn.searchresults.SearchResultsViewModel.trackScreenModeChange
    @Test
    fun `Given a FirstLaunchEvent When Availabilities OK Then last ViewState has list data`() {
        viewModelWithPlaceId.bind(viewEvents)

        val testObserver = viewModelWithPlaceId.viewStates().test()

        viewEvents.onNext(ScreenFirstLaunchEvent(availabilitiesOkAvailability, searchResultInputForSuccess, null))

        rxRule.testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        testObserver.assertValueCount(4).assertNoErrors()

        val loadAvailabilitiesState = testObserver.values()[3]

        assertThat(loadAvailabilitiesState.applyVerticalListItemChange).isEqualTo(true)
        assertThat(loadAvailabilitiesState.verticalListViewItems).isEqualTo(listOf(expectedHotelListItem, expectedHotelListItem2))
        verify(adobeAnalytics).track(eq(SEARCH_RESULTS_LIST_NAME), any<SearchResultsAnalyticsData>())
        verify(firebaseAnalytics).logEvent(FirebaseLogger.Event.GRAPHQL_AVAILABILITIES_CALL, FirebaseParams().apply {
            putString(FirebaseParams.ParamName.SRP_SUCCESS_STATUS, "true")})
    }

    @Ignore //TODO: Analytics triggered twice com.whitbread.premierinn.searchresults.SearchResultsViewModel.onAvailabilitiesLoadOpera
    @Test
    fun `Given a FirstLaunchEvent with leisure customer availability for leisure is invoked`() {
        viewModelWithPlaceId.bind(viewEvents)

        viewModelWithPlaceId.viewStates()
            .test()

        viewEvents.onNext(ScreenFirstLaunchEvent(availabilitiesOkAvailability, searchResultInputForSuccess, null))

        rxRule.testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        verify(graphQLSRPUseCase, times(2)).getHotelAvailabilitiesWithPromotions(availabilitiesOkAvailability)
    }

    @Test
    fun `Given a FirstLaunchEvent with business customer availability for business is invoked`() {
        whenever(businessPersistenceManager.getOperaCompanyId()).thenReturn("9408478")

        viewModelWithPlaceId.bind(viewEvents)

        viewModelWithPlaceId.viewStates()
            .test()

        viewEvents.onNext(ScreenFirstLaunchEvent(availabilitiesOkAvailability, searchResultInputForSuccess, null))

        rxRule.testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        verify(graphQLSRPUseCase).getHotelAvailabilitiesWithPromotions(availabilitiesOkAvailabilityBusiness)
    }

    @Test
    fun `GIVEN isAppIncentiveActive true WHEN appIncentiveLogic THEN state contain promoCode and appPromoContent` () {
        val promoCode = CODE_FREE_BREAKFAST_INCENTIVE
        // App Incentive
        whenever(isFeatureOn(ContentManagedResourceRepository.Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED)).thenReturn(true)
        whenever(simplePersistenceManager.isAppPromotionalIncentiveAvailable()).thenReturn(true)

        //App incentive takes precedence over free breakfast & site-wide promo so no need to mock

        whenever(getStringResource.invoke(ContentManagedResourceRepository.Key.APP_PROMO_CODE)).thenReturn(promoCode)
        whenever(getStringResource.invoke(ContentManagedResourceRepository.Key.APP_PROMO_CONTENT)).thenReturn(appContentJson)

        viewModelWithPlaceId.bind(viewEvents)
        val testObserver = viewModelWithPlaceId.viewStates().test()
        viewEvents.onNext(ScreenFirstLaunchEvent(availabilitiesOkAvailability, searchResultInputForSuccess, null))
        rxRule.testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        val initialState = testObserver.values()[0]

        assertThat(initialState.promoCode).isEqualTo(promoCode)
        assertThat(initialState.showPromoFooterBanner).isTrue()
        assertThat(initialState.promoContent).isEqualTo(appPromoContent)
    }

    @Test
    fun `GIVEN appIncentive enabled BUT not available AND site-wide promo AND freeBreakfast inactive WHEN promo logic THEN state has empty promoCode and null promoContent`() {
        // App Incentive
        whenever(isFeatureOn(ContentManagedResourceRepository.Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED)).thenReturn(true)
        whenever(simplePersistenceManager.isAppPromotionalIncentiveAvailable()).thenReturn(false)

        // Site-wide promo as false
        whenever(graphQLSRPUseCase.getHotelAvailabilitiesWithPromotions(any()))
            .thenReturn(Single.just(HotelAvailabilitiesWithPromotionsDomain(availabilitiesOk, defaultPromotionsFalse)))

        // Free breakfast as false
        whenever(simplePersistenceManager.getFreeBreakfastPromotionCode()).thenReturn("")

        viewModelWithPlaceId.bind(viewEvents)
        val testObserver = viewModelWithPlaceId.viewStates().test()
        viewEvents.onNext(ScreenFirstLaunchEvent(availabilitiesOkAvailability, searchResultInputForSuccess, null))
        rxRule.testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        val initialState = testObserver.values()[3]

        assertThat(initialState.promoCode).isEqualTo(EMPTY_STRING_DOMAIN)
        assertThat(initialState.showPromoFooterBanner).isFalse()
        assertThat(initialState.promoContent).isEqualTo(null)
    }


    @Test
    fun `GIVEN appIncentive disabled BUT available AND site-wide promo AND freeBreakfast inactive WHEN promo logic THEN state has empty promoCode and null promoContent`() {
        // App Incentive
        whenever(isFeatureOn(ContentManagedResourceRepository.Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED)).thenReturn(false)
        whenever(simplePersistenceManager.isAppPromotionalIncentiveAvailable()).thenReturn(true)

        // Site-wide promo as false
        whenever(graphQLSRPUseCase.getHotelAvailabilitiesWithPromotions(any()))
            .thenReturn(Single.just(HotelAvailabilitiesWithPromotionsDomain(availabilitiesOk, defaultPromotionsFalse)))

        // Free breakfast as false
        whenever(simplePersistenceManager.getFreeBreakfastPromotionCode()).thenReturn("")

        viewModelWithPlaceId.bind(viewEvents)
        val testObserver = viewModelWithPlaceId.viewStates().test()
        viewEvents.onNext(ScreenFirstLaunchEvent(availabilitiesOkAvailability, searchResultInputForSuccess, null))
        rxRule.testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        val initialState = testObserver.values()[3]

        assertThat(initialState.promoCode).isEqualTo("")
        assertThat(initialState.showPromoFooterBanner).isFalse()
        assertThat(initialState.promoContent).isEqualTo(null)
    }

    @Test
    fun `GIVEN freeBreakfast active AND appIncentive AND site-wide promo inactive WHEN promo logic THEN state has promoCode and promoContent`() {
        val promoCode = CODE_FREE_BREAKFAST_INCENTIVE

        // App Incentive
        whenever(isFeatureOn(ContentManagedResourceRepository.Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED)).thenReturn(false)
        whenever(simplePersistenceManager.isAppPromotionalIncentiveAvailable()).thenReturn(false)

        // Site-wide promo as false
        whenever(graphQLSRPUseCase.getHotelAvailabilitiesWithPromotions(any()))
            .thenReturn(Single.just(HotelAvailabilitiesWithPromotionsDomain(availabilitiesOk, defaultPromotionsFalse)))

        // Free breakfast
        whenever(simplePersistenceManager.getFreeBreakfastPromotionCode()).thenReturn(promoCode)

        whenever(getStringResource.invoke(ContentManagedResourceRepository.Key.FREE_BREAKFAST_PROMO_CONTENT)).thenReturn(appContentJson)

        viewModelWithPlaceId.bind(viewEvents)
        val testObserver = viewModelWithPlaceId.viewStates().test()
        viewEvents.onNext(ScreenFirstLaunchEvent(availabilitiesOkAvailability, searchResultInputForSuccess, null))
        rxRule.testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        val initialState = testObserver.values()[3]

        assertThat(initialState.promoCode).isEqualTo(promoCode)
        assertThat(initialState.showPromoFooterBanner).isTrue()
        assertThat(initialState.promoContent).isEqualTo(appPromoContent)
    }

    @Test
    fun `GIVEN freeBreakfast active AND appIncentive disabled BUT available AND site-wide promo inactive WHEN promo logic THEN state has promoCode and promoContent`() {
        val promoCode = CODE_FREE_BREAKFAST_INCENTIVE

        // App Incentive
        whenever(isFeatureOn(ContentManagedResourceRepository.Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED)).thenReturn(false)
        whenever(simplePersistenceManager.isAppPromotionalIncentiveAvailable()).thenReturn(true)

        // Site-wide promo as false
        whenever(graphQLSRPUseCase.getHotelAvailabilitiesWithPromotions(any()))
            .thenReturn(Single.just(HotelAvailabilitiesWithPromotionsDomain(availabilitiesOk, defaultPromotionsFalse)))

        // Free breakfast
        whenever(simplePersistenceManager.getFreeBreakfastPromotionCode()).thenReturn(promoCode)

        whenever(getStringResource.invoke(ContentManagedResourceRepository.Key.FREE_BREAKFAST_PROMO_CONTENT)).thenReturn(appContentJson)

        viewModelWithPlaceId.bind(viewEvents)
        val testObserver = viewModelWithPlaceId.viewStates().test()
        viewEvents.onNext(ScreenFirstLaunchEvent(availabilitiesOkAvailability, searchResultInputForSuccess, null))
        rxRule.testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        val initialState = testObserver.values()[3]

        assertThat(initialState.promoCode).isEqualTo(promoCode)
        assertThat(initialState.showPromoFooterBanner).isTrue()
        assertThat(initialState.promoContent).isEqualTo(appPromoContent)
    }

    @Test
    fun `GIVEN freeBreakfast active AND appIncentive enabled BUT not available AND site-wide promo inactive WHEN promo logic THEN state has promoCode and promoContent`() {
        val promoCode = CODE_FREE_BREAKFAST_INCENTIVE

        // App Incentive
        whenever(isFeatureOn(ContentManagedResourceRepository.Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED)).thenReturn(true)
        whenever(simplePersistenceManager.isAppPromotionalIncentiveAvailable()).thenReturn(false)

        // Site-wide promo as false
        whenever(graphQLSRPUseCase.getHotelAvailabilitiesWithPromotions(any()))
            .thenReturn(Single.just(HotelAvailabilitiesWithPromotionsDomain(availabilitiesOk, defaultPromotionsFalse)))

        // Free breakfast
        whenever(simplePersistenceManager.getFreeBreakfastPromotionCode()).thenReturn(promoCode)

        whenever(getStringResource.invoke(ContentManagedResourceRepository.Key.FREE_BREAKFAST_PROMO_CONTENT)).thenReturn(appContentJson)

        viewModelWithPlaceId.bind(viewEvents)
        val testObserver = viewModelWithPlaceId.viewStates().test()
        viewEvents.onNext(ScreenFirstLaunchEvent(availabilitiesOkAvailability, searchResultInputForSuccess, null))
        rxRule.testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        val initialState = testObserver.values()[3]

        assertThat(initialState.promoCode).isEqualTo(promoCode)
        assertThat(initialState.showPromoFooterBanner).isTrue()
        assertThat(initialState.promoContent).isEqualTo(appPromoContent)
    }


    @Test
    fun `GIVEN site-wide promo active AND appIncentive unavailable WHEN promo logic THEN state has site-wide promo promoCode and promoContent`() {
        val promoCode = "PROMO123"

        // App Incentive
        whenever(isFeatureOn(ContentManagedResourceRepository.Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED)).thenReturn(false)
        whenever(simplePersistenceManager.isAppPromotionalIncentiveAvailable()).thenReturn(false)

        // Site-wide promo
        whenever(graphQLSRPUseCase.getHotelAvailabilitiesWithPromotions(any()))
            .thenReturn(Single.just(HotelAvailabilitiesWithPromotionsDomain(availabilitiesOk, defaultPromotions)))

        //Site-wide promo takes precedence over free breakfastso no need to mock

        viewModelWithPlaceId.bind(viewEvents)
        val testObserver = viewModelWithPlaceId.viewStates().test()
        viewEvents.onNext(ScreenFirstLaunchEvent(availabilitiesOkAvailability, searchResultInputForSuccess, null))
        rxRule.testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        val initialState = testObserver.values()[3]

        assertThat(initialState.promoCode).isEqualTo(promoCode)
        assertThat(initialState.showPromoFooterBanner).isTrue()
        assertThat(initialState.promoContent).isEqualTo(siteWidePromoContent)
    }

    @Test
    fun `GIVEN site-wide promo active AND appIncentive enabled BUT not available WHEN promo logic THEN state has site-wide promo promoCode and promoContent`() {
        val promoCode = "PROMO123"

        // App Incentive
        whenever(isFeatureOn(ContentManagedResourceRepository.Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED)).thenReturn(true)
        whenever(simplePersistenceManager.isAppPromotionalIncentiveAvailable()).thenReturn(false)

        // Site-wide promo
        whenever(graphQLSRPUseCase.getHotelAvailabilitiesWithPromotions(any()))
            .thenReturn(Single.just(HotelAvailabilitiesWithPromotionsDomain(availabilitiesOk, defaultPromotions)))

        //Site-wide promo takes precedence over free breakfastso no need to mock

        viewModelWithPlaceId.bind(viewEvents)
        val testObserver = viewModelWithPlaceId.viewStates().test()
        viewEvents.onNext(ScreenFirstLaunchEvent(availabilitiesOkAvailability, searchResultInputForSuccess, null))
        rxRule.testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        val initialState = testObserver.values()[3]

        assertThat(initialState.promoCode).isEqualTo(promoCode)
        assertThat(initialState.showPromoFooterBanner).isTrue()
        assertThat(initialState.promoContent).isEqualTo(siteWidePromoContent)
    }

    @Test
    fun `GIVEN site-wide promo active AND appIncentive not enabled but available WHEN promo logic THEN state has site-wide promo promoCode and promoContent`() {
        val promoCode = "PROMO123"

        // App Incentive
        whenever(isFeatureOn(ContentManagedResourceRepository.Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED)).thenReturn(false)
        whenever(simplePersistenceManager.isAppPromotionalIncentiveAvailable()).thenReturn(true)

        // Site-wide promo
        whenever(graphQLSRPUseCase.getHotelAvailabilitiesWithPromotions(any()))
            .thenReturn(Single.just(HotelAvailabilitiesWithPromotionsDomain(availabilitiesOk, defaultPromotions)))

        //Site-wide promo takes precedence over free breakfastso no need to mock

        viewModelWithPlaceId.bind(viewEvents)
        val testObserver = viewModelWithPlaceId.viewStates().test()
        viewEvents.onNext(ScreenFirstLaunchEvent(availabilitiesOkAvailability, searchResultInputForSuccess, null))
        rxRule.testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        val initialState = testObserver.values()[3]

        assertThat(initialState.promoCode).isEqualTo(promoCode)
        assertThat(initialState.showPromoFooterBanner).isTrue()
        assertThat(initialState.promoContent).isEqualTo(siteWidePromoContent)
    }

    @Test
    fun `Given a FirstLaunchEvent with fullyBookedHotelId When Availabilities OK Then ErrorMessage Appears Along with Availabilities`() {
        viewModelWithPlaceId.bind(viewEvents)

        val testObserver = viewModelWithPlaceId.viewStates()
            .doOnNext { println(it) }
            .test()

        viewEvents.onNext(ScreenFirstLaunchEvent(availabilitiesSoldOutAvailability, searchResultInputForSoldOut, "LONLEI"))
        rxRule.testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        testObserver.assertValueCount(5).assertNoErrors()

        val finalState = testObserver.values()[4]

        assertThat(finalState.applyVerticalListItemChange).isTrue()
        assertThat(finalState.verticalListViewItems).isEqualTo(listOf(ErrorItem(R.string.search_results_hotel_fully_booked_message),
            LONLEI_FullyBookedHotelListItem, expectedHotelListItem2))
    }

    @Test
    fun `Given a FirstLaunchEvent with fullyBookedHotelId When Availabilities OK But FullyBookedItem missing in the List Then ErrorMessage Appears Along with Availabilities Without FullyBooked HotelItem`() {
        viewModelWithPlaceId.bind(viewEvents)

        val testObserver = viewModelWithPlaceId.viewStates()
            .doOnNext { println(it) }
            .test()

        viewEvents.onNext(ScreenFirstLaunchEvent(availabilitiesSoldOutAvailability, searchResultInputForSoldOut, "LONSTM"))
        rxRule.testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        testObserver.assertValueCount(5).assertNoErrors()

        val finalState = testObserver.values()[4]

        assertThat(finalState.applyVerticalListItemChange).isTrue()
        assertThat(finalState.verticalListViewItems).isEqualTo(listOf(ErrorItem(R.string.search_results_hotel_fully_booked_message),
            expectedHotelListItem2, LONLEI_FullyBookedHotelListItem))
    }

    @Ignore //TODO: Analytics triggered twice com.whitbread.premierinn.searchresults.SearchResultsViewModel.trackScreenModeChange
    @Test
    fun `Given mode changes from initial mode to Expanded List, no tracking is performed`() {
        viewModelWithPlaceId.bind(viewEvents)

        viewEvents.onNext(ScreenFirstLaunchEvent(availabilitiesOkAvailability, searchResultInputForSuccess, null))
        viewEvents.onNext(ChangeModeStateEvent(SearchResultsViewState.Mode.LIST_EXPANDED))

        rxRule.testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        verify(adobeAnalytics).track(eq(SEARCH_RESULTS_LIST_NAME), any<SearchResultsAnalyticsData>())
        verifyNoMoreInteractions(adobeAnalytics)
    }

    @Ignore //TODO: Analytics triggered twice com.whitbread.premierinn.searchresults.SearchResultsViewModel.trackScreenModeChange
    @Test
    fun `Given mode changes from initial mode to Map, tracking is performed`() {
        viewModelWithPlaceId.bind(viewEvents)

        viewEvents.onNext(ScreenFirstLaunchEvent(availabilitiesOkAvailability, searchResultInputForSuccess, null))

        rxRule.testScheduler.advanceTimeBy(2, TimeUnit.SECONDS)

        viewEvents.onNext(ChangeModeStateEvent(SearchResultsViewState.Mode.FULL_MAP))

        inOrder(adobeAnalytics) {
            verify(adobeAnalytics).track(eq(SEARCH_RESULTS_LIST_NAME), any<SearchResultsAnalyticsData>())
            verify(adobeAnalytics).track(eq(SEARCH_RESULTS_MAP_NAME), any<SearchResultsAnalyticsData>())
        }
    }

    @Test
    fun `WHEN a hotel is selected AND fallback is switched on THEN show fallback for that hotel`() {

        viewModelWithLatLong.bind(viewEvents)

        val testObserver = viewModelWithLatLong.viewStates().test()

        whenever(isFeatureOn(ContentManagedResourceRepository.Key.SHOULD_OPERA_REDIRECT_TO_WEB)).thenReturn(true)

        viewEvents.onNext(ShowFallbackOrHDPEvent(expectedHotelListItem))

        testObserver.assertValueCount(2).assertNoErrors()

        val finalState = testObserver.values()[1]

        assertThat(finalState.showFallbackPopup).isTrue()
    }

    @Test
    fun `WHEN a hotel is selected AND fallback is switched off THEN show HDP for that hotel`() {

        viewModelWithLatLong.bind(viewEvents)

        val testObserver = viewModelWithLatLong.viewStates().test()

        whenever(isFeatureOn(ContentManagedResourceRepository.Key.SHOULD_OPERA_REDIRECT_TO_WEB)).thenReturn(false)

        viewEvents.onNext(ShowFallbackOrHDPEvent(expectedHotelListItem))

        testObserver.assertValueCount(2).assertNoErrors()

        val finalState = testObserver.values()[1]

        assertThat(finalState.openHDPActivity).isTrue()
    }

    private val graphQLSRPUseCase: GraphQLSRPUseCase by lazy {
        mock(GraphQLSRPUseCase::class.java).apply {
            val availabilitiesLimited = InstanceFactory.create< HotelAvailabilitiesGraphQLContract.HotelAvailabilitiesData>(
                HotelAvailabilitiesGraphQLContract.HotelAvailabilitiesData::class.java,
                "apiTest/graphql/hotel_availabilities_srp_limited_avail_gql.json")
                .mapToHotelAvailabilitiesGQL()
            val availabilitiesSoldOut = InstanceFactory.create< HotelAvailabilitiesGraphQLContract.HotelAvailabilitiesData>(
                HotelAvailabilitiesGraphQLContract.HotelAvailabilitiesData::class.java,
                "apiTest/graphql/hotel_availabilities_srp_soldout_gql.json")
                .mapToHotelAvailabilitiesGQL()
            val availabilitiesError = InstanceFactory.create< HotelAvailabilitiesGraphQLContract.HotelAvailabilitiesData>(
                HotelAvailabilitiesGraphQLContract.HotelAvailabilitiesData::class.java,
                "apiTest/graphql/hotel_availabilities_srp_error_gql.json")
                .mapToHotelAvailabilitiesGQL()
            val availabilitiesNoHotels = InstanceFactory.create< HotelAvailabilitiesGraphQLContract.HotelAvailabilitiesData>(
                HotelAvailabilitiesGraphQLContract.HotelAvailabilitiesData::class.java,
                "apiTest/graphql/hotel_availabilities_srp_empty_gql.json")
                .mapToHotelAvailabilitiesGQL()

            whenever(getHotelAvailabilitiesWithPromotions(input = availabilitiesOkAvailability)).thenReturn(
                Single.just(HotelAvailabilitiesWithPromotionsDomain(availabilitiesOk, defaultPromotions)))
            whenever(getHotelAvailabilitiesWithPromotions(input = availabilitiesOkAvailabilityLatLong)).thenReturn(
                Single.just(HotelAvailabilitiesWithPromotionsDomain(availabilitiesOk, defaultPromotions)))
            whenever(getHotelAvailabilitiesWithPromotions(input = availabilitiesLimitedAvailability)).thenReturn(
                Single.just(HotelAvailabilitiesWithPromotionsDomain(availabilitiesLimited, defaultPromotions)))
            whenever(getHotelAvailabilitiesWithPromotions(input = availabilitiesSoldOutAvailability)).thenReturn(
                Single.just(HotelAvailabilitiesWithPromotionsDomain(availabilitiesSoldOut, defaultPromotions)))
            whenever(getHotelAvailabilitiesWithPromotions(input = this@SearchResultsViewModelTest.availabilitiesError)).thenReturn(
                Single.just(HotelAvailabilitiesWithPromotionsDomain(availabilitiesError, null)))
            whenever(getHotelAvailabilitiesWithPromotions(input = availabilitiesLatLongError)).thenReturn(
                Single.just(HotelAvailabilitiesWithPromotionsDomain(availabilitiesError, null)))
            whenever(getHotelAvailabilitiesWithPromotions(input = this@SearchResultsViewModelTest.availabilitiesNoHotels)).thenReturn(
                Single.just(HotelAvailabilitiesWithPromotionsDomain(availabilitiesNoHotels, null)))
        }
    }

    private val availabilitiesOk = InstanceFactory.create<HotelAvailabilitiesGraphQLContract.HotelAvailabilitiesData>(
        HotelAvailabilitiesGraphQLContract.HotelAvailabilitiesData::class.java,
        "apiTest/graphql/hotel_availabilities_srp_ok_avail_gql.json"
    ).mapToHotelAvailabilitiesGQL()
    private val randomHotelAvailability by lazy {
        InstanceFactory.create< HotelAvailabilitiesGraphQLContract.HotelAvailabilitiesData>(
            HotelAvailabilitiesGraphQLContract.HotelAvailabilitiesData::class.java,
            "apiTest/graphql/hotel_availabilities_srp_ok_avail_gql.json")
            .mapToHotelAvailabilitiesGQL().multiHotelAvailabilities?.get(0)!!
    }

    private val randomHotelAvailability2 by lazy {
        InstanceFactory.create< HotelAvailabilitiesGraphQLContract.HotelAvailabilitiesData>(
            HotelAvailabilitiesGraphQLContract.HotelAvailabilitiesData::class.java,
            "apiTest/graphql/hotel_availabilities_srp_limited_avail_gql.json")
            .mapToHotelAvailabilitiesGQL().multiHotelAvailabilities?.get(1)!!
    }

    private val fullyBookedHotelAvailability by lazy {
        InstanceFactory.create< HotelAvailabilitiesGraphQLContract.HotelAvailabilitiesData>(
            HotelAvailabilitiesGraphQLContract.HotelAvailabilitiesData::class.java,
            "apiTest/graphql/hotel_availabilities_srp_soldout_gql.json")
            .mapToHotelAvailabilitiesGQL().multiHotelAvailabilities?.get(0)!!
    }

    private val expectedHotelListItem by lazy {
        HotelListItem(name = randomUuid(), location = Location(randomDouble(), randomDouble()), type = randomInt(), brand = PI,
            code = "LONLEI", imagePath = randomUuid(), formattedDistance = randomUuid(), availability = OK, priceFrom = randomUuid(),
            distance = randomDouble(), parkingDrawableDescription = null, fullyBookedText = null, tripAdvisorRating = null, flag = null,
            pmsSource = OPERA_SOURCE, cellCode = EMPTY_STRING_DOMAIN)
    }

    private val expectedHotelListItem2 by lazy {
        HotelListItem(name = randomUuid(), location = Location(randomDouble(), randomDouble()), type = randomInt(), brand = PI,
            code = "LONSTM", imagePath = randomUuid(), formattedDistance = randomUuid(), availability = OK, priceFrom = randomUuid(),
            distance = randomDouble(), parkingDrawableDescription = null, fullyBookedText = null, tripAdvisorRating = null, flag = null,
            pmsSource = OPERA_SOURCE, cellCode = EMPTY_STRING_DOMAIN)
    }

    private val LONLEI_FullyBookedHotelListItem by lazy {
        HotelListItem(name = randomUuid(), location = Location(randomDouble(), randomDouble()), type = randomInt(), brand = PI,
            code = "LONLEI", imagePath = randomUuid(), formattedDistance = randomUuid(), availability = SOLD_OUT, priceFrom = randomUuid(),
            distance = randomDouble(), parkingDrawableDescription = null, fullyBookedText = null, tripAdvisorRating = null, flag = null,
            pmsSource = OPERA_SOURCE, cellCode = EMPTY_STRING_DOMAIN)
    }

    private val bookingCriteriaMapper: BookingCriteriaMapper by lazy {
        mock(BookingCriteriaMapper::class.java).apply {
            whenever(apply(bookingCriteriaForSuccess)).thenReturn(expectedCriteriaUiItem)
            whenever(apply(bookingCriteriaForNoList)).thenReturn(expectedCriteriaUiItem)
            whenever(apply(bookingCriteriaForError)).thenReturn(expectedCriteriaUiItem)
            whenever(apply(bookingCriteriaForSuccessFullyBooked)).thenReturn(expectedCriteriaUiItem)
            whenever(apply(searchResultInputForNoHotels.toBookingCriteria())).thenReturn(expectedCriteriaUiItem)
            whenever(apply(searchResultInputForError.toBookingCriteria())).thenReturn(expectedCriteriaUiItem)
            whenever(apply(searchResultInputForSuccess.toBookingCriteria())).thenReturn(expectedCriteriaUiItem)
            whenever(apply(searchResultInputForSoldOut.toBookingCriteria())).thenReturn(expectedCriteriaUiItem)
        }
    }

    private val hotelItemMapper: HotelItemMapper by lazy {
        mock(HotelItemMapper::class.java).apply {
            whenever(apply(randomHotelAvailability)).thenReturn(expectedHotelListItem)
            whenever(apply(randomHotelAvailability2)).thenReturn(expectedHotelListItem2)
            whenever(apply(fullyBookedHotelAvailability)).thenReturn(LONLEI_FullyBookedHotelListItem)
        }
    }

    private val expectedMapCenterLocation = Location(1.0, 1.0)
    private val searchItem = SearchSuggetionItem(name = "London", location = expectedMapCenterLocation, type = SearchSuggetionItem.Type.LOCATION)
    private val arrivalDate = LocalDate.of(2018, 12, 4) // -> 4/12/2018

    private val bookingCriteriaForSuccess = BookingCriteria(arrivalDate = arrivalDate, numberOfNights = 4, roomsCriteria = listOf(RoomCriteria(2, 0, 0, false, RoomType.DOUBLE, roomNumber = 1)), searchCriteria = searchItem)
    private val bookingCriteriaForSuccessFullyBooked = BookingCriteria(arrivalDate = arrivalDate, numberOfNights = 6, roomsCriteria = listOf(RoomCriteria(2, 0, 0, false, RoomType.DOUBLE, roomNumber = 1)), searchCriteria = searchItem)

    private val bookingCriteriaForNoList = BookingCriteria(arrivalDate = arrivalDate, numberOfNights = 2, roomsCriteria = emptyList(), searchCriteria = searchItem)
    private val bookingCriteriaForError = BookingCriteria(arrivalDate = arrivalDate, numberOfNights = 3, roomsCriteria = emptyList(), searchCriteria = searchItem)
    private val expectedCriteriaUiItem = BookingCriteriaUi(searchItemName = "London", bookingCriteriaFormatted = "formatted", searchedLocation = expectedMapCenterLocation)

    private val availabilitiesOkAvailability = HotelAvailabilitiesRequestBody(
        Place("ChIJIyaYpQC4h0gRJxfnfHsU8mQ", "PLACEID", "MILES", 30),
        "2024-06-05", "2024-06-10", Collections.singletonList(Room("DB", 1, 0)),
        emptyList(), "gb", "en", SUB_CHANNEL, Channel.PI.name, SUB_CHANNEL,
        "DISTANCE", 1, 40, 40, null)

    private val availabilitiesOkAvailabilityBusiness = HotelAvailabilitiesRequestBody(
        Place("ChIJIyaYpQC4h0gRJxfnfHsU8mQ", "PLACEID", "MILES", 30),
        "2024-06-05", "2024-06-10", Collections.singletonList(Room("DB", 1, 0)),
        emptyList(), "gb", "en", SUB_CHANNEL, Channel.BB.name, SUB_CHANNEL,
        "DISTANCE", 1, 40, 40, "9408478")

    private val availabilitiesOkAvailabilityLatLong = HotelAvailabilitiesRequestBody(
        Place("53.96206,-1.07888", "LATLONG", "MILES", 30),
        "2024-06-05", "2024-06-10", Collections.singletonList(Room("DB", 1, 0)),
        emptyList(), "gb", "en", SUB_CHANNEL, Channel.PI.name, SUB_CHANNEL,
        "DISTANCE", 1, 40, 40, null)

    private val availabilitiesLimitedAvailability = HotelAvailabilitiesRequestBody(
        Place("53.96206,-1.07888", "LATLONG", "MILES", 30),
        "2024-10-04", "2024-10-05", Collections.singletonList(Room("DB", 1, 0)),
        emptyList(), "gb", "en", SUB_CHANNEL, Channel.PI.name, SUB_CHANNEL,
        "DISTANCE", 1, 40, 10, null)

    private val availabilitiesSoldOutAvailability = HotelAvailabilitiesRequestBody(
        Place("ChIJIyaYpQC4h0gRJxfnfHsU8mQ", "PLACEID", "MILES", 30),
        "2024-08-11", "2024-08-14", Collections.singletonList(Room("DB", 1, 0)),
        emptyList(), "gb", "en", SUB_CHANNEL, Channel.PI.name, SUB_CHANNEL,
        "DISTANCE", 1, 40, 40, null)

    private val availabilitiesLatLongError = HotelAvailabilitiesRequestBody(
        Place("51.324646,-0.1077176", "LATLONG", "MILES", 30),
        "2024-03-20", "2024-03-22", Collections.singletonList(Room("DB", 1, 0)),
        emptyList(), "gb", "en", SUB_CHANNEL, Channel.PI.name, SUB_CHANNEL,
        "DISTANCE", 1, 40, 40, null)

    private val availabilitiesError = HotelAvailabilitiesRequestBody(
        Place("ChIJIyaYpQC4h0gRJxfnfHsU8mQ", "PLACEID", "MILES", 30),
        "2024-10-20", "2024-10-22", Collections.singletonList(Room("DB", 1, 0)),
        emptyList(), "gb", "en", SUB_CHANNEL, Channel.PI.name, SUB_CHANNEL,
        "DISTANCE", 1, 40, 40, null)

    private val availabilitiesNoHotels = HotelAvailabilitiesRequestBody(
        Place("ChIJIyaYpQC4h0gRJxfnfHsU8mQ", "PLACEID", "MILES", 30),
        "2024-10-04", "2024-10-05", Collections.singletonList(Room("DB", 1, 0)),
        emptyList(), "gb", "en", SUB_CHANNEL, Channel.PI.name, SUB_CHANNEL,
        "DISTANCE", 1, 40, 40, null)

    private val appContentJson = "{ \"homepageBanner\":" +
            " { " +
            "\"title\": \"First-time offer\"," +
            " \"datePrefixText\": \"Book by\"," +
            " \"date\": \"11 October 2025\"," +
            " \"discountAmount\": \"10\"," +
            " \"discountPercentageSign\": \"%\"," +
            " \"discountText\": \"off\"," +
            " \"offerDescription\": \"your stay with us\"," +
            " \"buttonText\": \"Book a stay\"," +
            " \"disclaimer\": \"This discount is valid for the standard rate only.\"," +
            " \"terms\": " +
            "{ " +
            "\"text\": \"Terms and conditions\"," +
            " \"url\": \"https://www.premierinn.com/gb/en/terms.html?INTCMP=And_termsAndConditions\" " +
            "}" +
            " }," +
            " \"srpBanner\":" +
            " { " +
            "\"title\": \"First-time offer - 10% off\"," +
            " \"date\": \"11 Oct 25\"," +
            " \"subTitle\": \"Book by %1\$s, standard rates only. Select a hotel to see your savings. %2\$s apply.\"," +
            " \"terms\": " +
            "{" +
            " \"text\": \"T&Cs\"," +
            " \"url\": \"https://www.premierinn.com/gb/en/terms.html?INTCMP=And_termsAndConditions\"" +
            " } " +
            "} " +
            "}"

    private val appPromoContent = PromoContentDomain(
        homepageBanner = HomepageBannerDomain(
            title = "First-time offer",
            datePrefixText = "Book by",
            date = "11 October 2025",
            discountAmount = "10",
            discountPercentageSign = "%",
            discountText = "off",
            offerDescription = "your stay with us",
            buttonText = "Book a stay",
            disclaimer = "This discount is valid for the standard rate only.",
            terms = TermsDomain(
                text = "Terms and conditions",
                url = "https://www.premierinn.com/gb/en/terms.html?INTCMP=And_termsAndConditions"
            )
        ),
        srpBanner =
            SrpBannerDomain(
                title = "First-time offer - 10% off",
                date = "11 Oct 25",
                subTitle = "Book by %1\$s, standard rates only. Select a hotel to see your savings. %2\$s apply.",
                terms = TermsDomain(
                    text = "T&Cs",
                    url = "https://www.premierinn.com/gb/en/terms.html?INTCMP=And_termsAndConditions"
                )
            )
    )

    private val siteWidePromoContent = PromoContentDomain(
        homepageBanner = HomepageBannerDomain(
            title = "",
            datePrefixText = "",
            date = "",
            discountAmount = "",
            discountPercentageSign = "",
            discountText = "",
            offerDescription = "",
            buttonText = "",
            disclaimer = "",
            terms = TermsDomain(
                text = "",
                url = ""
            )
        ),
        srpBanner =
            SrpBannerDomain(
                title = "Site Wide Promo",
                date = "",
                subTitle = "Site Wide Promo Subtitle",
                terms = TermsDomain(
                    text = "",
                    url = "https://www.premierinn.com/gb/en/terms.html?INTCMP=And_termsAndConditions"
                )
            )
    )

    val defaultPromotions = PromotionsInformationDomain(
        showPromo = true,
        isWithinPromoWindow = true,
        promotionCode = "PROMO123",
        landingPage = null,
        promoBannerColour = null,
        promoBannerIcon = null,
        appPromoBannerTitle = "Site Wide Promo",
        appPromoBannerSubtitle = "Site Wide Promo Subtitle",
        appPromoInvalidMessage = null,
        appPromoExpiredMessage = null,
        appPromoAmendMessage = null,
        termsLink = "https://www.premierinn.com/gb/en/terms.html?INTCMP=And_termsAndConditions",
        promoBookingInfo = null,
        promoBox = null,
        promoKind = null,
        promoBoxStatus = null,
        promoBoxMessageKey = null
    )

    val defaultPromotionsFalse = PromotionsInformationDomain(
        showPromo = false,
        isWithinPromoWindow = true,
        promotionCode = "",
        landingPage = null,
        promoBannerColour = null,
        promoBannerIcon = null,
        appPromoBannerTitle = null,
        appPromoBannerSubtitle = null,
        appPromoInvalidMessage = null,
        appPromoExpiredMessage = null,
        appPromoAmendMessage = null,
        termsLink = null,
        promoBookingInfo = null,
        promoBox = null,
        promoKind = null,
        promoBoxStatus = null,
        promoBoxMessageKey = null
    )
}

