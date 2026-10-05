package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Hotel

const val OPERA_CANCEL_POLICY_CONFIGS_STUB_ID = "booking.opera.cancel-policy-configs"

/** Builds the Opera cancel-policies configuration read for each hotel with cancellation policy rules. */
fun cancelPolicyConfigs(hotels: List<Hotel>): PlannedStub =
    PlannedStub(
        id = OPERA_CANCEL_POLICY_CONFIGS_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = hotels.map(::cancelPolicyConfigMapping),
    )

private fun cancelPolicyConfigMapping(hotel: Hotel): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/rsv/config/v1/cancelpolicies",
                queryParameters = mapOf("hotelIds" to StringValuePattern(equalTo = hotel.hotelId)),
                headers = hotelHeaders(hotel.hotelId),
            ),
        response =
            jsonResponse(
                jsonBody =
                    stubJsonObject(
                        "cancelPenalties" to
                            mapOf(
                                "cancelPenalties" to
                                    listOf(
                                        mapOf(
                                            "hotelId" to hotel.hotelId,
                                            "cancelPenaltyConfig" to
                                                hotel.cancellationPolicyRules.map { rule ->
                                                    mapOf(
                                                        "policyCode" to rule.policyCode,
                                                        "penaltyDescription" to rule.description,
                                                        "deadline" to
                                                            mapOf(
                                                                "offsetFromArrival" to rule.offsetFromArrivalDays,
                                                                // Date field; only the time-of-day survives mapping.
                                                                "offsetDropTime" to "1970-01-01T${rule.offsetDropTime}Z",
                                                            ),
                                                        "manual" to false,
                                                        "nonRefundable" to false,
                                                        "sequence" to 1,
                                                        "inactive" to false,
                                                    )
                                                },
                                        ),
                                    ),
                                "totalPages" to 1,
                                "offset" to 0,
                                "limit" to 20,
                                "hasMore" to false,
                                "totalResults" to hotel.cancellationPolicyRules.size,
                                "count" to hotel.cancellationPolicyRules.size,
                            ),
                    ),
            ),
    )
