package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

@Serializable
data class CancelReservationRequest(
    val hotelId: String,
    val reservationIds: List<String>,
    val paymentOption: CancelReservationPaymentOption? = null,
    val defaultPaymentMethod: String? = null,
    val digitalPaymentMethod: String? = null,
    val reservationOverrideReason: CancelReservationOverrideReason? = null,
    val chargesByReservationIds: Map<String, List<CancelReservationDepositFolioCharge>>? = null,
)

@Serializable
enum class CancelReservationPaymentOption {
    PAY_NOW,
    PAY_ON_ARRIVAL,
    RESERVE_WITHOUT_CARD,
    ACCOUNT_COMPANY,
}

@Serializable
data class CancelReservationOverrideReason(
    val reasonCode: String,
    val reasonName: String,
    val callerName: String,
    val managerName: String? = null,
)

@Serializable
data class CancelReservationDepositFolioCharge(
    val transactionCode: String,
    val quantity: Int,
    val reference: String,
    val currencyAmount: CancelReservationCurrencyAmount,
)

@Serializable
data class CancelReservationCurrencyAmount(
    val amount: Double,
    val currencyCode: String,
)
