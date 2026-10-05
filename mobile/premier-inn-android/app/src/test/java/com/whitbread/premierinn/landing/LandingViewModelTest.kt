package com.whitbread.premierinn.landing

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.whitbread.premierinn.api.response.InstanceFactory
import com.whitbread.premierinn.api.response.availability.Coordinates
import com.whitbread.premierinn.api.response.search.SearchItemInput
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.common.AppConfiguration
import com.whitbread.premierinn.common.CoronavirusFlags
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.PROMO_CODE
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.FIRST_TIME_OFFER
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.FREE_BREAKFAST
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.analytics.analyticsDataOf
import com.whitbread.premierinn.common.model.BookingFixture
import com.whitbread.premierinn.common.model.DashboardFixture
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.common.utils.LocationProvider
import com.whitbread.premierinn.data.common.DEFAULT_MAX_ARRIVAL_DATE
import com.whitbread.premierinn.data.common.DEFAULT_MAX_NIGHTS_INN_BUSINESS
import com.whitbread.premierinn.data.common.DEFAULT_MAX_NIGHTS_LEISURE
import com.whitbread.premierinn.data.common.DEFAULT_MAX_ROOMS_AMEND
import com.whitbread.premierinn.data.common.DEFAULT_MAX_ROOMS_INN_BUSINESS
import com.whitbread.premierinn.data.common.DEFAULT_MAX_ROOMS_LEISURE
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.data.common.toDomain
import com.whitbread.premierinn.data.remote.AccountApiContract
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn
import com.whitbread.premierinn.domain.authentication.usecase.TryToGetCustomerWhenCredentialsAreSavedButTokenIsMissing
import com.whitbread.premierinn.domain.common.EMPLOYEE_RATE_MAX_NIGHT
import com.whitbread.premierinn.domain.common.EMPLOYEE_RATE_MAX_ROOM
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.countries.GetCountries
import com.whitbread.premierinn.domain.customer.entity.BookingPreferences
import com.whitbread.premierinn.domain.customer.usecase.GetCustomer
import com.whitbread.premierinn.domain.dashboard.usecase.DashboardUseCase
import com.whitbread.premierinn.domain.dashboard.usecase.GetDashboard
import com.whitbread.premierinn.domain.dashboard.usecase.GetUpcomingBooking
import com.whitbread.premierinn.domain.graphql.businessRestrictions.entity.ArrivalDateLimitation
import com.whitbread.premierinn.domain.graphql.businessRestrictions.entity.CombinedBusinessRestrictionsDomain
import com.whitbread.premierinn.domain.graphql.businessRestrictions.entity.NightsLimitation
import com.whitbread.premierinn.domain.graphql.businessRestrictions.entity.RoomsLimitation
import com.whitbread.premierinn.domain.graphql.businessRestrictions.usecase.GraphQLCombinedBusinessRestrictionsUseCase
import com.whitbread.premierinn.domain.graphql.common.usecase.HomePageAppsContentUseCase
import com.whitbread.premierinn.domain.graphql.countries.usecase.GraphQLCountriesUseCase
import com.whitbread.premierinn.domain.graphql.landingview.IsHotelAvailableUseCase
import com.whitbread.premierinn.domain.home.entity.SelectedHomeScreenCriteria
import com.whitbread.premierinn.domain.location.usecase.GetGooglePlaceIdLocation
import com.whitbread.premierinn.domain.recentsearch.entity.RecentSearch
import com.whitbread.premierinn.domain.recentsearch.usecase.ClearAllRecentSearches
import com.whitbread.premierinn.domain.recentsearch.usecase.GetRecentSearches
import com.whitbread.premierinn.domain.recentsearch.usecase.StoreRecentSearch
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import com.whitbread.premierinn.domain.search.entity.Location
import com.whitbread.premierinn.domain.search.usecase.StoreSearchItem
import com.whitbread.premierinn.landing.model.LandingInputModel
import com.whitbread.premierinn.landing.model.CODE_FREE_BREAKFAST_INCENTIVE
import com.whitbread.premierinn.landing.model.PromoCodeInput
import com.whitbread.premierinn.roomcriteria.RoomCriteriaHelper
import com.whitbread.premierinn.utils.RxJavaTestRule
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.threeten.bp.LocalDate

val DASHBOARD = DashboardFixture.aDashboardItem()
val BOOKING = BookingFixture.aBooking()

class LandingViewModelTest {

    @get:Rule
    val rxRule = RxJavaTestRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @MockK
    lateinit var locationProvider: LocationProvider

    @MockK
    lateinit var messageProvider: MessageProvider

    @MockK
    lateinit var roomCriteriaHelper: RoomCriteriaHelper

    @MockK
    private lateinit var uiMapper: UiMapper

    @MockK
    private lateinit var searchPayloadMapper: SearchPayloadMapper

    @MockK
    private lateinit var isHotelAvailable: IsHotelAvailableUseCase

    @MockK
    private lateinit var getPlaceLocation: GetGooglePlaceIdLocation

    @MockK
    private lateinit var storeSearchItem: StoreSearchItem

    @MockK
    private lateinit var tryToGetCustomer: TryToGetCustomerWhenCredentialsAreSavedButTokenIsMissing

    @MockK
    private lateinit var getCustomer: GetCustomer

    @MockK
    private lateinit var isFeatureOn: IsFeatureOn

    @MockK
    private lateinit var getStringResource: GetStringResource

    @MockK
    private lateinit var crashlyticsLogger: LogService

    @MockK
    private lateinit var persistenceManager: SimplePersistenceManager

    @MockK
    private lateinit var businessPersistenceManager: BusinessPersistenceManager

    @MockK
    private lateinit var getUpcomingBooking: GetUpcomingBooking

    @MockK
    private lateinit var dashboardUseCase: DashboardUseCase

    @MockK
    private lateinit var getDashboard: GetDashboard

    @MockK
    private lateinit var trackingAnalytics: TrackingAnalytics

    @MockK
    private lateinit var getRecentSearches: GetRecentSearches

    @MockK
    private lateinit var storeRecentSearch: StoreRecentSearch

    @MockK
    private lateinit var clearAllRecentSearches: ClearAllRecentSearches

    @MockK
    private lateinit var isCustomerLoggedIn: IsCustomerLoggedIn

    @MockK
    private lateinit var getCountries: GetCountries

    @MockK
    private lateinit var graphQLCombinedBusinessRestrictionsUseCase: GraphQLCombinedBusinessRestrictionsUseCase

    @MockK
    private lateinit var countriesUseCase: GraphQLCountriesUseCase

    @MockK
    private lateinit var deviceLocaleProvider: DeviceLocaleProvider

    @MockK
    private lateinit var configuration: AppConfiguration

    @MockK
    private lateinit var homePageAppsContentUseCase: HomePageAppsContentUseCase

    private val coronavirusFlags = CoronavirusFlags

    private var successCustomerResponse: AccountApiContract.CustomerResponse? = null
    private val savedStateHandle: SavedStateHandle = mockk()

    @Before
    fun setUp() {
        MockKAnnotations.init(this, relaxed = true)
        every { tryToGetCustomer() } returns Completable.complete()
        every { isFeatureOn(any()) } returns false
        every { isHotelAvailable.execute(any()) } returns Single.just(true)
        every { getStringResource.invoke(Key.OPERA_FALLBACK_POPUP_DETAILS) } returns "{" +
                "\"operaFallbackAlertTitle\": \"Dieses Hotel ist nur auf unserer Website aufrufbar\"," +
                "\"operaFallbackAlertMessage\": \"Möchten Sie die Hotelinformationen auf premierinn.com/ premierinn.de ansehen?\"," +
                "\"operaFallbackAlertClose\": \"Schließen\"," +
                "\"operaFallbackAlertContinue\": \"Fortfahren\"}"
        successCustomerResponse = InstanceFactory.create<AccountApiContract.CustomerResponse?>(
            AccountApiContract.CustomerResponse::class.java, "apiTest/customer-success.json"
        )
        every { savedStateHandle.get<LandingInputModel>(LANDING_BUNDLE) } returns null
    }

