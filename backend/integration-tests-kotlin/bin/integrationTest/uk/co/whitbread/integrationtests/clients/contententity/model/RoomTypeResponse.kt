package uk.co.whitbread.integrationtests.clients.contententity.model

import kotlinx.serialization.Serializable

@Serializable
data class RoomTypeResponse(
    val roomTypes: List<RoomTypeInformationResponse> = emptyList(),
)

@Serializable
data class RoomTypeInformationResponse(
    val roomTypeCode: List<String> = emptyList(),
    val roomCategory: String? = null,
    val roomLabel: String? = null,
    val roomDescription: String? = null,
    val roomInfoLabel: String? = null,
    val roomInfo: String? = null,
    val gridImage: String? = null,
    val roomImage: String? = null,
    val groupId: String? = null,
    val facilities: List<String> = emptyList(),
)
