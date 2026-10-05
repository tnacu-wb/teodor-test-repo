package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.ReservationPaymentTypeEntry
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_CREDIT_CARD_INFO_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.creditCardInfoFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
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
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

private val paymentTypeFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
    )

/**
 * Proves the OHIP adapter's payment-type lookup: `GET /ohip/v1/reservations/paymentType` loads
 * each requested reservation's payment methods from Opera and, only for a reservation whose
 * payment method carries a stored card id, enriches it with Opera Front Desk credit-card details.
 * A reservation without a stored card returns its entry without card enrichment and must not
 * trigger a Front Desk call.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/GetPaymentTypeReservationsByReservationIds.md
 */
class GetPaymentTypeReservationsSpec :
    JourneySpec(
        "OHIP adapter returns reservation payment types",
        {
            val ohipApi = OhipApi()

            scenario("a reservation without a stored card is returned without Front Desk enrichment") {
                val booking =
                    paymentTypeBooking(
                        rooms = listOf(plainRoom(reservationId = "6006401")),
                    )

                installFor(booking)

                val result =
                    ohipApi.getPaymentTypeReservationsByReservationIds(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(requireNotNull(booking.room.reservationId)),
                        testId = testId,
                        featureFlagOverrides = paymentTypeFlagPins,
                    )

                result.attachEvidence("Get Payment Type No Card")

                expect("returns the reservation's entry without card details") {
                    result.response.status.value shouldBe 200
                    result.body.size shouldBe 1
                    result.body.first().reservationIdOrNull() shouldBe booking.room.reservationId
                    // Without a stored card the endpoint returns an empty card object, not null.
                    result.body
                        .first()
                        .paymentCardType
                        ?.cardNumberMasked shouldBe ""
                    result.body
                        .first()
                        .paymentCardType
                        ?.cardNumberLast4Digits
                        .shouldBeNull()
                }

                expect("loads only the reservation from Opera, never Front Desk") {
                    // One Opera call: GET reservation payment methods.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("only the card-bearing reservation of a batch is enriched with Front Desk card details") {
                val cardRoom =
                    plainRoom(reservationId = "6006402").copy(
                        operaPaymentCard =
                            OperaPaymentCard(
                                cardId = "22345",
                                cardType = "Va",
                                cardNumber = "4764776852337921103",
                                expirationDate = "2027-03-31",
                                cardHolderName = "Charlie",
                            ),
                    )
                val plainRoom = plainRoom(reservationId = "6006403")
                val booking = paymentTypeBooking(rooms = listOf(cardRoom, plainRoom))

                installFor(booking)

                val result =
                    ohipApi.getPaymentTypeReservationsByReservationIds(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = booking.rooms.map { requireNotNull(it.reservationId) },
                        testId = testId,
                        featureFlagOverrides = paymentTypeFlagPins,
                    )

                result.attachEvidence("Get Payment Type Selective Enrichment")

                expect("returns card details only for the card-bearing reservation") {
                    result.response.status.value shouldBe 200
                    result.body.size shouldBe 2

                    val cardEntry = result.body.entryFor(requireNotNull(cardRoom.reservationId))
                    val plainEntry = result.body.entryFor(requireNotNull(plainRoom.reservationId))
                    // Front Desk enrichment lands the card number in `token` and sets expirationDate;
                    // cardNumberMasked comes from the reservation itself, not the enrichment.
                    cardEntry.paymentCardType?.token shouldBe cardRoom.operaPaymentCard?.cardNumber
                    cardEntry.paymentCardType?.expirationDate shouldBe cardRoom.operaPaymentCard?.expirationDate
                    plainEntry.paymentCardType?.token shouldBe ""
                }

                expect("loads both reservations and one Front Desk card lookup") {
                    // Three Opera calls: two reservation GETs, one GET creditCardInfo.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a batch with one rejected reservation lookup still returns the survivor") {
                val booking = paymentTypeBooking(rooms = listOf(plainRoom(reservationId = "6006404")))
                val failingId = "9999404"

                installFor(booking)
                // The failing id is not part of the Booking; only its rejection is installed.
                installStub(getReservationBadRequest(booking.hotel.hotelId, failingId))

                val result =
                    ohipApi.getPaymentTypeReservationsByReservationIds(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(requireNotNull(booking.room.reservationId), failingId),
                        testId = testId,
                        featureFlagOverrides = paymentTypeFlagPins,
                    )

                result.attachEvidence("Get Payment Type One Rejected Lookup")

                expect("drops the failing reservation and returns the survivor") {
                    result.response.status.value shouldBe 200
                    result.body.size shouldBe 1
                    result.body.first().reservationIdOrNull() shouldBe booking.room.reservationId
                }

                expect("attempted both reservation reads") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a batch where every reservation lookup fails returns an empty list") {
                val hotelId = Hotels.HEAPTI.hotelId

                installStub(getReservationBadRequest(hotelId, "9999405"))
                installStub(getReservationBadRequest(hotelId, "9999406"))

                val result =
                    ohipApi.getPaymentTypeReservationsByReservationIds(
                        hotelId = hotelId,
                        reservationIds = listOf("9999405", "9999406"),
                        testId = testId,
                        featureFlagOverrides = paymentTypeFlagPins,
                    )

                result.attachEvidence("Get Payment Type All Rejected")

                expect("returns 200 with an empty list, never an error") {
                    result.response.status.value shouldBe 200
                    result.body.size shouldBe 0
                }

                expect("attempted both reservation reads") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected Front Desk card lookup aborts the whole call") {
                val cardRoom =
                    plainRoom(reservationId = "6006406").copy(
                        operaPaymentCard =
                            OperaPaymentCard(
                                cardId = "32345",
                                cardType = "Va",
                                cardNumber = "4764776852337921103",
                                expirationDate = "2027-03-31",
                                cardHolderName = "Charlie",
                            ),
                    )
                val booking = paymentTypeBooking(rooms = listOf(cardRoom))

                // Unlike a per-reservation Opera failure, Front Desk enrichment runs outside the
                // per-item error guard and propagates.
                installFor(booking, excluded = setOf(OPERA_CREDIT_CARD_INFO_STUB_ID))
                installStub(creditCardInfoFailure(booking))

                val result =
                    ohipApi.getPaymentTypeReservationsByReservationIds(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(requireNotNull(booking.room.reservationId)),
                        testId = testId,
                        featureFlagOverrides = paymentTypeFlagPins,
                    )

                result.attachEvidence("Get Payment Type Front Desk Error")

                expect("returns the mapped Front Desk error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 957
                }

                expect("stops after the rejected card lookup") {
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun ReservationPaymentTypeEntry.reservationIdOrNull(): String? =
    ids?.firstOrNull { it.type == "Reservation" }?.id ?: ids?.firstOrNull()?.id

private fun List<ReservationPaymentTypeEntry>.entryFor(reservationId: String): ReservationPaymentTypeEntry =
    requireNotNull(firstOrNull { entry -> entry.ids.orEmpty().any { it.id == reservationId } }) {
        "no payment-type entry for reservation $reservationId"
    }

private fun paymentTypeBooking(rooms: List<BookingRoom>): Booking {
    val arrival = LocalDate.now().plusDays(14)
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms = rooms,
    )
}

private fun plainRoom(reservationId: String): BookingRoom =
    BookingRoom(
        reservationId = reservationId,
        roomType = "LOWDBL",
        adults = 2,
        status = ReservationStatus.RESERVED,
    )