    private fun createViewModel(): LandingViewModel {
        return LandingViewModel(
            savedStateHandle,
            tryToGetCustomer = tryToGetCustomer,
            getCustomer = getCustomer,
            getLocation = locationProvider,
            messageProvider = messageProvider,
            roomCriteriaHelper = roomCriteriaHelper,
            searchPayloadMapper = searchPayloadMapper,
            isHotelAvailable = isHotelAvailable,
            uiMapper = uiMapper,
            getPlaceLocation = getPlaceLocation,
            storeSearchItem = storeSearchItem,
            isFeatureOn = isFeatureOn,
            getStringResource = getStringResource,
            crashlyticsLogger = crashlyticsLogger,
            persistenceManager = persistenceManager,
            businessPersistenceManager = businessPersistenceManager,
            getUpcomingBooking = getUpcomingBooking,
            dashboardUseCase = dashboardUseCase,
            getDashboard = getDashboard,
            trackingAnalytics = trackingAnalytics,
            getRecentSearches = getRecentSearches,
            storeRecentSearch = storeRecentSearch,
            clearAllRecentSearches = clearAllRecentSearches,
            isCustomerLoggedIn = isCustomerLoggedIn,
            getCountries = getCountries,
            countriesUseCase = countriesUseCase,
            deviceLocaleProvider = deviceLocaleProvider,
            appConfiguration = configuration,
            homePageAppsContentUseCase = homePageAppsContentUseCase,
            graphQLCombinedBusinessRestrictionsUseCase = graphQLCombinedBusinessRestrictionsUseCase
        )
    }

    private fun createViewModelWithHotel(): LandingViewModel {
        val searchItemInput = SearchItemInput.builder()
            .searchText("Newhaven")
            .hotelId("NEWDRO")
            .brand("PI")
            .location(Coordinates.create(51.508101f, -0.1270057f))
            .build()
        val landingInputModel = LandingInputModel(searchItem = searchItemInput)
        every { savedStateHandle.get<LandingInputModel>(LANDING_BUNDLE) } returns landingInputModel
        return LandingViewModel(
            savedStateHandle = savedStateHandle,
            tryToGetCustomer = tryToGetCustomer,
            getCustomer = getCustomer,
            getLocation = locationProvider,
            messageProvider = messageProvider,
            roomCriteriaHelper = roomCriteriaHelper,
            searchPayloadMapper = searchPayloadMapper,
            isHotelAvailable = isHotelAvailable,
            uiMapper = uiMapper,
            getPlaceLocation = getPlaceLocation,
            storeSearchItem = storeSearchItem,
            isFeatureOn = isFeatureOn,
            getStringResource = getStringResource,
            crashlyticsLogger = crashlyticsLogger,
            persistenceManager = persistenceManager,
            businessPersistenceManager = businessPersistenceManager,
            getUpcomingBooking = getUpcomingBooking,
            dashboardUseCase = dashboardUseCase,
            getDashboard = getDashboard,
            trackingAnalytics = trackingAnalytics,
            getRecentSearches = getRecentSearches,
            storeRecentSearch = storeRecentSearch,
            clearAllRecentSearches = clearAllRecentSearches,
            isCustomerLoggedIn = isCustomerLoggedIn,
            getCountries = getCountries,
            countriesUseCase = countriesUseCase,
            deviceLocaleProvider = deviceLocaleProvider,
            appConfiguration = configuration,
            homePageAppsContentUseCase = homePageAppsContentUseCase,
            graphQLCombinedBusinessRestrictionsUseCase = graphQLCombinedBusinessRestrictionsUseCase
        )
    }

    private fun createViewModelWithHotelWithPromoCodeInput(): LandingViewModel {
        val searchItemInput = SearchItemInput.builder()
            .searchText("Burnley")
            .hotelId("BURQUE")
            .brand("PI")
            .location(Coordinates.create(51.508101f, -0.1270057f))
            .build()
        val landingInputModel = LandingInputModel(
            searchItem = searchItemInput,
            promoCodeInput = PromoCodeInput("appIncentive")
        )
        every { savedStateHandle.get<LandingInputModel>(LANDING_BUNDLE) } returns landingInputModel
        return LandingViewModel(
            savedStateHandle = savedStateHandle,
            tryToGetCustomer = tryToGetCustomer,
            getCustomer = getCustomer,
            getLocation = locationProvider,
            messageProvider = messageProvider,
            roomCriteriaHelper = roomCriteriaHelper,
            searchPayloadMapper = searchPayloadMapper,
            isHotelAvailable = isHotelAvailable,
            uiMapper = uiMapper,
            getPlaceLocation = getPlaceLocation,
            storeSearchItem = storeSearchItem,
            isFeatureOn = isFeatureOn,
            getStringResource = getStringResource,
            crashlyticsLogger = crashlyticsLogger,
            persistenceManager = persistenceManager,
            businessPersistenceManager = businessPersistenceManager,
            getUpcomingBooking = getUpcomingBooking,
            dashboardUseCase = dashboardUseCase,
            getDashboard = getDashboard,
            trackingAnalytics = trackingAnalytics,
            getRecentSearches = getRecentSearches,
            storeRecentSearch = storeRecentSearch,
            clearAllRecentSearches = clearAllRecentSearches,
            isCustomerLoggedIn = isCustomerLoggedIn,
            getCountries = getCountries,
            countriesUseCase = countriesUseCase,
            deviceLocaleProvider = deviceLocaleProvider,
            appConfiguration = configuration,
            homePageAppsContentUseCase = homePageAppsContentUseCase,
            graphQLCombinedBusinessRestrictionsUseCase = graphQLCombinedBusinessRestrictionsUseCase
        )
    }

    private fun createViewModelWithHotelWithBreakfastDeeplink(): LandingViewModel {
        val searchItemInput = SearchItemInput.builder()
            .searchText("Heathrow")
            .hotelId("HEAPTI")
            .brand("PI")
            .location(Coordinates.create(51.508101f, -0.1270057f))
            .build()
        val landingInputModel = LandingInputModel(
            searchItem = searchItemInput,
            promoCodeInput = PromoCodeInput(promoCode = CODE_FREE_BREAKFAST_INCENTIVE)
        )
        every { savedStateHandle.get<LandingInputModel>(LANDING_BUNDLE) } returns landingInputModel
        return LandingViewModel(
            savedStateHandle = savedStateHandle,
            tryToGetCustomer = tryToGetCustomer,
            getCustomer = getCustomer,
            getLocation = locationProvider,
            messageProvider = messageProvider,
            roomCriteriaHelper = roomCriteriaHelper,
            searchPayloadMapper = searchPayloadMapper,
            isHotelAvailable = isHotelAvailable,
            uiMapper = uiMapper,
            getPlaceLocation = getPlaceLocation,
            storeSearchItem = storeSearchItem,
            isFeatureOn = isFeatureOn,
            getStringResource = getStringResource,
            crashlyticsLogger = crashlyticsLogger,
            persistenceManager = persistenceManager,
            businessPersistenceManager = businessPersistenceManager,
            getUpcomingBooking = getUpcomingBooking,
            dashboardUseCase = dashboardUseCase,
            getDashboard = getDashboard,
            trackingAnalytics = trackingAnalytics,
            getRecentSearches = getRecentSearches,
            storeRecentSearch = storeRecentSearch,
            clearAllRecentSearches = clearAllRecentSearches,
            isCustomerLoggedIn = isCustomerLoggedIn,
            getCountries = getCountries,
            countriesUseCase = countriesUseCase,
            deviceLocaleProvider = deviceLocaleProvider,
            appConfiguration = configuration,
            homePageAppsContentUseCase = homePageAppsContentUseCase,
            graphQLCombinedBusinessRestrictionsUseCase = graphQLCombinedBusinessRestrictionsUseCase
        )
    }

