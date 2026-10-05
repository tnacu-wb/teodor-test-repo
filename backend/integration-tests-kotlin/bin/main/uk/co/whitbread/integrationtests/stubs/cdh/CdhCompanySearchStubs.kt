package uk.co.whitbread.integrationtests.stubs.cdh

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

const val CDH_COMPANY_SEARCH_STUB_ID = "booking.cdh.company-search"

fun companySearch(companies: List<Company>): PlannedStub =
    PlannedStub(
        id = CDH_COMPANY_SEARCH_STUB_ID,
        target = WireMockTarget.CDH,
        mappings = listOf(companySearchMapping(companies)),
    )

private fun companySearchMapping(companies: List<Company>): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/AccountServices/V2/GetcompaniesV2",
                headers =
                    mapOf(
                        "AccessContext" to StringValuePattern(equalTo = "OPERA"),
                        "AccessedBy" to StringValuePattern(equalTo = "company-entity-service"),
                    ),
            ),
        response =
            jsonResponse(
                jsonBody = companySearchResponse(companies),
            ),
    )

private fun companySearchResponse(companies: List<Company>): JsonObject =
    JsonObject(
        mapOf(
            "TotalResults" to JsonPrimitive(companies.size),
            "SearchResults" to JsonPrimitive(companies.size),
            "Results" to JsonArray(companies.map(::companySearchResult)),
        ),
    )

private fun companySearchResult(company: Company): JsonObject =
    JsonObject(
        mapOf(
            "CompanyName" to JsonPrimitive(company.name),
            "CompanyType" to JsonPrimitive(company.profileType),
            "GlobalCompanyId" to JsonPrimitive(company.corpId.toInt()),
            "Status" to JsonPrimitive(if (company.active) "ACTIVE" else "INACTIVE"),
            "CompanyAddress" to companyAddress(company),
            "MainContact" to
                JsonObject(
                    mapOf(
                        "PhoneNumber" to JsonPrimitive(company.telephoneNumber),
                    ),
                ),
        ),
    )

private fun companyAddress(company: Company): JsonObject =
    JsonObject(
        mapOf(
            "AddressLine1" to JsonPrimitive(company.address.addressLine1),
            "AddressLine2" to JsonPrimitive(company.address.addressLine2),
            "AddressLine3" to JsonPrimitive(company.address.addressLine3),
            "AddressLine4" to JsonPrimitive(company.address.addressLine4),
            "CountryCode" to JsonPrimitive(company.address.country),
            "PostCode" to JsonPrimitive(company.address.postalCode),
        ),
    )
