package uk.co.whitbread.integrationtests.clients.hotelreservation.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateReservationPackagesResponse(
    val basketReference: String,
)
