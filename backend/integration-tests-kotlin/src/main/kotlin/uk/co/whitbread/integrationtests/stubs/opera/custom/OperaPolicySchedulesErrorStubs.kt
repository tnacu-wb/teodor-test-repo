package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.opera.policySchedules
import uk.co.whitbread.integrationtests.testkit.model.Hotel

const val OPERA_POLICY_SCHEDULES_REJECTED_STUB_ID = "opera.policy-schedules.rejected"

/** Builds an Opera rejection for the policy-schedules read, installed with the default excluded. */
fun policySchedulesFailure(hotel: Hotel): PlannedStub =
    policySchedules(listOf(hotel)).rejectedByOpera(
        id = OPERA_POLICY_SCHEDULES_REJECTED_STUB_ID,
        detail = "Policy schedules could not be fetched.",
    )
