package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.opera.putReservation
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import java.util.UUID

const val OPERA_SPECIAL_REQUESTS_FINAL_PUT_ERROR_STUB_ID = "custom.opera.special-requests-final-put-error"

private const val SCENARIO_STARTED = "Started"
private const val COMMENTS_REMOVED = "special-requests-comments-removed"

/**
 * Accepts the preliminary special-request comment-removal PUT, then rejects the final PUT.
 *
 * Both operations target the same reservation URL, so WireMock scenario state is the stable way
 * to distinguish their temporal order without coupling this reusable failure to request bodies.
 */
fun finalSpecialRequestsPutFailure(
    booking: Booking,
    room: BookingRoom = booking.room,
): PlannedStub {
    val defaultMapping = putReservation(booking, room).mappings.single()
    val scenarioName = "opera-special-requests-final-put:${requireNotNull(room.reservationId)}:${UUID.randomUUID()}"

    return PlannedStub(
        id = OPERA_SPECIAL_REQUESTS_FINAL_PUT_ERROR_STUB_ID,
        target = putReservation(booking, room).target,
        mappings =
            listOf(
                defaultMapping.copy(
                    scenarioName = scenarioName,
                    requiredScenarioState = SCENARIO_STARTED,
                    newScenarioState = COMMENTS_REMOVED,
                ),
                defaultMapping
                    .rejectedByOpera(
                        detail = "Final special-request update could not be applied.",
                        status = 500,
                    ).copy(
                        scenarioName = scenarioName,
                        requiredScenarioState = COMMENTS_REMOVED,
                    ),
            ),
    )
}
