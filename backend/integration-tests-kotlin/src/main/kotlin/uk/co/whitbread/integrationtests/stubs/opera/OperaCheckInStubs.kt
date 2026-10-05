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

const val OPERA_CHECK_IN_STUB_ID = "booking.opera.check-in"

/**
 * Models Opera Front Office's ability to check a reservation into its assigned room.
 *
 * A reservation whose world state carries an assigned room ([BookingRoom.assignedRoomId])
 * accepts a check-in POST for exactly that room — the real caller always sends
 * `ignoreWarnings=true`, so the matcher requires it — and Opera answers with the in-house
 * reservation snapshot: status `InHouse` and the assigned room as the current room.
 */
fun checkIn(
    booking: Booking,
    rooms: List<BookingRoom>,
): PlannedStub =
    PlannedStub(
        id = OPERA_CHECK_IN_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = rooms.map { room -> checkInMapping(booking, room) },
    )

private fun checkInMapping(
    booking: Booking,
    room: BookingRoom,
): StubMapping {
    val hotelId = booking.hotel.hotelId
    val reservationId =
        requireNotNull(room.reservationId) { "check-in rooms need a reservationId" }
    val assignedRoomId =
        requireNotNull(room.assignedRoomId) { "check-in rooms need an assignedRoomId" }
    val roomIdLiteral = jsonPathStringLiteral(assignedRoomId)

    return StubMapping(
        request =
            RequestPattern(
                method = "POST",
                urlPath = "/fof/v1/hotels/$hotelId/reservations/$reservationId/checkIns",
                headers = hotelHeaders(hotelId),
                bodyPatterns =
                    listOf(
                        BodyPattern(
                            matchesJsonPath = "$[?(@.reservation.roomId == $roomIdLiteral)]",
                        ),
                        BodyPattern(
                            matchesJsonPath = "$[?(@.reservation.ignoreWarnings == true)]",
                        ),
                    ),
            ),
        response =
            jsonResponse(
                jsonBody =
                    stubJsonObject(
                        "reservation" to
                            listOf(
                                mapOf(
                                    "reservationIdList" to
                                        listOf(
                                            mapOf(
                                                "type" to "Reservation",
                                                "id" to reservationId,
                                            ),
                                        ),
                                    "hotelId" to hotelId,
                                    "reservationStatus" to "InHouse",
                                    "computedReservationStatus" to "InHouse",
                                    "roomStayReservation" to true,
                                    "roomStay" to
                                        mapOf(
                                            "currentRoomInfo" to
                                                mapOf(
                                                    "roomType" to room.roomType,
                                                    "roomId" to assignedRoomId,
                                                ),
                                            "arrivalDate" to booking.arrival?.toString(),
                                            "departureDate" to booking.departure?.toString(),
                                        ),
                                ),
                            ),
                    ),
            ),
    )
}
