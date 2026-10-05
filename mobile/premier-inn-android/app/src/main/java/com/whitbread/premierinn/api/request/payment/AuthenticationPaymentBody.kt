package com.whitbread.premierinn.api.request.payment

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.api.request.booking.PaymentCard
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.WILDCARD_CHARACTER
import com.whitbread.premierinn.reviewbooking.ReviewBookingInput

data class AuthenticationPaymentBody(@SerializedName("redirectUrl") val redirectUrl: String,
                                     @SerializedName("paymentCard") val paymentCard: PaymentCard,
                                     @SerializedName("device") val device: Device,
                                     @SerializedName("environment") val environment: String,
                                     @SerializedName("prepaymentRequired") val prepaymentRequired: Boolean,
                                     @SerializedName("useExistingCard") val useExistingCard: Boolean,
                                     @SerializedName("cardSecurityCode") val cardSecurityCode: String,
                                     @SerializedName("cardType") val cardType: String,
                                     @SerializedName("cardNumber") val cardNumber: String)

data class Device(@SerializedName("colourDepth") val colourDepth: String,
                  @SerializedName("javaEnabled") val javaEnabled: Boolean,
                  @SerializedName("language") val language: String,
                  @SerializedName("screenHeight") val screenHeight: Int,
                  @SerializedName("screenWidth") val screenWidth: Int,
                  @SerializedName("timeZone") val timeZone: String,
                  @SerializedName("windowSize") val windowSize: String)

fun createAuthenticationPaymentBody(
        reviewBookingInput: ReviewBookingInput,
        redirectUrl: String,
        device: Device): AuthenticationPaymentBody {
    val prepaymentRequired = true
    val paymentCard = PaymentCard.create(reviewBookingInput, prepaymentRequired)

    return AuthenticationPaymentBody(
            redirectUrl = redirectUrl,
            paymentCard = paymentCard,
            device = device,
            environment = WILDCARD_CHARACTER,
            prepaymentRequired = prepaymentRequired,
            useExistingCard = paymentCard.useExistingCard(),
            cardSecurityCode = paymentCard.cardSecurityCode() ?: EMPTY_STRING,
            cardType = paymentCard.cardType() ?: EMPTY_STRING,
            cardNumber = paymentCard.cardNumber() ?: EMPTY_STRING)
}