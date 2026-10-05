package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.ResponseDefinition
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom

const val OPERA_DELETE_CANCELLATION_POLICY_STUB_ID = "booking.opera.delete-cancellation-policy"
const val OPERA_CREATE_CANCELLATION_POLICY_STUB_ID = "booking.opera.create-cancellation-policy"

/**
 * Builds Opera cancellation-policy DELETE mappings for each reservation room.
 *
 * When the room carries policies, the first policy's `policyId` query is pinned. Otherwise the
 * path alone identifies the capability so a missing policy still matches.
 */
fun deleteCancellationPolicies(
    booking: Booking,
    rooms: List<BookingRoom>,
): PlannedStub =
    PlannedStub(
        id = OPERA_DELETE_CANCELLATION_POLICY_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = rooms.map { room -> deleteCancellationPolicyMapping(booking, room) },
    )

/**
 * Builds Opera cancellation-policy POST mappings for each reservation room.
 *
 * The rewritten deadline is a request field, not a world fact, so the body is not matched.
 */
fun createCancellationPolicies(
    booking: Booking,
    rooms: List<BookingRoom>,
): PlannedStub =
    PlannedStub(
        id = OPERA_CREATE_CANCELLATION_POLICY_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = rooms.map { room -> createCancellationPolicyMapping(booking, room) },
    )

private fun deleteCancellationPolicyMapping(
    booking: Booking,
    room: BookingRoom,
): StubMapping {
    val reservationId = requiredReservationId(room)
    val policyId = room.cancellationPolicies.firstOrNull()?.policyId
    return StubMapping(
        request =
            RequestPattern(
                method = "DELETE",
                urlPath =
                    "/rsv/v1/hotels/${booking.hotel.hotelId}/reservations/$reservationId/cancellationPolicies",
                queryParameters =
                    policyId?.let { id ->
                        mapOf("policyId" to StringValuePattern(equalTo = id))
                    },
            ),
        response = ResponseDefinition(status = 200),
    )
}

private fun createCancellationPolicyMapping(
    booking: Booking,
    room: BookingRoom,
): StubMapping {
    val reservationId = requiredReservationId(room)
    return StubMapping(
        request =
            RequestPattern(
                method = "POST",
                urlPath =
                    "/rsv/v1/hotels/${booking.hotel.hotelId}/reservations/$reservationId/cancellationPolicies",
            ),
        response = ResponseDefinition(status = 200),
    )
}

private fun requiredReservationId(room: BookingRoom): String =
    requireNotNull(room.reservationId) {
        "BookingRoom.reservationId must be configured for cancellation-policy stubs"
    }
