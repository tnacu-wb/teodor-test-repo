package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_AMOUNTS_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.reservationAmountsFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.reservationAmountsWithoutSummary
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
// (release_ohip_use_token_service in WebClientConfig) is evaluated outside the request context and
// cannot be pinned.
private val getDetailsForAmendFlagPins = mapOf<FeatureFlag, Boolean>()

/**
 * Proves `GET /ohip/v1/reservations/amend/getDetailsForAmend`: the adapter reads Opera's
 * reservation rate-info summary once per requested reservation id, sums the stay scalars across
 * them, keys deposit and guest-pay per reservation, and maps a rejection on any read to an
 * internal server error without reading the remaining ids.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/GetDetailsForAmend.md
 */
class GetDetailsForAmendSpec :
    JourneySpec(
        "OHIP adapter reads the amend summary for a set of reservations",
        {
            val ohipApi = OhipApi()

            scenario("single part-paid reservation returns the Opera summary figures and its per-id maps") {
                val booking = amendSummaryBooking(reservationId = "6005651", amountAlreadyPaid = 25.0)
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.getDetailsForAmend(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                        featureFlagOverrides = getDetailsForAmendFlagPins,
                    )

                result.attachEvidence("Amend Summary Single Reservation")

                expect("returns the reservation's summary figures and its per-id maps") {
                    result.response.status.value shouldBe 200
                    // 59.00 a night for two nights, net at the 1.2 VAT divisor, 25.00 already paid.
                    result.body.totalCostOfStay shouldBe 118.00
                    result.body.net shouldBe 98.33
                    result.body.outStandingCostOfStay shouldBe 93.00
                    // Opera reports the deposit negative and this endpoint stores it as-is, unlike
                    // the sibling reservation-amounts reduction which negates it. Suspected
                    // inconsistency awaiting a service-team ruling:
                    // bug/get-details-for-amend-deposit-sign-asymmetry.md
                    result.body.deposit?.get(reservationId) shouldBe -25.00
                    result.body.guestPay?.get(reservationId) shouldBe 93.00
                }

                expect("reads the reservation rate-info summary once") {
                    // GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo?summaryInfo=true&type=Reservation&id={id}&detailDate={today},
                    // served by booking.opera.reservation-amounts. booking.opera.rate-info is
                    // installed on the same Opera URL but is matcher-disjoint (six criteria
                    // parameters) and attracts no call; GET_RATE_INFO is a shared constant, so the
                    // count cannot attribute per stub - harmless here because the total Opera cost
                    // is exactly the number of requested reservation ids.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 0
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("two reservations are summed into the scalars and keyed separately in the maps") {
                val booking = twoReservationAmendSummaryBooking(firstReservationId = "6005652", secondReservationId = "6005653")
                val firstReservationId = requireNotNull(booking.rooms[0].reservationId)
                val secondReservationId = requireNotNull(booking.rooms[1].reservationId)

                installFor(booking)

                val result =
                    ohipApi.getDetailsForAmend(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(firstReservationId, secondReservationId),
                        testId = testId,
                        featureFlagOverrides = getDetailsForAmendFlagPins,
                    )

                result.attachEvidence("Amend Summary Two Reservations")

                expect("returns the summed scalars and one map entry per reservation") {
                    result.response.status.value shouldBe 200
                    // 59.00 x 2 nights plus 79.00 x 2 nights.
                    result.body.totalCostOfStay shouldBe 276.00
                    result.body.net shouldBe 230.00
                    // Only the first reservation has money paid against it.
                    result.body.outStandingCostOfStay shouldBe 226.00
                    // As-is negative sign: bug/get-details-for-amend-deposit-sign-asymmetry.md
                    result.body.deposit?.get(firstReservationId) shouldBe -50.00
                    result.body.deposit?.get(secondReservationId) shouldBe 0.0
                    result.body.guestPay?.get(firstReservationId) shouldBe 68.00
                    result.body.guestPay?.get(secondReservationId) shouldBe 158.00
                }

                expect("reads one rate-info summary per requested reservation") {
                    // GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo once per id, in request
                    // order, sequential and blocking.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 0
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejection on the first reservation aborts the loop and the second reservation is never read") {
                val booking = twoReservationAmendSummaryBooking(firstReservationId = "6005655", secondReservationId = "6005656")
                val firstReservationId = requireNotNull(booking.rooms[0].reservationId)
                val secondReservationId = requireNotNull(booking.rooms[1].reservationId)

                installFor(booking)
                // Mid-journey override for the first reservation only: the second reservation's
                // happy mapping stays live and would answer 200, so the missing second read is a
                // real absence proof rather than a no-match accident.
                installStub(reservationAmountsFailure(booking, rooms = listOf(booking.rooms[0])))

                val result =
                    ohipApi.getDetailsForAmend(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(firstReservationId, secondReservationId),
                        testId = testId,
                        featureFlagOverrides = getDetailsForAmendFlagPins,
                    )

                result.attachEvidence("Amend Summary First Reservation Rejected")

                expect("returns the mapped rate-info error with no partial summary") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 900
                }

                expect("reads only the rejected reservation and never the second") {
                    // Only the rejected GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo for the
                    // first id; the second id's mapping is installed and must stay untouched.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 0
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            // Disabled: the service answers 500 instead of treating a reservation Opera reports no
            // rate-info summary for as nothing to add, because the reduction dereferences the
            // absent summary. See backend/integration-tests-kotlin/bug/
            // get-details-for-amend-missing-opera-summary-500.md
            scenario("!aggregates a reservation whose Opera response carries no summary as nothing to add") {
                val booking = amendSummaryBooking(reservationId = "6005657", amountAlreadyPaid = 25.0)
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_AMOUNTS_STUB_ID))
                installStub(reservationAmountsWithoutSummary(booking))

                val result =
                    ohipApi.getDetailsForAmend(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                        featureFlagOverrides = getDetailsForAmendFlagPins,
                    )

                result.attachEvidence("Amend Summary Missing Opera Summary")

                expect("returns the zeroed accumulator because the reservation adds nothing") {
                    result.response.status.value shouldBe 200
                    result.body.net shouldBe 0.0
                    result.body.totalCostOfStay shouldBe 0.0
                    result.body.outStandingCostOfStay shouldBe 0.0
                }

                expect("reads the reservation rate-info summary once") {
                    // GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo for the single id.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 0
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun amendSummaryBooking(
    reservationId: String,
    amountAlreadyPaid: Double,
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
                ),
            ),
    )
}

private fun twoReservationAmendSummaryBooking(
    firstReservationId: String,
    secondReservationId: String,
): Booking {
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
                    reservationId = firstReservationId,
                    roomType = firstRate.roomType,
                    adults = firstRate.adults,
                    status = ReservationStatus.RESERVED,
                    amountAlreadyPaid = 50.0,
                ),
                BookingRoom(
                    reservationId = secondReservationId,
                    roomType = secondRate.roomType,
                    adults = secondRate.adults,
                    status = ReservationStatus.RESERVED,
                    amountAlreadyPaid = 0.0,
                ),
            ),
    )
}
