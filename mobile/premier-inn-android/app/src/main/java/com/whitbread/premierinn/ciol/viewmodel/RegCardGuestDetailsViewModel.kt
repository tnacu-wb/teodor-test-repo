package com.whitbread.premierinn.ciol.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsData.Companion.ADDITIONAL_GUEST_ADD_BUTTON_KEY
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsData.Companion.BUTTON_CLICK
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsData.Companion.CIOL_CONTINUE_BUTTON_KEY
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsData.Companion.LEAD_GUEST_ADD_BUTTON_KEY
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsData.Companion.REG_CARD_GUEST_DETAILS
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsModel
import com.whitbread.premierinn.ciol.analytics.CiolCompletionAnalyticsData
import com.whitbread.premierinn.ciol.analytics.CiolCompletionAnalyticsModel
import com.whitbread.premierinn.ciol.analytics.RegCardAnalyticsData
import com.whitbread.premierinn.ciol.entity.AdditionalGuestUiModel
import com.whitbread.premierinn.ciol.entity.LeadGuestUiModel
import com.whitbread.premierinn.ciol.entity.PreStayUiModel
import com.whitbread.premierinn.ciol.entity.RegCardGuest
import com.whitbread.premierinn.ciol.entity.RegCardPdfModel
import com.whitbread.premierinn.ciol.entity.mapToRegCardGuest
import com.whitbread.premierinn.ciol.entity.mapToStayingGuestDetailsRegCard
import com.whitbread.premierinn.ciol.fragments.REG_CARD_FEATURE_TAG
import com.whitbread.premierinn.ciol.uimodel.CheckInCompletionModel
import com.whitbread.premierinn.ciol.uimodel.PriceBreakdownModel
import com.whitbread.premierinn.ciol.uimodel.mapToUiModel
import com.whitbread.premierinn.ciol.usecase.GenerateAndSaveRegCardPdfUseCase
import com.whitbread.premierinn.ciol.usecase.ValidateRegCardGuestDetailsUseCase
import com.whitbread.premierinn.ciol.utils.copy
import com.whitbread.premierinn.ciol.utils.getId
import com.whitbread.premierinn.ciol.utils.getNationality
import com.whitbread.premierinn.ciol.viewmodel.RegCardGuestDetailsViewModel.NavigationDestination.CompletionScreen
import com.whitbread.premierinn.ciol.viewmodel.RegCardGuestDetailsViewModel.NavigationDestination.UpsellsScreen
import com.whitbread.premierinn.ciol.viewmodel.state.utils.buildCreateReservationGuestRegCardRequestBody
import com.whitbread.premierinn.common.AppConfiguration
import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Action.CIOL_CONFIRMATION_ACTION
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.THIRD_PARTY_EXPORTED
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.format.DateFormat
import com.whitbread.premierinn.common.format.format
import com.whitbread.premierinn.common.utils.StringUtils
import com.whitbread.premierinn.common.utils.isGerman
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.ciol.usecase.IsUpsellFlowEnabledUseCase
import com.whitbread.premierinn.domain.ciol.usecase.GetCountryNameFromIsoCodeUseCase
import com.whitbread.premierinn.domain.ciol.usecase.GetIsoCodeFromCountryNameUseCase
import com.whitbread.premierinn.domain.ciol.usecase.GetNationalityFromIsoCodeUseCase
import com.whitbread.premierinn.domain.ciol.usecase.GetPriceBreakdownUseCase
import com.whitbread.premierinn.domain.ciol.usecase.GetUserSelectedCountryFromNationalityUseCase
import com.whitbread.premierinn.domain.ciol.usecase.IsPassportRequiredForCountryUseCase
import com.whitbread.premierinn.domain.graphql.ciol.usecase.AuthorizeCardUseCase
import com.whitbread.premierinn.domain.graphql.ciol.usecase.ConfirmPreCheckInUseCase
import com.whitbread.premierinn.domain.graphql.ciol.usecase.PreCheckInUseCase
import com.whitbread.premierinn.domain.graphql.ciol.usecase.UpdateReservationGuestsUseCase
import com.whitbread.premierinn.domain.graphql.requestBodyModels.AuthorizeCardRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PreCheckInRequestBody
import com.whitbread.premierinn.domain.hotel.entity.Hotel
import com.whitbread.premierinn.domain.result.Result
import com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData
import com.whitbread.premierinn.threeCp.ThreeCpInputAuthorizeCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.math.sign

