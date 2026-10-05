package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.Rate
import java.math.BigDecimal

const val OPERA_AVAILABILITY_STUB_ID = "booking.opera.availability"

private val positiveRoomQuantity = StringValuePattern(matches = "^[1-9][0-9]*$")

/**
 * Models Opera's single-hotel availability capability from the Booking's stay, rate, room-type,
 * inventory, and optional company facts.
 *
 * The rate-plan-set and promotion mappings serve ordinary availability searches. The
 * rate-plan-code plus room-type mappings serve reservation changes that price a requested room
 * type. When rooms declare a target room type, those mappings require the exact Booking-driven
 * grouped quantity and return rates only when inventory can satisfy it; otherwise they accept any
 * positive quantity for compatibility. Fixed-one legacy searches are inventory-aware too.
 * Company-aware selector mappings are scoped once per distinct configured company, while
 * room-type change mappings remain company-neutral. Every family follows OHIP's overlapping Opera
 * date intervals, and each family has disjoint empty-room-rate fallbacks for unknown selectors.
 */
fun hotelAvailability(booking: Booking): PlannedStub {
    val arrival = requireNotNull(booking.arrival)
    val departure = requireNotNull(booking.departure)
    val intervals =
        operaLimitedIntervals(arrival, departure, requestedDays = 89)
            .mapIndexed { index, interval ->
                if (index == 0) interval else interval.copy(startDate = interval.startDate.minusDays(1))
            }
    val standardRates = booking.hotel.availableRates.filter { rate -> rate.promotionCode == null }
    val ratesByPlanSet =
        standardRates
            .filter { rate -> !rate.ratePlanSet.isNullOrBlank() }
            .groupBy { rate -> requireNotNull(rate.ratePlanSet) }
    val ratesByPromotionCode =
        booking.hotel.availableRates
            .filter { rate -> rate.promotionCode != null }
            .groupBy { rate -> requireNotNull(rate.promotionCode) }
    val ratesByCodeAndRoomType =
        booking.hotel.availableRates.groupBy { rate -> RateAndRoomType(rate.ratePlan, rate.roomType) }
    val targetRoomQuantities =
        booking.rooms
            .mapNotNull { room -> room.roomTypeAfterUpdate }
            .groupingBy { roomType -> roomType }
            .eachCount()

    return PlannedStub(
        id = OPERA_AVAILABILITY_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            intervals.flatMap { interval ->
                legacySelectorMappings(booking, interval, ratesByPlanSet, ratesByPromotionCode) +
                    rateCodeRoomTypeMappings(
                        booking = booking,
                        interval = interval,
                        ratesByCodeAndRoomType = ratesByCodeAndRoomType,
                        targetRoomQuantities = targetRoomQuantities,
                    )
            },
    )
}

private fun legacySelectorMappings(
    booking: Booking,
    interval: OperaDateInterval,
    ratesByPlanSet: Map<String, List<Rate>>,
    ratesByPromotionCode: Map<String, List<Rate>>,
): List<StubMapping> =
    availabilityCompanyIds(booking).flatMap { companyId ->
        legacySelectorMappingsForCompany(
            booking = booking,
            interval = interval,
            ratesByPlanSet = ratesByPlanSet,
            ratesByPromotionCode = ratesByPromotionCode,
            companyId = companyId,
        )
    }

private fun legacySelectorMappingsForCompany(
    booking: Booking,
    interval: OperaDateInterval,
    ratesByPlanSet: Map<String, List<Rate>>,
    ratesByPromotionCode: Map<String, List<Rate>>,
    companyId: String?,
): List<StubMapping> {
    val configuredMappings =
        ratesByPlanSet.map { (ratePlanSet, rates) ->
            availabilityMapping(
                booking = booking,
                interval = interval,
                selectors = mapOf("ratePlanSet" to StringValuePattern(equalTo = ratePlanSet)),
                quantityMatcher = StringValuePattern(equalTo = "1"),
                requiredQuantity = 1,
                rates = rates,
                responseRatePlanSet = ratePlanSet,
                companyId = companyId,
            )
        }
    val promotionMappings =
        ratesByPromotionCode.map { (promotionCode, rates) ->
            availabilityMapping(
                booking = booking,
                interval = interval,
                selectors = mapOf("promotionCode" to StringValuePattern(equalTo = promotionCode)),
                quantityMatcher = StringValuePattern(equalTo = "1"),
                requiredQuantity = 1,
                rates = rates,
                responseRatePlanSet = null,
                companyId = companyId,
            )
        }
    val ratePlanFallback =
        availabilityMapping(
            booking = booking,
            interval = interval,
            selectors =
                mapOf(
                    "ratePlanSet" to StringValuePattern(matches = otherSelectorValues(ratesByPlanSet.keys)),
                ),
            quantityMatcher = StringValuePattern(equalTo = "1"),
            rates = emptyList(),
            responseRatePlanSet = null,
            companyId = companyId,
        )
    val promotionFallback =
        availabilityMapping(
            booking = booking,
            interval = interval,
            selectors =
                mapOf(
                    "promotionCode" to StringValuePattern(matches = otherSelectorValues(ratesByPromotionCode.keys)),
                ),
            quantityMatcher = StringValuePattern(equalTo = "1"),
            rates = emptyList(),
            responseRatePlanSet = null,
            companyId = companyId,
        )

    return configuredMappings + promotionMappings + ratePlanFallback + promotionFallback
}

