package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.opera.creditCardInfo
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom

const val OPERA_CREDIT_CARD_INFO_REJECTED_STUB_ID = "opera.credit-card-info.rejected"

/**
 * Builds a Front Desk rejection for the credit-card-info GET, installed with the matching default
 * excluded.
 */
fun creditCardInfoFailure(
    booking: Booking,
    rooms: List<BookingRoom> = booking.rooms,
): PlannedStub =
    creditCardInfo(booking, rooms).rejectedByOpera(
        id = OPERA_CREDIT_CARD_INFO_REJECTED_STUB_ID,
        detail = "Credit card info could not be fetched.",
    )
