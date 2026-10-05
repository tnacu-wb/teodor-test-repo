package uk.co.whitbread.integrationtests.journeys.hotelreservation

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.hotelreservation.HotelReservationApi
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.DepositCurrencyAmount
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.DepositFolioCharge
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.DepositFolioRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.SaveDepositFoliosRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_CREDIT_CARD_INFO_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_DEPOSIT_FOLIOS_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.creditCardInfoFailure
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

// Deterministic charge line every folio posts: one 9020 deposit posting for the whole stay.
private const val DEPOSIT_TRANSACTION_CODE = "9020"
private const val DEPOSIT_AMOUNT = 118.0

/**
 * Proves `POST /v1/reservations/save-deposit-folios`: hotel-reservation-entity-service passes the
 * folio list straight through ohip-adapter-service, which for each folio reads its Opera
 * reservation, enriches the saved card from Front Desk credit-card-info and posts the folio to
 * Opera cashiering, answering `201 Created` with no body — and propagates the downstream OHIP
 * `errCode` unchanged as a 500 when any of those three Opera calls is rejected, leaving the
 * remaining folios of a batch unposted.
 *
 * No feature flags: neither service evaluates one anywhere on this path, so every call sends no
 * overrides and the spec pins none.
 *
 * Flow: backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/SaveDepositFolios.md
 */
class SaveDepositFoliosSpec :
    JourneySpec(
        "Hotel reservation posts deposit folios to Opera cashiering",
        {
            val hotelReservationApi = HotelReservationApi()

            scenario("a carded reservation's deposit folio is posted to Opera cashiering") {
                val booking = saveDepositFoliosBooking(reservationIds = listOf("6110301"), cardIds = listOf("78001"))

                installFor(booking)

                val result =
                    hotelReservationApi.saveDepositFolios(
                        request = saveDepositFoliosRequest(booking),
                        testId = testId,
                    )

                result.attachEvidence("Save Deposit Folios Single Folio")

                expect("acknowledges the posted folio with 201 and no body") {
                    result.response.status.value shouldBe 201
                }

                expect("reads the reservation, enriches the card and posts the folio to cashiering") {
                    // Three Opera calls: GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId},
                    // GET /fof/config/v1/creditCardInfo and
                    // POST /csh/v1/hotels/{hotelId}/reservations/{reservationId}/depositFolios.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 1
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 1
                    // No payment provider, content or CDH hop sits on this path.
                    callCount(Upstream.WORLDLINE) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                }
            }

            scenario("a two-folio batch posts one deposit folio per reservation") {
                val booking =
                    saveDepositFoliosBooking(
                        reservationIds = listOf("6110302", "6110303"),
                        cardIds = listOf("78002", "78003"),
                    )

                installFor(booking)

                val result =
                    hotelReservationApi.saveDepositFolios(
                        request = saveDepositFoliosRequest(booking),
                        testId = testId,
                    )

                result.attachEvidence("Save Deposit Folios Two Folios")

                expect("acknowledges the posted batch with 201 and no body") {
                    result.response.status.value shouldBe 201
                }

                expect("repeats the reservation, card and cashiering calls once per folio") {
                    // Six Opera calls: each folio's own hotelId/reservationId drives its own
                    // reservation read, credit-card-info read and depositFolios POST.
                    callCount(Upstream.OPERA) shouldBe 6
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 2
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 2
                    callCount(Upstream.WORLDLINE) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                }
            }

            scenario("a rejected reservation lookup maps to the get-reservation error without posting a folio") {
                val booking = saveDepositFoliosBooking(reservationIds = listOf("6110304"), cardIds = listOf("78004"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationBadRequest(booking.hotel.hotelId, reservationId))

                val result =
                    hotelReservationApi.saveDepositFolios(
                        request = saveDepositFoliosRequest(booking),
                        testId = testId,
                    )

                result.attachEvidence("Save Deposit Folios Reservation Read Rejected")

                expect("returns the OHIP get-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 960
                }

                expect("stops after the rejected reservation read, never reaching card or cashiering") {
                    // One Opera call: the rejected reservation GET. The credit-card-info and
                    // depositFolios defaults stay installed, so the zero counts prove neither ran.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 0
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                }
            }

            scenario("a rejected credit-card-info read maps to the front-desk error without posting a folio") {
                val booking = saveDepositFoliosBooking(reservationIds = listOf("6110305"), cardIds = listOf("78005"))

                installFor(booking, excluded = setOf(OPERA_CREDIT_CARD_INFO_STUB_ID))
                installStub(creditCardInfoFailure(booking))

                val result =
                    hotelReservationApi.saveDepositFolios(
                        request = saveDepositFoliosRequest(booking),
                        testId = testId,
                    )

                result.attachEvidence("Save Deposit Folios Card Read Rejected")

                expect("returns the OHIP front-desk error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 957
                }

                expect("stops after the rejected card read, never reaching cashiering") {
                    // Two Opera calls: the reservation read and the rejected
                    // GET /fof/config/v1/creditCardInfo. The depositFolios default stays
                    // installed, so the zero count proves the cashiering POST never ran.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 1
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                }
            }

            scenario("a rejected cashiering post maps to the send-deposit-folios error") {
                val booking = saveDepositFoliosBooking(reservationIds = listOf("6110306"), cardIds = listOf("78006"))

                installFor(booking, excluded = setOf(OPERA_DEPOSIT_FOLIOS_STUB_ID))
                installStub(depositFoliosFailure(booking))

                val result =
                    hotelReservationApi.saveDepositFolios(
                        request = saveDepositFoliosRequest(booking),
                        testId = testId,
                    )

                result.attachEvidence("Save Deposit Folios Cashiering Rejected")

                expect("returns the OHIP send-deposit-folios error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 951
                }

                expect("fails on the cashiering POST after the reservation and card reads") {
                    // Three Opera calls: the reservation read, the card read and the rejected
                    // POST /csh/v1/hotels/{hotelId}/reservations/{reservationId}/depositFolios.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 1
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 1
                    callCount(Upstream.WORLDLINE) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                }
            }

            scenario("a failing folio aborts the remaining folios in the batch") {
                val booking =
                    saveDepositFoliosBooking(
                        reservationIds = listOf("6110307", "6110308", "6110309"),
                        cardIds = listOf("78007", "78008", "78009"),
                    )

                installFor(booking)
                // Mid-journey override installed after the default it shadows and before the call
                // it serves: only the second folio's cashiering POST is rejected, so folios one
                // and three keep the default success mapping. Excluding the default is not an
                // option — it carries all three rooms' mappings.
                installStub(depositFoliosFailure(booking, rooms = listOf(booking.rooms[1])))

                val result =
                    hotelReservationApi.saveDepositFolios(
                        request = saveDepositFoliosRequest(booking),
                        testId = testId,
                    )

                result.attachEvidence("Save Deposit Folios Batch Aborted")

                expect("returns the OHIP send-deposit-folios error for the failing folio") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 951
                }

                expect("posts the first folio, fails the second and never attempts the third") {
                    // Six Opera calls: two complete folio triples. The third folio's reservation,
                    // card and cashiering mappings are installed and uncalled, which is what
                    // proves the batch aborted rather than continuing.
                    callCount(Upstream.OPERA) shouldBe 6
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 2
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 2
                    callCount(Upstream.WORLDLINE) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                }
            }

            // Disabled: bug/save-deposit-folios-cardless-npe.md — the ohip-adapter card-enrichment
            // mapper dereferences the reservation's first payment method without a null guard, so
            // a cardless reservation fails with a raw 500 before any cashiering POST. The scenario
            // asserts the correct behavior (the folio is posted without card enrichment) and is
            // re-enabled when the bug is fixed.
            scenario("!a cardless reservation's deposit folio is still posted to cashiering") {
                val booking =
                    saveDepositFoliosBooking(
                        reservationIds = listOf("6110310"),
                        cardIds = listOf("78010"),
                        withCard = false,
                    )

                installFor(booking)

                val result =
                    hotelReservationApi.saveDepositFolios(
                        request = saveDepositFoliosRequest(booking),
                        testId = testId,
                    )

                result.attachEvidence("Save Deposit Folios Cardless")

                expect("acknowledges the posted folio with 201 and no body") {
                    result.response.status.value shouldBe 201
                }

                expect("reads the reservation and posts the folio without any card read") {
                    // Two Opera calls: the reservation read and the cashiering POST. A cardless
                    // reservation has no credit-card-info to read, so no such mapping is installed.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 0
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 1
                    callCount(Upstream.WORLDLINE) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                }
            }
        },
    )

