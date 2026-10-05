package uk.co.whitbread.integrationtests.stubs.opera.custom

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.opera.reservationAmounts
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom

const val OPERA_RESERVATION_AMOUNTS_REJECTED_STUB_ID = "opera.reservation-amounts.rejected"
const val OPERA_RESERVATION_AMOUNTS_NO_SUMMARY_STUB_ID = "opera.reservation-amounts.no-summary"

/**
 * Builds an Opera rejection for the rate-info summary GET used by migrated (BART_OHIP)
 * reservations, installed with the matching default excluded.
 */
fun reservationAmountsFailure(
    booking: Booking,
    rooms: List<BookingRoom> = booking.rooms,
): PlannedStub =
    reservationAmounts(booking, rooms).rejectedByOpera(
        id = OPERA_RESERVATION_AMOUNTS_REJECTED_STUB_ID,
        detail = "Reservation amounts could not be fetched.",
    )

/**
 * Models an Opera reservation whose rate-info read succeeds but carries no money summary at all:
 * a `200` whose body holds only the `links` envelope.
 *
 * Opera answers this way for a reservation it has no rate summary projection for, so any reduction
 * over `summary` has nothing to read. Derived from the default reservation-amounts builder so the
 * request matchers stay in one place; install with the default excluded.
 */
fun reservationAmountsWithoutSummary(
    booking: Booking,
    rooms: List<BookingRoom> = booking.rooms,
): PlannedStub {
    val default = reservationAmounts(booking, rooms)
    return default.copy(
        id = OPERA_RESERVATION_AMOUNTS_NO_SUMMARY_STUB_ID,
        mappings =
            default.mappings.map { mapping ->
                val body = requireNotNull(mapping.response.jsonBody) { "rate-info read must carry a JSON body" }
                val withoutSummary = JsonObject(body.jsonObject.filterKeys { key -> key != "summary" })
                mapping.copy(response = mapping.response.copy(jsonBody = withoutSummary))
            },
    )
}
