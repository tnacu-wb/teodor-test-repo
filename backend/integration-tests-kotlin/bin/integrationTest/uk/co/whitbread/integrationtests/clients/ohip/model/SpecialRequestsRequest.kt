package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/** Request body for `PUT /ohip/v1/reservations/special-requests` (`SpecialRequestsDto`). */
@Serializable
data class SpecialRequestsRequest(
    val hotelId: String,
    val reservationIds: List<String>,
    val specialRequests: List<String>,
    val bookingNotes: List<String> = emptyList(),
)
