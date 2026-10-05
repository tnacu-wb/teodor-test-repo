package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.opera.confirmationNumberFor
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom

const val OPERA_CANCEL_RESERVATION_NO_CANCELLATION_ID_STUB_ID = "opera.cancel-reservation.no-cancellation-id"

/**
 * Models an Opera cancellation POST that succeeds but reports no `Cancellation` identifier.
 *
 * Opera answers `200` with the reservation's own identifiers only, so the adapter's response
 * mapper — which filters `reservationIdList` on `type == Cancellation` — collects an empty
 * cancellation-id list. Install with the matching default excluded.
 */
fun cancelReservationWithoutCancellationId(
    booking: Booking,
    rooms: List<BookingRoom> = booking.rooms,
): PlannedStub =
    PlannedStub(
        id = OPERA_CANCEL_RESERVATION_NO_CANCELLATION_ID_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = rooms.map { room -> noCancellationIdMapping(booking, room) },
    )

private fun noCancellationIdMapping(
    booking: Booking,
    room: BookingRoom,
): StubMapping {
    val reservationId =
        requireNotNull(room.reservationId) {
            "BookingRoom.reservationId must be configured for cancel-reservation stubs"
        }

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
                                            mapOf(
                                                "id" to confirmationNumberFor(reservationId),
                                                "type" to "Confirmation",
                                            ),
                                        ),
                                ),
                            ),
                    ),
            ),
    )
}
