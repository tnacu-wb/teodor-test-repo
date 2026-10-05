package uk.co.whitbread.integrationtests.clients.spendingentity.model

import kotlinx.serialization.Serializable

@Serializable
data class PaymentInfoResponse(
    val payments: List<Payment> = emptyList(),
    val errors: String? = null,
)

@Serializable
data class Payment(
    val paymentDate: String? = null,
    val paymentDescription: String? = null,
    val failureReason: String? = null,
    val paymentFailed: Boolean? = null,
    val paymentValue: PaymentValue? = null,
)

@Serializable
data class PaymentValue(
    val value: String? = null,
    val currencyCode: String? = null,
    val currencySymbol: String? = null,
)
