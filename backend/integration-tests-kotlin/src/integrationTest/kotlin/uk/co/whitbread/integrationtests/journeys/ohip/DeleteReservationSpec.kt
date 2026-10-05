package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_DELETE_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.deleteReservationFailure
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

// The opera token-service flags are environment-pinned OFF; no other flag gates this chain.
private val deleteReservationFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
    )

/**
 * Proves `DELETE /ohip/v1/reservations`: the adapter passes hotel and reservation ids
 * straight through to one Opera reservation DELETE, with no other downstream call and no
 * body in either direction, and returns 204.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/DeleteReservation.md
 */
class DeleteReservationSpec :
    JourneySpec(
        "OHIP adapter deletes a reservation",
        {
            val ohipApi = OhipApi()

            scenario("an existing reservation is deleted with a single Opera pass-through call") {
                val booking = deletableBooking(reservationId = "6007501")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.deleteReservation(
                        hotelId = booking.hotel.hotelId,
                        reservationId = reservationId,
                        testId = testId,
                        featureFlagOverrides = deleteReservationFlagPins,
                    )

                result.attachEvidence("Delete Reservation")

                expect("returns 204 with no body") {
                    result.response.status.value shouldBe 204
                }

                expect("issues exactly one Opera call: the reservation DELETE") {
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.DELETE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera rejection of the reservation delete maps to internal error 938") {
                val booking = deletableBooking(reservationId = "6007502")

                // A downstream failure is exceptional behavior, not a normal Booking world state.
                installFor(booking, excluded = setOf(OPERA_RESERVATION_DELETE_STUB_ID))
                installStub(deleteReservationFailure(booking))

                val result =
                    ohipApi.deleteReservation(
                        hotelId = booking.hotel.hotelId,
                        reservationId = requireNotNull(booking.room.reservationId),
                        testId = testId,
                        featureFlagOverrides = deleteReservationFlagPins,
                    )

                result.attachEvidence("Delete Reservation Opera Error")

                expect("returns the mapped delete-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 938
                }

                expect("stops after the single rejected DELETE without retrying") {
                    // One Opera call: the rejected reservation DELETE (the delete client
                    // carries no retry spec).
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.DELETE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun deletableBooking(reservationId: String): Booking {
    val arrival = LocalDate.now().plusDays(5)
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
                ),
            ),
    )
}