    private fun createViewModelWithHotelWithWrongBreakfastPromoCode(): LandingViewModel {
        val searchItemInput = SearchItemInput.builder()
            .searchText("Heathrow")
            .hotelId("HEAPTI")
            .brand("PI")
            .location(Coordinates.create(51.508101f, -0.1270057f))
            .build()
        val landingInputModel = LandingInputModel(
            searchItem = searchItemInput,
            promoCodeInput = PromoCodeInput(promoCode = "WRONGCODE")
        )
        every { savedStateHandle.get<LandingInputModel>(LANDING_BUNDLE) } returns landingInputModel
        return LandingViewModel(
            savedStateHandle = savedStateHandle,
            tryToGetCustomer = tryToGetCustomer,
            getCustomer = getCustomer,
            getLocation = locationProvider,
            messageProvider = messageProvider,
            roomCriteriaHelper = roomCriteriaHelper,
            searchPayloadMapper = searchPayloadMapper,
            isHotelAvailable = isHotelAvailable,
            uiMapper = uiMapper,
            getPlaceLocation = getPlaceLocation,
            storeSearchItem = storeSearchItem,
            isFeatureOn = isFeatureOn,
            getStringResource = getStringResource,
            crashlyticsLogger = crashlyticsLogger,
            persistenceManager = persistenceManager,
            businessPersistenceManager = businessPersistenceManager,
            getUpcomingBooking = getUpcomingBooking,
            dashboardUseCase = dashboardUseCase,
            getDashboard = getDashboard,
            trackingAnalytics = trackingAnalytics,
            getRecentSearches = getRecentSearches,
            storeRecentSearch = storeRecentSearch,
            clearAllRecentSearches = clearAllRecentSearches,
            isCustomerLoggedIn = isCustomerLoggedIn,
            getCountries = getCountries,
            countriesUseCase = countriesUseCase,
            deviceLocaleProvider = deviceLocaleProvider,
            appConfiguration = configuration,
            homePageAppsContentUseCase = homePageAppsContentUseCase,
            graphQLCombinedBusinessRestrictionsUseCase = graphQLCombinedBusinessRestrictionsUseCase
        )
    }


    @Test
    fun `initial location is null`() {
        val viewModel = createViewModel()
        val testObserver = viewModel.states().test()
        testObserver.assertValue { state -> state.location == null }
    }

    @Test
    fun `initial arrival date is current date`() {
        val viewModel = createViewModel()
        val testObserver = viewModel.states().test()
        testObserver.assertValue { state -> state.arrival == LocalDate.now() }
    }

    @Test
    fun `initial departure date is tomorrow`() {
        val viewModel = createViewModel()
        val testObserver = viewModel.states().test()
        testObserver.assertValue { state -> state.departure == LocalDate.now().plusDays(1) }
    }

    @Test
    fun `initial room configuration is 1 guest in a double room`() {
        val viewModel = createViewModel()
        val testObserver = viewModel.states().test()
        testObserver.assertValue { it.roomCriteria.count() == 1 }
        testObserver.assertValue { it.roomCriteria[0].numberOfAdults == 1 }
        testObserver.assertValue { it.roomCriteria[0].numberOfChildren == 0 }
        testObserver.assertValue { it.roomCriteria[0].numberOfInfants == 0 }
        testObserver.assertValue { it.roomCriteria[0].includeCot.not() }
        testObserver.assertValue { it.roomCriteria[0].roomType == RoomType.DOUBLE }
    }

    @Test
    fun `load companyName from business shared preference`() {
        every { businessPersistenceManager.getCompanyName() } returns ("CompanyName")

        val viewModel = createViewModel()
        val testObserver = viewModel.states().test()
        testObserver.assertValue { state -> state.companyName == "CompanyName" }
    }

    @Test
    fun `GIVEN business user with empty operaCompanyId WHEN initialised THEN fetch customer from server and cache it`() {
        every { businessPersistenceManager.getBusinessCustomerEmail() } returns "business@domain.com"
        every { businessPersistenceManager.getOperaCompanyId() } returns "" // non-null but empty
        every { getCustomer.invoke() } returns Single.just(successCustomerResponse?.toDomain())

        // Also needed for getCustomerCompanyDetails()
        every { businessPersistenceManager.getCompanyName() } returns "Test Company"

        // Act
        createViewModel()

        // Assert
        verify { getCustomer.invoke() }
        verify { persistenceManager.saveCustomer(any()) }
    }

    @Test
    fun `update room, date & location configuration when selectedHomeScreenCriteria is present`() {
        every { persistenceManager.getSelectedHomeScreenCriteria() } returns SelectedHomeScreenCriteria(
            roomCriteria = listOf(
                RoomCriteria(
                    numberOfAdults = 2, numberOfChildren = 1,
                    numberOfInfants = 1, includeCot = true, roomType = RoomType.FAMILY
                )
            ),
            departure = LocalDate.now().plusDays(1), arrival = LocalDate.now(),
            searchText = "London", latitude = 1234F, longitude = 1234F, hotelId = "LON",
            brand = "PI"
        )

        val viewModel = createViewModel()
        viewModel.uiStates()
        val testObserver = viewModel.states().test()

        testObserver.assertValue { it.roomCriteria.count() == 1 }
        testObserver.assertValue { it.roomCriteria[0].numberOfAdults == 2 }
        testObserver.assertValue { it.roomCriteria[0].numberOfChildren == 1 }
        testObserver.assertValue { it.roomCriteria[0].numberOfInfants == 1 }
        testObserver.assertValue { it.roomCriteria[0].includeCot }
        testObserver.assertValue { it.roomCriteria[0].roomType == RoomType.FAMILY }
        testObserver.assertValue { it.departure == LocalDate.now().plusDays(1) }
        testObserver.assertValue { it.arrival == LocalDate.now() }
        testObserver.assertValue { it.location?.searchText() == "London" }
        testObserver.assertValue { it.location?.location()?.latitude() == 1234F }
        testObserver.assertValue { it.location?.location()?.longitude() == 1234F }
        testObserver.assertValue { it.location?.hotelId() == "LON" }
        testObserver.assertValue { it.location?.brand() == "PI" }
    }

    @Test
    fun `update room & date configuration when selectedHomeScreenCriteria is present with default location saved`() {
        every { persistenceManager.getSelectedHomeScreenCriteria() } returns SelectedHomeScreenCriteria(
            roomCriteria = listOf(
                RoomCriteria(
                    numberOfAdults = 2, numberOfChildren = 1,
                    numberOfInfants = 1, includeCot = true, roomType = RoomType.FAMILY
                )
            ),
            departure = LocalDate.now().plusDays(1), arrival = LocalDate.now(),
            searchText = null, latitude = null, longitude = null, hotelId = null,
            brand = null
        )

        val viewModel = createViewModel()
        viewModel.uiStates()
        val testObserver = viewModel.states().test()

        testObserver.assertValue { it.roomCriteria.count() == 1 }
        testObserver.assertValue { it.roomCriteria[0].numberOfAdults == 2 }
        testObserver.assertValue { it.roomCriteria[0].numberOfChildren == 1 }
        testObserver.assertValue { it.roomCriteria[0].numberOfInfants == 1 }
        testObserver.assertValue { it.roomCriteria[0].includeCot }
        testObserver.assertValue { it.roomCriteria[0].roomType == RoomType.FAMILY }
        testObserver.assertValue { it.departure == LocalDate.now().plusDays(1) }
        testObserver.assertValue { it.arrival == LocalDate.now() }
        testObserver.assertValue { it.location?.searchText() == null }
        testObserver.assertValue { it.location?.location()?.latitude() == null }
        testObserver.assertValue { it.location?.location()?.longitude() == null }
        testObserver.assertValue { it.location?.hotelId() == null }
        testObserver.assertValue { it.location?.brand() == null }
    }

    @Test
    fun `when selectedHomeScreenCriteria is empty then booking preferences is retrieved if present`() {
        every { persistenceManager.getSelectedHomeScreenCriteria() } returns SelectedHomeScreenCriteria.EMPTY
        every { persistenceManager.getCustomerBookingPreferences() } returns BookingPreferences(
            mealPreference = 11, roomCriteriaPreference = RoomCriteria(
                numberOfAdults = 2, numberOfChildren = 1,
                numberOfInfants = 1, includeCot = true, roomType = RoomType.FAMILY
            )
        )

        val viewModel = createViewModel()
        viewModel.uiStates()
        val testObserver = viewModel.states().test()

        testObserver.assertValue { it.roomCriteria.count() == 1 }
        testObserver.assertValue { it.roomCriteria[0].numberOfAdults == 2 }
        testObserver.assertValue { it.roomCriteria[0].numberOfChildren == 1 }
        testObserver.assertValue { it.roomCriteria[0].numberOfInfants == 1 }
        testObserver.assertValue { it.roomCriteria[0].includeCot }
        testObserver.assertValue { it.roomCriteria[0].roomType == RoomType.FAMILY }
    }

