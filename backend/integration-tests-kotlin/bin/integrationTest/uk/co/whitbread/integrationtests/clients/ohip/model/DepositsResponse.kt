package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

@Serializable
data class DepositsResponse(
    val deposits: List<ReservationDeposit> = emptyList(),
)

@Serializable
data class ReservationDeposit(
    val paymentReference: String? = null,
    val postedAmount: DepositCurrencyAmount? = null,
)

@Serializable
data class DepositCurrencyAmount(
    val amount: Double? = null,
    val currencyCode: String? = null,
)
