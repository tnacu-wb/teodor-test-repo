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
import uk.co.whitbread.integrationtests.testkit.model.Rate
import java.math.BigDecimal
import java.time.temporal.ChronoUnit

const val OPERA_MULTI_HOTEL_AVAILABILITY_STUB_ID = "booking.opera.multi-hotel-availability"
const val OPERA_MULTI_HOTEL_NEGOTIATED_AVAILABILITY_STUB_ID =
    "booking.opera.multi-hotel-negotiated-availability"

/** Rate plan set reserved for company-negotiated rates, served by the negotiated stub instead. */
internal const val NEGOTIATED_RATE_PLAN_SET = "NEGOTIATED"
private const val MULTI_HOTEL_BATCH_SIZE = 5

/**
 * Models Opera's cross-hotel availability search (`GET /par/v1/availability`).
 *
 * Two mutually exclusive request families share the URL, discriminated by the query
 * parameters the real callers send:
 *
 * - **ratePlanSet-shaped** — one mapping per rate plan set the hotels' public rates
 *   declare (excluding [NEGOTIATED_RATE_PLAN_SET]), plus a mandatory empty fallback for
 *   any other set, because the adapter always fans out over both `PBN` and `PBF`.
 * - **ratePlanCode-shaped** — one mapping per non-empty public rate-plan subset, plus
 *   an empty fallback for lists with no configured code, for searches that name
 *   explicit rate plans.
 *
 * Rate-plan-set mappings answer for one five-hotel caller batch. Rate-plan-code mappings
 * answer for all hotels in the Booking. A hotel whose rates match the requested selector
 * returns those rates; any other hotel returns an empty room stay so the adapter can mark
 * it unavailable.
 */
fun multiHotelAvailability(booking: Booking): PlannedStub {
    val publicRates =
        booking.hotels.associateWith { hotel ->
            hotel.availableRates.filter { rate ->
                rate.promotionCode == null && rate.ratePlanSet != NEGOTIATED_RATE_PLAN_SET
            }
        }
    val configuredSets =
        publicRates.values
            .flatten()
            .mapNotNull { rate -> rate.ratePlanSet?.takeIf(String::isNotBlank) }
            .distinct()
    val configuredPlans =
        publicRates.values
            .flatten()
            .map(Rate::ratePlan)
            .distinct()
    val hotelBatches = booking.hotels.chunked(MULTI_HOTEL_BATCH_SIZE)

    val setMappings =
        configuredSets.flatMap { ratePlanSet ->
            hotelBatches.map { hotels ->
                multiHotelMapping(
                    booking = booking,
                    hotels = hotels,
                    selectorName = "ratePlanSet",
                    selectorMatcher = StringValuePattern(equalTo = ratePlanSet),
                    ratesForHotel = { hotel ->
                        publicRates.getValue(hotel).filter { rate -> rate.ratePlanSet == ratePlanSet }
                    },
                    responseRatePlanSet = ratePlanSet,
                )
            }
        }
    val setFallbacks =
        hotelBatches.map { hotels ->
            multiHotelMapping(
                booking = booking,
                hotels = hotels,
                selectorName = "ratePlanSet",
                selectorMatcher = StringValuePattern(matches = otherMultiSelectorValues(configuredSets)),
                ratesForHotel = { emptyList() },
                responseRatePlanSet = null,
            )
        }
    val planMappings =
        configuredPlans.nonEmptySubsets().map { selectedPlans ->
            multiHotelMapping(
                booking = booking,
                selectorName = "ratePlanCode",
                selectorMatcher =
                    StringValuePattern(
                        matches = configuredPlanSubsetMatcher(selectedPlans, configuredPlans),
                    ),
                ratesForHotel = { hotel ->
                    publicRates.getValue(hotel).filter { rate -> rate.ratePlan in selectedPlans }
                },
                responseRatePlanSet = null,
            )
        }
    val planFallback =
        multiHotelMapping(
            booking = booking,
            selectorName = "ratePlanCode",
            selectorMatcher = StringValuePattern(matches = noConfiguredPlanMatcher(configuredPlans)),
            ratesForHotel = { emptyList() },
            responseRatePlanSet = null,
        )

    return PlannedStub(
        id = OPERA_MULTI_HOTEL_AVAILABILITY_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = setMappings + setFallbacks + planMappings + planFallback,
    )
}

/**
 * Models Opera's company-negotiated cross-hotel availability search: the same
 * `GET /par/v1/availability` URL, reached with `reservationProfileType=Company` and the
 * company's Opera profile id as `attachedProfileId` instead of a rate selector.
 *
 * Returns each hotel's [NEGOTIATED_RATE_PLAN_SET] rates; hotels without negotiated rates
 * come back with an empty room stay.
 */
fun multiHotelNegotiatedAvailability(booking: Booking): PlannedStub =
    PlannedStub(
        id = OPERA_MULTI_HOTEL_NEGOTIATED_AVAILABILITY_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            booking.companies.map { company ->
                multiHotelMapping(
                    booking = booking,
                    selectorName = "attachedProfileId",
                    selectorMatcher = StringValuePattern(equalTo = company.companyId),
                    ratesForHotel = { hotel ->
                        hotel.availableRates.filter { rate ->
                            rate.ratePlanSet == NEGOTIATED_RATE_PLAN_SET
                        }
                    },
                    responseRatePlanSet = NEGOTIATED_RATE_PLAN_SET,
                    extraParameters =
                        mapOf(
                            "reservationProfileType" to StringValuePattern(equalTo = "Company"),
                        ),
                )
            },
    )

