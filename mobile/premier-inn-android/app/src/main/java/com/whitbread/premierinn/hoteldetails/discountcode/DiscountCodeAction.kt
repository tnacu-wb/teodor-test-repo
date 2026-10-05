package com.whitbread.premierinn.hoteldetails.discountcode

import com.whitbread.premierinn.domain.hotel.entity.HotelBookingAvailabilityState

sealed interface DiscountCodeAction {

    // Affects Apply button state
    data class UpdateDiscountCode(val code: String) : DiscountCodeAction

    // Triggers validation
    data class ApplyDiscountCode(val code: String) : DiscountCodeAction

    // Displays success message and changes the button text to Continue
    data class DiscountAppliedSuccess(
        val promoCode: String,
        val availabilityState: HotelBookingAvailabilityState
    ) : DiscountCodeAction

    // Navigate back to HDP with promotional rates
    object Continue : DiscountCodeAction

    // Remove discount and revert to standard rates
    data class RemoveDiscountCode(val discountCode: String) : DiscountCodeAction

    // Error Handling
    sealed class DiscountAppliedError : DiscountCodeAction {

        enum class ErrorType {
            INVALID, // Code is directly invalid
            MULTIPLE_REDEEM, // Code is applied successfully, but user tries to use another code to override
            CODE_EXPIRED, // Code is valid but the date is expired
            UNAVAILABLE, // Code is valid and the date is correct but other restrictions apply i.e minimum stay 2-3 nights etc.
            CODE_ALREADY_APPLIED, // Code is been used to make a another booking already
            GENERAL // Use it for network issues or unknown errors
        }
    }

    object ClearError : DiscountCodeAction

    object Close : DiscountCodeAction
}