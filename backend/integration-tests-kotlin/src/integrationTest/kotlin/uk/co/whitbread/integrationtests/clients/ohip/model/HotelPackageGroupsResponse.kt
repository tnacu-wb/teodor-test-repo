package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

@Serializable
data class HotelPackageGroupsRequest(
    val hotelId: String,
    val packageGroupList: List<String>? = null,
    val packageCodeList: List<HotelPackageCodesRequest>? = null,
)

@Serializable
data class HotelPackageCodesRequest(
    val packageCodes: List<String> = emptyList(),
)

@Serializable
data class HotelPackageGroupsResponse(
    val packagesGroup: List<HotelPackageGroup> = emptyList(),
)

@Serializable
data class HotelPackageGroup(
    val packageGroup: String? = null,
    val packageGroupDescription: String? = null,
    val packageCodes: List<HotelPackageGroupCode> = emptyList(),
)

@Serializable
data class HotelPackageGroupCode(
    val packageCode: String? = null,
    val packageDescription: String? = null,
)
