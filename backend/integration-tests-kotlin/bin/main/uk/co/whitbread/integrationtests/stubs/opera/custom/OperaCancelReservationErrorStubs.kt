package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.opera.cancelReservation
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom

const val OPERA_CANCEL_RESERVATION_REJECTED_STUB_ID = "opera.cancel-reservation.rejected"

/**
 * Builds an Opera rejection for the reservation-cancellation POST, installed with the matching
 * default excluded.
 */
fun cancelReservationFailure(
    booking: Booking,
    rooms: List<BookingRoom> = booking.rooms,
): PlannedStub =
    cancelReservation(booking, rooms).rejectedByOpera(
        id = OPERA_CANCEL_RESERVATION_REJECTED_STUB_ID,
        detail = "Reservation could not be cancelled.",
    )
