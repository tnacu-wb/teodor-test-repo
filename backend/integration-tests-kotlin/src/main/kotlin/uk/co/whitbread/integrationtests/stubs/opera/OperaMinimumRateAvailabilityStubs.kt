package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.BodyPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import java.math.BigDecimal
import java.time.LocalDate
import java.time.temporal.ChronoUnit

const val OPERA_MINIMUM_RATE_AVAILABILITY_STUB_ID = "booking.opera.minimum-rate-availability"

/**
 * Models Opera's minimum-rate property search
 * (`POST /parext/v1/hotels/minimumRateAvailability`): the cheapest sellable offer per
 * hotel for a stay window, with an availability status derived from room stock.
 *
 * One mapping is installed per Opera date interval of the stay (stays over the adapter's
 * 90-day window split into overlapping intervals whose nightly rates join up), so a long
 * stay exercises the adapter's interval merge against per-interval responses. Every room
 * stay always carries both `availability` and `minimumRate` because the adapter's result
 * mapper dereferences both unconditionally.
 *
 * A hotel reports `AvailableForSale` while it has room stock over the interval, and
 * `NoAvailability` when its every room type is sold out; the minimum rate is the
 * cheapest declared rate's nightly price times the interval's nights.
 */
fun minimumRateAvailability(booking: Booking): PlannedStub {
    val arrival = requireNotNull(booking.arrival) { "booking.arrival is required for minimum-rate stubs" }
    val departure = requireNotNull(booking.departure) { "booking.departure is required for minimum-rate stubs" }
    val intervals =
        operaLimitedIntervals(arrival, departure, requestedDays = 89)
            .mapIndexed { index, interval ->
                if (index == 0) interval else interval.copy(startDate = interval.startDate.minusDays(1))
            }

    return PlannedStub(
        id = OPERA_MINIMUM_RATE_AVAILABILITY_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = intervals.map { interval -> minimumRateMapping(booking, interval) },
    )
}

private fun minimumRateMapping(
    booking: Booking,
    interval: OperaDateInterval,
): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "POST",
                urlPath = "/parext/v1/hotels/minimumRateAvailability",
                headers = hotelHeaders(booking.hotel.hotelId),
                bodyPatterns =
                    listOf(
                        BodyPattern(matchesJsonPath = "$[?(@.arrivalDate == '${interval.startDate}')]"),
                        BodyPattern(matchesJsonPath = "$[?(@.departureDate == '${interval.endDate}')]"),
                        BodyPattern(matchesJsonPath = "$[?(@.hotelIds[0] == '${booking.hotel.hotelId}')]"),
                        BodyPattern(matchesJsonPath = "$.rooms"),
                    ),
            ),
        response =
            jsonResponse(
                jsonBody =
                    stubJsonObject(
                        "roomStays" to booking.hotels.map { hotel -> minimumRateRoomStay(hotel, interval) },
                        "offset" to 0,
                        "limit" to booking.hotels.size,
                        "hasMore" to false,
                        "totalResults" to booking.hotels.size,
                    ),
            ),
    )

private fun minimumRateRoomStay(
    hotel: Hotel,
    interval: OperaDateInterval,
): Map<String, Any?> {
    val nights = ChronoUnit.DAYS.between(interval.startDate, interval.endDate)
    val cheapestNightlyRate =
        hotel.availableRates.minOfOrNull { rate -> BigDecimal.valueOf(rate.nightlyRate) }
            ?: BigDecimal.ZERO

    return mapOf(
        "propertyInfo" to
            mapOf(
                "hotelCode" to hotel.hotelId,
                "hotelName" to hotel.name,
                "chainCode" to hotel.chainCode,
            ),
        "availability" to if (hotel.hasRoomStock(interval.startDate, interval.endDate)) "AvailableForSale" else "NoAvailability",
        "minimumRate" to
            mapOf(
                "amountAfterTax" to cheapestNightlyRate.multiply(BigDecimal.valueOf(nights)),
                "currencyCode" to hotel.currency,
            ),
    )
}

/** True while at least one room type keeps stock on every night of the interval. */
private fun Hotel.hasRoomStock(
    startDate: LocalDate,
    endDate: LocalDate,
): Boolean =
    availableRoomTypes.any { roomType ->
        generateSequence(startDate) { date -> date.plusDays(1) }
            .takeWhile { date -> !date.isAfter(endDate) }
            .all { date ->
                val periodCount =
                    roomType.inventoryPeriods
                        .singleOrNull { period ->
                            !date.isBefore(period.startDate) && !date.isAfter(period.endDate)
                        }?.numberOfRooms
                (periodCount ?: roomType.numberOfRooms) > 0
            }
    }
