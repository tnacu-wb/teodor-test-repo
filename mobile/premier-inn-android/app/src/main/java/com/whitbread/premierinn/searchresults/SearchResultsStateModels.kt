package com.whitbread.premierinn.searchresults


import android.content.res.Resources
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.view.ListItem
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.domain.common.OperaFallbackPopupInfoDomain
import com.whitbread.premierinn.domain.common.PromoContentDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelAvailabilitiesRequestBody
import com.whitbread.premierinn.domain.search.entity.Location
import com.whitbread.premierinn.searchresults.SearchResultsViewState.ErrorType
import com.whitbread.premierinn.searchresults.SearchResultsViewState.Mode
import com.whitbread.premierinn.searchresults.SearchResultsViewState.Mode.FULL_MAP
import com.whitbread.premierinn.searchresults.SearchResultsViewState.Mode.HOTEL_SELECTED
import com.whitbread.premierinn.searchresults.SearchResultsViewState.Mode.LIST_AND_MAP
import com.whitbread.premierinn.searchresults.SearchResultsViewState.Mode.LIST_EXPANDED


/**
 *
 */
private val initMapCenterInLondon = Location(51.508101, -0.1270057)
private const val FIVE_MILES = 5.0

data class SearchResultsViewState(val mode: Mode = LIST_AND_MAP,
                                  val toolbarTitle: String? = null,
                                  val toolbarSubTitle: String? = null,
                                  val sortingFiltersShown: Boolean = true,
                                  val listButtonShown: Boolean = false,
                                  val mapCenter: Location = initMapCenterInLondon,
                                  val verticalListViewItems: List<ListItem>? = null,
                                  val previousSuccessfulListViewItems: List<ListItem>? = null,
                                  val isLayoutManagerVertical: Boolean = true,
                                  val searchedLocation: Location? = null,
                                  val initialSearchedLocation: Location? = null,
                                  val previousSuccessfulSearchedLocation: Location? = null,
                                  val fullyBookedHotelId: String? = null,
                                  val selectedItem: HotelListItem? = null,
                                  val previouslySelectedItem: HotelListItem? = null,
                                  val withHideAnimationInHorizontalList: Boolean = false,
                                  val applyNewUiMode: Boolean = true,
                                  val applyToolbarChanges: Boolean = false,
                                  val applyChangesOnRecyclerView: Boolean = true,
                                  val applyMapBoundsChange: Boolean = true,
                                  val applyNewMapCenter: Boolean = true,
                                  val applyVerticalListItemChange: Boolean = false,
                                  val applyMapPinsChange: Boolean = false,
                                  val scrollToSelectedAfterRenderingHorizontalItems: Boolean = false,
                                  val scrollToSelectedHotel: Boolean = false,
                                  val scrollToSelectedHotelAnimated: Boolean = false,
                                  val clearPreviousMapPin: Boolean = false,
                                  val applyListItemSelectionChange: Boolean = false,
                                  val viewCloses: Boolean = false,
                                  val backToPreviousSuccessfulResults : Boolean = false,
                                  val coronaVirusMessage: String? = null,
                                  val showInfoBanner: Boolean = false,
                                  val searchArea: Boolean = false,
                                  val isMapLoading: Boolean = false,
                                  val showSearchedLocationPin: Boolean = true,
                                  val showMapNoHotelError: Boolean = false,
                                  val openHDPActivity: Boolean = false,
                                  val showFallbackPopup: Boolean = false,
                                  val fallbackPopupInfo: OperaFallbackPopupInfoDomain? = null,
                                  val openHotelInWebViewEvent: Boolean = false,
                                  val showPromoFooterBanner: Boolean = false,
                                  val promoContent: PromoContentDomain? = null,
                                  val promoCode: String = EMPTY_STRING) {

    val showCoronavirusMessage = isLayoutManagerVertical && !coronaVirusMessage.isNullOrEmpty()

    val searchedLocationName = toolbarTitle

    val mapViewItems: List<HotelListItem> = run {
        if (backToPreviousSuccessfulResults)
            previousSuccessfulListViewItems.toMapViewItems()
        else
            verticalListViewItems.toMapViewItems()
    }

    val isLoading: Boolean = run {
        verticalListViewItems?.contains(LoadingListItem) ?: false
    }

    fun isMapCentered(): Boolean {
        return searchedLocation?.let {
            distance(mapCenter.latitude, mapCenter.longitude,
                    searchedLocation.latitude, searchedLocation.longitude) < FIVE_MILES
        } ?: let {
            false
        }
    }

    private fun tickOrNothing(value: Boolean): String = if (value) " ✔" else "  "
    override fun toString(): String =
            """ -------->
                inFlight = $isLoading
                ${tickOrNothing(applyNewUiMode)} Mode = $mode
                ${tickOrNothing(applyNewUiMode)} SortingFiltersVisible = $sortingFiltersShown vs listButtonShown = $listButtonShown
                ${tickOrNothing(applyToolbarChanges)} Toolbar = $toolbarTitle - $toolbarSubTitle
                ${tickOrNothing(applyChangesOnRecyclerView)} Change RecyclerViewMode Vertical = $isLayoutManagerVertical HideHorizontaListWithAnimation = $withHideAnimationInHorizontalList
                ${tickOrNothing(applyMapBoundsChange)} MapBoundsChange = $applyMapBoundsChange
                ${tickOrNothing(applyNewMapCenter)} MapCenter = $mapCenter
                ${tickOrNothing(applyVerticalListItemChange)} Vertical List id = ${verticalListViewItems?.hashCode()} size = ${verticalListViewItems?.size}
                ${tickOrNothing(applyMapPinsChange)} Map Pins Changed with Horizontal Map Items id = ${mapViewItems.hashCode()} size = ${mapViewItems.size}
                ${tickOrNothing(scrollToSelectedAfterRenderingHorizontalItems)} Init horizontal Item & Scroll To Selected = ${selectedItem?.id()}
                ${tickOrNothing(scrollToSelectedHotel)} Scroll To Selected From Hidden = ${selectedItem?.id()}
                ${tickOrNothing(scrollToSelectedHotelAnimated)} Scroll To Selected Animated = ${selectedItem?.id()}
                ${tickOrNothing(clearPreviousMapPin)} Clear Prev Selected Pin = ${previouslySelectedItem?.id()}
                ${tickOrNothing(applyListItemSelectionChange)} Mark selected List Item = ${selectedItem?.id()} and clear = ${previouslySelectedItem?.id()}
                ${tickOrNothing(viewCloses)} Screen closes = $viewCloses
                SearchedLocation = $searchedLocation
                ListButtonShown = $listButtonShown
                FullyBookedHotelId = $fullyBookedHotelId
                isMapLoading = $isMapLoading
                --------|
    """.trimIndent()

    enum class Mode {
        FULL_MAP, LIST_AND_MAP, HOTEL_SELECTED, LIST_EXPANDED
    }

    enum class ErrorType {
        NO_RESULTS,
        NETWORK,
        GENERIC_ERROR
    }
}