    @Test
    fun `when selectedHomeScreenCriteria & booking preferences is empty then state is returning data as expected`() {
        every { persistenceManager.getSelectedHomeScreenCriteria() } returns SelectedHomeScreenCriteria.EMPTY
        every { persistenceManager.getCustomerBookingPreferences() } returns BookingPreferences.EMPTY

        val viewModel = createViewModel()
        viewModel.uiStates()
        val testObserver = viewModel.states().test()

        testObserver.assertValue { it.roomCriteria.count() == 1 }
        testObserver.assertValue { it.roomCriteria[0].numberOfAdults == 1 }
        testObserver.assertValue { it.roomCriteria[0].numberOfChildren == 0 }
        testObserver.assertValue { it.roomCriteria[0].numberOfInfants == 0 }
        testObserver.assertValue { !it.roomCriteria[0].includeCot }
        testObserver.assertValue { it.roomCriteria[0].roomType == RoomType.DOUBLE }
    }

    @Test
    fun `when location set new state has new location`() {
        val searchMock = mockk<SearchItemInput>()
        every { searchMock.isHotel } returns true
        val viewModel = createViewModelWithHotel()

        viewModel.onLocationSet(searchMock)

        val testObserver = viewModel.states().test()
        testObserver.assertValue { it.location == searchMock }
    }

    @Test
    fun `when dates set new state has new arrival date and nights`() {
        val fakeArrivalDate = LocalDate.of(2019, 12, 20)
        val fakeDepartureDate = LocalDate.of(2019, 12, 25)

        val viewModel = createViewModel()
        viewModel.onDatesSet(fakeArrivalDate, fakeDepartureDate)

        val testObserver = viewModel.states().test()
        testObserver.assertValue { it.arrival == fakeArrivalDate }
        testObserver.assertValue { it.departure == fakeDepartureDate }
    }

    @Test
    fun `when location box is clicked new event is OpenSearch`() {
        val viewModel = createViewModel()
        val testObserver = viewModel.events().test()

        viewModel.onLocationBoxClick()

        testObserver.assertValue { it is OpenSearch }
    }

    @Test
    fun `when dates box is clicked new event is OpenCalendar`() {
        val viewModel = createViewModel()
        val testObserver = viewModel.events().test()

        viewModel.onDateBoxClick()

        testObserver.assertValue { it is OpenCalendar }
    }

    @Test
    fun `when rooms box is clicked new event is OpenRoomCriteria`() {
        val viewModel = createViewModel()
        val testObserver = viewModel.events().test()

        viewModel.onRoomsBoxClick()

        testObserver.assertValue { it is OpenRoomCriteria }
    }

    @Test
    fun `when dates were selected and dates box is clicked new event is OpenCalendar with selected dates`() {
        val viewModel = createViewModel()
        val testObserver = viewModel.events().test()

        val fakeArrivalDate = LocalDate.of(2019, 12, 20)
        val fakeDepartureDate = LocalDate.of(2019, 12, 25)

        viewModel.onDatesSet(fakeArrivalDate, fakeDepartureDate)
        viewModel.onDateBoxClick()

        testObserver.assertValue {
            (it as OpenCalendar).dates == Pair(
                fakeArrivalDate,
                fakeDepartureDate
            )
        }
    }

    @Test
    fun `when dates were never selected and dates box is clicked new event is OpenCalendar without selected dates`() {
        val viewModel = createViewModel()
        val testObserver = viewModel.events().test()

        viewModel.onDateBoxClick()

        testObserver.assertValue { (it as OpenCalendar).dates == null }
    }

    @Test
    fun `when rooms box is clicked new event is OpenRoomCriteria with latest rooms criteria`() {
        val viewModel = createViewModel()
        val testObserver = viewModel.events().test()

        val roomCriteria0 = mockk<RoomCriteria>()
        val roomCriteria1 = mockk<RoomCriteria>()

        viewModel.onRoomCriteriaSet(listOf(roomCriteria0, roomCriteria1))
        viewModel.onRoomsBoxClick()

        testObserver.assertValue { (it as OpenRoomCriteria).roomCriteria.count() == 2 }
        testObserver.assertValue { (it as OpenRoomCriteria).roomCriteria[0] == roomCriteria0 }
        testObserver.assertValue { (it as OpenRoomCriteria).roomCriteria[1] == roomCriteria1 }
    }

    @Test
    fun `when submit clicked and location is null invoke locationProvider to get current location`() {
        every { locationProvider.locationUpdateObservable2() } returns Observable.never<Location>()

        val viewModel = createViewModel()
        viewModel.onSubmitClick()

        verify { locationProvider.locationUpdateObservable2() }
    }

    @Test
    fun `when submit clicked and location is null and permission needed emit RequestLocationPermission event`() {
        val viewModel = createViewModel()
        val testObserver = viewModel.events().test()
        every { locationProvider.locationUpdateObservable2() } returns Observable.error(
            SecurityException()
        )

        viewModel.onSubmitClick()

        testObserver.assertValue(RequestLocationPermission)
    }

    @Test
    fun `when location permission is granted find current location`() {
        every { locationProvider.locationUpdateObservable2() } returns Observable.just(
            Location(
                0.0,
                0.0
            )
        )
        every { searchPayloadMapper.toSearchPayload(any()) } returns getSearchPayload()

        val viewModel = createViewModel()
        viewModel.onLocationPermissionResult(true)

        verify { locationProvider.locationUpdateObservable2() }
        verify { storeRecentSearch.execute(getRecentSearch()) }
    }

    @Test
    fun `when COVID feature is ON and shouldShowBanner is true covidBanner should be present`() {
        every { isFeatureOn(Key.FEATURE_COVID_HOME) } returns true
        coronavirusFlags.shouldShowBanner = true
        every { getStringResource(Key.COVID_BANNER_MESSAGE) } returns "Covid message"

        val viewModel = createViewModel()
        val testObserver = viewModel.states().test()

        viewModel.onActivityResumed()

        testObserver.assertValueAt(testObserver.valueCount() - 1) { it.covidBanner == "Covid message" }
    }

    @Test
    fun `when COVID feature is ON and shouldShowBanner is false covidBanner should be null`() {
        every { isFeatureOn(Key.FEATURE_COVID_HOME) } returns true
        coronavirusFlags.shouldShowBanner = false
        every { getStringResource(Key.COVID_BANNER_MESSAGE) } returns "Covid message"

        val viewModel = createViewModel()
        val testObserver = viewModel.states().test()

        viewModel.onActivityResumed()

        testObserver.assertValueAt(testObserver.valueCount() - 1) { it.covidBanner == null }
    }

    @Test
    fun `when COVID feature is OFF and shouldShowBanner is true covidBanner should be null`() {
        every { isFeatureOn(Key.FEATURE_COVID_HOME) } returns false
        coronavirusFlags.shouldShowBanner = true
        every { getStringResource(Key.COVID_BANNER_MESSAGE) } returns "Covid message"

        val viewModel = createViewModel()
        val testObserver = viewModel.states().test()

        viewModel.onActivityResumed()

        testObserver.assertValueAt(testObserver.valueCount() - 1) { it.covidBanner == null }
    }

    @Test
    fun `when COVID feature is ON and shouldShowBanner is true and banner is dismissed covidBanner should be null`() {
        every { isFeatureOn(Key.FEATURE_COVID_HOME) } returns true
        coronavirusFlags.shouldShowBanner = true
        every { getStringResource(Key.COVID_BANNER_MESSAGE) } returns "Covid message"

        val viewModel = createViewModel()

        viewModel.onActivityResumed()
        viewModel.onCovidDismissButtonClicked()

        val testObserver = viewModel.states().test()

        testObserver.assertValueAt(testObserver.valueCount() - 1) { it.covidBanner == null }
    }

