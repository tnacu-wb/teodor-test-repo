package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationEmpty
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.OperaPaymentCard
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.model.RoutingInstruction
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

/**
 * Proves `PUT /ohip/v1/reservations/movePaymentDetails`: the adapter loads each requested
 * reservation from Opera, enriches its saved card from Front Desk credit-card-info, and PUTs
 * a change-reservation moving payment details to folio window 2, returning `200 OK` with no
 * body. When `release_set_cnp_booking_alerts` is enabled, reservations routed to folio
 * window 2 additionally receive a CNP check-in alert via a further change-reservation PUT.
 *
 * The opera token-service flags are environment-pinned OFF (infrastructure scope, not
 * overridable per request), so no overrides are sent for them.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/MovePaymentDetails.md
 */
class MovePaymentDetailsSpec :
    JourneySpec(
        "OHIP adapter moves reservation payment details to folio window 2",
        {
            val ohipApi = OhipApi()

            scenario("payment details move to folio window 2 without a CNP alert when the alert flag is off") {
                val booking =
                    movePaymentDetailsBooking(
                        reservationId = "6012001",
                        cardId = "77011",
                        routingInstructions = emptyList(),
                    )
                val room = booking.room

                installFor(booking)

                val result =
                    ohipApi.movePaymentDetails(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(room.reservationId!!),
                        testId = testId,
                        featureFlagOverrides = mapOf(OhipFeatureFlag.SET_CNP_BOOKING_ALERTS to false),
                    )

                result.attachEvidence("Move Payment Details Flag Off")

                expect("acknowledges the move with 200 and no body") {
                    result.response.status.value shouldBe 200
                }

                expect("loads the reservation, enriches the card, and moves payment in one PUT with no alert PUT") {
                    // Three calls: reservation GET, Front Desk credit-card-info GET, and the
                    // change-reservation PUT. The exact count proves no CNP alert PUT was sent.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a folio-window-2-routed reservation also receives a CNP alert when the alert flag is on") {
                val booking =
                    movePaymentDetailsBooking(
                        reservationId = "6012002",
                        cardId = "77012",
                        routingInstructions = listOf(RoutingInstruction(folioWindowNumber = 2)),
                    )
                val room = booking.room

                installFor(booking)

                val result =
                    ohipApi.movePaymentDetails(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(room.reservationId!!),
                        testId = testId,
                        featureFlagOverrides = mapOf(OhipFeatureFlag.SET_CNP_BOOKING_ALERTS to true),
                    )

                result.attachEvidence("Move Payment Details CNP Alert")

                expect("acknowledges the move with 200 and no body") {
                    result.response.status.value shouldBe 200
                }

                expect("adds the CNP check-in alert with a second change-reservation PUT") {
                    // Four calls: reservation GET, credit-card-info GET, the payment-move PUT,
                    // and the flag-gated CNP alert PUT for the folio-window-2 routing.
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a reservation Opera holds nothing for fails the move without card lookup or PUT") {
                val booking =
                    movePaymentDetailsBooking(
                        reservationId = "6012101",
                        cardId = "77111",
                        routingInstructions = emptyList(),
                    )
                val room = booking.room

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationEmpty(booking.hotel.hotelId, room.reservationId!!))

                val result =
                    ohipApi.movePaymentDetails(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(room.reservationId),
                        testId = testId,
                        featureFlagOverrides = mapOf(OhipFeatureFlag.SET_CNP_BOOKING_ALERTS to false),
                    )

                result.attachEvidence("Move Payment Details Missing Reservation")

                expect("returns the mapped find-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 24
                }

                expect("stops after the empty reservation read, never enriching or PUTting") {
                    // One Opera call: the empty reservation GET. The credit-card-info and
                    // change-reservation PUT defaults are installed, so the exact count proves
                    // neither ran.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a reservation without a saved card is skipped silently") {
                val booking =
                    movePaymentDetailsBooking(
                        reservationId = "6012102",
                        cardId = "unused",
                        routingInstructions = emptyList(),
                        withCard = false,
                    )
                val room = booking.room

                installFor(booking)

                val result =
                    ohipApi.movePaymentDetails(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(room.reservationId!!),
                        testId = testId,
                        featureFlagOverrides = mapOf(OhipFeatureFlag.SET_CNP_BOOKING_ALERTS to false),
                    )

                result.attachEvidence("Move Payment Details Cardless Skip")

                expect("acknowledges with 200 and no body") {
                    result.response.status.value shouldBe 200
                }

                expect("reads the reservation but never moves payment details") {
                    // One Opera call: the reservation GET only. The change-reservation PUT
                    // default is installed, so the exact count proves the cardless skip (a
                    // credit-card-info mock cannot exist in a cardless world — there is no
                    // card id to match — so the PUT absence carries the proof).
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun movePaymentDetailsBooking(
    reservationId: String,
    cardId: String,
    routingInstructions: List<RoutingInstruction>,
    withCard: Boolean = true,
): Booking {
    val arrival = LocalDate.now().plusDays(14)
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
                    routingInstructions = routingInstructions,
                    operaPaymentCard =
                        if (withCard) {
                            OperaPaymentCard(
                                cardId = cardId,
                                cardNumber = "4111111111111111",
                                expirationDate = arrival.plusYears(2).toString(),
                                cardHolderName = "Amelia Wright",
                            )
                        } else {
                            null
                        },
                ),
            ),
    )
}
