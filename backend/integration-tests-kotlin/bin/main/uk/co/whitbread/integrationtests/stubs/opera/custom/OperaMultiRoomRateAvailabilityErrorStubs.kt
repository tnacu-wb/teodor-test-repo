package uk.co.whitbread.integrationtests.stubs.opera.custom

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonPathStringLiteral
import uk.co.whitbread.integrationtests.stubs.opera.multiRoomRateAvailability
import uk.co.whitbread.integrationtests.testkit.model.Booking

const val CUSTOM_MULTI_ROOM_RATE_AVAILABILITY_ERROR_STUB_ID = "custom.opera.multi-room-rate-availability-error"
const val CUSTOM_MULTI_ROOM_RATE_AVAILABILITY_EMPTY_ROOM_STUB_ID =
    "custom.opera.multi-room-rate-availability-empty-room"

/**
 * Answers every Booking-shaped multi-room-rate availability search with an Opera 500 rejection.
 *
 * Install with `booking.opera.multi-room-rate-availability` excluded.
 */
fun multiRoomRateAvailabilityFailure(booking: Booking): PlannedStub =
    multiRoomRateAvailability(booking).rejectedByOpera(
        id = CUSTOM_MULTI_ROOM_RATE_AVAILABILITY_ERROR_STUB_ID,
        detail = "multi room rate availability search failed",
        status = 500,
    )

/**
 * Models Opera reporting no availability for one PMS room type while every other room type of
 * the Booking still answers normally. It removes the selected type from every response that
 * contains it. It removes room stays and hotels that have no remaining available room type.
 *
 * Install with `booking.opera.multi-room-rate-availability` excluded.
 */
fun multiRoomRateAvailabilityEmptyForRoomType(
    booking: Booking,
    pmsRoomType: String,
): PlannedStub =
    multiRoomRateAvailability(booking).let { default ->
        val roomTagMatcher = "$.rooms[?(@.tag == ${jsonPathStringLiteral(pmsRoomType)})]"
        val selected =
            default.mappings.filter { mapping ->
                mapping.request.bodyPatterns.orEmpty().any { pattern ->
                    pattern.matchesJsonPath == roomTagMatcher
                }
            }
        require(selected.isNotEmpty()) {
            "Expected at least one mapping for PMS room type $pmsRoomType"
        }

        default.copy(
            id = CUSTOM_MULTI_ROOM_RATE_AVAILABILITY_EMPTY_ROOM_STUB_ID,
            mappings =
                default.mappings.map { mapping ->
                    if (mapping in selected) {
                        mapping.copy(
                            response =
                                mapping.response.copy(
                                    jsonBody = requireNotNull(mapping.response.jsonBody).withoutRoomType(pmsRoomType),
                                    body = null,
                                ),
                        )
                    } else {
                        mapping
                    }
                },
        )
    }

private fun JsonElement.withoutRoomType(pmsRoomType: String): JsonObject {
    val response = jsonObject
    val availableHotels =
        response
            .getValue("hotelAvailability")
            .jsonArray
            .mapNotNull { hotel -> hotel.hotelWithoutRoomTypeOrNull(pmsRoomType) }
    return JsonObject(response + ("hotelAvailability" to JsonArray(availableHotels)))
}

private fun JsonElement.hotelWithoutRoomTypeOrNull(pmsRoomType: String): JsonObject? {
    val hotel = jsonObject
    val availableRoomStays =
        hotel
            .getValue("roomStays")
            .jsonArray
            .mapNotNull { roomStay -> roomStay.roomStayWithoutRoomTypeOrNull(pmsRoomType) }
    if (availableRoomStays.isEmpty()) return null

    return JsonObject(hotel + ("roomStays" to JsonArray(availableRoomStays)))
}

private fun JsonElement.roomStayWithoutRoomTypeOrNull(pmsRoomType: String): JsonObject? {
    val roomStay = jsonObject
    val availableRoomTypes =
        roomStay
            .getValue("roomTypes")
            .jsonArray
            .filter { roomType ->
                roomType.jsonObject
                    .getValue("roomType")
                    .jsonPrimitive.content != pmsRoomType
            }
    if (availableRoomTypes.isEmpty()) return null

    return JsonObject(roomStay + ("roomTypes" to JsonArray(availableRoomTypes)))
}
