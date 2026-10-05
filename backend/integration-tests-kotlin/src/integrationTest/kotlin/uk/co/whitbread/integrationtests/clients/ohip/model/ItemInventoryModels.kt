package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable
import java.time.LocalDate

data class ItemInventoryRequest(
    val hotelId: String,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val itemCodes: List<String>? = null,
)

@Serializable
data class ItemInventoryResponse(
    val itemsInventory: List<ItemInventory> = emptyList(),
)

@Serializable
data class ItemInventory(
    val description: String? = null,
    val code: String? = null,
    val name: String? = null,
    val inventories: List<ItemInventoryAvailability> = emptyList(),
)

@Serializable
data class ItemInventoryAvailability(
    val date: String? = null,
    val total: Int? = null,
    val available: Int? = null,
)
