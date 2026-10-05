package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.opera.multiHotelAvailability
import uk.co.whitbread.integrationtests.stubs.opera.multiHotelNegotiatedAvailability
import uk.co.whitbread.integrationtests.testkit.model.Booking

const val CUSTOM_MULTI_HOTEL_AVAILABILITY_ERROR_STUB_ID = "custom.opera.multi-hotel-availability-error"
const val CUSTOM_MULTI_HOTEL_NEGOTIATED_AVAILABILITY_ERROR_STUB_ID =
    "custom.opera.multi-hotel-negotiated-availability-error"

/**
 * Answers every Booking-shaped cross-hotel availability search with an Opera 500 rejection.
 *
 * The default's matchers are retained under this stub's own id, so exactly the requests the
 * adapter would otherwise succeed with now fail. Install with
 * `booking.opera.multi-hotel-availability` excluded.
 */
fun multiHotelAvailabilityFailure(booking: Booking): PlannedStub =
    multiHotelAvailability(booking).rejectedByOpera(
        id = CUSTOM_MULTI_HOTEL_AVAILABILITY_ERROR_STUB_ID,
        detail = "availability search failed",
        status = 500,
    )

/**
 * Answers every company-negotiated cross-hotel availability search with an Opera 500 rejection.
 *
 * Install with `booking.opera.multi-hotel-negotiated-availability` excluded; the public-rate
 * availability default may stay installed, its mappings are untouched.
 */
fun multiHotelNegotiatedAvailabilityFailure(booking: Booking): PlannedStub =
    multiHotelNegotiatedAvailability(booking).rejectedByOpera(
        id = CUSTOM_MULTI_HOTEL_NEGOTIATED_AVAILABILITY_ERROR_STUB_ID,
        detail = "negotiated availability search failed",
        status = 500,
    )
