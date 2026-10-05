package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/**
 * Request body for `PUT /ohip/v1/reservations/alerts`
 * (`UpdateReservationAlertsRequestDto`). [reservationIds] binds to a `Set`, so duplicate entries
 * collapse and each distinct id drives its own Opera reservation update; the same [alerts] content
 * is written to every one of them under [hotelId].
 */
@Serializable
data class UpdateReservationAlertsRequest(
    val reservationIds: Set<String>,
    val hotelId: String,
    val alerts: List<ReservationAlertRequest>,
)

/**
 * One requested Opera reservation alert (`AlertDto`). Every field is copied straight onto the
 * Opera `AlertType`, except [area], which the service resolves by `AlertAreaType` constant name —
 * so `CHECKIN` here becomes the wire value `CheckIn` in the Opera body.
 */
@Serializable
data class ReservationAlertRequest(
    val id: String,
    val area: String,
    val code: String,
    val description: String,
    val screenNotification: Boolean,
    val printerNotification: Boolean,
)
