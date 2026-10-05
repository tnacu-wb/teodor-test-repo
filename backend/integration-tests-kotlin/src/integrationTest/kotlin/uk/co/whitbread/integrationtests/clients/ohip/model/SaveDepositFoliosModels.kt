package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/** Request body for `POST /ohip/v1/reservations/deposit-folios`. */
@Serializable
data class SaveDepositFoliosRequest(
    val depositFolios: List<DepositFolioRequest>,
)

/** One deposit folio to post against one Opera reservation. */
@Serializable
data class DepositFolioRequest(
    val hotelId: String,
    val reservationId: String,
    val paymentId: String? = null,
    val defaultPaymentMethod: String? = null,
    val charges: List<DepositFolioCharge>,
)

/** One charge line inside a deposit folio. [DepositCurrencyAmount] is shared with the GET sibling. */
@Serializable
data class DepositFolioCharge(
    val transactionCode: String,
    val quantity: Int,
    val reference: String,
    val currencyAmount: DepositCurrencyAmount,
)