fun List<ListItem>?.toMapViewItems(): List<HotelListItem> {
    return this?.filter { it is HotelListItem }?.map { it as HotelListItem }?.map { it.copy(type = R.layout.item_search_results_horizontal) }.orEmpty()
}

fun List<ListItem>?.toListViewItems(): List<HotelListItem> {
    return this?.filter { it is HotelListItem }?.map { it as HotelListItem }?.map { it.copy(type = R.layout.item_search_results_hotel_details) }.orEmpty()
}

fun SearchResultsViewState.resetRenderProperties(forceUpdate: Boolean = false): SearchResultsViewState {
    return this.copy(
            applyNewUiMode = forceUpdate,
            applyNewMapCenter = forceUpdate,
            applyToolbarChanges = forceUpdate,
            applyListItemSelectionChange = false,
            applyVerticalListItemChange = forceUpdate && mode == LIST_AND_MAP,
            applyMapPinsChange = forceUpdate,
            applyMapBoundsChange = forceUpdate,
            applyChangesOnRecyclerView = forceUpdate,
            scrollToSelectedAfterRenderingHorizontalItems = forceUpdate && mode == HOTEL_SELECTED,
            scrollToSelectedHotel = forceUpdate && selectedItem != null,
            scrollToSelectedHotelAnimated = forceUpdate && selectedItem != null,
            clearPreviousMapPin = forceUpdate && previouslySelectedItem != null,
            withHideAnimationInHorizontalList = false,
            viewCloses = false,
            backToPreviousSuccessfulResults = false,
            isMapLoading = false,
            searchArea = false
    )
}
fun SearchResultsViewState.copyWithOpenHDPActivity(openHDPActivity: Boolean, listItem: HotelListItem): SearchResultsViewState {
    return this.copy(
        openHDPActivity = openHDPActivity,
        selectedItem = listItem
    )
}

