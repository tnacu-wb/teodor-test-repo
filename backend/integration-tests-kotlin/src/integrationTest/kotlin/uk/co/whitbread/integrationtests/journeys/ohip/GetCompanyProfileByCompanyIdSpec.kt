package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_COMPANY_PROFILE_BY_ID_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_COMPANY_PROFILE_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.companyProfileByIdFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.companyProfileFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.presets.Companies

private val companyProfileFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
    )

/**
 * Proves the OHIP adapter's company lookup by company id:
 * `GET /ohip/v1/profile/company/id/{companyId}` reads the Opera profile for the company id and,
 * only when that profile carries a CorporateId link, chains a second Opera read of the corporate
 * profile to build the full company response. An unlinked profile skips the second read and
 * returns an empty company profile rather than an error.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/GetCompanyProfileByCompanyId.md
 */
class GetCompanyProfileByCompanyIdSpec :
    JourneySpec(
        "OHIP adapter returns a company profile by company id",
        {
            val ohipApi = OhipApi()

            scenario("a linked profile chains the corporate read and returns the full company") {
                val company = Companies.NEILL_TECHNICAL_SERVICES
                val booking = Booking(companies = listOf(company))

                installFor(booking)

                val result =
                    ohipApi.getCompanyProfileByCompanyId(
                        companyId = company.companyId,
                        testId = testId,
                        featureFlagOverrides = companyProfileFlagPins,
                    )

                result.attachEvidence("Get Company Profile Linked")

                expect("returns the corporate profile's company details") {
                    result.response.status.value shouldBe 200
                    result.body.name shouldBe company.name
                    result.body.corpId shouldBe company.corpId
                    result.body.telephoneNumber shouldBe company.telephoneNumber
                    result.body.active shouldBe true
                }

                expect("reads the profile and then the corporate profile from Opera") {
                    // Two Opera calls: GET profiles/{companyId}, GET companies/{corporateId}.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.GET_COMPANY) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an unlinked profile skips the corporate read and returns an empty company") {
                val company = Companies.NEILL_TECHNICAL_SERVICES.copy(corporateIdLinked = false)
                val booking = Booking(companies = listOf(company))

                installFor(booking)

                val result =
                    ohipApi.getCompanyProfileByCompanyId(
                        companyId = company.companyId,
                        testId = testId,
                        featureFlagOverrides = companyProfileFlagPins,
                    )

                result.attachEvidence("Get Company Profile Unlinked")

                expect("returns an empty company profile rather than an error") {
                    result.response.status.value shouldBe 200
                    // The empty CompanyProfileDto omits name and blanks the identifiers.
                    result.body.name shouldBe null
                    result.body.corpId shouldBe ""
                }

                expect("reads only the profile, never the corporate profile") {
                    // One Opera call: GET profiles/{companyId}; the corporate read is skipped.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected profile read maps to the company-profile error before the corporate read") {
                val company = Companies.NEILL_TECHNICAL_SERVICES
                val booking = Booking(companies = listOf(company))

                installFor(booking, excluded = setOf(OPERA_COMPANY_PROFILE_BY_ID_STUB_ID))
                installStub(companyProfileByIdFailure(company))

                val result =
                    ohipApi.getCompanyProfileByCompanyId(
                        companyId = company.companyId,
                        testId = testId,
                        featureFlagOverrides = companyProfileFlagPins,
                    )

                result.attachEvidence("Get Company Profile First Read Error")

                expect("returns the mapped company-profile error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 924
                }

                expect("never reached the corporate read") {
                    // Distinguished from the second-read failure below only by call count.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected corporate read maps to the same error instead of the empty-profile fallback") {
                val company = Companies.NEILL_TECHNICAL_SERVICES
                val booking = Booking(companies = listOf(company))

                installFor(booking, excluded = setOf(OPERA_COMPANY_PROFILE_STUB_ID))
                installStub(companyProfileFailure(company))

                val result =
                    ohipApi.getCompanyProfileByCompanyId(
                        companyId = company.companyId,
                        testId = testId,
                        featureFlagOverrides = companyProfileFlagPins,
                    )

                result.attachEvidence("Get Company Profile Second Read Error")

                expect("returns the mapped company-profile error, not the unlinked empty profile") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 924
                }

                expect("attempted both Opera reads") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.GET_COMPANY) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )
