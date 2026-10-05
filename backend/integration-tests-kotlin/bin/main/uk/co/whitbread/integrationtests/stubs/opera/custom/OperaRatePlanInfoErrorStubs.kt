package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.opera.ratePlanInfo
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.Rate

const val OPERA_RATE_PLAN_INFO_REJECTED_STUB_ID = "custom.opera.rate-plan-info-rejected"

/** Builds an Opera not-found rejection for one exact rate-plan details read. */
fun ratePlanInfoNotFound(
    hotel: Hotel,
    rate: Rate,
): PlannedStub =
    ratePlanInfo(hotel, rate).rejectedByOpera(
        id = OPERA_RATE_PLAN_INFO_REJECTED_STUB_ID,
        detail = "Rate plan could not be found.",
        status = 404,
    )