fun SearchResultsViewState.resetOpenHDPActivity(openHDPActivity: Boolean): SearchResultsViewState {
    return this.copy(
        openHDPActivity = openHDPActivity
    )
}

fun SearchResultsViewState.copyWithOpenFallbackPopup(showFallbackPopup: Boolean, fallbackPopupInfo: OperaFallbackPopupInfoDomain): SearchResultsViewState {
    return this.copy(
        showFallbackPopup = showFallbackPopup,
        fallbackPopupInfo = fallbackPopupInfo
    )
}

fun SearchResultsViewState.copyWithFallbackResetOrCancelButton(showFallbackPopup: Boolean): SearchResultsViewState {
    return this.copy(
        showFallbackPopup = showFallbackPopup,
    )
}

fun SearchResultsViewState.copyWithOpenHotelInWebView(): SearchResultsViewState {
    return if(openHotelInWebViewEvent) {
        this.copy(openHotelInWebViewEvent = false)
    } else {
        this.copy(openHotelInWebViewEvent = true)
    }
}

fun SearchResultsViewState.isLayoutMangerVertical(newMode: Mode): Boolean {
    return when (newMode) {
        LIST_AND_MAP -> true
        HOTEL_SELECTED -> false
        else -> this.isLayoutManagerVertical
    }
}

fun SearchResultsViewState.copyWithMode(newMode: Mode, newSelectedItemId: String?, requestClose: Boolean?): SearchResultsViewState {
    val previousMode = mode
    return if(verticalListViewItems.isNullOrEmpty() && !previousSuccessfulListViewItems.isNullOrEmpty()){
        copyWithInfoBannerGoBack(newMode)
    }else if (requestClose == true && previousMode == LIST_AND_MAP && newMode == LIST_AND_MAP) {
        resetRenderProperties().copy(viewCloses = true, verticalListViewItems = verticalListViewItems)
    } else {
        val diffMode = newMode != previousMode
        val newSelectedItem = mapViewItems.find { newSelectedItemId == it.id() }

        val itemsList: List<ListItem> = if (previousSuccessfulListViewItems.isNullOrEmpty() || previousSuccessfulListViewItems.contains(ErrorItem(errorResId = R.string.search_results_no_hotel_available))){
            verticalListViewItems!!
        }else {
            if (newMode == HOTEL_SELECTED) previousSuccessfulListViewItems.toMapViewItems() else previousSuccessfulListViewItems.toListViewItems()
        }

        resetRenderProperties().copy(mode = newMode,
                applyNewUiMode = diffMode,
                sortingFiltersShown = newMode == LIST_AND_MAP,
                listButtonShown = newMode == FULL_MAP || newMode == HOTEL_SELECTED,
                withHideAnimationInHorizontalList = previousMode == HOTEL_SELECTED && newMode == LIST_AND_MAP,
                selectedItem = newSelectedItem,
                clearPreviousMapPin = (diffMode || newMode == HOTEL_SELECTED) && selectedItem != null && newSelectedItem != selectedItem,
                previouslySelectedItem = if (newSelectedItem != selectedItem) selectedItem else null,
                verticalListViewItems = itemsList,
                applyVerticalListItemChange = newMode == HOTEL_SELECTED || previousMode == HOTEL_SELECTED,
                scrollToSelectedAfterRenderingHorizontalItems = isLayoutManagerVertical && newMode == HOTEL_SELECTED && newSelectedItem != null,
                isLayoutManagerVertical = isLayoutMangerVertical(newMode),
                scrollToSelectedHotel = diffMode && newMode == HOTEL_SELECTED && !isLayoutManagerVertical && !scrollToSelectedAfterRenderingHorizontalItems && newSelectedItem != null,
                scrollToSelectedHotelAnimated = !diffMode && newMode == HOTEL_SELECTED && newSelectedItem != null,
                applyMapBoundsChange = diffMode && newMode != LIST_EXPANDED,
                applyNewMapCenter = diffMode && (newMode == LIST_AND_MAP || (newMode == FULL_MAP && previousMode == LIST_AND_MAP)),
                applyChangesOnRecyclerView = isLayoutManagerVertical != isLayoutMangerVertical(newMode))
    }
}

