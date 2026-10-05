package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/**
 * Request body for `POST /ohip/v1/reservations/profiles`
 * (`AttachReservationProfileRequestDto`). [reservationIds] is sent as a raw JSON array so a
 * duplicate id can travel on the wire; the service binds it into a `Set<String>` that collapses
 * duplicates before the attach fan-out. [profileId] is only the Opera CRM read's path segment —
 * the attached id is the first entry of Opera's returned `profileIdList`.
 */
@Serializable
data class AttachReservationProfileRequest(
    val reservationIds: List<String>,
    val hotelId: String,
    val profileId: String,
)
