package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/**
 * Unlike the singular cancel endpoint's `yyyy-MM-dd` deadline, `absoluteDeadline` here must be an
 * ISO zoned date-time (e.g. `2026-08-31T12:00:00Z`): the service parses it synchronously with
 * `DateTimeFormatter.ISO_ZONED_DATE_TIME` before scheduling the background Opera rewrite.
 */
@Serializable
data class UpdateCancellationPoliciesRequest(
    val hotelId: String,
    val reservationIds: List<String>,
    val absoluteDeadline: String,
)
