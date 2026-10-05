package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/**
 * Request body for `PUT /ohip/v1/reservations/email` (`UpdateBookerEmailRequestDto`).
 * [reservationIds] binds into a `Set<String>` on the service, so reads and reservation writes
 * are one-per-distinct-id by construction. [emailAddress] is written once to the resolved
 * primary-guest CRM profile and restamped on every reservation's `ReservationContact` block.
 */
@Serializable
data class UpdateBookerEmailRequest(
    val hotelId: String,
    val reservationIds: List<String>,
    val emailAddress: String,
)