fun SearchResultsViewState.copyWithMapCentre(newCentre: Location?): SearchResultsViewState {
    val resetCenterRequired = newCentre == null // else update MapCenter property
    return if (resetCenterRequired) {
        resetRenderProperties().copy(applyNewMapCenter = searchedLocation != null, showInfoBanner = false, applyNewUiMode = true)
    } else {
        resetRenderProperties().copy(mapCenter = newCentre!!, applyNewMapCenter = false, showInfoBanner = false, applyNewUiMode = true)
    }
}

fun SearchResultsViewState.copyWithHotelSelectedIdle(newSelectedItem: HotelListItem): SearchResultsViewState {
    val newPrevSelectedItem = if (newSelectedItem != selectedItem) selectedItem else previouslySelectedItem
    return this.resetRenderProperties().copy(selectedItem = newSelectedItem,
            previouslySelectedItem = newPrevSelectedItem,
            clearPreviousMapPin = newPrevSelectedItem != null,
            applyListItemSelectionChange = true)
}

fun SearchResultsViewState.copyWithLoading(loadingItem: ListItem): SearchResultsViewState {
    val listItems = mutableListOf<ListItem>(loadingItem)
    return this.resetRenderProperties().copy(
            verticalListViewItems = listItems,
            applyVerticalListItemChange = verticalListViewItems != emptyList<ListItem>()
    )
}

fun SearchResultsViewState.copyWithMapLoading(isMapLoading: Boolean): SearchResultsViewState {
    return this.resetRenderProperties().copy(isMapLoading = isMapLoading, applyNewUiMode = true)
}

fun SearchResultsViewState.copyWithHotelViewItems(
    newList: List<ListItem>,
    initialLocation: Location,
    newMode: Mode? = null,
    showPromoFooterBanner: Boolean = this.showPromoFooterBanner,
    promoContent: PromoContentDomain? = this.promoContent,
    promoCode: String = this.promoCode
): SearchResultsViewState {
    val prevList = verticalListViewItems
    val listItems = if (newList.contains(ErrorItem(errorResId = R.string.search_results_no_hotel_available)) || mode != HOTEL_SELECTED)
        newList
    else
        newList.toMapViewItems()
    return if (fullyBookedHotelId != null) {
        val fullyBookedItem = listItems.firstOrNull { it.id() == fullyBookedHotelId }
        if (fullyBookedItem != null) {
            val joinedList = listOf(ErrorItem(R.string.search_results_hotel_fully_booked_message), fullyBookedItem) + listItems.filter { it.id() != fullyBookedHotelId }
            this.resetRenderProperties().copy(
                initialSearchedLocation = initialLocation,
                verticalListViewItems = joinedList,
                applyMapPinsChange = true,
                applyVerticalListItemChange = prevList != joinedList,
                showPromoFooterBanner = showPromoFooterBanner,
                promoContent = promoContent,
                promoCode = promoCode
            )
        } else {
            val joinedList = listOf(ErrorItem(R.string.search_results_hotel_fully_booked_message)) + listItems
            this.resetRenderProperties().copy(
                initialSearchedLocation = initialLocation,
                verticalListViewItems = joinedList,
                applyMapPinsChange = true,
                applyVerticalListItemChange = prevList != joinedList,
                showPromoFooterBanner = showPromoFooterBanner,
                promoContent = promoContent,
                promoCode = promoCode
            )
        }
    } else {
        this.resetRenderProperties().copy(
            mode = newMode ?: mode,
            initialSearchedLocation = initialLocation,
            applyNewUiMode = mode == HOTEL_SELECTED,
            verticalListViewItems = listItems,
            previousSuccessfulListViewItems = listItems,
            previousSuccessfulSearchedLocation = searchedLocation,
            applyMapPinsChange = true,
            applyVerticalListItemChange = prevList != listItems,
            showPromoFooterBanner = showPromoFooterBanner,
            promoContent = promoContent,
            promoCode = promoCode
        )
    }
}

fun SearchResultsViewState.copyWithSearchCriteria(newLocation: Location, newToolbarTitle: String, newToolbarSubTitle: String): SearchResultsViewState {
    return this.resetRenderProperties().copy(
            mapCenter = newLocation,
            applyNewMapCenter = false,
            searchedLocation = newLocation,
            toolbarTitle = newToolbarTitle,
            toolbarSubTitle = newToolbarSubTitle,
            applyToolbarChanges = toolbarTitle != newToolbarTitle)
}

fun SearchResultsViewState.copyWithNewSearchCriteria(newLocation: Location?, newToolbarTitle: String, newList: List<ListItem>): SearchResultsViewState {
    val prevList = verticalListViewItems

    val newListItems = if (mode == HOTEL_SELECTED) newList.toMapViewItems() else newList.toListViewItems()

    return if (fullyBookedHotelId != null) {
        val fullyBookedItem = newList.firstOrNull { it.id() == fullyBookedHotelId }
        if (fullyBookedItem != null) {
            val joinedList = listOf(ErrorItem(R.string.search_results_hotel_fully_booked_message), fullyBookedItem) + newListItems.filter { it.id() != fullyBookedHotelId }
            this.resetRenderProperties().copy(
                    verticalListViewItems = joinedList,
                    previousSuccessfulListViewItems = newListItems,
                    applyMapPinsChange = true,
                    applyVerticalListItemChange = prevList != joinedList,
                    mapCenter = newLocation ?: searchedLocation ?: initMapCenterInLondon,
                    applyNewMapCenter = false,
                    searchedLocation = newLocation,
                    toolbarTitle = newToolbarTitle,
                    applyToolbarChanges = true,
                    searchArea = false,
                    isMapLoading = false,
                    showSearchedLocationPin = false,
                    applyChangesOnRecyclerView = true,
                    showMapNoHotelError = false)
        } else {
            val joinedList = listOf(ErrorItem(R.string.search_results_hotel_fully_booked_message)) + newListItems
            this.resetRenderProperties().copy(
                    verticalListViewItems = joinedList,
                    previousSuccessfulListViewItems = newListItems,
                    applyMapPinsChange = true,
                    applyVerticalListItemChange = prevList != joinedList,
                    mapCenter = newLocation ?: searchedLocation ?: initMapCenterInLondon,
                    applyNewMapCenter = false,
                    searchedLocation = newLocation,
                    toolbarTitle = newToolbarTitle,
                    applyToolbarChanges = true,
                    searchArea = false,
                    showSearchedLocationPin = false,
                    isMapLoading = false,
                    applyChangesOnRecyclerView = true,
                    showMapNoHotelError = false)
        }
    } else {
        this.resetRenderProperties().copy(
                verticalListViewItems = newListItems,
                previousSuccessfulListViewItems = newListItems,
                applyMapPinsChange = true,
                applyVerticalListItemChange = prevList != newListItems,
                mapCenter = newLocation ?: searchedLocation ?: initMapCenterInLondon,
                applyNewMapCenter = false,
                searchedLocation = newLocation,
                previousSuccessfulSearchedLocation = newLocation,
                toolbarTitle = newToolbarTitle,
                showSearchedLocationPin = false,
                searchArea = false,
                applyToolbarChanges = true,
                isMapLoading = false,
                applyChangesOnRecyclerView = prevList != newListItems,
                showMapNoHotelError = false)
    }
}

fun SearchResultsViewState.copyWithInfoBannerGoBack(mode: Mode?): SearchResultsViewState {
    return this.resetRenderProperties().copy(
            mode = mode ?: FULL_MAP,
            sortingFiltersShown = mode == LIST_AND_MAP,
            applyNewUiMode = mode != null,
            verticalListViewItems = previousSuccessfulListViewItems,
            previousSuccessfulListViewItems =  previousSuccessfulListViewItems,
            applyMapPinsChange = true,
            applyVerticalListItemChange = true,
            mapCenter = searchedLocation ?: initMapCenterInLondon,
            applyNewMapCenter = true,
            searchedLocation = previousSuccessfulSearchedLocation,
            toolbarTitle = searchedLocationName,
            applyToolbarChanges = true,
            searchArea = false,
            showSearchedLocationPin = false,
            applyChangesOnRecyclerView = true,
            isLayoutManagerVertical = if (mode != null) isLayoutMangerVertical(mode) else true,
            backToPreviousSuccessfulResults = true)
}

