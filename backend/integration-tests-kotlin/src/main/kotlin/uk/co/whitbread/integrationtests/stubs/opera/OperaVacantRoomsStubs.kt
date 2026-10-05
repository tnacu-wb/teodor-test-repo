package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.HotelPhysicalRoom

const val OPERA_VACANT_ROOMS_STUB_ID = "booking.opera.vacant-rooms"

/**
 * Models Opera Front Office's clean-vacant room lookup over a hotel's physical-room inventory.
 *
 * For every room type present in [Hotel.physicalRooms], Opera answers the front-office rooms
 * query — filtered to that type with `hotelRoomStatus=Clean`, `hotelFORoomStatus=Vacant`,
 * `includeAllRoomConditions=true`, and a page size of 60, exactly as the real caller sends —
 * with the rooms of that type currently Clean and Vacant. A room type whose rooms are all
 * dirty or occupied returns an empty room list, mirroring a hotel with nothing to allocate.
 */
fun vacantRooms(hotels: List<Hotel>): PlannedStub =
    PlannedStub(
        id = OPERA_VACANT_ROOMS_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            hotels.flatMap { hotel ->
                hotel.physicalRooms
                    .map { room -> room.roomType }
                    .distinct()
                    .map { roomType -> vacantRoomsMapping(hotel, roomType) }
            },
    )

private fun vacantRoomsMapping(
    hotel: Hotel,
    roomType: String,
): StubMapping {
    val cleanVacantRooms =
        hotel.physicalRooms.filter { room ->
            room.roomType == roomType &&
                room.housekeepingStatus == "Clean" &&
                room.frontOfficeStatus == "Vacant"
        }

    return StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/fof/v1/hotels/${hotel.hotelId}/rooms",
                queryParameters =
                    mapOf(
                        "roomType" to StringValuePattern(equalTo = roomType),
                        "hotelRoomStatus" to StringValuePattern(equalTo = "Clean"),
                        "hotelFORoomStatus" to StringValuePattern(equalTo = "Vacant"),
                        "includeAllRoomConditions" to StringValuePattern(equalTo = "true"),
                        "limit" to StringValuePattern(equalTo = "60"),
                    ),
                headers = hotelHeaders(hotel.hotelId),
            ),
        response =
            jsonResponse(
                jsonBody =
                    stubJsonObject(
                        "hotelRoomsDetails" to
                            mapOf(
                                "hotelId" to hotel.hotelId,
                                "room" to cleanVacantRooms.map { room -> vacantRoomJson(room) },
                            ),
                        "totalResults" to cleanVacantRooms.size,
                        "hasMore" to false,
                        "limit" to 60,
                        "offset" to 0,
                        "totalPages" to 1,
                    ),
            ),
    )
}

private fun vacantRoomJson(room: HotelPhysicalRoom): Map<String, Any?> =
    buildMap {
        put("roomId", room.roomId)
        put(
            "roomType",
            mapOf(
                "roomType" to room.roomType,
                "pseudo" to false,
            ),
        )
        room.floor?.let { floor -> put("floor", floor) }
        put(
            "housekeeping",
            mapOf(
                "roomStatus" to
                    mapOf(
                        "roomStatus" to room.housekeepingStatus,
                        "frontOfficeStatus" to room.frontOfficeStatus,
                    ),
            ),
        )
    }
