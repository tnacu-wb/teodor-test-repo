package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.HotelCancellationPolicyRule

const val OPERA_POLICY_SCHEDULES_STUB_ID = "booking.opera.policy-schedules"

/**
 * Builds Opera policy-schedules mappings for each hotel with cancellation policy rules.
 *
 * Each hotel gets one catch-all mapping serving an empty schedule list, installed first, plus one
 * mapping per configured rate plan installed after it. WireMock serves the most recently added
 * match, so a configured rate plan gets its schedule and any other rate plan gets `[]` rather
 * than an unmatched 404 — real Opera answers unknown rate plans with an empty list too.
 */
fun policySchedules(hotels: List<Hotel>): PlannedStub =
    PlannedStub(
        id = OPERA_POLICY_SCHEDULES_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            hotels.flatMap { hotel ->
                listOf(emptyPolicySchedulesMapping(hotel)) +
                    hotel.cancellationPolicyRules.map { rule -> policyScheduleMapping(hotel, rule) }
            },
    )

private fun policySchedulesUrl(hotel: Hotel): String = "/rsv/config/v1/hotels/${hotel.hotelId}/policyschedules"

private fun emptyPolicySchedulesMapping(hotel: Hotel): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = policySchedulesUrl(hotel),
                headers = hotelHeaders(hotel.hotelId),
            ),
        response =
            jsonResponse(
                jsonBody = stubJsonObject("policySchedules" to emptyList<Any>()),
            ),
    )

private fun policyScheduleMapping(
    hotel: Hotel,
    rule: HotelCancellationPolicyRule,
): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = policySchedulesUrl(hotel),
                queryParameters =
                    mapOf(
                        "policyType" to StringValuePattern(equalTo = "Cancellation"),
                        "ratePlanCodes" to StringValuePattern(equalTo = rule.ratePlanCode),
                    ),
                headers = hotelHeaders(hotel.hotelId),
            ),
        response =
            jsonResponse(
                jsonBody =
                    stubJsonObject(
                        "policySchedules" to
                            listOf(
                                mapOf(
                                    "scheduleId" to mapOf("id" to "1", "type" to "PolicyScheduleId"),
                                    "scheduleDetail" to
                                        mapOf(
                                            "policy" to
                                                mapOf(
                                                    "code" to rule.policyCode,
                                                    "description" to rule.description,
                                                ),
                                            "applicableCodes" to mapOf("ratePlanCodes" to listOf(rule.ratePlanCode)),
                                            "sequence" to 1,
                                            "override" to false,
                                            "inactive" to false,
                                        ),
                                    "hotelId" to hotel.hotelId,
                                    "policyType" to "Cancellation",
                                ),
                            ),
                        "totalPages" to 1,
                        "offset" to 0,
                        "limit" to 20,
                        "hasMore" to false,
                        "totalResults" to 1,
                        "count" to 1,
                    ),
            ),
    )
