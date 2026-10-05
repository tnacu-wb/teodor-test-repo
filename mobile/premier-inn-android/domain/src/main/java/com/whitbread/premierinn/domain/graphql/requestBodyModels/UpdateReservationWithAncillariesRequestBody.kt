package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class UpdateReservationWithAncillariesRequestBody(
    val basketReferenceId: String,
    val hotelId: String,
    val arrival: String,
    val departure: String,
    val roomsSelections: List<AmendRoomsSelections>,
    val previousRoomsSelections: List<AmendRoomsSelections>
)

data class AmendRoomsSelections(
    val reservationId: String,
    val packagesSelection: MutableList<AmendSelectedPackages>
)

data class AmendSelectedPackages(
    val id: String,
    var noOfSelections: Int
)