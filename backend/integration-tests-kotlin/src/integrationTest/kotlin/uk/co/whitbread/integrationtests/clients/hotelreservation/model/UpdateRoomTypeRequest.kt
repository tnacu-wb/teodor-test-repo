package uk.co.whitbread.integrationtests.clients.hotelreservation.model

import kotlinx.serialization.Serializable

/** Request body for `PUT /v1/reservations/roomTypeUpdate`. */
@Serializable
data class UpdateRoomTypeRequest(
    val basketReferenceId: String,
    val reservationIds: List<String>,
    val hotelId: String,
    val rateCode: String,
    val roomTypes: List<String>,
    val startDate: String,
    val endDate: String,
    val currency: String,
    val adultsNumber: List<Int>,
    val childrenNumber: List<Int>,
)

/** Basket reference returned after the room-type update completes. */
@Serializable
data class UpdateRoomTypeResponse(
    val basketReference: String,
)
