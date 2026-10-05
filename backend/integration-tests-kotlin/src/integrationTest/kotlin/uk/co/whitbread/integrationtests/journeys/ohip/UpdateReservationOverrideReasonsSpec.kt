package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateReservationOverrideReasonsRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_PUT_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.putReservationFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.CharacterUdf
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

// The flow lists only the two Opera transport-auth flags; both are environment-pinned OFF and
// evaluated outside request scope, so every scenario states them at that fixed value.
private val updateOverrideReasonsFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
        OhipFeatureFlag.USE_TOKEN_REFRESH_SKEW to false,
    )

private const val OVERRIDE_UDF_NAME = "UDFC08"
private const val REASON_CODE = "ILL"
private const val REASON_NAME = "Medical Appointments"
private const val CALLER_NAME = "John Doe"
private const val MANAGER_NAME = "Jane Manager"

/**
 * Proves `PUT /ohip/v1/reservations/overrideReasons` at the adapter boundary: every distinct
 * requested reservation id of one hotel receives the same override-reason audit value in Opera
 * character UDF `UDFC08`, sent as one shared body whose reservation identity comes only from the
 * Opera URL, with no reservation read, cache, or rollback anywhere on the path.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/UpdateReservationOverrideReasons.md
 */
class UpdateReservationOverrideReasonsSpec :
    JourneySpec(
        "OHIP adapter writes reservation override reasons",
        {
            val ohipApi = OhipApi()

            scenario("two distinct reservations receive the same override-reason audit value") {
                val booking =
                    overrideReasonsBooking(
                        reservationIds = listOf("6157801", "6157802"),
                        overrideReasonValue = "$REASON_CODE,$REASON_NAME,$CALLER_NAME,$MANAGER_NAME",
                    )
                val reservationIds = booking.rooms.map { requireNotNull(it.reservationId) }

                installFor(booking)

                val result =
                    ohipApi.updateReservationOverrideReasons(
                        request =
                            UpdateReservationOverrideReasonsRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = reservationIds,
                                reasonCode = REASON_CODE,
                                reasonName = REASON_NAME,
                                callerName = CALLER_NAME,
                                managerName = MANAGER_NAME,
                            ),
                        testId = testId,
                        featureFlagOverrides = updateOverrideReasonsFlagPins,
                    )

                result.attachEvidence("Update Override Reasons")

                expect("returns an empty 200 response") {
                    result.response.status.value shouldBe 200
                }

                expect("updates both reservations and reads neither") {
                    // Two PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}, each accepted
                    // only by the mapping pinning the request hotel and the single UDFC08 entry
                    // carrying the four comma-joined components; the reservation GET default is
                    // installed by the same gate, so the zero proves there is no read before the
                    // write and no rollback after it.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("the same reservation id supplied twice is updated once without a manager name") {
                val booking =
                    overrideReasonsBooking(
                        reservationIds = listOf("6157803"),
                        overrideReasonValue = "$REASON_CODE,$REASON_NAME,$CALLER_NAME",
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.updateReservationOverrideReasons(
                        request =
                            UpdateReservationOverrideReasonsRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId, reservationId),
                                reasonCode = REASON_CODE,
                                reasonName = REASON_NAME,
                                callerName = CALLER_NAME,
                            ),
                        testId = testId,
                        featureFlagOverrides = updateOverrideReasonsFlagPins,
                    )

                result.attachEvidence("Update Override Reasons Duplicate Id")

                expect("returns an empty 200 response") {
                    result.response.status.value shouldBe 200
                }

                expect("updates the one reservation once and reads it never") {
                    // The request array repeats the id, but the service binds it to a Set, so the
                    // fan-out sends a single PUT
                    // /rsv/v1/hotels/{hotelId}/reservations/{reservationId} — accepted only by the
                    // mapping pinning the three-component UDFC08 value the omitted manager name
                    // produces.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera rejection of the update maps to the change-reservation error") {
                val booking = overrideReasonsBooking(reservationIds = listOf("6157804"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationFailure(booking))

                val result =
                    ohipApi.updateReservationOverrideReasons(
                        request =
                            UpdateReservationOverrideReasonsRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                reasonCode = REASON_CODE,
                                reasonName = REASON_NAME,
                                callerName = CALLER_NAME,
                                managerName = MANAGER_NAME,
                            ),
                        testId = testId,
                        featureFlagOverrides = updateOverrideReasonsFlagPins,
                    )

                result.attachEvidence("Update Override Reasons Opera Rejection")

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

private fun overrideReasonsBooking(
    reservationIds: List<String>,
    overrideReasonValue: String? = null,
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
                    characterUdfsAfterUpdate =
                        overrideReasonValue?.let {
                            listOf(CharacterUdf(name = OVERRIDE_UDF_NAME, value = it))
                        },
                )
            },
    )
}
