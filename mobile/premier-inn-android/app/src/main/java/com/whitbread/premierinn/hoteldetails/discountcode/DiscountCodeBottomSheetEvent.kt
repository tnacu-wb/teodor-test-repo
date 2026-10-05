package com.whitbread.premierinn.hoteldetails.discountcode

import com.whitbread.premierinn.domain.hotel.entity.HotelBookingAvailabilityState

sealed interface DiscountCodeBottomSheetEvent {

    data class UpdateHotelDetailsRates(
        val promoCode: String,
        val availabilityState: HotelBookingAvailabilityState,
        val successMessage: String?,
        val promoKind: String? = null
    ) : DiscountCodeBottomSheetEvent

    data class DiscountCodeError(
        val promoCode: String,
        val errorMessage: String?,
        val promoKind: String?
    ) : DiscountCodeBottomSheetEvent

    object DiscountCodeBottomSheetClosed : DiscountCodeBottomSheetEvent
}