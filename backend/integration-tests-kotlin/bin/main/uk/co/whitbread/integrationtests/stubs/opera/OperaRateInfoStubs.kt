package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.Rate
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.temporal.ChronoUnit

/** Opera's context and type values for a reservation-scoped rate-info lookup. */
private const val OPERA_ID_CONTEXT = "OPERA"
private const val RESERVATION_RATE_INFO_TYPE = "Reservation"

/** UK VAT divisor used to derive a net amount from a gross nightly rate. */
private val VAT_MULTIPLIER = BigDecimal("1.2")

const val OPERA_RATE_INFO_STUB_ID = "booking.opera.rate-info"
const val OPERA_RESERVATION_AMOUNTS_STUB_ID = "booking.opera.reservation-amounts"

/** Builds a rate-info mapping for an explicitly supplied [rate]. */
fun rateInfo(
    booking: Booking,
    rate: Rate,
): PlannedStub = rateInfo(booking, listOf(rate))

fun rateInfo(
    booking: Booking,
    rates: List<Rate>,
): PlannedStub =
    PlannedStub(
        id = OPERA_RATE_INFO_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = rateInfoMappings(booking, booking.hotel, rates),
    )

/**
 * Builds price-breakdown rate-info mappings for every hotel that declares rates, so
 * multi-hotel availability searches price each hotel's rooms against that hotel's own
 * rate catalogue.
 */
fun rateInfoForAllHotels(booking: Booking): PlannedStub =
    PlannedStub(
        id = OPERA_RATE_INFO_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            booking.hotels
                .filter { hotel -> hotel.availableRates.isNotEmpty() }
                .flatMap { hotel -> rateInfoMappings(booking, hotel, hotel.availableRates) },
    )

private fun rateInfoMappings(
    booking: Booking,
    hotel: Hotel,
    rates: List<Rate>,
): List<StubMapping> {
    val arrival = requireNotNull(booking.arrival)
    val departure = requireNotNull(booking.departure)
    val intervals =
        operaLimitedIntervals(arrival, departure, requestedDays = 20)
            .mapIndexed { index, interval ->
                if (index == 0) interval else interval.copy(startDate = interval.startDate.minusDays(1))
            }

    return rates.flatMap { rate ->
        intervals.flatMap { interval -> rateInfoMappingVariants(booking, hotel, rate, interval) }
    }
}

// Two exactly-pinned mappings per tuple and interval: price-breakdown sends
// summaryInfo=true while rate-code-pricing sends no summaryInfo at all. Pinning each
// caller's full request shape means a caller that regresses the parameter stops
// matching instead of being silently served.
private fun rateInfoMappingVariants(
    booking: Booking,
    hotel: Hotel,
    rate: Rate,
    interval: OperaDateInterval,
): List<StubMapping> =
    listOf(
        StringValuePattern(equalTo = "true"),
        StringValuePattern(absent = true),
    ).map { summaryInfoPattern ->
        StubMapping(
            request =
                RequestPattern(
                    method = "GET",
                    urlPath = "/rsv/v1/hotels/${hotel.hotelId}/reservations/rateInfo",
                    queryParameters =
                        mapOf(
                            "criteriaStartDate" to StringValuePattern(equalTo = interval.startDate.toString()),
                            "criteriaEndDate" to StringValuePattern(equalTo = interval.endDate.toString()),
                            "adults" to StringValuePattern(equalTo = rate.adults.toString()),
                            "children" to StringValuePattern(equalTo = rate.children.toString()),
                            "ratePlanCode" to StringValuePattern(equalTo = rate.ratePlan),
                            "roomType" to StringValuePattern(equalTo = rate.roomType),
                            "summaryInfo" to summaryInfoPattern,
                        ),
                ),
            response =
                jsonResponse(
                    jsonBody = rateInfoBody(hotel, rate, interval),
                ),
        )
    }

