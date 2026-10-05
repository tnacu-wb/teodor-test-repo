package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.CancelReservationPaymentOption
import uk.co.whitbread.integrationtests.clients.ohip.model.CancelReservationRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_CANCEL_RESERVATION_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_CITY_TAX_RATE_INFO_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_CREDIT_CARD_INFO_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_DEPOSIT_FOLIOS_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_AMOUNTS_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_FOLIOS_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.cancelReservationFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.cityTaxRateInfoFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.creditCardInfoFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.depositFoliosFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationEmpty
import uk.co.whitbread.integrationtests.stubs.opera.custom.reservationAmountsFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.reservationFoliosFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.OperaPaymentCard
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.model.SelectedPackage
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

private val cancelReservationFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
    )

/**
 * Proves the OHIP adapter's reservation cancellation: `POST /ohip/v1/reservations/cancellations`
 * posts one Opera cancellation per reservation id, and for `PAY_NOW` / `PAY_ON_ARRIVAL` first
 * loads each reservation, optionally reads Front Desk card details and rate-info, and reverses
 * deposits via a cashiering deposit folio, returning cancellation ids and refunded deposits.
 * Any rejected downstream call aborts with its mapped internal error before the cancellation.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/CancelReservation.md
 */
class CancelReservationSpec :
    JourneySpec(
        "OHIP adapter reservations can be cancelled",
        {
            val ohipApi = OhipApi()

            scenario("a reserved room is cancelled without a deposit refund") {
                val booking = reservedRoomBooking(reservationId = "6005101")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.cancelReservation(
                        request =
                            CancelReservationRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                            ),
                        testId = testId,
                        featureFlagOverrides = cancelReservationFlagPins,
                    )

                result.attachEvidence("Cancel Reservation")

                expect("returns the Opera cancellation id and no refunded deposits") {
                    result.response.status.value shouldBe 200
                    result.body.cancellationIds shouldBe listOf(expectedCancellationId(reservationId))
                    result.body.refundedDeposits
                        .orEmpty()
                        .shouldBeEmpty()
                }

                expect("posts only the Opera cancellation") {
                    // One Opera call: POST /rsv/v1/hotels/{hotelId}/reservations/{reservationId}/cancellations.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a pay-now cancel with no deposit posts Opera cancellation after loading the reservation") {
                val booking = reservedRoomBooking(reservationId = "6005112")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.cancelReservation(
                        request = payNowRequest(booking),
                        testId = testId,
                        featureFlagOverrides = cancelReservationFlagPins,
                    )

                result.attachEvidence("Cancel Reservation Pay Now No Deposit")

                expect("returns the Opera cancellation id and no refunded deposits") {
                    result.response.status.value shouldBe 200
                    result.body.cancellationIds shouldBe listOf(expectedCancellationId(reservationId))
                    result.body.refundedDeposits
                        .orEmpty()
                        .shouldBeEmpty()
                }

                expect("loads the reservation then posts the Opera cancellation") {
                    // Two Opera calls: GET reservation, POST cancellations.
                    // VAT is rules-agent-entity-service, not a WireMock upstream.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a pay-now cancel of a part-paid reservation refunds the Opera deposit") {
                val booking =
                    reservedRoomBooking(
                        reservationId = "6005113",
                        amountAlreadyPaid = 50.0,
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.cancelReservation(
                        request = payNowRequest(booking),
                        testId = testId,
                        featureFlagOverrides = cancelReservationFlagPins,
                    )

                result.attachEvidence("Cancel Reservation Pay Now Deposit")

                expect("returns the cancellation id and a refunded-deposit entry for the room") {
                    result.response.status.value shouldBe 200
                    result.body.cancellationIds shouldBe listOf(expectedCancellationId(reservationId))
                    result.body.refundedDeposits.orEmpty() shouldContainKey reservationId
                }

                expect("posts a deposit folio then the Opera cancellation") {
                    // Three Opera calls: GET reservation, POST depositFolios, POST cancellations.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 1
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a pay-on-arrival cancel with nothing on the folio still cancels") {
                val booking = reservedRoomBooking(reservationId = "6005114")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.cancelReservation(
                        request = payOnArrivalRequest(booking),
                        testId = testId,
                        featureFlagOverrides = cancelReservationFlagPins,
                    )

                result.attachEvidence("Cancel Reservation Pay On Arrival Empty Folio")

                expect("returns the Opera cancellation id and no refunded deposits") {
                    result.response.status.value shouldBe 200
                    result.body.cancellationIds shouldBe listOf(expectedCancellationId(reservationId))
                    result.body.refundedDeposits
                        .orEmpty()
                        .shouldBeEmpty()
                }

                expect("loads reservation and folios then posts the Opera cancellation") {
                    // Three Opera calls: GET reservation, GET folios, POST cancellations.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 1
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a pay-now cancel of a migrated reservation refunds the rate-info deposit") {
                val booking =
                    reservedRoomBooking(
                        reservationId = "6005116",
                        amountAlreadyPaid = 50.0,
                        bookingReference = "BART5116",
                        bookingReferenceIdContext = "BART_OHIP",
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.cancelReservation(
                        request = payNowRequest(booking),
                        testId = testId,
                        featureFlagOverrides = cancelReservationFlagPins,
                    )

                result.attachEvidence("Cancel Reservation Pay Now Migrated")

                expect("returns the cancellation id and a refunded-deposit entry for the room") {
                    result.response.status.value shouldBe 200
                    result.body.cancellationIds shouldBe listOf(expectedCancellationId(reservationId))
                    result.body.refundedDeposits.orEmpty() shouldContainKey reservationId
                }

                expect("loads reservation amounts then posts a deposit folio and the cancellation") {
                    // Four Opera calls: GET reservation, GET rateInfo summary, POST depositFolios,
                    // POST cancellations.
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 1
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a pay-now cancel with city tax loads per-day rate-info detail") {
                val booking =
                    reservedRoomBooking(
                        reservationId = "6005117",
                        selectedPackages = listOf(SelectedPackage(code = "CITYTAX")),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.cancelReservation(
                        request = payNowRequest(booking),
                        testId = testId,
                        featureFlagOverrides = cancelReservationFlagPins,
                    )

                result.attachEvidence("Cancel Reservation Pay Now City Tax")

                expect("returns the Opera cancellation id") {
                    result.response.status.value shouldBe 200
                    result.body.cancellationIds shouldBe listOf(expectedCancellationId(reservationId))
                }

                expect("loads reservation and city-tax rate-info then posts the cancellation") {
                    // Three Opera calls: GET reservation, GET rateInfo detailDate/summaryInfo=false,
                    // POST cancellations.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a pay-now cancel with an Opera payment card loads Front Desk card details") {
                val booking =
                    reservedRoomBooking(
                        reservationId = "6005118",
                        operaPaymentCard =
                            OperaPaymentCard(
                                cardId = "12345",
                                cardType = "Va",
                                cardNumber = "4764776852337921103",
                                expirationDate = "2025-03-31",
                                cardHolderName = "Charlie",
                            ),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.cancelReservation(
                        request = payNowRequest(booking),
                        testId = testId,
                        featureFlagOverrides = cancelReservationFlagPins,
                    )

                result.attachEvidence("Cancel Reservation Pay Now Card")

                expect("returns the Opera cancellation id") {
                    result.response.status.value shouldBe 200
                    result.body.cancellationIds shouldBe listOf(expectedCancellationId(reservationId))
                }

                expect("loads reservation and credit-card-info then posts the cancellation") {
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 1
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera rejection of the cancellation post maps to internal error 950") {
                val booking = reservedRoomBooking(reservationId = "6005121")

                // A downstream failure is exceptional behavior, not a normal Booking world state.
                installFor(booking, excluded = setOf(OPERA_CANCEL_RESERVATION_STUB_ID))
                installStub(cancelReservationFailure(booking))

                val result =
                    ohipApi.cancelReservation(
                        request =
                            CancelReservationRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(requireNotNull(booking.room.reservationId)),
                            ),
                        testId = testId,
                        featureFlagOverrides = cancelReservationFlagPins,
                    )

                result.attachEvidence("Cancel Reservation Opera Cancel Error")

                expect("returns the mapped cancellation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 950
                }

                expect("stops after the rejected cancellation post") {
                    // One Opera call: the rejected POST cancellations.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a pay-now cancel with a rejected reservation lookup aborts before any cancellation") {
                val hotelId = Hotels.HEAPTI.hotelId
                val reservationId = "invalid-reservation-id"

                installStub(getReservationBadRequest(hotelId = hotelId, reservationId = reservationId))

                val result =
                    ohipApi.cancelReservation(
                        request =
                            CancelReservationRequest(
                                hotelId = hotelId,
                                reservationIds = listOf(reservationId),
                                paymentOption = CancelReservationPaymentOption.PAY_NOW,
                                defaultPaymentMethod = "CA",
                            ),
                        testId = testId,
                        featureFlagOverrides = cancelReservationFlagPins,
                    )

                result.attachEvidence("Cancel Reservation Opera Lookup Error")

                expect("returns the mapped reservation-lookup error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 960
                }

                expect("stops after the failed reservation lookup") {
                    // One Opera call: the reservation GET that fails; no cancellation POST.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a pay-now cancel of a reservation Opera holds nothing for still cancels") {
                val booking = reservedRoomBooking(reservationId = "6005122")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationEmpty(hotelId = booking.hotel.hotelId, reservationId = reservationId))

                val result =
                    ohipApi.cancelReservation(
                        request = payNowRequest(booking),
                        testId = testId,
                        featureFlagOverrides = cancelReservationFlagPins,
                    )

                result.attachEvidence("Cancel Reservation Empty Reservation")

                expect("returns the Opera cancellation id and no refunded deposits") {
                    result.response.status.value shouldBe 200
                    result.body.cancellationIds shouldBe listOf(expectedCancellationId(reservationId))
                    result.body.refundedDeposits
                        .orEmpty()
                        .shouldBeEmpty()
                }

                expect("skips deposit work and still posts the Opera cancellation") {
                    // Two Opera calls: the empty reservation GET, POST cancellations.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a pay-now cancel with a rejected deposit-folio post aborts before cancellation") {
                val booking =
                    reservedRoomBooking(
                        reservationId = "6005123",
                        amountAlreadyPaid = 50.0,
                    )

                installFor(booking, excluded = setOf(OPERA_DEPOSIT_FOLIOS_STUB_ID))
                installStub(depositFoliosFailure(booking))

                val result =
                    ohipApi.cancelReservation(
                        request = payNowRequest(booking),
                        testId = testId,
                        featureFlagOverrides = cancelReservationFlagPins,
                    )

                result.attachEvidence("Cancel Reservation Deposit Folio Error")

                expect("returns the mapped deposit-folio error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 951
                }

                expect("stops after the rejected deposit folio without cancelling") {
                    // Two Opera calls: GET reservation, then the rejected POST depositFolios.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a pay-on-arrival cancel with a rejected folios read aborts before cancellation") {
                val booking = reservedRoomBooking(reservationId = "6005124")

                installFor(booking, excluded = setOf(OPERA_RESERVATION_FOLIOS_STUB_ID))
                installStub(reservationFoliosFailure(booking))

                val result =
                    ohipApi.cancelReservation(
                        request = payOnArrivalRequest(booking),
                        testId = testId,
                        featureFlagOverrides = cancelReservationFlagPins,
                    )

                result.attachEvidence("Cancel Reservation Folios Error")

                expect("returns the mapped folios error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 945
                }

                expect("stops after the rejected folios read without cancelling") {
                    // Two Opera calls: GET reservation, then the rejected GET folios.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a pay-now cancel with a rejected Front Desk card lookup aborts before cancellation") {
                val booking =
                    reservedRoomBooking(
                        reservationId = "6005125",
                        operaPaymentCard =
                            OperaPaymentCard(
                                cardId = "12346",
                                cardType = "Va",
                                cardNumber = "4764776852337921103",
                                expirationDate = "2025-03-31",
                                cardHolderName = "Charlie",
                            ),
                    )

                installFor(booking, excluded = setOf(OPERA_CREDIT_CARD_INFO_STUB_ID))
                installStub(creditCardInfoFailure(booking))

                val result =
                    ohipApi.cancelReservation(
                        request = payNowRequest(booking),
                        testId = testId,
                        featureFlagOverrides = cancelReservationFlagPins,
                    )

                result.attachEvidence("Cancel Reservation Card Lookup Error")

                expect("returns the mapped Front Desk error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 957
                }

                expect("stops after the rejected card lookup without cancelling") {
                    // Two Opera calls: GET reservation, then the rejected GET creditCardInfo.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a pay-now cancel of a migrated reservation with a rejected rate-info summary aborts") {
                val booking =
                    reservedRoomBooking(
                        reservationId = "6005126",
                        amountAlreadyPaid = 50.0,
                        bookingReference = "BART5126",
                        bookingReferenceIdContext = "BART_OHIP",
                    )

                installFor(booking, excluded = setOf(OPERA_RESERVATION_AMOUNTS_STUB_ID))
                installStub(reservationAmountsFailure(booking))

                val result =
                    ohipApi.cancelReservation(
                        request = payNowRequest(booking),
                        testId = testId,
                        featureFlagOverrides = cancelReservationFlagPins,
                    )

                result.attachEvidence("Cancel Reservation Rate Summary Error")

                expect("returns the mapped rate-info summary error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 946
                }

                expect("stops after the rejected rate-info summary without cancelling") {
                    // Two Opera calls: GET reservation, then the rejected GET rateInfo summary.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a pay-now cancel with city tax and a rejected rate-info detail aborts") {
                val booking =
                    reservedRoomBooking(
                        reservationId = "6005127",
                        selectedPackages = listOf(SelectedPackage(code = "CITYTAX")),
                    )

                installFor(booking, excluded = setOf(OPERA_CITY_TAX_RATE_INFO_STUB_ID))
                installStub(cityTaxRateInfoFailure(booking))

                val result =
                    ohipApi.cancelReservation(
                        request = payNowRequest(booking),
                        testId = testId,
                        featureFlagOverrides = cancelReservationFlagPins,
                    )

                result.attachEvidence("Cancel Reservation City Tax Error")

                expect("returns the mapped rate-info detail error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 900
                }

                expect("stops after the rejected rate-info detail without cancelling") {
                    // Two Opera calls: GET reservation, then the rejected GET rateInfo detailDate.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun payNowRequest(booking: Booking): CancelReservationRequest =
    CancelReservationRequest(
        hotelId = booking.hotel.hotelId,
        reservationIds = listOf(requireNotNull(booking.room.reservationId)),
        paymentOption = CancelReservationPaymentOption.PAY_NOW,
        defaultPaymentMethod = "CA",
    )

private fun payOnArrivalRequest(booking: Booking): CancelReservationRequest =
    CancelReservationRequest(
        hotelId = booking.hotel.hotelId,
        reservationIds = listOf(requireNotNull(booking.room.reservationId)),
        paymentOption = CancelReservationPaymentOption.PAY_ON_ARRIVAL,
        defaultPaymentMethod = "CA",
    )

private fun reservedRoomBooking(
    reservationId: String,
    amountAlreadyPaid: Double = 0.0,
    bookingReference: String? = null,
    bookingReferenceIdContext: String = "WB_DIGITAL",
    selectedPackages: List<SelectedPackage> = emptyList(),
    operaPaymentCard: OperaPaymentCard? = null,
): Booking {
    val arrival = LocalDate.now().plusDays(14)
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = arrival.plusDays(2),
        bookingReference = bookingReference,
        bookingReferenceIdContext = bookingReferenceIdContext,
        rooms =
            listOf(
                BookingRoom(
                    reservationId = reservationId,
                    roomType = rate.roomType,
                    adults = rate.adults,
                    status = ReservationStatus.RESERVED,
                    amountAlreadyPaid = amountAlreadyPaid,
                    selectedPackages = selectedPackages,
                    operaPaymentCard = operaPaymentCard,
                ),
            ),
    )
}

private fun expectedCancellationId(reservationId: String): String = "CXL${reservationId.takeLast(7).padStart(7, '0')}"
