package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.opera.depositFolios
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom

const val OPERA_DEPOSIT_FOLIOS_REJECTED_STUB_ID = "opera.deposit-folios.rejected"

/**
 * Builds an Opera rejection for the cashiering deposit-folio POST, installed with the matching
 * default excluded.
 */
fun depositFoliosFailure(
    booking: Booking,
    rooms: List<BookingRoom> = booking.rooms,
): PlannedStub =
    depositFolios(booking, rooms).rejectedByOpera(
        id = OPERA_DEPOSIT_FOLIOS_REJECTED_STUB_ID,
        detail = "Deposit folio could not be posted.",
    )
