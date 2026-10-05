package uk.co.whitbread.integrationtests.clients.hotelreservation.model

import kotlinx.serialization.Serializable

/** Request body for `PUT /v1/reservations/special-requests`. */
@Serializable
data class UpdateReservationSpecialRequestsRequest(
    val reservationIds: List<String>,
    val hotelId: String,
    val specialRequests: List<String>,
    val bookingNotes: List<String>? = null,
)
