package uk.co.whitbread.integrationtests.journeys.companyentity

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.companyentity.CompanyEntityApi
import uk.co.whitbread.integrationtests.clients.companyentity.model.CompanyResponse
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_COMPANY_NEGOTIATED_RATES_STUB_ID
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.Company
import uk.co.whitbread.integrationtests.testkit.presets.Companies

/**
 * Integration coverage for `GET /v1/companies/{corporateId}`.
 *
 * The endpoint resolves a single Opera company profile by corporate id through the real
 * company-entity-service and ohip-adapter-service containers. These scenarios verify default
 * negotiated-rate enrichment, the no-rates response shape, and the `excludeNegotiatedRates`
 * request flag.
 */
class GetCompanySpec :
    JourneySpec(
        "get company by corporate id",
        {
            /**
             * Verifies the default single-company lookup with negotiated-rate enrichment enabled.
             *
             * The scenario proves company entity calls OHIP for the Opera company profile, calls the
             * negotiated-rates endpoint because the profile has a company id, and maps the enriched
             * company response including address fields.
             */
            scenario("GET /v1/companies/{corporateId} returns company with negotiated rates by default") {
                val company = Companies.NEILL_TECHNICAL_SERVICES
                val booking = Booking(companies = listOf(company))

                installFor(booking)

                val result =
                    CompanyEntityApi().getCompany(
                        corporateId = company.corpId,
                        testId = testId,
                    )

                result.attachEvidence("Get Company")

                expect("returns the company") {
                    result.response.status.value shouldBe 200
                    result.body.shouldMatch(company)
                    result.body.shouldMatchAddress(company)
                }
            }

            /**
             * Verifies that a resolved company profile is still returned when Opera has no rates.
             *
             * The scenario uses reusable company data whose negotiated-rates stub returns an empty
             * list, then asserts the response keeps the company and sets `negotiatedRateEnabled=false`.
             */
            scenario("GET /v1/companies/{corporateId} returns company without negotiated rates") {
                val company = Companies.ACME_WITHOUT_NEGOTIATED_RATES
                val booking = Booking(companies = listOf(company))

                installFor(booking)

                val result =
                    CompanyEntityApi().getCompany(
                        corporateId = company.corpId,
                        testId = testId,
                    )

                result.attachEvidence("Get Company")

                expect("returns the company without negotiated rates") {
                    result.response.status.value shouldBe 200
                    result.body.shouldMatch(company)
                    result.body.shouldMatchAddress(company)
                }
            }

            /**
             * Verifies the `excludeNegotiatedRates=true` request branch.
             *
             * The scenario skips the negotiated-rates stub and asserts the endpoint still returns the
             * company profile, proving the downstream rates call was not required for this request.
             */
            scenario("GET /v1/companies/{corporateId} skips negotiated rates when requested") {
                val company = Companies.NEILL_TECHNICAL_SERVICES
                val booking = Booking(companies = listOf(company))

                installFor(
                    booking,
                    excluded = setOf(OPERA_COMPANY_NEGOTIATED_RATES_STUB_ID),
                )

                val result =
                    CompanyEntityApi().getCompany(
                        corporateId = company.corpId,
                        excludeNegotiatedRates = true,
                        testId = testId,
                    )

                result.attachEvidence("Get Company")

                expect("returns the company without negotiated-rate enrichment") {
                    result.response.status.value shouldBe 200
                    result.body.shouldMatch(company, negotiatedRateEnabled = false)
                    result.body.shouldMatchAddress(company)
                }
            }
        },
    )

private fun CompanyResponse.shouldMatch(
    expected: Company,
    negotiatedRateEnabled: Boolean = expected.negotiatedRateEnabled,
) {
    name shouldBe expected.name
    telephoneNumber shouldBe expected.telephoneNumber
    profileType shouldBe expected.profileType
    corpId shouldBe expected.corpId
    companyId shouldBe expected.companyId
    language shouldBe expected.language
    active shouldBe expected.active
    restricted shouldBe expected.restricted
    restrictedReason shouldBe expected.restrictedReason.takeIf { it.isNotEmpty() }
    this.negotiatedRateEnabled shouldBe negotiatedRateEnabled
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
