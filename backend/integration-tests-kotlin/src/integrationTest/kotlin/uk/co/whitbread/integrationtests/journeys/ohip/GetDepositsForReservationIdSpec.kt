package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_DEPOSITS_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.reservationDepositsOperaFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

class GetDepositsForReservationIdSpec :
    JourneySpec(
        "Opera reservation deposits can be retrieved",
        {
            val ohipApi = OhipApi()

            scenario("a reservation with no posted deposits returns an empty deposit list") {
                val booking = depositBooking(reservationId = "6004302")
                val room = booking.room

                installFor(booking)

                val result =
                    ohipApi.getDepositsForReservationId(
                        hotelId = booking.hotel.hotelId,
                        reservationId = requireNotNull(room.reservationId),
                        testId = testId,
                    )

                result.attachEvidence("Get Reservation Deposits")

                expect("returns that the reservation has no posted deposits") {
                    result.response.status.value shouldBe 200
                    result.body.deposits.shouldBeEmpty()
                }

                expect("reads only the reservation deposit folio from Opera") {
                    // One Opera call: the reservation deposit-folio lookup.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION_DEPOSITS) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a posted reservation deposit returns its payment reference, amount, and currency") {
                val booking =
                    depositBooking(
                        reservationId = "6004303",
                        paymentReference = "PAY-6004303",
                        amountAlreadyPaid = 72.5,
                    )
                val room = booking.room

                installFor(booking)

                val result =
                    ohipApi.getDepositsForReservationId(
                        hotelId = booking.hotel.hotelId,
                        reservationId = requireNotNull(room.reservationId),
                        testId = testId,
                    )

                result.attachEvidence("Get Posted Reservation Deposit")

                expect("returns the posted deposit in the hotel's currency") {
                    result.response.status.value shouldBe 200
                    val deposit = result.body.deposits.single()
                    deposit.paymentReference shouldBe room.depositPaymentReference
                    deposit.postedAmount?.amount shouldBe room.amountAlreadyPaid
                    deposit.postedAmount?.currencyCode shouldBe booking.hotel.currency
                }

                expect("reads only the reservation deposit folio from Opera") {
                    // One Opera call: the reservation deposit-folio lookup.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION_DEPOSITS) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera deposit lookup failure returns the OHIP deposit error") {
                val booking = depositBooking(reservationId = "6004304")
                val room = booking.room

                // Exceptional: a downstream rejection cannot coexist as normal Booking world state.
                installFor(booking, excluded = setOf(OPERA_RESERVATION_DEPOSITS_STUB_ID))
                installStub(reservationDepositsOperaFailure(booking, room))

                val result =
                    ohipApi.getDepositsForReservationId(
                        hotelId = booking.hotel.hotelId,
                        reservationId = requireNotNull(room.reservationId),
                        testId = testId,
                    )

                result.attachEvidence("Get Reservation Deposits Opera Failure")

                expect("returns the OHIP deposit retrieval error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 909
                }

                expect("stops after the rejected Opera deposit lookup") {
                    // One Opera call: the rejected reservation deposit-folio lookup.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION_DEPOSITS) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun depositBooking(
    reservationId: String,
    paymentReference: String? = null,
    amountAlreadyPaid: Double = 0.0,
): Booking {
    val arrival = LocalDate.now().plusDays(28)
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms =
            listOf(
                BookingRoom(
                    reservationId = reservationId,
                    roomType = rate.roomType,
                    adults = rate.adults,
                    status = ReservationStatus.RESERVED,
                    amountAlreadyPaid = amountAlreadyPaid,
                    depositPaymentReference = paymentReference,
                ),
            ),
    )
}
