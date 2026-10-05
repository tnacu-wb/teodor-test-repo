package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.AvailabilityBookingChannel
import uk.co.whitbread.integrationtests.clients.ohip.model.AvailabilityRoomV2
import uk.co.whitbread.integrationtests.clients.ohip.model.MultiHotelAvailabilityV2Request
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_MINIMUM_RATE_AVAILABILITY_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.minimumRateAvailabilityDroppingHotelAfterFirstInterval
import uk.co.whitbread.integrationtests.stubs.opera.custom.minimumRateAvailabilityFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.minimumRateAvailabilityWithoutMinimumRate
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.HotelRoomType
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

private val arrival: LocalDate = LocalDate.now().plusDays(14)
private val departure: LocalDate = arrival.plusDays(2)

/**
 * Journeys for `POST /ohip/v2/hotels/availabilities` (minimum-rate multi-hotel summary).
 *
 * Flow doc: backend/integration-tests-kotlin/flows/ohip-adapter-service/GetMultiHotelAvailabilityV2.md
 *
 * No request-scoped feature flags gate this flow. The opera token-service flags are
 * environment-pinned OFF and evaluated outside the request context, so they are never
 * overridden.
 */
class GetMultiHotelAvailabilityV2Spec :
    JourneySpec(
        "Minimum-rate multi-hotel availability can be fetched from OHIP adapter service",
        {
            val ohipApi = OhipApi()

            scenario("a multi-hotel search summarises each hotel's minimum rate in one Opera call") {
                val booking = minimumRateBooking()

                installFor(booking)

                val result =
                    ohipApi.getMultiHotelAvailabilityV2(
                        request = minimumRateRequest(booking, roomTypes = listOf("DOUBLE")),
                        testId = testId,
                    )

                result.attachEvidence("Get Minimum Rate Availability Two Hotels")

                expect("returns an available minimum-rate summary per hotel") {
                    result.response.status.value shouldBe 200
                    result.body.hotelAvailabilityResults shouldHaveSize 2
                    result.body.hotelAvailabilityResults.map { hotel -> hotel.hotelId } shouldContainExactlyInAnyOrder
                        booking.hotels.map { hotel -> hotel.hotelId }
                    result.body.hotelAvailabilityResults.forEach { hotel ->
                        hotel.available shouldBe true
                        hotel.minimumRate shouldBe 118.0
                    }
                }

                expect("performs exactly one Opera minimum-rate search") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.FIND_MINIMUM_RATE_AVAILABILITY) shouldBe 1
                }
            }

            scenario("a room without room types resolves substitutions before the same Opera search") {
                val booking = minimumRateBooking()

                installFor(booking)

                val result =
                    ohipApi.getMultiHotelAvailabilityV2(
                        // No roomTypes: the adapter asks the real rules-agent service for the
                        // PMS substitutions of the DB tag before searching Opera.
                        request = minimumRateRequest(booking, roomTypes = emptyList()),
                        testId = testId,
                    )

                result.attachEvidence("Get Minimum Rate Availability Rules Agent Substitution")

                expect("returns the same available summary per hotel") {
                    result.response.status.value shouldBe 200
                    result.body.hotelAvailabilityResults shouldHaveSize 2
                    result.body.hotelAvailabilityResults.forEach { hotel -> hotel.available shouldBe true }
                }

                expect("still performs exactly one Opera minimum-rate search") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.FIND_MINIMUM_RATE_AVAILABILITY) shouldBe 1
                }
            }

            scenario("a sold-out hotel reports unavailable while keeping its minimum rate") {
                val booking =
                    minimumRateBooking().let { base ->
                        base.copy(
                            hotels =
                                listOf(
                                    base.hotels.first(),
                                    base.hotels.last().copy(
                                        availableRoomTypes =
                                            base.hotels
                                                .last()
                                                .availableRoomTypes
                                                .map { roomType -> roomType.copy(numberOfRooms = 0) },
                                    ),
                                ),
                        )
                    }
                val soldOutHotel = booking.hotels.last()

                installFor(booking)

                val result =
                    ohipApi.getMultiHotelAvailabilityV2(
                        request = minimumRateRequest(booking, roomTypes = listOf("DOUBLE")),
                        testId = testId,
                    )

                result.attachEvidence("Get Minimum Rate Availability Sold Out Hotel")

                expect("marks only the sold-out hotel unavailable, with its rate still priced") {
                    result.response.status.value shouldBe 200
                    val soldOut =
                        result.body.hotelAvailabilityResults.single { hotel -> hotel.hotelId == soldOutHotel.hotelId }
                    val available =
                        result.body.hotelAvailabilityResults.single { hotel -> hotel.hotelId != soldOutHotel.hotelId }
                    soldOut.available shouldBe false
                    soldOut.minimumRate shouldBe 118.0
                    available.available shouldBe true
                }
            }

            scenario("a stay over ninety days splits into two Opera searches with summed rates") {
                val longDeparture = arrival.plusDays(120)
                val booking = minimumRateBooking().copy(departure = longDeparture)

                installFor(booking)

                val result =
                    ohipApi.getMultiHotelAvailabilityV2(
                        request =
                            minimumRateRequest(booking, roomTypes = listOf("DOUBLE"))
                                .copy(departureDate = longDeparture.toString()),
                        testId = testId,
                    )

                result.attachEvidence("Get Minimum Rate Availability Interval Split")

                expect("sums the per-interval minimum rates over the whole stay") {
                    result.response.status.value shouldBe 200
                    result.body.hotelAvailabilityResults shouldHaveSize 2
                    result.body.hotelAvailabilityResults.forEach { hotel ->
                        hotel.available shouldBe true
                        // 120 nights at the cheapest nightly rate of 59.0 across both intervals.
                        hotel.minimumRate shouldBe 7080.0
                    }
                }

                expect("performs one Opera search per date interval") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.FIND_MINIMUM_RATE_AVAILABILITY) shouldBe 2
                }
            }

            scenario("an Opera minimum-rate failure maps to the multi-hotel availability error") {
                val booking = minimumRateBooking()

                installFor(booking, excluded = setOf(OPERA_MINIMUM_RATE_AVAILABILITY_STUB_ID))
                installStub(minimumRateAvailabilityFailure(booking))

                val result =
                    ohipApi.getMultiHotelAvailabilityV2(
                        request = minimumRateRequest(booking, roomTypes = listOf("DOUBLE")),
                        testId = testId,
                    )

                result.attachEvidence("Get Minimum Rate Availability Opera Failure")

                expect("maps the Opera failure to the multi-hotel availability error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 906
                }

                expect("attempted exactly one Opera minimum-rate search") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.FIND_MINIMUM_RATE_AVAILABILITY) shouldBe 1
                }
            }

            scenario("a hotel missing from a later interval is dropped from the merged summary") {
                val longDeparture = arrival.plusDays(120)
                val booking = minimumRateBooking().copy(departure = longDeparture)
                val droppedHotel = booking.hotels.last()
                val keptHotel = booking.hotels.first()

                installFor(booking, excluded = setOf(OPERA_MINIMUM_RATE_AVAILABILITY_STUB_ID))
                installStub(
                    minimumRateAvailabilityDroppingHotelAfterFirstInterval(
                        booking = booking,
                        droppedHotelId = droppedHotel.hotelId,
                    ),
                )

                val result =
                    ohipApi.getMultiHotelAvailabilityV2(
                        request =
                            minimumRateRequest(booking, roomTypes = listOf("DOUBLE"))
                                .copy(departureDate = longDeparture.toString()),
                        testId = testId,
                    )

                result.attachEvidence("Get Minimum Rate Availability Interval Intersection")

                expect("keeps only the hotel present in every interval") {
                    result.response.status.value shouldBe 200
                    val hotel = result.body.hotelAvailabilityResults.single()
                    hotel.hotelId shouldBe keptHotel.hotelId
                    hotel.available shouldBe true
                }

                expect("still performed one Opera search per date interval") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.FIND_MINIMUM_RATE_AVAILABILITY) shouldBe 2
                }
            }

            // BUG: the result mapper dereferences minimumRate unconditionally, so an Opera room
            // stay without one fails the whole request with a 500 NPE instead of a summary.
            // See bug/minimum-rate-availability-missing-minimum-rate-npe.md; re-enable when fixed.
            scenario("!an Opera room stay without a minimum rate is still summarised") {
                val booking = minimumRateBooking()

                installFor(booking, excluded = setOf(OPERA_MINIMUM_RATE_AVAILABILITY_STUB_ID))
                installStub(minimumRateAvailabilityWithoutMinimumRate(booking))

                val result =
                    ohipApi.getMultiHotelAvailabilityV2(
                        request = minimumRateRequest(booking, roomTypes = listOf("DOUBLE")),
                        testId = testId,
                    )

                result.attachEvidence("Get Minimum Rate Availability Missing Minimum Rate")

                expect("returns each hotel's availability with no minimum rate") {
                    result.response.status.value shouldBe 200
                    result.body.hotelAvailabilityResults shouldHaveSize 2
                    result.body.hotelAvailabilityResults.forEach { hotel ->
                        hotel.available shouldBe true
                        hotel.minimumRate shouldBe null
                    }
                }

                expect("attempted exactly one Opera minimum-rate search") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.FIND_MINIMUM_RATE_AVAILABILITY) shouldBe 1
                }
            }
        },
    )

