package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.opera.checkIn
import uk.co.whitbread.integrationtests.stubs.opera.housekeepingOverview
import uk.co.whitbread.integrationtests.stubs.opera.roomAssignment
import uk.co.whitbread.integrationtests.stubs.opera.vacantRooms
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Hotel

const val CUSTOM_VACANT_ROOMS_ERROR_STUB_ID = "custom.opera.vacant-rooms-error"
const val CUSTOM_ROOM_ASSIGNMENT_ERROR_STUB_ID = "custom.opera.room-assignment-error"
const val CUSTOM_CHECK_IN_REJECTED_STUB_ID = "custom.opera.check-in-rejected"
const val CUSTOM_HOUSEKEEPING_OVERVIEW_FAILURE_STUB_ID = "custom.opera.housekeeping-overview-failure"

/**
 * Builds an Opera server-error rejection for the front-office clean-vacant rooms query,
 * installed with the matching default excluded.
 */
fun vacantRoomsFailure(hotels: List<Hotel>): PlannedStub =
    vacantRooms(hotels).rejectedByOpera(
        id = CUSTOM_VACANT_ROOMS_ERROR_STUB_ID,
        detail = "Room search could not be completed.",
        status = 500,
    )

/**
 * Builds an Opera rejection for the room-assignment POST, installed with the matching default
 * excluded. Opera refuses the assignment (e.g. the room is out of order), a 400 the adapter
 * must map rather than pass through.
 */
fun roomAssignmentFailure(
    booking: Booking,
    rooms: List<BookingRoom>,
): PlannedStub =
    roomAssignment(booking, rooms).rejectedByOpera(
        id = CUSTOM_ROOM_ASSIGNMENT_ERROR_STUB_ID,
        detail = "Room assignment could not be processed.",
    )

/**
 * Builds an Opera rejection for the front-office check-in POST, installed with the matching
 * default excluded. Opera refuses the check-in (e.g. the reservation is not arrivable today),
 * which the adapter must map without retrying.
 */
fun checkInRejected(
    booking: Booking,
    rooms: List<BookingRoom>,
): PlannedStub =
    checkIn(booking, rooms).rejectedByOpera(
        id = CUSTOM_CHECK_IN_REJECTED_STUB_ID,
        detail = "Reservation cannot be checked in.",
    )

/**
 * Builds an Opera server-error rejection for the housekeeping-overview query, installed with
 * the matching default excluded. Distinct from the unknown-room branch, which is a 200 without
 * a room array and stays on the default stub.
 */
fun housekeepingOverviewFailure(hotels: List<Hotel>): PlannedStub =
    housekeepingOverview(hotels).rejectedByOpera(
        id = CUSTOM_HOUSEKEEPING_OVERVIEW_FAILURE_STUB_ID,
        detail = "Housekeeping overview could not be fetched.",
        status = 500,
    )
