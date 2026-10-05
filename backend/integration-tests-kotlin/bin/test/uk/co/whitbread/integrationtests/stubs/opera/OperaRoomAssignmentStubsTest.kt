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
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import java.time.LocalDate

class OperaRoomAssignmentStubsTest :
    FunSpec({
        test("room-assignment mapping matches the real caller's POST shape") {
            val booking = frontOfficeBooking()
            val mapping =
                roomAssignment(booking, listOf(booking.room))
                    .mappings
                    .single()

            mapping.request.method shouldBe "POST"
            mapping.request.urlPath shouldBe
                "/fof/v1/hotels/FOHOTL/reservations/RSV-FO-1/roomAssignments"
            mapping.request.headers!!
                .getValue("x-hotelid")
                .equalTo shouldBe "FOHOTL"
            val jsonPaths = mapping.request.bodyPatterns!!.mapNotNull { it.matchesJsonPath }
            jsonPaths shouldContain "$[?(@.criteria.reservationIdList[0].id == \"RSV-FO-1\")]"
            jsonPaths shouldContain "$.criteria.roomId"
        }

        test("room-assignment response carries at least one link so callers see success") {
            val booking = frontOfficeBooking()
            val response =
                roomAssignment(booking, listOf(booking.room))
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

        test("room-assignment stub installs for every reservation room") {
            val gated = defaultStubsFor(frontOfficeBooking())
            val ungated = defaultStubsFor(frontOfficeBooking(reservationId = null))

            gated.map { it.id } shouldContain OPERA_ROOM_ASSIGNMENT_STUB_ID
            gated.single { it.id == OPERA_ROOM_ASSIGNMENT_STUB_ID }.mappings shouldHaveSize 1
            ungated.map { it.id } shouldNotContain OPERA_ROOM_ASSIGNMENT_STUB_ID
        }
    })

internal fun frontOfficeBooking(
    reservationId: String? = "RSV-FO-1",
    assignedRoomId: String? = null,
): Booking =
    Booking(
        hotels =
            listOf(
                Hotel(
                    hotelId = "FOHOTL",
                    shortId = "fo-hotel",
                    name = "Front Office Hotel",
                    addressLine = "1 Reception Way",
                    city = "London",
                    postcode = "FO1 TEST",
                    country = "GB",
                    phone = "+441110000002",
                    currency = "GBP",
                    availableRates =
                        listOf(
                            Rate(
                                ratePlan = "SEMIFLEX",
                                roomType = "DOUBLE",
                                adults = 2,
                                ratePlanSet = "PBF",
                            ),
                        ),
                ),
            ),
        arrival = LocalDate.of(2026, 9, 14),
        departure = LocalDate.of(2026, 9, 16),
        rooms =
            listOf(
                BookingRoom(
                    reservationId = reservationId,
                    roomType = "DOUBLE",
                    adults = 2,
                    status = ReservationStatus.RESERVED,
                    assignedRoomId = assignedRoomId,
                ),
            ),
    )
