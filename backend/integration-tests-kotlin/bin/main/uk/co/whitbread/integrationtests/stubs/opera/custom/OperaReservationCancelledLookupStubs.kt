package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.opera.getReservations
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus

const val OPERA_RESERVATION_CANCELLED_LOOKUP_STUB_ID = "opera.get-reservation.cancelled"

/**
 * Models an Opera reservation that has already been cancelled outside the journey: the lookup
 * answers the default reservation body with Opera `reservationStatus` `Cancelled`.
 *
 * The default builder already maps [ReservationStatus.CANCELLED] to that wire value, so this
 * builder only re-ids the default read — a scenario needs a *temporal* change (the setup created
 * a live reservation and the endpoint must see it cancelled), which one Booking cannot express.
 *
 * Install as a mid-journey override next to the call it serves, after the default read: WireMock
 * serves the most recently added match.
 */
fun cancelledReservationLookup(
    booking: Booking,
    rooms: List<BookingRoom> = booking.rooms,
): PlannedStub =
    getReservations(
        booking = booking,
        rooms = rooms.map { room -> room.copy(status = ReservationStatus.CANCELLED) },
    ).copy(id = OPERA_RESERVATION_CANCELLED_LOOKUP_STUB_ID)
