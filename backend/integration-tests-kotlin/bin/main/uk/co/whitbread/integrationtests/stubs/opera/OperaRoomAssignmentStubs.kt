package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.BodyPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonPathStringLiteral
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom

const val OPERA_ROOM_ASSIGNMENT_STUB_ID = "booking.opera.room-assignment"

/**
 * Models Opera Front Office's ability to assign a physical room to a reservation.
 *
 * Opera accepts a room-assignment POST for any of the booking's reservations — the room number
 * itself is caller input, not reservation state — and acknowledges with a self link. The
 * matcher requires the caller's `criteria` wrapper carrying the reservation id and a room id,
 * so a request that drops either stops matching.
 */
fun roomAssignment(
    booking: Booking,
    rooms: List<BookingRoom>,
): PlannedStub =
    PlannedStub(
        id = OPERA_ROOM_ASSIGNMENT_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = rooms.map { room -> roomAssignmentMapping(booking, room) },
    )

private fun roomAssignmentMapping(
    booking: Booking,
    room: BookingRoom,
): StubMapping {
    val hotelId = booking.hotel.hotelId
    val reservationId =
        requireNotNull(room.reservationId) { "room-assignment rooms need a reservationId" }
    val reservationIdLiteral = jsonPathStringLiteral(reservationId)

    return StubMapping(
        request =
            RequestPattern(
                method = "POST",
                urlPath = "/fof/v1/hotels/$hotelId/reservations/$reservationId/roomAssignments",
                headers = hotelHeaders(hotelId),
                bodyPatterns =
                    listOf(
                        BodyPattern(
                            matchesJsonPath =
                                "$[?(@.criteria.reservationIdList[0].id == $reservationIdLiteral)]",
                        ),
                        BodyPattern(matchesJsonPath = "$.criteria.roomId"),
                    ),
            ),
        response =
            jsonResponse(
                jsonBody =
                    stubJsonObject(
                        "links" to
                            listOf(
                                mapOf(
                                    "href" to
                                        "/fof/v1/hotels/$hotelId/reservations/$reservationId",
                                    "rel" to "self",
                                    "method" to "GET",
                                    "operationId" to "getReservation",
                                ),
                            ),
                    ),
            ),
    )
}