/** Builds an Opera rate-info response from the configured flat nightly rate. */
private fun rateInfoBody(
    hotel: Hotel,
    rate: Rate,
    interval: OperaDateInterval,
): kotlinx.serialization.json.JsonObject {
    var grossTotal = BigDecimal.ZERO
    var netTotal = BigDecimal.ZERO

    val details =
        generateSequence(interval.startDate) { it.plusDays(1) }
            .takeWhile { it.isBefore(interval.endDate) }
            .map { date ->
                val net = BigDecimal.valueOf(rate.nightlyRate)
                val gross = net.divide(BigDecimal("1.2"), 2, RoundingMode.HALF_UP)
                val revenue = gross
                val tax = net.subtract(gross)

                grossTotal += gross
                netTotal += net

                mapOf(
                    "summaryDate" to date.toString(),
                    "revenue" to revenue,
                    "package" to 0,
                    "tax" to tax,
                    "gross" to gross,
                    "net" to net,
                    "ratePlanCode" to rate.ratePlan,
                    "currencyCode" to hotel.currency,
                )
            }.toList()

    return stubJsonObject(
        "summary" to
            mapOf(
                "details" to details,
                "gross" to grossTotal,
                "net" to netTotal,
                "currencyCode" to hotel.currency,
                "start" to interval.startDate.toString(),
                "end" to interval.endDate.toString(),
                "hasSuppressedRate" to false,
            ),
        "links" to emptyList<Any>(),
    )
}

/**
 * Builds the Opera reservation-amount mapping OHIP calls when `rateInfoNeeded` is on.
 *
 * This is the stub that decides what a guest is charged: `ohip-adapter-service` reduces the
 * `summary` below into `rateInfo.summary.totalCostOfStay` on its basket response, which the
 * Payment Orchestration Service converts to Datatrans minor units.
 *
 * Booking fields consumed, and how:
 *
 * | Field | Use |
 * | --- | --- |
 * | `hotel.hotelId` | request path, so each hotel's reservations match their own mapping |
 * | `room.reservationId` | `id` query parameter, one mapping per reservation |
 * | `arrival`, `departure` | night count behind every amount, plus `start` and `end` |
 * | `hotel.availableRates[].nightlyRate` | rate matched to the room by [selectedRateOrNull]; nightly rate times nights is the stay total |
 * | `hotel.currency` | `currencyCode`, which drives the payment service's minor-unit exponent |
 * | `room.amountAlreadyPaid` | negative Opera `deposit` and the reduction from the outstanding stay cost |
 *
 * The default `amountAlreadyPaid` is zero, so a pre-payment booking has the full stay cost
 * outstanding. A non-zero value represents the same reusable part-paid state as the reservation
 * folio stub; no caller-specific replacement is needed. A zero is emitted as a scale-0 `0` (see
 * [operaAmount]), which is what Opera sends and what the downstream zero checks need to see.
 *
 * Unlike the sibling [rateInfo] builder, a room with no matching rate is rejected here instead
 * of falling back to a zero nightly rate: a silent zero would travel all the way to the gateway
 * as an amount of `0` and a journey would assert it happily.
 *
 * The total this returns is proved end to end by the Datatrans stub matching on the converted
 * minor-unit amount, so a wrong figure stops matching and fails its journey. A journey cannot
 * read request bodies from the scenario-owned journal, only call counts, so keep that matcher in
 * place: it is the only thing that checks the conversion.
 *
 * Every amount OHIP reduces must be present and non-null. It sums them with `BigDecimal::add`
 * and negates `deposit`, so an omitted field surfaces as a NullPointerException inside the
 * adapter rather than as a readable stub failure.
 */
fun reservationAmounts(
    booking: Booking,
    room: BookingRoom,
): PlannedStub = reservationAmounts(booking, listOf(room))

fun reservationAmounts(
    booking: Booking,
    rooms: List<BookingRoom>,
): PlannedStub =
    PlannedStub(
        id = OPERA_RESERVATION_AMOUNTS_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = rooms.flatMap { room -> reservationAmountsMappingVariants(booking, room) },
    )

// Two exactly-pinned mappings per room: basket flows send idContext=OPERA while the
// amend detail read sends no idContext at all. Pinning each caller's full request shape
// means a caller that regresses the parameter stops matching instead of being
// silently served.
private fun reservationAmountsMappingVariants(
    booking: Booking,
    room: BookingRoom,
): List<StubMapping> {
    val reservationId =
        room.reservationId
            ?: error("BookingRoom.reservationId must be configured for reservation-amount stubs")
    val rate =
        selectedRateOrNull(booking, room)
            ?: error(
                "booking.hotel.availableRates must contain a rate matching room " +
                    "$reservationId (roomType=${room.roomType}, adults=${room.adults}, " +
                    "children=${room.children}) for reservation-amount stubs",
            )

    return listOf(
        StringValuePattern(equalTo = OPERA_ID_CONTEXT),
        StringValuePattern(absent = true),
    ).map { idContextPattern ->
        StubMapping(
            request =
                RequestPattern(
                    method = "GET",
                    urlPath = "/rsv/v1/hotels/${booking.hotel.hotelId}/reservations/rateInfo",
                    // Disjoint from the criteria-based rateInfo mapping: that one requires
                    // criteriaStartDate and roomType, which this call never sends, and this one
                    // requires id, which the criteria call never sends. Neither can consume the
                    // other's request. summaryInfo=true keeps it disjoint from the city-tax
                    // detail mapping.
                    queryParameters =
                        mapOf(
                            "idContext" to idContextPattern,
                            "id" to StringValuePattern(equalTo = reservationId),
                            "summaryInfo" to StringValuePattern(equalTo = "true"),
                            "type" to StringValuePattern(equalTo = RESERVATION_RATE_INFO_TYPE),
                        ),
                ),
            response = jsonResponse(jsonBody = reservationAmountsBody(booking, room, rate)),
        )
    }
}

/** Builds the reservation-amount summary from the room's nightly rate and stay length. */
private fun reservationAmountsBody(
    booking: Booking,
    room: BookingRoom,
    rate: Rate,
): kotlinx.serialization.json.JsonObject {
    val nights = ChronoUnit.DAYS.between(booking.arrival!!, booking.departure!!)
    val total = BigDecimal.valueOf(rate.nightlyRate).multiply(BigDecimal.valueOf(nights))
    val net = total.divide(VAT_MULTIPLIER, 2, RoundingMode.HALF_UP)
    val amountAlreadyPaid = operaAmount(room.amountAlreadyPaid)
    require(amountAlreadyPaid <= total) {
        "BookingRoom.amountAlreadyPaid must not exceed the reservation total"
    }
    val outstanding = total.subtract(amountAlreadyPaid)

    return stubJsonObject(
        "summary" to
            mapOf(
                // Required even though the payment path reads only the aggregates: the basket
                // response mapper streams details unconditionally, so omitting it fails the whole
                // lookup with a null-pointer message that names neither this stub nor the field.
                "details" to nightlyAmountDetails(booking, rate),
                "gross" to total,
                "net" to net,
                // Opera reports deposits as a negative amount; OHIP negates this while reducing
                // reservation summaries.
                "deposit" to amountAlreadyPaid.negate(),
                "totalCostOfStay" to total,
                "outStandingCostOfStay" to outstanding,
                "currencyCode" to booking.hotel.currency,
                "start" to booking.arrival.toString(),
                "end" to booking.departure.toString(),
                "hasSuppressedRate" to false,
            ),
        "links" to emptyList<Any>(),
    )
}

/**
 * Converts a `BookingRoom` money fact into the `BigDecimal` Opera would put on the wire.
 *
 * Opera reports a whole amount with no fractional part as a scale-0 number (`0`, not `0.0`), and
 * the value's scale survives every hop: [stubJsonObject] renders a `Number` through
 * `JsonPrimitive`/`toString`, `ohip-adapter-service` reduces the deposit with
 * `BigDecimal.ZERO.add(deposit.negate())` — which keeps the larger scale — and
 * hotel-reservation-entity-service then tests the reduced `amountPaid` with the scale-sensitive
 * `BigDecimal.ZERO.equals(...)`. `BigDecimal.valueOf(0.0)` yields scale 1, so a nothing-paid room
 * would otherwise put `"deposit": 0.0` on the wire and no downstream zero check could ever see a
 * real zero. Routing every amount through here keeps a zero fact a scale-0 zero while leaving
 * every fractional amount exactly as `BigDecimal.valueOf` renders it.
 */
private fun operaAmount(value: Double): BigDecimal = if (value == 0.0) BigDecimal.ZERO else BigDecimal.valueOf(value)

/** Builds one amount detail per night of the stay. */
private fun nightlyAmountDetails(
    booking: Booking,
    rate: Rate,
): List<Map<String, Any?>> {
    val nightly = BigDecimal.valueOf(rate.nightlyRate)
    val nightlyNet = nightly.divide(VAT_MULTIPLIER, 2, RoundingMode.HALF_UP)

    return generateSequence(booking.arrival!!) { date -> date.plusDays(1) }
        .takeWhile { date -> date.isBefore(booking.departure!!) }
        .map { date ->
            mapOf(
                "summaryDate" to date.toString(),
                "revenue" to nightlyNet,
                "package" to BigDecimal.ZERO,
                "tax" to nightly.subtract(nightlyNet),
                "gross" to nightly,
                "net" to nightlyNet,
                "ratePlanCode" to rate.ratePlan,
                "currencyCode" to booking.hotel.currency,
            )
        }.toList()
}

const val OPERA_CITY_TAX_RATE_INFO_STUB_ID = "booking.opera.city-tax-rate-info"

private const val CITYTAX_PACKAGE_CODE = "CITYTAX"

/**
 * Builds reservation-scoped rate-info detail mappings for CITYTAX consumption dates.
 *
 * Disjoint from [rateInfo] (stay criteria, no `id`) and [reservationAmounts]
 * (`id` + `summaryInfo=true` without `detailDate`). The cancel CITYTAX path calls
 * `summaryInfo=false` with `id` and `detailDate` equal to each CITYTAX schedule date.
 *
 * The VAT detail is priced from the same room's CITYTAX package, so the amount Opera reports here
 * and the amount the reservation read posts stay one Booking fact.
 */
fun cityTaxRateInfo(
    booking: Booking,
    rooms: List<BookingRoom>,
): PlannedStub {
    val arrival =
        requireNotNull(booking.arrival) {
            "booking.arrival must be configured for city-tax rate-info stubs"
        }
    return PlannedStub(
        id = OPERA_CITY_TAX_RATE_INFO_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            rooms.map { room ->
                val reservationId =
                    requireNotNull(room.reservationId) {
                        "BookingRoom.reservationId must be configured for city-tax rate-info stubs"
                    }
                val cityTaxPackage =
                    requireNotNull(room.selectedPackages.firstOrNull { it.code == CITYTAX_PACKAGE_CODE }) {
                        "BookingRoom.selectedPackages must carry a CITYTAX package for city-tax rate-info stubs"
                    }
                cityTaxRateInfoMapping(booking, reservationId, arrival.toString(), operaPackagePrice(cityTaxPackage))
            },
    )
}

/** Opera's UK VAT share of a city-tax amount, at the standard 20% rate and Opera money precision. */
private fun cityTaxVat(amountBeforeTax: BigDecimal): BigDecimal =
    amountBeforeTax.multiply(CITY_TAX_VAT_RATE).setScale(2, RoundingMode.HALF_UP)

private val CITY_TAX_VAT_RATE = BigDecimal("0.20")

private fun cityTaxRateInfoMapping(
    booking: Booking,
    reservationId: String,
    detailDate: String,
    amountBeforeTax: BigDecimal,
): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/rsv/v1/hotels/${booking.hotel.hotelId}/reservations/rateInfo",
                queryParameters =
                    mapOf(
                        "id" to StringValuePattern(equalTo = reservationId),
                        "detailDate" to StringValuePattern(equalTo = detailDate),
                        "summaryInfo" to StringValuePattern(equalTo = "false"),
                        "type" to StringValuePattern(equalTo = RESERVATION_RATE_INFO_TYPE),
                    ),
            ),
        response =
            jsonResponse(
                jsonBody =
                    stubJsonObject(
                        "detail" to
                            mapOf(
                                "packages" to
                                    listOf(
                                        mapOf(
                                            "code" to CITYTAX_PACKAGE_CODE,
                                            "amountBeforeTax" to amountBeforeTax,
                                            "taxes" to
                                                mapOf(
                                                    "tax" to listOf(mapOf("amount" to cityTaxVat(amountBeforeTax))),
                                                ),
                                        ),
                                    ),
                            ),
                    ),
            ),
    )

/**
 * Resolves the catalogue rate currently booked for [room].
 *
 * Room type and occupancy are sufficient while they identify at most one rate. When several
 * catalogue rates share those facts, [BookingRoom.ratePlan] is the reusable reservation fact that
 * disambiguates them. Ambiguous world data is rejected instead of depending on catalogue order.
 */
internal fun selectedRateOrNull(
    booking: Booking,
    room: BookingRoom = booking.room,
): Rate? {
    val matchingRates =
        booking.hotel.availableRates.filter { rate ->
            rate.roomType == room.roomType &&
                rate.adults == room.adults &&
                rate.children == room.children &&
                (room.ratePlan == null || rate.ratePlan == room.ratePlan)
        }
    require(matchingRates.size <= 1) {
        "bookingRoom.ratePlan must identify at most one available rate for " +
            "roomType=${room.roomType}, adults=${room.adults}, children=${room.children}; " +
            "matched rate plans=${matchingRates.joinToString { rate -> rate.ratePlan }}"
    }
    return matchingRates.singleOrNull()
}
