package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.framework.wiremock.model.ResponseDefinition
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.opera.activityLog
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom

const val OPERA_ACTIVITY_LOG_NO_CONTENT_STUB_ID = "opera.activity-log.no-content"
const val OPERA_ACTIVITY_LOG_REJECTED_STUB_ID = "opera.activity-log.rejected"
const val OPERA_ACTIVITY_LOG_SERVER_ERROR_STUB_ID = "opera.activity-log.server-error"

/** Builds an Opera 204 for the activity-log read: no activity exists for the reservation. */
fun activityLogNoContent(
    booking: Booking,
    room: BookingRoom,
): PlannedStub {
    val default = activityLog(booking, listOf(room))
    return default.copy(
        id = OPERA_ACTIVITY_LOG_NO_CONTENT_STUB_ID,
        mappings = default.mappings.map { it.copy(response = ResponseDefinition(status = 204)) },
    )
}

/** Builds an Opera 4xx rejection for the activity-log read. */
fun activityLogOperaError(
    booking: Booking,
    room: BookingRoom,
): PlannedStub =
    activityLog(booking, listOf(room)).rejectedByOpera(
        id = OPERA_ACTIVITY_LOG_REJECTED_STUB_ID,
        detail = "Activity log could not be fetched.",
    )

/** Builds an Opera 5xx failure for the activity-log read. */
fun activityLogServerError(
    booking: Booking,
    room: BookingRoom,
): PlannedStub =
    activityLog(booking, listOf(room)).rejectedByOpera(
        id = OPERA_ACTIVITY_LOG_SERVER_ERROR_STUB_ID,
        detail = "Activity log read failed downstream.",
        status = 502,
    )
