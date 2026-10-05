package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.collections.shouldBeIn
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.RoomLevelInventoryResponse
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_HOTEL_INVENTORY_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.hotelInventoryFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.HotelRoomType
import uk.co.whitbread.integrationtests.testkit.model.RoomInventoryPeriod
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

private val arrival: LocalDate = LocalDate.now().plusDays(30)
private val departure: LocalDate = arrival.plusDays(2)

private val hotelInventoryFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
    )

/**
 * Journey for the [getHotelRoomsInventory flow](../../../../../../../../../flows/ohip-adapter-service/GetHotelRoomsInventory.md).
 */
class GetHotelRoomsInventorySpec :
    JourneySpec(
        "Available hotel room inventory can be fetched from OHIP adapter service",
        {
            val ohipApi = OhipApi()

            scenario("available hotel room inventory excludes sold-out room types") {
                val booking = hotelInventoryBooking()

                installFor(booking)

                val result =
                    ohipApi.getHotelRoomsInventory(
                        hotelId = booking.hotel.hotelId,
                        dateRangeStart = requireNotNull(booking.arrival),
                        dateRangeEnd = requireNotNull(booking.departure),
                        testId = testId,
                        featureFlagOverrides = hotelInventoryFlagPins,
                    )

                result.attachEvidence("Get Available Hotel Room Inventory")

                expect("returns the available count for each room type that has stock") {
                    result.response.status.value shouldBe 200
                    result.body.roomTypeInventories shouldContainExactlyInAnyOrder
                        booking.hotel.availableRoomTypes
                            .filter { roomType -> roomType.numberOfRooms > 0 }
                            .map { roomType ->
                                RoomLevelInventoryResponse(
                                    availableCount = roomType.numberOfRooms,
                                    code = roomType.roomType,
                                )
                            }
                }

                expect("reads only hotel room inventory from Opera") {
                    // One Opera call: the hotel-inventory lookup for the requested date range.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_HOTEL_INVENTORY) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a long stay returns the lowest room stock across Opera request chunks") {
                val booking = longStayHotelInventoryBooking()

                installFor(booking)

                val result =
                    ohipApi.getHotelRoomsInventory(
                        hotelId = booking.hotel.hotelId,
                        dateRangeStart = requireNotNull(booking.arrival),
                        dateRangeEnd = requireNotNull(booking.departure),
                        testId = testId,
                        featureFlagOverrides = hotelInventoryFlagPins,
                    )

                result.attachEvidence("Get Long-Stay Hotel Room Inventory")

                expect("returns each room type's lowest stock and excludes one sold out in a later chunk") {
                    result.response.status.value shouldBe 200
                    result.body.roomTypeInventories shouldContainExactlyInAnyOrder
                        listOf(
                            RoomLevelInventoryResponse(
                                availableCount = 3,
                                code = "DOUBLE",
                            ),
                        )
                }

                expect("reads both date chunks from Opera") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_HOTEL_INVENTORY) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera hotel inventory failure is returned as an OHIP error") {
                val booking = hotelInventoryBooking()

                installFor(booking, excluded = setOf(OPERA_HOTEL_INVENTORY_STUB_ID))
                installStub(hotelInventoryFailure(booking))

                val result =
                    ohipApi.getHotelRoomsInventory(
                        hotelId = booking.hotel.hotelId,
                        dateRangeStart = requireNotNull(booking.arrival),
                        dateRangeEnd = requireNotNull(booking.departure),
                        testId = testId,
                        featureFlagOverrides = hotelInventoryFlagPins,
                    )

                result.attachEvidence("Get Hotel Room Inventory Opera Failure")

                expect("maps the Opera failure to the hotel-inventory error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 901
                }

                expect("stops after the rejected Opera inventory lookup") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_HOTEL_INVENTORY) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("one failed Opera chunk fails a long-stay hotel inventory request") {
                val booking = longStayHotelInventoryBooking()

                installFor(booking, excluded = setOf(OPERA_HOTEL_INVENTORY_STUB_ID))
                installStub(
                    hotelInventoryFailure(
                        booking = booking,
                        failedIntervalStart = requireNotNull(booking.departure),
                    ),
                )

                val result =
                    ohipApi.getHotelRoomsInventory(
                        hotelId = booking.hotel.hotelId,
                        dateRangeStart = requireNotNull(booking.arrival),
                        dateRangeEnd = requireNotNull(booking.departure),
                        testId = testId,
                        featureFlagOverrides = hotelInventoryFlagPins,
                    )

                result.attachEvidence("Get Long-Stay Hotel Room Inventory Opera Failure")

                expect("returns the hotel-inventory error instead of partial inventory") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 901
                }

                expect("requests one or both Opera inventory chunks and no other upstream") {
                    // Not a service defect — the public contract is the same 500/errCode 901
                    // either way. The exact chunk count is unassertable because the
                    // service fans the chunks out of a HashMap keyed by chunk start date:
                    // dispatch order follows LocalDate.hashCode of the relative dates, and a
                    // first-dispatched failing chunk short-circuits the flatMap before the
                    // sibling is requested — 1 or 2 calls depending on the calendar day.
                    callCount(Upstream.OPERA) shouldBeIn setOf(1, 2)
                    callCount(OperaEndpoint.GET_HOTEL_INVENTORY) shouldBeIn setOf(1, 2)
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun hotelInventoryBooking(): Booking =
    Booking(
        hotels =
            listOf(
                Hotels.HEAPTI.copy(
                    hotelId = "HINV01",
                    shortId = "HI1",
                    availableRoomTypes =
                        listOf(
                            HotelRoomType(roomClass = "ST", roomType = "DOUBLE", numberOfRooms = 7),
                            HotelRoomType(roomClass = "ST", roomType = "TWINRM", numberOfRooms = 2),
                            HotelRoomType(roomClass = "ST", roomType = "FMTHRE", numberOfRooms = 0),
                        ),
                ),
            ),
        arrival = arrival,
        departure = departure,
    )

private fun longStayHotelInventoryBooking(): Booking {
    val longStayDeparture = arrival.plusDays(90)
    return Booking(
        hotels =
            listOf(
                Hotels.HEAPTI.copy(
                    hotelId = "HINV02",
                    shortId = "HI2",
                    availableRoomTypes =
                        listOf(
                            HotelRoomType(
                                roomClass = "ST",
                                roomType = "DOUBLE",
                                numberOfRooms = 7,
                                inventoryPeriods =
                                    listOf(
                                        RoomInventoryPeriod(
                                            startDate = longStayDeparture,
                                            endDate = longStayDeparture,
                                            numberOfRooms = 3,
                                        ),
                                    ),
                            ),
                            HotelRoomType(
                                roomClass = "ST",
                                roomType = "TWINRM",
                                numberOfRooms = 2,
                                inventoryPeriods =
                                    listOf(
                                        RoomInventoryPeriod(
                                            startDate = longStayDeparture,
                                            endDate = longStayDeparture,
                                            numberOfRooms = 0,
                                        ),
                                    ),
                            ),
                        ),
                ),
            ),
        arrival = arrival,
        departure = longStayDeparture,
    )
}
