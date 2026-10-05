package uk.co.whitbread.integrationtests.journeys.companyentity

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.companyentity.CompanyEntityApi
import uk.co.whitbread.integrationtests.clients.companyentity.model.CompanyResponse
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.Company
import uk.co.whitbread.integrationtests.testkit.presets.Companies

/**
 * Integration coverage for `GET /v1/companies`.
 *
 * The endpoint searches CDH through the real company-entity-service and cdh-adapter-service
 * containers, enriches valid CDH company rows through OHIP/Opera company profile data, and then
 * optionally filters the enriched result set by negotiated-rate availability.
 */
class GetCompaniesFromCdhSpec :
    JourneySpec(
        "get companies from CDH",
        {
            /**
             * Verifies the main CDH-to-Opera enrichment path.
             *
             * The scenario proves company entity maps the public paging request into the CDH adapter
             * call, caps the CDH page size to 20, resolves the returned corporate id through Opera,
             * fetches negotiated rates, and maps profile/address fields into the public response.
             */
            scenario("GET /v1/companies returns enriched company results") {
                val company = Companies.NEILL_TECHNICAL_SERVICES
                val booking = Booking(companies = listOf(company))

                installFor(booking)

                val result =
                    CompanyEntityApi().getCompaniesFromCdh(
                        companyName = "Neill",
                        offset = 1,
                        limit = 50,
                        testId = testId,
                    )

                result.attachEvidence("Get Companies From CDH")

                expect("returns the enriched company") {
                    result.response.status.value shouldBe 200
                    result.body.totalResults shouldBe 1
                    result.body.limit shouldBe 20
                    result.body.offset shouldBe 1
                    result.body.companies shouldHaveSize 1
                }

                val actual = result.body.companies.single()

                expect("maps company profile fields") {
                    actual.shouldMatch(company)
                }

                expect("maps company address") {
                    actual.shouldMatchAddress(company)
                }
            }

            /**
             * Verifies that the default search returns both negotiated and non-negotiated companies.
             *
             * The scenario installs one company with rates and one without rates, leaves
             * `negotiatedRateCompanies=false`, and asserts both rows remain in the response while
             * preserving each company's negotiated-rate flag from Opera rate data.
             */
            scenario("GET /v1/companies returns mixed negotiated-rate companies by default") {
                val companies =
                    listOf(
                        Companies.NEILL_TECHNICAL_SERVICES,
                        Companies.ACME_WITHOUT_NEGOTIATED_RATES,
                    )
                val booking = Booking(companies = companies)

                installFor(booking)

                val result =
                    CompanyEntityApi().getCompaniesFromCdh(
                        companyName = "Company",
                        offset = 1,
                        limit = 10,
                        negotiatedRateCompanies = false,
                        testId = testId,
                    )

                result.attachEvidence("Get Companies From CDH")

                expect("returns both companies") {
                    result.response.status.value shouldBe 200
                    result.body.totalResults shouldBe 2
                    result.body.limit shouldBe 10
                    result.body.offset shouldBe 1
                    result.body.companies shouldHaveSize 2
                    result.body.companies.map { it.corpId } shouldBe companies.map { it.corpId }
                }

                expect("keeps negotiated-rate flags from Opera rates") {
                    result.body.companies.map { it.negotiatedRateEnabled } shouldBe listOf(true, false)
                }
            }

            /**
             * Verifies the `negotiatedRateCompanies=true` post-enrichment filter.
             *
             * The scenario starts with mixed Opera rate data and asserts company entity removes the
             * company with an empty negotiated-rates response after enrichment.
             */
            scenario("GET /v1/companies filters to companies with negotiated rates when requested") {
                val companyWithRates = Companies.NEILL_TECHNICAL_SERVICES
                val companyWithoutRates = Companies.ACME_WITHOUT_NEGOTIATED_RATES
                val booking = Booking(companies = listOf(companyWithRates, companyWithoutRates))

                installFor(booking)

                val result =
                    CompanyEntityApi().getCompaniesFromCdh(
                        companyName = "Company",
                        offset = 1,
                        limit = 10,
                        negotiatedRateCompanies = true,
                        testId = testId,
                    )

                result.attachEvidence("Get Companies From CDH")

                expect("returns only companies with negotiated rates") {
                    result.response.status.value shouldBe 200
                    result.body.totalResults shouldBe 1
                    result.body.limit shouldBe 10
                    result.body.offset shouldBe 1
                    result.body.companies shouldHaveSize 1
                    result.body.companies
                        .single()
                        .shouldMatch(companyWithRates)
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
    negotiatedRateEnabled shouldBe expected.negotiatedRateEnabled
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
