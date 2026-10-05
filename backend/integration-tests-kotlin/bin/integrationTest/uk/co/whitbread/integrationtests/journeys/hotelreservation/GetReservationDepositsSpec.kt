package uk.co.whitbread.integrationtests.journeys.hotelreservation

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.hotelreservation.HotelReservationApi
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

/**
 * Proves `GET /v1/reservations/deposits`: hotel-reservation-entity-service passes the hotel and
 * reservation straight through ohip-adapter-service to the single Opera deposit-folio read,
 * returning each posted deposit's payment reference, amount and currency, an empty list when the
 * reservation has no posted deposits, and the OHIP deposit error code 909 as a 500 when Opera
 * rejects the read.
 *
 * Flow: backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/GetReservationDeposits.md
 */
class GetReservationDepositsSpec :
    JourneySpec(
        "Hotel reservation returns a reservation's posted deposits",
        {
            val hotelReservationApi = HotelReservationApi()

            scenario("a posted reservation deposit is returned with its reference, amount and currency") {
                val booking =
                    depositsBooking(
                        reservationId = "6107601",
                        depositPaymentReference = "PAY-6107601",
                        amountAlreadyPaid = 72.5,
                    )
                val room = booking.room

                installFor(booking)

                val result =
                    hotelReservationApi.getReservationDeposits(
                        hotelId = booking.hotel.hotelId,
                        reservationId = requireNotNull(room.reservationId),
                        testId = testId,
                    )

                result.attachEvidence("Get Reservation Deposits")

                expect("returns the posted deposit") {
                    result.response.status.value shouldBe 200
                    val deposit = result.body.deposits.single()
                    deposit.paymentReference shouldBe room.depositPaymentReference
                    deposit.postedAmount?.amount shouldBe room.amountAlreadyPaid
                    deposit.postedAmount?.currencyCode shouldBe booking.hotel.currency
                }

                expect("calls only the Opera deposit folio") {
                    // One Opera call: GET /csh/v1/hotels/{hotelId}/depositFolio?id={reservationId}.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION_DEPOSITS) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a reservation with no posted deposits returns an empty deposit list") {
                val booking = depositsBooking(reservationId = "6107602")

                installFor(booking)

                val result =
                    hotelReservationApi.getReservationDeposits(
                        hotelId = booking.hotel.hotelId,
                        reservationId = requireNotNull(booking.room.reservationId),
                        testId = testId,
                    )

                result.attachEvidence("Get Reservation Deposits Empty")

                expect("returns no deposits") {
                    result.response.status.value shouldBe 200
                    result.body.deposits.shouldBeEmpty()
                }

                expect("calls only the Opera deposit folio") {
                    // One Opera call: GET /csh/v1/hotels/{hotelId}/depositFolio?id={reservationId}.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION_DEPOSITS) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera deposit folio failure surfaces as 500 errCode 909") {
                val booking = depositsBooking(reservationId = "6107603")

                // A downstream rejection is exceptional behavior, not a normal Booking world
                // state, so the generic deposit read is excluded and answered by the custom
                // failure stub carrying its own id.
                installFor(booking, excluded = setOf(OPERA_RESERVATION_DEPOSITS_STUB_ID))
                installStub(reservationDepositsOperaFailure(booking, booking.room))

                val result =
                    hotelReservationApi.getReservationDeposits(
                        hotelId = booking.hotel.hotelId,
                        reservationId = requireNotNull(booking.room.reservationId),
                        testId = testId,
                    )

                result.attachEvidence("Get Reservation Deposits Opera Failure")

                expect("returns the OHIP deposit error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 909
                }

                expect("stops after the rejected Opera lookup") {
                    // One Opera call: the rejected GET /csh/v1/hotels/{hotelId}/depositFolio.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION_DEPOSITS) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun depositsBooking(
    reservationId: String,
    depositPaymentReference: String? = null,
    amountAlreadyPaid: Double = 0.0,
): Booking {
    val arrival = LocalDate.now().plusDays(21)
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
                    depositPaymentReference = depositPaymentReference,
                ),
            ),
    )
}
