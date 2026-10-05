package com.whitbread.premierinn.ciol.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsData.Companion.PRICE_BREAKDOWN_ACTION
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsModel
import com.whitbread.premierinn.ciol.analytics.CiolCompletionAnalyticsData
import com.whitbread.premierinn.ciol.analytics.CiolCompletionAnalyticsModel
import com.whitbread.premierinn.ciol.analytics.logCiolAnalytics
import com.whitbread.premierinn.ciol.analytics.setCompletionData
import com.whitbread.premierinn.ciol.entity.PreStayUiModel
import com.whitbread.premierinn.ciol.entity.RegCardPdfModel
import com.whitbread.premierinn.ciol.entity.upsells.BreakfastUiModel
import com.whitbread.premierinn.ciol.entity.upsells.ExtrasItemUiModel
import com.whitbread.premierinn.ciol.entity.upsells.MealUiModel
import com.whitbread.premierinn.ciol.entity.upsells.UpdateReservationPackagesUiModel
import com.whitbread.premierinn.ciol.entity.upsells.UpsellEntry
import com.whitbread.premierinn.ciol.entity.upsells.UpsellItem
import com.whitbread.premierinn.ciol.entity.upsells.addItems
import com.whitbread.premierinn.ciol.entity.upsells.buildUpsellPairForUpsellDetails
import com.whitbread.premierinn.ciol.entity.upsells.convertToUpsellItemList
import com.whitbread.premierinn.ciol.entity.upsells.copy
import com.whitbread.premierinn.ciol.entity.upsells.details.BookingConfirmationUiModel
import com.whitbread.premierinn.ciol.entity.upsells.details.RoomSelection
import com.whitbread.premierinn.ciol.entity.upsells.details.UpsellDetailsModel
import com.whitbread.premierinn.ciol.entity.upsells.details.UpsellType
import com.whitbread.premierinn.ciol.entity.upsells.details.buildUpsellDetailsModel
import com.whitbread.premierinn.ciol.entity.upsells.details.convertToBookingConfirmationUiModel
import com.whitbread.premierinn.ciol.entity.upsells.details.convertToRoomSelectionList
import com.whitbread.premierinn.ciol.entity.upsells.details.deepCopy
import com.whitbread.premierinn.ciol.entity.upsells.details.getCurrentRoomSelections
import com.whitbread.premierinn.ciol.entity.upsells.organizeToCategories
import com.whitbread.premierinn.ciol.mapper.convertToUiModel
import com.whitbread.premierinn.ciol.uilogic.FormatUpsellsList
import com.whitbread.premierinn.ciol.uimodel.CheckInCompletionModel
import com.whitbread.premierinn.ciol.uimodel.PriceBreakdownModel
import com.whitbread.premierinn.ciol.uimodel.mapToDomainModel
import com.whitbread.premierinn.ciol.uimodel.mapToPriceBreakdownRoomSelection
import com.whitbread.premierinn.ciol.uimodel.mapToUiModel
import com.whitbread.premierinn.ciol.usecase.GenerateAndSaveRegCardPdfUseCase
import com.whitbread.premierinn.ciol.utils.addUpsellsSelectionsFromAllRooms
import com.whitbread.premierinn.ciol.utils.disableMultiMealSelection
import com.whitbread.premierinn.ciol.utils.getNumberOfSelections
import com.whitbread.premierinn.ciol.utils.getUpsellName
import com.whitbread.premierinn.ciol.utils.updateRoomSelections
import com.whitbread.premierinn.ciol.utils.updateUpsells
import com.whitbread.premierinn.ciol.viewmodel.UpsellsViewModel.UpsellAction.GoToSelectRoom
import com.whitbread.premierinn.ciol.viewmodel.UpsellsViewModel.UpsellAction.ShowUpsellDetailsBottomSheet
import com.whitbread.premierinn.common.AppConfiguration
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.CIOL_COMPLETION
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.UPSELLS
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.format.DateFormat
import com.whitbread.premierinn.common.format.format
import com.whitbread.premierinn.common.utils.StringUtils
import com.whitbread.premierinn.common.utils.isGerman
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.booking.entity.BookingConfirmation
import com.whitbread.premierinn.domain.booking.entity.PreStayModel
import com.whitbread.premierinn.domain.booking.entity.hasChildren
import com.whitbread.premierinn.domain.ciol.usecase.GetPriceBreakdownUseCase
import com.whitbread.premierinn.domain.ciol.usecase.UpdatePriceBreakdownUseCase
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.entity.isPibaCNPBooking
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.graphql.ciol.usecase.AuthorizeCardUseCase
import com.whitbread.premierinn.domain.graphql.ciol.usecase.ConfirmPreCheckInUseCase
import com.whitbread.premierinn.domain.graphql.ciol.usecase.GetBookingConfirmationUseCase
import com.whitbread.premierinn.domain.graphql.ciol.usecase.GetUpsellsUseCase
import com.whitbread.premierinn.domain.graphql.ciol.usecase.PreCheckInUseCase
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AuthorizeCardRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelPackagesRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PreCheckInRequestBody
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key
import com.whitbread.premierinn.domain.result.Result
import com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.formattedCheckInDay
import com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.formattedCheckOutDay
import com.whitbread.premierinn.threeCp.ThreeCpInputAuthorizeCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import kotlin.math.sign

