package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

@Serializable
data class CancelReservationResponse(
    val cancellationIds: List<String>? = null,
    val refundedDeposits: Map<String, CancelReservationDepositsResponse>? = null,
)

@Serializable
data class CancelReservationDepositsResponse(
    val deposits: List<CancelReservationDeposit>? = null,
)

@Serializable
data class CancelReservationDeposit(
    val paymentReference: String? = null,
    val postedAmount: CancelReservationCurrencyAmountType? = null,
)

@Serializable
data class CancelReservationCurrencyAmountType(
    val amount: Double? = null,
    val currencyCode: String? = null,
)
