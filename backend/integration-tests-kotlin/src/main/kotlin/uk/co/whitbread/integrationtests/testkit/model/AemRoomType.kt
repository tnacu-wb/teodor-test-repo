package uk.co.whitbread.integrationtests.testkit.model

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

@Serializable
@JsonSchema.Description("Room-type display content the mocked AEM room-type endpoint serves. Presence gates the room-type stub.")
data class AemRoomType(
    val country: String,
    val language: String,
    val brand: String,
    val roomTypes: List<AemRoomTypeInformation>,
) {
    init {
        require(country.isNotBlank()) { "roomType country must not be blank" }
        require(language.isNotBlank()) { "roomType language must not be blank" }
        require(brand.isNotBlank()) { "roomType brand must not be blank" }
        require(roomTypes.isNotEmpty()) { "roomType.roomTypes must not be empty" }
    }
}

@Serializable
@JsonSchema.Description("Display content for one room type: labels, descriptions, images, and facilities.")
data class AemRoomTypeInformation(
    val roomTypeCode: String,
    val roomCategory: String,
    val roomLabel: String,
    val roomDescription: String,
    val roomInfoLabel: String = "",
    val roomInfo: String = "",
    val gridImage: String,
    val roomImage: String,
    val facilities: List<String> = emptyList(),
    val groupId: String,
    val substitutionMessage: String = "",
) {
    init {
        require(roomTypeCode.isNotBlank()) { "roomType.roomTypes.roomTypeCode must not be blank" }
        require(roomCategory.isNotBlank()) { "roomType.roomTypes.roomCategory must not be blank" }
        require(roomLabel.isNotBlank()) { "roomType.roomTypes.roomLabel must not be blank" }
        require(roomDescription.isNotBlank()) { "roomType.roomTypes.roomDescription must not be blank" }
        require(gridImage.isNotBlank()) { "roomType.roomTypes.gridImage must not be blank" }
        require(roomImage.isNotBlank()) { "roomType.roomTypes.roomImage must not be blank" }
        require(groupId.isNotBlank()) { "roomType.roomTypes.groupId must not be blank" }
    }
}
