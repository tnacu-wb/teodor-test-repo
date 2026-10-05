package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.opera.cancellationReasons
import uk.co.whitbread.integrationtests.testkit.model.Hotel

const val OPERA_CANCELLATION_REASONS_REJECTED_STUB_ID = "opera.cancellation-reasons.rejected"

/**
 * Builds an Opera rejection for the cancellation-reasons LOV lookup, installed with the matching
 * default excluded.
 */
fun cancellationReasonsFailure(hotel: Hotel): PlannedStub =
    cancellationReasons(listOf(hotel)).rejectedByOpera(
        id = OPERA_CANCELLATION_REASONS_REJECTED_STUB_ID,
        detail = "List of values could not be fetched.",
    )
