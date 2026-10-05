package uk.co.whitbread.integrationtests.clients.hotelreservation.model

import kotlinx.serialization.Serializable

/** Request body for `POST /v1/reservations/save-deposit-folios`. */
@Serializable
data class SaveDepositFoliosRequest(
    val depositFolios: List<DepositFolioRequest>,
)

/**
 * One deposit folio to post against one Opera reservation. Each folio carries its own
 * `hotelId`/`reservationId`, so a batch may span hotels and reservations.
 */
@Serializable
data class DepositFolioRequest(
    val hotelId: String,
    val reservationId: String,
    val paymentId: String? = null,
    val defaultPaymentMethod: String? = null,
    val charges: List<DepositFolioCharge>,
)

/**
 * One charge line inside a deposit folio. [DepositCurrencyAmount] is shared with the deposits
 * read models in this package.
 */
@Serializable
data class DepositFolioCharge(
    val transactionCode: String,
    val quantity: Int,
    val reference: String,
    val currencyAmount: DepositCurrencyAmount,
)
