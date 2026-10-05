package uk.co.whitbread.integrationtests.stubs.opera

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.testkit.model.Company

const val OPERA_COMPANY_NEGOTIATED_RATES_STUB_ID = "booking.opera.company-negotiated-rates"

/** Models the Opera negotiated-rate collection held by one [company] profile. */
fun companyNegotiatedRates(company: Company): PlannedStub = companyNegotiatedRates(listOf(company))

/** Models the Opera negotiated-rate collections held by the supplied [companies]. */
fun companyNegotiatedRates(companies: List<Company>): PlannedStub =
    PlannedStub(
        id = OPERA_COMPANY_NEGOTIATED_RATES_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = companies.map(::companyNegotiatedRatesMapping),
    )

private fun companyNegotiatedRatesMapping(company: Company): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/rtp/v1/profiles/${company.companyId}/negotiatedRates",
                headers =
                    authenticatedHubHeaders() +
                        mapOf(
                            "x-hotelid" to StringValuePattern(absent = true),
                        ),
            ),
        response =
            jsonResponse(
                jsonBody = negotiatedRatesResponse(company),
            ),
    )

private fun negotiatedRatesResponse(company: Company): JsonObject =
    JsonObject(
        mapOf(
            "negotiatedRates" to
                JsonArray(
                    if (company.negotiatedRateEnabled) {
                        listOf(
                            JsonObject(
                                mapOf(
                                    "hotelId" to JsonPrimitive("DUBSOU"),
                                    "ratePlanCode" to JsonPrimitive("BUSIFLEX"),
                                ),
                            ),
                        )
                    } else {
                        emptyList()
                    },
                ),
        ),
    )
