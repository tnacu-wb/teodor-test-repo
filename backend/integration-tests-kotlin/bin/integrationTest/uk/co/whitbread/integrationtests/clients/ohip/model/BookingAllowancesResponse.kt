package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/**
 * Response of `GET /ohip/v1/reservations/bookingAllowances`: the business allowances the
 * reservation's first routing-instruction folio resolves to, plus the text of its
 * `BUSINESS NOTES` comment when it carries one.
 */
@Serializable
data class BookingAllowancesResponse(
    val bookingAllowances: List<BookingAllowanceEntry> = emptyList(),
    val businessNotes: String? = null,
)

/** One resolved allowance and the credit limit of the routing instruction that carried it. */
@Serializable
data class BookingAllowanceEntry(
    val allowance: String? = null,
    val budget: Double? = null,
)
