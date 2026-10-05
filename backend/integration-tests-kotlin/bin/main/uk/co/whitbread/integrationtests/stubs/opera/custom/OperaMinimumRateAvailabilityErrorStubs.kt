package uk.co.whitbread.integrationtests.stubs.opera.custom

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.opera.minimumRateAvailability
import uk.co.whitbread.integrationtests.testkit.model.Booking

const val CUSTOM_MINIMUM_RATE_AVAILABILITY_ERROR_STUB_ID = "custom.opera.minimum-rate-availability-error"
const val CUSTOM_MINIMUM_RATE_AVAILABILITY_PARTIAL_INTERVAL_STUB_ID =
    "custom.opera.minimum-rate-availability-partial-interval"
const val CUSTOM_MINIMUM_RATE_AVAILABILITY_MISSING_RATE_STUB_ID =
    "custom.opera.minimum-rate-availability-missing-rate"

/**
 * Answers every Booking-shaped minimum-rate property search with an Opera 500 rejection.
 *
 * Install with `booking.opera.minimum-rate-availability` excluded.
 */
fun minimumRateAvailabilityFailure(booking: Booking): PlannedStub =
    minimumRateAvailability(booking).rejectedByOpera(
        id = CUSTOM_MINIMUM_RATE_AVAILABILITY_ERROR_STUB_ID,
        detail = "minimum rate availability search failed",
        status = 500,
    )

/**
 * Models a hotel that Opera reports in the first stay interval but omits from every later one —
 * the world state behind the adapter's interval-intersection merge for stays over 90 days.
 *
 * The first interval's mapping answers for all hotels; every later interval's mapping answers
 * only for the hotels other than [droppedHotelId]. Requires a Booking whose stay splits into at
 * least two Opera intervals. Install with `booking.opera.minimum-rate-availability` excluded.
 */
fun minimumRateAvailabilityDroppingHotelAfterFirstInterval(
    booking: Booking,
    droppedHotelId: String,
): PlannedStub {
    require(booking.hotels.any { hotel -> hotel.hotelId == droppedHotelId }) {
        "Hotel $droppedHotelId is not part of the booking"
    }
    val full = minimumRateAvailability(booking)
    require(full.mappings.size >= 2) {
        "Stay must split into at least two Opera intervals, found ${full.mappings.size}"
    }
    val reduced =
        minimumRateAvailability(
            booking.copy(hotels = booking.hotels.filterNot { hotel -> hotel.hotelId == droppedHotelId }),
        )

    return PlannedStub(
        id = CUSTOM_MINIMUM_RATE_AVAILABILITY_PARTIAL_INTERVAL_STUB_ID,
        target = full.target,
        mappings = listOf(full.mappings.first()) + reduced.mappings.drop(1),
    )
}

/**
 * Models an Opera minimum-rate response whose room stays carry no `minimumRate` block — a
 * data anomaly some properties produce when no sellable rate exists for the window.
 *
 * Every matcher of the generic capability is retained; only the `minimumRate` key is removed
 * from each room stay. Install with `booking.opera.minimum-rate-availability` excluded.
 */
fun minimumRateAvailabilityWithoutMinimumRate(booking: Booking): PlannedStub =
    minimumRateAvailability(booking).let { default ->
        default.copy(
            id = CUSTOM_MINIMUM_RATE_AVAILABILITY_MISSING_RATE_STUB_ID,
            mappings =
                default.mappings.map { mapping ->
                    val body = requireNotNull(mapping.response.jsonBody).jsonObject
                    val strippedStays =
                        JsonArray(
                            (body.getValue("roomStays") as JsonArray).map { stay ->
                                JsonObject(stay.jsonObject.filterKeys { key -> key != "minimumRate" })
                            },
                        )
                    mapping.copy(
                        response =
                            jsonResponse(
                                jsonBody = JsonObject(body + ("roomStays" to strippedStays)),
                            ),
                    )
                },
        )
    }
