package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/**
 * Request body for `PUT /ohip/v1/reservations/ccAgentId`
 * (`UpdateReservationCcAgentIdRequestDto`). The ids are sent as a raw JSON array; the service
 * binds them into a `Set<String>` that keeps the array order. [ccAgentId] becomes the value of
 * character UDF `UDFC08`, and [clearFirst] — absent by default, which the service reads as
 * false — asks for a completed clearing pass before the value pass.
 */
@Serializable
data class UpdateReservationCcAgentIdRequest(
    val hotelId: String,
    val reservationIds: List<String>,
    val ccAgentId: String,
    val clearFirst: Boolean? = null,
)
