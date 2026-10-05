package uk.co.whitbread.integrationtests.stubs.opera

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.stubs.defaultStubsFor
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import java.time.LocalDate

/**
 * Characterizes the Opera cashiering folio window: the money it reports posted against a
 * reservation, and the separate fact that lets the folio hold more than the reservation's own
 * money summary reports — the only world in which a consumer reconciling the two is observable.
 */
class OperaFolioStubsTest :
    FunSpec({
        val arrival: LocalDate = LocalDate.of(2026, 9, 1)
        val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2, nightlyRate = 100.0)
        val booking =
            Booking(
                hotels =
                    listOf(
                        Hotel(
                            hotelId = "FOLIO1",
                            shortId = "folio-hotel",
                            name = "Folio Hotel",
                            addressLine = "1 Gate Street",
                            city = "London",
                            postcode = "SW1A 1AA",
                            phone = "02079460000",
                            availableRates = listOf(rate),
                        ),
                    ),
                arrival = arrival,
                departure = arrival.plusDays(2),
                rooms =
                    listOf(
                        BookingRoom(
                            reservationId = "RSV-FOLIO-1",
                            roomType = rate.roomType,
                            adults = rate.adults,
                            status = ReservationStatus.RESERVED,
                        ),
                    ),
            )

        fun windowFor(room: BookingRoom) =
            reservationFolios(booking, room)
                .mappings
                .single()
                .response.jsonBody!!
                .jsonObject
                .getValue("reservationFolioInformation")
                .jsonObject
                .getValue("folioWindows")
                .jsonArray
                .single()
                .jsonObject

        test("the folio read matches the reservation's own cashiering folio URL") {
            val request = reservationFolios(booking, booking.room).mappings.single().request

            request.method shouldBe "GET"
            request.urlPath shouldBe "/csh/v1/hotels/FOLIO1/reservations/RSV-FOLIO-1/folios"
        }

        test("a reservation with nothing taken against it reports one empty folio window") {
            val window = windowFor(booking.room)

            window.getValue("emptyFolio").jsonPrimitive.content shouldBe "true"
            window.getValue("emptyWindow").jsonPrimitive.content shouldBe "true"
            window
                .getValue("payment")
                .jsonObject
                .getValue("amount")
                .jsonPrimitive
                .content shouldBe "0.0"
        }

        test("a part-paid reservation posts the money already taken on the folio") {
            val window = windowFor(booking.room.copy(amountAlreadyPaid = 40.0))

            window.getValue("emptyFolio").jsonPrimitive.content shouldBe "false"
            window
                .getValue("payment")
                .jsonObject
                .getValue("amount")
                .jsonPrimitive
                .content shouldBe "40.0"
        }

        test("a folio holding more than the reservation summary reports posts that larger amount") {
            val window =
                windowFor(booking.room.copy(amountAlreadyPaid = 40.0, amountPostedOnFolio = 90.0))

            window.getValue("emptyFolio").jsonPrimitive.content shouldBe "false"
            window
                .getValue("payment")
                .jsonObject
                .getValue("amount")
                .jsonPrimitive
                .content shouldBe "90.0"
        }

        test("a posted amount of zero empties the window even when the summary reports a payment") {
            val window =
                windowFor(booking.room.copy(amountAlreadyPaid = 40.0, amountPostedOnFolio = 0.0))

            window.getValue("emptyFolio").jsonPrimitive.content shouldBe "true"
            window
                .getValue("payment")
                .jsonObject
                .getValue("amount")
                .jsonPrimitive
                .content shouldBe "0.0"
        }

        test("the posted-amount fact rides the reservation gate rather than adding one of its own") {
            val posted =
                booking.copy(
                    rooms =
                        booking.rooms.map {
                            it.copy(amountAlreadyPaid = 40.0, amountPostedOnFolio = 90.0)
                        },
                )

            defaultStubsFor(posted).map { it.id } shouldContain OPERA_RESERVATION_FOLIOS_STUB_ID
            defaultStubsFor(posted)
                .single { it.id == OPERA_RESERVATION_FOLIOS_STUB_ID }
                .mappings
                .single()
                .response
                .jsonBody!!
                .jsonObject
                .getValue("reservationFolioInformation")
                .jsonObject
                .getValue("folioWindows")
                .jsonArray
                .single()
                .jsonObject
                .getValue("payment")
                .jsonObject
                .getValue("amount")
                .jsonPrimitive
                .content shouldBe "90.0"
        }

        test("a posted amount cannot be negative") {
            shouldThrow<IllegalArgumentException> {
                BookingRoom(amountPostedOnFolio = -1.0)
            }.message.orEmpty() shouldBe "bookingRoom.amountPostedOnFolio must be finite and non-negative"
        }
    })
