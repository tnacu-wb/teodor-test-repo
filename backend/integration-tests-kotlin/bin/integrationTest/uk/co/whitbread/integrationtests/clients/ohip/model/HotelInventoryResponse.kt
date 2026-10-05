package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

@Serializable
data class HotelInventoryResponse(
    val roomTypeInventories: List<RoomLevelInventoryResponse> = emptyList(),
)

@Serializable
data class RoomLevelInventoryResponse(
    val availableCount: Int? = null,
    val code: String? = null,
)
