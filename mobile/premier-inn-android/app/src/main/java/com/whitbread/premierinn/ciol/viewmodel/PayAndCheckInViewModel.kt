package com.whitbread.premierinn.ciol.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.braintreepayments.api.UserCanceledException
import com.whitbread.premierinn.ciol.analytics.CiolAnalyticsModel
import com.whitbread.premierinn.ciol.analytics.CiolCompletionAnalyticsData
import com.whitbread.premierinn.ciol.analytics.CiolCompletionAnalyticsModel
import com.whitbread.premierinn.ciol.analytics.logCiolAnalytics
import com.whitbread.premierinn.ciol.entity.RegCardPdfModel
import com.whitbread.premierinn.ciol.entity.upsells.UpdateReservationPackagesUiModel
import com.whitbread.premierinn.ciol.entity.upsells.toUpdateReservationPackagesRequestBody
import com.whitbread.premierinn.ciol.fragments.PAY_AND_CHECK_IN_FEATURE_TAG
import com.whitbread.premierinn.ciol.mapper.convertToUiModel
import com.whitbread.premierinn.ciol.uimodel.CheckInCompletionModel
import com.whitbread.premierinn.ciol.uimodel.PriceBreakdownModel
import com.whitbread.premierinn.ciol.uimodel.mapToUiModel
import com.whitbread.premierinn.ciol.usecase.GenerateAndSaveRegCardPdfUseCase
import com.whitbread.premierinn.ciol.usecase.GetConfirmationSpinnerMessagesUseCase
import com.whitbread.premierinn.ciol.usecase.PaymentPollingUseCase
import com.whitbread.premierinn.common.AppConfiguration
import com.whitbread.premierinn.common.PaymentMethodType
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Action.CIOL_CONFIRMATION_ACTION
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.CIOL_COMPLETION
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState.CIOL_PAYMENTS
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.format.DateFormat
import com.whitbread.premierinn.common.format.format
import com.whitbread.premierinn.common.utils.StringUtils
import com.whitbread.premierinn.common.utils.isGerman
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.booking.entity.PreStayModel
import com.whitbread.premierinn.domain.ciol.usecase.GetCountriesUseCase
import com.whitbread.premierinn.domain.ciol.usecase.GetPriceBreakdownUseCase
import com.whitbread.premierinn.domain.common.ANDROID_APPS_CHANNEL
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.BUSINESS_FULL_NAME
import com.whitbread.premierinn.domain.common.LEISURE_FULL_NAME
import com.whitbread.premierinn.domain.common.entity.isPibaCPBooking
import com.whitbread.premierinn.domain.common.usecase.IsFeatureOn
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import com.whitbread.premierinn.domain.error.DomainError
import com.whitbread.premierinn.domain.error.DomainError.BillingAddressValidationError.CountriesRetrievalError
import com.whitbread.premierinn.domain.error.DomainError.BillingAddressValidationError.InvalidFieldsError
import com.whitbread.premierinn.domain.graphql.ciol.usecase.GetPaymentMethodsUseCase
import com.whitbread.premierinn.domain.graphql.ciol.usecase.InitiatePaymentUseCase
import com.whitbread.premierinn.domain.graphql.ciol.usecase.InitiatePaymentUseCase.InitiatePaymentResultData.GooglePayPayment
import com.whitbread.premierinn.domain.graphql.ciol.usecase.InitiatePaymentUseCase.InitiatePaymentResultData.NormalPayment
import com.whitbread.premierinn.domain.graphql.ciol.usecase.InitiatePaymentUseCase.InitiatePaymentResultData.PayPalPayment
import com.whitbread.premierinn.domain.graphql.ciol.usecase.PreCheckInUseCase
import com.whitbread.premierinn.domain.graphql.ciol.usecase.UpdateReservationPackagesUseCase
import com.whitbread.premierinn.domain.graphql.ciol.usecase.ValidateBillingAddressUseCase
import com.whitbread.premierinn.domain.graphql.ciol.usecase.ValidateBillingAddressUseCase.InvalidBillingAddressFields.FIRST_LINE
import com.whitbread.premierinn.domain.graphql.ciol.usecase.ValidateBillingAddressUseCase.InvalidBillingAddressFields.POSTCODE
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PaymentMethodsFlow
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PaymentMethodsRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PreCheckInRequestBody
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key
import com.whitbread.premierinn.domain.result.Result
import com.whitbread.premierinn.reviewbooking.ParcelablePaymentMethod
import com.whitbread.premierinn.reviewbooking.mappers.buildPaymentRequestBody
import com.whitbread.premierinn.reviewbooking.mappers.getListOfApplicablePaymentMethod
import com.whitbread.premierinn.reviewbooking.mappers.toParcelablePaymentMethod
import com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.formattedCheckInDay
import com.whitbread.premierinn.summary.analytics.SummaryAnalyticsData.formattedCheckOutDay
import com.whitbread.premierinn.threeCp.ThreeCpGooglePayInput
import com.whitbread.premierinn.threeCp.ThreeCpInputPayAndCheckIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PayAndCheckInViewModel @Inject constructor(
    private val deviceLocaleProvider: DeviceLocaleProvider,
    private val getPaymentMethodsUseCase: GetPaymentMethodsUseCase,
    private val initiatePaymentUseCase: InitiatePaymentUseCase,
    private val getCountriesUseCase: GetCountriesUseCase,
    private val validateBillingAddressUseCase: ValidateBillingAddressUseCase,
    private val appConfiguration: AppConfiguration,
    private val paymentPollingUseCase: PaymentPollingUseCase,
    private val getConfirmationSpinnerMessagesUseCase: GetConfirmationSpinnerMessagesUseCase,
    private val trackingAnalytics: TrackingAnalytics,
    private val updateReservationPackagesUseCase: UpdateReservationPackagesUseCase,
    private val getPriceBreakdownUseCase: GetPriceBreakdownUseCase,
    private val generateAndSaveRegCardPdfUseCase: GenerateAndSaveRegCardPdfUseCase,
    private val preCheckInInitiationUseCase: PreCheckInUseCase,
    private val isFeatureOn: IsFeatureOn
) : ViewModel() {

    private val _state = MutableStateFlow(PayAndCheckInState())
    val state: StateFlow<PayAndCheckInState> = _state

    data class PayAndCheckInState(
        val preStayModel: PreStayModel? = null,
        val paymentMethods: List<ParcelablePaymentMethod> = emptyList(),
        val selectedPaymentMethod: ParcelablePaymentMethod? = null,
        val billingAddressCode: String = EMPTY_STRING,
        val shouldDisplayDetailedAddress: Boolean = false,
        val payPalData: PayPalData? = null,
        val isInitiatePaymentLoading: Boolean = false,
        val paymentLoading: PaymentLoading? = null,
        val isLoading: Boolean = false,
        val countries: List<CountryDomain> = mutableListOf(),
        val selectedCountryIndex: Int? = null,
        val showFindAddress: Boolean = true,
        val billingAddress: Address? = null,
        val error: Error? = null,
        val billingAddressErrors: MutableList<BillingAddressError> = mutableListOf(),
        val navigation: NavigationDestination? = null,
        val priceBreakdownModel: PriceBreakdownModel? = null,
        val completionAnalyticsModel: CiolCompletionAnalyticsModel? = null,
        val specialOccasion: String = EMPTY_STRING,
        val wereUpsellsUpdatedRemotely: Boolean = false,
        val regCardPdfModel: RegCardPdfModel? = null,
        val isPibaCpEnabled: Boolean = false
    )

    data class PaymentLoading(val loadingMessage: String)

    data class PayPalData(val clientToken: String, val billingAddress: Address?)

    sealed class NavigationDestination {
        data class PayScreen(
            val data: ThreeCpInputPayAndCheckIn,
            val isForRegCard: Boolean = false,
            val shouldShowInfoBanner: Boolean = false
        ) : NavigationDestination()
        data class GPayScreen(val data: ThreeCpGooglePayInput) : NavigationDestination()
        data class CompletionScreen(
            val completionModel: CheckInCompletionModel
        ) : NavigationDestination()
    }

    sealed class Error {
        data object GenericError : Error()
        data object PaymentMethodsError : Error()
        data object InitiatePaymentError : Error()
        data object InitiatePaymentGenericError : Error()
        data class PaymentPendingError(val email: String) : Error()
        data object PaymentFailedError : Error()
        data object PaymentGenericError : Error()
        data object UpdatePackagesError : Error()
        data object RegCardError: Error()
    }

    sealed class BillingAddressError {
        object PostcodeInvalidError : BillingAddressError()
        object FirstLineInvalidError : BillingAddressError()
    }

    fun onScreenOpened(
        preStayModel: PreStayModel?,
        analyticsModel: CiolCompletionAnalyticsModel?,
        specialOccasion: String,
        regCardPdfModel: RegCardPdfModel?
    ) {
        _state.update { it.copy(isPibaCpEnabled = isFeatureOn.invoke(Key.FEATURE_PIBA_CP_ENABLED)) }

        _state.update {
            it.copy(
                specialOccasion = specialOccasion,
                regCardPdfModel = regCardPdfModel
            )
        }

        analyticsModel?.run {
            _state.update {
                it.copy(completionAnalyticsModel = this)
            }
        }
        preStayModel?.run {
            _state.update {
                it.copy(
                    preStayModel = this,
                    billingAddressCode = preStayModel.preStayDetails.bookerDetails.address.postalCode
                )
            }
            retrievePaymentMethods(preStayModel)
        } ?: run {
            _state.update { it.copy(error = Error.GenericError) }
        }

        getPriceBreakdown()
        logCiolAnalytics(trackingAnalytics, preStayModel?.convertToUiModel(), CIOL_PAYMENTS)
    }

    fun onBillingAddressSwitchStateChanged(isChecked: Boolean) {
        viewModelScope.launch {
            if (!isChecked) {
                _state.update { it.copy(isLoading = true) }
                getCountriesUseCase(null).collect { result ->
                    when (result) {
                        is Result.Success -> _state.update {
                            it.copy(
                                shouldDisplayDetailedAddress = true,
                                isLoading = false,
                                countries = result.data.first
                            )
                        }

                        is Result.Error -> _state.update {
                            it.copy(
                                shouldDisplayDetailedAddress = false,
                                isLoading = false,
                                error = Error.GenericError
                            )
                        }
                    }
                }
            } else {
                _state.update { it.copy(shouldDisplayDetailedAddress = false) }
            }
        }
    }

    fun onBillingAddressGenerated(billingAddress: Address) {
        _state.update { it.copy(billingAddress = billingAddress) }
    }

    fun onContinueButtonPressed(
        billingAddress: Address?,
        shouldDisplayDetailedAddress: Boolean,
        updateReservationPackagesUiModel: UpdateReservationPackagesUiModel?
    ) {
        validateAddress(billingAddress, shouldDisplayDetailedAddress, updateReservationPackagesUiModel)
    }

    fun validateAddress(
        billingAddress: Address?,
        shouldDisplayDetailedAddress: Boolean,
        updateReservationPackagesUiModel: UpdateReservationPackagesUiModel?
    ) {
        _state.update { it.copy(billingAddressErrors = mutableListOf()) }

        viewModelScope.launch {
            validateBillingAddressUseCase(
                shouldDisplayDetailedAddress = shouldDisplayDetailedAddress,
                billingAddress = billingAddress
            ).collect { result ->
                when(result) {
                    is Result.Success -> {
                        _state.value.preStayModel?.let {
                            logAnalytics(
                                preStayModel = it,
                                isContinueButton = true,
                                shouldDisplayDetailedAddress = shouldDisplayDetailedAddress,
                                pageName = CIOL_PAYMENTS,
                                action = CIOL_CONFIRMATION_ACTION
                            )
                        }
                        updateReservationPackages(
                            billingAddress = result.data.let { shouldDisplayDetailedAddress ->
                                if (shouldDisplayDetailedAddress) billingAddress else null
                            },
                            updateReservationPackagesUiModel = updateReservationPackagesUiModel
                        )
                    }
                    is Result.Error -> {
                        when (val error = result.error) {
                            CountriesRetrievalError -> _state.update { it.copy(error = Error.GenericError) }
                            is InvalidFieldsError -> {
                                _state.update {
                                    it.copy(billingAddressErrors = error.invalidFields.map { invalidField ->
                                        when (invalidField) {
                                            POSTCODE -> BillingAddressError.PostcodeInvalidError
                                            FIRST_LINE -> BillingAddressError.FirstLineInvalidError
                                        }
                                    }.toMutableList())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun resetBillingAddressFields() {
        _state.update {
            it.copy(
                billingAddressErrors = mutableListOf(),
                selectedCountryIndex = null,
                showFindAddress = true
            )
        }
    }

    fun onCountrySelected(ukCountryPosition: Int, selectedCountryIndex: Int) {
        _state.update {
            it.copy(
                selectedCountryIndex = selectedCountryIndex,
                showFindAddress = ukCountryPosition == selectedCountryIndex
            )
        }
    }

    fun onPayPalDataReady(
        payPalNonce: String?, payPalDeviceData: String?, billingAddress: Address?, error: Exception?
    ) {
        _state.update { it.copy(payPalData = null) }
        if (payPalNonce != null && payPalDeviceData != null) {
            initiatePayment(payPalNonce, payPalDeviceData, billingAddress, true)
        } else {
            if (error !is UserCanceledException) {
                _state.update {
                    it.copy(isInitiatePaymentLoading = false, error = Error.InitiatePaymentGenericError)
                }
            } else {
                // Display error only if the user hasn't cancelled the flow
                _state.update {
                    it.copy(isInitiatePaymentLoading = false)
                }
            }
        }
    }

    fun onPayPalDataCollectionInitialised() {
        _state.update { it.copy(payPalData = null) }
    }

    fun onUserNavigated() {
        _state.update { it.copy(navigation = null) }
    }

    fun onPaymentMethodSelected(paymentMethod: ParcelablePaymentMethod) {
        paymentMethod.isSelected = true

        val updatedPaymentMethods = _state.value.paymentMethods
        updatedPaymentMethods.forEach { it.isSelected = false }
        updatedPaymentMethods.first { it == paymentMethod }.isSelected = true

        _state.update {
            it.copy(
                paymentMethods = updatedPaymentMethods,
                selectedPaymentMethod = paymentMethod
            )
        }
    }

    fun onPaymentComponentClosed(transactionId: String = EMPTY_STRING) {
        _state.value.preStayModel?.let { preStayModel ->
            _state.update {
                it.copy(
                    paymentLoading = PaymentLoading(
                        getConfirmationSpinnerMessagesUseCase().messages[0].message
                    )
                )
            }
            paymentPollingUseCase.invoke(preStayModel.basketReference) { result ->
                logAnalytics(
                    preStayModel = preStayModel,
                    isPaymentComplete = result is Result.Success,
                    action = CIOL_CONFIRMATION_ACTION,
                    shouldShowUpsellsRevenueChange = true
                )
                when (result) {
                    is Result.Success -> {
                        if (_state.value.regCardPdfModel == null) {
                            _state.update {
                                it.copy(
                                    paymentLoading = null,
                                    navigation = NavigationDestination.CompletionScreen(
                                        CheckInCompletionModel(
                                            preStayModel.preStayDetails.bookerDetails.leadBookerFirstName,
                                            preStayModel.preStayHeaderInfo.hotelImage,
                                            preStayModel.preStayHeaderInfo.hotelBrand,
                                            preStayModel.bookingReference,
                                            preStayModel.preStayDetails.hotelId
                                        )
                                    )
                                )
                            }
                        } else {
                            if (isLeadGuestGerman()) {
                                performPreCheckInInitiationRequest(preStayModel)
                            } else {
                                generateAndSaveRegCardPdf(transactionId, preStayModel)
                            }
                        }

                    }
                    is Result.Error -> {
                        when (result.error) {
                            DomainError.PaymentPollingError.GenericError -> {
                                _state.update {
                                    it.copy(paymentLoading = null, error = Error.PaymentGenericError)
                                }
                            }
                            DomainError.PaymentPollingError.PaymentFailedError -> {
                                _state.update {
                                    it.copy(paymentLoading = null, error = Error.PaymentFailedError)
                                }
                            }
                            DomainError.PaymentPollingError.PaymentPendingError -> {
                                _state.update {
                                    it.copy(
                                        paymentLoading = null,
                                        error = Error.PaymentPendingError(
                                            preStayModel.preStayDetails.bookerDetails.leadBookerEmail
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } ?: run {
            _state.update {
                it.copy(error = Error.GenericError)
            }
        }
    }

    fun onErrorHandled() {
        _state.update { it.copy(error = null)}
    }

    private fun logAnalytics(
        preStayModel: PreStayModel,
        isPaymentComplete: Boolean? = null,
        isContinueButton: Boolean? = null,
        shouldDisplayDetailedAddress: Boolean? = null,
        totalPrice: String = EMPTY_STRING,
        pageName: String = CIOL_COMPLETION,
        action: String = EMPTY_STRING,
        shouldShowUpsellsRevenueChange: Boolean = false
    ) {
        val checkInDay =
            formattedCheckInDay(preStayModel.preStayDetails.startDate) ?: StringUtils.EMPTY_STRING
        val checkOutDay =
            formattedCheckOutDay(preStayModel.preStayDetails.endDate) ?: StringUtils.EMPTY_STRING
        trackingAnalytics.track(
            pageName,
            CiolCompletionAnalyticsData(
                _state.value.selectedPaymentMethod?.parcelableCard?.type ?: EMPTY_STRING,
                false,
                CiolAnalyticsModel(
                    bookingId = preStayModel.bookingReference,
                    action = action,
                    checkInDate = preStayModel.preStayDetails.startDate.format((DateFormat.SLASHED_DAY_MONTH_YEAR)),
                    checkInDay = checkInDay,
                    checkOutDay = checkOutDay,
                    checkInOutDay = "$checkInDay-$checkOutDay",
                    checkOutDate = preStayModel.preStayDetails.endDate.format((DateFormat.SLASHED_DAY_MONTH_YEAR)),
                    noNights = preStayModel.preStayDetails.numberOfNights.toString(),
                    noRooms = preStayModel.preStayDetails.rooms.size.toString(),
                    noAdults = preStayModel.preStayDetails.numberOfAdults.toString(),
                    noChildren = preStayModel.preStayDetails.numberOfChildren.toString(),
                    hotelId = preStayModel.preStayDetails.hotelId
                ),
                completionData = _state.value.completionAnalyticsModel
                    ?: CiolCompletionAnalyticsModel(),
                specialOccasion = _state.value.specialOccasion,
                isPaymentComplete = isPaymentComplete,
                isContinueButton = isContinueButton,
                shouldDisplayDetailedAddress = shouldDisplayDetailedAddress,
                totalPrice = totalPrice,
                shouldShowUpsellsRevenueChange = shouldShowUpsellsRevenueChange
            )
        )
    }

    fun updateReservationPackages(
        billingAddress: Address?,
        updateReservationPackagesUiModel: UpdateReservationPackagesUiModel?
    ) = viewModelScope.launch {
        if (updateReservationPackagesUiModel == null) {
            continueToPay(billingAddress)
            return@launch
        }
        _state.update { it.copy(isInitiatePaymentLoading = true) }
        updateReservationPackagesUseCase(
            updateReservationPackagesUiModel.toUpdateReservationPackagesRequestBody()
        ).collect { result ->
            when (result) {
                is Result.Error -> _state.update {
                    it.copy(
                        isInitiatePaymentLoading = false,
                        error = Error.UpdatePackagesError
                    )
                }

                is Result.Success -> {
                    _state.update {
                        it.copy(
                            isInitiatePaymentLoading = false, wereUpsellsUpdatedRemotely = true
                        )
                    }
                    continueToPay(billingAddress)
                }
            }
        }
    }

    private fun continueToPay(billingAddress: Address?) = viewModelScope.launch {
        if (_state.value.selectedPaymentMethod?.type == PaymentMethodType.PAYPAL.name) {
            _state.value.selectedPaymentMethod?.clientToken?.let { clientToken ->
                _state.update { it.copy(isInitiatePaymentLoading = true, payPalData = PayPalData(clientToken, billingAddress)) }
            } ?: run {
                _state.update { it.copy(error = Error.InitiatePaymentGenericError) }
            }
        } else {
            initiatePayment(billingAddress = billingAddress)
        }
    }

    private fun retrievePaymentMethods(preStayModel: PreStayModel) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getPaymentMethodsUseCase(
                PaymentMethodsRequestBody(
                    preStayModel.basketReference,
                    deviceLocaleProvider.getDeviceLanguage(),
                    deviceLocaleProvider.getCountryIfRegion(deviceLocaleProvider.getDeviceLocale())
                        .lowercase(Locale.getDefault()),
                    ANDROID_APPS_CHANNEL,
                    if (_state.value.preStayModel?.isBusinessBooking == true) BUSINESS_FULL_NAME else LEISURE_FULL_NAME,
                    PaymentMethodsFlow.CheckInOnline
                ),
                preStayModel.paymentOption.isPibaCPBooking() && _state.value.isPibaCpEnabled
            ).collect { result ->
                when (result) {
                    is Result.Success -> {
                        _state.update { it.copy(
                            isLoading = false,
                            paymentMethods = result.data.toParcelablePaymentMethod().getListOfApplicablePaymentMethod()
                        ) }
                        // Pre select the first payment method
                        if (_state.value.paymentMethods.isNotEmpty()) {
                            onPaymentMethodSelected(_state.value.paymentMethods.first())
                        }
                    }

                    is Result.Error -> {
                        _state.update { it.copy(isLoading = false, error = Error.PaymentMethodsError) }
                        Log.e(
                            "PayAndCheckIn",
                            result.error.message ?: "Error while retrieving message"
                        )
                    }
                }
            }
        }
    }

    private fun getPriceBreakdown() = viewModelScope.launch {
        getPriceBreakdownUseCase.invoke().collect { priceBreakdown ->
            _state.update { it.copy(priceBreakdownModel = priceBreakdown.mapToUiModel()) }
        }
    }

    private fun initiatePayment(
        payPalNonce: String? = null,
        payPalDeviceData: String? = null,
        billingAddress: Address? = null,
        isPayPal: Boolean = false,
        isInnBusinessUser: Boolean? = false
    ) = viewModelScope.launch {
        _state.value.apply {
            if (preStayModel != null && selectedPaymentMethod != null) {
                _state.update {
                    it.copy(isInitiatePaymentLoading = true)
                }
                initiatePaymentUseCase(
                    isPayPal,
                    buildPaymentRequestBody(
                        appConfiguration,
                        preStayModel,
                        selectedPaymentMethod,
                        deviceLocaleProvider.getDeviceLanguage(),
                        payPalNonce,
                        payPalDeviceData,
                        billingAddress,
                        isInnBusinessUser
                    )
                ).collect { result ->
                    Log.i(PAY_AND_CHECK_IN_FEATURE_TAG, "Initiate payment result arrived: ${result.toString()}")
                    when(result) {
                        is Result.Success -> {
                            when (val resultData = result.data) {
                                is NormalPayment -> {
                                    _state.update {
                                        it.copy(
                                            isInitiatePaymentLoading = false,
                                            navigation = NavigationDestination.PayScreen(
                                                ThreeCpInputPayAndCheckIn(resultData.htmlContent),
                                                _state.value.regCardPdfModel != null,
                                                _state.value.regCardPdfModel != null && !isLeadGuestGerman()
                                            )
                                        )
                                    }
                                }

                                is GooglePayPayment -> {
                                    _state.update {
                                        it.copy(
                                            isInitiatePaymentLoading = false,
                                            navigation = NavigationDestination.GPayScreen(
                                                ThreeCpGooglePayInput(
                                                    resultData.iPageSessionIdForGPay,
                                                    resultData.providerUrl
                                                )
                                            )
                                        )
                                    }
                                }

                                is PayPalPayment -> {
                                    _state.update { it.copy(isInitiatePaymentLoading = false) }
                                    onPaymentComponentClosed()
                                }
                            }
                        }
                        is Result.Error -> {
                            val error = when (result.error) {
                                is DomainError.PaymentError.CouldNotInitiatePaymentError -> Error.InitiatePaymentError
                                is DomainError.PaymentError.GenericError -> Error.InitiatePaymentGenericError
                            }
                            _state.update { it.copy(isInitiatePaymentLoading = false, error = error) }
                        }
                    }
                }
            } else {
                _state.update { it.copy(error = Error.GenericError) }
            }
        }
    }

    private fun generateAndSaveRegCardPdf(transactionId: String, preStayModel: PreStayModel) {
        viewModelScope.launch {
            generateAndSaveRegCardPdfUseCase.invoke(
                preStayModel.preStayDetails.hotelId,
                _state.value.regCardPdfModel!!.apply { this.transactionId = transactionId }
            )
                .onStart { _state.update { it.copy(isLoading = true) } }
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            performPreCheckInInitiationRequest(preStayModel)
                        }

                        is Result.Error -> {
                            _state.update { it.copy(isLoading = false, paymentLoading = null, error = Error.RegCardError) }
                        }
                    }
                }
        }
    }

    private fun performPreCheckInInitiationRequest(preStayModel: PreStayModel) {
        viewModelScope.launch {
            preCheckInInitiationUseCase.invoke(
                PreCheckInRequestBody(
                    arrivalTime = preStayModel.preStayDetails.startDate.toString(),
                    reservationId = preStayModel.preStayDetails.reservationGuests.first().reservationId,
                    hotelId = preStayModel.preStayDetails.hotelId
                )
            )
                .onStart { _state.update { it.copy(isLoading = true) } }
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            _state.update {
                                it.copy(
                                    paymentLoading = null,
                                    navigation = NavigationDestination.CompletionScreen(
                                        CheckInCompletionModel(
                                            preStayModel.preStayDetails.bookerDetails.leadBookerFirstName,
                                            preStayModel.preStayHeaderInfo.hotelImage,
                                            preStayModel.preStayHeaderInfo.hotelBrand,
                                            preStayModel.bookingReference,
                                            preStayModel.preStayDetails.hotelId
                                        )
                                    )
                                )
                            }
                        }

                        is Result.Error -> {
                            _state.update { it.copy(isLoading = false, paymentLoading = null, error = Error.RegCardError) }
                        }
                    }
                }
        }
    }

    fun trackExpandButton(totalAmount: Double) {
        _state.value.preStayModel?.let {
            logAnalytics(
                preStayModel = it,
                totalPrice = totalAmount.toString(),
                pageName = CIOL_PAYMENTS
            )
        }
    }

    private fun isLeadGuestGerman(): Boolean = _state.value.preStayModel?.preStayDetails?.reservationGuests
        ?.find { guest -> !guest.isAccompanyingGuest }?.nationality?.isGerman() == true
}
