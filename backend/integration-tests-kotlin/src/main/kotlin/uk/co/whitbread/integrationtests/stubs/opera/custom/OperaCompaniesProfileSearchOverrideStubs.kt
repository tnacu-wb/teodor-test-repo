package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.opera.companiesProfileSearchMapping
import uk.co.whitbread.integrationtests.testkit.model.Company

const val CUSTOM_COMPANY_PROFILE_SEARCH_STUB_ID = "custom.opera.company-profile-search-override"

/**
 * Replaces the Opera company profile-summary search with one exact query shape and response —
 * hotel, result limit, and a company-name or AR-number search term. Carries its own stub id, so
 * it is installed with
 * [OPERA_COMPANY_PROFILE_SEARCH_STUB_ID][uk.co.whitbread.integrationtests.stubs.opera.OPERA_COMPANY_PROFILE_SEARCH_STUB_ID] passed in `excluded` plus `installStub`.
 */
fun companiesProfileSearchOverride(
    companies: List<Company>,
    hotelId: String? = null,
    limit: Int? = null,
    companyName: String? = null,
    arNumber: String? = null,
): PlannedStub =
    PlannedStub(
        id = CUSTOM_COMPANY_PROFILE_SEARCH_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            listOf(
                companiesProfileSearchMapping(
                    companies = companies,
                    hotelId = hotelId,
                    limit = limit,
                    companyName = companyName,
                    arNumber = arNumber,
                ),
            ),
    )
