package uk.co.whitbread.integrationtests.journeys.companyentity

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.companyentity.CompanyEntityApi
import uk.co.whitbread.integrationtests.clients.companyentity.model.CompanyResponse
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_COMPANY_PROFILE_SEARCH_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.companiesProfileSearchOverride
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.Company
import uk.co.whitbread.integrationtests.testkit.presets.Companies
import uk.co.whitbread.integrationtests.testkit.presets.Hotels

private const val HOTEL_ID = "DUBSOU"

/**
 * Integration coverage for `GET /v1/companies/profile`.
 *
 * The endpoint searches Opera company profile summaries through the real company-entity-service
 * and ohip-adapter-service containers, then maps the returned profile summary rows into the
 * public `CompaniesResponse` shape. These tests focus on request criteria handling, the
 * company-entity limit cap, empty-result mapping, and the fact that this endpoint does not enrich
 * companies with negotiated-rate data.
 */
class GetCompaniesProfileSpec :
    JourneySpec(
        "get companies profile",
        {
            /**
             * Verifies the company-name search path.
             *
             * The scenario installs a profile-search stub that only matches the expected Opera
             * `profileName` query and hotel header, then asserts that the public response maps the
             * single Opera `profileInfo` row to the expected company profile fields.
             */
            scenario("GET /v1/companies/profile returns companies matching company name") {
                val company = Companies.NEILL_TECHNICAL_SERVICES
                val booking = profileSearchBooking(company)

                installFor(booking, excluded = setOf(OPERA_COMPANY_PROFILE_SEARCH_STUB_ID))
                installStub(
                    companiesProfileSearchOverride(
                        companies = listOf(company),
                        hotelId = HOTEL_ID,
                        limit = 10,
                        companyName = company.name,
                    ),
                )

                val result =
                    CompanyEntityApi().getCompaniesProfile(
                        hotelId = HOTEL_ID,
                        companyName = company.name,
                        limit = 10,
                        testId = testId,
                    )

                result.attachEvidence("Get Companies Profile")

                expect("returns the matching company profile") {
                    result.response.status.value shouldBe 200
                    result.body.totalResults shouldBe 1
                    result.body.limit shouldBe 10
                    result.body.offset shouldBe 0
                    result.body.companies shouldHaveSize 1
                    result.body.companies
                        .single()
                        .shouldMatchProfileCompany(company)
                }
            }

            /**
             * Verifies AR-number search precedence over company-name search.
             *
             * The request intentionally includes both `companyName` and `arNumber`; the Opera stub
             * requires the downstream `aRNumber` query so the test proves OHIP adapter uses AR number
             * when it is present and still maps the AR number back into the response.
             */
            scenario("GET /v1/companies/profile searches by AR number when provided") {
                val company = Companies.NEILL_TECHNICAL_SERVICES.copy(arNumber = "AR-123")
                val booking = profileSearchBooking(company)

                installFor(booking, excluded = setOf(OPERA_COMPANY_PROFILE_SEARCH_STUB_ID))
                installStub(
                    companiesProfileSearchOverride(
                        companies = listOf(company),
                        hotelId = HOTEL_ID,
                        limit = 10,
                        arNumber = company.arNumber,
                    ),
                )

                val result =
                    CompanyEntityApi().getCompaniesProfile(
                        hotelId = HOTEL_ID,
                        companyName = "ignored when arNumber is present",
                        arNumber = company.arNumber,
                        limit = 10,
                        testId = testId,
                    )

                result.attachEvidence("Get Companies Profile")

                expect("returns the company matched by AR number") {
                    result.response.status.value shouldBe 200
                    result.body.totalResults shouldBe 1
                    result.body.companies shouldHaveSize 1
                    result.body.companies
                        .single()
                        .shouldMatchProfileCompany(company)
                }
            }

            /**
             * Verifies the company-entity service applies the business cap for profile searches.
             *
             * The public request asks for more than 50 rows, while the Opera stub only matches
             * `limit=50`. A successful response and `body.limit == 50` prove the capped value was
             * passed through company entity to OHIP adapter and then to Opera.
             */
            scenario("GET /v1/companies/profile caps requested limit to 50") {
                val company = Companies.NEILL_TECHNICAL_SERVICES
                val booking = profileSearchBooking(company)

                installFor(booking, excluded = setOf(OPERA_COMPANY_PROFILE_SEARCH_STUB_ID))
                installStub(
                    companiesProfileSearchOverride(
                        companies = listOf(company),
                        hotelId = HOTEL_ID,
                        limit = 50,
                        companyName = company.name,
                    ),
                )

                val result =
                    CompanyEntityApi().getCompaniesProfile(
                        hotelId = HOTEL_ID,
                        companyName = company.name,
                        limit = 99,
                        testId = testId,
                    )

                result.attachEvidence("Get Companies Profile")

                expect("uses the capped limit downstream") {
                    result.response.status.value shouldBe 200
                    result.body.limit shouldBe 50
                    result.body.totalResults shouldBe 1
                    result.body.companies shouldHaveSize 1
                }
            }

            /**
             * Verifies the empty-result mapping path.
             *
             * The Opera profile-search stub returns an empty `profileInfo` list, and the scenario
             * asserts company entity preserves the empty companies collection and paging metadata
             * instead of treating the search as an error.
             */
            scenario("GET /v1/companies/profile returns empty results when Opera has no matches") {
                val booking = profileSearchBooking(Companies.NEILL_TECHNICAL_SERVICES)

                installFor(booking, excluded = setOf(OPERA_COMPANY_PROFILE_SEARCH_STUB_ID))
                installStub(
                    companiesProfileSearchOverride(
                        companies = emptyList(),
                        hotelId = HOTEL_ID,
                        limit = 10,
                        companyName = "Missing Company",
                    ),
                )

                val result =
                    CompanyEntityApi().getCompaniesProfile(
                        hotelId = HOTEL_ID,
                        companyName = "Missing Company",
                        limit = 10,
                        testId = testId,
                    )

                result.attachEvidence("Get Companies Profile")

                expect("returns an empty companies list") {
                    result.response.status.value shouldBe 200
                    result.body.totalResults shouldBe 0
                    result.body.limit shouldBe 10
                    result.body.offset shouldBe 0
                    result.body.companies shouldHaveSize 0
                }
            }
        },
    )

private fun profileSearchBooking(company: Company): Booking =
    Booking(
        hotels = listOf(Hotels.HEAPTI.copy(hotelId = HOTEL_ID)),
        companies = listOf(company),
    )

private fun CompanyResponse.shouldMatchProfileCompany(expected: Company) {
    name shouldBe expected.name
    telephoneNumber shouldBe expected.telephoneNumber
    profileType shouldBe expected.profileType
    corpId shouldBe expected.corpId
    companyId shouldBe expected.companyId
    language shouldBe expected.language
    arNumber shouldBe expected.arNumber.orEmpty()
    active shouldBe expected.active
    restricted shouldBe expected.restricted
    restrictedReason shouldBe expected.restrictedReason
    negotiatedRateEnabled shouldBe false
    shouldMatchAddress(expected)
}

private fun CompanyResponse.shouldMatchAddress(expected: Company) {
    val actual = address.shouldNotBeNull()
    actual.addressLine1 shouldBe expected.address.addressLine1
    actual.addressLine2 shouldBe expected.address.addressLine2
    actual.addressLine3 shouldBe expected.address.addressLine3
    actual.addressLine4 shouldBe expected.address.addressLine4
    actual.country shouldBe expected.address.country
    actual.postalCode shouldBe expected.address.postalCode
}