fun Mode.toMapPadding(resources: Resources, rootLayoutHeight: Int): Int {
    return when (this) {
        HOTEL_SELECTED -> {
            resources.getDimensionPixelSize(R.dimen.search_results_horizontal_item_height) + (resources.getDimensionPixelSize(R.dimen.default_medium_margin))
        }
        LIST_AND_MAP -> rootLayoutHeight
        else -> 0
    }
}

fun SearchResultsViewState.copyWithDismissBanner(): SearchResultsViewState {
    return this.copy(coronaVirusMessage = null)
}


fun SearchResultsViewState.copyWithDismissInfoBanner(location: Location?): SearchResultsViewState {
    return this.copy(showInfoBanner = false, searchedLocation = location, showMapNoHotelError = false)
}

fun SearchResultsViewState.copyWithSearchArea(): SearchResultsViewState {
    return this.copy(searchArea = true, searchedLocation = mapCenter)
}

fun SearchResultsViewState.copyWithLoadAvailabilityError(verticalListViewItems: List<ListItem>?, applyVerticalListItemChange: Boolean, initialLocation: Location?): SearchResultsViewState {
    return this.resetRenderProperties().copy(
            initialSearchedLocation = initialLocation,
            verticalListViewItems = verticalListViewItems,
            applyVerticalListItemChange = applyVerticalListItemChange,
            showMapNoHotelError = true)
}

fun SearchResultsViewState.copyWithHotelViewAvailabilityError(): SearchResultsViewState {
    return this.resetRenderProperties().copy(
            mode = FULL_MAP,
            verticalListViewItems = emptyList(),
            showInfoBanner = true,
            isMapLoading = false,
            applyNewUiMode = true,
            showMapNoHotelError = true)
}

fun SearchResultsViewState.copyWithLoadNewAvailabilityError(): SearchResultsViewState {
    return this.resetRenderProperties().copy(
            mode = FULL_MAP,
            verticalListViewItems = emptyList(),
            showInfoBanner = true,
            isMapLoading = false,
            applyNewUiMode = true,
            showMapNoHotelError = false)
}

sealed class SearchResultsViewEvent {
    data class RequestCloseSelfEvent(val timestamp: Long) : SearchResultsViewEvent()
    object ResetCurrentStateRenderEvent : SearchResultsViewEvent()
    data class HotelSelectedIdleEvent(val selectedItem: HotelListItem) : SearchResultsViewEvent()
    data class ChangeMapCentreEvent(val centre: Location?) : SearchResultsViewEvent()
    data class ScreenFirstLaunchEvent(val params: HotelAvailabilitiesRequestBody, val searchResultInput: SearchResultsInput,
                                      val fullyBookedHotel: String?) : SearchResultsViewEvent()
    data class ChangeModeStateEvent(val mode: Mode, val selectedItemId: String? = null) : SearchResultsViewEvent()
    class RequestAvailabilityEvent(val params: HotelAvailabilitiesRequestBody, val searchResultInput: SearchResultsInput) : SearchResultsViewEvent()
    data class RequestBannerGoBackEvent(val mode: Mode?) : SearchResultsViewEvent()
    data class DismissInformationBannerEvent(val centre: Location?) : SearchResultsViewEvent()
    class RequestNewAvailabilityEvent(val params: HotelAvailabilitiesRequestBody, val searchResultInput: SearchResultsInput,
                                      val centre: Location?) : SearchResultsViewEvent()
    object DismissCoronavirusEvent : SearchResultsViewEvent()
    object ScreenResumedEvent : SearchResultsViewEvent()
    object SearchAreaEvent : SearchResultsViewEvent()
    data class ShowFallbackOrHDPEvent(val listItem: HotelListItem) : SearchResultsViewEvent()
    data class ResetOpenHDPActivityEvent(val openHDPActivity: Boolean) : SearchResultsViewEvent()
    data class ShowFallbackPopupEvent(val showFallbackPopup: Boolean, val fallbackPopupInfo: OperaFallbackPopupInfoDomain) : SearchResultsViewEvent()
    data class ResetFallbackEvent(val showFallbackPopup: Boolean) : SearchResultsViewEvent()
    object OpenHotelInWebViewEvent : SearchResultsViewEvent()
}

