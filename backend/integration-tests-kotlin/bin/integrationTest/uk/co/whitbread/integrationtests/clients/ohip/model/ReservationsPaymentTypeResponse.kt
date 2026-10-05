package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/** One entry of the bare JSON array returned by `GET /ohip/v1/reservations/paymentType`. */
@Serializable
data class ReservationPaymentTypeEntry(
    val ids: List<PaymentTypeReservationId>? = null,
    val paymentCardType: PaymentCardType? = null,
)

@Serializable
data class PaymentTypeReservationId(
    val id: String? = null,
    val type: String? = null,
)

@Serializable
data class PaymentCardType(
    val cardType: String? = null,
    val userDefinedCardType: String? = null,
    val token: String? = null,
    val cardNumberMasked: String? = null,
    val cardNumberLast4Digits: String? = null,
    val expirationDate: String? = null,
    val cardHolderName: String? = null,
)
