package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.opera.createCancellationPolicies
import uk.co.whitbread.integrationtests.stubs.opera.deleteCancellationPolicies
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom

const val OPERA_DELETE_CANCELLATION_POLICY_REJECTED_STUB_ID = "opera.delete-cancellation-policy.rejected"
const val OPERA_CREATE_CANCELLATION_POLICY_REJECTED_STUB_ID = "opera.create-cancellation-policy.rejected"

/**
 * Builds an Opera rejection for the cancellation-policy DELETE, installed with the matching
 * default excluded.
 *
 * The default matchers are retained, so the failure hits exactly the request the adapter would
 * otherwise succeed with, including the pinned first policyId.
 */
fun deleteCancellationPolicyFailure(
    booking: Booking,
    rooms: List<BookingRoom> = booking.rooms,
): PlannedStub =
    rejectedByOpera(
        deleteCancellationPolicies(booking, rooms),
        id = OPERA_DELETE_CANCELLATION_POLICY_REJECTED_STUB_ID,
    )

/**
 * Builds an Opera rejection for the cancellation-policy POST, installed with the matching
 * default excluded so the reservation GET and policy DELETE defaults stay untouched.
 */
fun createCancellationPolicyFailure(
    booking: Booking,
    rooms: List<BookingRoom> = booking.rooms,
): PlannedStub =
    rejectedByOpera(
        createCancellationPolicies(booking, rooms),
        id = OPERA_CREATE_CANCELLATION_POLICY_REJECTED_STUB_ID,
    )

private fun rejectedByOpera(
    default: PlannedStub,
    id: String,
): PlannedStub =
    default.copy(
        id = id,
        mappings = default.mappings.map { mapping -> mapping.rejectedByOpera() },
    )

private fun StubMapping.rejectedByOpera(): StubMapping =
    copy(
        response =
            jsonResponse(
                status = 400,
                jsonBody =
                    stubJsonObject(
                        "type" to "Bad Request",
                        "title" to "Cancellation policy could not be modified.",
                        "detail" to "Cancellation policy could not be modified.",
                        "o:errorCode" to "OPERAWS-GEN01278",
                        "language" to "en",
                    ),
            ),
    )
