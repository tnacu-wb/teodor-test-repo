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

const val OPERA_HOTEL_INVENTORY_STUB_ID = "booking.opera.hotel-inventory"

/**
 * Builds interval-aware hotel-inventory mappings from the room stock declared by
 * [booking], one mapping set per hotel so multi-hotel searches read every hotel's
 * house and room-type counts.
 */
fun hotelInventory(booking: Booking): PlannedStub =
    PlannedStub(
        id = OPERA_HOTEL_INVENTORY_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            booking.hotels
                .filter { hotel -> hotel.availableRoomTypes.isNotEmpty() }
                .ifEmpty { listOf(booking.hotel) }
                .flatMap { hotel -> hotelInventoryMappings(booking, hotel) },
    )

private fun hotelInventoryMappings(
    booking: Booking,
    hotel: Hotel,
): List<StubMapping> {
    val arrival = requireNotNull(booking.arrival)
    val departure = requireNotNull(booking.departure)
    return fixedInclusiveIntervals(arrival, departure, maximumDays = 90).map { interval ->
        val availableCount =
            hotel.availableRoomTypes.sumOf { roomType ->
                roomType.minimumAvailableRooms(interval.startDate, interval.endDate)
            }
        StubMapping(
            request =
                RequestPattern(
                    method = "GET",
                    urlPath = "/inv/v1/hotels/${hotel.hotelId}/hotelInventory",
                    queryParameters =
                        mapOf(
                            "dateRangeStart" to StringValuePattern(equalTo = interval.startDate.toString()),
                            "dateRangeEnd" to StringValuePattern(equalTo = interval.endDate.toString()),
                            "dailyInventory" to StringValuePattern(equalTo = "false"),
                            "houseLevel" to StringValuePattern(equalTo = "true"),
                        ),
                    headers = hotelHeaders(hotel.hotelId),
                ),
            response =
                jsonResponse(
                    jsonBody =
                        stubJsonObject(
                            "hotelInventories" to
                                listOf(
                                    mapOf(
                                        "houseInventory" to
                                            listOf(
                                                inventoryCount(
                                                    availableCount = availableCount,
                                                    startDate = interval.startDate.toString(),
                                                    endDate = interval.endDate.toString(),
                                                ),
                                            ),
                                        "roomTypeInventories" to
                                            hotel.availableRoomTypes.mapIndexed { index, roomType ->
                                                mapOf(
                                                    "inventoryCounts" to
                                                        listOf(
                                                            inventoryCount(
                                                                availableCount =
                                                                    roomType.minimumAvailableRooms(
                                                                        interval.startDate,
                                                                        interval.endDate,
                                                                    ),
                                                                startDate = interval.startDate.toString(),
                                                                endDate = interval.endDate.toString(),
                                                            ),
                                                        ),
                                                    "code" to roomType.roomType,
                                                    "sequence" to index + 1,
                                                )
                                            },
                                    ),
                                ),
                            "links" to emptyList<Any>(),
                        ),
                ),
        )
    }
}

private fun inventoryCount(
    availableCount: Int,
    startDate: String,
    endDate: String,
): Map<String, Any> =
    mapOf(
        "available" to (availableCount > 0),
        "availableCount" to availableCount,
        "startDate" to startDate,
        "endDate" to endDate,
    )
