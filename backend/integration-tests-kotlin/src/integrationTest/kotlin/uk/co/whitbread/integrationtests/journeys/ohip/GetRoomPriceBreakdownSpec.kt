package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.RoomPriceBreakdownRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RATE_INFO_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.RateInfoFailureSelector
import uk.co.whitbread.integrationtests.stubs.opera.custom.rateInfoFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate
import java.time.temporal.ChronoUnit

private val priceBreakdownArrival: LocalDate = LocalDate.now().plusDays(17)
private val priceBreakdownDeparture: LocalDate = priceBreakdownArrival.plusDays(2)

private val priceBreakdownFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
    )

/**
 * Flow: [GetRoomPriceBreakdown](../../../../../../../../../flows/ohip-adapter-service/GetRoomPriceBreakdown.md)
 */
class GetRoomPriceBreakdownSpec :
    JourneySpec(
        "A room price breakdown can be fetched from OHIP adapter service",
        {
            val ohipApi = OhipApi()

            scenario("a room price breakdown returns Opera nightly and full-stay totals") {
                val booking = roomPriceBreakdownBooking()
                val expectedRate = booking.hotel.availableRates.single()
                val requestedRoom = booking.room
                val expectedArrival = requireNotNull(booking.arrival)
                val expectedDeparture = requireNotNull(booking.departure)
                val expectedNights =
                    ChronoUnit.DAYS
                        .between(
                            expectedArrival,
                            expectedDeparture,
                        ).toDouble()
                val expectedNetTotal = expectedRate.nightlyRate * expectedNights
                val expectedGrossPerNight = expectedRate.nightlyRate / 1.2
                val expectedGrossTotal = expectedGrossPerNight * expectedNights

                installFor(booking)

                val result =
                    ohipApi.getRoomPriceBreakdown(
                        request =
                            RoomPriceBreakdownRequest(
                                hotelId = booking.hotel.hotelId,
                                arrivalDate = expectedArrival,
                                departureDate = expectedDeparture,
                                ratePlanCode = expectedRate.ratePlan,
                                roomTypes = listOf(requireNotNull(requestedRoom.roomType)),
                                adultsNo = listOf(requireNotNull(requestedRoom.adults)),
                                childrenNo = listOf(requestedRoom.children),
                            ),
                        testId = testId,
                        featureFlagOverrides = priceBreakdownFlagPins,
                    )

                result.attachEvidence("Get Room Price Breakdown")

                expect("returns the nightly prices and full-stay totals from Opera") {
                    result.response.status.value shouldBe 200
                    val priceBreakdown = result.body.priceBreakdown.single()

                    priceBreakdown.totalNetAmount shouldBe expectedNetTotal
                    priceBreakdown.totalGrossAmount shouldBe expectedGrossTotal
                    priceBreakdown.totalTaxAmount shouldBe expectedNetTotal - expectedGrossTotal
                    priceBreakdown.currencyCode shouldBe booking.hotel.currency
                    priceBreakdown.baseRateAmount.shouldBeNull()
                    priceBreakdown.effectiveRateAmount.shouldBeNull()
                    priceBreakdown.dailyPrices.map { dailyPrice -> dailyPrice.date } shouldContainExactly
                        listOf(expectedArrival.toString(), expectedArrival.plusDays(1).toString())
                    priceBreakdown.dailyPrices.map { dailyPrice -> dailyPrice.netPrice } shouldContainExactly
                        listOf(expectedRate.nightlyRate, expectedRate.nightlyRate)
                    priceBreakdown.dailyPrices.map { dailyPrice -> dailyPrice.grossPrice } shouldContainExactly
                        listOf(expectedGrossPerNight, expectedGrossPerNight)
                }

                expect("reads only the requested rate information from Opera") {
                    // One Opera call: the rate-info lookup for the requested room and occupancy tuple.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("multiple room tuples return price breakdowns in request order") {
                val booking = multipleRoomPriceBreakdownBooking()
                val expectedArrival = requireNotNull(booking.arrival)
                val expectedDeparture = requireNotNull(booking.departure)
                val rates = booking.hotel.availableRates

                installFor(booking)

                val result =
                    ohipApi.getRoomPriceBreakdown(
                        request =
                            RoomPriceBreakdownRequest(
                                hotelId = booking.hotel.hotelId,
                                arrivalDate = expectedArrival,
                                departureDate = expectedDeparture,
                                ratePlanCode = rates.first().ratePlan,
                                roomTypes = rates.map { rate -> rate.roomType },
                                adultsNo = rates.map { rate -> rate.adults },
                                childrenNo = rates.map { rate -> rate.children },
                            ),
                        testId = testId,
                        featureFlagOverrides = priceBreakdownFlagPins,
                    )

                result.attachEvidence("Get Multiple Room Price Breakdowns")

                expect("preserves tuple order in the returned price breakdowns") {
                    result.response.status.value shouldBe 200
                    result.body.priceBreakdown.map { breakdown -> breakdown.totalNetAmount } shouldContainExactly
                        rates.map { rate -> rate.nightlyRate * 2 }
                    result.body.priceBreakdown.map { breakdown -> breakdown.currencyCode } shouldContainExactly
                        rates.map { booking.hotel.currency }
                    result.body.priceBreakdown.map { breakdown ->
                        breakdown.dailyPrices.map { dailyPrice -> dailyPrice.netPrice }
                    } shouldContainExactly
                        rates.map { rate -> listOf(rate.nightlyRate, rate.nightlyRate) }
                }

                expect("reads rate information once for each requested room tuple") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a long stay recombines split Opera rate information") {
                val booking = longStayRoomPriceBreakdownBooking()
                val expectedRate = booking.hotel.availableRates.single()
                val requestedRoom = booking.room
                val expectedArrival = requireNotNull(booking.arrival)
                val expectedDeparture = requireNotNull(booking.departure)
                val expectedDates =
                    generateSequence(expectedArrival) { date -> date.plusDays(1) }
                        .takeWhile { date -> date.isBefore(expectedDeparture) }
                        .map(LocalDate::toString)
                        .toList()
                val expectedGrossPerNight = expectedRate.nightlyRate / 1.2

                installFor(booking)

                val result =
                    ohipApi.getRoomPriceBreakdown(
                        request =
                            RoomPriceBreakdownRequest(
                                hotelId = booking.hotel.hotelId,
                                arrivalDate = expectedArrival,
                                departureDate = expectedDeparture,
                                ratePlanCode = expectedRate.ratePlan,
                                roomTypes = listOf(requireNotNull(requestedRoom.roomType)),
                                adultsNo = listOf(requireNotNull(requestedRoom.adults)),
                                childrenNo = listOf(requestedRoom.children),
                            ),
                        testId = testId,
                        featureFlagOverrides = priceBreakdownFlagPins,
                    )

                result.attachEvidence("Get Long-stay Room Price Breakdown")

                expect("recombines every nightly price and full-stay total") {
                    result.response.status.value shouldBe 200
                    val priceBreakdown = result.body.priceBreakdown.single()

                    priceBreakdown.totalNetAmount shouldBe expectedRate.nightlyRate * 22
                    priceBreakdown.totalGrossAmount shouldBe expectedGrossPerNight * 22
                    priceBreakdown.totalTaxAmount shouldBe
                        (expectedRate.nightlyRate - expectedGrossPerNight) * 22
                    priceBreakdown.currencyCode shouldBe booking.hotel.currency
                    priceBreakdown.dailyPrices.map { dailyPrice -> dailyPrice.date } shouldContainExactly expectedDates
                    priceBreakdown.dailyPrices.map { dailyPrice -> dailyPrice.netPrice } shouldContainExactly
                        List(22) { expectedRate.nightlyRate }
                    priceBreakdown.dailyPrices.map { dailyPrice -> dailyPrice.grossPrice } shouldContainExactly
                        List(22) { expectedGrossPerNight }
                }

                expect("reads both Opera rate-info intervals") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera rate-info failure is returned as an OHIP error") {
                val booking = roomPriceBreakdownBooking()
                val rate = booking.hotel.availableRates.single()

                installFor(booking, excluded = setOf(OPERA_RATE_INFO_STUB_ID))
                installStub(
                    rateInfoFailure(
                        booking = booking,
                        rates = listOf(rate),
                        selector = RateInfoFailureSelector(rate),
                    ),
                )

                val result =
                    ohipApi.getRoomPriceBreakdown(
                        request = priceBreakdownRequest(booking, listOf(rate)),
                        testId = testId,
                        featureFlagOverrides = priceBreakdownFlagPins,
                    )

                result.attachEvidence("Get Room Price Breakdown Opera Failure")

                expect("maps the Opera rejection to the rate-info error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 944
                }

                expect("stops after the failed Opera rate-info lookup") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("one failed Opera interval prevents a partial long-stay price breakdown") {
                val booking = longStayRoomPriceBreakdownBooking()
                val rate = booking.hotel.availableRates.single()
                val secondIntervalStart = requireNotNull(booking.arrival).plusDays(19)

                installFor(booking, excluded = setOf(OPERA_RATE_INFO_STUB_ID))
                installStub(
                    rateInfoFailure(
                        booking = booking,
                        rates = listOf(rate),
                        selector = RateInfoFailureSelector(rate, secondIntervalStart),
                    ),
                )

                val result =
                    ohipApi.getRoomPriceBreakdown(
                        request = priceBreakdownRequest(booking, listOf(rate)),
                        testId = testId,
                        featureFlagOverrides = priceBreakdownFlagPins,
                    )

                result.attachEvidence("Get Long-stay Room Price Breakdown Opera Interval Failure")

                expect("returns the rate-info error instead of a partial price breakdown") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 944
                }

                expect("calls both Opera rate-info intervals") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a later room-tuple failure prevents a partial price breakdown") {
                val booking = multipleRoomPriceBreakdownBooking()
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
                    ohipApi.getRoomPriceBreakdown(
                        request = priceBreakdownRequest(booking, rates),
                        testId = testId,
                        featureFlagOverrides = priceBreakdownFlagPins,
                    )

                result.attachEvidence("Get Multiple Room Price Breakdowns Later Tuple Failure")

                expect("returns the rate-info error instead of the earlier tuple's result") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 944
                }

                expect("calls Opera once for each room tuple through the failure") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun priceBreakdownRequest(
    booking: Booking,
    rates: List<Rate>,
): RoomPriceBreakdownRequest =
    RoomPriceBreakdownRequest(
        hotelId = booking.hotel.hotelId,
        arrivalDate = requireNotNull(booking.arrival),
        departureDate = requireNotNull(booking.departure),
        ratePlanCode = rates.first().ratePlan,
        roomTypes = rates.map { rate -> rate.roomType },
        adultsNo = rates.map { rate -> rate.adults },
        childrenNo = rates.map { rate -> rate.children },
    )

private fun roomPriceBreakdownBooking(): Booking {
    val rate =
        Rate(
            ratePlan = "PBD72",
            ratePlanSet = "PUBLIC",
            roomType = "LOWDBL",
            adults = 2,
            children = 0,
            nightlyRate = 72.0,
        )

    return Booking(
        hotels =
            listOf(
                Hotels.HEAPTI.copy(
                    hotelId = "PBD01",
                    shortId = "PB1",
                    availableRates = listOf(rate),
                ),
            ),
        arrival = priceBreakdownArrival,
        departure = priceBreakdownDeparture,
        rooms =
            listOf(
                BookingRoom(
                    roomType = rate.roomType,
                    adults = rate.adults,
                    children = rate.children,
                ),
            ),
    )
}

private fun multipleRoomPriceBreakdownBooking(): Booking {
    val rates =
        listOf(
            Rate(
                ratePlan = "PBMULTI",
                ratePlanSet = "PUBLIC",
                roomType = "LOWDBL",
                adults = 2,
                children = 0,
                nightlyRate = 72.0,
            ),
            Rate(
                ratePlan = "PBMULTI",
                ratePlanSet = "PUBLIC",
                roomType = "HIGDBL",
                adults = 1,
                children = 1,
                nightlyRate = 96.0,
            ),
        )

    return Booking(
        hotels =
            listOf(
                Hotels.HEAPTI.copy(
                    hotelId = "PBM01",
                    shortId = "PBM",
                    availableRates = rates,
                ),
            ),
        arrival = priceBreakdownArrival,
        departure = priceBreakdownDeparture,
        rooms =
            rates.map { rate ->
                BookingRoom(
                    roomType = rate.roomType,
                    adults = rate.adults,
                    children = rate.children,
                )
            },
    )
}

private fun longStayRoomPriceBreakdownBooking(): Booking {
    val rate =
        Rate(
            ratePlan = "PBLONG",
            ratePlanSet = "PUBLIC",
            roomType = "LOWDBL",
            adults = 2,
            children = 0,
            nightlyRate = 72.0,
        )

    return Booking(
        hotels =
            listOf(
                Hotels.HEAPTI.copy(
                    hotelId = "PBL01",
                    shortId = "PBL",
                    availableRates = listOf(rate),
                ),
            ),
        arrival = priceBreakdownArrival,
        departure = priceBreakdownArrival.plusDays(22),
        rooms =
            listOf(
                BookingRoom(
                    roomType = rate.roomType,
                    adults = rate.adults,
                    children = rate.children,
                ),
            ),
    )
}
