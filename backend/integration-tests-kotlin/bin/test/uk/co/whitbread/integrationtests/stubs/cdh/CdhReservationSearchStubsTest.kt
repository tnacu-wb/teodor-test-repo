package uk.co.whitbread.integrationtests.stubs.cdh

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.maps.shouldNotContainKey
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.stubs.defaultStubsFor
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_EXTERNAL_REFERENCE_SEARCH_STUB_ID
import uk.co.whitbread.integrationtests.testkit.model.Booker
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import java.time.LocalDate

class CdhReservationSearchStubsTest :
    FunSpec({
        test("CDH booking state selects deterministic V2 and V3 reservation searches") {
            val booking = cdhBooking()
            val defaults = defaultStubsFor(booking)

            val stub =
                defaults
                    .single { it.id == CDH_RESERVATION_SEARCH_STUB_ID }

            defaults.map { it.id } shouldContain OPERA_EXTERNAL_REFERENCE_SEARCH_STUB_ID

            stub.mappings.map { it.request.urlPath } shouldContainExactly
                listOf(
                    "/BookingServices/V2/ReservationSearch",
                    "/BookingServices/V3/ReservationSearch",
                )
            stub.mappings.forEach { mapping ->
                mapping.request.method shouldBe "POST"
                mapping.request.bodyPatterns?.map { it.matchesJsonPath } shouldContainExactly
                    listOf(
                        "$[?(@.BookingReference == \"00600421\")]",
                        "$[?(@.BookingsDatabaseSearch == true)]",
                    )
            }
        }

        test("reservation search response derives the confirmation result from Booking") {
            val booking = cdhBooking()
            val response =
                reservationSearch(booking)
                    .mappings
                    .first()
                    .response
                    .jsonBody!!
                    .jsonObject
            val result =
                response
                    .getValue("Results")
                    .jsonArray
                    .single()
                    .jsonObject
            val room =
                result
                    .getValue("Rooms")
                    .jsonArray
                    .single()
                    .jsonObject

            response.getValue("TotalResults").jsonPrimitive.content shouldBe "1"
            result.getValue("BookingReference").jsonPrimitive.content shouldBe booking.cdhBookingReference
            result.getValue("HotelCode").jsonPrimitive.content shouldBe booking.hotel.hotelId
            result.getValue("ArrivalDate").jsonPrimitive.content shouldBe "${booking.arrival}T00:00:00Z"
            result.getValue("Status").jsonPrimitive.content shouldBe "RESERVED"
            result.getValue("Amendable").jsonPrimitive.content shouldBe "false"
            result.getValue("Cancellable").jsonPrimitive.content shouldBe "false"
            result.getValue("WalkIn").jsonPrimitive.content shouldBe "false"
            result.getValue("IsPackage").jsonPrimitive.content shouldBe "false"
            result.getValue("PrePaid").jsonPrimitive.content shouldBe "false"
            room.getValue("ReservationId").jsonPrimitive.content shouldBe booking.room.reservationId
            room.getValue("Cot").jsonPrimitive.content shouldBe "false"
            room.getValue("CarDetails").jsonPrimitive.content shouldBe "false"
            room.getValue("Guests").jsonArray shouldBe kotlinx.serialization.json.JsonArray(emptyList())
            result shouldNotContainKey "CancellationDate"
            result shouldNotContainKey "Booker"
        }

        test("no CDH reference leaves the reservation search out of the plan") {
            val defaults = defaultStubsFor(cdhBooking().copy(cdhBookingReference = null))

            defaults.map { it.id } shouldNotContain CDH_RESERVATION_SEARCH_STUB_ID
        }

        test("every booking room becomes its own result carrying that room's status") {
            val booking =
                cdhBooking(
                    rooms =
                        listOf(
                            BookingRoom(reservationId = "600421", status = ReservationStatus.RESERVED),
                            BookingRoom(reservationId = "600422", status = ReservationStatus.CHECKED_IN),
                            BookingRoom(
                                reservationId = "600423",
                                status = ReservationStatus.CANCELLED,
                                cancellationDate = LocalDate.of(2026, 8, 20),
                            ),
                        ),
                )
            val response =
                reservationSearch(booking)
                    .mappings
                    .first()
                    .response
                    .jsonBody!!
                    .jsonObject
            val results = response.getValue("Results").jsonArray.map { it.jsonObject }

            response.getValue("TotalResults").jsonPrimitive.content shouldBe "3"
            response.getValue("SearchResults").jsonPrimitive.content shouldBe "3"
            response.getValue("TotalSize").jsonPrimitive.content shouldBe "3"
            results.map { it.getValue("Status").jsonPrimitive.content } shouldContainExactly
                listOf("RESERVED", "INHOUSE", "CANCELLED")
            results.map {
                it
                    .getValue("Rooms")
                    .jsonArray
                    .single()
                    .jsonObject
                    .getValue("ReservationId")
                    .jsonPrimitive.content
            } shouldContainExactly listOf("600421", "600422", "600423")
            results.map { it.getValue("BookingReference").jsonPrimitive.content }.distinct() shouldContainExactly
                listOf(booking.cdhBookingReference)
        }

        test("only a cancelled room reports a cancellation date") {
            val booking =
                cdhBooking(
                    rooms =
                        listOf(
                            BookingRoom(reservationId = "600421", status = ReservationStatus.RESERVED),
                            BookingRoom(
                                reservationId = "600423",
                                status = ReservationStatus.CANCELLED,
                                cancellationDate = LocalDate.of(2026, 8, 20),
                            ),
                        ),
                )
            val results =
                reservationSearch(booking)
                    .mappings
                    .first()
                    .response
                    .jsonBody!!
                    .jsonObject
                    .getValue("Results")
                    .jsonArray
                    .map { it.jsonObject }

            results[0] shouldNotContainKey "CancellationDate"
            results[1].getValue("CancellationDate").jsonPrimitive.content shouldBe "2026-08-20T00:00:00Z"
        }

        test("a booker fact becomes a Booker block on every result") {
            val booking =
                cdhBooking(
                    rooms =
                        listOf(
                            BookingRoom(reservationId = "600421", status = ReservationStatus.RESERVED),
                            BookingRoom(reservationId = "600422", status = ReservationStatus.CHECKED_IN),
                        ),
                ).copy(
                    booker =
                        Booker(
                            firstName = "Ada",
                            lastName = "Blackwood",
                            title = "Ms",
                            email = "ada.blackwood@example.com",
                        ),
                )
            val results =
                reservationSearch(booking)
                    .mappings
                    .first()
                    .response
                    .jsonBody!!
                    .jsonObject
                    .getValue("Results")
                    .jsonArray
                    .map { it.jsonObject }

            results shouldHaveSize 2
            results.forEach { result ->
                val booker = result.getValue("Booker").jsonObject

                booker.getValue("Title").jsonPrimitive.content shouldBe "Ms"
                booker.getValue("FirstName").jsonPrimitive.content shouldBe "Ada"
                booker.getValue("LastName").jsonPrimitive.content shouldBe "Blackwood"
                booker.getValue("EmailAddress").jsonPrimitive.content shouldBe "ada.blackwood@example.com"
            }
        }

        test("a booker identity is independent of the room's guest") {
            val booking =
                cdhBooking().copy(
                    rooms =
                        listOf(
                            BookingRoom(
                                reservationId = "600421",
                                status = ReservationStatus.RESERVED,
                                guestProfile =
                                    GuestProfile(
                                        profileId = "9001",
                                        firstName = "Grace",
                                        lastName = "Hopper",
                                        email = "grace.hopper@example.com",
                                        phone = "020 0000 0001",
                                        addressLine = "1 Navy Road",
                                        city = "London",
                                        postcode = "SW1A 1AA",
                                    ),
                            ),
                        ),
                    booker = Booker(firstName = "Ada", lastName = "Blackwood"),
                )
            val result =
                reservationSearch(booking)
                    .mappings
                    .first()
                    .response
                    .jsonBody!!
                    .jsonObject
                    .getValue("Results")
                    .jsonArray
                    .single()
                    .jsonObject
            val booker = result.getValue("Booker").jsonObject
            val guest =
                result
                    .getValue("Rooms")
                    .jsonArray
                    .single()
                    .jsonObject
                    .getValue("Guests")
                    .jsonArray
                    .single()
                    .jsonObject

            booker.getValue("LastName").jsonPrimitive.content shouldBe "Blackwood"
            guest.getValue("LastName").jsonPrimitive.content shouldBe "Hopper"
            booker shouldNotContainKey "Title"
            booker shouldNotContainKey "EmailAddress"
        }

        test("a booker fact does not change which default stubs are planned") {
            val withBooker =
                cdhBooking().copy(booker = Booker(firstName = "Ada", lastName = "Blackwood"))

            defaultStubsFor(withBooker).map { it.id } shouldContainExactly
                defaultStubsFor(cdhBooking()).map { it.id }
            defaultStubsFor(withBooker)
                .single { it.id == CDH_RESERVATION_SEARCH_STUB_ID }
                .mappings
                .first()
                .response
                .jsonBody!!
                .jsonObject
                .getValue("Results")
                .jsonArray
                .single()
                .jsonObject shouldContainKey "Booker"
        }

        test("a booker fact without a CDH reference still plans no reservation search") {
            val defaults =
                defaultStubsFor(
                    cdhBooking().copy(
                        cdhBookingReference = null,
                        booker = Booker(firstName = "Ada", lastName = "Blackwood"),
                    ),
                )

            defaults.map { it.id } shouldNotContain CDH_RESERVATION_SEARCH_STUB_ID
        }
    })

private fun cdhBooking(rooms: List<BookingRoom>? = null): Booking {
    val arrival = LocalDate.of(2026, 9, 10)
    return Booking(
        hotels =
            listOf(
                Hotel(
                    hotelId = "HEAPTI",
                    shortId = "LHR",
                    name = "Heathrow Terminal 4",
                    addressLine = "Sheffield Road",
                    city = "London",
                    postcode = "TW6 3AF",
                    phone = "020 0000 0000",
                ),
            ),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms =
            rooms
                ?: listOf(
                    BookingRoom(
                        reservationId = "600421",
                        status = ReservationStatus.RESERVED,
                    ),
                ),
        cdhBookingReference = "00600421",
    )
}
