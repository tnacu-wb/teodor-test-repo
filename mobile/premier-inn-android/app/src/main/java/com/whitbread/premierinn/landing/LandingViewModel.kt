package com.whitbread.premierinn.landing

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.whitbread.premierinn.api.response.availability.Coordinates
import com.whitbread.premierinn.api.response.search.SearchItemInput
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.common.AppConfiguration
import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.common.CoronavirusFlags
import com.whitbread.premierinn.common.FREQUENT_BOOKINGS
import com.whitbread.premierinn.common.LEISURE
import com.whitbread.premierinn.common.Reducer
import com.whitbread.premierinn.common.RxViewModelStore
import com.whitbread.premierinn.common.UPCOMING_BOOKING
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.PROMO_CODE
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.FIRST_TIME_OFFER
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.FREE_BREAKFAST
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.analytics.analyticsDataOf
import com.whitbread.premierinn.common.db.RecentSearchEntityMapper
import com.whitbread.premierinn.common.mapToAsyncResult
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.common.utils.LocationProvider
import com.whitbread.premierinn.common.utils.StringUtils
import com.whitbread.premierinn.common.utils.retrieveAppIncentivePromoContentFirebase
import com.whitbread.premierinn.common.utils.toCustomerResponse
import com.whitbread.premierinn.data.common.DEFAULT_MAX_ARRIVAL_DATE
import com.whitbread.premierinn.data.common.DEFAULT_MAX_NIGHTS_INN_BUSINESS
import com.whitbread.premierinn.data.common.DEFAULT_MAX_NIGHTS_LEISURE
import com.whitbread.premierinn.data.common.DEFAULT_MAX_ROOMS_AMEND
import com.whitbread.premierinn.data.common.DEFAULT_MAX_ROOMS_INN_BUSINESS
import com.whitbread.premierinn.data.common.DEFAULT_MAX_ROOMS_LEISURE
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.data.common.toOperaFallbackPopupInfoDomain
import com.whitbread.premierinn.data.common.toRoomType
import com.whitbread.premierinn.data.remote.OperaFallbackPopupInfo
import com.whitbread.premierinn.domain.authentication.usecase.IsCustomerLoggedIn
import com.whitbread.premierinn.domain.authentication.usecase.TryToGetCustomerWhenCredentialsAreSavedButTokenIsMissing
import com.whitbread.premierinn.domain.booking.entity.Booking
import com.whitbread.premierinn.domain.common.EMPLOYEE_CODE
import com.whitbread.premierinn.domain.common.EMPLOYEE_RATE_MAX_NIGHT
import com.whitbread.premierinn.domain.common.EMPLOYEE_RATE_MAX_ROOM
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.HOME_PAGE_APP_CONTENT_SUB_CHANNEL
import com.whitbread.premierinn.domain.common.OperaFallbackPopupInfoDomain
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.domain.common.SUB_CHANNEL
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.countries.GetCountries
import com.whitbread.premierinn.domain.customer.entity.BookingPreferences
import com.whitbread.premierinn.domain.customer.usecase.GetCustomer
import com.whitbread.premierinn.domain.dashboard.usecase.DashboardUseCase
import com.whitbread.premierinn.domain.dashboard.usecase.GetDashboard
import com.whitbread.premierinn.domain.dashboard.usecase.GetUpcomingBooking
import com.whitbread.premierinn.domain.graphql.businessRestrictions.usecase.GraphQLCombinedBusinessRestrictionsUseCase
import com.whitbread.premierinn.domain.graphql.common.usecase.HomePageAppsContentUseCase
import com.whitbread.premierinn.domain.graphql.countries.usecase.GraphQLCountriesUseCase
import com.whitbread.premierinn.domain.graphql.landingview.IsHotelAvailableUseCase
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HomePageContentRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelAvailabilityRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelInfoDetails
import com.whitbread.premierinn.domain.home.entity.SelectedHomeScreenCriteria
import com.whitbread.premierinn.domain.location.usecase.GetGooglePlaceIdLocation
import com.whitbread.premierinn.domain.recentsearch.entity.RecentSearch
import com.whitbread.premierinn.domain.recentsearch.usecase.ClearAllRecentSearches
import com.whitbread.premierinn.domain.recentsearch.usecase.GetRecentSearches
import com.whitbread.premierinn.domain.recentsearch.usecase.StoreRecentSearch
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import com.whitbread.premierinn.domain.roomcriteria.RoomCriteriaOrderedCollection.Companion.setMaxRooms
import com.whitbread.premierinn.domain.search.entity.Location
import com.whitbread.premierinn.domain.search.usecase.StoreSearchItem
import com.whitbread.premierinn.hoteldetails.hdpOperaExtensions.convertToRoomSearch
import com.whitbread.premierinn.landing.analytics.HomePageCardsData
import com.whitbread.premierinn.landing.analytics.HomePageCardsData.Companion.CARD_CLICK_ACTION
import com.whitbread.premierinn.landing.analytics.LandingAnalyticsData
import com.whitbread.premierinn.landing.analytics.toFrequentBookingTracking
import com.whitbread.premierinn.landing.analytics.toRecentSearchesTracking
import com.whitbread.premierinn.landing.analytics.toUpcomingBookingTracking
import com.whitbread.premierinn.landing.model.CODE_APP_INCENTIVE
import com.whitbread.premierinn.landing.model.CODE_FREE_BREAKFAST_INCENTIVE
import com.whitbread.premierinn.landing.model.CardNavigationModel
import com.whitbread.premierinn.landing.model.LandingInputModel
import com.whitbread.premierinn.roomcriteria.RoomCriteriaHelper
import com.whitbread.premierinn.searchresults.SearchResultsInput
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.ChronoUnit
import java.util.Date
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class LandingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    tryToGetCustomer: TryToGetCustomerWhenCredentialsAreSavedButTokenIsMissing, // potentially remove
    private val getCustomer: GetCustomer,
    private val getLocation: LocationProvider,
    private val messageProvider: MessageProvider,
    private val roomCriteriaHelper: RoomCriteriaHelper,
    private val uiMapper: UiMapper,
    private val searchPayloadMapper: SearchPayloadMapper,
    private val isHotelAvailable: IsHotelAvailableUseCase,
    private val getPlaceLocation: GetGooglePlaceIdLocation,
    private val storeSearchItem: StoreSearchItem,
    private val storeRecentSearch: StoreRecentSearch,
    private val isFeatureOn: IsFeatureOn,
    private val getStringResource: GetStringResource,
    private val crashlyticsLogger: LogService,
    private val persistenceManager: SimplePersistenceManager,
    private val businessPersistenceManager: BusinessPersistenceManager,
    private val getUpcomingBooking: GetUpcomingBooking,
    private val dashboardUseCase: DashboardUseCase,
    getDashboard: GetDashboard,
    private val getRecentSearches: GetRecentSearches,
    private val clearAllRecentSearches: ClearAllRecentSearches,
    private val trackingAnalytics: TrackingAnalytics,
    private val isCustomerLoggedIn: IsCustomerLoggedIn,
    getCountries: GetCountries,
    private val countriesUseCase: GraphQLCountriesUseCase,
    private val deviceLocaleProvider: DeviceLocaleProvider,
    private val appConfiguration: AppConfiguration,
    private val homePageAppsContentUseCase: HomePageAppsContentUseCase,
    private val graphQLCombinedBusinessRestrictionsUseCase: GraphQLCombinedBusinessRestrictionsUseCase
) : RxViewModelStore<State, Event>(initState()) {

    private val landingInputModel: LandingInputModel? by lazy {
        savedStateHandle.get<LandingInputModel>(LANDING_BUNDLE)
    }
    private var calculatedMaxNights: Int = DEFAULT_MAX_NIGHTS_LEISURE
    private var calculatedMaxRooms: Int = DEFAULT_MAX_ROOMS_LEISURE
    private var isEmpOfferEnabledInFb: Boolean
    private var listOfRatePlanCodes: List<String> = emptyList()
    private var placeId: String? = EMPTY_STRING_DOMAIN
    private var hotelBrand: String = EMPTY_STRING
    var isInnBusinessUser = false
    private val _isInnBusinessUser = MutableLiveData<Boolean>()
    val isInnBusinessUserLiveData: LiveData<Boolean> get() = _isInnBusinessUser
    private var operaCompanyId: String? = null
    private var isBusinessRestrictionsFetched: Boolean = false

    init {
        isInnBusinessUser = businessPersistenceManager.getBusinessCustomerEmail().isNotEmpty()
        isEmpOfferEnabledInFb = isFeatureOn.invoke(Key.FEATURE_ALLOW_EMPLOYEE_OFFER)

        fetchAllBusinessRestrictions()

        val deviceLocale = deviceLocaleProvider.getDeviceLocale()
        homePageAppsContentUseCase.invoke(
            HomePageContentRequestBody(
                if (isInnBusinessUser) Channel.BB.name else Channel.PI.name,
                HOME_PAGE_APP_CONTENT_SUB_CHANNEL,
                deviceLocale.language.lowercase(),
                deviceLocaleProvider.getCountryIfRegion(deviceLocale).lowercase()
            )
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ appsContent ->
                applyState(Reducer { state -> state.copy(bottomSheetContent = appsContent) })
            }, { error ->
                crashlyticsLogger.logException(error)
            }).addDisposable()

        landingInputModel?.searchItem?.let {
            applyState(Reducer { state -> state.copy(location = it) })
        }
        landingInputModel?.searchResultsInput?.let {
            applyState(Reducer { state ->
                state.copy(
                        arrival = it.arrivalDate(),
                        departure = it.departureDate(),
                        roomCriteria = roomCriteria(it)
                )
            })
        }

        getCustomerCompanyDetails()

        _isInnBusinessUser.value = isInnBusinessUser

        tryToGetCustomer().mapToAsyncResult().subscribe().addDisposable()

        getDashboard.invoke()
                .subscribe {
                    applyState(Reducer { state -> state.copy(dashboardItem = it) })
                    val upcomingBooking = it.firstOrNull { dashboard -> dashboard.type == UPCOMING_BOOKING }
                    val frequentBooking = it.firstOrNull { dashboard -> dashboard.type == FREQUENT_BOOKINGS }?.content?.frequentBookings
                    if (upcomingBooking != null) {
                        trackingAnalytics.track(AnalyticsConstants.ScreenState.PREMIER_INN, LandingAnalyticsData(upcomingBooking.toUpcomingBookingTracking()))
                    }
                    if (frequentBooking != null && frequentBooking.isNotEmpty()) {
                        trackingAnalytics.track(AnalyticsConstants.ScreenState.PREMIER_INN, LandingAnalyticsData(frequentBooking.toFrequentBookingTracking()))
                    }
                }.addDisposable()

        // This is since if API throws errors , we cant proceed with empty list
        // This is added just for fail safe
        val fetchCountriesFromAsset = getCountries.fetchCountriesFromAsset()

        countriesUseCase.getCountries(deviceLocaleProvider.getDeviceLocale().country.lowercase(),
                deviceLocaleProvider.getDeviceLanguage(), LEISURE)
                .toObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ countries ->
                    persistenceManager.saveListOfCountries(countries)
                },
                        {
                            FirebaseCrashlytics.getInstance().recordException(it)
                            persistenceManager.saveListOfCountries(fetchCountriesFromAsset)
                        }).addDisposable()

        if (isInnBusinessUser && operaCompanyId.isNullOrEmpty()) {
            getCustomerFromServerIfIsLoggedInAndCacheIt()
        }
    }

    fun getCustomerFromServerIfIsLoggedInAndCacheIt() {
        getCustomer().toObservable().map { it.toCustomerResponse() to true }.mapToAsyncResult()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                if (result is AsyncResult.Success) {
                    result.data?.first?.let { customer ->
                        persistenceManager.saveCustomer(customer)
                        getCustomerCompanyDetails()
                    }
                } else if (result is AsyncResult.Error) {
                    publish(BusinessLoginCustomerOrCompanyError)
                }
            }, { error ->
                FirebaseCrashlytics.getInstance().recordException(error)
                // Handle error in another ticket
            }).addDisposable()
    }

    fun getCustomerCompanyDetails() {
        businessPersistenceManager.getCompanyName().let { //currently only set after account login
            applyState(Reducer {state -> state.copy(companyName = it)})
        }

        operaCompanyId = businessPersistenceManager.getOperaCompanyId() //currently only set after account login
    }

    @VisibleForTesting
    fun fetchAllBusinessRestrictions() {
        graphQLCombinedBusinessRestrictionsUseCase.execute()
            .toObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ combined ->
                // Leisure (PI)
                val piRooms = combined.roomsLimitationPI?.maxRooms ?: DEFAULT_MAX_ROOMS_LEISURE
                val piMaxNights = combined.nightsLimitationPI?.maxNights ?: DEFAULT_MAX_NIGHTS_LEISURE
                val piMaxRoomsAmend = combined.roomsLimitationPI?.maxRoomsAmend ?: DEFAULT_MAX_ROOMS_AMEND
                val piMaxArrivalDate = combined.arrivalDateLimitationPI?.maxArrivalDate ?: DEFAULT_MAX_ARRIVAL_DATE

                // Employee
                val empRooms = combined.roomsLimitationEMPLOYEE?.maxRooms ?: EMPLOYEE_RATE_MAX_ROOM
                val empMaxNights = combined.nightsLimitationEMPLOYEE?.maxNights ?: EMPLOYEE_RATE_MAX_NIGHT
                val empMaxRoomsAmend = combined.roomsLimitationEMPLOYEE?.maxRoomsAmend ?: EMPLOYEE_RATE_MAX_ROOM
                val empMaxArrivalDate = combined.arrivalDateLimitationEMPLOYEE?.maxArrivalDate ?: DEFAULT_MAX_ARRIVAL_DATE

                // Inn Business (BB)
                val bbRooms = DEFAULT_MAX_ROOMS_INN_BUSINESS
                val bbMaxNights = combined.nightsLimitationBB?.maxNights ?: DEFAULT_MAX_NIGHTS_INN_BUSINESS
                val bbMaxArrivalDate = combined.arrivalDateLimitationBB?.maxArrivalDate ?: DEFAULT_MAX_ARRIVAL_DATE

                // Set leisure business rules
                persistenceManager.saveValuesForBusinessRulesLeisure(
                    piMaxNights,
                    piRooms,
                    piMaxRoomsAmend,
                    piMaxArrivalDate
                )

                // Set BB business rules
                businessPersistenceManager.saveValuesForBusinessRulesInnBusiness(
                    bbMaxNights,
                    bbRooms,
                    bbMaxArrivalDate
                )

                // Set employee business rules
                persistenceManager.saveValuesForBusinessRulesEmployee(
                    empMaxNights,
                    empRooms,
                    empMaxRoomsAmend,
                    empMaxArrivalDate
                )

                if (isInnBusinessUser) {
                    setMaxRooms(bbRooms)
                    calculatedMaxRooms = bbRooms
                    calculatedMaxNights = bbMaxNights
                } else {
                    if (appConfiguration.isEmployeeOfferEnabled && isEmpOfferEnabledInFb) {
                        setMaxRooms(empRooms)
                        calculatedMaxRooms = empRooms
                        calculatedMaxNights = empMaxNights
                    } else {
                        setMaxRooms(piRooms)
                        calculatedMaxRooms = piRooms
                        calculatedMaxNights = piMaxNights
                    }
                }
                isBusinessRestrictionsFetched = true
            }, {
                FirebaseCrashlytics.getInstance().recordException(it)
            }).addDisposable()
    }

    companion object {
        fun initState() = State(
            location = null,
            arrival = LocalDate.now(),
            departure = LocalDate.now().plusDays(1),
            roomCriteria = listOf(
                RoomCriteria(
                    numberOfAdults = 1,
                    numberOfChildren = 0,
                    numberOfInfants = 0,
                    includeCot = false,
                    roomType = RoomType.DOUBLE,
                    roomNumber = 1
                )
            ),
            dateExplicitlySet = false,
            findingLocation = false,
            requestingPermission = false,
            findingHotelAvailability = false,
            covidBanner = null,
            leadGuestDetails = LeadGuestDetails(EMPTY_STRING, EMPTY_STRING),
            dashboardItem = emptyList(),
            recentSearches = emptyList(),
            recentSearchesCleared = false,
            companyName = EMPTY_STRING,
            bottomSheetContent = null
        )
    }

    fun uiStates(): Observable<UIModel> {
        val bookingPreference = if (isInnBusinessUser) {
            businessPersistenceManager.getCustomerBookingPreferences()
        } else {
            persistenceManager.getCustomerBookingPreferences()
        }
        val selectedHomeScreenCriteria = persistenceManager.getSelectedHomeScreenCriteria()

        if (selectedHomeScreenCriteria.departure != null) {
            if (selectedHomeScreenCriteria.searchText != null) {
                applyState(Reducer { it.copy(
                        location = locationFromSharedPref(selectedHomeScreenCriteria),
                        arrival = selectedHomeScreenCriteria.arrival!!,
                        departure = selectedHomeScreenCriteria.departure!!,
                        roomCriteria = selectedHomeScreenCriteria.roomCriteria!!) })
            } else {
                applyState(Reducer { it.copy(
                        arrival = selectedHomeScreenCriteria.arrival!!,
                        departure = selectedHomeScreenCriteria.departure!!,
                        roomCriteria = selectedHomeScreenCriteria.roomCriteria!!) })
            }
        } else {
            // Even though AS complains its not required check, we definitely see this happening
            if (bookingPreference.roomCriteriaPreference != null) {
                applyState(Reducer { it.copy(roomCriteria = listOf(bookingPreference.roomCriteriaPreference)) })
            } else {
                crashlyticsLogger.log("uiStates() : roomcriteria in booking preference is null :$bookingPreference")
                applyState(Reducer { it.copy(roomCriteria = listOf(BookingPreferences.EMPTY.roomCriteriaPreference)) })
            }
        }

        getUpcomingBooking.execute().subscribe({ booking ->
            val leadGuestDetails = LeadGuestDetails(booking.leadGuestSurname, persistenceManager.getCustomerEmail())
            applyState(Reducer { it.copy(leadGuestDetails = leadGuestDetails) })
        }, {
            crashlyticsLogger.logException(it)
        }).addDisposable()

        return states().map { state -> uiMapper.toUIModel(state) }
    }

    private fun locationFromSharedPref(selectedHomeScreenCriteria: SelectedHomeScreenCriteria): SearchItemInput? {
        val lat = selectedHomeScreenCriteria.latitude ?: 0f
        val long = selectedHomeScreenCriteria.longitude ?: 0f

        return SearchItemInput.builder()
        .brand(selectedHomeScreenCriteria.brand)
        .hotelId(selectedHomeScreenCriteria.hotelId.toString())
        .searchText(selectedHomeScreenCriteria.searchText.toString())
        .location(Coordinates.create(lat, long)).build()

    }

    private fun roomCriteria(input: SearchResultsInput): List<RoomCriteria> {
        val count = input.numRooms()
        val list = mutableListOf<RoomCriteria>()
        for (i in 0 until count) {
            list.add(
                    RoomCriteria(
                            numberOfAdults = input.adults()[i],
                            numberOfChildren = input.children()[i],
                            numberOfInfants = input.infants()[i],
                            includeCot = input.cots()[i],
                            roomType = input.roomTypeCodes()[i].toRoomType(),
                            roomNumber = i + 1,
                            roomId = EMPTY_STRING
                    )
            )
        }
        return list.toList()
    }

    fun onLocationBoxClick() {
        publish(OpenSearch)
    }

    fun onDateBoxClick() {
        val maxArrivalDate = if (isInnBusinessUser)
            businessPersistenceManager.getMaxArrivalDateInnBusiness()
        else
            persistenceManager.getMaxArrivalDateLeisure()

        with(currentState()) {
            val dates = if (dateExplicitlySet)
                Pair(arrival, departure)
            else null
            publish(OpenCalendar(dates, calculatedMaxNights, maxArrivalDate))
        }
    }

    fun onRoomsBoxClick() {
        publish(OpenRoomCriteria(currentState().roomCriteria, isInnBusinessUser))
    }

    fun onSubmitClick() {
        if (appConfiguration.isEmployeeOfferEnabled && isEmpOfferEnabledInFb) {
            listOfRatePlanCodes = listOf(EMPLOYEE_CODE)
        }
        with(currentState()) {
            when {
                location == null -> findLocation()
                location.isHotel -> {
                    val payload =  searchPayloadMapper.toSearchPayload(this)
                    val searchedNights =
                        ChronoUnit.DAYS.between(payload.arrivalDate, payload.departureDate)
                    if (isFeatureOn(Key.SHOULD_OPERA_REDIRECT_TO_WEB)) {
                        publish(OpenFallbackPopup(getFallbackPopupInfo()))
                    } else {
                        if (payload.roomsCount > calculatedMaxRooms) {
                            publish(getMaxRoomsError())
                        } else if (searchedNights > calculatedMaxNights) {
                            publish(
                                MaxNightsError(
                                    nights = calculatedMaxNights
                                )
                            )
                        } else {
                            hotelBrand = location.brand().toString()
                            findHotelAvailability(
                                hotelCode = location.hotelId()!!,
                                payload = payload
                            )
                            saveSearch(location)
                            saveRecentSearchPayload(
                                hotelCode = location.hotelId().orEmpty(),
                                hotelBrand = location.brand().orEmpty(),
                                searchPayload = payload
                            )
                        }
                    }
                }
                else -> {
                    val input = if (location.areCoordinatesSet())
                        Single.just(location)
                    else updateCoordinates(location)
                    input.doAfterSuccess { saveSearch(it) }
                            .map { copy(location = it) }
                            .doOnSubscribe {
                                applyState(Reducer { state -> state.copy(findingHotelAvailability = true) })
                            }.doAfterTerminate {
                                applyState(Reducer { state -> state.copy(findingHotelAvailability = false) })
                            }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ state ->
                                applyState(Reducer { state })
                                val payload = searchPayloadMapper.toSearchPayload(state)
                                val searchedNights =
                                    ChronoUnit.DAYS.between(payload.arrivalDate, payload.departureDate)
                                if (payload.roomsCount > calculatedMaxRooms) {
                                    publish(getMaxRoomsError())
                                } else if (searchedNights > calculatedMaxNights) {
                                    publish(
                                        MaxNightsError(
                                            nights = calculatedMaxNights
                                        )
                                    )
                                } else {
                                    saveRecentSearchPayload(searchPayload = payload)
                                    publish(OpenSearchResults(payload.toSearchResultsInput(), placeId,
                                        deviceLocaleProvider.getDeviceLocale().country.lowercase(),
                                        deviceLocaleProvider.getDeviceLanguage()))
                                }
                            }, { crashlyticsLogger.logException(it) })
                            .addDisposable()
                }
            }
        }
    }

    fun onRecentSearchClicked(recentSearch: RecentSearch) {
        val payload = recentSearch.toSearchPayLoad()
        val searchedNights =
            ChronoUnit.DAYS.between(recentSearch.arrivalDate, recentSearch.departureDate)

        if (isCustomerLoggedIn.isLoggedInAsBusinessCustomer() && recentSearch.roomsCount > 1) {
            publish(BusinessBookerRecentSearchError)
        } else if (recentSearch.hotelBrand.isNullOrEmpty()) {
            if (recentSearch.roomsCount > calculatedMaxRooms) {
                publish(getMaxRoomsError())
            } else if (searchedNights > calculatedMaxNights) {
                publish(
                    MaxNightsError(
                        nights = calculatedMaxNights
                    )
                )
            } else {
                placeId = EMPTY_STRING_DOMAIN
                publish(
                    OpenSearchResults(
                        payload.toSearchResultsInput(), placeId,
                        deviceLocaleProvider.getDeviceLocale().country.lowercase(),
                        deviceLocaleProvider.getDeviceLanguage()
                    )
                )
            }
        } else {
            if (isFeatureOn(Key.SHOULD_OPERA_REDIRECT_TO_WEB)) {
                publish(OpenFallbackPopup(getFallbackPopupInfo()))
            } else {
                if (recentSearch.roomsCount > calculatedMaxRooms) {
                    publish(getMaxRoomsError())
                } else if (searchedNights > calculatedMaxNights) {
                    publish(
                        MaxNightsError(
                            nights = calculatedMaxNights
                        )
                    )
                } else {
                    hotelBrand = recentSearch.hotelBrand.toString()
                    findHotelAvailability(
                        hotelCode = recentSearch.hotelCode.orEmpty(),
                        payload = payload
                    )
                }
            }
        }
    }

    fun onHomeCardsClick(card: CardNavigationModel) {
        trackingAnalytics.trackAction(
            CARD_CLICK_ACTION,
            HomePageCardsData(trackingId = card.trackingId)
        )

        if (card.latitude.isNotBlank() && card.longitude.isNotBlank()) {
            val searchResultInput = SearchResultsInput.builderWithDefaults()
                .placeName(EMPTY_STRING)
                .arrivalDate(LocalDate.now())
                .departureDate(LocalDate.now().plusDays(1))
                .latitude(card.latitude.toFloat())
                .longitude(card.longitude.toFloat()).build()

            publish(
                OpenSearchResults(
                    searchResultInput, EMPTY_STRING,
                    deviceLocaleProvider.getDeviceLocale().country.lowercase(),
                    deviceLocaleProvider.getDeviceLanguage()
                )
            )
        } else if (card.linkPath.isNotBlank()) {
            publish(OpenCardDestination(card.linkPath, card.openLinkInApp))
        }
    }

    fun onNotificationLinkClick(linkPath: String, openLinkInApp: Boolean) {
        publish(OpenNotificationLinkDestination(linkPath, openLinkInApp))
    }

    fun onLocationSet(location: SearchItemInput) {
        if (!location.isHotel) {
            placeId = location.hotelId()
        }
        applyState(Reducer { it.copy(location = location) })
    }

    fun onDatesSet(arrival: LocalDate, departure: LocalDate) {
        applyState(Reducer {
            it.copy(
                    arrival = arrival,
                    departure = departure,
                    dateExplicitlySet = true
            )
        })
    }

    fun onRoomCriteriaSet(roomCriteria: List<RoomCriteria>) {
        applyState(Reducer { it.copy(roomCriteria = roomCriteria) })
    }

    fun onLocationPermissionResult(granted: Boolean) {
        applyState(Reducer { state -> state.copy(requestingPermission = false) })
        if (granted && currentState().location == null) {
            findLocation()
        }
    }

    fun onNotificationPermissionResult(granted: Boolean) {
        applyState(Reducer { state -> state.copy(requestingPermission = false) })
        if (granted) {

        }
    }

    private fun getMaxRoomsError() = MaxRoomsError(
        message = roomCriteriaHelper.getMaxRoomsErrorMessage(getStringResource(Key.GROUP_BOOKINGS_NUMBER)),
        phoneNumber = getStringResource(Key.GROUP_BOOKINGS_NUMBER),
        isGroupFormRequired = roomCriteriaHelper.isMaxRoomsGroupFormRequired()
    )

    private fun findLocation() {
        getLocation.locationUpdateObservable2().firstOrError()
                .doOnSubscribe {
                    applyState(Reducer { state -> state.copy(findingLocation = true) })
                }
                .doAfterTerminate {
                    applyState(Reducer { state -> state.copy(findingLocation = false) })
                }
                .subscribe({
                    val state = currentState().copy(location = searchItemInput(it))
                    applyState(Reducer { state })
                    saveRecentSearchPayload(searchPayload = searchPayloadMapper.toSearchPayload(state))
                    publish(OpenSearchResults(
                        searchPayloadMapper.toSearchPayload(state).toSearchResultsInput(), placeId,
                        deviceLocaleProvider.getDeviceLocale().country,
                        deviceLocaleProvider.getDeviceLanguage()))
                }, {
                    if (it is SecurityException) {
                        publish(RequestLocationPermission)
                        applyState(Reducer { state -> state.copy(requestingPermission = true) })
                    }
                })
                .addDisposable()
    }

    private fun findHotelAvailability(hotelCode: String, payload: SearchPayload) {
        val roomsCount = payload.roomsCount
        val adults = payload.adults
        val children = payload.children
        val cots = payload.cots
        val roomTypeCodes = payload.roomTypeCodes
        if (appConfiguration.isEmployeeOfferEnabled && isEmpOfferEnabledInFb) {
            listOfRatePlanCodes = listOf(EMPLOYEE_CODE)
        }

        isHotelAvailable.execute(
            HotelAvailabilityRequestBody(
                payload.arrivalDate.toString(),
                payload.departureDate.toString(),
                HotelInfoDetails(hotelCode),
                convertToRoomSearch(roomsCount, adults, children, cots, roomTypeCodes),
                BookingChannelDetails(
                    Channel.BB.name.takeIf { isInnBusinessUser } ?: Channel.PI.name,
                    SUB_CHANNEL,
                    deviceLocaleProvider.getDeviceLanguage()
                ),
                hotelBrand,
                listOfRatePlanCodes, operaCompanyId
            )).doOnSubscribe {
            applyState(Reducer { state -> state.copy(findingHotelAvailability = true) })
        }.doAfterTerminate {
            applyState(Reducer { state -> state.copy(findingHotelAvailability = false) })
        }.subscribe({
            if (it) {
                publish(
                    OpenHotelDetails(
                        searchPayload = payload,
                        hotelCode = hotelCode,
                        brand = hotelBrand
                    )
                )
            } else {
                publish(
                    OpenHotelAlternatives(
                        searchPayload = payload,
                        hotelCode = hotelCode,
                        placeId = placeId,
                        country = deviceLocaleProvider.getDeviceLocale().country,
                        language = deviceLocaleProvider.getDeviceLanguage()
                    )
                )
            }
        }, { crashlyticsLogger.logException(it) })
            .addDisposable()
    }

    private fun getFallbackPopupInfo(): OperaFallbackPopupInfoDomain {
        return Gson().fromJson(
                getStringResource.invoke(ContentManagedResourceRepository.Key.OPERA_FALLBACK_POPUP_DETAILS),
                OperaFallbackPopupInfo::class.java
        ).toOperaFallbackPopupInfoDomain()
    }


    private fun saveSearch(searchItem: SearchItemInput) {
        val item = RecentSearchEntityMapper.MAPPER.apply(searchItem)
        storeSearchItem.execute(item, Date().time)
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    private fun saveRecentSearchPayload(hotelCode: String = StringUtils.EMPTY_STRING, hotelBrand: String = StringUtils.EMPTY_STRING,
                                        searchPayload: SearchPayload) {
        val recentSearch = RecentSearch(
                searchTerm = searchPayload.placeName ?: StringUtils.EMPTY_STRING,
                hotelCode = hotelCode,
                latitude = searchPayload.latitude,
                longitude = searchPayload.longitude,
                hotelBrand = hotelBrand,
                arrivalDate = searchPayload.arrivalDate,
                departureDate = searchPayload.departureDate,
                roomsCount = searchPayload.roomsCount,
                adults = searchPayload.adults,
                children = searchPayload.children,
                infants = searchPayload.infants,
                cots = searchPayload.cots,
                roomTypeCodes = searchPayload.roomTypeCodes
        )
        storeRecentSearch.execute(recentSearch)
                .subscribeOn(Schedulers.io())
                .subscribe({ }, { error -> crashlyticsLogger.logException(error) })
                .addDisposable()
    }

    private fun updateCoordinates(searchItem: SearchItemInput): Single<SearchItemInput> {
        return searchItem.hotelId()?.let { code ->
            getPlaceLocation.execute(code)
                    .map { Coordinates.create(it.latitude.toFloat(), it.longitude.toFloat()) }
                    .map {
                        searchItem.toBuilder().location(it).build()
                    }
        } ?: Single.just(searchItem)
    }

    private fun SearchItemInput.areCoordinatesSet(): Boolean {
        val lat = location().latitude()
        val lon = location().longitude()
        return abs(lat) > 0.01 || abs(lon) > 0.01
    }

    private fun searchItemInput(location: Location): SearchItemInput {
        return SearchItemInput.builder()
                .location(
                        Coordinates.create(
                                location.latitude.toFloat(),
                                location.longitude.toFloat()
                        )
                )
                .searchText(messageProvider.currentLocation())
                .build()
    }

    fun onCovidDismissButtonClicked() {
        CoronavirusFlags.shouldShowBanner = false
        applyState(Reducer { it.copy(covidBanner = null) })
    }

    fun onFallbackContinueButtonClicked() {
        publish(OpenHotelInWebView)
    }

    fun runPromoCheck() {
        if (!runAppIncentivePromo()) {
            runFreeBreakfastPromo()
        }
    }

    /**
     * Scenario	                isIncentivePageViewed	isIncentivePageViewedThisSession	Result
     * First normal open................false	            false	                        no popup
     * Deep link open...................true	            false → true	                popup shows
     * Navigates again in app...........true	            true	                        no popup
     * Deep link again .................true	            reset to false → true	        popup shows
     * (same session)
     * Kills app + reopen...............true	            false	                        popup shows
     * normal (after ever
     * opening via link)
     *
     */
    fun runAppIncentivePromo(): Boolean {
        val isAppPromotionalIncentiveEnabled =
            isFeatureOn.invoke(Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED)
        if (!isAppPromotionalIncentiveEnabled) return false

        val isIncentivePageViewed = persistenceManager.isIncentivePageViewed()
        val promoCodeInput = landingInputModel?.promoCodeInput

        if (promoCodeInput?.promoType == CODE_APP_INCENTIVE) {
            persistenceManager.setIncentivePageViewed(true)
            IncentiveFrequencyState.isIncentivePageViewed = true
            IncentiveFrequencyState.isIncentivePageViewedThisSession = false
            persistenceManager.setFreeBreakfastPromotionCode(EMPTY_STRING)
        } else if (!IncentiveFrequencyState.isIncentivePageViewed && isIncentivePageViewed) {
            IncentiveFrequencyState.isIncentivePageViewed = true
        }

        if (IncentiveFrequencyState.isIncentivePageViewed && !IncentiveFrequencyState.isIncentivePageViewedThisSession) {
            IncentiveFrequencyState.isIncentivePageViewedThisSession = true
        } else return false

        val isAppPromotionalIncentiveAvailable =
            persistenceManager.isAppPromotionalIncentiveAvailable() != false
        if (!isAppPromotionalIncentiveAvailable) return false

        val promoContent = retrieveAppIncentivePromoContentFirebase(getStringResource)
        val promoCode = getStringResource.invoke(Key.APP_PROMO_CODE)
        publish(OpenIncentiveScreen(promoContent, promoCode))
        trackingAnalytics.track(FIRST_TIME_OFFER, analyticsDataOf(PROMO_CODE to promoCode))
        persistenceManager.setIsAppPromotionalIncentiveAvailable(true)
        return true
    }

    fun runFreeBreakfastPromo() {
        val promoCode = landingInputModel?.promoCodeInput?.promoCode
            ?.takeIf { it == CODE_FREE_BREAKFAST_INCENTIVE }
            ?: return

        persistenceManager.setFreeBreakfastPromotionCode(promoCode)

        trackingAnalytics.track(FREE_BREAKFAST, analyticsDataOf(PROMO_CODE to promoCode))
    }


    fun onFallbackCancelButtonClicked() {
        val state = currentState().copy(location = null)
        applyState(Reducer { state })
    }

    fun onActivityResumed() {
        val covidBanner =
                if (isFeatureOn(Key.FEATURE_COVID_HOME) && CoronavirusFlags.shouldShowBanner) {
                    getStringResource(Key.COVID_BANNER_MESSAGE)
                } else null
        applyState(Reducer { it.copy(covidBanner = covidBanner) })

        if (persistenceManager.shouldRefreshDashboard()) {
            startUpcomingBooking()
        }

        if (isBusinessRestrictionsFetched) {
            setMaxRooms(calculatedMaxRooms)
        }

        if (isInnBusinessUser) {
            if (currentState().roomCriteria.size > 1) {
                onRoomCriteriaSet(currentState().roomCriteria.take(1))
            }
        }
        updateRecentSearches()
    }

    fun updateRecentSearches( isFromOnCreate: Boolean = false) {
        getRecentSearches.execute()
            .subscribeOn(Schedulers.io())
            .subscribe({ recentSearches ->
                applyState(Reducer { state -> state.copy(recentSearches = recentSearches) })

                if (isFromOnCreate) {
                    trackingAnalytics.track(
                        AnalyticsConstants.ScreenState.LANDING,
                        LandingAnalyticsData(
                            recentSearches.toRecentSearchesTracking(),
                            landingInputModel?.campaignModel
                        )
                    )
                }
                // need to reset campaign model to null after tracking as we only want to track campaign on first open after deep link
                landingInputModel?.campaignModel = null

            }, { error ->
                crashlyticsLogger.logException(error)
            })
            .addDisposable()
    }

    private fun startUpcomingBooking() {
        getUpcomingBooking.execute()
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorComplete()
                .doOnSuccess { getDashboard(it) }
                .doOnComplete { getDashboard(null) }
                .subscribe()
                .addDisposable()
    }

    private fun getDashboard(booking: Booking?) {
        dashboardUseCase.invoke(booking?.bookingReference, booking?.leadGuestSurname, booking?.arrivalDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    persistenceManager.setRefreshDashboard(false)
                }, { error ->
                    persistenceManager.setRefreshDashboard(false)
                    crashlyticsLogger.logException(error)
                }).addDisposable()
    }

    fun deleteRecentSearches() {
        clearAllRecentSearches.execute()
                .subscribeOn(Schedulers.io())
                .subscribe({
                    applyState(Reducer { state -> state.copy(recentSearchesCleared = true, recentSearches = emptyList()) })
                }, { error -> crashlyticsLogger.logException(error) })
                .addDisposable()
    }

    fun recentSearchCleared() {
        applyState(Reducer { state -> state.copy(recentSearchesCleared = false) })
    }

    fun saveSelectedHomeScreenCriteria() {
        val roomCriteria = currentState().roomCriteria
        val lat = currentState().location?.location()?.latitude()
        val long = currentState().location?.location()?.longitude()
        val searchText = currentState().location?.searchText()
        val hotelId = currentState().location?.hotelId()
        val brand = currentState().location?.brand()
        val arrival = currentState().arrival
        val departure = currentState().departure

        persistenceManager.storeSelectedHomeScreenCriteria(
                SelectedHomeScreenCriteria(
                        roomCriteria = roomCriteria,
                        searchText = searchText,
                        latitude = lat,
                        longitude = long,
                        hotelId = hotelId,
                        brand = brand,
                        arrival = arrival,
                        departure = departure))
    }
}
