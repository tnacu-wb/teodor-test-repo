package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class PaymentMethodsRequestBody @JvmOverloads constructor(
    val basketReference: String,
    val language: String,
    val country: String,
    val clientChannel: String,
    val userType: String,
    val flowType: PaymentMethodsFlow? = null
)

enum class PaymentMethodsFlow {
    CheckInOnline
}