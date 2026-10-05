package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.ItemInventoryAvailability
import uk.co.whitbread.integrationtests.clients.ohip.model.ItemInventoryRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_ITEM_INVENTORY_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.hotelItemInventoryFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.HotelInventoryItem
import uk.co.whitbread.integrationtests.testkit.model.HotelItemInventory
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

private const val ITEM_INVENTORY_HOTEL_ID = "ITEM01"
private val itemInventoryStartDate: LocalDate = LocalDate.of(2030, 1, 10)
private val itemInventoryEndDate: LocalDate = LocalDate.of(2030, 1, 12)
private val longItemInventoryEndDate: LocalDate = itemInventoryStartDate.plusDays(90)

/**
 * Flow: [GetItemInventory](../../../../../../../../../flows/ohip-adapter-service/GetItemInventory.md)
 */
class GetItemInventorySpec :
    JourneySpec(
        "Hotel item inventory can be fetched from OHIP adapter service",
        {
            val ohipApi = OhipApi()

            scenario("a hotel returns its item inventory for the requested stay") {
                val booking = itemInventoryBooking()
                val expectedItem = requireNotNull(booking.hotel.itemInventory).items.single()
                val arrival = requireNotNull(booking.arrival)
                val departure = requireNotNull(booking.departure)
                val expectedDates =
                    generateSequence(arrival) { date -> date.plusDays(1) }
                        .takeWhile { date -> !date.isAfter(departure) }
                        .toList()

                installFor(booking)

                val result =
                    ohipApi.getItemInventory(
                        request =
                            ItemInventoryRequest(
                                hotelId = booking.hotel.hotelId,
                                startDate = arrival,
                                endDate = departure,
                            ),
                        testId = testId,
                        featureFlagOverrides = mapOf(OhipFeatureFlag.USE_TOKEN_SERVICE to false),
                    )

                result.attachEvidence("Get Hotel Item Inventory")

                expect("returns the item's identity and daily stock") {
                    result.response.status.value shouldBe 200
                    val item = result.body.itemsInventory.single()
                    item.code shouldBe expectedItem.code
                    item.name shouldBe expectedItem.name
                    item.description shouldBe expectedItem.description
                    item.inventories shouldBe
                        expectedDates.map { date ->
                            ItemInventoryAvailability(
                                date = date.toString(),
                                total = expectedItem.total,
                                available = expectedItem.available,
                            )
                        }
                }

                expect("reads item inventory only from Opera") {
                    // One Opera call: the hotel item-inventory lookup for the requested date range.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_ITEM_INVENTORY) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a long stay returns merged item inventory from every Opera interval") {
                val booking = itemInventoryBooking(departure = longItemInventoryEndDate)
                val expectedItem = requireNotNull(booking.hotel.itemInventory).items.single()
                val arrival = requireNotNull(booking.arrival)
                val departure = requireNotNull(booking.departure)
                val expectedDates =
                    generateSequence(arrival) { date -> date.plusDays(1) }
                        .takeWhile { date -> !date.isAfter(departure) }
                        .toList()

                installFor(booking)

                val result =
                    ohipApi.getItemInventory(
                        request =
                            ItemInventoryRequest(
                                hotelId = booking.hotel.hotelId,
                                startDate = arrival,
                                endDate = departure,
                            ),
                        testId = testId,
                        featureFlagOverrides = mapOf(OhipFeatureFlag.USE_TOKEN_SERVICE to false),
                    )

                result.attachEvidence("Get Long-Stay Hotel Item Inventory")

                expect("merges one item with stock for every date in the stay") {
                    result.response.status.value shouldBe 200
                    val item = result.body.itemsInventory.single()
                    item.code shouldBe expectedItem.code
                    item.name shouldBe expectedItem.name
                    item.description shouldBe expectedItem.description
                    item.inventories shouldBe
                        expectedDates.map { date ->
                            ItemInventoryAvailability(
                                date = date.toString(),
                                total = expectedItem.total,
                                available = expectedItem.available,
                            )
                        }
                }

                expect("reads both item-inventory intervals only from Opera") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_ITEM_INVENTORY) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera item-inventory failure is returned as an OHIP error") {
                val booking = itemInventoryBooking()
                val arrival = requireNotNull(booking.arrival)
                val departure = requireNotNull(booking.departure)

                installFor(booking, excluded = setOf(OPERA_ITEM_INVENTORY_STUB_ID))
                installStub(hotelItemInventoryFailure(booking))

                val result =
                    ohipApi.getItemInventory(
                        request =
                            ItemInventoryRequest(
                                hotelId = booking.hotel.hotelId,
                                startDate = arrival,
                                endDate = departure,
                            ),
                        testId = testId,
                        featureFlagOverrides = mapOf(OhipFeatureFlag.USE_TOKEN_SERVICE to false),
                    )

                result.attachEvidence("Get Hotel Item Inventory Opera Failure")

                expect("maps the Opera rejection to the item-inventory error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 902
                }

                expect("calls only the failed Opera item-inventory interval") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_ITEM_INVENTORY) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("one failed Opera interval fails a long item-inventory request") {
                val booking = itemInventoryBooking(departure = longItemInventoryEndDate)
                val arrival = requireNotNull(booking.arrival)
                val departure = requireNotNull(booking.departure)

                installFor(booking, excluded = setOf(OPERA_ITEM_INVENTORY_STUB_ID))
                installStub(
                    hotelItemInventoryFailure(
                        booking = booking,
                        failedIntervalIndex = 1,
                    ),
                )

                val result =
                    ohipApi.getItemInventory(
                        request =
                            ItemInventoryRequest(
                                hotelId = booking.hotel.hotelId,
                                startDate = arrival,
                                endDate = departure,
                            ),
                        testId = testId,
                        featureFlagOverrides = mapOf(OhipFeatureFlag.USE_TOKEN_SERVICE to false),
                    )

                result.attachEvidence("Get Long-Stay Hotel Item Inventory Opera Failure")

                expect("maps one failed interval to the item-inventory error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 902
                }

                expect("calls both Opera intervals without returning partial inventory") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_ITEM_INVENTORY) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun itemInventoryBooking(departure: LocalDate = itemInventoryEndDate): Booking =
    Booking(
        hotels =
            listOf(
                Hotels.HEAPTI.copy(
                    hotelId = ITEM_INVENTORY_HOTEL_ID,
                    availableRates = emptyList(),
                    itemInventory =
                        HotelItemInventory(
                            items =
                                listOf(
                                    HotelInventoryItem(
                                        code = "COT-ITEM01",
                                        name = "Travel Cot",
                                        description = "Travel cot stock",
                                        total = 5,
                                        available = 3,
                                    ),
                                ),
                        ),
                ),
            ),
        arrival = itemInventoryStartDate,
        departure = departure,
    )
