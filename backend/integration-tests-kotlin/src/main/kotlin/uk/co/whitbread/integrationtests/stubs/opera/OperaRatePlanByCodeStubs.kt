package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.Rate

const val OPERA_RATE_PLAN_INFO_STUB_ID = "booking.opera.rate-plan-info"

/** Models Opera rate-plan details for one promotional [rate] offered by [hotel]. */
fun ratePlanInfo(
    hotel: Hotel,
    rate: Rate,
): PlannedStub = ratePlanInfo(hotel, listOf(rate))

/** Models Opera rate-plan details for the promotional [rates] offered by [hotel]. */
fun ratePlanInfo(
    hotel: Hotel,
    rates: List<Rate>,
): PlannedStub =
    PlannedStub(
        id = OPERA_RATE_PLAN_INFO_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = rates.map { rate -> ratePlanInfoMapping(hotel, rate) },
    )

private fun ratePlanInfoMapping(
    hotel: Hotel,
    rate: Rate,
): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/rtp/v1/hotels/${hotel.hotelId}/ratePlans/${rate.ratePlan}",
                headers =
                    authenticatedHotelHeaders(hotel.hotelId) +
                        mapOf(
                            "ratePlanCode" to
                                StringValuePattern(equalTo = rate.ratePlan),
                        ),
            ),
        response =
            jsonResponse(
                jsonBody =
                    stubJsonObject(
                        "ratePlans" to
                            listOf(
                                mapOf(
                                    "hotelId" to hotel.hotelId,
                                    "ratePlanCode" to rate.ratePlan,
                                    "ratePlanBasedOnRates" to ratePlanBasedOnRates(rate),
                                ),
                            ),
                        "links" to emptyList<Any>(),
                    ),
            ),
    )

private fun ratePlanBasedOnRates(rate: Rate): List<Map<String, Any>> =
    rate.dynamicBaseRatePlan
        ?.let { dynamicBaseRatePlan ->
            listOf(
                mapOf(
                    "dynamicBaseRate" to
                        mapOf(
                            "dynamicBasedOnRatePlan" to dynamicBaseRatePlan,
                            "dynamicBaseAmount" to 0,
                            "flatOrPercentage" to "PCT",
                        ),
                ),
            )
        }.orEmpty()
