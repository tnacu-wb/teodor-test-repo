package uk.co.whitbread.integrationtests.clients.hotelreservation.model

import kotlinx.serialization.Serializable

/** Request body for `PUT /v1/reservations/alerts`. */
@Serializable
data class UpdateReservationAlertsRequest(
    val reservationIds: Set<String>,
    val hotelId: String,
    val alerts: List<ReservationAlert>,
)

/** One Opera reservation alert forwarded unchanged through HRE and ohip-adapter-service. */
@Serializable
data class ReservationAlert(
    val id: String,
    val area: String,
    val code: String,
    val description: String,
    val screenNotification: Boolean,
    val printerNotification: Boolean,
)