    @Test
    fun `on resume activity get upcoming booking`() {
        every { persistenceManager.shouldRefreshDashboard() } returns true
        val viewModel = createViewModel()
        viewModel.onActivityResumed()
        verify(exactly = 1) { getUpcomingBooking.execute() }
    }

    @Test
    fun `on create activity get recent searches`() {
        every { persistenceManager.shouldRefreshDashboard() } returns true
        val viewModel = createViewModel()
        viewModel.updateRecentSearches()
        verify(exactly = 1) { getRecentSearches.execute() }
    }


    @Test
    fun `when has upcoming booking and dashboard has data then load dashboard upcoming booking`() {
        every { getDashboard.invoke() } returns Observable.just(listOf(DASHBOARD))
        every { getUpcomingBooking.execute() } returns Maybe.just(BOOKING)
        every {
            dashboardUseCase.invoke(
                BOOKING.bookingReference,
                BOOKING.leadGuestSurname,
                BOOKING.arrivalDate
            )
        } returns Completable.complete()

        val viewModel = createViewModel()
        viewModel.onActivityResumed()
        viewModel.uiStates()

        val testObserver = viewModel.states().test()
        testObserver.assertValue { it.dashboardItem.contains(DASHBOARD) }
    }

    @Test
    fun `on delete recent searches clear all search entries and update the recentSearchesCleared flag`() {
        every { clearAllRecentSearches.execute() } returns Completable.complete()
        val viewModel = createViewModel()
        viewModel.deleteRecentSearches()

        val testObserver = viewModel.states().test()
        testObserver.assertValue { it.recentSearchesCleared }
        testObserver.assertValue { it.recentSearches == emptyList<RecentSearch>() }
        verify(exactly = 1) { clearAllRecentSearches.execute() }
    }

    @Test
    fun `after recent searches are cleared and ui is updated then reset the recentSearchesCleared flag`() {
        val viewModel = createViewModel()
        viewModel.recentSearchCleared()

        val testObserver = viewModel.states().test()
        testObserver.assertValue { it.recentSearchesCleared.not() }
    }

    @Test
    fun `when logged in as BB user and more than one room recent search clicked emit BusinessBookerRecentSearchError event`() {
        every { isCustomerLoggedIn.isLoggedInAsBusinessCustomer() } returns true

        val viewModel = createViewModel()
        val testObserver = viewModel.events().test()

        viewModel.onRecentSearchClicked(getTwoRoomRecentSearch())

        testObserver.assertValue(BusinessBookerRecentSearchError)
    }

    /****************** fallback tests ********************/

    /******* Submit button scenarios *******/

    @Test
    fun `WHEN submit button is pressed AND fallback flag is true THEN show fallback message`() {
        val viewModel = createViewModelWithHotel()
        val testObserver = viewModel.events().test()

        every { locationProvider.locationUpdateObservable2() } returns Observable.just(
            Location(
                51.508101,
                -0.1270057
            )
        )
        every { isFeatureOn(Key.SHOULD_OPERA_REDIRECT_TO_WEB) } returns true

        viewModel.onSubmitClick()

        verify(exactly = 0) { isHotelAvailable.execute(any()) }
        testObserver.assertValue { it is OpenFallbackPopup }
    }

    @Test
    fun `WHEN submit button is pressed AND fallback flag is false THEN go to HDP`() {
        val viewModel = createViewModelWithHotel()
        val testObserver = viewModel.events().test()

        every { locationProvider.locationUpdateObservable2() } returns Observable.just(
            Location(
                51.508101,
                -0.1270057
            )
        )
        every { isFeatureOn(Key.SHOULD_OPERA_REDIRECT_TO_WEB) } returns false

        viewModel.onSubmitClick()

        verify { isHotelAvailable.execute(any()) }
        testObserver.assertValue { it is OpenHotelDetails }
    }

    /******* Recent searches scenarios *******/

    @Test
    fun `WHEN a recent search is selected AND fallback flag is true THEN show fallback message`() {
        val viewModel = createViewModelWithHotel()
        val testObserver = viewModel.events().test()

        every { locationProvider.locationUpdateObservable2() } returns Observable.just(
            Location(
                51.508101,
                -0.1270057
            )
        )
        every { isFeatureOn(Key.SHOULD_OPERA_REDIRECT_TO_WEB) } returns true

        viewModel.onRecentSearchClicked(getRecentSearchForHotel())

        verify(exactly = 0) { isHotelAvailable.execute(any()) }
        testObserver.assertValue { it is OpenFallbackPopup }
    }

    @Test
    fun `WHEN a recent search is selected AND fallback flag is false THEN show HDP`() {
        every { businessPersistenceManager.getBusinessCustomerEmail() } returns EMPTY_STRING_DOMAIN
        every { persistenceManager.getMaxRoomsLeisure() } returns (4)
        every { persistenceManager.getMaxNightsLeisure() } returns (9)
        every { persistenceManager.getMaxArrivalDateLeisure() } returns (10)

        every { locationProvider.locationUpdateObservable2() } returns Observable.just(
            Location(
                51.508101,
                -0.1270057
            )
        )
        every { isFeatureOn(Key.SHOULD_OPERA_REDIRECT_TO_WEB) } returns false

        val viewModel = createViewModelWithHotel()
        val testObserver = viewModel.events().test()

        viewModel.onRecentSearchClicked(getRecentSearchForHotel())

        verify { isHotelAvailable.execute(any()) }
        testObserver.assertValue { it is OpenHotelDetails }
    }


    @Test
    fun `when fallback continue button is clicked then load webview`() {
        val viewModel = createViewModelWithHotel()
        val testObserver = viewModel.events().test()

        viewModel.onFallbackContinueButtonClicked()

        testObserver.assertValue { it is OpenHotelInWebView }
    }

    @Test
    fun `when fallback cancel button is clicked then clear the current search`() {
        val viewModel = createViewModelWithHotel()
        val testObserver = viewModel.states().test()

        every { messageProvider.currentLocation() }.returns("Current location")

        viewModel.onFallbackCancelButtonClicked()

        testObserver.assertValueAt(testObserver.valueCount() - 1) { it.location == null }
    }


    @Test
    fun `GIVEN BusinessRules returns successful WHEN initialised THEN save to persistenceManager`() {
        //WHEN
        every { persistenceManager.getMaxNightsLeisure() } returns DEFAULT_MAX_NIGHTS_LEISURE
        every { persistenceManager.getMaxRoomsLeisure() } returns DEFAULT_MAX_ROOMS_LEISURE
        every { persistenceManager.getMaxRoomsAmend() } returns DEFAULT_MAX_ROOMS_AMEND
        every { persistenceManager.getMaxArrivalDateLeisure() } returns DEFAULT_MAX_ARRIVAL_DATE
        every { businessPersistenceManager.getBusinessCustomerEmail() } returns "" // Not InnBusiness user
        every { graphQLCombinedBusinessRestrictionsUseCase.execute() } returns Single.just(
            mockCombinedRules()
        )

        // WHEN
        createViewModel()

        // THEN
        verify(atLeast = 1) {
            persistenceManager.saveValuesForBusinessRulesLeisure(
                DEFAULT_MAX_NIGHTS_LEISURE,
                DEFAULT_MAX_ROOMS_LEISURE, // comes from mockCombinedRules()
                DEFAULT_MAX_ROOMS_AMEND,
                DEFAULT_MAX_ARRIVAL_DATE
            )
        }
    }

    @Test
    fun `GIVEN BusinessRules for InnBusiness returns successful WHEN initialised THEN save to business persistenceManager`() {
        //WHEN
        every { businessPersistenceManager.getBusinessCustomerEmail() } returns "test@company.com" // InnBusiness user
        every {
            businessPersistenceManager.saveValuesForBusinessRulesInnBusiness(
                any(),
                any(),
                any()
            )
        } returns Unit
        every { graphQLCombinedBusinessRestrictionsUseCase.execute() } returns Single.just(
            mockCombinedRules()
        )

        val viewModel = createViewModel()
        viewModel.fetchAllBusinessRestrictions()

        verify(atLeast = 1) {
            businessPersistenceManager.saveValuesForBusinessRulesInnBusiness(
                DEFAULT_MAX_NIGHTS_LEISURE,
                DEFAULT_MAX_ROOMS_INN_BUSINESS,
                DEFAULT_MAX_ARRIVAL_DATE
            )
        }
    }

