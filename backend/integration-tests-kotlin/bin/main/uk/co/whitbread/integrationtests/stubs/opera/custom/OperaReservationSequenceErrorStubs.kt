package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.opera.putReservations
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import java.util.UUID

const val OPERA_SECOND_RESERVATION_PUT_ERROR_STUB_ID = "custom.opera.second-reservation-put-error"

private const val SCENARIO_STARTED = "Started"
private const val ONE_RESERVATION_UPDATED = "one-reservation-updated"

/**
 * Accepts whichever reservation PUT arrives first, then rejects the next reservation PUT.
 *
 * The mappings retain each Booking room's normal reservation URL and healthy response. Shared
 * WireMock scenario state makes the failure independent of set iteration order, so callers can
 * prove sequential partial completion without coupling the stub to a particular reservation id.
 */
fun secondReservationPutFailure(
    booking: Booking,
    rooms: List<BookingRoom> = booking.rooms,
): PlannedStub {
    require(rooms.size >= 2) { "A second reservation PUT failure requires at least two rooms" }

    val default = putReservations(booking, rooms)
    val scenarioName = "opera-second-reservation-put:${UUID.randomUUID()}"
    return PlannedStub(
        id = OPERA_SECOND_RESERVATION_PUT_ERROR_STUB_ID,
        target = default.target,
        mappings =
            default.mappings.flatMap { defaultMapping ->
                listOf(
                    defaultMapping.copy(
                        scenarioName = scenarioName,
                        requiredScenarioState = SCENARIO_STARTED,
                        newScenarioState = ONE_RESERVATION_UPDATED,
                    ),
                    defaultMapping
                        .rejectedByOpera(
                            detail = "The second reservation could not be modified.",
                            status = 500,
                        ).copy(
                            scenarioName = scenarioName,
                            requiredScenarioState = ONE_RESERVATION_UPDATED,
                        ),
                )
            },
    )
}
