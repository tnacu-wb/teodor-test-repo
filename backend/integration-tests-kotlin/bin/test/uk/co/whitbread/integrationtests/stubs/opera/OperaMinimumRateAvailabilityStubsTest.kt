package uk.co.whitbread.integrationtests.stubs.opera

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.stubs.defaultStubsFor
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.HotelRoomType
import uk.co.whitbread.integrationtests.testkit.model.Rate
import java.time.LocalDate

class OperaMinimumRateAvailabilityStubsTest :
    FunSpec({
        test("minimum-rate mapping matches the caller's POST body and hotel header") {
            val booking = minimumRateBooking()
            val mapping = minimumRateAvailability(booking).mappings.single()

            mapping.request.method shouldBe "POST"
            mapping.request.urlPath shouldBe "/parext/v1/hotels/minimumRateAvailability"
            mapping.request.headers
                .shouldNotBeNull()
                .getValue("x-hotelid")
                .equalTo shouldBe "MRA001"
            val jsonPaths = mapping.request.bodyPatterns!!.mapNotNull { it.matchesJsonPath }
            jsonPaths shouldContain "$[?(@.arrivalDate == '2026-09-10')]"
            jsonPaths shouldContain "$[?(@.departureDate == '2026-09-12')]"
            jsonPaths shouldContain "$[?(@.hotelIds[0] == 'MRA001')]"
            jsonPaths shouldContain "$.rooms"
        }

        test("every room stay carries availability and minimumRate derived from the facts") {
            val booking = minimumRateBooking()
            val roomStays =
                minimumRateAvailability(booking)
                    .mappings
                    .single()
                    .response.jsonBody!!
                    .jsonObject
                    .getValue("roomStays")
                    .jsonArray
                    .associateBy { stay ->
                        stay.jsonObject
                            .getValue("propertyInfo")
                            .jsonObject
                            .getValue("hotelCode")
                            .jsonPrimitive.content
                    }

            val stocked = roomStays.getValue("MRA001").jsonObject
            val soldOut = roomStays.getValue("MRA002").jsonObject
            stocked.getValue("availability").jsonPrimitive.content shouldBe "AvailableForSale"
            // Two nights at the cheapest configured nightly rate of 49.0.
            stocked
                .getValue("minimumRate")
                .jsonObject
                .getValue("amountAfterTax")
                .jsonPrimitive.content shouldBe "98.0"
            soldOut.getValue("availability").jsonPrimitive.content shouldBe "NoAvailability"
            soldOut
                .getValue("minimumRate")
                .jsonObject
                .getValue("currencyCode")
                .jsonPrimitive.content shouldBe "GBP"
        }

        test("a stay longer than the Opera window installs one overlapping mapping per interval") {
            val booking =
                minimumRateBooking().copy(
                    departure = LocalDate.of(2026, 9, 10).plusDays(120),
                )
            val mappings = minimumRateAvailability(booking).mappings

            mappings shouldHaveSize 2
            val firstPaths = mappings[0].request.bodyPatterns!!.mapNotNull { it.matchesJsonPath }
            val secondPaths = mappings[1].request.bodyPatterns!!.mapNotNull { it.matchesJsonPath }
            firstPaths shouldContain "$[?(@.arrivalDate == '2026-09-10')]"
            // The second interval starts one day before the first ends so nightly rates join up.
            secondPaths shouldContain "$[?(@.arrivalDate == '${LocalDate.of(2026, 9, 10).plusDays(88)}')]"
            secondPaths shouldContain "$[?(@.departureDate == '${LocalDate.of(2026, 9, 10).plusDays(120)}')]"
        }

        test("gate: availability bookings install the stub, reservation bookings do not") {
            val ids = defaultStubsFor(minimumRateBooking()).map { it.id }
            val reservationIds =
                defaultStubsFor(
                    minimumRateBooking().copy(
                        rooms =
                            listOf(
                                BookingRoom(
                                    reservationId = "RSV-1",
                                    roomType = "DOUBLE",
                                    ratePlan = "FLEXRATE",
                                    adults = 2,
                                ),
                            ),
                    ),
                ).map { it.id }

            ids shouldContain OPERA_MINIMUM_RATE_AVAILABILITY_STUB_ID
            reservationIds shouldNotContain OPERA_MINIMUM_RATE_AVAILABILITY_STUB_ID
        }
    })

private fun minimumRateHotel(
    hotelId: String,
    roomCount: Int,
    rates: List<Rate>,
): Hotel =
    Hotel(
        hotelId = hotelId,
        shortId = "mr-$hotelId",
        name = "Minimum Rate $hotelId",
        addressLine = "1 Summary Street",
        city = "London",
        postcode = "SW1A 1AA",
        phone = "02079460000",
        availableRoomTypes = listOf(HotelRoomType(roomClass = "ST", roomType = "DOUBLE", numberOfRooms = roomCount)),
        availableRates = rates,
    )

private fun minimumRateBooking(): Booking =
    Booking(
        hotels =
            listOf(
                minimumRateHotel(
                    "MRA001",
                    roomCount = 5,
                    rates =
                        listOf(
                            Rate(ratePlan = "FLEXRATE", ratePlanSet = "PBF", roomType = "DOUBLE", adults = 2, nightlyRate = 59.0),
                            Rate(ratePlan = "SAVER", ratePlanSet = "PBN", roomType = "DOUBLE", adults = 2, nightlyRate = 49.0),
                        ),
                ),
                minimumRateHotel(
                    "MRA002",
                    roomCount = 0,
                    rates = listOf(Rate(ratePlan = "FLEXRATE", ratePlanSet = "PBF", roomType = "DOUBLE", adults = 2)),
                ),
            ),
        arrival = LocalDate.of(2026, 9, 10),
        departure = LocalDate.of(2026, 9, 12),
        rooms = listOf(BookingRoom(roomType = "DB", adults = 2)),
    )
