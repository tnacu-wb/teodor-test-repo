package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateReservationCcAgentIdRequest
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
private val updateCcAgentIdFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
        OhipFeatureFlag.USE_TOKEN_REFRESH_SKEW to false,
    )

private const val CC_AGENT_ID = "jane.doe@wb.com"

/**
 * Proves `PUT /ohip/v1/reservations/ccAgentId` at the adapter boundary: every distinct requested
 * reservation id of one hotel receives the contact-centre agent identifier in Opera character UDF
 * `UDFC08`, sent as one shared body whose reservation identity comes only from the Opera URL,
 * optionally preceded by a completed clearing pass, with no reservation read, cache, or rollback
 * anywhere on the path.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/UpdateReservationCcAgentId.md
 */
class UpdateReservationCcAgentIdSpec :
    JourneySpec(
        "OHIP adapter writes the contact-centre agent id to reservations",
        {
            val ohipApi = OhipApi()

            scenario("two distinct reservations receive the same contact-centre agent id") {
                val booking =
                    ccAgentIdBooking(
                        reservationIds = listOf("6157901", "6157902"),
                        ccAgentIdAfterUpdate = CC_AGENT_ID,
                    )
                val reservationIds = booking.rooms.map { requireNotNull(it.reservationId) }

                installFor(booking)

                val result =
                    ohipApi.updateReservationCcAgentId(
                        request =
                            UpdateReservationCcAgentIdRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = reservationIds,
                                ccAgentId = CC_AGENT_ID,
                            ),
                        testId = testId,
                        featureFlagOverrides = updateCcAgentIdFlagPins,
                    )

                result.attachEvidence("Update Cc Agent Id")

                expect("returns an empty 200 response") {
                    result.response.status.value shouldBe 200
                }

                expect("updates both reservations once each and reads neither") {
                    // Two PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}, one per id in
                    // a single pass, each accepted only by the mapping pinning the request hotel
                    // and the single UDFC08 entry carrying this agent id; the reservation GET
                    // default is installed by the same gate, so the zero proves there is no read
                    // before the write and no rollback after it.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("clearFirst sends a completed clearing pass before the value pass") {
                val booking =
                    ccAgentIdBooking(
                        reservationIds = listOf("6157903"),
                        ccAgentIdAfterUpdate = CC_AGENT_ID,
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.updateReservationCcAgentId(
                        request =
                            UpdateReservationCcAgentIdRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                ccAgentId = CC_AGENT_ID,
                                clearFirst = true,
                            ),
                        testId = testId,
                        featureFlagOverrides = updateCcAgentIdFlagPins,
                    )

                result.attachEvidence("Update Cc Agent Id Clear First")

                expect("returns an empty 200 response") {
                    result.response.status.value shouldBe 200
                }

                expect("updates the one reservation twice and reads it never") {
                    // The one reservation URL receives two PUT
                    // /rsv/v1/hotels/{hotelId}/reservations/{reservationId} calls instead of one.
                    // The room's only installed mappings are the pinned pair — a UDFC08 entry with
                    // no value member, and a UDFC08 entry carrying the agent id — with no
                    // permissive fallback, so two accepted PUTs prove both exact bodies were sent.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera rejection of the clearing pass stops the value pass") {
                val booking = ccAgentIdBooking(reservationIds = listOf("6157904"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationFailure(booking))

                val result =
                    ohipApi.updateReservationCcAgentId(
                        request =
                            UpdateReservationCcAgentIdRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                ccAgentId = CC_AGENT_ID,
                                clearFirst = true,
                            ),
                        testId = testId,
                        featureFlagOverrides = updateCcAgentIdFlagPins,
                    )

                result.attachEvidence("Update Cc Agent Id Opera Rejection")

                expect("returns the mapped change-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 958
                }

                expect("attempts only the clearing pass and takes no compensating action") {
                    // One rejected PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}: the
                    // envelope is not retryable, and the clear pass is blocked to completion, so
                    // the value pass never starts and this clearFirst request sends 1 PUT, not 2.
                    // The installed reservation GET default proves there is no read or undo phase.
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

private fun ccAgentIdBooking(
    reservationIds: List<String>,
    ccAgentIdAfterUpdate: String? = null,
): Booking {
    val arrival = LocalDate.now().plusDays(28)
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
                    ccAgentIdAfterUpdate = ccAgentIdAfterUpdate,
                )
            },
    )
}
