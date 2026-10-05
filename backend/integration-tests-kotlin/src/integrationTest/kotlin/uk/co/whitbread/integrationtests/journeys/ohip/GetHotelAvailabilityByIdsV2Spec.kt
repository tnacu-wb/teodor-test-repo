package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.AvailabilityBookingChannel
import uk.co.whitbread.integrationtests.clients.ohip.model.AvailabilityByIdsV2Request
import uk.co.whitbread.integrationtests.clients.ohip.model.AvailabilityByIdsV3Request
import uk.co.whitbread.integrationtests.clients.ohip.model.AvailabilityCorporateRate
import uk.co.whitbread.integrationtests.clients.ohip.model.AvailabilityRatesV2
import uk.co.whitbread.integrationtests.clients.ohip.model.AvailabilityRatesV3
import uk.co.whitbread.integrationtests.clients.ohip.model.AvailabilityRoomV2
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_HOTEL_INVENTORY_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_MULTI_ROOM_RATE_AVAILABILITY_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.hotelInventoryFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.multiRoomRateAvailabilityEmptyForRoomType
import uk.co.whitbread.integrationtests.stubs.opera.custom.multiRoomRateAvailabilityFailure
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
 * Journeys for `POST /ohip/v2/hotels/availabilities/distr` and
 * `POST /ohip/v3/hotels/availabilities/distr` (multi-room-rate availability).
 *
 * Flow docs:
 * backend/integration-tests-kotlin/flows/ohip-adapter-service/GetHotelAvailabilityByIdsV2.md
 * backend/integration-tests-kotlin/flows/ohip-adapter-service/GetHotelAvailabilityByIdsV3.md
 *
 * No request-scoped feature flags gate these flows. The opera token-service flags are
 * environment-pinned OFF and evaluated outside the request context, so they are never
 * overridden.
 *
 * Room tags equal their PMS room types throughout: the adapter restores occupancy onto
 * response rooms by matching tags, and the default stub echoes each room type as its tag.
 */
