package com.whitbread.premierinn.threeCp

import com.whitbread.premierinn.common.BookingFlowInput
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.payment.entity.ThreeCPaymentServiceResponseEntity
import com.whitbread.premierinn.reviewbooking.ReviewBookingInput
import java.nio.charset.StandardCharsets

fun ThreeCPaymentServiceResponseEntity.toThreeCpInput(
    reviewBookingInput: ReviewBookingInput,
    bookingFlowInput: BookingFlowInput,
    isCustomerLoggedIn: Boolean,
    isBusinessCustomerLoggedIn: Boolean): ThreeCpInput {
    return ThreeCpInput(
        sessionID = this.providerResponse.threeCResponse.sessionId,
        template = this.providerResponse.threeCResponse.template,
        iPageHtml = this.providerResponse.threeCResponse.iPageHtml,
        iPageSessionIdForGPay = this.providerResponse.threeCResponse.iPageSessionIdForGPay ?: EMPTY_STRING_DOMAIN,
        providerUrl = this.providerResponse.threeCResponse.providerUrl,
        bookingFlowInput = bookingFlowInput,
        reviewBookingInput = reviewBookingInput,
        isCustomerLoggedIn = isCustomerLoggedIn,
        isBusinessCustomerLoggedIn = isBusinessCustomerLoggedIn
    )
}

fun String.fromBase64(): String{
    return String(android.util.Base64.decode(this, android.util.Base64.DEFAULT), StandardCharsets.UTF_8)
}


