package com.whitbread.premierinn.summary

import androidx.annotation.VisibleForTesting
import androidx.core.util.Pair
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whitbread.premierinn.api.request.booking.BookingAddress
import com.whitbread.premierinn.api.request.booking.Breakfast
import com.whitbread.premierinn.api.response.availability.UpsellItem
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.common.BookingFlowInput
import com.whitbread.premierinn.common.PaymentProvider
import com.whitbread.premierinn.common.StringResourceProvider
import com.whitbread.premierinn.common.ULTIMATE_WIFI_24_HRS
import com.whitbread.premierinn.common.ULTIMATE_WIFI_7_DAYS
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Value.EMPLOYEE_RATE_CODE
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.format.PriceFormat
import com.whitbread.premierinn.common.mapper.toParcelablePrice
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.common.summary.SummaryExtrasToggleHelper
import com.whitbread.premierinn.common.summary.SummaryMealsIncrementDecrementHelper
import com.whitbread.premierinn.common.summary.model.SummaryExtrasItem
import com.whitbread.premierinn.common.summary.model.SummaryMealItem
import com.whitbread.premierinn.common.summary.model.SummaryPrice
import com.whitbread.premierinn.common.summary.model.SummaryRoomItem
import com.whitbread.premierinn.common.utils.getAdditionalInfoQuestions
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.domain.common.EXTRAS_LIST
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.STANDARD_ROOM_CLASS_OPERA
import com.whitbread.premierinn.domain.customer.entity.BookingPreferences
import com.whitbread.premierinn.domain.graphql.requestBodyModels.SaveReservationWithAncillariesRequestBody
import com.whitbread.premierinn.domain.graphql.summary.usecase.GraphQLSummaryUseCase
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.FREE_BREAKFAST_PROMO_TAG
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import com.whitbread.premierinn.domain.result.Result
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataInput
import com.whitbread.premierinn.paymentdetails.PaymentDetailsInput
import com.whitbread.premierinn.reviewbooking.ReviewBookingInput
import com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData
import com.whitbread.premierinn.summary.models.ParcelableExtrasItem
import com.whitbread.premierinn.summary.models.ParcelableMenuAndAllergyInfo
import com.whitbread.premierinn.summarybreakdown.SummaryBreakdownInput
import com.whitbread.premierinn.summarybreakdown.SummaryBreakdownRoom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val deviceLocaleProvider: DeviceLocaleProvider,
    private val graphQLSummaryUseCase: GraphQLSummaryUseCase,
    private val simplePersistenceManager: SimplePersistenceManager,
    private val businessPersistenceManager: BusinessPersistenceManager,
    private val stringProvider: StringResourceProvider,
    private val summaryMealsHelper: SummaryMealsIncrementDecrementHelper,
    private val summaryExtrasHelper: SummaryExtrasToggleHelper,
    private val trackingAnalytics: TrackingAnalytics,
    private val logService: LogService,
    private val getStringResource: GetStringResource
): ViewModel(){


    private val _state = MutableStateFlow(savedStateHandle.get<SummaryState>("uiState") ?: SummaryState())
    val state: StateFlow<SummaryState> = _state

    private val _navigationEvent = MutableSharedFlow<SummaryNavigation>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    var isLoggedIn = false
        private set

    var isInnBusinessUser = false
        private set

    var isWifiAvailable = false
        private set

    private val bookingPreference: BookingPreferences by lazy {
        simplePersistenceManager.getCustomerBookingPreferences()
    }

    private val preferenceUpsellCode: String by lazy {
        bookingPreference.mealPreference?.let {
            getOperaBreakFastCode(it.toString())
        } ?: EMPTY_STRING
    }

    private val input: SummaryInput by lazy {
        requireNotNull(savedStateHandle.get<SummaryInput>(SUMMARY_INPUT_V2))
    }

    fun init() {
        isLoggedIn = input.isLoggedIn
        isInnBusinessUser = input.isBusinessUser()
        initialiseState()
        logAnalytics()

    }

    private fun initialiseState() = viewModelScope.launch {
        if (savedStateHandle.get<SummaryState>("uiState") == null) {
            _state.update {
                it.copy(
                    roomList = roomList(),
                    basketReference = input.basketReference()!!,
                    totalStayPrice = totalStayPrice(roomList()))
            }
        }
    }

    fun saveReservationWithAncillaries() = viewModelScope.launch {
        val saveResWithAncillariesRequest: SaveReservationWithAncillariesRequestBody =
            savedStateHandle.get<SummaryState>("uiState")?.let { prevState ->
                input.buildSaveReservationWithAncillariesRequestBody(
                    _state.value.basketReference, state.value.roomList, prevState.roomList
                )
            } ?: input.buildSaveReservationWithAncillariesRequestBody(
                _state.value.basketReference, state.value.roomList, null
            )

        _state.update { it.copy(isLoading = true) }

        graphQLSummaryUseCase.saveResWithAncillaries(saveResWithAncillariesRequest).collect { result ->
            when (result) {
                is Result.Success -> {
                    if (isInnBusinessUser) {
                        goToNextScreen(isLoggedIn)
                    } else {
                        savedStateHandle["uiState"] = _state.value
                        goToNextScreen(isLoggedIn)
                    }
                }

                is Result.Error -> {
                    val currentMealSelection = saveResWithAncillariesRequest.roomsSelections.all { it.packagesSelection.isEmpty() }
                    val previousMealSelection = saveResWithAncillariesRequest.previousRoomsSelections.all { it.packagesSelection.isEmpty() }
                    val mealError = when {
                        currentMealSelection && previousMealSelection -> {
                            goToNextScreen(isLoggedIn)
                            MealSelectionStatus.NO_SELECTION
                        }
                        !currentMealSelection && previousMealSelection -> MealSelectionStatus.NEW_MEAL_ERROR
                        else -> MealSelectionStatus.PREVIOUS_MEAL_ERROR
                    }

                    logService.logException(
                        SummaryError.SummaryErrorException(
                            SummaryError.SaveReservationWithAncillariesError(mealError, isLoggedIn)
                        ),
                        "saveWithAncillaries failed"
                    )

                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = SummaryError.SaveReservationWithAncillariesError(mealError, isLoggedIn)
                        )
                    }
                }
            }
        }
    }

    @VisibleForTesting
    internal fun mealsList(adults: Int): List<SummaryMealItem> {
        return input.upsellItems().map { upsellItem ->
            val isPromoMeal = upsellItem.price().amount < 0
                    && input.promotionCode() == simplePersistenceManager.getFreeBreakfastPromotionCode()
            val priceAmount = if (isPromoMeal) 0f else abs(upsellItem.price().amount)

            SummaryMealItem(
                id = upsellItem.code() ?: EMPTY_STRING,
                name = upsellItem.legend() ?: EMPTY_STRING,
                price = SummaryPrice(
                    amount = priceAmount,
                    formattedPrice = PriceFormat.format(
                        priceAmount,
                        upsellItem.price().currency,
                        deviceLocaleProvider
                    )
                ),
                originalPrice = if (isPromoMeal) SummaryPrice(
                    amount = abs(upsellItem.price().amount),
                    formattedPrice = PriceFormat.format(
                        abs(upsellItem.price().amount),
                        upsellItem.price().currency,
                        deviceLocaleProvider
                    )
                ) else null,
                offerTag = if (isPromoMeal) getStringResource.invoke(FREE_BREAKFAST_PROMO_TAG) else EMPTY_STRING,
                description = upsellItem.shortDescription().orEmpty(),
                kidsEatFree = upsellItem.isFreeForChildren(),
                counter = if (upsellItem.code() == preferenceUpsellCode || isPromoMeal) adults else 0
            )
        }
    }

    private fun extrasList(): List<SummaryExtrasItem> {
        return input.extrasItems()
            ?.filter { it.id in EXTRAS_LIST && it.price != null && it.available!! >= input.totalRooms() }
            ?.filter { filterUltimateWifi(it) }
            ?.map { extrasItem ->
                SummaryExtrasItem(
                    id = extrasItem.id,
                    name = extrasItem.name,
                    price = SummaryPrice(
                        amount = extrasItem.price!!.toFloat(),
                        formattedPrice = PriceFormat.format(
                            extrasItem.price.toFloat() ?: 0f,
                            extrasItem.currency,
                            deviceLocaleProvider
                        )
                    ),
                    description = extrasItem.description,
                    selected = false
                )
            }?.sortedBy { EXTRAS_LIST.indexOf(it.id) } ?: emptyList()
    }

    private fun roomList(): List<SummaryRoomItem> {
        return SummaryBreakdownRoom.createSummaryBreakdownRooms(
            input.roomBookings(), input.accessibleRoomBookings(),
            input.twinRoomBookings(), input.arrivalDate(),
            false, deviceLocaleProvider
        ).mapIndexed { index, summaryBreakdownRoom ->
            SummaryRoomItem(
                roomNumber = (index + 1).toString(),
                roomType = stringProvider.getRoomDescription(summaryBreakdownRoom.roomType()),
                adults = summaryBreakdownRoom.adults(),
                children = summaryBreakdownRoom.children(),
                meals = mealsList(summaryBreakdownRoom.adults()),
                extras = extrasList()
            )
        }
    }

    private fun totalStayPrice(roomList: List<SummaryRoomItem>): String {
        return PriceFormat.format(
            listOf(
                input.totalStayPrice().amount,
                roomList.totalSelectedMealsPrice(input.totalNights()),
                roomList.totalSelectedExtrasPrice()
            ).sum(),
            input.totalStayPrice().currency,
            deviceLocaleProvider
        )
    }

    private fun logAnalytics() {
        trackingAnalytics.track(ScreenState.SUMMARY, createAncillariesAnalyticsBody())
    }

    private fun createAncillariesAnalyticsBody(): SummaryAnalyticsData {
        val chosenRate = input.rate()
        val pushToken = simplePersistenceManager.getFirebaseToken()
        val rateCode = if (input.isEmployeeRateSelected()) EMPLOYEE_RATE_CODE else chosenRate!!.code()
        return SummaryAnalyticsData.builder()
            .hotelCode(input.hotel().code())
            .prepaid(false)
            .rateCode(rateCode)
            .rateDescription(chosenRate.description())
            .rateName(chosenRate.rateName())
            .extrasShown(
                Pair<List<UpsellItem>, List<ParcelableExtrasItem>>(
                    input.upsellItems(),
                    input.extrasItems()
                        ?.filter { it.id in EXTRAS_LIST }
                        ?.filter { filterUltimateWifi(it) }
                )
            )
            .checkInDate(input.arrivalDate())
            .nights(input.totalNights())
            .rooms(input.totalRooms())
            .adults(input.totalAdults())
            .children(input.totalGuests() - input.totalAdults())
            .roomPrice(input.totalStayPrice())
            .selectedRooms(
                listOfNotNull(
                    input.roomBookings(),
                    input.accessibleRoomBookings(),
                    input.twinRoomBookings()
                ).flatten())
            .pushToken(pushToken)
            .lettingTypes(null)
            .lettingType(null)
            .promoCode(input.promotionCode())
            .promoName(input.promotionTag())
            .rateTag(input.rateTag())
            .build()
    }

    private fun constructReviewBookingInput(roomList: List<SummaryRoomItem>): ReviewBookingInput {

        val address = simplePersistenceManager.getCustomer().address
        val customer = simplePersistenceManager.getCustomer()
        val bookerDetails = GuestDetailsFormDataInput.create(
            customer.fullName.title, customer.fullName.firstName, customer.fullName.lastName,
            customer.contact.email, customer.contact.mobile, deviceLocaleProvider.getDeviceLanguage()
        )
        val guestDetailsList = mutableListOf<GuestDetailsFormDataInput>()
        guestDetailsList.add(bookerDetails)

        val paymentDetailsInput = PaymentDetailsInput.builder()
            .bookingFlowInput(constructBookingFlowInput(roomList, true))
            .paymentMethodsDetailInput(_state.value.paymentMethods)
            .address(
                BookingAddress.create(
                    address.countryCode.takeUnless {address.countryCode.isNullOrEmpty()} ?: EMPTY_STRING,
                    EMPTY_STRING, address.line1, address.line2,
                    address.line3.takeUnless {address.line3.isNullOrEmpty()} ?: EMPTY_STRING,
                    address.postCode.takeUnless {address.postCode.isNullOrEmpty()} ?: EMPTY_STRING
                )
            )
            .bookerDetails(bookerDetails)
            .guestDetailsList(guestDetailsList) // Not sure what needs to be passed for BB
            .isBookerStaying(true) // Not sure what needs to be passed for BB
            .isTaxExempt(false) // Not sure what needs to be passed for BB
            .marketingOptIn(false) // Not sure what needs to be passed for BB
            .isBusinessTrip(true)
            .accountPassword(null)
            .shouldCreateAccount(false)
            .paymentProvider(PaymentProvider.from(simplePersistenceManager.getPaymentProvider()))
            .build()

        val acceptedCreditCard = _state.value.paymentMethods?.toAcceptedCreditCard()
        val bookingReference = _state.value.paymentMethods?.parcelableBookingConfirmation?.bookingReference
        val paypalClientToken = _state.value.paymentMethods?.parcelablePaymentMethods?.find { it.clientToken !=null }?.clientToken
        input.extrasItems()?.let {
            isWifiAvailable = it.any { extra ->
                extra.id == ULTIMATE_WIFI_24_HRS || extra.id == ULTIMATE_WIFI_7_DAYS }
        }

        val reviewBookingInput = ReviewBookingInput.builder()
            .paymentDetailsInput(paymentDetailsInput)
            .cardHolderAddress(
                BookingAddress.create(
                    address.countryCode.takeUnless {address.countryCode.isNullOrEmpty()} ?: EMPTY_STRING,
                    EMPTY_STRING, address.line1, address.line2,
                    address.line3.takeUnless {address.line3.isNullOrEmpty()} ?: EMPTY_STRING,
                    address.postCode.takeUnless {address.postCode.isNullOrEmpty()} ?: EMPTY_STRING
                )
            )
            .marketingOptIn(false)
            .guestHistoryNumber(customer.guestHistoryNumber)
            .acceptedCreditCards(acceptedCreditCard)
            .bookingReference(bookingReference)
            .uuidBasketReference(_state.value.basketReference)
            .paypalClientToken(paypalClientToken)
            .isBusinessUser(isInnBusinessUser)
            .isWifiAvailable(isWifiAvailable)
            .build()

        return reviewBookingInput
    }

    fun constructBookingFlowInput(roomList: List<SummaryRoomItem>, isLoggedIn: Boolean): BookingFlowInput {
        this.isLoggedIn = isLoggedIn
        val selectedUpsellItems : List<UpsellItem> = roomList.getSelectedUpsellItems(input.upsellItems())
        val selectedExtrasItems : List<ParcelableExtrasItem> = roomList.getSelectedExtrasItems(input.extrasItems())
        val breakfasts : List<Breakfast> = Breakfast.createBreakfasts(roomList)

        val bookingFlowInput = BookingFlowInput.create(input,
            breakfasts, null, selectedUpsellItems, roomList.totalSelectedMealsPrice(input.totalNights()),
            selectedExtrasItems)

        return bookingFlowInput
    }

    fun constructSummaryBreakDownInput(): SummaryBreakdownInput {
        val roomList = state.value.roomList
        val totalMealsPrice = roomList.totalSelectedMealsPrice(input.totalNights())
        val totalExtrasPrice = roomList.totalSelectedExtrasPrice()
        val totalPrice = input.totalStayPrice().amount + totalMealsPrice + totalExtrasPrice
        val totalChildrenBreakfast = Breakfast.createBreakfasts(roomList).sumOf { it.children() }

        return SummaryBreakdownInput.create(input, totalMealsPrice,
            roomList.getSelectedUpsellItems(input.upsellItems()),
            roomList.getSelectedExtrasItems(input.extrasItems()),
            PriceDomain(totalPrice, input.totalStayPrice().currency).toParcelablePrice(), false,
            totalChildrenBreakfast)
    }

    fun goToNextScreen(isLoggedIn: Boolean) {
        when {
            isInnBusinessUser -> {
                viewModelScope.launch {
                    navigateToReviewOrAdditionalInfo()
                }
            }
            isLoggedIn -> navigateToGuestDetails()
            else -> navigateToLogin()
        }
    }

    private fun navigateToGuestDetails() = viewModelScope.launch {
        val input = constructBookingFlowInput(state.value.roomList, true)
        _navigationEvent.emit(SummaryNavigation.OpenGuestDetailsActivity(input))
        _state.update { it.copy(isLoading = false) }
    }


    private fun navigateToLogin() = viewModelScope.launch {
        _navigationEvent.emit(SummaryNavigation.OpenLoginActivity)
        _state.update { it.copy(isLoading = false) }
    }

    private suspend fun navigateToReviewOrAdditionalInfo() {
        val reviewInput = constructReviewBookingInput(state.value.roomList)
        val listOfQuestions = businessPersistenceManager.getAdditionalInfoQuestions()
        val updatedInput = listOfQuestions?.let {
            reviewInput.toBuilder().listOfEmployeeQuestionsModel(it).build()
        } ?: reviewInput

        _state.update {
            it.copy(isLoading = false)
        }
        _navigationEvent.emit(
            if (listOfQuestions != null && listOfQuestions.isNotEmpty()) {
                SummaryNavigation.OpenAdditionalInfoActivity(updatedInput)
            } else {
                SummaryNavigation.OpenReviewBookActivity(updatedInput)
            }
        )
    }

    fun getMenuAndAllergyInfoList(isAllergyInfo: Boolean): List<ParcelableMenuAndAllergyInfo> {
        return if (isAllergyInfo) input.allergyInfo() ?: emptyList() else input.menus()
            ?: emptyList()
    }

    fun mealIncrementCounter(roomIndex: Int, mealIndex: Int) {
        val mutableRoomList = _state.value.roomList.toMutableList()
        val roomItem = mutableRoomList[roomIndex]
        mutableRoomList[roomIndex] = summaryMealsHelper.incrementMealItem(roomItem, mealIndex)

        _state.update { it.copy(roomList = mutableRoomList, totalStayPrice = totalStayPrice(mutableRoomList)) }
    }

    fun mealDecrementCounter(roomIndex: Int, mealIndex: Int) {
        val mutableRoomList = _state.value.roomList.toMutableList()
        val roomItem = mutableRoomList[roomIndex]
        mutableRoomList[roomIndex] = summaryMealsHelper.decrementMealItem(roomItem, mealIndex)

        _state.update { it.copy(roomList = mutableRoomList, totalStayPrice = totalStayPrice(mutableRoomList)) }
    }

    fun updateExtrasToggleState(roomIndex: Int, extraIndex: Int, isSelected: Boolean) {
        val mutableRoomList = _state.value.roomList.toMutableList()
        val roomItem = mutableRoomList[roomIndex]
        mutableRoomList[roomIndex] =
            summaryExtrasHelper.updateExtraSelectedState(roomItem, extraIndex, isSelected)

        _state.update { it.copy(roomList = mutableRoomList, totalStayPrice = totalStayPrice(mutableRoomList)) }
    }

    fun onErrorHandled() {
        _state.update { it.copy(error = null)}
    }

    private fun filterUltimateWifi(item: ParcelableExtrasItem): Boolean = if (input.selectedRoomClass() != STANDARD_ROOM_CLASS_OPERA) {
        item.id != ULTIMATE_WIFI_24_HRS
    } else {
        true
    }
}

