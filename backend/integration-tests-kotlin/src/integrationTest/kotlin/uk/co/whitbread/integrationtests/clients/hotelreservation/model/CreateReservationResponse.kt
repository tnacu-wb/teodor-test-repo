package uk.co.whitbread.integrationtests.clients.hotelreservation.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateReservationResponse(
    val hotelId: String,
    val basketReference: String,
    val reservations: List<CreatedReservation> = emptyList(),
    val totalCost: Double? = null,
    val currencyCode: String? = null,
)

@Serializable
data class CreatedReservation(
    val createDateTime: String? = null,
    val roomStay: CreatedReservationRoomStay? = null,
)

@Serializable
data class CreatedReservationRoomStay(
    val adultsNumber: Int? = null,
    val childrenNumber: Int? = null,
    val cot: Boolean? = null,
    val pmsRoomType: String? = null,
    val ratePlanCode: String? = null,
    val arrivalDate: String? = null,
    val departureDate: String? = null,
)
