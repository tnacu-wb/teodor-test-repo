package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.MultiHotelAvailabilityRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_MULTI_HOTEL_AVAILABILITY_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.multiHotelAvailabilityFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.presets.Companies
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

private val arrival: LocalDate = LocalDate.now().plusDays(14)
private val departure: LocalDate = arrival.plusDays(2)

/**
 * Journeys for `GET /ohip/hotels/availabilities` (multi-hotel PBN/PBF availability).
 *
 * Flow doc: backend/integration-tests-kotlin/flows/ohip-adapter-service/GetMultiHotelAvailability.md
 *
 * No request-scoped feature flags gate this flow. The opera token-service flags
 * (`release_ohip_use_token_service`, `release_ohip_use_token_refresh_skew`) are
 * environment-pinned OFF and evaluated outside the request context, so they are not
 * overridden here.
 *
 * Every hotel that should report availability carries both a PBN and a PBF rate: the
 * adapter merges the two rate-plan-set legs into one result and treats an empty leg as
 * the hotel having become unavailable, so a hotel with rates in only one set would
 * depend on the merge order of concurrent legs.
 */
class GetMultiHotelAvailabilitySpec :
    JourneySpec(
        "Multi-hotel availability can be fetched from OHIP adapter service",
        {
            val ohipApi = OhipApi()

            scenario("both rate plan set legs merge into every hotel's availability") {
                val booking = multiHotelAvailabilityBooking()

                installFor(booking)

                val result =
                    ohipApi.getMultiHotelAvailability(
                        request = multiHotelAvailabilityRequest(booking),
                        testId = testId,
                    )

                result.attachEvidence("Get Multi Hotel Availability Two Hotels")

                expect("returns an available result for each requested hotel") {
                    result.response.status.value shouldBe 200
                    result.body.hotelAvailabilityResults shouldHaveSize 2
                    result.body.hotelAvailabilityResults.map { hotel -> hotel.hotelId } shouldContainExactlyInAnyOrder
                        booking.hotels.map { hotel -> hotel.hotelId }
                    result.body.hotelAvailabilityResults.forEach { hotel -> hotel.available shouldBe true }
                }

                expect("merges the PBN and PBF rates for the requested room type") {
                    result.body.hotelAvailabilityResults.forEach { hotel ->
                        val roomType = hotel.roomTypes.single()
                        roomType.roomType shouldBe "DB"
                        roomType.adults shouldBe "2"
                        roomType.roomRates.map { roomRate -> roomRate.ratePlan } shouldContainExactlyInAnyOrder
                            listOf("FLEXRATE", "STANDARD")
                        roomType.roomRates.forEach { roomRate ->
                            roomRate.roomType shouldBe "DOUBLE"
                            roomRate.totalPrice shouldBe 118.0
                        }
                    }
                }

                expect("fans out exactly one Opera search per rate plan set") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_MULTI_HOTEL_AVAILABILITY) shouldBe 2
                }
            }

            scenario("five hotels remain in one Opera batch") {
                val booking = batchedMultiHotelAvailabilityBooking(hotelCount = 5)

                installFor(booking)

                val result =
                    ohipApi.getMultiHotelAvailability(
                        request = multiHotelAvailabilityRequest(booking),
                        testId = testId,
                    )

                result.attachEvidence("Get Multi Hotel Availability Five Hotel Boundary")

                expect("returns every requested hotel as available") {
                    result.response.status.value shouldBe 200
                    result.body.hotelAvailabilityResults shouldHaveSize 5
                    result.body.hotelAvailabilityResults.map { hotel -> hotel.hotelId } shouldContainExactlyInAnyOrder
                        booking.hotels.map(Hotel::hotelId)
                    result.body.hotelAvailabilityResults.forEach { hotel -> hotel.available shouldBe true }
                }

                expect("uses one Opera batch for each rate plan set") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_MULTI_HOTEL_AVAILABILITY) shouldBe 2
                }
            }

            scenario("six hotels remain available across two Opera batches") {
                val booking = batchedMultiHotelAvailabilityBooking(hotelCount = 6)

                installFor(booking)

                val result =
                    ohipApi.getMultiHotelAvailability(
                        request = multiHotelAvailabilityRequest(booking),
                        testId = testId,
                    )

                result.attachEvidence("Get Multi Hotel Availability Six Hotel Boundary")

                expect("returns every requested hotel as available") {
                    result.response.status.value shouldBe 200
                    result.body.hotelAvailabilityResults shouldHaveSize 6
                    result.body.hotelAvailabilityResults.map { hotel -> hotel.hotelId } shouldContainExactlyInAnyOrder
                        booking.hotels.map(Hotel::hotelId)
                    result.body.hotelAvailabilityResults.forEach { hotel -> hotel.available shouldBe true }
                }

                expect("uses two Opera batches for each rate plan set") {
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(OperaEndpoint.GET_MULTI_HOTEL_AVAILABILITY) shouldBe 4
                }
            }

            scenario("a hotel without rates is returned as unavailable beside an available one") {
                val booking =
                    multiHotelAvailabilityBooking().let { base ->
                        base.copy(
                            hotels =
                                listOf(
                                    base.hotels.first(),
                                    base.hotels.last().copy(availableRates = emptyList()),
                                ),
                        )
                    }
                val availableHotel = booking.hotels.first()
                val rateLessHotel = booking.hotels.last()

                installFor(booking)

                val result =
                    ohipApi.getMultiHotelAvailability(
                        request = multiHotelAvailabilityRequest(booking),
                        testId = testId,
                    )

                result.attachEvidence("Get Multi Hotel Availability Mixed Availability")

                expect("keeps the rate-carrying hotel available") {
                    result.response.status.value shouldBe 200
                    val available =
                        result.body.hotelAvailabilityResults.single { hotel -> hotel.hotelId == availableHotel.hotelId }
                    available.available shouldBe true
                    available.roomTypes
                        .single()
                        .roomRates
                        .map { roomRate -> roomRate.ratePlan } shouldContainExactlyInAnyOrder
                        listOf("FLEXRATE", "STANDARD")
                }

                expect("marks the rate-less hotel unavailable without rates") {
                    val unavailable =
                        result.body.hotelAvailabilityResults.single { hotel -> hotel.hotelId == rateLessHotel.hotelId }
                    unavailable.available shouldBe false
                    unavailable.roomTypes.single().roomRates shouldBe emptyList()
                }

                expect("still fans out one Opera search per rate plan set") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_MULTI_HOTEL_AVAILABILITY) shouldBe 2
                }
            }

            scenario("an Opera availability failure fails the whole multi-hotel search") {
                val booking = multiHotelAvailabilityBooking()

                installFor(booking, excluded = setOf(OPERA_MULTI_HOTEL_AVAILABILITY_STUB_ID))
                installStub(multiHotelAvailabilityFailure(booking))

                val result =
                    ohipApi.getMultiHotelAvailability(
                        request = multiHotelAvailabilityRequest(booking),
                        testId = testId,
                    )

                result.attachEvidence("Get Multi Hotel Availability Opera Failure")

                expect("maps the Opera failure to the multi-hotel availability error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 964
                }

                expect("never retried the failed search") {
                    // Both legs subscribe concurrently but the first Opera error cancels the
                    // sibling leg reactively, so the second request may never be dispatched.
                    callCount(Upstream.OPERA) shouldBeInRange 1..2
                    callCount(OperaEndpoint.GET_MULTI_HOTEL_AVAILABILITY) shouldBeInRange 1..2
                }
            }

            scenario("a supplied companyId never reaches Opera and only public rates return") {
                val company = Companies.NEILL_TECHNICAL_SERVICES
                val booking =
                    multiHotelAvailabilityBooking().let { base ->
                        base.copy(
                            companies = listOf(company),
                            hotels =
                                base.hotels.map { hotel ->
                                    hotel.copy(
                                        availableRates =
                                            hotel.availableRates +
                                                Rate(
                                                    ratePlan = "CORPFLEX",
                                                    ratePlanSet = "NEGOTIATED",
                                                    roomType = "DOUBLE",
                                                    adults = 2,
                                                    nightlyRate = 45.0,
                                                ),
                                    )
                                },
                        )
                    }

                // The negotiated-availability stub installs from the Booking facts; it staying
                // unhit is the absence proof that splitRequest drops companyId (flow doc).
                installFor(booking)

                val result =
                    ohipApi.getMultiHotelAvailability(
                        request = multiHotelAvailabilityRequest(booking).copy(companyId = company.companyId),
                        testId = testId,
                    )

                result.attachEvidence("Get Multi Hotel Availability Company Id Dropped")

                expect("returns only the public PBN and PBF rates, never the negotiated one") {
                    result.response.status.value shouldBe 200
                    result.body.hotelAvailabilityResults shouldHaveSize 2
                    result.body.hotelAvailabilityResults.forEach { hotel ->
                        hotel.available shouldBe true
                        hotel.roomTypes
                            .single()
                            .roomRates
                            .map { roomRate -> roomRate.ratePlan } shouldContainExactlyInAnyOrder
                            listOf("FLEXRATE", "STANDARD")
                    }
                }

                expect("searched only the two public rate plan set legs") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_MULTI_HOTEL_AVAILABILITY) shouldBe 2
                }
            }
        },
    )

