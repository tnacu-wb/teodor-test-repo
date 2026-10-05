package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

@Serializable
data class HotelRoomTypesResponse(
    val hotelId: String? = null,
    val roomType: List<HotelRoomTypeResponse> = emptyList(),
)

@Serializable
data class HotelRoomTypeResponse(
    val roomClass: String? = null,
    val accessible: Boolean? = null,
    val roomType: String? = null,
    val numberOfRooms: String? = null,
)
