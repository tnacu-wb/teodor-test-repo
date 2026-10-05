package uk.co.whitbread.integrationtests.stubs.opera

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.HotelRoomType

const val OPERA_ROOM_TYPES_STUB_ID = "booking.opera.room-types"

fun roomTypes(hotel: Hotel): PlannedStub = roomTypes(listOf(hotel))

fun roomTypes(hotels: List<Hotel>): PlannedStub =
    PlannedStub(
        id = OPERA_ROOM_TYPES_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = hotels.map(::roomTypesMapping),
    )

private fun roomTypesMapping(hotel: Hotel): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/rm/config/v1/hotels/${hotel.hotelId}/roomTypes",
                queryParameters =
                    mapOf(
                        "summaryInfo" to StringValuePattern(equalTo = "true"),
                        "limit" to StringValuePattern(equalTo = "50"),
                        "physical" to StringValuePattern(equalTo = "true"),
                    ),
                headers =
                    mapOf(
                        "x-hotelid" to StringValuePattern(equalTo = hotel.hotelId),
                    ),
            ),
        response =
            jsonResponse(
                body = roomTypesBody(hotel),
            ),
    )

private val responseJson = Json { prettyPrint = true }

private fun roomTypesBody(hotel: Hotel): String {
    val body =
        buildJsonObject {
            put(
                "roomTypesSummary",
                buildJsonArray {
                    add(
                        buildJsonObject {
                            put("hotelId", hotel.hotelId)
                            put(
                                "roomTypeSummary",
                                buildJsonArray {
                                    hotel.availableRoomTypes.forEach { roomType ->
                                        add(roomTypeJson(roomType))
                                    }
                                },
                            )
                        },
                    )
                },
            )
            put("totalPages", if (hotel.availableRoomTypes.isEmpty()) 0 else 1)
            put("offset", 0)
            put("limit", 50)
            put("hasMore", false)
            put("totalResults", hotel.availableRoomTypes.size)
            put("links", buildJsonArray {})
        }
    return responseJson.encodeToString(JsonObject.serializer(), body)
}

private fun roomTypeJson(roomType: HotelRoomType): JsonObject =
    buildJsonObject {
        put("roomClass", roomType.roomClass)
        put("accessible", roomType.accessible)
        put("roomType", roomType.roomType)
        put("numberOfRooms", roomType.numberOfRooms)
    }
