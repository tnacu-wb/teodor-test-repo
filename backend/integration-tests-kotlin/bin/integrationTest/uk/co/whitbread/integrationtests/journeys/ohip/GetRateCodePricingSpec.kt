package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.RateCodePricingRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RATE_INFO_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.RateInfoFailureSelector
import uk.co.whitbread.integrationtests.stubs.opera.custom.rateInfoFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

private val rateCodePricingArrival: LocalDate = LocalDate.now().plusDays(17)

// The flow doc lists no flags on this path. The Opera token-service flag is evaluated
// outside the request context, so featureFlagOverrides cannot reach it - never pin it.
private val rateCodePricingFlagPins = mapOf<FeatureFlag, Boolean>()

/**
 * Proves that GET /ohip/hotels/{hotelId}/rate-code-pricing prices one rate plan over a stay from
 * Opera's criteria-shaped rate-info lookups, aggregating every requested room tuple and every
 * split interval, and maps an Opera rejection to the rate-code rate-info error.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/GetRateCodePricing.md
 */
class GetRateCodePricingSpec :
    JourneySpec(
        "A rate code can be priced for a stay through OHIP adapter service",
        {
            val ohipApi = OhipApi()

            scenario("a rate code is priced for one room tuple over the stay") {
                val booking = rateCodePricingBooking()
                val rate = booking.hotel.availableRates.single()

                installFor(booking)

                val result =
                    ohipApi.getRateCodePricing(
                        request = rateCodePricingRequest(booking),
                        testId = testId,
                        featureFlagOverrides = rateCodePricingFlagPins,
                    )

                result.attachEvidence("Get Rate Code Pricing")

                expect("returns the aggregated net cost of the stay for the requested rate plan") {
                    result.response.status.value shouldBe 200
                    result.body.ratePlanCode shouldBe rate.ratePlan
                    result.body.totalNetAmount shouldBe rate.nightlyRate * 2
                    result.body.currencyCode shouldBe booking.hotel.currency
                }

                expect("reads Opera rate information once for the requested room tuple") {
                    // One Opera call: GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo in its
                    // criteria shape (six criteria parameters, no summaryInfo and no reservation id).
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    // Installed on this availability-shaped Booking but never called by this endpoint.
                    callCount(OperaEndpoint.GET_MULTI_HOTEL_AVAILABILITY) shouldBe 0
                    callCount(OperaEndpoint.FIND_MINIMUM_RATE_AVAILABILITY) shouldBe 0
                    callCount(OperaEndpoint.FIND_MULTI_ROOM_RATE_AVAILABILITY) shouldBe 0
                    callCount(OperaEndpoint.GET_HOTEL_INVENTORY) shouldBe 0
                    callCount(OperaEndpoint.GET_RATE_PLANS) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("totals are aggregated across every requested room tuple") {
                val booking = multiTupleRateCodePricingBooking()
                val rates = booking.hotel.availableRates

                installFor(booking)

                val result =
                    ohipApi.getRateCodePricing(
                        request = rateCodePricingRequest(booking),
                        testId = testId,
                        featureFlagOverrides = rateCodePricingFlagPins,
                    )

                result.attachEvidence("Get Rate Code Pricing Multiple Tuples")

                expect("sums the net cost of every requested room tuple") {
                    result.response.status.value shouldBe 200
                    result.body.ratePlanCode shouldBe rates.first().ratePlan
                    result.body.totalNetAmount shouldBe rates.sumOf { rate -> rate.nightlyRate } * 2
                    result.body.currencyCode shouldBe booking.hotel.currency
                }

                expect("reads Opera rate information once for each requested room tuple") {
                    // Two Opera calls: one criteria-shaped
                    // GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo per requested tuple.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 2
                    callCount(OperaEndpoint.GET_MULTI_HOTEL_AVAILABILITY) shouldBe 0
                    callCount(OperaEndpoint.FIND_MINIMUM_RATE_AVAILABILITY) shouldBe 0
                    callCount(OperaEndpoint.FIND_MULTI_ROOM_RATE_AVAILABILITY) shouldBe 0
                    callCount(OperaEndpoint.GET_HOTEL_INVENTORY) shouldBe 0
                    callCount(OperaEndpoint.GET_RATE_PLANS) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a stay longer than the Opera window is priced from merged split intervals") {
                val booking = longStayRateCodePricingBooking()
                val rate = booking.hotel.availableRates.single()

                installFor(booking)

                val result =
                    ohipApi.getRateCodePricing(
                        request = rateCodePricingRequest(booking),
                        testId = testId,
                        featureFlagOverrides = rateCodePricingFlagPins,
                    )

                result.attachEvidence("Get Rate Code Pricing Long Stay")

                expect("returns the net cost of the whole stay from the merged intervals") {
                    result.response.status.value shouldBe 200
                    result.body.ratePlanCode shouldBe rate.ratePlan
                    result.body.totalNetAmount shouldBe rate.nightlyRate * 22
                    result.body.currencyCode shouldBe booking.hotel.currency
                }

                expect("reads Opera rate information once for each split interval") {
                    // Two Opera calls: the stay exceeds the 20-day rate-info window, so
                    // GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo is split into two intervals.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 2
                    callCount(OperaEndpoint.GET_MULTI_HOTEL_AVAILABILITY) shouldBe 0
                    callCount(OperaEndpoint.FIND_MINIMUM_RATE_AVAILABILITY) shouldBe 0
                    callCount(OperaEndpoint.FIND_MULTI_ROOM_RATE_AVAILABILITY) shouldBe 0
                    callCount(OperaEndpoint.GET_HOTEL_INVENTORY) shouldBe 0
                    callCount(OperaEndpoint.GET_RATE_PLANS) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera rate-info rejection on a later tuple returns the rate-code error instead of a partial total") {
                val booking = multiTupleRateCodePricingBooking()
                val rates = booking.hotel.availableRates

                installFor(booking, excluded = setOf(OPERA_RATE_INFO_STUB_ID))
                installStub(
                    rateInfoFailure(
                        booking = booking,
                        rates = rates,
                        selector = RateInfoFailureSelector(rates.last()),
                    ),
                )

                val result =
                    ohipApi.getRateCodePricing(
                        request = rateCodePricingRequest(booking),
                        testId = testId,
                        featureFlagOverrides = rateCodePricingFlagPins,
                    )

                result.attachEvidence("Get Rate Code Pricing Later Tuple Opera Failure")

                expect("maps the Opera rejection to the rate-code rate-info error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 904
                }

                expect("calls Opera once for each room tuple through the failure") {
                    // Two Opera calls: the first tuple's rate-info lookup succeeds and the second
                    // GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo is rejected.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 2
                    callCount(OperaEndpoint.GET_MULTI_HOTEL_AVAILABILITY) shouldBe 0
                    callCount(OperaEndpoint.FIND_MINIMUM_RATE_AVAILABILITY) shouldBe 0
                    callCount(OperaEndpoint.FIND_MULTI_ROOM_RATE_AVAILABILITY) shouldBe 0
                    callCount(OperaEndpoint.GET_HOTEL_INVENTORY) shouldBe 0
                    callCount(OperaEndpoint.GET_RATE_PLANS) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun rateCodePricingRequest(booking: Booking): RateCodePricingRequest {
    val rates = booking.hotel.availableRates

    return RateCodePricingRequest(
        hotelId = booking.hotel.hotelId,
        arrivalDate = requireNotNull(booking.arrival),
        departureDate = requireNotNull(booking.departure),
        ratePlanCode = rates.first().ratePlan,
        roomTypes = rates.map { rate -> rate.roomType },
        adultsNo = rates.map { rate -> rate.adults },
        childrenNo = rates.map { rate -> rate.children },
    )
}

private fun rateCodePricingBooking(): Booking =
    availabilityBooking(
        hotelId = "RCP01",
        shortId = "RC1",
        rates =
            listOf(
                Rate(
                    ratePlan = "RCP72",
                    ratePlanSet = "PUBLIC",
                    roomType = "LOWDBL",
                    adults = 2,
                    children = 0,
                    nightlyRate = 72.0,
                ),
            ),
        departure = rateCodePricingArrival.plusDays(2),
    )

private fun multiTupleRateCodePricingBooking(): Booking =
    availabilityBooking(
        hotelId = "RCM01",
        shortId = "RCM",
        rates =
            listOf(
                Rate(
                    ratePlan = "RCPMULTI",
                    ratePlanSet = "PUBLIC",
                    roomType = "LOWDBL",
                    adults = 2,
                    children = 0,
                    nightlyRate = 72.0,
                ),
                Rate(
                    ratePlan = "RCPMULTI",
                    ratePlanSet = "PUBLIC",
                    roomType = "HIGDBL",
                    adults = 1,
                    children = 1,
                    nightlyRate = 96.0,
                ),
            ),
        departure = rateCodePricingArrival.plusDays(2),
    )

private fun longStayRateCodePricingBooking(): Booking =
    availabilityBooking(
        hotelId = "RCL01",
        shortId = "RCL",
        rates =
            listOf(
                Rate(
                    ratePlan = "RCPLONG",
                    ratePlanSet = "PUBLIC",
                    roomType = "LOWDBL",
                    adults = 2,
                    children = 0,
                    nightlyRate = 72.0,
                ),
            ),
        departure = rateCodePricingArrival.plusDays(22),
    )

/**
 * Builds the availability-shaped Booking this endpoint needs: a hotel with a rate catalogue and
 * requestable rooms carrying no reservation id, which is what gates the Opera rate-info default.
 */
private fun availabilityBooking(
    hotelId: String,
    shortId: String,
    rates: List<Rate>,
    departure: LocalDate,
): Booking =
    Booking(
        hotels =
            listOf(
                Hotels.HEAPTI.copy(
                    hotelId = hotelId,
                    shortId = shortId,
                    availableRates = rates,
                ),
            ),
        arrival = rateCodePricingArrival,
        departure = departure,
        rooms =
            rates.map { rate ->
                BookingRoom(
                    roomType = rate.roomType,
                    adults = rate.adults,
                    children = rate.children,
                )
            },
    )
