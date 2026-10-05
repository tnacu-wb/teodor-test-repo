package com.whitbread.premierinn.domain.graphql.reviewBooking.entity

data class InitiatePaymentDomain (
    val status: String?,
    val paymentRequiredDetails: PaymentRequiredDetailsDomain?
)

data class PaymentRequiredDetailsDomain (
    val iPageHtml: String?,
    val sessionId: String?,
    val providerUrl: String
)