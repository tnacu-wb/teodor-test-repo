package com.whitbread.premierinn.hoteldetails.discountcode.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.common.SUB_CHANNEL
import com.whitbread.premierinn.domain.graphql.hdp.usecase.GraphQLHDPUseCase
import com.whitbread.premierinn.domain.graphql.promotions.entity.PromotionsInformationDomain
import com.whitbread.premierinn.domain.graphql.promotions.usecase.GraphQLPromotionsInformationUseCase
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelAvailabilityRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelInfoDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.RoomSearch
import com.whitbread.premierinn.domain.hotel.entity.HotelBookingAvailabilityState
import com.whitbread.premierinn.hoteldetails.discountcode.DiscountCodeAction
import com.whitbread.premierinn.hoteldetails.discountcode.DiscountCodeBottomSheetEvent
import com.whitbread.premierinn.hoteldetails.discountcode.DiscountCodeBottomSheetState
import com.whitbread.premierinn.hoteldetails.discountcode.DiscountCodeConstants
import com.whitbread.premierinn.hoteldetails.discountcode.DiscountCodeConstants.CODE_ALREADY_APPLIED
import com.whitbread.premierinn.hoteldetails.discountcode.DiscountCodeConstants.CODE_EXPIRED
import com.whitbread.premierinn.hoteldetails.discountcode.DiscountCodeConstants.INVALID
import com.whitbread.premierinn.hoteldetails.discountcode.DiscountCodeConstants.MULTIPLE_REDEEM
import com.whitbread.premierinn.hoteldetails.discountcode.DiscountCodeConstants.SUCCESS
import com.whitbread.premierinn.hoteldetails.discountcode.DiscountCodeConstants.UNAVAILABLE
import com.whitbread.premierinn.hoteldetails.discountcode.DiscountCodeInput
import com.whitbread.premierinn.hoteldetails.discountcode.clearError
import com.whitbread.premierinn.hoteldetails.discountcode.withError
import com.whitbread.premierinn.hoteldetails.discountcode.withLoading
import com.whitbread.premierinn.hoteldetails.discountcode.withSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscountCodeBottomSheetViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val graphQLHDPUseCase: GraphQLHDPUseCase,
    private val graphQLPromotionsInformationUseCase: GraphQLPromotionsInformationUseCase,
    private val logService: LogService,
    deviceLocaleProvider: DeviceLocaleProvider
) : ViewModel() {

    private val input: DiscountCodeInput by lazy {
        requireNotNull(savedStateHandle.get<DiscountCodeInput>(DiscountCodeConstants.BundleKeys.INPUT_KEY))
    }
    private val disposables = CompositeDisposable()
    private var applyDisposable: Disposable? = null
    private var clearDisposable: Disposable? = null
    private var isCleared = false

    private val _state = MutableStateFlow(DiscountCodeBottomSheetState())
    val state: StateFlow<DiscountCodeBottomSheetState> = _state.asStateFlow()

    private val language = deviceLocaleProvider.getDeviceLanguage()
    private val country =
        deviceLocaleProvider.getCountryIfRegion(deviceLocaleProvider.getDeviceLocale()).lowercase()

    private var pendingSuccessMessage: String? = null

    init {
        // First check if there's an applied promo code from the input
        input.appliedPromoCode?.takeIf { it.isNotEmpty() }?.let { inputPromoCode ->
            _state.value = _state.value.copy(
                discountCode = "", // Keep TextField empty
                appliedPromoCode = inputPromoCode,
                isSuccess = true,
                successMessage = input.appliedPromoMessage
            )
        }
    }

    fun processAction(action: DiscountCodeAction) {
        when (action) {
            is DiscountCodeAction.UpdateDiscountCode -> {
                // Just update the discount code - don't clear success state
                // Success state will only be cleared when a new code is successfully applied
                updateState {
                    it.copy(discountCode = action.code).clearError()
                }
            }

            is DiscountCodeAction.ApplyDiscountCode -> {
                applyDiscountCodeInternal(action.code)
            }

            is DiscountCodeAction.DiscountAppliedSuccess -> {
                handleSuccess(action.promoCode, action.availabilityState)
            }

            is DiscountCodeAction.ClearError ->
                updateState { it.clearError() }

            is DiscountCodeAction.RemoveDiscountCode ->
                clearDiscount()

            is DiscountCodeAction.Continue,
            is DiscountCodeAction.Close ->
                updateState { it.copy(event = DiscountCodeBottomSheetEvent.DiscountCodeBottomSheetClosed) }
        }
    }

    private fun applyDiscountCodeInternal(discountCode: String) {
        // Prevent duplicate calls while loading
        if (_state.value.isLoading || applyDisposable != null && !applyDisposable!!.isDisposed) return

        // Check if a discount code is already displayed
        if (_state.value.appliedPromoCode?.isNotEmpty() == true) {
            updateState {
                it.withError(DiscountCodeAction.DiscountAppliedError.ErrorType.MULTIPLE_REDEEM)
            }
            return
        }

        updateState { it.withLoading(true).clearError() }

        // Dispose previous applyDisposable
        applyDisposable?.dispose()

        try {
            applyDisposable = graphQLPromotionsInformationUseCase.execute(
                country = country,
                language = language,
                channel = input.channel,
                brand = input.hotelBrand,
                stayStartDate = input.arrivalDate,
                stayEndDate = input.departureDate,
                basketReference = EMPTY_STRING,
                promotionCode = discountCode,
                isPromoBox = true
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { promoResponse ->
                        if (isCleared) return@subscribe
                        handlePromoValidationResponse(promoResponse, discountCode)
                    },
                    { _ ->
                        if (isCleared) return@subscribe
                        handleApiError()
                    }
                )
            applyDisposable?.let { disposables.add(it) }
        } catch (_: Exception) {
            handleApiError()
        }
    }

    private fun handlePromoValidationResponse(
        promoResponse: PromotionsInformationDomain,
        discountCode: String
    ) {
        val status = promoResponse.promoBoxStatus
        val messageKey = promoResponse.promoBoxMessageKey
        val getMessageKeyFromStatus = status?.let { DiscountCodeConstants.getMessageKeyForStatus(it) }
        val message = DiscountCodeConstants.resolvePromoBoxMessage(promoResponse, messageKey)
            ?: DiscountCodeConstants.resolvePromoBoxMessage(promoResponse, getMessageKeyFromStatus)
            ?: promoResponse.promoBox?.whenInvalid
        val promoKind = promoResponse.promoKind

        when (status) {
            SUCCESS -> {
                pendingSuccessMessage = message
                updateState { it.copy(promoKind = promoKind) }
                fetchAvailabilityWithPromoCode(discountCode)
            }

            CODE_ALREADY_APPLIED -> {
                updateState {
                    it.withLoading(false).withError(
                        DiscountCodeAction.DiscountAppliedError.ErrorType.CODE_ALREADY_APPLIED,
                        message
                    ).copy(event = DiscountCodeBottomSheetEvent.DiscountCodeError(
                        promoCode = discountCode,
                        errorMessage = message,
                        promoKind = promoKind
                    ))
                }
            }

            INVALID -> {
                updateState {
                    it.withLoading(false).withError(
                        DiscountCodeAction.DiscountAppliedError.ErrorType.INVALID,
                        message
                    ).copy(event = DiscountCodeBottomSheetEvent.DiscountCodeError(
                        promoCode = discountCode,
                        errorMessage = message,
                        promoKind = promoKind
                    ))
                }
            }

            CODE_EXPIRED -> {
                updateState {
                    it.withLoading(false).withError(
                        DiscountCodeAction.DiscountAppliedError.ErrorType.CODE_EXPIRED,
                        message
                    ).copy(event = DiscountCodeBottomSheetEvent.DiscountCodeError(
                        promoCode = discountCode,
                        errorMessage = message,
                        promoKind = promoKind
                    ))
                }
            }
            UNAVAILABLE -> {
                updateState {
                    it.withLoading(false).withError(
                        DiscountCodeAction.DiscountAppliedError.ErrorType.INVALID,
                        message
                    ).copy(event = DiscountCodeBottomSheetEvent.DiscountCodeError(
                        promoCode = discountCode,
                        errorMessage = message,
                        promoKind = promoKind
                    ))
                }
            }

            MULTIPLE_REDEEM -> {
                updateState {
                    it.withLoading(false).withError(
                        DiscountCodeAction.DiscountAppliedError.ErrorType.MULTIPLE_REDEEM,
                        message
                    ).copy(event = DiscountCodeBottomSheetEvent.DiscountCodeError(
                        promoCode = discountCode,
                        errorMessage = message,
                        promoKind = promoKind
                    ))
                }
            }

            else -> {
                updateState {
                    it.withLoading(false).withError(
                        DiscountCodeAction.DiscountAppliedError.ErrorType.GENERAL,
                        message
                    ).copy(event = DiscountCodeBottomSheetEvent.DiscountCodeError(
                        promoCode = discountCode,
                        errorMessage = message,
                        promoKind = promoKind
                    ))
                }
            }
        }
    }

    private fun fetchAvailabilityWithPromoCode(discountCode: String) {
        try {
            val requestBody = createRequestBody()

            applyDisposable = graphQLHDPUseCase.fetchHotelAvailabilityAndRatesInfoAndRoomType(
                requestBody, language, country, input.hotelCode, input.channel, discountCode, _state.value.promoKind
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { availabilityState ->
                        // Ignore loading states, only process real results
                        if (isCleared) return@subscribe
                        if (availabilityState.action is GraphQLHDPUseCase.AvailabilityAction.AvailabilityLoading) {
                            return@subscribe
                        }
                        processAvailabilityResponse(availabilityState, discountCode)
                    },
                    { _ ->
                        if (isCleared) return@subscribe
                        handleApiError()
                    }
                )
            applyDisposable?.let { disposables.add(it) }
        } catch (_: Exception) {
            handleApiError()
        }
    }

    private fun handleApiError() {
        updateState {
            it.withLoading(false)
                .withError(DiscountCodeAction.DiscountAppliedError.ErrorType.GENERAL)
                .copy(event = DiscountCodeBottomSheetEvent.DiscountCodeError(
                    promoCode = it.discountCode,
                    errorMessage = null,
                    promoKind = it.promoKind
                ))
        }
    }

    private fun processAvailabilityResponse(
        availabilityState: HotelBookingAvailabilityState?,
        discountCode: String
    ) {
        if (availabilityState == null) {
            handleApiError()
            return
        }
        if (availabilityState.isHotelAvailabilitySuccessfulHDP) {
            handleSuccess(discountCode.uppercase(), availabilityState)
        } else {
            handleApiError()
        }
    }


    private fun handleSuccess(promoCode: String, availabilityState: HotelBookingAvailabilityState) {
        updateState {
            it.withSuccess(promoCode, availabilityState, pendingSuccessMessage).copy(
                event = DiscountCodeBottomSheetEvent.UpdateHotelDetailsRates(
                    promoCode,
                    availabilityState,
                    pendingSuccessMessage,
                    promoKind = it.promoKind
                )
            )
        }
        pendingSuccessMessage = null
    }

    private fun clearDiscount() {
        clearDisposable?.dispose()
        updateState { it.withLoading(true).clearError() }
        try {
            val requestBody = createRequestBody()
            clearDisposable = graphQLHDPUseCase.fetchHotelAvailabilityAndRatesInfoAndRoomType(
                requestBody,
                language,
                country,
                input.hotelCode,
                input.channel,
                null, // No discount code
                null  // No promoKind
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { availabilityState ->
                        if (isCleared) return@subscribe
                        handleClearDiscountSuccess(availabilityState)
                    },
                    { _ ->
                        if (isCleared) return@subscribe
                        resetToInitialState()
                    }
                )
            clearDisposable?.let { disposables.add(it) }
        } catch (_: Exception) {
            resetToInitialState()
        }
    }

    private fun handleClearDiscountSuccess(availabilityState: HotelBookingAvailabilityState) {
        if (availabilityState.isHotelAvailabilitySuccessfulHDP) {
            updateState {
                it.copy(
                    isLoading = false,
                    isSuccess = false,
                    appliedPromoCode = null,
                    availabilityState = availabilityState,
                    discountCode = EMPTY_STRING,
                    successMessage = null,
                    promoKind = null
                ).clearError().copy(
                    event = DiscountCodeBottomSheetEvent.UpdateHotelDetailsRates(
                        EMPTY_STRING,
                        availabilityState,
                        null,
                        promoKind = null
                    )
                )
            }
        } else {
            resetToInitialState()
        }
    }

    private fun resetToInitialState() {
        updateState {
            it.copy(
                isLoading = false,
                isSuccess = false,
                appliedPromoCode = null,
                availabilityState = null,
                discountCode = EMPTY_STRING
            ).clearError()
        }
    }

    private fun createRequestBody(): HotelAvailabilityRequestBody {
        try {
            return HotelAvailabilityRequestBody(
                input.arrivalDate,
                input.departureDate,
                HotelInfoDetails(input.hotelCode),
                input.roomSearchData.map {
                    RoomSearch(it.adultsNumber, it.childrenNumber, it.cotRequired, it.roomType)
                },
                BookingChannelDetails(input.channel, SUB_CHANNEL, language),
                input.hotelBrand,
                null,
                input.operaCompanyId
            )
        } catch (error: Exception) {
            logService.logException(
                error,
                "DiscountCodeBottomSheet: Failed to create booking request - ${error.message}"
            )
            throw IllegalArgumentException(
                "Failed to create booking request: ${error.message}",
                error
            )
        }
    }

    private fun updateState(update: (DiscountCodeBottomSheetState) -> DiscountCodeBottomSheetState) {
        viewModelScope.launch {
            _state.value = update(_state.value)
        }
    }

    fun onEventConsumed() {
        updateState { it.copy(event = null) }
    }

    override fun onCleared() {
        super.onCleared()
        isCleared = true
        applyDisposable?.dispose()
        clearDisposable?.dispose()
        disposables.clear()
    }
}