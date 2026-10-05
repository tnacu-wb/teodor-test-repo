package com.whitbread.premierinn.searchresults

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import com.jakewharton.rxrelay2.PublishRelay
import com.whitbread.premierinn.R
import com.whitbread.premierinn.api.response.ErrorBody
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.common.AppConfiguration
import com.whitbread.premierinn.common.CoronavirusFlags
import com.whitbread.premierinn.common.ReactiveViewModel
import com.whitbread.premierinn.common.Result
import com.whitbread.premierinn.common.Result.Error
import com.whitbread.premierinn.common.StringResourceProvider
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.SEARCH_RESULTS_LIST_NAME
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.SEARCH_RESULTS_MAP_NAME
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.SEARCH_RESULTS_MAP_VIEW_ACTION
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.SEARCH_RESULTS_NO_RESULTS_FOUND
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Type.LOOK_TO_BOOK
import com.whitbread.premierinn.common.analytics.CampaignDataModel
import com.whitbread.premierinn.common.analytics.FirebaseLogger
import com.whitbread.premierinn.common.analytics.FirebaseParams
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.common.utils.retrieveAppIncentivePromoContentFirebase
import com.whitbread.premierinn.common.utils.retrieveFreeBreakfastContentFirebase
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.data.common.toOperaFallbackPopupInfoDomain
import com.whitbread.premierinn.data.common.toSrpBannerPromoContentDomain
import com.whitbread.premierinn.data.remote.ApiThrowable
import com.whitbread.premierinn.data.remote.GraphQlThrowable
import com.whitbread.premierinn.data.remote.OperaFallbackPopupInfo
import com.whitbread.premierinn.domain.common.EMPLOYEE_CODE
import com.whitbread.premierinn.domain.common.OperaFallbackPopupInfoDomain
import com.whitbread.premierinn.domain.common.PromoContentDomain
import com.whitbread.premierinn.domain.common.SUB_CHANNEL
import com.whitbread.premierinn.domain.common.latLongConcatenated
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.graphql.promotions.entity.PromotionsInformationDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelAvailabilitiesRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Place
import com.whitbread.premierinn.domain.graphql.srp.entity.HotelAvailabilitiesWithPromotionsDomain
import com.whitbread.premierinn.domain.graphql.srp.usecase.GraphQLSRPUseCase
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import com.whitbread.premierinn.domain.search.entity.Location
import com.whitbread.premierinn.landing.model.CODE_FREE_BREAKFAST_INCENTIVE
import com.whitbread.premierinn.searchresults.SearchResultsViewAction.ChangeMapCentreAction
import com.whitbread.premierinn.searchresults.SearchResultsViewAction.ChangeModeAction
import com.whitbread.premierinn.searchresults.SearchResultsViewAction.CheckCoronaVirusBannerAction
import com.whitbread.premierinn.searchresults.SearchResultsViewAction.DismissCoronavirusAction
import com.whitbread.premierinn.searchresults.SearchResultsViewAction.DismissInformationBannerAction
import com.whitbread.premierinn.searchresults.SearchResultsViewAction.HotelSelectedIdleAction
import com.whitbread.premierinn.searchresults.SearchResultsViewAction.InformationBannerGoBackAction
import com.whitbread.premierinn.searchresults.SearchResultsViewAction.LoadAvailabilitiesAction
import com.whitbread.premierinn.searchresults.SearchResultsViewAction.LoadCriteriaAction
import com.whitbread.premierinn.searchresults.SearchResultsViewAction.LoadNewAvailabilitiesAction
import com.whitbread.premierinn.searchresults.SearchResultsViewAction.OpenHotelInWebViewAction
import com.whitbread.premierinn.searchresults.SearchResultsViewAction.PopulateFullyBookedItemsAction
import com.whitbread.premierinn.searchresults.SearchResultsViewAction.RequestLoadCurrentStateAction
import com.whitbread.premierinn.searchresults.SearchResultsViewAction.ResetFallbackAction
import com.whitbread.premierinn.searchresults.SearchResultsViewAction.ResetOpenHDPActivityAction
import com.whitbread.premierinn.searchresults.SearchResultsViewAction.SearchAreaAction
import com.whitbread.premierinn.searchresults.SearchResultsViewAction.ShowFallbackOrHDPAction
import com.whitbread.premierinn.searchresults.SearchResultsViewAction.ShowFallbackPopupAction
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.ChangeMapCentreEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.ChangeModeStateEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.DismissCoronavirusEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.DismissInformationBannerEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.HotelSelectedIdleEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.OpenHotelInWebViewEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.RequestAvailabilityEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.RequestBannerGoBackEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.RequestCloseSelfEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.RequestNewAvailabilityEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.ResetCurrentStateRenderEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.ResetFallbackEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.ResetOpenHDPActivityEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.ScreenFirstLaunchEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.ScreenResumedEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.SearchAreaEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.ShowFallbackOrHDPEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewEvent.ShowFallbackPopupEvent
import com.whitbread.premierinn.searchresults.SearchResultsViewResult.ChangeMapCentre
import com.whitbread.premierinn.searchresults.SearchResultsViewResult.ChangeUiMode
import com.whitbread.premierinn.searchresults.SearchResultsViewResult.CoronaVirusBanner
import com.whitbread.premierinn.searchresults.SearchResultsViewResult.DismissCoronavirusBanner
import com.whitbread.premierinn.searchresults.SearchResultsViewResult.DismissInfoBanner
import com.whitbread.premierinn.searchresults.SearchResultsViewResult.HotelSelectedIdle
import com.whitbread.premierinn.searchresults.SearchResultsViewResult.InfoBanner
import com.whitbread.premierinn.searchresults.SearchResultsViewResult.InfoBannerGoBack
import com.whitbread.premierinn.searchresults.SearchResultsViewResult.LoadAvailabilities
import com.whitbread.premierinn.searchresults.SearchResultsViewResult.LoadCriteria
import com.whitbread.premierinn.searchresults.SearchResultsViewResult.LoadNewAvailabilities
import com.whitbread.premierinn.searchresults.SearchResultsViewResult.OpenHDPActivity
import com.whitbread.premierinn.searchresults.SearchResultsViewResult.OpenHotelInWebView
import com.whitbread.premierinn.searchresults.SearchResultsViewResult.RequestLoadCurrentState
import com.whitbread.premierinn.searchresults.SearchResultsViewResult.ResetFallback
import com.whitbread.premierinn.searchresults.SearchResultsViewResult.ResetOpenHDPActivity
import com.whitbread.premierinn.searchresults.SearchResultsViewResult.SearchArea
import com.whitbread.premierinn.searchresults.SearchResultsViewResult.ShowFallbackPopup
import com.whitbread.premierinn.searchresults.SearchResultsViewResult.StoreFullyBookedItems
import com.whitbread.premierinn.searchresults.SearchResultsViewState.ErrorType.GENERIC_ERROR
import com.whitbread.premierinn.searchresults.SearchResultsViewState.ErrorType.NETWORK
import com.whitbread.premierinn.searchresults.SearchResultsViewState.ErrorType.NO_RESULTS
import com.whitbread.premierinn.searchresults.SearchResultsViewState.Mode.FULL_MAP
import com.whitbread.premierinn.searchresults.SearchResultsViewState.Mode.HOTEL_SELECTED
import com.whitbread.premierinn.searchresults.SearchResultsViewState.Mode.LIST_AND_MAP
import com.whitbread.premierinn.searchresults.SearchResultsViewState.Mode.LIST_EXPANDED
import com.whitbread.premierinn.searchresults.analytics.MapViewAnalyticsData
import com.whitbread.premierinn.searchresults.analytics.SearchResultsAnalyticsData
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 *
 */
