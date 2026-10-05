package uk.co.whitbread.integrationtests.stubs.opera

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.jsonObject
import uk.co.whitbread.integrationtests.stubs.defaultStubsFor
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import java.time.LocalDate

class OperaExternalReferenceStubsTest :
    FunSpec({
        test("the fourth-character-R search matcher includes Opera's wildcard suffix") {
            val booking = Booking(bookingReference = "BARREL42")
            val mapping = reservationsByExternalReference(booking, emptyList()).mappings.single()

            mapping.request.queryParameters
                ?.get("externalReferenceIds")
                ?.equalTo shouldBe "BARREL42-%"
        }

        test("a reference with no reservation rooms selects a successful no-match response") {
            val booking = Booking(bookingReference = "UNKNOWN42")

            defaultStubsFor(booking).map { it.id } shouldContain OPERA_EXTERNAL_REFERENCE_SEARCH_STUB_ID

            val reservationInfo =
                defaultStubsFor(booking)
                    .single { it.id == OPERA_EXTERNAL_REFERENCE_SEARCH_STUB_ID }
                    .mappings
                    .single()
                    .response
                    .jsonBody!!
                    .jsonObject
                    .getValue("reservations")
                    .jsonObject
                    .getValue("reservationInfo")

            reservationInfo shouldBe JsonNull
        }

        test("a populated response requires both stay dates") {
            val room = BookingRoom()
            val arrival = LocalDate.of(2026, 8, 20)
            val departure = arrival.plusDays(1)

            val missingArrival =
                shouldThrow<IllegalArgumentException> {
                    reservationsByExternalReference(
                        Booking(bookingReference = "BART7421", departure = departure),
                        listOf(room),
                    )
                }
            val missingDeparture =
                shouldThrow<IllegalArgumentException> {
                    reservationsByExternalReference(
                        Booking(bookingReference = "BART7421", arrival = arrival),
                        listOf(room),
                    )
                }

            missingArrival.message shouldBe
                "booking.arrival must be configured for external-reference search stubs"
            missingDeparture.message shouldBe
                "booking.departure must be configured for external-reference search stubs"
        }

        test("a CDH reference selects an empty Opera external-reference result") {
            val booking = Booking(cdhBookingReference = "00600421")

            val stub = reservationsByExternalReference(booking, emptyList())
            val mapping = stub.mappings.single()

            mapping.request.queryParameters
                ?.get("externalReferenceIds")
                ?.equalTo shouldBe "00600421"
            mapping.response.jsonBody!!
                .jsonObject
                .getValue("reservations")
                .jsonObject
                .getValue("reservationInfo") shouldBe JsonNull
        }

        test("external and CDH references install matching and empty mappings together") {
            val booking =
                Booking(
                    bookingReference = "BART7421",
                    cdhBookingReference = "00600421",
                )

            val mappings = reservationsByExternalReference(booking, emptyList()).mappings

            mappings.map {
                it.request.queryParameters
                    ?.get("externalReferenceIds")
                    ?.equalTo
            } shouldBe listOf("BART7421", "00600421")
            mappings.forEach { mapping ->
                mapping.response.jsonBody!!
                    .jsonObject
                    .getValue("reservations")
                    .jsonObject
                    .getValue("reservationInfo") shouldBe JsonNull
            }
        }
    })
