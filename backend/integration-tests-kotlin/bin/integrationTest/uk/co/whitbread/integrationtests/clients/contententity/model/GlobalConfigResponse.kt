package uk.co.whitbread.integrationtests.clients.contententity.model

import kotlinx.serialization.Serializable

@Serializable
data class GlobalConfigResponse(
    val maxRoomsLim: SearchRulesResponse? = null,
    val roomClassConfig: List<RoomClassOrderResponse> = emptyList(),
    val roomUpgradeOptions: RoomUpgradeOptionsResponse? = null,
    val hotelsWithCityTax: List<String> = emptyList(),
    val upsellItemsExtras: List<UpsellItemExtraResponse> = emptyList(),
)

@Serializable
data class SearchRulesResponse(
    val maxNights: Int? = null,
    val maxRooms: Int? = null,
    val maxRoomsAmend: Int? = null,
    val maxArrivalDate: Int? = null,
    val roomOccupancies: List<AcceptedRoomTypesResponse> = emptyList(),
)

@Serializable
data class AcceptedRoomTypesResponse(
    val acceptedRoomTypes: List<String> = emptyList(),
    val adultsNumber: Int? = null,
    val childrenNumber: Int? = null,
)

@Serializable
data class RoomClassOrderResponse(
    val code: String? = null,
    val order: Int? = null,
    val availableUpgrades: List<String> = emptyList(),
)

@Serializable
data class RoomUpgradeOptionsResponse(
    val roomUpgrades: List<RoomUpgradeResponse> = emptyList(),
    val priceText: String? = null,
    val primaryButtonText: String? = null,
    val secondaryButtonText: String? = null,
)

@Serializable
data class RoomUpgradeResponse(
    val roomClass: String? = null,
    val heading: String? = null,
    val description: String? = null,
    val imageUrl: String? = null,
)

@Serializable
data class UpsellItemExtraResponse(
    val promoText: String? = null,
    val promoPackageCode: String? = null,
    val packageCode: String? = null,
)