sealed class SearchResultsViewAction {
    object RequestLoadCurrentStateAction : SearchResultsViewAction()
    data class PopulateFullyBookedItemsAction(val hotelCode: String?) : SearchResultsViewAction()
    data class HotelSelectedIdleAction(val selectedItem: HotelListItem) : SearchResultsViewAction()
    data class ChangeModeAction(val mode: Mode, val selectedItemId: String? = null, val requestClose: Boolean = false) : SearchResultsViewAction()
    data class ChangeMapCentreAction(val centre: Location?) : SearchResultsViewAction()
    data class LoadCriteriaAction(val params: HotelAvailabilitiesRequestBody, val searchResultsInput: SearchResultsInput) : SearchResultsViewAction()
    data class LoadAvailabilitiesAction(val params: HotelAvailabilitiesRequestBody, val searchResultInput: SearchResultsInput) : SearchResultsViewAction()
    data class DismissInformationBannerAction(val centre: Location?) : SearchResultsViewAction()
    data class InformationBannerGoBackAction(val mode: Mode?) : SearchResultsViewAction()
    object CheckCoronaVirusBannerAction : SearchResultsViewAction()
    data class LoadNewAvailabilitiesAction(val params: HotelAvailabilitiesRequestBody, val searchResultInput: SearchResultsInput,
                                           val centre: Location?) : SearchResultsViewAction()
    object DismissCoronavirusAction : SearchResultsViewAction()
    object SearchAreaAction : SearchResultsViewAction()
    data class ShowFallbackOrHDPAction(val listItem: HotelListItem) : SearchResultsViewAction()
    data class ResetOpenHDPActivityAction(val openHDPActivity: Boolean) : SearchResultsViewAction()
    data class ShowFallbackPopupAction(val showFallbackPopup: Boolean, val fallbackPopupInfo: OperaFallbackPopupInfoDomain) : SearchResultsViewAction()
    data class ResetFallbackAction(val showFallbackPopup: Boolean) : SearchResultsViewAction()
    object OpenHotelInWebViewAction : SearchResultsViewAction()
}

sealed class SearchResultsViewResult {
    object RequestLoadCurrentState : SearchResultsViewResult()
    data class StoreFullyBookedItems(val hotelCode: String?) : SearchResultsViewResult()
    data class HotelSelectedIdle(val selectedItem: HotelListItem) : SearchResultsViewResult()
    data class ChangeUiMode(val mode: Mode, val selectedItemId: String? = null, val requestClose: Boolean = false) : SearchResultsViewResult()
    data class ChangeMapCentre(val centre: Location?) : SearchResultsViewResult()
    data class LoadAvailabilities(val data: List<ListItem>? = null, val centre: Location? = null, val error: ErrorType? = null) : SearchResultsViewResult()
    data class DismissInfoBanner(val centre: Location?) : SearchResultsViewResult()
    data class LoadNewAvailabilities(val data: List<ListItem>? = null, val error: ErrorType? = null, val centre: Location?) : SearchResultsViewResult()
    data class LoadCriteria(val data: BookingCriteriaUi) : SearchResultsViewResult()
    data class CoronaVirusBanner(val message: String?) : SearchResultsViewResult()
    data class InfoBannerGoBack(val mode: Mode?) : SearchResultsViewResult()
    object DismissCoronavirusBanner : SearchResultsViewResult()
    object InfoBanner : SearchResultsViewResult()
    object SearchArea : SearchResultsViewResult()
    data class OpenHDPActivity(val openHDPActivity: Boolean, val listItem: HotelListItem) : SearchResultsViewResult()
    data class ResetOpenHDPActivity(val openHDPActivity: Boolean) : SearchResultsViewResult()
    data class ShowFallbackPopup(val showFallbackPopup: Boolean, val fallbackPopupInfo: OperaFallbackPopupInfoDomain) : SearchResultsViewResult()
    data class ResetFallback(val showFallbackPopup: Boolean) : SearchResultsViewResult()
    object OpenHotelInWebView : SearchResultsViewResult()
}