private fun saveDepositFoliosBooking(
    reservationIds: List<String>,
    cardIds: List<String>,
    withCard: Boolean = true,
): Booking {
    val arrival = LocalDate.now().plusDays(14)
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms =
            reservationIds.mapIndexed { index, reservationId ->
                BookingRoom(
                    reservationId = reservationId,
                    roomType = rate.roomType,
                    adults = rate.adults,
                    status = ReservationStatus.RESERVED,
                    operaPaymentCard =
                        if (withCard) {
                            OperaPaymentCard(
                                cardId = cardIds[index],
                                cardNumber = "4111111111111111",
                                expirationDate = arrival.plusYears(2).toString(),
                                cardHolderName = "Amelia Wright",
                            )
                        } else {
                            null
                        },
                )
            },
    )
}

private fun saveDepositFoliosRequest(booking: Booking): SaveDepositFoliosRequest =
    SaveDepositFoliosRequest(
        depositFolios =
            booking.rooms.map { room ->
                val reservationId = requireNotNull(room.reservationId)

                DepositFolioRequest(
                    hotelId = booking.hotel.hotelId,
                    reservationId = reservationId,
                    charges =
                        listOf(
                            DepositFolioCharge(
                                transactionCode = DEPOSIT_TRANSACTION_CODE,
                                quantity = 1,
                                reference = "deposit for $reservationId",
                                currencyAmount =
                                    DepositCurrencyAmount(
                                        amount = DEPOSIT_AMOUNT,
                                        currencyCode = booking.hotel.currency,
                                    ),
                            ),
                        ),
                )
            },
    )
