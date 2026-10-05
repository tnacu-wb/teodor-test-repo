package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateDiscountRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationEmpty
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

// The flow lists only the two Opera transport-auth flags; both are environment-pinned OFF and
// evaluated outside request scope, so every scenario states them at that fixed value.
private val updateDiscountFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
        OhipFeatureFlag.USE_TOKEN_REFRESH_SKEW to false,
    )

private const val NIGHTLY_RATE = 59.0
private const val NIGHTS = 2L

/**
 * Proves the two adapter-owned rejections of `PUT /ohip/v1/reservations/discount`: the complete
 * reservation read always happens first, then a discount above the aggregate nightly-rate total is
 * rejected with errCode 22, and a reservation Opera answers with an empty success body fails the
 * completeness guard with errCode 23. Neither path sends a reservation update.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/UpdateDiscount.md
 */
class UpdateDiscountSpec :
    JourneySpec(
        "OHIP adapter rejects ineligible reservation discounts",
        {
            val ohipApi = OhipApi()

            scenario("a discount above the aggregate nightly-rate total is rejected before any update") {
                val booking = discountBooking(reservationId = "6206201")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.updateDiscount(
                        request =
                            UpdateDiscountRequest(
                                reservationIds = listOf(reservationId),
                                hotelId = booking.hotel.hotelId,
                                currency = "GBP",
                                // One penny above the stay's aggregate nightly base total.
                                discountAmount = NIGHTLY_RATE * NIGHTS + 0.01,
                            ),
                        testId = testId,
                        featureFlagOverrides = updateDiscountFlagPins,
                    )

                result.attachEvidence("Update Discount Above Aggregate")

                expect("returns the invalid-discount-amount error") {
                    result.response.status.value shouldBe 400
                    result.errorBody?.errCode shouldBe 22
                }

                expect("reads the reservation and updates nothing") {
                    // One GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}; the permissive
                    // PUT default stays installed, so the zero proves the write phase never starts.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an empty Opera reservation body fails the completeness guard before any update") {
                val booking = discountBooking(reservationId = "6206202")
                val reservationId = requireNotNull(booking.room.reservationId)

                // Exceptional: an empty success body is a transport-level Opera response the
                // generic reservation-read capability cannot represent.
                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationEmpty(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.updateDiscount(
                        request =
                            UpdateDiscountRequest(
                                reservationIds = listOf(reservationId),
                                hotelId = booking.hotel.hotelId,
                                currency = "GBP",
                                discountAmount = 20.0,
                            ),
                        testId = testId,
                        featureFlagOverrides = updateDiscountFlagPins,
                    )

                result.attachEvidence("Update Discount Missing Reservation")

                expect("returns the reservation-not-found error") {
                    result.response.status.value shouldBe 404
                    result.errorBody?.errCode shouldBe 23
                }

                expect("attempts the reservation read and updates nothing") {
                    // One GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId} answered with an
                    // empty body; the permissive PUT default stays installed to prove the absence.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun discountBooking(reservationId: String): Booking {
    val arrival = LocalDate.now().plusDays(14)
    val bookedRate =
        Rate(
            ratePlan = "SEMIFLEX",
            roomType = "LOWDBL",
            adults = 2,
            nightlyRate = NIGHTLY_RATE,
            discountAllowed = true,
        )

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(bookedRate))),
        arrival = arrival,
        departure = arrival.plusDays(NIGHTS),
        rooms =
            listOf(
                BookingRoom(
                    reservationId = reservationId,
                    roomType = bookedRate.roomType,
                    ratePlan = bookedRate.ratePlan,
                    adults = bookedRate.adults,
                    status = ReservationStatus.RESERVED,
                ),
            ),
    )
}