private fun rateCodeRoomTypeMappings(
    booking: Booking,
    interval: OperaDateInterval,
    ratesByCodeAndRoomType: Map<RateAndRoomType, List<Rate>>,
    targetRoomQuantities: Map<String, Int>,
): List<StubMapping> {
    val configuredMappings =
        ratesByCodeAndRoomType.map { (selector, rates) ->
            val targetQuantity = targetRoomQuantities[selector.roomType]
            availabilityMapping(
                booking = booking,
                interval = interval,
                selectors =
                    mapOf(
                        "ratePlanCode" to StringValuePattern(equalTo = selector.ratePlanCode),
                        "roomType" to StringValuePattern(equalTo = selector.roomType),
                    ),
                quantityMatcher =
                    targetQuantity
                        ?.let { quantity -> StringValuePattern(equalTo = quantity.toString()) }
                        ?: positiveRoomQuantity,
                requiredQuantity = targetQuantity,
                rates = rates,
                responseRatePlanSet = rates.firstNotNullOfOrNull { rate -> rate.ratePlanSet },
                companyId = null,
            )
        }
    val roomTypesByRateCode =
        ratesByCodeAndRoomType.keys.groupBy(
            keySelector = RateAndRoomType::ratePlanCode,
            valueTransform = RateAndRoomType::roomType,
        )
    val unavailableRoomTypeMappings =
        roomTypesByRateCode.map { (ratePlanCode, roomTypes) ->
            availabilityMapping(
                booking = booking,
                interval = interval,
                selectors =
                    mapOf(
                        "ratePlanCode" to StringValuePattern(equalTo = ratePlanCode),
                        "roomType" to StringValuePattern(matches = otherSelectorValues(roomTypes.toSet())),
                    ),
                quantityMatcher = positiveRoomQuantity,
                rates = emptyList(),
                responseRatePlanSet = null,
                companyId = null,
            )
        }
    val unavailableRateCodeMapping =
        availabilityMapping(
            booking = booking,
            interval = interval,
            selectors =
                mapOf(
                    "ratePlanCode" to
                        StringValuePattern(
                            matches = otherSelectorValues(ratesByCodeAndRoomType.keys.map { it.ratePlanCode }.toSet()),
                        ),
                    "roomType" to StringValuePattern(matches = ".+"),
                ),
            quantityMatcher = positiveRoomQuantity,
            rates = emptyList(),
            responseRatePlanSet = null,
            companyId = null,
        )

    return configuredMappings + unavailableRoomTypeMappings + unavailableRateCodeMapping
}

private fun availabilityMapping(
    booking: Booking,
    interval: OperaDateInterval,
    selectors: Map<String, StringValuePattern>,
    quantityMatcher: StringValuePattern,
    requiredQuantity: Int? = null,
    rates: List<Rate>,
    responseRatePlanSet: String?,
    companyId: String?,
): StubMapping {
    val hotel = booking.hotel
    val availableRates =
        rates.filter { rate ->
            requiredQuantity == null ||
                availabilityCapacity(booking, interval, rate.roomType) >= requiredQuantity
        }

    return StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/par/v1/hotels/${hotel.hotelId}/availability",
                queryParameters =
                    buildMap {
                        put("roomStayStartDate", StringValuePattern(equalTo = interval.startDate.toString()))
                        put("roomStayEndDate", StringValuePattern(equalTo = interval.endDate.toString()))
                        put("roomStayQuantity", quantityMatcher)
                        putAll(selectors)
                        put("limit", StringValuePattern(equalTo = "20"))
                        put("reservationGuestIdType", StringValuePattern(equalTo = "Profile"))
                        companyId?.let { put("reservationGuestId", StringValuePattern(equalTo = it)) }
                    },
                headers = hotelHeaders(hotel.hotelId),
            ),
        response =
            jsonResponse(
                jsonBody = availabilityBody(booking, interval, availableRates, responseRatePlanSet),
            ),
    )
}