class GetHotelAvailabilityByIdsV2Spec :
    JourneySpec(
        "Multi-room-rate availability can be fetched from OHIP adapter service",
        {
            val ohipApi = OhipApi()

            scenario("a DISTR search fans out per room and checks house inventory") {
                val booking = multiRoomRateBooking()

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailabilitiesByIdsV2(
                        request =
                            byIdsV2Request(
                                booking = booking,
                                channel = "DISTR",
                                rooms = listOf(roomOf("DOUBLE"), roomOf("TWINRM")),
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Get Multi Room Rate Availability Distr")

                expect("merges both per-room searches into one room class for the hotel") {
                    result.response.status.value shouldBe 200
                    val hotel = result.body.hotelAvailability.single()
                    hotel.hotelId shouldBe booking.hotel.hotelId
                    val roomStay = hotel.roomStays.single()
                    roomStay.roomClass shouldBe "ST"
                    roomStay.roomTypes.map { roomType -> roomType.roomType } shouldContainExactlyInAnyOrder
                        listOf("DOUBLE", "TWINRM")
                    roomStay.roomTypes.forEach { roomType ->
                        roomType.adults shouldBe "2"
                        roomType.numberOfRooms shouldBe "1"
                        roomType.roomRates.single().ratePlanCode shouldBe "FLEXRATE"
                    }
                }

                expect("sends one Opera search per room plus one house-inventory read") {
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.FIND_MULTI_ROOM_RATE_AVAILABILITY) shouldBe 2
                    callCount(OperaEndpoint.GET_HOTEL_INVENTORY) shouldBe 1
                }
            }

            scenario("a non-DISTR search groups same-occupancy rooms and skips inventory") {
                val booking = multiRoomRateBooking()

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailabilitiesByIdsV2(
                        request =
                            byIdsV2Request(
                                booking = booking,
                                channel = "PI",
                                rooms = listOf(roomOf("DOUBLE"), roomOf("DOUBLE")),
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Get Multi Room Rate Availability Grouped")

                expect("returns the grouped room type for the hotel") {
                    result.response.status.value shouldBe 200
                    val roomStay =
                        result.body.hotelAvailability
                            .single()
                            .roomStays
                            .single()
                    roomStay.roomClass shouldBe "ST"
                    roomStay.roomTypes.single().roomType shouldBe "DOUBLE"
                }

                expect("sends exactly one Opera search and never reads hotel inventory") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.FIND_MULTI_ROOM_RATE_AVAILABILITY) shouldBe 1
                }
            }

            scenario("a non-DISTR grouped search returns every distinct room type") {
                val booking = multiRoomRateBooking()

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailabilitiesByIdsV2(
                        request =
                            byIdsV2Request(
                                booking = booking,
                                channel = "PI",
                                rooms = listOf(roomOf("DOUBLE"), roomOf("TWINRM")),
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Get Multi Room Rate Availability Distinct Grouped Rooms")

                expect("returns every requested room type for the hotel") {
                    result.response.status.value shouldBe 200
                    val roomStay =
                        result.body.hotelAvailability
                            .single()
                            .roomStays
                            .single()
                    roomStay.roomClass shouldBe "ST"
                    roomStay.roomTypes.map { roomType -> roomType.roomType } shouldContainExactlyInAnyOrder
                        listOf("DOUBLE", "TWINRM")
                }

                expect("sends one grouped Opera search and never reads hotel inventory") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.FIND_MULTI_ROOM_RATE_AVAILABILITY) shouldBe 1
                }
            }

            scenario("ten hotels remain in one multi-room-rate batch") {
                val booking = multiRoomRateBooking(hotelCount = 10)

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailabilitiesByIdsV2(
                        request =
                            byIdsV2Request(
                                booking = booking,
                                channel = "PI",
                                rooms = listOf(roomOf("DOUBLE")),
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Get Multi Room Rate Availability Ten Hotel Boundary")

                expect("returns every requested hotel") {
                    result.response.status.value shouldBe 200
                    result.body.hotelAvailability shouldHaveSize 10
                    result.body.hotelAvailability.map { hotel -> hotel.hotelId } shouldContainExactlyInAnyOrder
                        booking.hotels.map(Hotel::hotelId)
                }

                expect("uses one Opera availability batch") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.FIND_MULTI_ROOM_RATE_AVAILABILITY) shouldBe 1
                }
            }

            scenario("eleven hotels remain available across two multi-room-rate batches") {
                val booking = multiRoomRateBooking(hotelCount = 11)

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailabilitiesByIdsV2(
                        request =
                            byIdsV2Request(
                                booking = booking,
                                channel = "PI",
                                rooms = listOf(roomOf("DOUBLE")),
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Get Multi Room Rate Availability Eleven Hotel Boundary")

                expect("returns every requested hotel") {
                    result.response.status.value shouldBe 200
                    result.body.hotelAvailability shouldHaveSize 11
                    result.body.hotelAvailability.map { hotel -> hotel.hotelId } shouldContainExactlyInAnyOrder
                        booking.hotels.map(Hotel::hotelId)
                }

                expect("uses two Opera availability batches") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.FIND_MULTI_ROOM_RATE_AVAILABILITY) shouldBe 2
                }
            }

            scenario("a corporate search stamps the corporate id on every room rate") {
                val booking = multiRoomRateBooking()
                val corporateId = "2569623"

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailabilitiesByIdsV2(
                        request =
                            byIdsV2Request(
                                booking = booking,
                                channel = "PI",
                                rooms = listOf(roomOf("DOUBLE")),
                                rates =
                                    AvailabilityRatesV2(
                                        corporateRates =
                                            AvailabilityCorporateRate(
                                                corporateId = corporateId,
                                                ratePlanSets = listOf("NEGOTIATED"),
                                            ),
                                    ),
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Get Multi Room Rate Availability Corporate")

                expect("returns the room rate stamped with the requested corporate id") {
                    result.response.status.value shouldBe 200
                    val roomRate =
                        result.body.hotelAvailability
                            .single()
                            .roomStays
                            .single()
                            .roomTypes
                            .single()
                            .roomRates
                            .single()
                    roomRate.ratePlanCode shouldBe "FLEXRATE"
                    roomRate.globalCompanyId shouldBe corporateId
                }

                expect("resolves the corporate id from the request without extra Opera calls") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.FIND_MULTI_ROOM_RATE_AVAILABILITY) shouldBe 1
                }
            }

            scenario("a V3 multi-corporate search fans out one Opera search per corporate id") {
                val booking = multiRoomRateBooking()
                val corporateIds = listOf("2569623", "3456789")

                installFor(booking)

                val result =
                    ohipApi.getHotelAvailabilitiesByIdsV3(
                        request =
                            AvailabilityByIdsV3Request(
                                bookingChannel = bookingChannel("PI"),
                                hotelIds = listOf(booking.hotel.hotelId),
                                arrivalDate = booking.arrival!!.toString(),
                                departureDate = booking.departure!!.toString(),
                                rooms = listOf(roomOf("DOUBLE")),
                                rates =
                                    AvailabilityRatesV3(
                                        corporateRates =
                                            corporateIds.map { corporateId ->
                                                AvailabilityCorporateRate(
                                                    corporateId = corporateId,
                                                    ratePlanSets = listOf("NEGOTIATED"),
                                                )
                                            },
                                    ),
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Get Multi Room Rate Availability V3 Multi Corporate")

                expect("merges one stamped room type per corporate rate") {
                    result.response.status.value shouldBe 200
                    val roomStay =
                        result.body.hotelAvailability
                            .single()
                            .roomStays
                            .single()
                    roomStay.roomTypes shouldHaveSize 2
                    roomStay.roomTypes.map { roomType ->
                        roomType.roomRates.single().globalCompanyId
                    } shouldContainExactlyInAnyOrder corporateIds
                }

                expect("sends one Opera search per corporate id") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.FIND_MULTI_ROOM_RATE_AVAILABILITY) shouldBe 2
                }
            }

            scenario("an Opera multi-room-rate failure maps to the availability error") {
                val booking = multiRoomRateBooking()

                installFor(booking, excluded = setOf(OPERA_MULTI_ROOM_RATE_AVAILABILITY_STUB_ID))
                installStub(multiRoomRateAvailabilityFailure(booking))

                val result =
                    ohipApi.getHotelAvailabilitiesByIdsV2(
                        request =
                            byIdsV2Request(
                                booking = booking,
                                channel = "PI",
                                rooms = listOf(roomOf("DOUBLE")),
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Get Multi Room Rate Availability Opera Failure")

                expect("maps the Opera failure to the multi-room-rate availability error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 915
                }

                expect("attempted exactly one Opera search") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.FIND_MULTI_ROOM_RATE_AVAILABILITY) shouldBe 1
                }
            }

            scenario("a DISTR house-inventory failure maps to the hotel inventory error") {
                val booking = multiRoomRateBooking()

                installFor(booking, excluded = setOf(OPERA_HOTEL_INVENTORY_STUB_ID))
                installStub(hotelInventoryFailure(booking))

                val result =
                    ohipApi.getHotelAvailabilitiesByIdsV2(
                        request =
                            byIdsV2Request(
                                booking = booking,
                                channel = "DISTR",
                                rooms = listOf(roomOf("DOUBLE"), roomOf("TWINRM")),
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Get Multi Room Rate Availability Inventory Failure")

                expect("maps the Opera failure to the hotel inventory error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 901
                }

                expect("performed both room searches before the failed inventory read") {
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.FIND_MULTI_ROOM_RATE_AVAILABILITY) shouldBe 2
                    callCount(OperaEndpoint.GET_HOTEL_INVENTORY) shouldBe 1
                }
            }

            scenario("one room search without availability empties the whole DISTR result") {
                val booking = multiRoomRateBooking()

                installFor(booking, excluded = setOf(OPERA_MULTI_ROOM_RATE_AVAILABILITY_STUB_ID))
                installStub(multiRoomRateAvailabilityEmptyForRoomType(booking, pmsRoomType = "TWINRM"))

                val result =
                    ohipApi.getHotelAvailabilitiesByIdsV2(
                        request =
                            byIdsV2Request(
                                booking = booking,
                                channel = "DISTR",
                                rooms = listOf(roomOf("DOUBLE"), roomOf("TWINRM")),
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Get Multi Room Rate Availability All Or Nothing")

                expect("returns an empty availability result") {
                    result.response.status.value shouldBe 200
                    result.body.hotelAvailability shouldBe emptyList()
                }

                expect("never read hotel inventory for the empty result") {
                    // Both per-room searches ran; the installed hotel-inventory stub stays unhit.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.FIND_MULTI_ROOM_RATE_AVAILABILITY) shouldBe 2
                }
            }
        },
    )

private fun multiRoomRateBooking(hotelCount: Int = 1): Booking =
    Booking(
        hotels =
            (1..hotelCount).map { index ->
                Hotels.HEAPTI.copy(
                    hotelId = "MRJ${index.toString().padStart(3, '0')}",
                    shortId = "mrj-$index",
                    name = "Multi Room Rate Journey Hotel $index",
                    availableRates =
                        listOf(
                            Rate(ratePlan = "FLEXRATE", ratePlanSet = "PBF", roomType = "DOUBLE", adults = 2, nightlyRate = 59.0),
                            Rate(ratePlan = "FLEXRATE", ratePlanSet = "PBF", roomType = "TWINRM", adults = 2, nightlyRate = 59.0),
                        ),
                    availableRoomTypes =
                        listOf(
                            HotelRoomType(roomClass = "ST", roomType = "DOUBLE", numberOfRooms = 5),
                            HotelRoomType(roomClass = "ST", roomType = "TWINRM", numberOfRooms = 2),
                        ),
                )
            },
        arrival = arrival,
        departure = departure,
        rooms = listOf(BookingRoom(roomType = "DB", adults = 2)),
    )

private fun bookingChannel(channel: String): AvailabilityBookingChannel =
    AvailabilityBookingChannel(channel = channel, subchannel = "WEB", language = "EN")

private fun roomOf(pmsRoomType: String): AvailabilityRoomV2 =
    AvailabilityRoomV2(
        tag = pmsRoomType,
        roomTypes = listOf(pmsRoomType),
        adults = 2,
        children = 0,
        numberOfRooms = 1,
    )

private fun byIdsV2Request(
    booking: Booking,
    channel: String,
    rooms: List<AvailabilityRoomV2>,
    rates: AvailabilityRatesV2 = AvailabilityRatesV2(ratePlanCodes = listOf("FLEXRATE")),
): AvailabilityByIdsV2Request =
    AvailabilityByIdsV2Request(
        bookingChannel = bookingChannel(channel),
        hotelIds = booking.hotels.map(Hotel::hotelId),
        arrivalDate = booking.arrival!!.toString(),
        departureDate = booking.departure!!.toString(),
        rooms = rooms,
        rates = rates,
    )
