package com.whitbread.premierinn.domain.payment.entity

data class ThreeCPaymentServiceResponseEntity (
        val paymentId: String,
        val providerResponse: ProviderResponse,
        val revisedSolution: Boolean
)

data class ProviderResponse(
        val threeCResponse: ThreeCResponse
)

data class ThreeCResponse(
    val sessionId: String,
    val template: String,
    val iPageHtml: String,
    val iPageSessionIdForGPay: String?,
    val providerUrl:String
)