private fun availabilityCompanyIds(booking: Booking): List<String?> =
    if (booking.companies.isEmpty()) {
        listOf(null)
    } else {
        booking.companies.map { company -> company.companyId }.distinct()
    }

private fun availabilityBody(
    booking: Booking,
    interval: OperaDateInterval,
    rates: List<Rate>,
    ratePlanSet: String?,
) = if (rates.isEmpty()) {
    stubJsonObject(
        "hotelAvailability" to
            listOf(
                mapOf(
                    "roomStays" to
                        listOf(
                            mapOf(
                                "roomRates" to emptyList<Any>(),
                            ),
                        ),
                    "hotelId" to booking.hotel.hotelId,
                    "closed" to false,
                    "redemption" to false,
                    "hasMore" to false,
                ),
            ),
        "links" to emptyList<Any>(),
    )
} else {
    stubJsonObject(
        "hotelAvailability" to
            listOf(
                buildMap {
                    put(
                        "roomStays",
                        listOf(
                            mapOf(
                                "roomRates" to rates.map { rate -> availabilityRate(booking, interval, rate) },
                            ),
                        ),
                    )
                    ratePlanSet?.let { put("ratePlanSet", it) }
                    put("hotelId", booking.hotel.hotelId)
                    put("closed", false)
                    put("redemption", false)
                    put("hasMore", false)
                },
            ),
        "links" to emptyList<Any>(),
    )
}

private fun availabilityRate(
    booking: Booking,
    interval: OperaDateInterval,
    rate: Rate,
): Map<String, Any?> {
    val nights =
        generateSequence(interval.startDate) { date -> date.plusDays(1) }
            .takeWhile { date -> date.isBefore(interval.endDate) }
            .toList()
    val nightlyRate = BigDecimal.valueOf(rate.nightlyRate)
    val total = nightlyRate.multiply(BigDecimal.valueOf(nights.size.toLong()))
    val numberOfUnits = availabilityCapacity(booking, interval, rate.roomType)

    return buildMap {
        put("total", amountBeforeTax(total))
        put(
            "rates",
            mapOf(
                "rate" to
                    nights.map { date ->
                        mapOf(
                            "base" to amountBeforeTax(nightlyRate),
                            "total" to amountBeforeTax(nightlyRate),
                            "effectiveRate" to amountBeforeTax(nightlyRate),
                            "start" to date.toString(),
                            "end" to date.toString(),
                        )
                    },
            ),
        )
        put("roomType", rate.roomType)
        put("ratePlanCode", rate.ratePlan)
        rate.promotionCode?.let { promotionCode -> put("promotionCode", promotionCode) }
        put("start", interval.startDate.toString())
        put("end", interval.endDate.toString())
        put("suppressRate", false)
        put("marketCode", "OTH")
        put("numberOfUnits", numberOfUnits)
    }
}

private fun availabilityCapacity(
    booking: Booking,
    interval: OperaDateInterval,
    roomTypeCode: String,
): Int {
    if (!interval.startDate.isBefore(interval.endDate)) {
        return 0
    }

    return booking.hotel.availableRoomTypes
        .firstOrNull { roomType -> roomType.roomType == roomTypeCode }
        ?.minimumAvailableRooms(
            startDate = interval.startDate,
            endDateInclusive = interval.endDate.minusDays(1),
        ) ?: 0
}

private fun amountBeforeTax(amount: BigDecimal): Map<String, BigDecimal> = mapOf("amountBeforeTax" to amount)

private fun otherSelectorValues(configured: Set<String>): String {
    if (configured.isEmpty()) {
        return ".+"
    }

    val alternatives = configured.joinToString("|") { value -> Regex.escape(value) }
    return "^(?!(?:$alternatives)$).+$"
}

private data class RateAndRoomType(
    val ratePlanCode: String,
    val roomType: String,
)
