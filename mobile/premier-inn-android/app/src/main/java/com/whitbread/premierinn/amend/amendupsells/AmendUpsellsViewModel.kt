
package com.whitbread.premierinn.amend.amendupsells

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whitbread.premierinn.amend.AmendStringProvider
import com.whitbread.premierinn.amend.analytics.AmendMealsData
import com.whitbread.premierinn.amend.toListOfAmendRoomsSelections
import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.api.response.ErrorBody
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.ciol.utils.isKidsMeal
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.AMEND_MEALS
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.format.DateFormat
import com.whitbread.premierinn.common.format.PriceFormat
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.common.summary.SummaryExtrasToggleHelper
import com.whitbread.premierinn.common.summary.SummaryMealsIncrementDecrementHelper
import com.whitbread.premierinn.common.summary.model.SummaryExtrasItem
import com.whitbread.premierinn.common.summary.model.SummaryMealItem
import com.whitbread.premierinn.common.summary.model.SummaryPrice
import com.whitbread.premierinn.common.summary.model.SummaryRoomItem
import com.whitbread.premierinn.common.utils.filterAncillaryCloseOutItems
import com.whitbread.premierinn.common.utils.getUpsellItemsAllowed
import com.whitbread.premierinn.common.utils.isRestaurantClosed
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl
import com.whitbread.premierinn.domain.common.AppDispatchers
import com.whitbread.premierinn.domain.common.DONATION_LIST
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.OPERA_FREE_CHILD_BREAKFAST
import com.whitbread.premierinn.domain.common.Upsell
import com.whitbread.premierinn.domain.common.hoteldetails.entity.AncillaryCloseOutItem
import com.whitbread.premierinn.domain.graphql.amend.usecase.GetUpsellsAndAncillaryCloseoutUseCase
import com.whitbread.premierinn.domain.graphql.amend.usecase.GraphQLAmendUseCase
import com.whitbread.premierinn.domain.graphql.hdp.entity.ExtrasItemDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.MealDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.UpsellDomainItem
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AmendRoomsSelections
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AmendSelectedPackages
import com.whitbread.premierinn.domain.graphql.requestBodyModels.UpdateReservationWithAncillariesRequestBody
import com.whitbread.premierinn.domain.graphql.utils.getDescription
import com.whitbread.premierinn.domain.graphql.utils.getId
import com.whitbread.premierinn.domain.graphql.utils.getName
import com.whitbread.premierinn.domain.graphql.utils.getNumberOfSelections
import com.whitbread.premierinn.domain.graphql.utils.getShortDescription
import com.whitbread.premierinn.domain.graphql.utils.isFreeForChildren
import com.whitbread.premierinn.domain.reservation.entity.Reservation
import com.whitbread.premierinn.domain.reservation.entity.RoomSelectedUpsells
import com.whitbread.premierinn.domain.reservation.usecase.ObserveAmendedReservationUseCase
import com.whitbread.premierinn.domain.reservation.usecase.meals.UpdateAllRoomUpsellUseCase
import com.whitbread.premierinn.domain.result.Result
import com.whitbread.premierinn.summary.models.ParcelableMenuAndAllergyInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.rx2.asFlow
import kotlinx.coroutines.rx2.await
import kotlinx.coroutines.withContext
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AmendUpsellsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val deviceLocaleProvider: DeviceLocaleProvider,
    private val trackingAnalytics: TrackingAnalytics,
    private val crashlyticsLogger: LogService,
    private val graphQLAmendUseCase: GraphQLAmendUseCase,
    private val getUpsellsAndAncillariesCloseoutUseCase: GetUpsellsAndAncillaryCloseoutUseCase,
    private val observeAmendedReservationUseCase: ObserveAmendedReservationUseCase,
    private val summaryMealsHelper: SummaryMealsIncrementDecrementHelper,
    private val summaryExtrasHelper: SummaryExtrasToggleHelper,
    private val updateAllRoomUpsellUseCase: UpdateAllRoomUpsellUseCase,
    private val storage: SimplePersistenceManagerImpl,
    private val businessStorage: BusinessPersistenceManager,
    private val amendStringProvider: AmendStringProvider,
    private val dispatchers: AppDispatchers
) : ViewModel() {

    private val input: AmendUpsellsInput by lazy {
        requireNotNull(savedStateHandle.get<AmendUpsellsInput>(AMEND_UPSELLS_INPUT))
    }

    private var shouldShowPackagesLoading = true
    private var shouldShowHotelInfoLoading = true

    private val _state = MutableStateFlow(AmendUpsellsState())
    val state: StateFlow<AmendUpsellsState> = _state

    init {

        initHotelHeader()

        getAmendedReservation()
        getPackagesAndAncillaryCloseout()

        initState()
    }

    fun initHotelHeader() {
        _state.update {
            shouldShowHotelInfoLoading = false
            manageLoading()
            it.copy(
                galleryImageSrc = input.galleryImages,
                hotelName = input.hotelName
            )
        }
    }

    private fun initState() {
        _state.update { it.copy(
            isDinnerAllowance = input.manageBookingInput.dinnerAllowance() > 0,
            showApprovedMeals = input.manageBookingInput.isBusinessBooking
        ) }
    }

    private fun getPackagesAndAncillaryCloseout() = viewModelScope.launch {
        val packagesRequestBody = input.createPackagesRequestBody(deviceLocaleProvider)

        shouldShowPackagesLoading = true

        getUpsellsAndAncillariesCloseoutUseCase(packagesRequestBody).collect { result ->
            _state.update {
                when (result) {
                    is Result.Success -> {
                        val ancillariesCloseoutList = filterAncillaryCloseOutItems(result.data.ancillaryCloseout,
                            input.arrivalDeparturePair.first, input.arrivalDeparturePair.second)


                        val availableUpsells = if (input.manageBookingInput.isBusinessBooking) {
                            filterAvailableUpsellsForBusinessBooking(result.data.availableUpsells)
                        } else { result.data.availableUpsells }

                        val filterUpsellsBasedOnAncillariesCloseout = if (ancillariesCloseoutList.isNotEmpty()) {
                            availableUpsells.filter { upsell ->
                                upsell.getId() !in ancillariesCloseoutList
                            }.toMutableList()
                        } else {
                            availableUpsells
                        }
                        val roomList = setRoomList(filterUpsellsBasedOnAncillariesCloseout)

                        val adultMeals = filterUpsellsBasedOnAncillariesCloseout
                            .filterIsInstance<MealDomain>()
                            .filterNot { meals -> meals.id?.isKidsMeal() == true }

                        trackAvailableUpsells(filterUpsellsBasedOnAncillariesCloseout)
                        shouldShowPackagesLoading = false
                        manageLoading()

                        it.copy(
                            listOfGalleryUpsellImages = setGalleryImages(availableUpsells),
                            availableUpsells = filterUpsellsBasedOnAncillariesCloseout,
                            roomList = roomList,
                            listOfAncillaryCloseoutItem  = result.data.ancillaryCloseout,
                            isRestaurantClosed = ancillariesCloseoutList.isRestaurantClosed(adultMeals),
                            areAllMealsRestricted = roomList.none { room -> room.meals.isNotEmpty() }

                        )
                    }

                    is Result.Error -> {
                        trackingAnalytics.trackError(ErrorBody.create(-1, amendStringProvider.trackAmendUpsellsError))
                        crashlyticsLogger.log("Get Packages call failed" + result.error.message)
                        shouldShowPackagesLoading = false
                        manageLoading()
                        sendEvent(AmendUpsellsEvent.GeneralError)
                        it
                    }
                }
            }
        }
    }

    private fun getAmendedReservation() = viewModelScope.launch {
        observeAmendedReservationUseCase.invoke(input.manageBookingInput.bookingReference()).asFlow().collect { reservation ->
            val (numberOfChildren: Int, numberAdults: Int) = getGuestCount(reservation)

            _state.update {
                it.copy(
                    reservation = reservation,
                    numberOfRooms = reservation.roomsCriteria.size,
                    numberOfGuests = numberOfChildren + numberAdults
                )
            }
        }
    }

    fun onAction(action: AmendUpsellsAction) {
        when (action) {
            is AmendUpsellsAction.MealIncrement -> {
                mealIncrementCounter(action.roomIndex, action.mealIndex)
            }
            is AmendUpsellsAction.MealDecrement -> {
                mealDecrementCounter(action.roomIndex, action.mealIndex)
            }
            is AmendUpsellsAction.ExtraToggled ->{
                updateExtraToggled(action.roomIndex, action.extraIndex, action.enabled)
            }
            is AmendUpsellsAction.BackClicked -> {
                sendEvent(AmendUpsellsEvent.NavigateBack)
            }
            is AmendUpsellsAction.ContinueClicked -> {
                updateAmend()
            }
            is AmendUpsellsAction.MenuAndAllergyInfoClicked -> {
                val menuAndAllergyInfoList = getMenuAndAllergyInfoList(action.isAllergyInfo)
                sendEvent(AmendUpsellsEvent.ShowMenuAndAllergyInfo(menuAndAllergyInfoList))
            }
        }
    }

    private fun updateAmend() {
        val amendUpsellsSelection = mapRoomListToSelectedUpsells()
        viewModelScope.launch {
            updateAllRoomUpsellUseCase.invoke(input.manageBookingInput.bookingReference(), amendUpsellsSelection).asFlow().collect { result ->
                when(result) {
                    is UpdateAllRoomUpsellUseCase.UpdateUpsellState.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is UpdateAllRoomUpsellUseCase.UpdateUpsellState.Updated -> {
                        updateReservationWithAncillaries(amendUpsellsSelection)
                    }
                    else -> {
                        _state.update { it.copy(isLoading = false) }
                    }
                }
            }
        }
    }

    private fun updateReservationWithAncillaries(amendUpsellsSelection: List<RoomSelectedUpsells>) {
        lateinit var requestBody: UpdateReservationWithAncillariesRequestBody
        val dateFormatterForArrAndDepDate = DateTimeFormatter.ofPattern(DateFormat.DASHED_YEAR_MONTH_DAY)
        val listOfCurrentRoomSelections = mutableListOf<AmendRoomsSelections>()
        val arrivalDate = input.arrivalDeparturePair.first.format(dateFormatterForArrAndDepDate)
        val departureDate = input.arrivalDeparturePair.second.format(dateFormatterForArrAndDepDate)

        amendUpsellsSelection.forEach { amendedUpsell ->
            val listOfPackageSelections = mutableListOf<AmendSelectedPackages>()
            val roomId = amendedUpsell.roomId
            val roomCriteria = _state.value.reservation?.roomsCriteria?.find { it.roomId == roomId }
            val originalRoomSelection = storage.getOriginalRoomSelection().toListOfAmendRoomsSelections()

            if (amendedUpsell.roomId == originalRoomSelection[0].reservationId) {
                originalRoomSelection
                    .flatMap { it.packagesSelection }
                    .find { it.id in DONATION_LIST }?.let { donationPackage ->
                        listOfPackageSelections.add(
                            AmendSelectedPackages(
                                id = donationPackage.id,
                                noOfSelections = donationPackage.noOfSelections
                            )
                        )
                    }
            }

            roomCriteria?.numberOfChildren?.takeIf { it > 0 }?.let { childCount ->
                amendedUpsell.selectedUpsells
                    .filterIsInstance<MealDomain>()
                    .firstOrNull { it.freeBreakfastOption == true }
                    ?.let { freeBreakfastUpsell ->
                        listOfPackageSelections.add(
                            AmendSelectedPackages(
                                id = freeBreakfastUpsell.freeBreakfastCode ?: OPERA_FREE_CHILD_BREAKFAST,
                                noOfSelections = childCount
                            )
                        )
                    }
            }

            amendedUpsell.selectedUpsells.forEach { upsell ->
                listOfPackageSelections.add(
                    AmendSelectedPackages(
                        id = upsell.getId() ?: EMPTY_STRING_DOMAIN,
                        noOfSelections = upsell.getNumberOfSelections()
                    )
                )
            }
            listOfCurrentRoomSelections.add(AmendRoomsSelections(reservationId = amendedUpsell.roomId, packagesSelection = listOfPackageSelections))
        }

        val prevRoomSelectionFromSP = storage.getOriginalRoomSelection().toListOfAmendRoomsSelections()

        if (listOfCurrentRoomSelections.size != prevRoomSelectionFromSP.size) {
            val sizeOfCurrentRoom = listOfCurrentRoomSelections.size
            val sizeOfPrevRoom = storage.getOriginalRoomSelection().toListOfAmendRoomsSelections().size
            if (sizeOfPrevRoom > sizeOfCurrentRoom) {
                // remove room scenario
                val mutableListOfPrevRoomSP = prevRoomSelectionFromSP.toMutableList()
                val filterRemovedRoom = mutableListOfPrevRoomSP.filter { it.reservationId !in  listOfCurrentRoomSelections.map { item -> item.reservationId }}
                filterRemovedRoom.forEach { roomToRemove ->
                    val index = mutableListOfPrevRoomSP.indexOf(roomToRemove)
                    mutableListOfPrevRoomSP.removeAt(index)
                }
                requestBody = UpdateReservationWithAncillariesRequestBody(
                    input.temporaryBasketReference,
                    input.manageBookingInput.hotelCode(),
                    arrivalDate,
                    departureDate,
                    roomsSelections = listOfCurrentRoomSelections.sortedBy { it.reservationId },
                    previousRoomsSelections = mutableListOfPrevRoomSP.sortedBy { it.reservationId }
                )
            } else {
                requestBody = UpdateReservationWithAncillariesRequestBody(
                    input.temporaryBasketReference,
                    input.manageBookingInput.hotelCode(),
                    arrivalDate,
                    departureDate,
                    roomsSelections = listOfCurrentRoomSelections.sortedBy { it.reservationId },
                    previousRoomsSelections = storage.getOriginalRoomSelection().toListOfAmendRoomsSelections().sortedBy { it.reservationId }
                )
            }
        } else {
            requestBody = UpdateReservationWithAncillariesRequestBody(
                input.temporaryBasketReference,
                input.manageBookingInput.hotelCode(),
                arrivalDate,
                departureDate,
                roomsSelections = listOfCurrentRoomSelections.sortedBy { it.reservationId },
                previousRoomsSelections = storage.getOriginalRoomSelection().toListOfAmendRoomsSelections().sortedBy { it.reservationId }
            )
        }

        if (requestBody.roomsSelections != requestBody.previousRoomsSelections) {
            viewModelScope.launch {
                val result = runCatching {
                    withContext(dispatchers.io) {
                        graphQLAmendUseCase.updateReservationWithAncillaries(requestBody).await()

                    }
                }

                result.onSuccess { response ->
                   if (response.updateReservationPackagesByReservation.isNotEmpty()) {
                       sendEvent(AmendUpsellsEvent.Continue)
                   } else {
                       crashlyticsLogger.logException(Throwable(), "updateReservationWithAncillaries() Error: updateReservationPackagesByReservation is empty")
                       sendEvent(AmendUpsellsEvent.GeneralError)
                   }
                    _state.update { it.copy(isLoading = false) }
                }.onFailure { throwable ->
                    crashlyticsLogger.logException(throwable, "updateReservationWithAncillaries() Error")
                    _state.update { it.copy(isLoading = false) }
                    sendEvent(AmendUpsellsEvent.GeneralError)
                }
            }
        } else {
            _state.update { it.copy(isLoading = false) }
            sendEvent(AmendUpsellsEvent.Continue)
        }
    }

   private fun mapRoomListToSelectedUpsells(): List<RoomSelectedUpsells> {
       return _state.value.roomList.map { room ->
           val selectedMealUpsells = room.meals
               .filter { it.counter > 0 }
               .flatMap { meal ->
                   _state.value.availableUpsells
                       .filterIsInstance<MealDomain>()
                       .filter { it.getId() == meal.id }
                       .map { mealDomain ->
                           mealDomain.copy(numberOfSelections = meal.counter)
                       }
               }

           val selectedExtraUpsells = room.extras
               .filter { it.selected }
               .mapNotNull { extra ->
                   _state.value.availableUpsells
                       .filterIsInstance<ExtrasItemDomain>()
                       .find { it.getId() == extra.id }
                       ?.copy(numberOfSelections = 1)
               }

           RoomSelectedUpsells(
               roomId = room.roomId,
               selectedUpsells = selectedMealUpsells + selectedExtraUpsells
           )
       }
   }

    private fun mealIncrementCounter(roomIndex: Int, mealIndex: Int) {
        val currentRoomList = _state.value.roomList.toMutableList()
        val currentRoom = currentRoomList[roomIndex]
        currentRoomList[roomIndex] = summaryMealsHelper.incrementMealItem(currentRoom, mealIndex)

        _state.update {
            it.copy(roomList = currentRoomList)
        }
    }

    private fun mealDecrementCounter(roomIndex: Int, mealIndex: Int) {
        val currentRoomList = _state.value.roomList.toMutableList()
        val currentRoom = currentRoomList[roomIndex]
        currentRoomList[roomIndex] = summaryMealsHelper.decrementMealItem(currentRoom, mealIndex)

        _state.update {
            it.copy(roomList = currentRoomList)
        }
    }

    private fun updateExtraToggled(roomIndex: Int, extraIndex: Int, enabled: Boolean) {
        val currentRoomList = _state.value.roomList.toMutableList()
        val currentRoom = currentRoomList[roomIndex]
        currentRoomList[roomIndex] = summaryExtrasHelper.updateExtraSelectedState(currentRoom, extraIndex, enabled)

        _state.update {
            it.copy(roomList = currentRoomList)
        }
    }

    private fun getGuestCount(reservation: Reservation): Pair<Int, Int> {
        val numberOfChildren: Int = reservation.roomsCriteria.sumOf { it.numberOfChildren }
        val numberAdults: Int = reservation.roomsCriteria.sumOf { it.numberOfAdults }
        return Pair(numberOfChildren, numberAdults)
    }

    private fun setRoomList(availableUpsells: MutableList<UpsellDomainItem>): List<SummaryRoomItem> {
        val roomList = mutableListOf<SummaryRoomItem>()
        val (meals, extras) = getMealsAndExtras(availableUpsells)
        _state.value.reservation?.let { reservation ->
            reservation.roomsCriteria.forEachIndexed { index, room ->
                roomList.add(
                    SummaryRoomItem(
                        roomNumber = (index + 1).toString(),
                        roomType = room.roomType.name,
                        adults = room.numberOfAdults,
                        children = room.numberOfChildren,
                        meals = meals[room.roomId] ?: emptyList(),
                        extras = extras[room.roomId] ?: emptyList(),
                        roomId = room.roomId
                    )
                )
            }
        }
        return roomList
    }

    private fun List<MealDomain>.toSummaryMealItems(): List<SummaryMealItem> {
        return filterNot { it.id?.isKidsMeal() == true }.map { meal ->
            SummaryMealItem(
                id = meal.getId() ?: EMPTY_STRING_DOMAIN,
                name = meal.getName() ?: EMPTY_STRING_DOMAIN,
                counter = 0,
                price = SummaryPrice(
                    amount = meal.price?.toFloat() ?: 0F,
                    formattedPrice = PriceFormat.format(
                        meal.price?.toFloat() ?: 0F,
                        meal.currency,
                        deviceLocaleProvider
                    )
                ),
                description = meal.getShortDescription() ?: EMPTY_STRING_DOMAIN,
                offerTag = EMPTY_STRING_DOMAIN,
                kidsEatFree = meal.isFreeForChildren()
            )
        }
    }

    private fun List<ExtrasItemDomain>.toSummaryExtraItems(): List<SummaryExtrasItem> {
        return map { extra ->
            SummaryExtrasItem(
                id = extra.getId() ?: EMPTY_STRING_DOMAIN,
                name = extra.getName() ?: EMPTY_STRING_DOMAIN,
                price = SummaryPrice(
                    amount = extra.price?.toFloat() ?: 0F,
                    formattedPrice = PriceFormat.format(
                        extra.price?.toFloat() ?: 0F,
                        extra.currency,
                        deviceLocaleProvider
                    )
                ),
                description = extra.getDescription() ?: EMPTY_STRING_DOMAIN,
                selected = false
            )
        }
    }

    private fun getMealsAndExtras(availableUpsells: MutableList<UpsellDomainItem>): Pair<Map<String, List<SummaryMealItem>>, Map<String, List<SummaryExtrasItem>>> {
        val reservationUpsells = _state.value.reservation?.upsells ?: emptyList()
        val roomUpsells = reservationUpsells.groupBy { it.roomId }

        val availableMeals = availableUpsells.filterIsInstance<MealDomain>().toSummaryMealItems()
        val availableExtras = availableUpsells.filterIsInstance<ExtrasItemDomain>().toSummaryExtraItems()

        val reservationMeals = mutableMapOf<String, List<SummaryMealItem>>()
        val reservationExtras = mutableMapOf<String, List<SummaryExtrasItem>>()

        _state.value.reservation?.roomsCriteria?.forEach { room ->
            val roomId = room.roomId
            val mealsForRoom = availableMeals.map { it.copy() }.toMutableList()
            val extrasForRoom = availableExtras.map { it.copy() }.toMutableList()

            roomUpsells[roomId]?.forEach { upsell ->
                if (upsell.category == Upsell.Category.BREAKFAST) {
                    val mealIndex = mealsForRoom.indexOfFirst { it.id == upsell.code }
                    if (mealIndex != -1) {
                        mealsForRoom[mealIndex] = mealsForRoom[mealIndex].copy(counter = upsell.quantity)
                    }
                } else {
                    val extraIndex = extrasForRoom.indexOfFirst { it.id == upsell.code }
                    if (extraIndex != -1) {
                        extrasForRoom[extraIndex] = extrasForRoom[extraIndex].copy(selected = true)
                    }
                }
            }
            reservationMeals[roomId] = mealsForRoom
            reservationExtras[roomId] = extrasForRoom
        }
        return Pair(reservationMeals, reservationExtras)
    }

    fun getMenuAndAllergyInfoList(isAllergyInfo: Boolean): List<ParcelableMenuAndAllergyInfo> {
        val availableUpsells = _state.value.availableUpsells
        return if (isAllergyInfo) {
            getAllergyInfo(availableUpsells)
        } else {
            getMenu(availableUpsells)
        }
    }

    private fun getAllergyInfo(availableUpsells: MutableList<UpsellDomainItem>): List<ParcelableMenuAndAllergyInfo> {
        val meals = availableUpsells.filterIsInstance<MealDomain>()
        return meals.map {
            ParcelableMenuAndAllergyInfo(
                name = it.name,
                menuOrAllergyInfoSrc = it.allergyInfoSrc ?: EMPTY_STRING_DOMAIN
            )
        }
    }

    private fun getMenu(availableUpsells: MutableList<UpsellDomainItem>): List<ParcelableMenuAndAllergyInfo> {
        val meals = availableUpsells.filterIsInstance<MealDomain>()
        return meals.map {
            ParcelableMenuAndAllergyInfo(
                name = it.name,
                menuOrAllergyInfoSrc = it.menu?.menuSrc ?: EMPTY_STRING_DOMAIN
            )
        }
    }

    private fun setGalleryImages(listOfUpsellDomainItems : List<UpsellDomainItem>): MutableList<String> {
        val listOfGalleryUpsellImages = mutableListOf<String>()
        listOfUpsellDomainItems.forEach { upsell ->
            if (upsell is MealDomain && !upsell.imageSrc.isNullOrEmpty()) {
                listOfGalleryUpsellImages.add(Urls.CONTENT_BASE_URL + upsell.imageSrc!!)
            }
        }
        return listOfGalleryUpsellImages
    }

    private fun trackAvailableUpsells(availableUpsells: MutableList<UpsellDomainItem>) {
        val description = availableUpsells.joinToString { it.getShortDescription() ?: EMPTY_STRING_DOMAIN }
        val codes = availableUpsells.joinToString { it.getId() ?: EMPTY_STRING_DOMAIN }
        trackingAnalytics.track(
            AMEND_MEALS, AmendMealsData(
                input.manageBookingInput.bookingReference(),
                description,
                codes
            )
        )
    }

    private fun filterAvailableUpsellsForBusinessBooking(
        availableUpsells: MutableList<UpsellDomainItem>
    ): MutableList<UpsellDomainItem> {

        val allowedUpsellItems = businessStorage.getUpsellItemsAllowed() ?: return availableUpsells

        return availableUpsells.filterTo(mutableListOf()) { upsell ->
            when (upsell) {
                is MealDomain -> upsell.bartId != null && allowedUpsellItems.contains(upsell.bartId)
                else -> true
            }
        }
    }

    private fun manageLoading() {
        if (!shouldShowPackagesLoading && !shouldShowHotelInfoLoading){
            _state.update { it.copy(isLoading = false) }
            shouldShowPackagesLoading = true
            shouldShowHotelInfoLoading = true
        }
    }

    private fun sendEvent(event: AmendUpsellsEvent) {
        _state.update {
            it.copy(event = event)
        }
    }

    fun onEventConsumed() {
        _state.update {
            it.copy(event = null)
        }
    }

    data class AmendUpsellsState(
        val dateAndNights: String = EMPTY_STRING_DOMAIN,
        val guestsAndRooms: String = EMPTY_STRING_DOMAIN,
        val isRestaurantClosed: Boolean = false,
        val showApprovedMeals: Boolean = false,
        val areAllMealsRestricted: Boolean = false,
        val isDinnerAllowance: Boolean = false,
        var isLoading: Boolean = true,
        var listOfGalleryUpsellImages: MutableList<String> = mutableListOf(),
        val roomList: List<SummaryRoomItem> = emptyList(),
        val availableUpsells: MutableList<UpsellDomainItem> = mutableListOf(),
        val listOfAncillaryCloseoutItem: List<AncillaryCloseOutItem> = mutableListOf(),
        val reservation: Reservation? = null,
        val hotelName:  String? = null,
        val galleryImageSrc: List<String>? = null,
        val numberOfGuests: Int = 0,
        val numberOfRooms: Int = 0,
        val event: AmendUpsellsEvent? = null
    )
}