private fun multiHotelMapping(
    booking: Booking,
    hotels: List<Hotel> = booking.hotels,
    selectorName: String,
    selectorMatcher: StringValuePattern,
    ratesForHotel: (Hotel) -> List<Rate>,
    responseRatePlanSet: String?,
    extraParameters: Map<String, StringValuePattern> = emptyMap(),
): StubMapping {
    val arrival = requireNotNull(booking.arrival) { "booking.arrival is required for availability stubs" }
    val departure = requireNotNull(booking.departure) { "booking.departure is required for availability stubs" }

    return StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/par/v1/availability",
                queryParameters =
                    mapOf(
                        "hotelIds" to exactValues(hotels.map(Hotel::hotelId)),
                        "limit" to StringValuePattern(equalTo = "20"),
                        "roomStayStartDate" to StringValuePattern(equalTo = arrival.toString()),
                        "roomStayEndDate" to StringValuePattern(equalTo = departure.toString()),
                        "roomStayQuantity" to StringValuePattern(matches = ".+"),
                        selectorName to selectorMatcher,
                    ) + extraParameters,
                headers =
                    mapOf(
                        "x-hubid" to StringValuePattern(matches = ".+"),
                    ),
            ),
        response =
            jsonResponse(
                jsonBody =
                    stubJsonObject(
                        "hotelAvailability" to
                            hotels.map { hotel ->
                                hotelSegment(
                                    booking = booking,
                                    hotel = hotel,
                                    rates = ratesForHotel(hotel),
                                    ratePlanSet = responseRatePlanSet,
                                )
                            },
                        "links" to emptyList<Any>(),
                    ),
            ),
    )
}

private fun exactValues(values: List<String>): StringValuePattern =
    StringValuePattern(
        hasExactly = values.map { value -> StringValuePattern(equalTo = value) },
    )

private fun hotelSegment(
    booking: Booking,
    hotel: Hotel,
    rates: List<Rate>,
    ratePlanSet: String?,
): Map<String, Any?> =
    buildMap {
        put(
            "roomStays",
            listOf(
                mapOf(
                    "roomRates" to rates.map { rate -> multiHotelRoomRate(booking, hotel, rate) },
                ),
            ),
        )
        if (rates.isNotEmpty() && ratePlanSet != null) {
            put("ratePlanSet", ratePlanSet)
        }
        put("hotelId", hotel.hotelId)
        put("closed", false)
        put("redemption", false)
        put("hasMore", false)
    }

private fun multiHotelRoomRate(
    booking: Booking,
    hotel: Hotel,
    rate: Rate,
): Map<String, Any?> {
    val arrival = requireNotNull(booking.arrival)
    val departure = requireNotNull(booking.departure)
    val nights = ChronoUnit.DAYS.between(arrival, departure)
    val nightlyRate = BigDecimal.valueOf(rate.nightlyRate)
    val stayTotal = nightlyRate.multiply(BigDecimal.valueOf(nights))
    val numberOfUnits =
        hotel.availableRoomTypes
            .firstOrNull { roomType -> roomType.roomType == rate.roomType }
            ?.numberOfRooms ?: 1

    return mapOf(
        // The multi-hotel result mapper multiplies total.amountBeforeTax by numberOfUnits,
        // so the stay total is emitted per unit and numberOfUnits is echoed separately.
        "total" to
            mapOf(
                "amountBeforeTax" to stayTotal,
                "currencyCode" to hotel.currency,
            ),
        "rates" to
            mapOf(
                "rate" to
                    generateSequence(arrival) { date -> date.plusDays(1) }
                        .takeWhile { date -> date.isBefore(departure) }
                        .map { date ->
                            mapOf(
                                "base" to mapOf("amountBeforeTax" to nightlyRate),
                                "total" to mapOf("amountBeforeTax" to nightlyRate),
                                "effectiveRate" to mapOf("amountBeforeTax" to nightlyRate),
                                "start" to date.toString(),
                                "end" to date.toString(),
                            )
                        }.toList(),
            ),
        "roomType" to rate.roomType,
        "ratePlanCode" to rate.ratePlan,
        "start" to arrival.toString(),
        "end" to departure.toString(),
        "suppressRate" to false,
        "marketCode" to "OTH",
        "numberOfUnits" to if (rate.ratePlanSet == NEGOTIATED_RATE_PLAN_SET) numberOfUnits else 1,
    )
}

private fun configuredPlanSubsetMatcher(
    selected: Collection<String>,
    configured: Collection<String>,
): String {
    val required = selected.joinToString("") { value -> tokenLookahead(value, required = true) }
    val excluded = configured.filterNot(selected::contains).joinToString("") { value -> tokenLookahead(value, required = false) }
    return "^$required$excluded.+$"
}

private fun noConfiguredPlanMatcher(configured: Collection<String>): String {
    if (configured.isEmpty()) {
        return ".+"
    }
    val alternatives = configured.joinToString("|") { value -> Regex.escape(value) }
    return "^(?!.*(?:^|,)(?:$alternatives)(?:,|$)).+$"
}

private fun tokenLookahead(
    value: String,
    required: Boolean,
): String {
    val prefix = if (required) "?=" else "?!"
    return "($prefix.*(?:^|,)${Regex.escape(value)}(?:,|$))"
}

/** Returns every non-empty subset while preserving configured plan order. */
private fun <T> List<T>.nonEmptySubsets(): List<List<T>> =
    fold(listOf(emptyList<T>())) { subsets, item ->
        subsets + subsets.map { subset -> subset + item }
    }.filter { subset -> subset.isNotEmpty() }

private fun otherMultiSelectorValues(configured: Collection<String>): String {
    if (configured.isEmpty()) {
        return ".+"
    }
    val alternatives = configured.joinToString("|") { value -> Regex.escape(value) }
    return "^(?!(?:$alternatives)$).+$"
}
