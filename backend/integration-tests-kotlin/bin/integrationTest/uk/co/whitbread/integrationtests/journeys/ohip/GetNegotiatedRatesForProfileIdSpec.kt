package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_COMPANY_NEGOTIATED_RATES_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.companyNegotiatedRatesNotFound
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.presets.Companies

// Both Opera token-service paths are environment-pinned OFF for this endpoint.
private val negotiatedRatesFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
        OhipFeatureFlag.USE_TOKEN_REFRESH_SKEW to false,
    )

/**
 * Proves GET /ohip/{profileId}/negotiatedRates preserves the negotiated hotel and rate-plan
 * identity and maps an Opera rejection to the adapter's negotiated-rate error.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/GetNegotiatedRatesForProfileId.md
 */
class GetNegotiatedRatesForProfileIdSpec :
    JourneySpec(
        "OHIP adapter returns negotiated rates for a company profile",
        {
            val ohipApi = OhipApi()

            scenario("a company profile with negotiated rates returns the mapped rate") {
                val company = Companies.NEILL_TECHNICAL_SERVICES
                val booking = Booking(companies = listOf(company))

                installFor(booking)

                val result =
                    ohipApi.getNegotiatedRates(
                        profileId = company.companyId,
                        testId = testId,
                        featureFlagOverrides = negotiatedRatesFlagPins,
                    )

                result.attachEvidence("Get Negotiated Rates")

                expect("returns the negotiated hotel and rate-plan code") {
                    result.response.status.value shouldBe 200
                    result.body.negotiatedRates
                        .single()
                        .hotelId shouldBe "DUBSOU"
                    result.body.negotiatedRates
                        .single()
                        .ratePlanCode shouldBe "BUSIFLEX"
                }

                expect("reads negotiated rates exactly once from Opera") {
                    // One Opera call: GET /rtp/v1/profiles/{profileId}/negotiatedRates.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_NEGOTIATED_RATES) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera rejection maps to the negotiated-rate error") {
                val company = Companies.NEILL_TECHNICAL_SERVICES
                val booking = Booking(companies = listOf(company))

                installFor(booking, excluded = setOf(OPERA_COMPANY_NEGOTIATED_RATES_STUB_ID))
                installStub(companyNegotiatedRatesNotFound(company))

                val result =
                    ohipApi.getNegotiatedRates(
                        profileId = company.companyId,
                        testId = testId,
                        featureFlagOverrides = negotiatedRatesFlagPins,
                    )

                result.attachEvidence("Get Negotiated Rates Opera Error")

                expect("returns the mapped negotiated-rate error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 929
                }

                expect("attempts one rejected negotiated-rate read") {
                    // One rejected Opera call: GET /rtp/v1/profiles/{profileId}/negotiatedRates.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_NEGOTIATED_RATES) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )
