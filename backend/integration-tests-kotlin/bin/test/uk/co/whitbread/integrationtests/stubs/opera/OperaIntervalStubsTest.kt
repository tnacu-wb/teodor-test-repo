package uk.co.whitbread.integrationtests.stubs.opera

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.HotelInventoryItem
import uk.co.whitbread.integrationtests.testkit.model.HotelItemInventory
import uk.co.whitbread.integrationtests.testkit.model.HotelRoomType
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.RoomInventoryPeriod
import java.time.LocalDate

class OperaIntervalStubsTest :
    FunSpec({
        test("hotel inventory uses dated room stock across 90-day request chunks") {
            val arrival = LocalDate.of(2030, 1, 1)
            val departure = arrival.plusDays(90)
            val booking =
                booking(
                    arrival = arrival,
                    departure = departure,
                    roomTypes =
                        listOf(
                            HotelRoomType(
                                roomClass = "ST",
                                roomType = "DOUBLE",
                                numberOfRooms = 7,
                                inventoryPeriods =
                                    listOf(
                                        RoomInventoryPeriod(
                                            startDate = departure,
                                            endDate = departure,
                                            numberOfRooms = 3,
                                        ),
                                    ),
                            ),
                        ),
                )

            val mappings = hotelInventory(booking).mappings

            mappings.map { mapping -> mapping.request.query("dateRangeStart") } shouldContainExactly
                listOf(arrival.toString(), departure.toString())
            mappings.map { mapping -> mapping.request.query("dateRangeEnd") } shouldContainExactly
                listOf(departure.minusDays(1).toString(), departure.toString())
            mappings.map { mapping -> mapping.roomAvailableCount() } shouldContainExactly listOf(7, 3)
        }

        test("item inventory returns only the dates belonging to each Opera interval") {
            val arrival = LocalDate.of(2030, 1, 1)
            val departure = arrival.plusDays(90)
            val booking =
                booking(
                    arrival = arrival,
                    departure = departure,
                    itemInventory =
                        HotelItemInventory(
                            items = listOf(HotelInventoryItem(code = "COT", name = "Travel Cot", total = 3)),
                        ),
                )

            val mappings = hotelItemInventory(booking).mappings

            mappings.map { mapping -> mapping.request.query("startDate") } shouldContainExactly
                listOf(arrival.toString(), arrival.plusDays(89).toString())
            mappings.map { mapping -> mapping.request.query("endDate") } shouldContainExactly
                listOf(arrival.plusDays(88).toString(), departure.toString())
            mappings.map { mapping -> mapping.itemInventoryDates().size } shouldContainExactly listOf(89, 2)
        }

        test("rate info calculates each overlapping interval response independently") {
            val arrival = LocalDate.of(2030, 1, 1)
            val departure = arrival.plusDays(22)
            val rate =
                Rate(
                    ratePlan = "FLEX",
                    ratePlanSet = "PUBLIC",
                    roomType = "DOUBLE",
                    adults = 2,
                    nightlyRate = 72.0,
                )
            val booking = booking(arrival = arrival, departure = departure, rates = listOf(rate))

            val mappings = rateInfo(booking, rate).mappings

            // Two summaryInfo caller-shape variants per interval share one interval response.
            mappings.map { mapping -> mapping.request.query("criteriaStartDate") }.distinct() shouldContainExactly
                listOf(arrival.toString(), arrival.plusDays(19).toString())
            mappings.map { mapping -> mapping.request.query("criteriaEndDate") }.distinct() shouldContainExactly
                listOf(arrival.plusDays(19).toString(), departure.toString())
            mappings
                .distinctBy { mapping -> mapping.request.query("criteriaStartDate") }
                .sumOf { mapping -> mapping.rateInfoNetTotal() } shouldBe 72.0 * 22
        }
    })

private fun booking(
    arrival: LocalDate,
    departure: LocalDate,
    roomTypes: List<HotelRoomType> = emptyList(),
    itemInventory: HotelItemInventory? = null,
    rates: List<Rate> = emptyList(),
): Booking =
    Booking(
        hotels =
            listOf(
                Hotel(
                    hotelId = "INTERVAL01",
                    shortId = "INT",
                    name = "Interval Hotel",
                    addressLine = "1 Interval Street",
                    city = "London",
                    postcode = "SW1A 1AA",
                    phone = "02070000000",
                    availableRoomTypes = roomTypes,
                    availableRates = rates,
                    itemInventory = itemInventory,
                ),
            ),
        arrival = arrival,
        departure = departure,
    )

private fun uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern.query(name: String): String =
    requireNotNull(queryParameters?.get(name)?.equalTo)

private fun StubMapping.roomAvailableCount(): Int =
    response.jsonBody!!
        .jsonObject
        .getValue("hotelInventories")
        .jsonArray
        .single()
        .jsonObject
        .getValue("roomTypeInventories")
        .jsonArray
        .single()
        .jsonObject
        .getValue("inventoryCounts")
        .jsonArray
        .single()
        .jsonObject
        .getValue("availableCount")
        .jsonPrimitive
        .content
        .toInt()

private fun StubMapping.itemInventoryDates(): List<String> =
    response.jsonBody!!
        .jsonObject
        .getValue("itemsInventory")
        .jsonArray
        .single()
        .jsonObject
        .getValue("inventories")
        .jsonArray
        .map { inventory ->
            inventory.jsonObject
                .getValue("date")
                .jsonPrimitive.content
        }

private fun StubMapping.rateInfoNetTotal(): Double =
    response.jsonBody!!
        .jsonObject
        .getValue("summary")
        .jsonObject
        .getValue("net")
        .jsonPrimitive
        .content
        .toDouble()
