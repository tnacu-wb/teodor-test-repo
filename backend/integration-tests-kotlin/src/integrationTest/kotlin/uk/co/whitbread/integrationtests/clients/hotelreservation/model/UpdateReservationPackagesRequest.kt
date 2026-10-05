package uk.co.whitbread.integrationtests.clients.hotelreservation.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateReservationPackagesRequest(
    val basketReferenceId: String,
    val hotelId: String,
    val arrivalDate: String,
    val departureDate: String,
    val reservationsId: List<String>? = null,
    val roomsSelections: List<ReservationRoomPackageSelections>,
    val previousRoomsSelections: List<ReservationRoomPackageSelections>? = null,
)

@Serializable
data class ReservationRoomPackageSelections(
    val packagesSelection: List<ReservationPackageSelection>,
)

@Serializable
data class ReservationPackageSelection(
    val id: String,
    val noOfSelections: Int,
)
