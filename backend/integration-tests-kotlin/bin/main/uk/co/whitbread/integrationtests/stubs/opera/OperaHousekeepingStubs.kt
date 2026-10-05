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

const val OPERA_HOUSEKEEPING_OVERVIEW_STUB_ID = "booking.opera.housekeeping-overview"

/**
 * Models Opera Housekeeping's per-room overview over a hotel's physical-room inventory.
 *
 * A query for a room in [Hotel.physicalRooms] returns that room with its housekeeping status;
 * a query for any other room id returns the overview wrapper without a `room` array, which is
 * how Opera reports an unknown room (the caller maps it rather than receiving an error). The
 * catch-all mapping is added before the per-room mappings so a known room id always resolves
 * to its specific entry (WireMock serves the most recently added match).
 */
fun housekeepingOverview(hotels: List<Hotel>): PlannedStub =
    PlannedStub(
        id = OPERA_HOUSEKEEPING_OVERVIEW_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            hotels.flatMap { hotel ->
                listOf(unknownRoomMapping(hotel)) +
                    hotel.physicalRooms.map { room -> knownRoomMapping(hotel, room) }
            },
    )

private fun unknownRoomMapping(hotel: Hotel): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/hsk/v1/hotels/${hotel.hotelId}/housekeepingOverview",
                queryParameters =
                    mapOf(
                        // The real caller always filters the overview to one room id; a
                        // request without the filter must not match.
                        "roomIdText" to StringValuePattern(matches = ".+"),
                    ),
                headers = hotelHeaders(hotel.hotelId),
            ),
        response =
            jsonResponse(
                jsonBody =
                    stubJsonObject(
                        // Both wrappers stay present without a room array: the service's
                        // mapper dereferences them unconditionally and reports the absent
                        // room as "RoomId Not Available".
                        "housekeepingRoomInfo" to
                            mapOf(
                                "housekeepingRooms" to
                                    mapOf("hotelId" to hotel.hotelId),
                            ),
                    ),
            ),
    )

private fun knownRoomMapping(
    hotel: Hotel,
    room: HotelPhysicalRoom,
): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/hsk/v1/hotels/${hotel.hotelId}/housekeepingOverview",
                queryParameters =
                    mapOf(
                        "roomIdText" to StringValuePattern(equalTo = room.roomId),
                    ),
                headers = hotelHeaders(hotel.hotelId),
            ),
        response =
            jsonResponse(
                jsonBody =
                    stubJsonObject(
                        "housekeepingRoomInfo" to
                            mapOf(
                                "housekeepingRooms" to
                                    mapOf(
                                        "hotelId" to hotel.hotelId,
                                        "room" to
                                            listOf(
                                                mapOf(
                                                    "roomId" to room.roomId,
                                                    "housekeeping" to
                                                        mapOf(
                                                            "housekeepingRoomStatus" to
                                                                mapOf(
                                                                    "housekeepingRoomStatus" to
                                                                        room.housekeepingStatus,
                                                                ),
                                                        ),
                                                ),
                                            ),
                                    ),
                            ),
                    ),
            ),
    )
