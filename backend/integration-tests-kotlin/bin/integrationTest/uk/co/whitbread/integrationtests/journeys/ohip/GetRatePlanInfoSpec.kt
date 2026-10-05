package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RATE_PLAN_INFO_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.ratePlanInfoNotFound
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.presets.Hotels

// Both Opera token-service paths are environment-pinned OFF for this endpoint.
private val ratePlanInfoFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
        OhipFeatureFlag.USE_TOKEN_REFRESH_SKEW to false,
    )

/**
 * Proves GET /ohip/ratePlanInfo maps one promotional Opera rate plan and translates an
 * Opera not-found response to the adapter's rate-plan-info error.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/GetRatePlanInfo.md
 */
class GetRatePlanInfoSpec :
    JourneySpec(
        "OHIP adapter returns rate-plan details",
        {
            val ohipApi = OhipApi()

            scenario("a promotional rate plan returns its dynamic base-rate relationship") {
                val booking = ratePlanInfoBooking()
                val rate = booking.hotel.availableRates.single()

                installFor(booking)

                val result =
                    ohipApi.getRatePlanInfo(
                        ratePlanCode = rate.ratePlan,
                        hotelId = booking.hotel.hotelId,
                        testId = testId,
                        featureFlagOverrides = ratePlanInfoFlagPins,
                    )

                result.attachEvidence("Get Rate Plan Info")

                expect("returns the requested promotional rate-plan details") {
                    result.response.status.value shouldBe 200
                    result.body.ratePlanInfo
                        .single()
                        .hotelId shouldBe booking.hotel.hotelId
                    result.body.ratePlanInfo
                        .single()
                        .ratePlanCode shouldBe rate.ratePlan
                    result.body.ratePlanInfo
                        .single()
                        .ratePlanBasedOnRates
                        .single()
                        .dynamicBaseRate
                        ?.dynamicBasedOnRatePlan shouldBe rate.dynamicBaseRatePlan
                }

                expect("reads the rate plan exactly once from Opera") {
                    // One Opera call: GET /rtp/v1/hotels/{hotelId}/ratePlans/{ratePlanCode}.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_PLAN) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera not-found response maps to the rate-plan-info error") {
                val booking = ratePlanInfoBooking()
                val rate = booking.hotel.availableRates.single()

                installFor(booking, excluded = setOf(OPERA_RATE_PLAN_INFO_STUB_ID))
                installStub(ratePlanInfoNotFound(booking.hotel, rate))

                val result =
                    ohipApi.getRatePlanInfo(
                        ratePlanCode = rate.ratePlan,
                        hotelId = booking.hotel.hotelId,
                        testId = testId,
                        featureFlagOverrides = ratePlanInfoFlagPins,
                    )

                result.attachEvidence("Get Rate Plan Info Opera Error")

                expect("returns the mapped rate-plan-info error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 973
                }

                expect("attempts the rejected rate-plan read exactly once") {
                    // One rejected Opera call: GET /rtp/v1/hotels/{hotelId}/ratePlans/{ratePlanCode}.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_PLAN) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun ratePlanInfoBooking(): Booking {
    val rate =
        Rate(
            ratePlan = "PROMOFLEX",
            roomType = "DOUBLE",
            adults = 2,
            promotionCode = "SUMMER20",
            dynamicBaseRatePlan = "FLEXRATE",
        )
    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
    )
}
