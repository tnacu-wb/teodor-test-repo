package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Company

const val CUSTOM_COMPANY_PROFILE_NO_PROFILE_ID_STUB_ID = "custom.opera.company-profile-no-profile-id"

/**
 * Models an Opera company whose CRM record carries no `Profile`-type id: the corporate read
 * succeeds but its `companyIdList` names only the external `CorporateId`, so the adapter cannot
 * resolve the Opera profile to search negotiated rates with.
 *
 * Install with `booking.opera.company-profile` excluded.
 */
fun companyProfileWithoutProfileId(company: Company): PlannedStub =
    PlannedStub(
        id = CUSTOM_COMPANY_PROFILE_NO_PROFILE_ID_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            listOf(
                StubMapping(
                    request =
                        RequestPattern(
                            method = "GET",
                            urlPath = "/crm/v1/companies/${company.corpId}",
                        ),
                    response =
                        jsonResponse(
                            jsonBody =
                                stubJsonObject(
                                    "companyIdList" to
                                        listOf(
                                            mapOf(
                                                "id" to company.corpId,
                                                "type" to "CorporateId",
                                            ),
                                        ),
                                ),
                        ),
                ),
            ),
    )
