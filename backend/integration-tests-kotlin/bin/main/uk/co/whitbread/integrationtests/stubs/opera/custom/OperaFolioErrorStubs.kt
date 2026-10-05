package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.opera.reservationFolios
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom

const val OPERA_RESERVATION_FOLIOS_REJECTED_STUB_ID = "opera.reservation-folios.rejected"

/**
 * Builds an Opera rejection for the cashiering folios GET, installed with the matching default
 * excluded.
 */
fun reservationFoliosFailure(
    booking: Booking,
    rooms: List<BookingRoom> = booking.rooms,
): PlannedStub =
    reservationFolios(booking, rooms).rejectedByOpera(
        id = OPERA_RESERVATION_FOLIOS_REJECTED_STUB_ID,
        detail = "Folios could not be fetched.",
    )
