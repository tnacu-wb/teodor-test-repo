package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.HotelRestriction

const val OPERA_RESTRICTIONS_STUB_ID = "booking.opera.restrictions"

private const val RESTRICTIONS_MAX_RANGE_DAYS = 90

/**
 * Builds Opera restrictions reads for each hotel with declared restrictions, one mapping per
 * <=90-day chunk of the Booking's stay range, mirroring OHIP's date-range splitting. Each chunk
 * responds with the restrictions overlapping it.
 *
 * The Opera query params are asymmetric by design: `restrictionSearchCriteriaStartDate` for the
 * start but plain `end` for the end.
 */
fun restrictionsByDateRange(booking: Booking): PlannedStub {
    val arrival = requireNotNull(booking.arrival) { "restrictions stubs need Booking.arrival" }
    val departure = requireNotNull(booking.departure) { "restrictions stubs need Booking.departure" }

    return PlannedStub(
        id = OPERA_RESTRICTIONS_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            booking.hotels
                .filter { hotel -> hotel.restrictions.isNotEmpty() }
                .flatMap { hotel ->
                    operaLimitedIntervals(arrival, departure, RESTRICTIONS_MAX_RANGE_DAYS)
                        .map { interval -> restrictionsMapping(hotel, interval) }
                },
    )
}

internal fun restrictionsMapping(
    hotel: Hotel,
    interval: OperaDateInterval,
): StubMapping {
    val overlapping =
        hotel.restrictions.filter { restriction ->
            !restriction.start.isAfter(interval.endDate) && !restriction.end.isBefore(interval.startDate)
        }

    return StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/par/v1/hotels/${hotel.hotelId}/restrictions",
                queryParameters =
                    mapOf(
                        "restrictionSearchCriteriaStartDate" to
                            StringValuePattern(equalTo = interval.startDate.toString()),
                        "end" to StringValuePattern(equalTo = interval.endDate.toString()),
                    ),
                headers = hotelHeaders(hotel.hotelId),
            ),
        response =
            jsonResponse(
                jsonBody =
                    stubJsonObject(
                        "restrictionsByDateRange" to
                            mapOf(
                                "restrictionsByDateRange" to
                                    mapOf(
                                        "restrictionSets" to overlapping.map(::restrictionSet),
                                        "hotelId" to hotel.hotelId,
                                        "hasMore" to false,
                                    ),
                            ),
                        "links" to emptyList<Any>(),
                    ),
            ),
    )
}

private fun restrictionSet(restriction: HotelRestriction): Map<String, Any?> =
    mapOf(
        "restrictionControl" to
            mapOf(
                "house" to (restriction.roomType == null && restriction.ratePlanCode == null),
                "roomType" to restriction.roomType,
                "roomClass" to null,
                "ratePlanCode" to restriction.ratePlanCode,
                "ratePlanCategory" to null,
            ),
        "restrictionStatus" to mapOf("code" to restriction.status, "unit" to 0),
        "actualTimeSpan" to
            mapOf(
                "startDate" to restriction.start.toString(),
                "endDate" to restriction.end.toString(),
            ),
        "onRequest" to false,
        "start" to restriction.start.toString(),
        "end" to restriction.end.toString(),
        "sunday" to true,
        "monday" to true,
        "tuesday" to true,
        "wednesday" to true,
        "thursday" to true,
        "friday" to true,
        "saturday" to true,
    )
