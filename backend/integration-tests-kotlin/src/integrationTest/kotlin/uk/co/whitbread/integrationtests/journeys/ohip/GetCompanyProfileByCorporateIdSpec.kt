package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.presets.Companies

// Both Opera token-service paths are environment-pinned OFF for this endpoint.
private val corporateProfileFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
        OhipFeatureFlag.USE_TOKEN_REFRESH_SKEW to false,
    )

/**
 * Proves GET /ohip/v1/profile/company/{corporateId} maps a successful empty Opera company
 * response without falling back to a profile lookup or negotiated-rate enrichment.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/GetCompanyProfileByCorporateId.md
 */
class GetCompanyProfileByCorporateIdSpec :
    JourneySpec(
        "OHIP adapter returns a company profile by corporate id",
        {
            val ohipApi = OhipApi()

            scenario("an unknown corporate id returns an empty company without fallback reads") {
                val company = Companies.NEILL_TECHNICAL_SERVICES
                val booking = Booking(companies = listOf(company))

                installFor(booking)

                val result =
                    ohipApi.getCompanyProfileByCorporateId(
                        corporateId = company.companyId,
                        testId = testId,
                        featureFlagOverrides = corporateProfileFlagPins,
                    )

                result.attachEvidence("Get Empty Company Profile By Corporate Id")

                expect("returns the null-safe empty company profile") {
                    result.response.status.value shouldBe 200
                    result.body.name shouldBe null
                    result.body.corpId shouldBe ""
                    result.body.companyId shouldBe ""
                }

                expect("reads only the requested corporate profile from Opera") {
                    // One Opera call: GET /crm/v1/companies/{corporateId}.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_COMPANY) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.GET_NEGOTIATED_RATES) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )
