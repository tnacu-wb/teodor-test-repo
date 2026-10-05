package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/**
 * Request body for `PUT /ohip/v1/reservations/overrideReasons`
 * (`UpdateReservationOverrideReasonsRequestDto`). The ids are sent as a raw JSON array so a
 * repeated id reaches the service, whose `Set<String>` binding collapses it before the fan-out.
 * The reason fields are comma-joined into character UDF `UDFC08`, with a non-blank
 * [managerName] appended as the optional fourth component.
 */
@Serializable
data class UpdateReservationOverrideReasonsRequest(
    val hotelId: String,
    val reservationIds: List<String>,
    val reasonCode: String,
    val reasonName: String,
    val callerName: String,
    val managerName: String? = null,
)
