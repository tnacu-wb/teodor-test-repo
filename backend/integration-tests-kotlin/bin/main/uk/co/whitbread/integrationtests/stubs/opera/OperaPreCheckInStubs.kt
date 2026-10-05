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

const val OPERA_PRE_CHECK_IN_STUB_ID = "booking.opera.pre-check-in"

/**
 * Models Opera's ability to record a mobile pre-check-in status against a reservation.
 *
 * Opera acknowledges the pre-check-in details POST for a reservation whose world state allows
 * it (the room carries [BookingRoom.preCheckInAvailable]) by returning a self link; callers
 * treat the operation as successful only when at least one link is present, so the response
 * always carries one.
 */
fun preCheckInStatus(
    booking: Booking,
    rooms: List<BookingRoom>,
): PlannedStub =
    PlannedStub(
        id = OPERA_PRE_CHECK_IN_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = rooms.map { room -> preCheckInMapping(booking, room) },
    )

private fun preCheckInMapping(
    booking: Booking,
    room: BookingRoom,
): StubMapping {
    val hotelId = booking.hotel.hotelId
    val reservationId =
        requireNotNull(room.reservationId) { "pre-check-in rooms need a reservationId" }
    val hotelIdLiteral = jsonPathStringLiteral(hotelId)

    return StubMapping(
        request =
            RequestPattern(
                method = "POST",
                urlPath = "/rsv/v1/hotels/$hotelId/reservations/$reservationId/preCheckIn",
                headers = hotelHeaders(hotelId),
                bodyPatterns =
                    listOf(
                        // The real caller wraps the hotel id and the arrival info in a
                        // single reservation object; a request that drops either stops
                        // matching.
                        BodyPattern(
                            matchesJsonPath = "$[?(@.reservation.hotelId == $hotelIdLiteral)]",
                        ),
                        BodyPattern(
                            matchesJsonPath = "$.reservation.preCheckInDetails.arrival.arrivalTime",
                        ),
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
                                        "/rsv/v1/hotels/$hotelId/reservations/$reservationId",
                                    "rel" to "self",
                                    "method" to "GET",
                                    "operationId" to "getReservation",
                                ),
                            ),
                    ),
            ),
    )
}
