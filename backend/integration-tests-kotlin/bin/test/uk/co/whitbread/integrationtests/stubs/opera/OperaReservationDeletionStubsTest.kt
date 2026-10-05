package uk.co.whitbread.integrationtests.stubs.opera

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.stubs.defaultStubsFor
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.Rate
import java.time.LocalDate

class OperaReservationDeletionStubsTest :
    FunSpec({
        test("reservation-deletion mapping matches the exact reservation DELETE and returns 204") {
            val booking = deletionBooking()
            val mapping =
                deleteReservation(booking, listOf(booking.room))
                    .mappings
                    .single()

            mapping.request.method shouldBe "DELETE"
            mapping.request.urlPath shouldBe "/rsv/v1/hotels/DLHOTL/reservations/RSV-DL-1"
            mapping.request.headers!!
                .getValue("x-hotelid")
                .equalTo shouldBe "DLHOTL"
            mapping.response.status shouldBe 204
            mapping.response.jsonBody shouldBe null
        }

        test("reservation-deletion stub installs for every reservation room and only those") {
            val withReservation = defaultStubsFor(deletionBooking())
            val withoutReservation =
                defaultStubsFor(
                    deletionBooking().let { booking ->
                        booking.copy(rooms = booking.rooms.map { it.copy(reservationId = null) })
                    },
                )

            withReservation.map { it.id } shouldContain OPERA_RESERVATION_DELETE_STUB_ID
            withoutReservation.map { it.id } shouldNotContain OPERA_RESERVATION_DELETE_STUB_ID
        }
    })

private fun deletionBooking(): Booking =
    Booking(
        hotels =
            listOf(
                Hotel(
                    hotelId = "DLHOTL",
                    shortId = "dl-hotel",
                    name = "Deletion Hotel",
                    addressLine = "1 Removal Road",
                    city = "Leeds",
                    postcode = "DL1 TEST",
                    country = "GB",
                    phone = "+441130000000",
                    currency = "GBP",
                    availableRates =
                        listOf(
                            Rate(
                                ratePlan = "SEMIFLEX",
                                roomType = "DOUBLE",
                                adults = 1,
                                ratePlanSet = "PBF",
                            ),
                        ),
                ),
            ),
        arrival = LocalDate.of(2026, 9, 15),
        departure = LocalDate.of(2026, 9, 17),
        rooms =
            listOf(
                BookingRoom(
                    reservationId = "RSV-DL-1",
                    roomType = "DOUBLE",
                    adults = 1,
                ),
            ),
    )
