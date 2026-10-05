package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.ActivityLogEntry
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom

const val OPERA_ACTIVITY_LOG_STUB_ID = "booking.opera.activity-log"

private val defaultActivityLogEntries =
    listOf(
        ActivityLogEntry(
            actionType = "UPDATE RESERVATION",
            actionDescription = "Reservation updated",
        ),
    )

/**
 * Builds one Opera activity-log read per reservation room. A room without configured entries
 * serves one synthetic default entry so the happy-path response is never empty. The `limit` and
 * `offset` request parameters are pass-through request input and are deliberately not matched.
 */
fun activityLog(
    booking: Booking,
    rooms: List<BookingRoom>,
): PlannedStub =
    PlannedStub(
        id = OPERA_ACTIVITY_LOG_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = rooms.map { room -> activityLogMapping(booking, room) },
    )

private fun activityLogMapping(
    booking: Booking,
    room: BookingRoom,
): StubMapping {
    val hotelId = booking.hotel.hotelId
    val reservationId = requireNotNull(room.reservationId) { "activity-log rooms need a reservationId" }
    val entries = room.activityLogEntries.ifEmpty { defaultActivityLogEntries }

    return StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/rsv/v1/hotels/$hotelId/reservations/activityLog",
                queryParameters =
                    mapOf(
                        "parameterName" to StringValuePattern(equalTo = "RESV_NAME_ID"),
                        "parameterValue" to StringValuePattern(equalTo = reservationId),
                    ),
                headers = hotelHeaders(hotelId),
            ),
        response =
            jsonResponse(
                jsonBody =
                    stubJsonObject(
                        "activityLog" to
                            mapOf(
                                "activityLog" to
                                    entries.map { entry ->
                                        mapOf(
                                            "hotelId" to hotelId,
                                            "module" to "RESERVATION",
                                            "logDate" to "2026-01-15T10:00:00Z",
                                            "logUserName" to entry.logUserName,
                                            "actionType" to entry.actionType,
                                            "actionDescription" to entry.actionDescription,
                                        )
                                    },
                                "totalPages" to 1,
                                "offset" to 0,
                                "limit" to 20,
                                "hasMore" to false,
                                "totalResults" to entries.size,
                                "count" to entries.size,
                            ),
                    ),
            ),
    )
}
