package uk.co.whitbread.integrationtests.stubs.opera.custom

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.opera.hotelAvailability
import uk.co.whitbread.integrationtests.testkit.model.Booking

const val CUSTOM_HOTEL_AVAILABILITY_ERROR_STUB_ID = "custom.opera.hotel-availability-error"
const val CUSTOM_HOTEL_AVAILABILITY_MISMATCHED_ROOM_TYPE_STUB_ID =
    "custom.opera.hotel-availability-mismatched-room-type"

/**
 * Answers every Booking-shaped single-hotel availability request with an Opera 500 rejection.
 *
 * Install with `booking.opera.availability` excluded.
 */
fun hotelAvailabilityFailure(booking: Booking): PlannedStub =
    hotelAvailability(booking).rejectedByOpera(
        id = CUSTOM_HOTEL_AVAILABILITY_ERROR_STUB_ID,
        detail = "hotel availability search failed",
        status = 500,
    )

/**
 * Models Opera returning priced room rates under a different room type than the requested one.
 * Every matcher and unrelated response remains identical to the generic availability capability.
 *
 * Install with `booking.opera.availability` excluded.
 */
fun hotelAvailabilityWithMismatchedRoomType(
    booking: Booking,
    requestedRoomType: String,
    reportedRoomType: String,
): PlannedStub {
    require(requestedRoomType.isNotBlank()) { "requestedRoomType must not be blank" }
    require(reportedRoomType.isNotBlank()) { "reportedRoomType must not be blank" }
    require(requestedRoomType != reportedRoomType) {
        "reportedRoomType must differ from requestedRoomType"
    }

    return hotelAvailability(booking).let { default ->
        val selectedMappings =
            default.mappings.filter { mapping ->
                mapping.request.queryParameters
                    ?.get("ratePlanCode")
                    ?.equalTo != null &&
                    mapping.request.queryParameters
                        .get("roomType")
                        ?.equalTo == requestedRoomType
            }
        require(selectedMappings.isNotEmpty()) {
            "Expected at least one rate-plan-code availability mapping for room type $requestedRoomType"
        }

        default.copy(
            id = CUSTOM_HOTEL_AVAILABILITY_MISMATCHED_ROOM_TYPE_STUB_ID,
            mappings =
                default.mappings.map { mapping ->
                    if (mapping in selectedMappings) {
                        mapping.copy(
                            response =
                                mapping.response.copy(
                                    jsonBody =
                                        requireNotNull(mapping.response.jsonBody)
                                            .withReportedRoomType(reportedRoomType),
                                    body = null,
                                ),
                        )
                    } else {
                        mapping
                    }
                },
        )
    }
}

private fun JsonElement.withReportedRoomType(reportedRoomType: String): JsonObject {
    val response = jsonObject
    val hotels =
        response.getValue("hotelAvailability").jsonArray.map { hotel ->
            val hotelObject = hotel.jsonObject
            val roomStays =
                hotelObject.getValue("roomStays").jsonArray.map { roomStay ->
                    val roomStayObject = roomStay.jsonObject
                    val roomRates =
                        roomStayObject.getValue("roomRates").jsonArray.map { roomRate ->
                            JsonObject(
                                roomRate.jsonObject +
                                    ("roomType" to JsonPrimitive(reportedRoomType)),
                            )
                        }
                    JsonObject(roomStayObject + ("roomRates" to JsonArray(roomRates)))
                }
            JsonObject(hotelObject + ("roomStays" to JsonArray(roomStays)))
        }
    return JsonObject(response + ("hotelAvailability" to JsonArray(hotels)))
}
