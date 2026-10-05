package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/**
 * The aggregated amend summary from `GET /ohip/v1/reservations/amend/getDetailsForAmend`.
 *
 * The three scalars are sums across every requested reservation id, while `deposit` and `guestPay`
 * are keyed by reservation id. The money fields are `BigDecimal` server-side and are kept as
 * `Double` here so scenarios compare values rather than wire scales; a deposit value can be
 * negative (Opera's own sign, which this endpoint does not flip) or absent.
 */
@Serializable
data class AmendSummaryResponse(
    val net: Double? = null,
    val deposit: Map<String, Double?>? = null,
    val totalCostOfStay: Double? = null,
    val outStandingCostOfStay: Double? = null,
    val guestPay: Map<String, Double?>? = null,
)
