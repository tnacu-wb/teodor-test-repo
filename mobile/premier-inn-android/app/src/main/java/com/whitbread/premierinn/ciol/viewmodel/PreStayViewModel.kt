package com.whitbread.premierinn.ciol.viewmodel

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsData.Companion.BUTTON_CLICK
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsData.Companion.CIOL_CONTINUE_BUTTON_KEY
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsModel
import com.whitbread.premierinn.ciol.analytics.CiolCompletionAnalyticsData
import com.whitbread.premierinn.ciol.analytics.CiolCompletionAnalyticsModel
import com.whitbread.premierinn.ciol.analytics.RegCardAnalyticsData
import com.whitbread.premierinn.ciol.analytics.logCiolAnalytics
import com.whitbread.premierinn.ciol.entity.PreStayUiModel
import com.whitbread.premierinn.ciol.mapper.convertToHotelPackagesRequestBody
import com.whitbread.premierinn.ciol.mapper.convertToUiModel
import com.whitbread.premierinn.ciol.mapper.mapToHotelPreferenceUiModel
import com.whitbread.premierinn.ciol.mapper.toHotelPreferenceDomain
import com.whitbread.premierinn.ciol.uimodel.CheckInCompletionModel
import com.whitbread.premierinn.ciol.uimodel.HotelPreferenceUiModel
import com.whitbread.premierinn.ciol.uimodel.PriceBreakdownModel
import com.whitbread.premierinn.ciol.uimodel.mapToUiModel
import com.whitbread.premierinn.common.CITY_TAX
import com.whitbread.premierinn.common.ECI_LCO
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Action.CIOL_CONFIRMATION_ACTION
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_ACTION_KEY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.CIOL_SPECIAL_OCCASION_KEY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.CIOL_COMPLETION
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.PRE_STAY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.PRE_STAY_CONTINUE_CLICK
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.format.DateFormat
import com.whitbread.premierinn.common.format.format
import com.whitbread.premierinn.common.utils.StringUtils
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.booking.entity.PreStayModel
import com.whitbread.premierinn.domain.ciol.usecase.IsUpsellFlowEnabledUseCase
import com.whitbread.premierinn.domain.ciol.usecase.GetPriceBreakdownUseCase
import com.whitbread.premierinn.domain.ciol.usecase.UpdatePriceBreakdownUseCase
import com.whitbread.premierinn.domain.common.DONATION_LIST
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.HAS_TWIN_PACKAGE_CODE
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.entity.isPibaCNPBooking
import com.whitbread.premierinn.domain.common.entity.isPibaCPBooking
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.graphql.ciol.usecase.ConfirmPreCheckInUseCase
import com.whitbread.premierinn.domain.graphql.ciol.usecase.GetHotelPreferencesUseCase
import com.whitbread.premierinn.domain.graphql.ciol.usecase.GetUpsellsUseCase
import com.whitbread.premierinn.domain.graphql.ciol.usecase.UpdateReservationPreferencesUseCase
import com.whitbread.premierinn.domain.graphql.hdp.entity.RoomSelectionDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.UpsellDomainItem
import com.whitbread.premierinn.domain.hotel.entity.Hotel
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key
import com.whitbread.premierinn.domain.result.Result
import com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.formattedCheckInDay
import com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.formattedCheckOutDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.sign

const val PRE_CHECKED_IN = "PRE_CHECKED_IN"
const val ONE_ROOM_RESERVATION = 1

private val TAG = PreStayViewModel::class.simpleName

