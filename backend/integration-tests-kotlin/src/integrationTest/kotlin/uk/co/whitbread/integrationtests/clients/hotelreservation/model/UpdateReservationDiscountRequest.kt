package uk.co.whitbread.integrationtests.clients.hotelreservation.model

import kotlinx.serialization.Serializable

/** Request body for `PUT /v1/reservations/discount`. */
@Serializable
data class UpdateReservationDiscountRequest(
    val discountAmount: Double,
    val currency: String,
    val hotelId: String,
    val reservationIds: List<String>,
)
