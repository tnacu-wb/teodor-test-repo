package uk.co.whitbread.integrationtests.stubs.aem

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.testkit.model.AemRoomType
import uk.co.whitbread.integrationtests.testkit.model.AemRoomTypeInformation

const val AEM_ROOM_TYPE_STUB_ID = "booking.aem.room-type"

fun roomType(roomType: AemRoomType): PlannedStub =
    PlannedStub(
        id = AEM_ROOM_TYPE_STUB_ID,
        target = WireMockTarget.AEM,
        mappings = listOf(roomTypeMapping(roomType)),
    )

private fun roomTypeMapping(roomType: AemRoomType): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                url =
                    "/${roomType.country}/${roomType.language}" +
                        "/content-service.room-types.detail/brand/${roomType.brand.lowercase()}.json",
            ),
        response =
            jsonResponse(
                body = roomTypeBody(roomType),
            ),
    )

private fun roomTypeBody(roomType: AemRoomType): String {
    val body =
        mapOf(
            "roomTypes" to roomType.roomTypes.map(::roomTypeInformationBody),
        )

    return Json.encodeToString(JsonElement.serializer(), toJsonElement(body))
}

private fun roomTypeInformationBody(roomTypeInformation: AemRoomTypeInformation): Map<String, Any?> =
    mapOf(
        "roomTypeCode" to roomTypeInformation.roomTypeCode,
        "roomCategory" to roomTypeInformation.roomCategory,
        "roomLabel" to roomTypeInformation.roomLabel,
        "roomDescription" to roomTypeInformation.roomDescription,
        "roomInfoLabel" to roomTypeInformation.roomInfoLabel,
        "roomInfo" to roomTypeInformation.roomInfo,
        "gridImage" to roomTypeInformation.gridImage,
        "roomImage" to roomTypeInformation.roomImage,
        "facilities" to roomTypeInformation.facilities,
        "groupId" to roomTypeInformation.groupId,
        "substitutionMessage" to roomTypeInformation.substitutionMessage,
    )