@HiltViewModel
class PreStayViewModel @Inject constructor(
    private val confirmPreCheckInUseCase: ConfirmPreCheckInUseCase,
    private val getUpsellsUseCase: GetUpsellsUseCase,
    private val getHotelPreferencesUseCase: GetHotelPreferencesUseCase,
    private val updateReservationPreferencesUseCase: UpdateReservationPreferencesUseCase,
    private val deviceLocaleProvider: DeviceLocaleProvider,
    private val trackingAnalytics: TrackingAnalytics,
    private val isUpsellFlowEnabled: IsUpsellFlowEnabledUseCase,
    private val getPriceBreakdownUseCase: GetPriceBreakdownUseCase,
    private val updatePriceBreakdownUseCase: UpdatePriceBreakdownUseCase,
    private val isFeatureOn: IsFeatureOn
) : ViewModel() {

    @VisibleForTesting
    val _state = MutableStateFlow(PreStayState())
    val state: StateFlow<PreStayState> = _state

    data class PreStayState(
        val preStayModel: PreStayModel? = null,
        val upsellItems: List<UpsellDomainItem> = emptyList(),
        val preselectedRoomSelections: List<RoomSelectionDomain> = emptyList(),
        val upsellAvailabilityChanged: Boolean = false,
        var specialOccasions: List<HotelPreferenceUiModel>? = null,
        val selectedOccasion: HotelPreferenceUiModel? = null,
        val openBottomSheet: Boolean = false,
        val shouldDisplaySelectOccasionsLayout: Boolean = false,
        val showMissingOccasionError: Boolean = false,
        val navigation: NavigationDestination? = null,
        val isLoading: Boolean = false,
        val error: PreStaySharedViewModel.Error? = null,
        val shouldShowGuestInfo: Boolean = true,
        val priceBreakdownModel: PriceBreakdownModel? = null,
        val isPibaCpEnabled: Boolean = false
    )

    sealed class NavigationDestination {
        data object UpsellsFragment : NavigationDestination()

        data object PayAndCheckInFragment : NavigationDestination()

        data class CompletionScreen(
            val completionModel: CheckInCompletionModel
        ) : NavigationDestination()

        data class RegCardGuestDetailsFragment(
            val areUpsellItemsAvailable: Boolean
        ) : NavigationDestination()
    }

    fun onScreenOpened(preStayModel: PreStayModel) {
        logCiolAnalytics(trackingAnalytics, preStayModel.convertToUiModel(), PRE_STAY)

        _state.update { it.copy(isPibaCpEnabled = isFeatureOn.invoke(Key.FEATURE_PIBA_CP_ENABLED)) }

        _state.update { it.copy(preStayModel = preStayModel) }

        _state.update { it.copy(shouldShowGuestInfo = !(preStayModel.preStayDetails.rooms.size == ONE_ROOM_RESERVATION && preStayModel.preStayHeaderInfo.hotelBrand == Hotel.Brand.PID.name)) }

        getUpsells(preStayModel)
        getPriceBreakdown(preStayModel)
        retrieveSpecialOccasions(preStayModel.preStayDetails.hotelId, deviceLocaleProvider.getDeviceLanguage())
    }

    private fun getUpsells(preStayModel: PreStayModel) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            getUpsellsUseCase(preStayModel.convertToHotelPackagesRequestBody(deviceLocaleProvider)).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                upsellItems = result.data.availableUpsells,
                                preselectedRoomSelections = result.data.preselectedRoomSelections ?: emptyList(),
                                upsellAvailabilityChanged = true
                            )
                        }
                    }

                    is Result.Error -> {
                        Log.e(TAG, result.error.message ?: "Error while retrieving packages")
                    }
                }
            }
        }
    }

    private fun getPriceBreakdown(preStayModel: PreStayModel) = viewModelScope.launch {
        // If priceBreakdown wasn't populated yet, use preStayModel
        getPriceBreakdownUseCase().value.run {
            if (this.outstandingBalance.amount == 0f || this.nights == 0) {
                updatePriceBreakdownUseCase(this.copy(
                    outstandingBalance = preStayModel.outstandingBalance ?: PriceDomain.createDefault(),
                    nights = preStayModel.preStayDetails.numberOfNights
                ))
            }
        }

        getPriceBreakdownUseCase.invoke().collect { priceBreakdown ->
            _state.update { it.copy(priceBreakdownModel = priceBreakdown.mapToUiModel()) }
        }
    }

    fun onConfirmPreCheckInOrContinueNavigation(preStayUiModel: PreStayUiModel) {
        val upsellItemsAvailable = state.value.upsellItems.isNotEmpty()
        if (!preStayUiModel.preStayDetails.preCheckInStatus &&
            preStayUiModel.preStayDetails.rooms.size == ONE_ROOM_RESERVATION &&
            preStayUiModel.preStayHeaderInfo.hotelBrand == Hotel.Brand.PID.name) {
            _state.update { it.copy(navigation = NavigationDestination.RegCardGuestDetailsFragment(upsellItemsAvailable)) }
            return
        }
        val hasOutstandingBalance =
            preStayUiModel.outstandingBalance?.amount?.let { sign(it) != 0F } ?: false
        val upsellFlowEnabled = isUpsellFlowEnabled()

        if (shouldCheckInForPibaCNPBooking(preStayUiModel)) return

        if (state.value.isPibaCpEnabled && preStayUiModel.paymentOption.isPibaCPBooking() && hasOutstandingBalance && (hasNoPackages() || preStayUiModel.isBusinessBooking)) {
            _state.update {
                it.copy(
                    navigation = NavigationDestination.PayAndCheckInFragment
                )
            }
            return
        }

        if (hasOutstandingBalance) {
            _state.update {
                it.copy(
                    navigation = if (upsellFlowEnabled && upsellItemsAvailable && !preStayUiModel.isBusinessBooking && !preStayUiModel.isThirdPartyBooking)
                        NavigationDestination.UpsellsFragment else NavigationDestination.PayAndCheckInFragment
                )
            }
        } else {
            if (upsellFlowEnabled && upsellItemsAvailable && !preStayUiModel.isBusinessBooking && !preStayUiModel.isThirdPartyBooking) {
                _state.update { it.copy(navigation = NavigationDestination.UpsellsFragment) }
            } else {
                confirmPreCheckIn(preStayUiModel)
            }
        }
    }

    private fun shouldCheckInForPibaCNPBooking(preStayUiModel: PreStayUiModel): Boolean {
        val isPibaCNP = preStayUiModel.paymentOption.isPibaCNPBooking()
        val isPibaCNPFlow = isPibaCNP && (hasNoPackages() || preStayUiModel.isBusinessBooking)

        if (isPibaCNPFlow) {
            confirmPreCheckIn(preStayUiModel, isPibaCNPFlow)
            return true
        }
        return false
    }

    fun hasNoPackages(): Boolean {
        return state.value.preselectedRoomSelections.all { room ->
            room.packagesSelection.filterNot {
                pkg -> pkg.id in DONATION_LIST || pkg.id == CITY_TAX ||
                    pkg.id in ECI_LCO || pkg.id == HAS_TWIN_PACKAGE_CODE
            }.isEmpty()
        }
    }

    private fun confirmPreCheckIn(preStayUiModel: PreStayUiModel, isPibaCnp: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            preStayUiModel.let { model ->
                confirmPreCheckInUseCase(model.basketReference, isPibaCnp)
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
                                    _state.update { it.copy(isLoading = false, error = PreStaySharedViewModel.Error.GenericError(isPibaCnp)) }
                                }
                            }

                            is Result.Error -> {
                                _state.update { it.copy(isLoading = false, error = PreStaySharedViewModel.Error.GenericError(isPibaCnp)) }
                                Log.e(TAG,result.error.message ?: "Error when trying to confirm pre checkIn")
                            }
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
                    action = CIOL_CONFIRMATION_ACTION,
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
                _state.value.selectedOccasion?.label ?: EMPTY_STRING_DOMAIN,
                isPaymentComplete = isPaymentComplete
            )
        )
    }

    fun onUserNavigated() {
        _state.update { it.copy(navigation = null) }
    }

    fun onSpecialOccasionSelected(selectedOccasion: HotelPreferenceUiModel) {
        _state.update { it.copy(
            selectedOccasion = selectedOccasion, showMissingOccasionError = false) }
    }

    fun onOccasionsSwitchStateChanged(isChecked: Boolean) {
        _state.update {
            it.copy(
                selectedOccasion = null,
                showMissingOccasionError = false,
                shouldDisplaySelectOccasionsLayout = isChecked
            )
        }
    }

    fun retrieveSpecialOccasions(hotelId: String, language: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            getHotelPreferencesUseCase(hotelId, language).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                specialOccasions = result.data
                                    ?.map { hotelPreferenceDomain -> hotelPreferenceDomain.mapToHotelPreferenceUiModel() }
                            )
                        }
                    }

                    is Result.Error -> {
                        _state.update {
                            it.copy(isLoading = false)
                        }
                    }
                }
            }
        }
    }

    fun onSelectOccasionClicked() {
        _state.update { it.copy(openBottomSheet = true) }
    }

    fun updateReservationPreferences(preStayUiModel: PreStayUiModel, selectedOccasion: HotelPreferenceUiModel?) = viewModelScope.launch {
        selectedOccasion?.let { nonNullSelectedOccasion ->
            _state.update { it.copy(isLoading = true) }
            updateReservationPreferencesUseCase(
                hotelId = preStayUiModel.preStayDetails.hotelId,
                reservationsIds = preStayUiModel.preStayDetails.roomGuests.distinctBy { it.roomId }.map { it.roomId },
                selectedOccasion = nonNullSelectedOccasion.toHotelPreferenceDomain(),
                previousPreferences = preStayUiModel.preStayDetails.preferences.map { it.toHotelPreferenceDomain() }
            ).collect { result ->
                when (result) {
                    is Result.Error -> {
                        _state.update { it.copy(isLoading = false, error = PreStaySharedViewModel.Error.GenericError()) }
                    }

                    is Result.Success -> {
                        _state.update { it.copy(isLoading = false) }
                    }
                }
            }
        }
    }

    fun onOccasionsBottomSheetDisplayed() {
        _state.update { it.copy(openBottomSheet = false) }
    }

    fun showMissingOccasionError() {
        _state.update { it.copy(showMissingOccasionError = true) }
    }

    fun onErrorHandled() {
        _state.update { it.copy(error = null)}
    }

    fun trackContinueButton(occasion: String) {
        val isUkHotel = state.value.preStayModel?.preStayHeaderInfo?.hotelBrand == Hotel.Brand.PI.name
        trackingAnalytics.trackAction(
            PRE_STAY_CONTINUE_CLICK,
            RegCardAnalyticsData(mutableMapOf(CIOL_CONTINUE_BUTTON_KEY to BUTTON_CLICK).apply {
                if (occasion.isNotEmpty()) put(CIOL_SPECIAL_OCCASION_KEY, occasion)
                if (isUkHotel) put(CIOL_ACTION_KEY, EMPTY_STRING)
            })
        )
    }
}
