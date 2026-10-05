package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.DepositCurrencyAmount
import uk.co.whitbread.integrationtests.clients.ohip.model.DepositFolioCharge
import uk.co.whitbread.integrationtests.clients.ohip.model.DepositFolioRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.SaveDepositFoliosRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_DEPOSIT_FOLIOS_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.depositFoliosFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.OperaPaymentCard
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

/**
 * Proves `POST /ohip/v1/reservations/deposit-folios`: for each folio the adapter loads the
 * reservation from Opera, enriches its saved payment card from Front Desk credit-card-info,
 * and posts the folio to Opera cashiering with `overrideInsufficientCC=true`, returning
 * `201 Created` with no body.
 *
 * No endpoint-specific feature flags; the opera token-service flags are environment-pinned
 * OFF (infrastructure scope, not overridable per request), so no overrides are sent.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/SaveDepositFolios.md
 */
class SaveDepositFoliosSpec :
    JourneySpec(
        "OHIP adapter posts deposit folios to Opera cashiering",
        {
            val ohipApi = OhipApi()

            scenario("a carded reservation's deposit folio is posted to Opera cashiering") {
                val booking = depositFoliosBooking(reservationId = "6010001", cardId = "77001")

                installFor(booking)

                val result =
                    ohipApi.saveDepositFolios(
                        request = depositFoliosRequest(booking),
                        testId = testId,
                    )

                result.attachEvidence("Save Deposit Folios")

                expect("acknowledges the posted folio with 201 and no body") {
                    result.response.status.value shouldBe 201
                }

                expect("loads the reservation, enriches the card, and posts the folio to cashiering") {
                    // Three calls: reservation GET, Front Desk credit-card-info GET, and the
                    // cashiering deposit-folios POST.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 1
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected reservation lookup maps to the get-reservation error without posting a folio") {
                val booking = depositFoliosBooking(reservationId = "6010101", cardId = "77101")
                val room = booking.room

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationBadRequest(booking.hotel.hotelId, room.reservationId!!))

                val result =
                    ohipApi.saveDepositFolios(
                        request = depositFoliosRequest(booking),
                        testId = testId,
                    )

                result.attachEvidence("Save Deposit Folios Reservation Lookup Error")

                expect("returns the mapped get-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 960
                }

                expect("stops after the rejected reservation GET, never reaching card or cashiering") {
                    // One Opera call: the rejected reservation GET. The credit-card-info and
                    // cashiering defaults are installed, so the exact count proves neither ran.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            // BUG (disabled until fixed): a reservation with no saved payment card NPEs in the
            // card-enrichment mapper ("resPaymentMethod" is null) and the request fails with a
            // raw 500 before any cashiering POST. The scenario asserts the correct behavior —
            // the folio still posted without card enrichment. See
            // bug/save-deposit-folios-cardless-npe.md.
            scenario("!a cardless reservation's deposit folio is still posted to cashiering") {
                val booking = depositFoliosBooking(reservationId = "6010103", cardId = "unused", withCard = false)

                installFor(booking)

                val result =
                    ohipApi.saveDepositFolios(
                        request = depositFoliosRequest(booking),
                        testId = testId,
                    )

                result.attachEvidence("Save Deposit Folios Cardless")

                expect("acknowledges the posted folio with 201 and no body") {
                    result.response.status.value shouldBe 201
                }

                expect("loads the reservation and posts the folio without any card read") {
                    // Two calls: the reservation GET and the cashiering deposit-folios POST; a
                    // cardless world has no credit-card-info to read.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected cashiering post maps to the send-deposit-folios error") {
                val booking = depositFoliosBooking(reservationId = "6010102", cardId = "77102")

                installFor(booking, excluded = setOf(OPERA_DEPOSIT_FOLIOS_STUB_ID))
                installStub(depositFoliosFailure(booking))

                val result =
                    ohipApi.saveDepositFolios(
                        request = depositFoliosRequest(booking),
                        testId = testId,
                    )

                result.attachEvidence("Save Deposit Folios Cashiering Error")

                expect("returns the mapped send-deposit-folios error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 951
                }

                expect("fails on the cashiering POST after the reservation and card reads") {
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 1
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun depositFoliosBooking(
    reservationId: String,
    cardId: String,
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

private fun depositFoliosRequest(booking: Booking): SaveDepositFoliosRequest {
    val room = booking.room

    return SaveDepositFoliosRequest(
        depositFolios =
            listOf(
                DepositFolioRequest(
                    hotelId = booking.hotel.hotelId,
                    reservationId = room.reservationId!!,
                    charges =
                        listOf(
                            DepositFolioCharge(
                                transactionCode = "9020",
                                quantity = 1,
                                reference = "deposit for ${room.reservationId}",
                                currencyAmount =
                                    DepositCurrencyAmount(
                                        amount = 118.0,
                                        currencyCode = booking.hotel.currency,
                                    ),
                            ),
                        ),
                ),
            ),
    )
}
