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

const val OPERA_ITEM_INVENTORY_STUB_ID = "booking.opera.item-inventory"

/**
 * Builds interval-aware item-inventory mappings from each hotel's declared daily stock,
 * one mapping set per hotel that declares itemInventory.
 */
fun hotelItemInventory(booking: Booking): PlannedStub {
    val hotels = booking.hotels.filter { hotel -> hotel.itemInventory != null }
    require(hotels.isNotEmpty()) {
        "At least one hotel must declare itemInventory for the item-inventory stub"
    }

    return PlannedStub(
        id = OPERA_ITEM_INVENTORY_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = hotels.flatMap { hotel -> itemInventoryMappings(booking, hotel) },
    )
}

private fun itemInventoryMappings(
    booking: Booking,
    hotel: Hotel,
): List<StubMapping> {
    val arrival = requireNotNull(booking.arrival)
    val departure = requireNotNull(booking.departure)
    val itemInventory =
        requireNotNull(hotel.itemInventory) {
            "Hotel ${hotel.hotelId} must declare itemInventory"
        }

    return operaLimitedIntervals(arrival, departure, requestedDays = 90).map { interval ->
        val inventoryDates =
            generateSequence(interval.startDate) { date -> date.plusDays(1) }
                .takeWhile { date -> !date.isAfter(interval.endDate) }
                .toList()
        StubMapping(
            request =
                RequestPattern(
                    method = "GET",
                    urlPath = "/inv/v1/hotels/${hotel.hotelId}/itemInventory",
                    queryParameters =
                        mapOf(
                            "welcomeOffer" to StringValuePattern(equalTo = "false"),
                            "sellInReservation" to StringValuePattern(equalTo = "true"),
                            "startDate" to StringValuePattern(equalTo = interval.startDate.toString()),
                            "endDate" to StringValuePattern(equalTo = interval.endDate.toString()),
                        ),
                    headers = hotelHeaders(hotel.hotelId),
                ),
            response =
                jsonResponse(
                    jsonBody =
                        stubJsonObject(
                            "itemsInventory" to
                                itemInventory.items.map { item ->
                                    mapOf(
                                        "description" to item.description,
                                        "code" to item.code,
                                        "name" to item.name,
                                        "inventories" to
                                            inventoryDates.map { date ->
                                                mapOf(
                                                    "date" to date.toString(),
                                                    "total" to item.total,
                                                    "available" to item.available,
                                                )
                                            },
                                    )
                                },
                            "links" to emptyList<Any>(),
                        ),
                ),
        )
    }
}
