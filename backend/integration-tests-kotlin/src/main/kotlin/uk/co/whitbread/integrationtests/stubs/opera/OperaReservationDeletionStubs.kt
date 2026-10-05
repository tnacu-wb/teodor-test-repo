package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.ResponseDefinition
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom

const val OPERA_RESERVATION_DELETE_STUB_ID = "booking.opera.delete-reservation"

/**
 * Models Opera's reservation API accepting the hard delete of an existing reservation.
 *
 * Any Opera reservation can be deleted, so the stub installs for every room holding a
 * reservation id. Opera acknowledges the delete with an empty 204; there is no request or
 * response body on this operation.
 */
fun deleteReservation(
    booking: Booking,
    rooms: List<BookingRoom>,
): PlannedStub =
    PlannedStub(
        id = OPERA_RESERVATION_DELETE_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = rooms.map { room -> deleteReservationMapping(booking, room) },
    )

private fun deleteReservationMapping(
    booking: Booking,
    room: BookingRoom,
): StubMapping {
    val hotelId = booking.hotel.hotelId
    val reservationId =
        requireNotNull(room.reservationId) { "reservation-deletion rooms need a reservationId" }
    return StubMapping(
        request =
            RequestPattern(
                method = "DELETE",
                urlPath = "/rsv/v1/hotels/$hotelId/reservations/$reservationId",
                headers = hotelHeaders(hotelId),
            ),
        response = ResponseDefinition(status = 204),
    )
}
