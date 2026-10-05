package uk.co.whitbread.integrationtests.stubs.opera

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.double
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.opera.custom.OPERA_RESERVATION_DEPOSITS_REJECTED_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.reservationDepositsOperaFailure
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import java.time.LocalDate

class OperaReservationDepositsStubsTest :
    FunSpec({
        test("reservation deposit response stays empty when no payment reference is present") {
            val booking = depositBooking()

            deposits(reservationDeposits(booking, listOf(booking.room)))
                .shouldBeEmpty()
        }

        test("reservation deposit response derives a posted deposit from reusable booking facts") {
            val booking = depositBooking(paymentReference = "PAY-7421", amountAlreadyPaid = 72.5)
            val deposit = deposits(reservationDeposits(booking, listOf(booking.room))).single().jsonObject

            deposit.getValue("reference").jsonPrimitive.content shouldBe "PAY-7421"
            deposit
                .getValue("postedAmount")
                .jsonObject
                .getValue("amount")
                .jsonPrimitive
                .double shouldBe 72.5
            deposit
                .getValue("postedAmount")
                .jsonObject
                .getValue("currencyCode")
                .jsonPrimitive
                .content shouldBe "EUR"
        }

        test("reservation deposit failure carries its own id and retains the generic request matcher") {
            val booking = depositBooking()
            val default = reservationDeposits(booking, listOf(booking.room))
            val failure = reservationDepositsOperaFailure(booking, booking.room)

            failure.id shouldBe OPERA_RESERVATION_DEPOSITS_REJECTED_STUB_ID
            failure.target shouldBe default.target
            failure.mappings.single().request shouldBe default.mappings.single().request
            failure.mappings
                .single()
                .response.status shouldBe 500
        }
    })

private fun deposits(stub: PlannedStub) =
    stub.mappings
        .single()
        .response
        .jsonBody!!
        .jsonObject
        .getValue("reservationDepositFoliosInfo")
        .jsonArray
        .single()
        .jsonObject
        .getValue("deposits")
        .jsonArray

private fun depositBooking(
    paymentReference: String? = null,
    amountAlreadyPaid: Double = 0.0,
): Booking =
    Booking(
        hotels =
            listOf(
                Hotel(
                    hotelId = "DEP01",
                    shortId = "dep-hotel",
                    name = "Deposit Hotel",
                    addressLine = "1 Deposit Street",
                    city = "Dublin",
                    postcode = "D01 TEST",
                    country = "IE",
                    phone = "+35310000000",
                    currency = "EUR",
                ),
            ),
        arrival = LocalDate.of(2026, 9, 1),
        departure = LocalDate.of(2026, 9, 3),
        rooms =
            listOf(
                BookingRoom(
                    reservationId = "RSV-DEP-1",
                    roomType = "DOUBLE",
                    adults = 2,
                    amountAlreadyPaid = amountAlreadyPaid,
                    depositPaymentReference = paymentReference,
                ),
            ),
    )
