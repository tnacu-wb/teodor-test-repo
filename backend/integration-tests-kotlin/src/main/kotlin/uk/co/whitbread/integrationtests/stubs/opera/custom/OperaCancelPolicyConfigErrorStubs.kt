package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.opera.cancelPolicyConfigs
import uk.co.whitbread.integrationtests.testkit.model.Hotel

const val OPERA_CANCEL_POLICY_CONFIGS_REJECTED_STUB_ID = "opera.cancel-policy-configs.rejected"
const val OPERA_CANCEL_POLICY_CONFIGS_NO_MATCH_STUB_ID = "opera.cancel-policy-configs.no-match"

/** Builds an Opera rejection for the cancel-policies configuration read. */
fun cancelPolicyConfigsFailure(hotel: Hotel): PlannedStub =
    cancelPolicyConfigs(listOf(hotel)).rejectedByOpera(
        id = OPERA_CANCEL_POLICY_CONFIGS_REJECTED_STUB_ID,
        detail = "Cancel policy configuration could not be fetched.",
    )

/**
 * Builds a successful cancel-policies read whose config list is empty, so no entry matches the
 * policy code the schedule resolved. The generic default derives its config list from the same
 * rules as the schedules stub, so the mismatch is expressed by stripping the rules here.
 */
fun cancelPolicyConfigsNoMatch(hotel: Hotel): PlannedStub =
    cancelPolicyConfigs(listOf(hotel.copy(cancellationPolicyRules = emptyList())))
        .copy(id = OPERA_CANCEL_POLICY_CONFIGS_NO_MATCH_STUB_ID)
