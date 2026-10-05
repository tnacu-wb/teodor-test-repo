package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.opera.companiesProfileSearch
import uk.co.whitbread.integrationtests.testkit.model.Booking

const val OPERA_COMPANY_PROFILE_SEARCH_REJECTED_STUB_ID =
    "custom.opera.company-profile-search-rejected"

/** Builds an Opera not-found rejection for the Booking's exact company profile search. */
fun companiesProfileSearchNotFound(booking: Booking): PlannedStub =
    companiesProfileSearch(booking).rejectedByOpera(
        id = OPERA_COMPANY_PROFILE_SEARCH_REJECTED_STUB_ID,
        detail = "Company profile search could not be completed.",
        status = 404,
    )
