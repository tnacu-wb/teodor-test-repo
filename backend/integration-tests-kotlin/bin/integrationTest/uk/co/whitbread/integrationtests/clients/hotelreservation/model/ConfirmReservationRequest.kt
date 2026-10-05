package uk.co.whitbread.integrationtests.clients.hotelreservation.model

import kotlinx.serialization.Serializable

@Serializable
data class ConfirmReservationRequest(
    val reservationId: String,
    val hotelId: String,
    val paymentOption: ConfirmReservationPaymentOption,
    val paymentMethod: String? = null,
    val paymentType: String? = null,
    val paymentCard: ConfirmReservationPaymentCard? = null,
    val paymentId: String? = null,
    val pibaCardPresent: Boolean? = null,
    val ccAgentId: String? = null,
    val threeDSIndicator: String? = null,
)

@Serializable
enum class ConfirmReservationPaymentOption {
    PAY_NOW,
    PAY_ON_ARRIVAL,
    RESERVE_WITHOUT_CARD,
    ACCOUNT_COMPANY,
}

@Serializable
data class ConfirmReservationPaymentCard(
    val cardType: String? = null,
    val token: String? = null,
    val expirationDate: String? = null,
    val cardHolderName: String? = null,
    val cardNumberLast4Digits: String? = null,
    val citId: String? = null,
)