    @Test
    fun `WHEN employee offer is enabled THEN use predefined business rules for employee rate`() {
        every { isFeatureOn.invoke(Key.FEATURE_ALLOW_EMPLOYEE_OFFER) } returns true
        every { configuration.isEmployeeOfferEnabled } returns true
        every { businessPersistenceManager.getBusinessCustomerEmail() } returns EMPTY_STRING_DOMAIN // Not InnBusiness user
        every { graphQLCombinedBusinessRestrictionsUseCase.execute() } returns Single.just(
            mockCombinedRules()
        )

        val viewModel = createViewModel()
        viewModel.fetchAllBusinessRestrictions()

        verify(atLeast = 1) {
            persistenceManager.saveValuesForBusinessRulesEmployee(
                EMPLOYEE_RATE_MAX_NIGHT,           // 9
                EMPLOYEE_RATE_MAX_ROOM,            // 2
                EMPLOYEE_RATE_MAX_ROOM,            // 2
                DEFAULT_MAX_ARRIVAL_DATE           // 364
            )
        }
    }

    @Test
    fun `WHEN employee offer is disabled THEN use default business rules`() {
        every { configuration.isEmployeeOfferEnabled } returns false
        every { isFeatureOn.invoke(Key.FEATURE_ALLOW_EMPLOYEE_OFFER) } returns false
        every { businessPersistenceManager.getBusinessCustomerEmail() } returns "" // Not InnBusiness user
        every { persistenceManager.getMaxNightsLeisure() } returns DEFAULT_MAX_NIGHTS_LEISURE
        every { persistenceManager.getMaxRoomsLeisure() } returns DEFAULT_MAX_ROOMS_LEISURE
        every { persistenceManager.getMaxRoomsAmend() } returns DEFAULT_MAX_ROOMS_AMEND
        every { persistenceManager.getMaxArrivalDateLeisure() } returns DEFAULT_MAX_ARRIVAL_DATE
        every { graphQLCombinedBusinessRestrictionsUseCase.execute() } returns Single.just(
            mockCombinedRules()
        )

        val viewModel = createViewModel()
        viewModel.fetchAllBusinessRestrictions()

        verify {
            persistenceManager.saveValuesForBusinessRulesLeisure(
                DEFAULT_MAX_NIGHTS_LEISURE,
                DEFAULT_MAX_ROOMS_LEISURE,
                DEFAULT_MAX_ROOMS_AMEND,
                DEFAULT_MAX_ARRIVAL_DATE
            )
        }
    }

    @Test
    fun `WHEN rooms count exceeds max THEN publish MaxRoomsError for normal leisure customer`() {
        val maxRoomErrorMessage = "Max rooms error message"
        val phoneNumber = "1234567890"

        every { businessPersistenceManager.getBusinessCustomerEmail() } returns EMPTY_STRING_DOMAIN
        every { isCustomerLoggedIn.isLoggedInAsBusinessCustomer() } returns false
        every { configuration.isEmployeeOfferEnabled } returns false
        every { isFeatureOn.invoke(Key.FEATURE_ALLOW_EMPLOYEE_OFFER) } returns false
        every { persistenceManager.getMaxRoomsLeisure() } returns (4)
        every { persistenceManager.getMaxNightsLeisure() } returns (9)
        every { persistenceManager.getMaxArrivalDateLeisure() } returns (10)
        every { roomCriteriaHelper.getMaxRoomsErrorMessage(any()) } returns maxRoomErrorMessage
        every { roomCriteriaHelper.isMaxRoomsGroupFormRequired() } returns false
        every { getStringResource(Key.GROUP_BOOKINGS_NUMBER) } returns phoneNumber

        val viewModel = createViewModel()

        val recentSearch = RecentSearch(
            searchTerm = "Test",
            hotelCode = null,
            latitude = 51.508102f,
            longitude = -0.129068f,
            hotelBrand = null,
            arrivalDate = LocalDate.now(),
            departureDate = LocalDate.now().plusDays(1),
            roomsCount = 5,
            adults = listOf(2),
            children = listOf(1),
            infants = listOf(0),
            cots = listOf(false),
            roomTypeCodes = listOf("Standard")
        )

        val testObserver = viewModel.events().test()

        viewModel.onRecentSearchClicked(recentSearch)

        testObserver.assertValue {
            it is MaxRoomsError
                    && it.message == maxRoomErrorMessage
                    && it.phoneNumber == phoneNumber
                    && it.isGroupFormRequired == false
        }
    }

    @Test
    fun `WHEN nights count exceeds max THEN publish MaxNightsError for normal leisure customer`() {
        every { businessPersistenceManager.getBusinessCustomerEmail() } returns EMPTY_STRING_DOMAIN
        every { isCustomerLoggedIn.isLoggedInAsBusinessCustomer() } returns false
        every { configuration.isEmployeeOfferEnabled } returns false
        every { isFeatureOn.invoke(Key.FEATURE_ALLOW_EMPLOYEE_OFFER) } returns false
        every { persistenceManager.getMaxRoomsLeisure() } returns (4)
        every { persistenceManager.getMaxNightsLeisure() } returns (9)
        every { persistenceManager.getMaxArrivalDateLeisure() } returns (10)
        every { getStringResource(Key.NON_CHARGEABLE_PHONE_DESC) } returns "Non-chargeable phone description"
        every { getStringResource(Key.GROUP_BOOKINGS_NUMBER) } returns "1234567890"

        val viewModel = createViewModel()

        val recentSearch = RecentSearch(
            searchTerm = "Test",
            hotelCode = null,
            latitude = 51.508102f,
            longitude = -0.129068f,
            hotelBrand = null,
            arrivalDate = LocalDate.now(),
            departureDate = LocalDate.now().plusDays((10).toLong()),
            roomsCount = 2,
            adults = listOf(2),
            children = listOf(1),
            infants = listOf(0),
            cots = listOf(false),
            roomTypeCodes = listOf("Standard")
        )

        val testObserver = viewModel.events().test()

        viewModel.onRecentSearchClicked(recentSearch)

        testObserver.assertValue {
            it is MaxNightsError
                    && it.nights == 9
        }
    }

    @Test
    fun `WHEN rooms count exceeds max THEN publish MaxRoomsError for employee offer enabled`() {
        val maxRoomErrorMessage = "Max rooms error message"
        val phoneNumber = "1234567890"

        every { businessPersistenceManager.getBusinessCustomerEmail() } returns EMPTY_STRING_DOMAIN
        every { configuration.isEmployeeOfferEnabled } returns true
        every { isFeatureOn.invoke(Key.FEATURE_ALLOW_EMPLOYEE_OFFER) } returns true
        every { persistenceManager.getMaxRoomsLeisure() } returns EMPLOYEE_RATE_MAX_ROOM
        every { isCustomerLoggedIn.isLoggedInAsBusinessCustomer() } returns false
        every { roomCriteriaHelper.getMaxRoomsErrorMessage(any()) } returns maxRoomErrorMessage
        every { roomCriteriaHelper.isMaxRoomsGroupFormRequired() } returns false
        every { getStringResource(Key.GROUP_BOOKINGS_NUMBER) } returns phoneNumber
        every { graphQLCombinedBusinessRestrictionsUseCase.execute() } returns Single.just(
            mockCombinedRules()
        )

        val viewModel = createViewModel()

        val recentSearch = RecentSearch(
            searchTerm = "Test",
            hotelCode = null,
            latitude = 51.508102f,
            longitude = -0.129068f,
            hotelBrand = null,
            arrivalDate = LocalDate.now(),
            departureDate = LocalDate.now().plusDays(1),
            roomsCount = EMPLOYEE_RATE_MAX_ROOM + 1,
            adults = listOf(2),
            children = listOf(1),
            infants = listOf(0),
            cots = listOf(false),
            roomTypeCodes = listOf("Standard")
        )

        val testObserver = viewModel.events().test()

        viewModel.onRecentSearchClicked(recentSearch)

        testObserver.assertValue {
            it is MaxRoomsError
                    && it.message == maxRoomErrorMessage
                    && it.phoneNumber == phoneNumber
                    && it.isGroupFormRequired == false

        }
    }

