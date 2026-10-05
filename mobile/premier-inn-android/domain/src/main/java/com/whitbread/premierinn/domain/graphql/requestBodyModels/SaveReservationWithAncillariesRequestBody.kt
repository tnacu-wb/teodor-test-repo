package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class SaveReservationWithAncillariesRequestBody(
    val basketReferenceId: String,
    val hotelId: String,
    val arrival: String,
    val departure: String,
    val roomsSelections: List<RoomsSelections>,
    val previousRoomsSelections: List<PreviousRoomsSelections>
)

data class RoomsSelections(
    val packagesSelection: List<SelectedPackages>
)

data class PreviousRoomsSelections(
    val packagesSelection: List<SelectedPackages>
)

data class SelectedPackages(
    val id: String,
    val noOfSelections: Int
)