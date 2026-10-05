package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom

const val OPERA_CANCEL_RESERVATION_STUB_ID = "booking.opera.cancel-reservation"

/**
 * Builds Opera cancellation POST mappings for each reservation room.
 *
 * The request body is deliberately not matched: callers send default or override reasons, while
 * the hotel and reservation path identifies the downstream capability.
 */
fun cancelReservation(
    booking: Booking,
    rooms: List<BookingRoom>,
): PlannedStub =
    PlannedStub(
        id = OPERA_CANCEL_RESERVATION_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = rooms.map { room -> cancelReservationMapping(booking, room) },
    )

private fun cancelReservationMapping(
    booking: Booking,
    room: BookingRoom,
): StubMapping {
    val reservationId =
        requireNotNull(room.reservationId) {
            "BookingRoom.reservationId must be configured for cancel-reservation stubs"
        }
    val confirmationNumber = confirmationNumberFor(reservationId)
    val cancellationId = cancellationIdFor(reservationId)

    return StubMapping(
        request =
            RequestPattern(
                method = "POST",
                urlPath = "/rsv/v1/hotels/${booking.hotel.hotelId}/reservations/$reservationId/cancellations",
            ),
        response =
            jsonResponse(
                jsonBody =
                    stubJsonObject(
                        "reservations" to
                            listOf(
                                mapOf(
                                    "reservationIdList" to
                                        listOf(
                                            mapOf("id" to reservationId, "type" to "Reservation"),
                                            mapOf("id" to confirmationNumber, "type" to "Confirmation"),
                                            mapOf("id" to cancellationId, "type" to "Cancellation"),
                                        ),
                                ),
                            ),
                    ),
            ),
    )
}