    @Test
    fun `WHEN nights count exceeds max THEN publish MaxNightsError for employee offer enabled`() {
        every { businessPersistenceManager.getBusinessCustomerEmail() } returns EMPTY_STRING_DOMAIN
        every { configuration.isEmployeeOfferEnabled } returns true
        every { isFeatureOn.invoke(Key.FEATURE_ALLOW_EMPLOYEE_OFFER) } returns true
        every { persistenceManager.getMaxRoomsLeisure() } returns EMPLOYEE_RATE_MAX_ROOM
        every { persistenceManager.getMaxNightsLeisure() } returns EMPLOYEE_RATE_MAX_NIGHT
        every { isCustomerLoggedIn.isLoggedInAsBusinessCustomer() } returns false
        every { getStringResource(Key.NON_CHARGEABLE_PHONE_DESC) } returns "Non-chargeable phone description"
        every { getStringResource(Key.GROUP_BOOKINGS_NUMBER) } returns "1234567890"
        every { graphQLCombinedBusinessRestrictionsUseCase.execute() } returns Single.just(
            mockCombinedRules()
        )

        val viewModel = createViewModel()

        val recentSearch = RecentSearch(
            searchTerm = "Test",
            hotelCode = null,
            latitude = 51.508102f,
            longitude = -0.129068f,
            hotelBrand = null,
            arrivalDate = LocalDate.now(),
            departureDate = LocalDate.now().plusDays((EMPLOYEE_RATE_MAX_NIGHT + 10).toLong()),
            roomsCount = 1,
            adults = listOf(2),
            children = listOf(1),
            infants = listOf(0),
            cots = listOf(false),
            roomTypeCodes = listOf("Standard")
        )

        val testObserver = viewModel.events().test()

        viewModel.onRecentSearchClicked(recentSearch)

        testObserver.assertValue {
            it is MaxNightsError
                    && it.nights == EMPLOYEE_RATE_MAX_NIGHT
        }
    }

    @Test
    fun `WHEN promo ff disable THEN screen not visible`() {
        every { isFeatureOn.invoke(Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED) } returns false
        val viewModel = createViewModelWithHotelWithPromoCodeInput()

        val testObserver = viewModel.events().test()

        viewModel.runPromoCheck()

        testObserver.assertNoValues()
        testObserver.assertNotComplete()
    }

    @Test
    fun `WHEN promo ff enabled AND page viewed false AND session view false THEN screen not visible`() {
        every { isFeatureOn.invoke(Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED) } returns true
        val viewModel = createViewModel()

        val testObserver = viewModel.events().test()

        viewModel.runPromoCheck()

        testObserver.assertNoValues()
        testObserver.assertNotComplete()
    }

    @Test
    fun `WHEN promo ff enabled AND come with deeplink AND page viewed true AND session view false THEN screen not visible`() {
        every { isFeatureOn.invoke(Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED) } returns true
        val viewModel = createViewModelWithHotelWithPromoCodeInput()

        val testObserver = viewModel.events().test()

        viewModel.runPromoCheck()

        testObserver.assertNoValues()
        testObserver.assertNotComplete()
    }

    @Test
    fun `WHEN promo ff enabled AND come with deeplink AND page viewed true AND session view false AND promo incentive available is false THEN screen not visible`() {
        every { isFeatureOn.invoke(Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED) } returns true
        every { persistenceManager.isAppPromotionalIncentiveAvailable() } returns false
        val viewModel = createViewModelWithHotelWithPromoCodeInput()

        val testObserver = viewModel.events().test()

        viewModel.runPromoCheck()

        testObserver.assertNoValues()
        testObserver.assertNotComplete()
    }

    @Test
    fun `WHEN promo ff enabled AND store inventive page viewed true AND page viewed true AND session view true AND promo incentive available is true THEN screen not visible`() {
        every { isFeatureOn.invoke(Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED) } returns true
        every { persistenceManager.isIncentivePageViewed() } returns true
        val viewModel = createViewModelWithHotel()
        IncentiveFrequencyState.isIncentivePageViewed = true
        IncentiveFrequencyState.isIncentivePageViewedThisSession = true

        val testObserver = viewModel.events().test()

        viewModel.runPromoCheck()

        testObserver.assertNoValues()
        testObserver.assertNotComplete()
    }

    @Test
    fun `WHEN promo ff enabled AND come with deeplink AND page viewed true AND session view false AND promo incentive available is true THEN screen is visible`() {
        val promoCode = "promoCode"
        every { isFeatureOn.invoke(Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED) } returns true
        every { persistenceManager.isAppPromotionalIncentiveAvailable() } returns true
        every { getStringResource.invoke(Key.APP_PROMO_CONTENT) } returns appContentJson
        every { getStringResource.invoke(Key.APP_PROMO_CODE) } returns promoCode
        val viewModel = createViewModelWithHotelWithPromoCodeInput()

        val testObserver = viewModel.events().test()

        viewModel.runPromoCheck()

        testObserver.assertValue { it is OpenIncentiveScreen }
        verify { persistenceManager.setIsAppPromotionalIncentiveAvailable(true) }
        verify {
            trackingAnalytics.track(
                FIRST_TIME_OFFER,
                analyticsDataOf(PROMO_CODE to promoCode)
            )
        }
    }

    @Test
    fun `WHEN promo ff enabled AND store inventive page viewed AND page viewed false AND session view false AND promo incentive available is true THEN screen is visible`() {
        val promoCode = "promoCode"
        every { isFeatureOn.invoke(Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED) } returns true
        every { persistenceManager.isIncentivePageViewed() } returns true
        every { persistenceManager.isAppPromotionalIncentiveAvailable() } returns true
        every { getStringResource.invoke(Key.APP_PROMO_CONTENT) } returns appContentJson
        every { getStringResource.invoke(Key.APP_PROMO_CODE) } returns promoCode
        val viewModel = createViewModelWithHotel()

        val testObserver = viewModel.events().test()

        viewModel.runPromoCheck()

        testObserver.assertValue { it is OpenIncentiveScreen }
        verify { persistenceManager.setIsAppPromotionalIncentiveAvailable(true) }
        verify {
            trackingAnalytics.track(
                FIRST_TIME_OFFER,
                analyticsDataOf(PROMO_CODE to promoCode)
            )
        }
    }

    @Test
    fun `WHEN runAppIncentivePromo returns true THEN runFreeBreakfastPromo is not called`() {
        val viewModel = spyk(createViewModelWithHotel())
        every { viewModel.runAppIncentivePromo() } returns true

        viewModel.runPromoCheck()

        verify(exactly = 1) { viewModel.runAppIncentivePromo() }
        verify(exactly = 0) { viewModel.runFreeBreakfastPromo() }
    }


    @Test
    fun `WHEN runAppIncentivePromo returns false THEN runFreeBreakfastPromo is called`() {
        val viewModel = spyk(createViewModelWithHotel())
        every { viewModel.runAppIncentivePromo() } returns false

        viewModel.runPromoCheck()

        verify(exactly = 1) { viewModel.runAppIncentivePromo() }
        verify(exactly = 1) { viewModel.runFreeBreakfastPromo() }
    }

    @Test
    fun `WHEN runFreeBreakfastPromo is called AND AppIncentive is Available AND AppIncentive FF is True THEN breakfast promo is activated from deeplink promo code`() {
        val promoCode = CODE_FREE_BREAKFAST_INCENTIVE
        every { persistenceManager.isAppPromotionalIncentiveAvailable() } returns true
        every { isFeatureOn.invoke(Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED) } returns true

        val viewModel = createViewModelWithHotelWithBreakfastDeeplink()
        val testObserver = viewModel.events().test()

        viewModel.runFreeBreakfastPromo()

        testObserver.assertNoValues()
        testObserver.assertNotComplete()

        verify { persistenceManager.setFreeBreakfastPromotionCode(promoCode) }
        verify {
            trackingAnalytics.track(
                FREE_BREAKFAST,
                analyticsDataOf(PROMO_CODE to promoCode)
            )
        }
    }

