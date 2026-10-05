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

const val OPERA_COMPANY_PROFILE_STUB_ID = "booking.opera.company-profile"
const val OPERA_COMPANY_PROFILE_BY_ID_STUB_ID = "booking.opera.company-profile-by-id"

private val companyProfileFetchInstructions =
    listOf("ADDRESS", "COMMUNICATION", "SalesInfo", "Keyword", "Profile")

/**
 * The exact `fetchInstructions` set every real `/crm/v1/profiles/{id}` reader sends, character
 * for character and in order: a caller that drops or renames an instruction stops matching.
 */
private val companyProfileByIdFetchInstructions =
    listOf("Profile", "Address", "Communication", "Correspondence", "FutureReservation", "HistoryReservation")

/**
 * Builds the Opera profile-by-company-id read for each company in the Booking: the full CRM
 * profile Opera serves under the company's own profile id, carrying the id list consumers resolve
 * corporate links from plus the profile-details address block billing-address consumers
 * dereference. Two real caller shapes exist for `/crm/v1/profiles/{companyId}`, so each company
 * gets one mapping per shape: the reservation clients send the six repeated fetch instructions,
 * while `OhipProfileClient.getCompanyProfile` (company-entity-service's company-id fallback)
 * sends no query at all — both with the authenticated hub headers and no `x-hotelid`. A caller
 * sending any other instruction set matches neither.
 */
fun companyProfilesById(companies: List<Company>): PlannedStub =
    PlannedStub(
        id = OPERA_COMPANY_PROFILE_BY_ID_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            companies.flatMap { company ->
                listOf(
                    companyProfileByCompanyId(company, withFetchInstructions = true),
                    companyProfileByCompanyId(company, withFetchInstructions = false),
                )
            },
    )

/** Models the populated and raw-company-id Opera corporate-profile reads for [company]. */
fun companyProfile(company: Company): PlannedStub = companyProfiles(listOf(company))

/**
 * Builds the Opera corporate-profile reads for each company in the Booking.
 *
 * Models the generic world state around `/crm/v1/companies/{id}`: the corporate profile is
 * served under the company's corpId, and a read using the raw Opera companyId answers an empty
 * profile — Opera holds no corporate profile under that id. The empty mapping is what lets a
 * consumer's corporate-id-first lookup fall back to the profile-by-company-id read (served by
 * the company-profile-by-id default) without any scenario-specific stub.
 */
fun companyProfiles(companies: List<Company>): PlannedStub =
    PlannedStub(
        id = OPERA_COMPANY_PROFILE_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            companies.flatMap { company ->
                listOfNotNull(
                    emptyCompanyProfile(company.companyId).takeIf { company.companyId != company.corpId },
                    companyProfileMapping(company),
                )
            },
    )

private fun companyProfileMapping(company: Company): StubMapping =
    StubMapping(
        request = companyProfileRequest(company.corpId),
        response =
            jsonResponse(
                jsonBody = companyProfileResponse(company),
            ),
    )

private fun emptyCompanyProfile(corporateId: String): StubMapping =
    StubMapping(
        request = companyProfileRequest(corporateId),
        response =
            jsonResponse(
                jsonBody = JsonObject(emptyMap()),
            ),
    )

private fun companyProfileRequest(corporateId: String): RequestPattern =
    RequestPattern(
        method = "GET",
        urlPath = "/crm/v1/companies/$corporateId",
        queryParameters =
            mapOf(
                "fetchInstructions" to
                    StringValuePattern(
                        hasExactly =
                            companyProfileFetchInstructions.map { instruction ->
                                StringValuePattern(equalTo = instruction)
                            },
                    ),
            ),
        headers =
            authenticatedHubHeaders() +
                mapOf(
                    "x-hotelid" to StringValuePattern(absent = true),
                ),
    )

private fun companyProfileByCompanyId(
    company: Company,
    withFetchInstructions: Boolean,
): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/crm/v1/profiles/${company.companyId}",
                queryParameters =
                    mapOf(
                        "fetchInstructions" to
                            if (withFetchInstructions) {
                                StringValuePattern(
                                    hasExactly =
                                        companyProfileByIdFetchInstructions.map { instruction ->
                                            StringValuePattern(equalTo = instruction)
                                        },
                                )
                            } else {
                                StringValuePattern(absent = true)
                            },
                    ),
                headers =
                    authenticatedHubHeaders() +
                        mapOf(
                            "x-hotelid" to StringValuePattern(absent = true),
                        ),
            ),
        response =
            jsonResponse(
                jsonBody =
                    JsonObject(
                        mapOf(
                            "profileIdList" to
                                JsonArray(
                                    listOfNotNull(
                                        uniqueId(company.companyId, "Profile"),
                                        // An unlinked profile carries no CorporateId, so the
                                        // corporate profile read is skipped.
                                        uniqueId(company.corpId, "CorporateId")
                                            .takeIf { company.corporateIdLinked },
                                    ),
                                ),
                            "profileDetails" to companyProfileByIdDetails(company),
                        ),
                    ),
            ),
    )

