package uk.co.whitbread.integrationtests.journeys.companyentity

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.companyentity.CompanyEntityApi
import uk.co.whitbread.integrationtests.clients.companyentity.model.CompanyResponse
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.Company
import uk.co.whitbread.integrationtests.testkit.presets.Companies

/**
 * Integration coverage for `GET /v1/companies/id/{id}`.
 *
 * The endpoint accepts either a corporate id or an Opera company/profile id. It first tries the
 * value as a corporate id, falls back to a profile-id lookup when that result is empty, and only
 * returns a response body when the resolved company has negotiated rates.
 */
class GetCompanyByIdSpec :
    JourneySpec(
        "get company by id",
        {
            /**
             * Verifies the direct corporate-id path.
             *
             * The scenario passes the company corporate id as `{id}` and asserts the endpoint resolves
             * the Opera company profile without needing the profile-id fallback, enriches negotiated
             * rates, and maps the company/address response.
             */
            scenario("GET /v1/companies/id/{id} returns company when id is a corporate id") {
                val company = Companies.NEILL_TECHNICAL_SERVICES
                val booking = Booking(companies = listOf(company))

                installFor(booking)

                val result =
                    CompanyEntityApi().getCompanyById(
                        id = company.corpId,
                        testId = testId,
                    )

                result.attachEvidence("Get Company By Id")

                expect("returns the company with negotiated rates") {
                    result.response.status.value shouldBe 200
                    result.body.shouldMatch(company)
                    result.body.shouldMatchAddress(company)
                }
            }

            /**
             * Verifies the fallback path for Opera company/profile ids.
             *
             * The scenario passes the Opera company id as `{id}`. The default company-profile stub
             * answers `/crm/v1/companies/{companyId}` with an empty profile, so company entity falls
             * back to `/crm/v1/profiles/{companyId}`, reads the `CorporateId`, and resolves the full
             * company profile and negotiated rates through the corporate id.
             */
            scenario("GET /v1/companies/id/{id} falls back to company id lookup") {
                val company = Companies.NEILL_TECHNICAL_SERVICES
                val booking = Booking(companies = listOf(company))

                installFor(booking)

                val result =
                    CompanyEntityApi().getCompanyById(
                        id = company.companyId,
                        testId = testId,
                    )

                result.attachEvidence("Get Company By Id")

                expect("returns the company resolved from the fallback profile lookup") {
                    result.response.status.value shouldBe 200
                    result.body.shouldMatch(company)
                    result.body.shouldMatchAddress(company)
                }
            }

            /**
             * Verifies the endpoint's negotiated-rates gate.
             *
             * The scenario resolves a valid company profile whose negotiated-rates stub returns an
             * empty list, then asserts the public endpoint responds with HTTP 200 and no body rather
             * than returning the company profile.
             */
            scenario("GET /v1/companies/id/{id} returns no body when company has no negotiated rates") {
                val company = Companies.ACME_WITHOUT_NEGOTIATED_RATES
                val booking = Booking(companies = listOf(company))

                installFor(booking)

                val result =
                    CompanyEntityApi().getCompanyByIdText(
                        id = company.corpId,
                        testId = testId,
                    )

                result.attachEvidence("Get Company By Id")

                expect("returns an empty response body") {
                    result.response.status.value shouldBe 200
                    result.bodyText shouldBe ""
                }
            }
        },
    )

private fun CompanyResponse.shouldMatch(expected: Company) {
    name shouldBe expected.name
    telephoneNumber shouldBe expected.telephoneNumber
    profileType shouldBe expected.profileType
    corpId shouldBe expected.corpId
    companyId shouldBe expected.companyId
    language shouldBe expected.language
    active shouldBe expected.active
    restricted shouldBe expected.restricted
    restrictedReason shouldBe expected.restrictedReason.takeIf { it.isNotEmpty() }
    negotiatedRateEnabled shouldBe true
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