@HiltViewModel
class SearchResultsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val bookingCriteriaMapper: BookingCriteriaMapper,
    private val hotelItemMapper: HotelItemMapper,
    private val adobeAnalytics: TrackingAnalytics,
    private val firebaseLogger: FirebaseLogger,
    private val errorLogger: LogService,
    val isFeatureOn: IsFeatureOn,
    val simplePersistenceManager: SimplePersistenceManager,
    businessPersistenceManager: BusinessPersistenceManager,
    val getStringResource: GetStringResource,
    private val resourceProvider: StringResourceProvider,
    val deviceLocaleProvider: DeviceLocaleProvider,
    private val graphQLSRPUseCase: GraphQLSRPUseCase,
    private val appConfiguration: AppConfiguration
): ViewModel(), ReactiveViewModel<SearchResultsViewEvent, SearchResultsViewState> {

    private val placeId: String by lazy {
        savedStateHandle.get<String>(PLACE_ID) ?: EMPTY_STRING
    }

    private val trackingCode: String by lazy {
        savedStateHandle.get<SearchResultsInput>(EXTRA_SRP_INPUT)?.trackingCode() ?: EMPTY_STRING
    }

    private val campaignModel: CampaignDataModel? by lazy {
        savedStateHandle.get<SearchResultsInput>(EXTRA_SRP_INPUT)?.campaignModel()
    }

    private var viewState: SearchResultsViewState = SearchResultsViewState()
    private var disposable: Disposable? = null
    private val viewStates: Observable<SearchResultsViewState>
    private val events = PublishRelay.create<SearchResultsViewEvent>()
    private var latestAnalyticsDataWithoutMode: SearchResultsAnalyticsData.Builder? = null
    private val listViewModes = listOf(LIST_AND_MAP, LIST_EXPANDED)
    private val mapViewModes = listOf(HOTEL_SELECTED, FULL_MAP)
    private val isEmpOfferEnabledFb = isFeatureOn.invoke(Key.FEATURE_ALLOW_EMPLOYEE_OFFER)
    private var listOfRatePlans: List<String> = if (appConfiguration.isEmployeeOfferEnabled && isEmpOfferEnabledFb)
        listOf(EMPLOYEE_CODE) else emptyList()
    private var operaCompanyId: String? = null

    private var isAppIncentiveActive: Boolean = false
    private var shouldShowPromoFooterBanner = false
    private var promoContent: PromoContentDomain? = null
    private var promoCode = EMPTY_STRING


    init {

        appIncentiveLogic(simplePersistenceManager)

        viewStates = resultFrom(events)
            .scan(
                SearchResultsViewState(
                    showPromoFooterBanner = shouldShowPromoFooterBanner,
                    promoContent = promoContent,
                    promoCode = promoCode
                )
            ) { currentState, result -> newStateFrom(currentState, result) }
            .distinctUntilChanged()
            .doOnNext {
                trackScreenModeChange(it)
                viewState = it
            }
            .replay(1)
            .autoConnect(0)

        operaCompanyId = businessPersistenceManager.getOperaCompanyId()
    }

    private fun appIncentiveLogic(simplePersistenceManager: SimplePersistenceManager) {
        isAppIncentiveActive = isFeatureOn.invoke(Key.FEATURE_IS_APP_PROMOTIONAL_INCENTIVE_ENABLED)
                && simplePersistenceManager.isAppPromotionalIncentiveAvailable() == true

        if (isAppIncentiveActive) {
            shouldShowPromoFooterBanner = true
            promoContent = retrieveAppIncentivePromoContentFirebase(getStringResource)
            promoCode = getStringResource.invoke(Key.APP_PROMO_CODE)
        }

    }

    private fun siteWidePromoLogic(promotions: PromotionsInformationDomain?) {
        if (promotions?.showPromo == true && !promotions.promotionCode.isNullOrEmpty()) {
            promoCode = promotions.promotionCode!!
            shouldShowPromoFooterBanner = true
            promoContent = promotions.toSrpBannerPromoContentDomain()
        } else {
            freeBreakfastLogic(simplePersistenceManager)
        }
    }

    private fun freeBreakfastLogic(simplePersistenceManager: SimplePersistenceManager) {
        val freeBreakfastPromoCode = simplePersistenceManager.getFreeBreakfastPromotionCode()

        val isFreeBreakfastActive = freeBreakfastPromoCode == CODE_FREE_BREAKFAST_INCENTIVE

        if (isFreeBreakfastActive) {
            shouldShowPromoFooterBanner = true
            promoContent = retrieveFreeBreakfastContentFirebase(getStringResource)
            promoCode = freeBreakfastPromoCode
        }

    }

    fun getListOfRatePlanCodes(): List<String> =
        if (appConfiguration.isEmployeeOfferEnabled && isEmpOfferEnabledFb) listOf(EMPLOYEE_CODE) else emptyList()

    fun getOperaCompanyId(): String? = operaCompanyId

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }

    override fun bind(viewEvents: Observable<out SearchResultsViewEvent>) {
        disposable = viewEvents.subscribe { events.accept(it) }
    }

    override fun viewStates(): Observable<SearchResultsViewState> {
        return viewStates
    }

    private fun onChangeUiMode(): ObservableTransformer<ChangeModeAction, Result<ChangeUiMode>> {
        return ObservableTransformer { upstream ->
            upstream
                .map { Result.Success(ChangeUiMode(it.mode, it.selectedItemId, it.requestClose)) }
        }
    }

    private fun onRequestLoadCurrentState(): ObservableTransformer<RequestLoadCurrentStateAction, Result<RequestLoadCurrentState>> {
        return ObservableTransformer { upstream -> upstream.map { Result.Success(RequestLoadCurrentState) } }
    }

    private fun onHotelSelectedIdle(): ObservableTransformer<HotelSelectedIdleAction, Result<HotelSelectedIdle>> {
        return ObservableTransformer { upstream -> upstream.map { Result.Success(HotelSelectedIdle(it.selectedItem)) } }
    }

    private fun onChangeMapCentre(): ObservableTransformer<ChangeMapCentreAction, Result<ChangeMapCentre>> {
        return ObservableTransformer { upstream -> upstream.map { Result.Success(ChangeMapCentre(it.centre)) } }
    }

    private fun onCriteriaLoad(): ObservableTransformer<LoadCriteriaAction, Result<LoadCriteria>> {
        return ObservableTransformer { upstream ->
            upstream.map { it.searchResultsInput.toBookingCriteria() }
                .map(bookingCriteriaMapper)
                .map { Result.Success(LoadCriteria(it)) }
        }
    }

    private fun onPopulateFullyBookedItems(): ObservableTransformer<PopulateFullyBookedItemsAction, Result<StoreFullyBookedItems>> {
        return ObservableTransformer { upstream ->
            upstream.filter { it.hotelCode != null }
                .map { Result.Success(StoreFullyBookedItems(hotelCode = it.hotelCode)) }
        }
    }

    private fun getOperaFallbackPopupInfo(): OperaFallbackPopupInfoDomain {
        return Gson().fromJson(
            getStringResource.invoke(Key.OPERA_FALLBACK_POPUP_DETAILS),
            OperaFallbackPopupInfo::class.java
        ).toOperaFallbackPopupInfoDomain()
    }

    private fun showOperaFallbackOrHDP(): ObservableTransformer<ShowFallbackOrHDPAction, Result<OpenHDPActivity>> {
        return ObservableTransformer { upstream ->
            upstream.flatMap { selectedHotel ->
                val getOperaFallbackPopupInfoFromFB = getOperaFallbackPopupInfo()
                if (isFeatureOn(Key.SHOULD_OPERA_REDIRECT_TO_WEB)) {
                    events.accept(ShowFallbackPopupEvent(true, getOperaFallbackPopupInfoFromFB))
                    Observable.empty()
                } else {
                    Observable.just(Result.Success(OpenHDPActivity(true, selectedHotel.listItem)))
                }
            }
        }
    }

    private fun resetOpenHDPActivity(): ObservableTransformer<ResetOpenHDPActivityAction, Result<ResetOpenHDPActivity>> {
        return ObservableTransformer { upstream -> upstream.map { Result.Success(ResetOpenHDPActivity(it.openHDPActivity)) } }
    }

    private fun showOperaFallbackPopup(): ObservableTransformer<ShowFallbackPopupAction, Result<ShowFallbackPopup>> {
        return ObservableTransformer { upstream -> upstream.map { Result.Success(ShowFallbackPopup(it.showFallbackPopup, it.fallbackPopupInfo)) } }
    }

    private fun resetOperaFallback(): ObservableTransformer<ResetFallbackAction, Result<ResetFallback>> {
        return ObservableTransformer { upstream -> upstream.map { Result.Success(ResetFallback(it.showFallbackPopup)) } }
    }

    private fun openHotelInWebView(): ObservableTransformer<OpenHotelInWebViewAction, Result<OpenHotelInWebView>> {
        return ObservableTransformer { upstream -> upstream.map { Result.Success(OpenHotelInWebView) } }
    }

    private fun onAvailabilitiesLoad(): ObservableTransformer<LoadAvailabilitiesAction, Result<LoadAvailabilities>> {
        return ObservableTransformer { upstream ->
            upstream.switchMap { action ->
                val place: Place = if (placeId.isNotEmpty()) {
                    Place(placeId, "PLACEID", "MILES", 30)
                } else {
                    Place(latLongConcatenated(action.searchResultInput.latitude().toString(), action.searchResultInput.longitude().toString()),
                        "LATLONG", "MILES", 30)
                }
                val location = Location(latitude = action.searchResultInput.latitude().toDouble(),
                                        longitude = action.searchResultInput.longitude().toDouble())

                val availabilitiesRequestBody = HotelAvailabilitiesRequestBody(
                    place = place, startDate = action.params.startDate,
                    endDate = action.params.endDate,
                    rooms = action.params.rooms,
                    ratePlanCodes = listOfRatePlans,
                    country = deviceLocaleProvider.getDeviceLocale().country.lowercase(),
                    language = deviceLocaleProvider.getDeviceLanguage(),
                    oldWorldChannel = SUB_CHANNEL, channel = if (operaCompanyId.isNullOrEmpty()) Channel.PI.name else Channel.BB.name, subChannel = SUB_CHANNEL,
                    sort = action.params.sort, page = 1, initialPageSize = 40, lazyLoadPageSize = 40, operaCompanyId)

                val availabilitiesObservable = if (isAppIncentiveActive) {
                    graphQLSRPUseCase.getHotelAvailabilities(input = availabilitiesRequestBody)
                        .map { availabilities ->
                            HotelAvailabilitiesWithPromotionsDomain(availabilities, null)
                        }
                        .toObservable()
                } else {
                    graphQLSRPUseCase.getHotelAvailabilitiesWithPromotions(
                        input = availabilitiesRequestBody
                    ).toObservable()
                }

                availabilitiesObservable
                    .doOnNext {
                        latestAnalyticsDataWithoutMode = createAnalyticsDataBuilderWithoutMode(action.searchResultInput, it.availabilities)
                        firebaseLogger.logEvent(FirebaseAnalytics.Event.VIEW_SEARCH_RESULTS,
                            createFirebaseData(
                                action.searchResultInput,
                                it.availabilities.multiHotelAvailabilities
                            ))
                        if (!isAppIncentiveActive) siteWidePromoLogic(it.promotions)
                    }
                    .flatMap { result ->
                        Observable.fromArray(result.availabilities.multiHotelAvailabilities)
                            .flatMapIterable { it }.map(hotelItemMapper).toList().toObservable()
                    }
                    .map {
                        if (it.isNotEmpty()) {
                            setFirebaseParamsForSRP("true")
                            Result.Success(LoadAvailabilities(data = it, centre = location))
                        } else Error(LoadAvailabilities(centre = location, error = NO_RESULTS, data = listOf(ErrorItem(errorResId = R.string.search_results_no_hotel_available))))
                    }
                    .onErrorReturn { it.toResultError() }
                    .doOnNext { it.trackOnErrorIfNeeded() }
                    .subscribeOn(Schedulers.io())
                    .startWith(Result.Loading())
            }
        }
    }

    private fun loadNewAvailabilities(): ObservableTransformer<LoadNewAvailabilitiesAction, Result<LoadNewAvailabilities>> {
        return ObservableTransformer { upstream ->
            upstream.switchMap { action ->
                action.searchResultInput.latitude()
                val place: Place = if (placeId.isNotEmpty()) {
                    Place(placeId, "PLACEID", "MILES", 30)
                } else {
                    Place(latLongConcatenated(action.searchResultInput.latitude().toString(),
                                              action.searchResultInput.longitude().toString()),
                        "LATLONG", "MILES", 30)
                }

                val availabilitiesRequestBody = HotelAvailabilitiesRequestBody(
                    place = place, startDate = action.params.startDate,
                    endDate = action.params.endDate,
                    rooms = action.params.rooms,
                    ratePlanCodes = listOfRatePlans,
                    country = deviceLocaleProvider.getDeviceLocale().country.lowercase(),
                    language = deviceLocaleProvider.getDeviceLocale().language,
                    oldWorldChannel = SUB_CHANNEL, channel = if (operaCompanyId.isNullOrEmpty()) Channel.PI.name else Channel.BB.name, subChannel = SUB_CHANNEL,
                    sort = "DISTANCE", page = 1, initialPageSize = 40, lazyLoadPageSize = 40, operaCompanyId)

                val availabilitiesObservable = if (isAppIncentiveActive) {
                    graphQLSRPUseCase.getHotelAvailabilities(input = availabilitiesRequestBody)
                        .map { availabilities ->
                            HotelAvailabilitiesWithPromotionsDomain(availabilities, null)
                        }
                        .toObservable()
                } else {
                    graphQLSRPUseCase.getHotelAvailabilitiesWithPromotions(
                        input = availabilitiesRequestBody
                    ).toObservable()
                }

                availabilitiesObservable
                    .doOnNext {
                        latestAnalyticsDataWithoutMode = createAnalyticsDataBuilderWithoutMode(action.searchResultInput, it.availabilities)
                        firebaseLogger.logEvent(FirebaseAnalytics.Event.VIEW_SEARCH_RESULTS,
                            createFirebaseData(
                                action.searchResultInput,
                                it.availabilities.multiHotelAvailabilities
                            ))
                        if (!isAppIncentiveActive) siteWidePromoLogic(it.promotions)
                    }
                    .flatMap { result ->
                        Observable.fromArray(result.availabilities.multiHotelAvailabilities)
                            .flatMapIterable { it }.map(hotelItemMapper).toList().toObservable()
                    }
                    .map {
                        if (it.isNotEmpty()) {
                            setFirebaseParamsForSRP("true")
                            Result.Success(LoadNewAvailabilities(data = it, centre = action.centre))
                        } else Error(LoadNewAvailabilities(error = NO_RESULTS, data = listOf(ErrorItem(errorResId = R.string.search_results_no_hotel_available)), centre = null))
                    }
                    .onErrorReturn { it.toResultError().toNewAvailabilitiesAction() }
                    .doOnNext { it.trackOnNewAvailabilitiesError() }
                    .subscribeOn(Schedulers.io())
                    .startWith(Result.Loading())
            }
        }
    }

    private fun checkShouldShowCoronaVirusBanner(): ObservableTransformer<CheckCoronaVirusBannerAction, Result<CoronaVirusBanner>> {
        return ObservableTransformer { upstream ->
            upstream.map {
                val message = if (isFeatureOn(Key.FEATURE_COVID_SRP_AND_HDP) && CoronavirusFlags.shouldShowBanner) {
                    getStringResource(Key.COVID_BANNER_MESSAGE)
                } else {
                    null
                }
                Result.Success(CoronaVirusBanner(message))
            }
        }
    }

    private fun onCoronavirusBannerDismissed(): ObservableTransformer<DismissCoronavirusAction, Result<DismissCoronavirusBanner>> {
        return ObservableTransformer { upstream ->
            upstream.map { CoronavirusFlags.shouldShowBanner = false }
                    .map { Result.Success(DismissCoronavirusBanner) }
        }
    }

    private fun onInfoBannerDismissed(): ObservableTransformer<DismissInformationBannerAction, Result<DismissInfoBanner>> {
        return ObservableTransformer { upstream ->
            upstream
                    .map { Result.Success(DismissInfoBanner(it.centre)) }
        }
    }

    private fun onInfoBannerGoBack(): ObservableTransformer<InformationBannerGoBackAction, Result<InfoBannerGoBack>> {
        return ObservableTransformer { upstream ->
            upstream
                    .map { Result.Success(InfoBannerGoBack(it.mode)) }
        }
    }

    private fun onSearchArea(): ObservableTransformer<SearchAreaAction, Result<SearchArea>> {
        return ObservableTransformer { upstream ->
            upstream
                    .map { Result.Success(SearchArea) }
        }
    }

    private fun Result<LoadAvailabilities>.trackOnErrorIfNeeded() {
        if (this is Error) {
            when (this.type.error) {
                NO_RESULTS -> {
                    adobeAnalytics.track(SEARCH_RESULTS_NO_RESULTS_FOUND, LOOK_TO_BOOK)
                    trackFirebaseErrorEvents()
                }
                GENERIC_ERROR -> {
                    adobeAnalytics.trackError(ErrorBody.create(-1, "Error showing search results"))
                    trackFirebaseErrorEvents()
                }
                else -> {
                    //void
                }
            }
        }
    }

    private fun Result<LoadNewAvailabilities>.trackOnNewAvailabilitiesError() {
        if (this is Error) {
            when (this.type.error) {
                NO_RESULTS -> {
                    adobeAnalytics.track(SEARCH_RESULTS_NO_RESULTS_FOUND, LOOK_TO_BOOK)
                    trackFirebaseErrorEvents()
                }
                GENERIC_ERROR -> {
                    adobeAnalytics.trackError(ErrorBody.create(-1, "Error showing search results"))
                    trackFirebaseErrorEvents()
                }
                else -> {
                    //void
                }
            }
        }
    }

    private fun trackFirebaseErrorEvents() {
        setFirebaseParamsForSRP("false")
    }

    private fun setFirebaseParamsForSRP(isSuccess: String) {
        firebaseLogger.logEvent(FirebaseLogger.Event.GRAPHQL_AVAILABILITIES_CALL,
            FirebaseParams().apply {
                putString(FirebaseParams.ParamName.SRP_SUCCESS_STATUS, isSuccess)
            })
    }

    private fun Throwable.toResultError(): Error<LoadAvailabilities> {
        return if (this is ApiThrowable) {
            when (this) {
                is ApiThrowable.Network -> Error(LoadAvailabilities(error = NETWORK,
                        data = listOf(ErrorItem(errorResId = R.string.search_results_error))))
                is ApiThrowable.Generic, is ApiThrowable.Http -> {
                    errorLogger.logException(this)
                    Error(LoadAvailabilities(error = GENERIC_ERROR,
                            data = listOf(ErrorItem(errorResId = R.string.search_results_error))))
                }
                is GraphQlThrowable.GraphQLError ->  Error(LoadAvailabilities(error = GENERIC_ERROR,
                    data = listOf(ErrorItem(errorResId = R.string.search_results_error))))
            }
        } else {
            errorLogger.logException(this)
            Error(LoadAvailabilities(error = GENERIC_ERROR, data = listOf(ErrorItem(errorResId = R.string.search_results_error))))
        }
    }

    private fun Error<LoadAvailabilities>.toNewAvailabilitiesAction(): Error<LoadNewAvailabilities> {
        return Error(LoadNewAvailabilities(error = this.type.error, data = this.type.data, centre = null))
    }

    private fun newStateFrom(currentState: SearchResultsViewState, result: Result<out SearchResultsViewResult>): SearchResultsViewState {
        return when (result) {
            is Result.Loading -> {
                if (currentState.mode == FULL_MAP || currentState.mode == HOTEL_SELECTED)
                    currentState.copyWithMapLoading(true)
                else
                    currentState.copyWithLoading(LoadingListItem)
            }
            is Result.Success -> when (result.type) {
                is LoadAvailabilities -> currentState.copyWithHotelViewItems(result.type.data!!, result.type.centre!!,
                    showPromoFooterBanner = shouldShowPromoFooterBanner, promoContent = promoContent, promoCode = promoCode)
                is  ChangeUiMode -> currentState.copyWithMode(result.type.mode, result.type.selectedItemId, result.type.requestClose)
                is LoadCriteria -> currentState.copyWithSearchCriteria(
                        result.type.data.searchedLocation,
                        result.type.data.searchItemName,
                        result.type.data.bookingCriteriaFormatted)
                is StoreFullyBookedItems -> currentState.resetRenderProperties().copy(fullyBookedHotelId = result.type.hotelCode)
                is ChangeMapCentre -> currentState.copyWithMapCentre(result.type.centre)
                is HotelSelectedIdle -> currentState.copyWithHotelSelectedIdle(result.type.selectedItem)
                is RequestLoadCurrentState -> currentState.resetRenderProperties(true)
                is CoronaVirusBanner -> currentState.copy(coronaVirusMessage = result.type.message)
                is DismissCoronavirusBanner -> currentState.copyWithDismissBanner()
                is SearchArea -> currentState.copyWithSearchArea()
                is InfoBanner -> currentState.resetRenderProperties()
                is DismissInfoBanner -> currentState.copyWithDismissInfoBanner(result.type.centre)
                is LoadNewAvailabilities -> currentState.copyWithNewSearchCriteria(result.type.centre,
                        resourceProvider.getString(R.string.search_results_visible_map_area), result.type.data!!)
                is InfoBannerGoBack -> currentState.copyWithInfoBannerGoBack(result.type.mode)
                is OpenHDPActivity -> currentState.copyWithOpenHDPActivity(result.type.openHDPActivity, result.type.listItem)
                is ResetOpenHDPActivity -> currentState.resetOpenHDPActivity(result.type.openHDPActivity)
                is ShowFallbackPopup -> currentState.copyWithOpenFallbackPopup(result.type.showFallbackPopup, result.type.fallbackPopupInfo)
                is ResetFallback -> currentState.copyWithFallbackResetOrCancelButton(result.type.showFallbackPopup)
                is OpenHotelInWebView -> currentState.copyWithOpenHotelInWebView()
            }
            is Error -> when (result.type) {
                is LoadAvailabilities -> {
                    if (currentState.mode == FULL_MAP || currentState.mode == HOTEL_SELECTED) {
                        try {
                            currentState.copyWithHotelViewItems(result.type.data!!, result.type.centre!!,
                                showPromoFooterBanner = shouldShowPromoFooterBanner, promoContent = promoContent, promoCode = promoCode)
                        } catch (exception: NullPointerException) {
                            errorLogger.logNonFatalExceptions(exception)
                            errorLogger.setBooleanCustomKey("Is result.type.data list null? :",result.type.data.isNullOrEmpty())
                            errorLogger.setBooleanCustomKey("Is result.type.centre location null? :",result.type.centre == null)
                            currentState.copyWithHotelViewAvailabilityError()
                        }
                    } else
                        currentState.copyWithLoadAvailabilityError(result.type.data, true, result.type.centre)
                }

                is LoadNewAvailabilities -> {
                    currentState.copyWithLoadNewAvailabilityError()
                }

                else -> currentState
            }
        }
    }

    private fun resultFrom(events: Observable<out SearchResultsViewEvent>): Observable<Result<out SearchResultsViewResult>> {
        return events
                .switchMap { actionsMapper.apply(it) }
                .publish { action ->
                    Observable.merge(
                            action.ofType(ChangeModeAction::class.java).compose(onChangeUiMode()),
                            action.ofType(LoadCriteriaAction::class.java).compose(onCriteriaLoad()),
                            action.ofType(PopulateFullyBookedItemsAction::class.java).compose(onPopulateFullyBookedItems()),
                            action.ofType(LoadAvailabilitiesAction::class.java).compose(onAvailabilitiesLoad()))
                            .mergeWith(action.ofType(ChangeMapCentreAction::class.java).compose(onChangeMapCentre()))
                            .mergeWith(action.ofType(HotelSelectedIdleAction::class.java).compose(onHotelSelectedIdle()))
                            .mergeWith(action.ofType(RequestLoadCurrentStateAction::class.java).compose(onRequestLoadCurrentState()))
                            .mergeWith(action.ofType(CheckCoronaVirusBannerAction::class.java).compose(checkShouldShowCoronaVirusBanner()))
                            .mergeWith(action.ofType(DismissCoronavirusAction::class.java).compose(onCoronavirusBannerDismissed()))
                            .mergeWith(action.ofType(DismissInformationBannerAction::class.java).compose(onInfoBannerDismissed()))
                            .mergeWith(action.ofType(InformationBannerGoBackAction::class.java).compose(onInfoBannerGoBack()))
                            .mergeWith(action.ofType(SearchAreaAction::class.java).compose(onSearchArea()))
                            .mergeWith(action.ofType(ShowFallbackOrHDPAction::class.java).compose(showOperaFallbackOrHDP()))
                            .mergeWith(action.ofType(ResetOpenHDPActivityAction::class.java).compose(resetOpenHDPActivity()))
                            .mergeWith(action.ofType(ShowFallbackPopupAction::class.java).compose(showOperaFallbackPopup()))
                            .mergeWith(action.ofType(ResetFallbackAction::class.java).compose(resetOperaFallback()))
                            .mergeWith(action.ofType(OpenHotelInWebViewAction::class.java).compose(openHotelInWebView()))
                            .mergeWith(action.ofType(LoadNewAvailabilitiesAction::class.java).compose(loadNewAvailabilities()))
                }
    }

    private val actionsMapper = Function<SearchResultsViewEvent, Observable<SearchResultsViewAction>> {
        when (it) {
            is ScreenFirstLaunchEvent -> Observable.fromArray(
                LoadAvailabilitiesAction(it.params, it.searchResultInput),
                LoadCriteriaAction(it.params, it.searchResultInput),
                PopulateFullyBookedItemsAction(hotelCode = it.fullyBookedHotel)
            )
            is ChangeModeStateEvent -> Observable.just(ChangeModeAction(it.mode, it.selectedItemId))
            is HotelSelectedIdleEvent -> Observable.just(HotelSelectedIdleAction(it.selectedItem))
            is ChangeMapCentreEvent -> Observable.just(ChangeMapCentreAction(it.centre))
            is RequestAvailabilityEvent -> Observable.just(LoadAvailabilitiesAction(it.params, it.searchResultInput))
            is RequestNewAvailabilityEvent -> Observable.just(LoadNewAvailabilitiesAction(it.params, it.searchResultInput, it.centre))
            is RequestCloseSelfEvent -> Observable.just(ChangeModeAction(mode = LIST_AND_MAP, requestClose = true))
            is ResetCurrentStateRenderEvent -> Observable.just(RequestLoadCurrentStateAction)
            is DismissCoronavirusEvent -> Observable.just(DismissCoronavirusAction)
            is ScreenResumedEvent -> Observable.just(CheckCoronaVirusBannerAction)
            is SearchAreaEvent -> Observable.just(SearchAreaAction)
            is DismissInformationBannerEvent -> Observable.just(DismissInformationBannerAction(null))
            is RequestBannerGoBackEvent -> Observable.just(InformationBannerGoBackAction(it.mode))

            is ShowFallbackOrHDPEvent -> Observable.just(ShowFallbackOrHDPAction(it.listItem))
            is ResetOpenHDPActivityEvent -> Observable.just(ResetOpenHDPActivityAction(false))
            is ShowFallbackPopupEvent -> Observable.just(ShowFallbackPopupAction(it.showFallbackPopup, it.fallbackPopupInfo))
            is ResetFallbackEvent -> Observable.just(ResetFallbackAction(false))
            is OpenHotelInWebViewEvent -> Observable.just(OpenHotelInWebViewAction)
        }
    }

    private fun trackScreenModeChange(latestViewState: SearchResultsViewState) {
        latestAnalyticsDataWithoutMode?.let { analyticsBuilder ->
            if (!latestViewState.isLoading
                    && latestAnalyticsDataWithoutMode != null // Will be not null if availabilities has been loaded
                    && (switchedBetweenMapAndList(latestViewState) || latestViewState.applyVerticalListItemChange)) {
                val isFullMapMode = latestViewState.mode == FULL_MAP
                val screenName = if (isFullMapMode) SEARCH_RESULTS_MAP_NAME else SEARCH_RESULTS_LIST_NAME

                adobeAnalytics.track(screenName, analyticsBuilder
                    .pushToken(simplePersistenceManager.getFirebaseToken())
                    .inMapView(isFullMapMode)
                    .campaignModel(campaignModel)
                    .trackingCode(trackingCode).build())

                adobeAnalytics.trackAction(SEARCH_RESULTS_MAP_VIEW_ACTION, MapViewAnalyticsData(isFullMapMode))
            }
        }

    }

    private fun switchedBetweenMapAndList(newViewState: SearchResultsViewState): Boolean {
        // Checking we've switched from list mode to map mode or vice versa
        return if (newViewState.applyNewUiMode) {
            (isMapMode(newViewState) && isListMode(viewState))
                    || (isListMode(newViewState) && isMapMode(viewState))
        } else {
            false
        }
    }

    private fun isMapMode(viewState: SearchResultsViewState?): Boolean {
        viewState?.let {
            return it.mode in mapViewModes
        } ?: run { return false }
    }

    private fun isListMode(viewState: SearchResultsViewState?): Boolean {
        viewState?.let {
            return it.mode in listViewModes
        } ?: run { return false }
    }
}