    @Test
    fun `WHEN runFreeBreakfastPromo is called with wrong breakfast promo code THEN breakfast promo is not activated`() {
        val promoCode = CODE_FREE_BREAKFAST_INCENTIVE
        every { isFeatureOn.invoke(Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED) } returns false
        every { persistenceManager.isAppPromotionalIncentiveAvailable() } returns false

        val viewModel = createViewModelWithHotelWithWrongBreakfastPromoCode()
        val testObserver = viewModel.events().test()

        viewModel.runFreeBreakfastPromo()

        testObserver.assertNoValues()
        testObserver.assertNotComplete()
        verify(exactly = 0) { persistenceManager.setFreeBreakfastPromotionCode(any()) }
        verify(exactly = 0) {
            trackingAnalytics.track(
                FREE_BREAKFAST,
                analyticsDataOf(PROMO_CODE to promoCode)
            )
        }
    }

    @Test
    fun `WHEN runFreeBreakfastPromo is called without breakfast deeplink THEN breakfast promo is not activated`() {
        val promoCode = CODE_FREE_BREAKFAST_INCENTIVE

        every { isFeatureOn.invoke(Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED) } returns false
        every { persistenceManager.isAppPromotionalIncentiveAvailable() } returns false

        val viewModel = createViewModelWithHotel() // no promoCodeInput for breakfast

        val testObserver = viewModel.events().test()

        viewModel.runFreeBreakfastPromo()

        testObserver.assertNoValues()
        testObserver.assertNotComplete()
        verify(exactly = 0) { persistenceManager.setFreeBreakfastPromotionCode(any()) }
        verify(exactly = 0) {
            trackingAnalytics.track(
                FREE_BREAKFAST,
                analyticsDataOf(PROMO_CODE to promoCode)
            )
        }
    }

    @Test
    fun `WHEN runFreeBreakfastPromo is called AND all checks pass THEN free breakfast promo is activated`() {
        val promoCode = CODE_FREE_BREAKFAST_INCENTIVE
        every { isFeatureOn.invoke(Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED) } returns false
        every { persistenceManager.isAppPromotionalIncentiveAvailable() } returns false

        val viewModel = createViewModelWithHotelWithBreakfastDeeplink()
        val testObserver = viewModel.events().test()

        viewModel.runFreeBreakfastPromo()

        testObserver.assertNoValues()
        testObserver.assertNotComplete()

        verify { persistenceManager.setFreeBreakfastPromotionCode(promoCode) }
        verify {
            trackingAnalytics.track(
                FREE_BREAKFAST,
                analyticsDataOf(PROMO_CODE to promoCode)
            )
        }
    }

    @Test
    fun `isInnBusinessUser is true when business customer email is set`() {
        every { businessPersistenceManager.getBusinessCustomerEmail() } returns "business@company.com"
        val viewModel = createViewModel()
        assert(viewModel.isInnBusinessUser)
        assert(viewModel.isInnBusinessUserLiveData.value == true)
    }

    @Test
    fun `isInnBusinessUser is false when business customer email is empty`() {
        every { businessPersistenceManager.getBusinessCustomerEmail() } returns ""
        val viewModel = createViewModel()
        assert(!viewModel.isInnBusinessUser)
        assert(viewModel.isInnBusinessUserLiveData.value == false)
    }

    @Test
    fun `exceeding room limit for business user shows info box and does not add room`() {
        every { businessPersistenceManager.getBusinessCustomerEmail() } returns "business@company.com"
        every { businessPersistenceManager.getMaxRoomsInnBusiness() } returns DEFAULT_MAX_ROOMS_INN_BUSINESS
        every { businessPersistenceManager.getMaxNightsInnBusiness() } returns DEFAULT_MAX_NIGHTS_INN_BUSINESS
        every { businessPersistenceManager.getMaxArrivalDateInnBusiness() } returns DEFAULT_MAX_ARRIVAL_DATE
        every { isCustomerLoggedIn.isLoggedInAsBusinessCustomer() } returns true
        every { storeRecentSearch.execute(any()) } returns Completable.complete()

        val viewModel = createViewModel()
        val testObserver = viewModel.events().test()
        val businessRoomsLimit = DEFAULT_MAX_ROOMS_INN_BUSINESS
        val recentSearchExceedRooms = getRecentSearch().copy(roomsCount = businessRoomsLimit + 1)

        viewModel.onRecentSearchClicked(recentSearchExceedRooms)
        val events = testObserver.values()

        assert(events.any { it is BusinessBookerRecentSearchError })
        assert(events.none { it is MaxRoomsError })
        verify(exactly = 0) { storeRecentSearch.execute(any()) }
    }

    @Test
    fun `criteria validation applies business user logic`() {
        every { businessPersistenceManager.getBusinessCustomerEmail() } returns "business@company.com"
        every { businessPersistenceManager.getOperaCompanyId() } returns ""
        every { getCustomer.invoke() } returns Single.just(successCustomerResponse?.toDomain())
        every { businessPersistenceManager.getCompanyName() } returns "Test Company"
        createViewModel()
        verify { getCustomer.invoke() }
        verify { persistenceManager.saveCustomer(any()) }
    }

    private fun getSearchPayload() = SearchPayload(
        LocalDate.now(), LocalDate.now().plusDays(1), "London", 50f,
        1F, listOf(1), listOf(1), listOf(0), listOf(false), listOf("DB"), 1
    )

    private fun getRecentSearch() = RecentSearch(
        "London", "", 50.0F,
        1.0F, "", LocalDate.now(), LocalDate.now().plusDays(1),
        1, listOf(1), listOf(1), listOf(0), listOf(false), listOf("DB")
    )

    private fun getTwoRoomRecentSearch() = RecentSearch(
        "London", "", 50.0F,
        1.0F, "", LocalDate.now(), LocalDate.now().plusDays(1),
        2, listOf(1, 1), listOf(1, 1), listOf(0, 1), listOf(false, false), listOf("DB", "DB")
    )

    private fun getRecentSearchForHotel() = RecentSearch(
        "Newhaven", "NEWDRO", 50.0F,
        1.0F, "PI", LocalDate.now(), LocalDate.now().plusDays(1),
        2, listOf(1, 1), listOf(1, 1), listOf(0, 1), listOf(false, false), listOf("DB", "DB")
    )

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

    private fun mockCombinedRules(): CombinedBusinessRestrictionsDomain {
        val mockRoomsLimitationPI =
            RoomsLimitation(DEFAULT_MAX_ROOMS_LEISURE, DEFAULT_MAX_ROOMS_AMEND)
        val mockRoomsLimitationEmployee =
            RoomsLimitation(EMPLOYEE_RATE_MAX_ROOM, EMPLOYEE_RATE_MAX_ROOM)
        val mockRoomsLimitationBB =
            RoomsLimitation(DEFAULT_MAX_ROOMS_INN_BUSINESS, DEFAULT_MAX_ROOMS_INN_BUSINESS)
        val mockArrivalDateLimitation = ArrivalDateLimitation(DEFAULT_MAX_ARRIVAL_DATE)
        val mockNightsLimitationPI = NightsLimitation(DEFAULT_MAX_NIGHTS_LEISURE)
        val mockNightsLimitationEmployee = NightsLimitation(EMPLOYEE_RATE_MAX_NIGHT)
        val mockNightsLimitationBB = NightsLimitation(DEFAULT_MAX_NIGHTS_INN_BUSINESS)
        return CombinedBusinessRestrictionsDomain(
            roomsLimitationPI = mockRoomsLimitationPI,
            arrivalDateLimitationPI = mockArrivalDateLimitation,
            nightsLimitationPI = mockNightsLimitationPI,
            roomsLimitationBB = mockRoomsLimitationBB,
            arrivalDateLimitationBB = mockArrivalDateLimitation,
            nightsLimitationBB = mockNightsLimitationBB,
            roomsLimitationEMPLOYEE = mockRoomsLimitationEmployee,
            arrivalDateLimitationEMPLOYEE = mockArrivalDateLimitation,
            nightsLimitationEMPLOYEE = mockNightsLimitationEmployee,
        )
    }
}