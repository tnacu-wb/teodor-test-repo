package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/**
 * Request body for `PUT /ohip/v1/reservations/customReferenceNumber`
 * (`UpdateCustomReferenceNumberRequestDto`). The ids bind to a `Set<String>` server-side, so
 * duplicates collapse before the fan-out, and the one custom reference is applied to every
 * requested reservation.
 */
@Serializable
data class UpdateCustomReferenceNumberRequest(
    val hotelId: String,
    val reservationIds: List<String>,
    val customReferenceNumber: String,
)