/**
 * Builds the profile-details block of the company's own CRM profile: the address Opera holds for
 * the company, which billing-address consumers read as `addresses.addressInfo[0]` before writing.
 */
private fun companyProfileByIdDetails(company: Company): JsonObject =
    JsonObject(
        mapOf(
            "addresses" to
                JsonObject(
                    mapOf(
                        "addressInfo" to
                            JsonArray(
                                listOf(
                                    JsonObject(
                                        mapOf(
                                            "type" to JsonPrimitive("BUSINESS"),
                                            "primary" to JsonPrimitive(true),
                                            "address" to
                                                JsonObject(
                                                    mapOf(
                                                        "addressLine" to
                                                            JsonArray(
                                                                listOf(
                                                                    JsonPrimitive(company.address.addressLine1),
                                                                    JsonPrimitive(company.address.addressLine2),
                                                                    JsonPrimitive(company.address.addressLine3),
                                                                    JsonPrimitive(company.address.addressLine4),
                                                                ),
                                                            ),
                                                        "postalCode" to JsonPrimitive(company.address.postalCode),
                                                        "country" to
                                                            JsonObject(
                                                                mapOf(
                                                                    "value" to JsonPrimitive(company.address.country),
                                                                    "code" to JsonPrimitive(company.address.country),
                                                                ),
                                                            ),
                                                    ),
                                                ),
                                        ),
                                    ),
                                ),
                            ),
                    ),
                ),
        ),
    )

private fun companyProfileResponse(company: Company): JsonObject =
    JsonObject(
        mapOf(
            "companyIdList" to
                JsonArray(
                    listOf(
                        uniqueId(company.companyId, "Profile"),
                        uniqueId(company.corpId, "CorporateId"),
                    ),
                ),
            "companyDetails" to
                JsonObject(
                    mapOf(
                        "company" to
                            JsonObject(
                                mapOf(
                                    "companyName" to JsonPrimitive(company.name),
                                    "language" to JsonPrimitive(company.language),
                                ),
                            ),
                        "addresses" to
                            JsonObject(
                                mapOf(
                                    "addressInfo" to
                                        JsonArray(
                                            listOf(
                                                JsonObject(
                                                    mapOf(
                                                        "type" to JsonPrimitive("BUSINESS"),
                                                        "address" to
                                                            JsonObject(
                                                                mapOf(
                                                                    "addressLine" to
                                                                        JsonArray(
                                                                            listOf(
                                                                                JsonPrimitive(company.address.addressLine1),
                                                                                JsonPrimitive(company.address.addressLine2),
                                                                                JsonPrimitive(company.address.addressLine3),
                                                                                JsonPrimitive(company.address.addressLine4),
                                                                            ),
                                                                        ),
                                                                    "postalCode" to JsonPrimitive(company.address.postalCode),
                                                                    "country" to
                                                                        JsonObject(
                                                                            mapOf(
                                                                                "value" to JsonPrimitive(company.address.country),
                                                                                "code" to JsonPrimitive(company.address.country),
                                                                            ),
                                                                        ),
                                                                ),
                                                            ),
                                                    ),
                                                ),
                                            ),
                                        ),
                                ),
                            ),
                        "telephones" to
                            JsonObject(
                                mapOf(
                                    "telephoneInfo" to
                                        JsonArray(
                                            listOf(
                                                JsonObject(
                                                    mapOf(
                                                        "type" to JsonPrimitive("BUSINESS"),
                                                        "telephone" to
                                                            JsonObject(
                                                                mapOf(
                                                                    "phoneNumber" to JsonPrimitive(company.telephoneNumber),
                                                                ),
                                                            ),
                                                    ),
                                                ),
                                            ),
                                        ),
                                ),
                            ),
                        "profileType" to JsonPrimitive(company.profileType),
                        "statusCode" to JsonPrimitive(if (company.active) "Active" else "Inactive"),
                        "profileRestrictions" to
                            JsonObject(
                                mapOf(
                                    "restricted" to JsonPrimitive(company.restricted),
                                    "reason" to JsonPrimitive(company.restrictedReason),
                                ),
                            ),
                    ),
                ),
        ),
    )
