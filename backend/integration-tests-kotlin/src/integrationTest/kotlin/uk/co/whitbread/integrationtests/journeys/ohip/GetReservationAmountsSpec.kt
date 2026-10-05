package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_AMOUNTS_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_FOLIOS_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.reservationAmountsFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.reservationFoliosFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.reservationFoliosWithoutWindows
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

// No flags: the flow doc lists none on this chain, and the only flag that can touch it
// (release_ohip_use_token_service) is evaluated outside the request context and cannot be pinned.
private val reservationAmountsFlagPins = mapOf<FeatureFlag, Boolean>()

/**
 * Proves `GET /ohip/v1/reservations/amounts`: the adapter reduces Opera's reservation rate-info
 * summaries into one money response, corrects the deposit with whatever is actually posted on each
 * reservation's cashiering folio, sums across several reservations, and maps a rejection on either
 * Opera leg to an internal server error.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/GetReservationAmounts.md
 */
class GetReservationAmountsSpec :
    JourneySpec(
        "OHIP adapter reads the money summary for a set of reservations",
        {
            val ohipApi = OhipApi()

            scenario("nothing paid: the rate-info figures pass through untouched") {
                val booking = reservationAmountsBooking(reservationId = "6005601", amountAlreadyPaid = 0.0)
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.getReservationAmounts(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                        featureFlagOverrides = reservationAmountsFlagPins,
                    )

                result.attachEvidence("Reservation Amounts Nothing Paid")

                expect("returns the untouched rate-info figures") {
                    result.response.status.value shouldBe 200
                    result.body.currencyCode shouldBe "GBP"
                    // 59.00 a night for two nights, nothing posted on the folio, so the folio
                    // window is empty, the ACI total is zero and the summary survives.
                    result.body.totalCostOfStay shouldBe 118.00
                    result.body.gross shouldBe 118.00
                    result.body.deposit shouldBe 0.0
                    result.body.outStandingCostOfStay shouldBe 118.00
                    result.body.discount shouldBe null
                }

                expect("reads the reservation summary and its folios once each") {
                    // GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo?idContext=OPERA&id={id}&summaryInfo=true&type=Reservation
                    // then GET /csh/v1/hotels/{hotelId}/reservations/{id}/folios
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 1
                    // The reservation read and the CRM profile read are installed and off-path.
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("amounts for two reservations are summed into one response") {
                val booking = twoReservationAmountsBooking()
                val firstReservationId = requireNotNull(booking.rooms[0].reservationId)
                val secondReservationId = requireNotNull(booking.rooms[1].reservationId)

                installFor(booking)

                val result =
                    ohipApi.getReservationAmounts(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(firstReservationId, secondReservationId),
                        testId = testId,
                        featureFlagOverrides = reservationAmountsFlagPins,
                    )

                result.attachEvidence("Reservation Amounts Two Reservations")

                expect("returns the summed amounts of both reservations") {
                    result.response.status.value shouldBe 200
                    result.body.currencyCode shouldBe "GBP"
                    // 59.00 x 2 nights plus 79.00 x 2 nights.
                    result.body.totalCostOfStay shouldBe 276.00
                    result.body.gross shouldBe 276.00
                    // Only the first reservation has money posted against it.
                    result.body.deposit shouldBe 50.00
                    result.body.outStandingCostOfStay shouldBe 226.00
                    result.body.discount shouldBe null
                }

                expect("reads one summary and one folio per requested reservation") {
                    // GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo and
                    // GET /csh/v1/hotels/{hotelId}/reservations/{id}/folios, once per id.
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 2
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("money posted on the folio overrides the reservation summary deposit") {
                val booking =
                    reservationAmountsBooking(
                        reservationId = "6005605",
                        amountAlreadyPaid = 50.0,
                        // The folio holds more than the summary's deposit: the guest paid after the
                        // reservation summary was computed.
                        amountPostedOnFolio = 90.0,
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.getReservationAmounts(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                        featureFlagOverrides = reservationAmountsFlagPins,
                    )

                result.attachEvidence("Reservation Amounts Folio Posted More")

                expect("returns the folio total as the deposit") {
                    result.response.status.value shouldBe 200
                    result.body.currencyCode shouldBe "GBP"
                    // The stay total still comes from the rate-info summary.
                    result.body.totalCostOfStay shouldBe 118.00
                    // 90.00 posted on the cashiering folio (room.amountPostedOnFolio) beats the
                    // summary's 50.00 deposit (room.amountAlreadyPaid).
                    result.body.deposit shouldBe 90.00
                    result.body.outStandingCostOfStay shouldBe 28.00
                    result.body.discount shouldBe null
                }

                expect("reads the reservation summary and its folios once each") {
                    // GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo then
                    // GET /csh/v1/hotels/{hotelId}/reservations/{id}/folios
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("maps an Opera rate-info rejection to an internal error and never reads the folios") {
                val booking = reservationAmountsBooking(reservationId = "6005606", amountAlreadyPaid = 50.0)
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_AMOUNTS_STUB_ID))
                installStub(reservationAmountsFailure(booking))

                val result =
                    ohipApi.getReservationAmounts(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                        featureFlagOverrides = reservationAmountsFlagPins,
                    )

                result.attachEvidence("Reservation Amounts Opera Summary Error")

                expect("returns the mapped reservation-amounts error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 946
                }

                expect("fails on the summary and never reaches the folios") {
                    // Only the rejected GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo. The
                    // GET /csh/v1/hotels/{hotelId}/reservations/{id}/folios default stub is
                    // installed and must stay untouched.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 0
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("maps an Opera cashiering folios rejection to an internal error") {
                val booking = reservationAmountsBooking(reservationId = "6005607", amountAlreadyPaid = 50.0)
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_FOLIOS_STUB_ID))
                installStub(reservationFoliosFailure(booking))

                val result =
                    ohipApi.getReservationAmounts(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                        featureFlagOverrides = reservationAmountsFlagPins,
                    )

                result.attachEvidence("Reservation Amounts Opera Folios Error")

                expect("returns the mapped folios error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 945
                }

                expect("reads the summary, then fails on the folios") {
                    // GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo succeeds, the rejected
                    // GET /csh/v1/hotels/{hotelId}/reservations/{id}/folios follows it.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            // Disabled: the service answers 500 instead of treating a reservation with no
            // cashiering folio window as nothing posted, because the folio client filters the
            // window-less response away and the caller dereferences the resulting null total. See
            // backend/integration-tests-kotlin/bug/
            // get-reservation-amounts-empty-folio-windows-500.md
            scenario("!treats a reservation with no folio windows as nothing posted") {
                val booking = reservationAmountsBooking(reservationId = "6005608", amountAlreadyPaid = 50.0)
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_FOLIOS_STUB_ID))
                installStub(reservationFoliosWithoutWindows(booking))

                val result =
                    ohipApi.getReservationAmounts(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                        featureFlagOverrides = reservationAmountsFlagPins,
                    )

                result.attachEvidence("Reservation Amounts No Folio Windows")

                expect("returns the rate-info figures because nothing is posted") {
                    result.response.status.value shouldBe 200
                    result.body.currencyCode shouldBe "GBP"
                    result.body.totalCostOfStay shouldBe 118.00
                    result.body.deposit shouldBe 50.00
                    result.body.outStandingCostOfStay shouldBe 68.00
                }

                expect("reads the reservation summary and its folios once each") {
                    // GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo then
                    // GET /csh/v1/hotels/{hotelId}/reservations/{id}/folios
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun reservationAmountsBooking(
    reservationId: String,
    amountAlreadyPaid: Double,
    amountPostedOnFolio: Double? = null,
): Booking {
    val arrival = LocalDate.now().plusDays(30)
    val rate = Rate(ratePlan = "SEMIFLEX", ratePlanSet = "STANDARD", roomType = "LOWDBL", adults = 2)

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
                    amountPostedOnFolio = amountPostedOnFolio,
                ),
            ),
    )
}

private fun twoReservationAmountsBooking(): Booking {
    val arrival = LocalDate.now().plusDays(30)
    val firstRate = Rate(ratePlan = "SEMIFLEX", ratePlanSet = "STANDARD", roomType = "LOWDBL", adults = 2)
    val secondRate =
        Rate(ratePlan = "SEMIFLEX", ratePlanSet = "STANDARD", roomType = "TWINRM", adults = 2, nightlyRate = 79.0)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(firstRate, secondRate))),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms =
            listOf(
                BookingRoom(
                    reservationId = "6005603",
                    roomType = firstRate.roomType,
                    adults = firstRate.adults,
                    status = ReservationStatus.RESERVED,
                    amountAlreadyPaid = 50.0,
                ),
                BookingRoom(
                    reservationId = "6005604",
                    roomType = secondRate.roomType,
                    adults = secondRate.adults,
                    status = ReservationStatus.RESERVED,
                    amountAlreadyPaid = 0.0,
                ),
            ),
    )
}