private fun minimumRateHotel(hotel: Hotel): Hotel =
    hotel.copy(
        availableRates =
            listOf(
                Rate(ratePlan = "FLEXRATE", ratePlanSet = "PBF", roomType = "DOUBLE", adults = 2, nightlyRate = 59.0),
            ),
        availableRoomTypes = listOf(HotelRoomType(roomClass = "ST", roomType = "DOUBLE", numberOfRooms = 10)),
    )

private fun minimumRateBooking(): Booking =
    Booking(
        hotels = listOf(minimumRateHotel(Hotels.HEAPTI), minimumRateHotel(Hotels.FRAMTI)),
        arrival = arrival,
        departure = departure,
        rooms = listOf(BookingRoom(roomType = "DB", adults = 2)),
    )

private fun minimumRateRequest(
    booking: Booking,
    roomTypes: List<String>,
): MultiHotelAvailabilityV2Request =
    MultiHotelAvailabilityV2Request(
        bookingChannel = AvailabilityBookingChannel(channel = "PI", subchannel = "WEB", language = "EN"),
        hotelIds = booking.hotels.map(Hotel::hotelId),
        arrivalDate = booking.arrival!!.toString(),
        departureDate = booking.departure!!.toString(),
        rooms =
            listOf(
                AvailabilityRoomV2(
                    tag = "DB",
                    roomTypes = roomTypes,
                    adults = 2,
                    children = 0,
                    numberOfRooms = 1,
                ),
            ),
    )