@HiltViewModel
class RegCardGuestDetailsViewModel @Inject constructor(
    private val isPassportRequiredForCountryUseCase: IsPassportRequiredForCountryUseCase,
    private val validateRegCardGuestDetailsUseCase: ValidateRegCardGuestDetailsUseCase,
    private val updateReservationGuestsUseCase: UpdateReservationGuestsUseCase,
    private val getCountryNameFromIsoCodeUseCase: GetCountryNameFromIsoCodeUseCase,
    private val getNationalityFromIsoCodeUseCase: GetNationalityFromIsoCodeUseCase,
    private val getIsoCodeFromCountryNameUseCase: GetIsoCodeFromCountryNameUseCase,
    private val getUserSelectedCountryFromNationality: GetUserSelectedCountryFromNationalityUseCase,
    private val preCheckInConfirmationUseCase: ConfirmPreCheckInUseCase,
    val deviceLocaleProvider: DeviceLocaleProvider,
    private val configuration: AppConfiguration,
    private val analytics: TrackingAnalytics,
    private val isUpsellFlowEnabled: IsUpsellFlowEnabledUseCase,
    private val getPriceBreakdownUseCase: GetPriceBreakdownUseCase,
    private val authorizeCardUseCase: AuthorizeCardUseCase,
    private val generateAndSaveRegCardPdfUseCase: GenerateAndSaveRegCardPdfUseCase,
    private val preCheckInInitiationUseCase: PreCheckInUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GuestDetailsState())
    val state: StateFlow<GuestDetailsState> = _state

    data class GuestDetailsState(
        val basketReference: String = EMPTY_STRING,
        val preStayModel: PreStayUiModel? = null,
        val priceBreakdownModel: PriceBreakdownModel? = null,
        val upsellItemsAvailable: Boolean = false,
        val stayingGuests: List<RegCardGuest>? = null,
        val invalidIds: List<String> = emptyList(),
        val guestIdsWithEmptyFields: List<String> = emptyList(),
        val isLoading: Boolean = false,
        val shouldNavigate: Boolean? = false,
        val navigation: NavigationDestination? = null,
        val error: Error? = null,
        val specialOccasion: String = EMPTY_STRING
    )

    sealed class NavigationDestination {

        data class UpsellsScreen(
            val regCardPdfModel: RegCardPdfModel
        ) : NavigationDestination()
        data class PayScreen(
            val preStayModel: PreStayUiModel,
            val outstandingBalanceAmount: String,
            val regCardPdfModel: RegCardPdfModel
        ) : NavigationDestination()
        data class CompletionScreen(
            val completionModel: CheckInCompletionModel
        ) : NavigationDestination()
        data class AuthorizeCardScreen(
            val data: ThreeCpInputAuthorizeCard
        ) : NavigationDestination()
    }

    sealed class Error {
        data object GenericError : Error()
        data object RegCardGuestSaveDetailsError : Error()
    }

    fun onScreenOpened(
        basketReference: String?,
        preStayModel: PreStayUiModel?,
        areUpsellItemsAvailable: Boolean,
        specialOccasion: String
    ) {
        var stayingGuests = mutableListOf<RegCardGuest>()

        _state.update {
            it.copy(specialOccasion = specialOccasion)
        }

        preStayModel?.let {
            stayingGuests = preStayModel.preStayDetails.reservationGuests
                .map { it.mapToRegCardGuest(preStayModel.preStayDetails.bookerDetails) }.toMutableList()

            repeat(preStayModel.preStayDetails.run { numberOfAdults + numberOfChildren } - stayingGuests.size) {
                stayingGuests.add(
                    AdditionalGuestUiModel(
                        id = UUID.randomUUID().toString(),
                        reservationId = preStayModel.preStayDetails.reservationGuests.first().reservationId
                    )
                )
            }
        }

        if (_state.value.stayingGuests.isNullOrEmpty()) {
            basketReference?.let {
                _state.update {
                    it.copy(
                        basketReference = basketReference,
                        preStayModel = preStayModel,
                        upsellItemsAvailable = areUpsellItemsAvailable,
                        stayingGuests = stayingGuests,
                        guestIdsWithEmptyFields = validateRegCardGuestDetailsUseCase(stayingGuests)
                    )
                }
            } ?: run {
                Log.w(REG_CARD_FEATURE_TAG, "basketReference was null")
            }
        }

        getPriceBreakdown()

        trackScreenOpened(preStayModel?.isThirdPartyBooking ?: false)
    }

    private fun getPriceBreakdown() = viewModelScope.launch {
        getPriceBreakdownUseCase.invoke().collect { priceBreakdown ->
            _state.update { it.copy(priceBreakdownModel = priceBreakdown.mapToUiModel()) }
        }
    }

    fun onContinueButtonClicked() {
        val invalidIds = _state.value.stayingGuests?.let { validateRegCardGuestDetailsUseCase(it) }
        if (invalidIds.isNullOrEmpty()) {
            updateReservationGuests()
        } else {
            _state.update { it.copy(invalidIds = invalidIds) }
        }
        trackContinueButton()
    }

    fun updateNavigation() {
        val outstandingBalanceIsPositive = _state.value.priceBreakdownModel?.outstandingBalance?.amount
            ?.let { sign(it) != 0F } == true
        val upsellFlowIsEnabled = isUpsellFlowEnabled()
        val regCardPdfModel = RegCardPdfModel.from(EMPTY_STRING, state.value)
        var updatedNavigation = state.value.navigation
        val isBusinessBooking = state.value.preStayModel?.isBusinessBooking == true
        val isThirdPartyBooking = state.value.preStayModel?.isThirdPartyBooking ?: false

        if (outstandingBalanceIsPositive) {
            updatedNavigation = if (!isBusinessBooking && upsellFlowIsEnabled && state.value.upsellItemsAvailable && !isThirdPartyBooking) {
                UpsellsScreen(regCardPdfModel)
            } else {
                state.value.preStayModel?.let { preStayModel ->
                    NavigationDestination.PayScreen(
                        preStayModel,
                        _state.value.priceBreakdownModel?.outstandingBalance?.amount.toString(),
                        regCardPdfModel
                    )
                }
            }
        } else {
            if (!isBusinessBooking && upsellFlowIsEnabled && state.value.upsellItemsAvailable && !isThirdPartyBooking) {
                updatedNavigation = UpsellsScreen(regCardPdfModel)
            } else {
                val leadGuestNationality = state.value.stayingGuests?.find { it is LeadGuestUiModel }?.getNationality()?: EMPTY_STRING
                if (getIsoCodeFromCountryNameUseCase(leadGuestNationality)?.isGerman() == true) {
                    performPreCheckInInitiationRequest()
                } else {
                    performAuthorizeCard()
                }
            }
        }

        _state.update {
            it.copy(
                navigation = updatedNavigation,
                shouldNavigate = false
            )
        }
    }

    fun authorizeCardCompletedSuccessfully(transactionId: String) {
        generateAndSaveRegCardPdf(transactionId)
    }

    private fun updateReservationGuests() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val preStayDetails = _state.value.preStayModel?.preStayDetails
            val basketReference = _state.value.basketReference
            if (preStayDetails != null) {
                _state.value.stayingGuests?.map {
                    it.mapToStayingGuestDetailsRegCard(
                        preStayDetails.bookerDetails.leadBookerFirstName,
                        preStayDetails.bookerDetails.leadBookerLastName
                    )
                }?.let { stayingGuests ->
                    buildCreateReservationGuestRegCardRequestBody(
                        basketReference = basketReference,
                        hotelId = preStayDetails.hotelId,
                        reasonForStay = preStayDetails.roomGuests.firstOrNull()?.purposeForStay,
                        preCheckIn = true,
                        bookerDetails = preStayDetails.bookerDetails,
                        language = deviceLocaleProvider.getDeviceLanguage(),
                        stayingGuests = stayingGuests
                    )
                }?.let { requestBody ->
                    updateReservationGuestsUseCase(requestBody)
                        .collect { result ->
                        when (result) {
                            is Result.Success -> {
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        shouldNavigate = true
                                    )
                                }
                            }

                            is Result.Error -> {
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        error = Error.RegCardGuestSaveDetailsError)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun performAuthorizeCard() {
        viewModelScope.launch {
            authorizeCardUseCase.invoke(AuthorizeCardRequestBody(
                UUID.randomUUID().toString(),
                configuration.graphQLUrl.replace("api", "restapi"),
                deviceLocaleProvider.getDeviceLanguage(),
                deviceLocaleProvider.getDeviceLocale().country.lowercase()
            ))
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
                            _state.update { it.copy(isLoading = false, error = Error.GenericError) }
                        }
                    }
                }
        }
    }

    private fun generateAndSaveRegCardPdf(transactionId: String) {
        viewModelScope.launch {
            generateAndSaveRegCardPdfUseCase.invoke(
                state.value.preStayModel?.preStayDetails?.hotelId ?: EMPTY_STRING,
                RegCardPdfModel.from(transactionId, state.value)
            )
                .onStart { _state.update { it.copy(isLoading = true) } }
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            performPreCheckInInitiationRequest()
                        }

                        is Result.Error -> {
                            _state.update { it.copy(isLoading = false, error = Error.GenericError) }
                        }
                    }
                }
        }
    }

    private fun performPreCheckInInitiationRequest() {
        viewModelScope.launch {
            preCheckInInitiationUseCase.invoke(PreCheckInRequestBody(
                arrivalTime = state.value.preStayModel?.preStayDetails?.startDate?.toString() ?: EMPTY_STRING,
                reservationId = state.value.preStayModel?.preStayDetails?.reservationGuests?.first()?.reservationId ?: EMPTY_STRING,
                hotelId = state.value.preStayModel?.preStayDetails?.hotelId ?: EMPTY_STRING
            ))
                .onStart { _state.update { it.copy(isLoading = true) } }
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            state.value.preStayModel?.let {
                                performPreCheckInConfirmationRequest(it)
                            }
                        }

                        is Result.Error -> {
                            _state.update { it.copy(isLoading = false, error = Error.GenericError) }
                        }
                    }
                }
        }
    }

    private fun performPreCheckInConfirmationRequest(preStayUiModel: PreStayUiModel) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            preCheckInConfirmationUseCase(preStayUiModel.basketReference)
                .collect { result ->
                    val isPaymentComplete = result is Result.Success && result.data.basketStatus == PRE_CHECKED_IN
                    logAnalytics(preStayUiModel, isPaymentComplete)
                    when (result) {
                        is Result.Success -> {
                            if (result.data.basketStatus == PRE_CHECKED_IN) {
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        navigation = CompletionScreen(preStayUiModel.buildCompletionModel())
                                    )
                                }
                            } else {
                                _state.update { it.copy(isLoading = false, error = Error.GenericError) }
                            }
                        }

                        is Result.Error -> {
                            _state.update { it.copy(isLoading = false, error = Error.GenericError) }
                        }
                    }
                }
        }
    }

    private fun logAnalytics(preStayUiModel: PreStayUiModel, isPaymentComplete: Boolean) {
        val checkInDay = SummaryAnalyticsData.formattedCheckInDay(preStayUiModel.preStayDetails.startDate)
            ?: StringUtils.EMPTY_STRING
        val checkOutDay = SummaryAnalyticsData.formattedCheckOutDay(preStayUiModel.preStayDetails.endDate)
            ?: StringUtils.EMPTY_STRING
        analytics.track(
            AnalyticsConstants.ScreenState.CIOL_COMPLETION,
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
                specialOccasion = _state.value.specialOccasion,
                isPaymentComplete = isPaymentComplete
            )
        )
    }

    private fun PreStayUiModel.buildCompletionModel() = with(this) {
        CheckInCompletionModel(
            leadBooker = preStayDetails.bookerDetails.leadBookerFirstName,
            hotelImage = preStayHeaderInfo.hotelImage,
            hotelBrand = preStayHeaderInfo.hotelBrand,
            bookingReference = bookingReference,
            hotelId = preStayDetails.hotelId
        )
    }

    fun getLeadGuestFullAddress(leadGuest: LeadGuestUiModel): String {
        return listOfNotNull(
            leadGuest.address.addressLine1,
            leadGuest.address.addressLine2,
            leadGuest.address.addressLine3,
            leadGuest.address.postalCode,
            leadGuest.address.cityName,
            getCountryNameFromIsoCodeUseCase(leadGuest.address.country)
        ).filter { element -> element.isNotEmpty() }.joinToString()
    }

    fun onFinishGuestEditing(updatedGuest: RegCardGuest) {
        val updatedGuestList = _state.value.stayingGuests?.copy()?.toMutableList()?.apply {
            replaceAll { currentGuest ->
                if (updatedGuest.getId() == currentGuest.getId()) {
                    updatedGuest
                } else currentGuest
            }
        }

        updatedGuestList?.let {
            _state.update {
                it.copy(
                    invalidIds = emptyList(),
                    stayingGuests = updatedGuestList.copy(),
                    guestIdsWithEmptyFields = validateRegCardGuestDetailsUseCase(updatedGuestList)
                )
            }
        }
    }

    fun onErrorHandled() {
        _state.update { it.copy(error = null) }
    }

    fun onUserNavigated() {
        _state.update { it.copy(navigation = null) }
    }
    fun isPassportRequiredForCountry(countryName: String) = isPassportRequiredForCountryUseCase(countryName, Hotel.Brand.PID.name)

    fun getNationalityFromIsoCode(nationalityIsoCode: String) = getNationalityFromIsoCodeUseCase(nationalityIsoCode).ifBlank {
        nationalityIsoCode
    }

    fun getUserSelectedCountry(nationality: String) = getUserSelectedCountryFromNationality(nationality)

    fun trackAddLeadGuest() {
        analytics.track(REG_CARD_GUEST_DETAILS, RegCardAnalyticsData(mutableMapOf(LEAD_GUEST_ADD_BUTTON_KEY to BUTTON_CLICK)))
    }

    fun trackAddAdditionalGuest() {
        analytics.track(REG_CARD_GUEST_DETAILS, RegCardAnalyticsData(mutableMapOf(
            ADDITIONAL_GUEST_ADD_BUTTON_KEY to BUTTON_CLICK)))
    }

    private fun trackContinueButton() {
        analytics.track(REG_CARD_GUEST_DETAILS, RegCardAnalyticsData(mutableMapOf(
            CIOL_CONTINUE_BUTTON_KEY to BUTTON_CLICK)))
    }

    private fun trackScreenOpened(isThirdPartyBooking: Boolean) {
        analytics.track(REG_CARD_GUEST_DETAILS, RegCardAnalyticsData(mutableMapOf(
            THIRD_PARTY_EXPORTED to isThirdPartyBooking.toString())))
    }
}