private fun availabilityRates(): List<Rate> =
    listOf(
        Rate(ratePlan = "FLEXRATE", ratePlanSet = "PBF", roomType = "DOUBLE", adults = 2, nightlyRate = 59.0),
        Rate(ratePlan = "STANDARD", ratePlanSet = "PBN", roomType = "DOUBLE", adults = 2, nightlyRate = 59.0),
    )

private fun multiHotelAvailabilityBooking(): Booking =
    Booking(
        hotels =
            listOf(
                Hotels.HEAPTI.copy(availableRates = availabilityRates()),
                Hotels.FRAMTI.copy(availableRates = availabilityRates()),
            ),
        arrival = arrival,
        departure = departure,
        rooms = listOf(BookingRoom(roomType = "DB", adults = 2)),
    )

private fun batchedMultiHotelAvailabilityBooking(hotelCount: Int): Booking =
    multiHotelAvailabilityBooking().let { booking ->
        val template = booking.hotels.first()
        booking.copy(
            hotels =
                (1..hotelCount).map { index ->
                    template.copy(
                        hotelId = "MHB${index.toString().padStart(3, '0')}",
                        shortId = "mhb-$index",
                        name = "Batch Hotel $index",
                    )
                },
        )
    }

private fun multiHotelAvailabilityRequest(booking: Booking): MultiHotelAvailabilityRequest =
    MultiHotelAvailabilityRequest(
        hotelIds = booking.hotels.map(Hotel::hotelId),
        arrivalDate = booking.arrival!!,
        departureDate = booking.departure!!,
        numberOfRooms = listOf(1),
        roomTypes = booking.rooms.map { room -> requireNotNull(room.roomType) },
        adults = booking.rooms.map { room -> requireNotNull(room.adults) },
        children = booking.rooms.map { room -> room.children },
        cotsRequired = List(booking.rooms.size) { false },
        channel = "PI",
    )
