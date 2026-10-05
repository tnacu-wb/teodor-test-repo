package uk.co.whitbread.integrationtests.stubs.opera

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import uk.co.whitbread.integrationtests.stubs.defaultStubsFor
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.Rate
import java.time.LocalDate

class OperaPreCheckInStubsTest :
    FunSpec({
        test("pre-check-in mapping matches the real caller's POST shape") {
            val booking = preCheckInBooking(preCheckInAvailable = true)
            val mapping =
                preCheckInStatus(booking, listOf(booking.room))
                    .mappings
                    .single()

            mapping.request.method shouldBe "POST"
            mapping.request.urlPath shouldBe
                "/rsv/v1/hotels/PCHOTL/reservations/RSV-PC-1/preCheckIn"
            mapping.request.headers!!
                .getValue("x-hotelid")
                .equalTo shouldBe "PCHOTL"
            val jsonPaths = mapping.request.bodyPatterns!!.mapNotNull { it.matchesJsonPath }
            jsonPaths shouldContain "$[?(@.reservation.hotelId == \"PCHOTL\")]"
            jsonPaths shouldContain "$.reservation.preCheckInDetails.arrival.arrivalTime"
        }

        test("pre-check-in response carries at least one link so callers see success") {
            val booking = preCheckInBooking(preCheckInAvailable = true)
            val response =
                preCheckInStatus(booking, listOf(booking.room))
                    .mappings
                    .single()
                    .response

            response.status shouldBe 200
            response.jsonBody!!
                .jsonObject
                .getValue("links")
                .jsonArray
                .shouldNotBeEmpty()
        }

        test("pre-check-in stub installs only for rooms declaring preCheckInAvailable") {
            val gated = defaultStubsFor(preCheckInBooking(preCheckInAvailable = true))
            val ungated = defaultStubsFor(preCheckInBooking(preCheckInAvailable = false))

            gated.map { it.id } shouldContain OPERA_PRE_CHECK_IN_STUB_ID
            gated
                .single { it.id == OPERA_PRE_CHECK_IN_STUB_ID }
                .mappings shouldHaveSize 1
            ungated.map { it.id } shouldNotContain OPERA_PRE_CHECK_IN_STUB_ID
        }
    })

private fun preCheckInBooking(preCheckInAvailable: Boolean): Booking =
    Booking(
        hotels =
            listOf(
                Hotel(
                    hotelId = "PCHOTL",
                    shortId = "pc-hotel",
                    name = "Pre-Check-In Hotel",
                    addressLine = "1 Arrival Way",
                    city = "London",
                    postcode = "PC1 TEST",
                    country = "GB",
                    phone = "+441110000000",
                    currency = "GBP",
                    availableRates =
                        listOf(
                            Rate(ratePlan = "SEMIFLEX", roomType = "DOUBLE", adults = 2),
                        ),
                ),
            ),
        arrival = LocalDate.of(2026, 9, 10),
        departure = LocalDate.of(2026, 9, 12),
        rooms =
            listOf(
                BookingRoom(
                    reservationId = "RSV-PC-1",
                    roomType = "DOUBLE",
                    adults = 2,
                    preCheckInAvailable = preCheckInAvailable,
                ),
            ),
    )
