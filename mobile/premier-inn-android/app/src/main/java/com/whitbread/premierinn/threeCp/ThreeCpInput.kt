package com.whitbread.premierinn.threeCp

import android.os.Parcelable
import com.whitbread.premierinn.common.BookingFlowInput
import com.whitbread.premierinn.reviewbooking.ReviewBookingInput
import kotlinx.parcelize.Parcelize

@Parcelize
data class ThreeCpInput(
    val sessionID: String,
    val template: String,
    val iPageHtml: String,
    val iPageSessionIdForGPay: String,
    val providerUrl: String,
    val bookingFlowInput: BookingFlowInput,
    val reviewBookingInput: ReviewBookingInput,
    val isCustomerLoggedIn: Boolean,
    val isBusinessCustomerLoggedIn: Boolean
) : Parcelable

@Parcelize
data class ThreeCpGooglePayInput(
    val iPageSessionIdForGPay: String,
    val providerUrl: String,
) : Parcelable

@Parcelize
data class ThreeCpInputAmend(val iPageHtml: String) : Parcelable

@Parcelize
data class ThreeCpInputSaveCard(val iPageHtml: String) : Parcelable

@Parcelize
data class ThreeCpInputPayAndCheckIn(val iPageHtml: String) : Parcelable

@Parcelize
data class ThreeCpInputAuthorizeCard(val iPageHtml: String) : Parcelable