@HiltViewModel
class UpsellsViewModel @Inject constructor(
    private val getUpsellsUseCase: GetUpsellsUseCase,
    private val getBookingConfirmationUseCase: GetBookingConfirmationUseCase,
    private val confirmPreCheckInUseCase: ConfirmPreCheckInUseCase,
    private val trackingAnalytics: TrackingAnalytics,
    private val formatUpsellsList: FormatUpsellsList,
    private val deviceLocaleProvider: DeviceLocaleProvider,
    private val configuration: AppConfiguration,
    private val getPriceBreakdownUseCase: GetPriceBreakdownUseCase,
    private val updatePriceBreakdownUseCase: UpdatePriceBreakdownUseCase,
    private val authorizeCardUseCase: AuthorizeCardUseCase,
    private val generateAndSaveRegCardPdfUseCase: GenerateAndSaveRegCardPdfUseCase,
    private val preCheckInInitiationUseCase: PreCheckInUseCase,
    private val isFeatureOn: IsFeatureOn
) : ViewModel() {

    private lateinit var packagesRequestBody: HotelPackagesRequestBody

    fun initParams(hotelPackagesRequestBody: HotelPackagesRequestBody){
        this.packagesRequestBody = hotelPackagesRequestBody
    }
    private val _state = MutableStateFlow(UpsellsState())
    val state: StateFlow<UpsellsState> = _state

    sealed class Error {
        data class GenericError(val isPibaCnp: Boolean = false) : Error()
    }

    data class UpsellsState(
        val upsellItems: List<UpsellItem> = emptyList(),
        val deviceLocale: Locale = Locale.getDefault(),
        val bookingConfirmation: BookingConfirmation? = null,
        val isMultiRoomBooking: Boolean = false,
        val totalRoomsAdults: Int = 0,
        val preselectedRoomSelections: List<RoomSelection> = emptyList(),
        val roomSelections: List<RoomSelection> = emptyList(),
        val navigation: NavigationDestination? = null,
        val isLoading: Boolean = false,
        val error: Error? = null,
        val action: UpsellAction? = null,
        val specialOccasion: String = EMPTY_STRING,
        val priceBreakdownModel: PriceBreakdownModel? = null,
        val regCardPdfModel: RegCardPdfModel? = null,
        val preStayModel: PreStayModel? = null,
        val isPibaCpEnabled: Boolean = false
    )

    sealed class UpsellAction {
        data class ShowUpsellDetailsBottomSheet(val upsellDetails: UpsellDetailsModel) : UpsellAction()
        data class GoToSelectRoom(
            val bookingConfirmation: BookingConfirmationUiModel,
            val preselectedRoomSelections: List<RoomSelection>,
            val roomSelections: List<RoomSelection>,
            val upsellItems: List<UpsellItem>,
            val upsellType: UpsellType
        ) : UpsellAction()
    }

    sealed class NavigationDestination {
        data class PayAndCheckInFragment(
            val preStayModel: PreStayModel?,
            val analyticsModel: CiolCompletionAnalyticsModel,
            val updateReservationPackagesUiModel: UpdateReservationPackagesUiModel,
            val regCardPdfModel: RegCardPdfModel?
        ) : NavigationDestination()
        data class CompletionScreen(
            val completionModel: CheckInCompletionModel
        ) : NavigationDestination()
        data class AuthorizeCardScreen(
            val data: ThreeCpInputAuthorizeCard
        ) : NavigationDestination()
    }

    fun onScreenOpened(preStayModel: PreStayModel, specialOccasion: String, regCardPdfModel: RegCardPdfModel?) {
        val preStayDetails = preStayModel.preStayDetails
        logCiolAnalytics(trackingAnalytics, preStayModel.convertToUiModel(),
            UPSELLS
        )
        _state.update { it.copy(isPibaCpEnabled = isFeatureOn.invoke(Key.FEATURE_PIBA_CP_ENABLED)) }
        _state.update {
            it.copy(
                preStayModel = preStayModel,
                specialOccasion = specialOccasion,
                regCardPdfModel = regCardPdfModel
            )
        }
        if (_state.value.bookingConfirmation == null) {
            getBookingConfirmation { _ -> updatePriceBreakdown() }
            viewModelScope.launch {
                _state.update {
                    it.copy(
                        isLoading = true,
                        isMultiRoomBooking = preStayDetails.rooms.size > 1,
                        totalRoomsAdults = preStayDetails.numberOfAdults
                    )
                }
                getUpsells(preStayDetails.numberOfAdults)
            }
        }

        getPriceBreakdown()
    }

    fun authorizeCardCompletedSuccessfully(transactionId: String, preStayModel: PreStayModel) {
        generateAndSaveRegCardPdf(transactionId, preStayModel.convertToUiModel())
    }

    private fun updatePriceBreakdown() = viewModelScope.launch {
        getPriceBreakdownUseCase().value.run {
            updatePriceBreakdownUseCase(
                this.copy(
                    roomSelections = _state.value.roomSelections.map { it.mapToPriceBreakdownRoomSelection() },
                )
            )
        }
    }

    private fun getPriceBreakdown() = viewModelScope.launch {
        getPriceBreakdownUseCase.invoke().collect { priceBreakdown ->
            _state.update { it.copy(priceBreakdownModel = priceBreakdown.mapToUiModel()) }
        }
    }

    fun refreshScreenComponents(numberOfAdults: Int) {
        getUpsells(numberOfAdults)
        getBookingConfirmation { bookingConfirmation ->
            viewModelScope.launch {
                getPriceBreakdownUseCase().value.run {
                    val outstandingBalance = PriceDomain(
                        bookingConfirmation.balanceOutstanding ?: this.outstandingBalance.amount,
                        bookingConfirmation.currencyCode
                    )

                    updatePriceBreakdownUseCase(
                        this.copy(
                            outstandingBalance = outstandingBalance,
                            roomSelections = emptyList(),
                        )
                    )
                }
            }
        }
    }

    private fun getUpsells(numberOfAdults: Int) = viewModelScope.launch {
        getUpsellsUseCase(packagesRequestBody).collect { result ->
            when (result) {
                is Result.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            upsellItems = formatUpsellsList(
                                result.data.availableUpsells.convertToUpsellItemList()
                                    .organizeToCategories()
                                    .disableMultiMealSelection(numberOfAdults)
                            ),
                            preselectedRoomSelections = result.data.preselectedRoomSelections
                                .convertToRoomSelectionList(result.data.availableUpsells),
                            deviceLocale = deviceLocaleProvider.getDeviceLocale()
                        )
                    }
                }

                is Result.Error -> {
                    _state.update { it.copy(isLoading = false, error = Error.GenericError()) }
                }
            }
        }
    }

    private fun getBookingConfirmation(onSuccess: ((bookingConfirmation: BookingConfirmation) -> Unit)? = null) = viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
        getBookingConfirmationUseCase(
            uuidBasketReference = packagesRequestBody.basketReferenceId,
            country = deviceLocaleProvider.getCountryIfRegion(deviceLocaleProvider.getDeviceLocale()).lowercase(),
            language = deviceLocaleProvider.getDeviceLanguage()
        ).collect { result ->
            when(result) {
                is Result.Success -> {
                    _state.update { it.copy(
                        isLoading = false,
                        preStayModel = state.value.preStayModel?.preStayDetails?.copy(
                            reservationGuests = result.data.reservationByIdList[0].reservationGuestList
                        )?.let { preStayDetails ->
                            state.value.preStayModel?.copy(
                                preStayDetails = preStayDetails
                            )
                        },
                        bookingConfirmation = result.data,
                        roomSelections = result.data.reservationByIdList.map { reservation ->
                            RoomSelection(reservation.reservationId, mutableListOf())
                        }
                    ) }
                    onSuccess?.invoke(result.data)
                }
                is Result.Error -> {
                    _state.update { it.copy(isLoading = false, error = Error.GenericError()) }
                }
            }
        }
    }

    fun onContinueButtonClicked(preStayModel: PreStayModel) = viewModelScope.launch {
        val selectedUpsells = _state.value.roomSelections.flatMap { it.selectedUpsells }
        val noNights = preStayModel.preStayDetails.numberOfNights
        val outstandingBalance = _state.value.priceBreakdownModel?.outstandingBalance?.amount?.toDouble() ?: 0.0

        logCiolAnalytics(
            analytics = trackingAnalytics,
            preStayModel= preStayModel.convertToUiModel(),
            screenName = UPSELLS,
            extrasDescription = getExtraDescription(selectedUpsells),
            isContinueButton = true
        )
        val bookingConfirmation = _state.value.bookingConfirmation
        if (bookingConfirmation == null) {
            _state.update { it.copy(error = Error.GenericError()) }
            return@launch
        }

        if (_state.value.roomSelections.flatMap { it.selectedUpsells }.sumOf { it.getNumberOfSelections() } == 0) {
            checkNavigation(bookingConfirmation, preStayModel)
            return@launch
        }

        _state.update {
            it.copy(
                isLoading = false,
                navigation = NavigationDestination.PayAndCheckInFragment(
                    preStayModel = state.value.preStayModel,
                    analyticsModel = setCompletionData(
                        selectedUpsells,
                        noNights,
                        outstandingBalance
                    ),
                    UpdateReservationPackagesUiModel(
                        basketReference = packagesRequestBody.basketReferenceId,
                        roomSelectionList = _state.value.roomSelections,
                        hotelId = bookingConfirmation.hotelId,
                        arrivalDate = LocalDate.parse(bookingConfirmation.reservationByIdList.first().roomStay.arrivalDate)
                            .toString(),
                        departureDate = LocalDate.parse(bookingConfirmation.reservationByIdList.first().roomStay.departureDate)
                            .toString()
                    ),
                    _state.value.regCardPdfModel
                )
            )
        }
    }

    private fun getExtraDescription(selectedUpsells: List<UpsellEntry>): String {
        return selectedUpsells.groupingBy { it.getUpsellName() }
            .fold(0) { acc, item -> acc + item.getNumberOfSelections() }.map {
                "${it.value} x ${it.key}"
            }.joinToString(", ")
    }

    fun onUserNavigated() {
        _state.update { it.copy(navigation = null) }
    }

    private fun checkNavigation(bookingConfirmation: BookingConfirmation, preStayModel: PreStayModel) {
        if(shouldCheckInForPibaBooking(preStayModel.convertToUiModel())) return

        if (_state.value.priceBreakdownModel == null || sign(_state.value.priceBreakdownModel!!.outstandingBalance.amount) == 0F) {
            val leadGuest = _state.value.preStayModel?.preStayDetails?.reservationGuests?.find { !it.isAccompanyingGuest }

            if (_state.value.regCardPdfModel == null) {
                confirmPreCheckIn(preStayModel.convertToUiModel())
            } else {
                if (leadGuest?.nationality?.isGerman() == true) {
                    performPreCheckInInitiationRequest(preStayModel.convertToUiModel())
                } else {
                    performAuthorizeCard()
                }
            }

        } else {
            _state.update { it.copy(
                navigation = NavigationDestination.PayAndCheckInFragment(
                    preStayModel = state.value.preStayModel,
                    analyticsModel = CiolCompletionAnalyticsModel(
                        revenue = _state.value.priceBreakdownModel?.outstandingBalance?.amount.toString()
                    ),
                    UpdateReservationPackagesUiModel(
                        basketReference = packagesRequestBody.basketReferenceId,
                        roomSelectionList = _state.value.roomSelections,
                        hotelId = bookingConfirmation.hotelId,
                        arrivalDate = LocalDate.parse(bookingConfirmation.reservationByIdList.first().roomStay.arrivalDate)
                            .toString(),
                        departureDate = LocalDate.parse(bookingConfirmation.reservationByIdList.first().roomStay.departureDate)
                            .toString()
                    ),
                    _state.value.regCardPdfModel
                )
            ) }
        }
    }

    private fun shouldCheckInForPibaBooking(preStayUiModel: PreStayUiModel): Boolean {
        val isPibaCNP = _state.value.preStayModel?.paymentOption?.isPibaCNPBooking() ?: false
        val isPibaCNPFlow = isPibaCNP && state.value.preselectedRoomSelections.any { it.selectedUpsells.isNotEmpty() }

        if (isPibaCNPFlow) {
            confirmPreCheckIn(preStayUiModel, isPibaCNPFlow)
            return true
        }
        return false
    }

    private fun performAuthorizeCard() {
        viewModelScope.launch {
            authorizeCardUseCase.invoke(
                AuthorizeCardRequestBody(
                    UUID.randomUUID().toString(),
                    configuration.graphQLUrl.replace("api", "restapi"),
                    deviceLocaleProvider.getDeviceLanguage(),
                    deviceLocaleProvider.getDeviceLocale().country.lowercase()
                )
            )
                .onStart { _state.update { it.copy(isLoading = true) } }
                .collect { result ->
                    when(result) {
                        is Result.Success -> {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    navigation = NavigationDestination.AuthorizeCardScreen(
                                        ThreeCpInputAuthorizeCard(result.data?.paymentRedirect?: EMPTY_STRING)
                                    )
                                )
                            }
                        }
                        is Result.Error -> {
                            _state.update { it.copy(isLoading = false, error = Error.GenericError()) }
                        }
                    }
                }
        }
    }

    private fun generateAndSaveRegCardPdf(transactionId: String, preStayUiModel: PreStayUiModel) {
        viewModelScope.launch {
            generateAndSaveRegCardPdfUseCase.invoke(
                preStayUiModel.preStayDetails.hotelId,
                _state.value.regCardPdfModel!!.apply { this.transactionId = transactionId }
            )
                .onStart { _state.update { it.copy(isLoading = true) } }
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            performPreCheckInInitiationRequest(preStayUiModel)
                        }

                        is Result.Error -> {
                            _state.update { it.copy(isLoading = false, error = Error.GenericError()) }
                        }
                    }
                }
        }
    }

    private fun performPreCheckInInitiationRequest(preStayUiModel: PreStayUiModel) {
        viewModelScope.launch {
            preCheckInInitiationUseCase.invoke(
                PreCheckInRequestBody(
                    arrivalTime = preStayUiModel.preStayDetails.startDate.toString(),
                    reservationId = preStayUiModel.preStayDetails.reservationGuests.first().reservationId,
                    hotelId = preStayUiModel.preStayDetails.hotelId
                )
            )
                .onStart { _state.update { it.copy(isLoading = true) } }
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            confirmPreCheckIn(preStayUiModel)
                        }

                        is Result.Error -> {
                            _state.update { it.copy(isLoading = false, error = Error.GenericError()) }
                        }
                    }
                }
        }
    }

    private fun confirmPreCheckIn(preStayUiModel: PreStayUiModel, isPibaCnp: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            confirmPreCheckInUseCase(preStayUiModel.basketReference, isPibaCnp)
                .collect { result ->
                    val isPaymentComplete = result is Result.Success && result.data.basketStatus == PRE_CHECKED_IN
                    logAnalytics(preStayUiModel, isPaymentComplete)
                    when (result) {
                        is Result.Success -> {
                            if (result.data.basketStatus == PRE_CHECKED_IN) {
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        navigation = NavigationDestination.CompletionScreen(
                                            CheckInCompletionModel(
                                                preStayUiModel.preStayDetails.bookerDetails.leadBookerFirstName,
                                                preStayUiModel.preStayHeaderInfo.hotelImage,
                                                preStayUiModel.preStayHeaderInfo.hotelBrand,
                                                preStayUiModel.bookingReference,
                                                preStayUiModel.preStayDetails.hotelId
                                            )
                                        )
                                    )
                                }
                            } else {
                                _state.update { it.copy(isLoading = false, error = Error.GenericError(isPibaCnp)) }
                            }
                        }

                        is Result.Error -> {
                            _state.update { it.copy(isLoading = false, error = Error.GenericError(isPibaCnp)) }
                        }
                    }
                }
        }
    }

    private fun logAnalytics(preStayUiModel: PreStayUiModel, isPaymentComplete: Boolean) {
        val checkInDay = formattedCheckInDay(preStayUiModel.preStayDetails.startDate) ?: StringUtils.EMPTY_STRING
        val checkOutDay = formattedCheckOutDay(preStayUiModel.preStayDetails.endDate) ?: StringUtils.EMPTY_STRING
        trackingAnalytics.track(
            CIOL_COMPLETION,
            CiolCompletionAnalyticsData(
                EMPTY_STRING,
                true,
                CiolAnalyticsModel(
                    bookingId = preStayUiModel.bookingReference,
                    action = AnalyticsConstants.Action.CIOL_CONFIRMATION_ACTION,
                    checkInDate = preStayUiModel.preStayDetails.startDate.format((DateFormat.SLASHED_DAY_MONTH_YEAR)),
                    checkInDay = checkInDay,
                    checkOutDay = checkOutDay,
                    checkInOutDay = "$checkInDay-$checkOutDay",
                    checkOutDate = preStayUiModel.preStayDetails.endDate.format((DateFormat.SLASHED_DAY_MONTH_YEAR)),
                    noNights = preStayUiModel.preStayDetails.numberOfNights.toString(),
                    noRooms = preStayUiModel.preStayDetails.rooms.size.toString(),
                    noAdults = preStayUiModel.preStayDetails.numberOfAdults.toString(),
                    noChildren = preStayUiModel.preStayDetails.numberOfChildren.toString(),
                    hotelId = preStayUiModel.preStayDetails.hotelId
                ),
                CiolCompletionAnalyticsModel(),
                specialOccasion = _state.value.specialOccasion,
                isPaymentComplete = isPaymentComplete
            )
        )
    }

    fun onUpsellClicked(upsellItem: UpsellEntry, bookingReference: String) {
        val (upsellType, availableUpsells) = upsellItem
            .buildUpsellPairForUpsellDetails(_state.value.upsellItems, _state.value.bookingConfirmation?.hasChildren())

        // Make sure that roomSelections only contains the selected upsells for the current room
        val currentRoomId = _state.value.bookingConfirmation!!.reservationByIdList[0].reservationId
        val shouldNavigateToRoomSelectionsFragment = _state.value.bookingConfirmation?.reservationByIdList?.size != 1
        val currentRoomSelections = _state.value.roomSelections
            .getCurrentRoomSelections(currentRoomId, upsellType, shouldNavigateToRoomSelectionsFragment)

        _state.value.bookingConfirmation?.let { nonNullBookingConfirmation ->
            if (shouldNavigateToRoomSelectionsFragment && arrayOf(UpsellType.BREAKFAST, UpsellType.MEAL_DEAL, UpsellType.WIFI).contains(upsellType)) {
                _state.update {
                    it.copy(
                        action = GoToSelectRoom(
                            nonNullBookingConfirmation.convertToBookingConfirmationUiModel(),
                            _state.value.preselectedRoomSelections,
                            currentRoomSelections,
                            _state.value.upsellItems,
                            upsellType
                        )
                    )
                }
                return
            }

            if (arrayOf(UpsellType.BREAKFAST, UpsellType.MEAL_DEAL).contains(upsellType)) {
                availableUpsells.updateUpsells(currentRoomSelections.first())
            }

            val upsellDetails = buildUpsellDetailsModel(
                roomId = currentRoomId,
                roomSelections = currentRoomSelections,
                availableUpsells = availableUpsells,
                bookingConfirmation = nonNullBookingConfirmation,
                upsellType = upsellType,
                bookingId = bookingReference
            )

            _state.update {
                it.copy(
                    action = when (upsellItem) {
                        is ExtrasItemUiModel,
                        is BreakfastUiModel,
                        is MealUiModel -> ShowUpsellDetailsBottomSheet(upsellDetails)
                    }
                )
            }

        } ?: run {
            _state.update { it.copy(error = Error.GenericError()) }
        }
    }

    fun onActionPerformed() {
        _state.update { it.copy(action = null) }
    }

    fun onUpsellDetailsClosed(roomSelections: List<RoomSelection>) {
        val currentRoomSelections = _state.value.roomSelections.deepCopy()

        // Upsell items from the state contain a breakfast item, which can not be mapped correctly
        val upsellsToUpdate = mutableListOf<UpsellEntry>()
        _state.value.upsellItems
            .filterIsInstance<UpsellEntry>()
            .copy()
            .addItems(upsellsToUpdate)

        roomSelections.forEach { roomSelection ->
            currentRoomSelections.updateRoomSelections(roomSelection)
        }
        upsellsToUpdate.addUpsellsSelectionsFromAllRooms(roomSelections)

        val totalRoomsAdults = _state.value.bookingConfirmation?.reservationByIdList?.sumOf { it.roomStay.adultsNumber }?.toInt() ?: -1
        _state.update {
            it.copy(
                upsellItems = formatUpsellsList(
                    upsellsToUpdate.toMutableList()
                        .organizeToCategories()
                        .disableMultiMealSelection(totalRoomsAdults)
                ),
                roomSelections = currentRoomSelections
            )
        }

        _state.value.priceBreakdownModel?.let {
            updatePriceBreakdownUseCase(
                it.copy(roomSelections = currentRoomSelections).mapToDomainModel()
            )
        }
    }

    fun onErrorDisplayed() {
        _state.update { it.copy(error = null) }
    }

    fun trackExpandButton(priceAmount: Double) {
        _state.value.preStayModel?.let {
            logCiolAnalytics(
                analytics = trackingAnalytics,
                preStayModel= it.convertToUiModel(),
                screenName = UPSELLS,
                action = PRICE_BREAKDOWN_ACTION,
                totalPrice = priceAmount.toString()
            )
        }
    }
}