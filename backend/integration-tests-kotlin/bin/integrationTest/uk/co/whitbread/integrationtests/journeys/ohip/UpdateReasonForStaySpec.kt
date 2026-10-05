package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateReasonForStayRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_PUT_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.putReservationFailure
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
private val updateReasonForStayFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
        OhipFeatureFlag.USE_TOKEN_REFRESH_SKEW to false,
    )

private const val REASON_FOR_STAY = "LEI"

/**
 * Proves `PUT /ohip/v1/reservations/reasonForStay` at the adapter boundary: every requested
 * reservation id — duplicates included — receives its own Opera update carrying the request hotel
 * and that purpose of stay with no reservation read, and the public hotel and reservation ids are
 * derived from the Opera response bodies rather than echoed from the request.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/UpdateReasonForStay.md
 */
class UpdateReasonForStaySpec :
    JourneySpec(
        "OHIP adapter updates the reservation reason for stay",
        {
            val ohipApi = OhipApi()

            scenario("two distinct reservations receive the same purpose of stay") {
                val booking =
                    reasonForStayBooking(
                        reservationIds = listOf("6157701", "6157702"),
                        purposeOfStayAfterUpdate = REASON_FOR_STAY,
                    )
                val reservationIds = booking.rooms.map { requireNotNull(it.reservationId) }

                installFor(booking)

                val result =
                    ohipApi.updateReasonForStay(
                        request =
                            UpdateReasonForStayRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = reservationIds,
                                reasonForStay = REASON_FOR_STAY,
                            ),
                        testId = testId,
                        featureFlagOverrides = updateReasonForStayFlagPins,
                    )

                result.attachEvidence("Update Reason For Stay")

                expect("returns the hotel and reservation ids Opera reported") {
                    result.response.status.value shouldBe 200
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    // Derived from the Opera update bodies and filtered to identifier type
                    // Reservation, so the confirmation numbers those bodies also carry are absent;
                    // unordered because the fan-out emits in completion order, not input order.
                    result.body.reservationIds shouldContainExactlyInAnyOrder reservationIds
                }

                expect("updates both reservations and reads neither") {
                    // Two PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}, each accepted
                    // only by the mapping pinning the request hotel and that purposeOfStay; the
                    // reservation GET default is installed by the same gate, so the zero proves the
                    // path has no read before the write.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("the same reservation id supplied twice is updated twice") {
                val booking =
                    reasonForStayBooking(
                        reservationIds = listOf("6157703"),
                        purposeOfStayAfterUpdate = REASON_FOR_STAY,
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.updateReasonForStay(
                        request =
                            UpdateReasonForStayRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId, reservationId),
                                reasonForStay = REASON_FOR_STAY,
                            ),
                        testId = testId,
                        featureFlagOverrides = updateReasonForStayFlagPins,
                    )

                result.attachEvidence("Update Reason For Stay Duplicate Id")

                expect("returns the hotel and the repeated reservation id") {
                    result.response.status.value shouldBe 200
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    // No deduplication anywhere on the path: both updates emit their own body, so
                    // the derived list repeats the id.
                    result.body.reservationIds shouldContainExactlyInAnyOrder listOf(reservationId, reservationId)
                }

                expect("updates the one reservation twice and reads it never") {
                    // Two PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId} to the single
                    // reservation URL; the installed reservation GET default supplies the absence
                    // proof.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera rejection of the update maps to the change-reservation error") {
                val booking = reasonForStayBooking(reservationIds = listOf("6157704"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationFailure(booking))

                val result =
                    ohipApi.updateReasonForStay(
                        request =
                            UpdateReasonForStayRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                reasonForStay = REASON_FOR_STAY,
                            ),
                        testId = testId,
                        featureFlagOverrides = updateReasonForStayFlagPins,
                    )

                result.attachEvidence("Update Reason For Stay Opera Rejection")

                expect("returns the mapped change-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 958
                }

                expect("attempts the update once and takes no compensating action") {
                    // One rejected PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}: the
                    // envelope is not retryable, and the installed reservation GET default proves
                    // there is no read or undo phase after the failure.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun reasonForStayBooking(
    reservationIds: List<String>,
    purposeOfStayAfterUpdate: String? = null,
): Booking {
    val arrival = LocalDate.now().plusDays(21)
    val rates =
        listOf(
            Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2),
            Rate(ratePlan = "SEMIFLEX", roomType = "TWINRM", adults = 1),
        ).take(reservationIds.size)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = rates)),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms =
            reservationIds.mapIndexed { index, reservationId ->
                BookingRoom(
                    reservationId = reservationId,
                    roomType = rates[index].roomType,
                    adults = rates[index].adults,
                    status = ReservationStatus.RESERVED,
                    purposeOfStayAfterUpdate = purposeOfStayAfterUpdate,
                )
            },
    )
}
