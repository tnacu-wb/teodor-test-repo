package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class UpdateReservationPackagesRequestBody(
    val basketReferenceId: String,
    val hotelId: String,
    val arrivalDate: String,
    val departureDate: String,
    val roomSelections: List<AmendRoomsSelections>,
    val deletedRoomSelections: List<AmendRoomsSelections>
)
