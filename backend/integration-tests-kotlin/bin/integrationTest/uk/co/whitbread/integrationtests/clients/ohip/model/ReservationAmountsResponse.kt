package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/**
 * The aggregated money summary from `GET /ohip/v1/reservations/amounts`.
 *
 * One response covers the whole requested reservation-id set. The money fields are `BigDecimal`
 * server-side and are kept as `Double` here so scenarios compare values rather than wire scales.
 * `discount` is never populated on this path.
 */
@Serializable
data class ReservationAmountsResponse(
    val currencyCode: String? = null,
    val gross: Double? = null,
    val net: Double? = null,
    val deposit: Double? = null,
    val totalCostOfStay: Double? = null,
    val outStandingCostOfStay: Double? = null,
    val discount: Double? = null,
)
