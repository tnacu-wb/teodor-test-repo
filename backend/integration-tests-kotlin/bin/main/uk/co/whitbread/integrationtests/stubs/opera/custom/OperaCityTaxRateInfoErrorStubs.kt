package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.opera.cityTaxRateInfo
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom

const val OPERA_CITY_TAX_RATE_INFO_REJECTED_STUB_ID = "opera.city-tax-rate-info.rejected"

/**
 * Builds an Opera rejection for the per-day city-tax rate-info GET, installed with the matching
 * default excluded.
 */
fun cityTaxRateInfoFailure(
    booking: Booking,
    rooms: List<BookingRoom> = booking.rooms,
): PlannedStub =
    cityTaxRateInfo(booking, rooms).rejectedByOpera(
        id = OPERA_CITY_TAX_RATE_INFO_REJECTED_STUB_ID,
        detail = "Rate information lookup failed.",
    )
