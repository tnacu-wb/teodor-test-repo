package com.whitbread.premierinn.hoteldetails.discountcode

import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.domain.hotel.entity.HotelBookingAvailabilityState

data class DiscountCodeBottomSheetState(
    val isLoading: Boolean = false,
    val availabilityState: HotelBookingAvailabilityState? = null,
    val discountCode: String = EMPTY_STRING,
    val errorType: DiscountCodeAction.DiscountAppliedError.ErrorType? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isSuccess: Boolean = false,
    val appliedPromoCode: String? = null,
    val promoKind: String? = null,
    val event: DiscountCodeBottomSheetEvent? = null
)

fun DiscountCodeBottomSheetState.clearError(): DiscountCodeBottomSheetState =
    copy(errorType = null)

fun DiscountCodeBottomSheetState.withLoading(loading: Boolean): DiscountCodeBottomSheetState =
    copy(isLoading = loading)

fun DiscountCodeBottomSheetState.withSuccess(
    promoCode: String,
    availability: HotelBookingAvailabilityState,
    successMessage: String? = null
): DiscountCodeBottomSheetState = copy(
    discountCode = EMPTY_STRING,
    isLoading = false,
    isSuccess = true,
    appliedPromoCode = promoCode,
    availabilityState = availability,
    errorType = null,
    errorMessage = null,
    successMessage = successMessage
)

fun DiscountCodeBottomSheetState.withError(
    type: DiscountCodeAction.DiscountAppliedError.ErrorType,
    message: String? = null
): DiscountCodeBottomSheetState = copy(
    isLoading = false,
    isSuccess = false,
    errorType = type,
    errorMessage = message,
    successMessage = null
)