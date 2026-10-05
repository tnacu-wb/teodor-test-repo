package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
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
private val updateExternalRefFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
        OhipFeatureFlag.USE_TOKEN_REFRESH_SKEW to false,
    )

private const val EXTERNAL_REFERENCE = "WB-EXTREF-8001"

/**
 * Proves `PUT /ohip/v1/reservations/externalRef` at the adapter boundary: every distinct
 * reservation id supplied in the query string receives one Opera reservation update whose body
 * carries the request hotel, that reservation's own id typed `Reservation`, and the single
 * requested external reference under the configured Opera id context, with duplicate ids
 * collapsed by the `Set` binding and no reservation read, cache, or rollback on the path.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/UpdateReservationsWithExternalRef.md
 */
class UpdateReservationsWithExternalRefSpec :
    JourneySpec(
        "OHIP adapter tags reservations with an external reference",
        {
            val ohipApi = OhipApi()

            scenario("two distinct reservations are tagged with the same external reference") {
                val booking =
                    externalRefBooking(
                        reservationIds = listOf("6158001", "6158002"),
                        externalReferenceAfterUpdate = EXTERNAL_REFERENCE,
                    )
                val reservationIds = booking.rooms.map { requireNotNull(it.reservationId) }

                installFor(booking)

                val result =
                    ohipApi.updateReservationsWithExternalRef(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = reservationIds,
                        externalReference = EXTERNAL_REFERENCE,
                        testId = testId,
                        featureFlagOverrides = updateExternalRefFlagPins,
                    )

                result.attachEvidence("Update Reservations With External Ref")

                expect("returns an empty 200 response") {
                    result.response.status.value shouldBe 200
                }

                expect("updates both reservations once each and reads neither") {
                    // Two PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}, one per
                    // distinct id, each accepted only by the mapping pinning the request hotel in a
                    // single reservation instruction, that room's id as the one Reservation-typed
                    // reservationIdList entry, and the one externalReferences entry carrying this
                    // reference under the Booking's WB_DIGITAL id context. The reservation GET
                    // default is installed by the same gate, so the zero proves there is no read
                    // phase before the writes and no rollback after them.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("the same reservation id supplied twice produces exactly one update") {
                val booking =
                    externalRefBooking(
                        reservationIds = listOf("6158003"),
                        externalReferenceAfterUpdate = EXTERNAL_REFERENCE,
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.updateReservationsWithExternalRef(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId, reservationId),
                        externalReference = EXTERNAL_REFERENCE,
                        testId = testId,
                        featureFlagOverrides = updateExternalRefFlagPins,
                    )

                result.attachEvidence("Update Reservations With External Ref Duplicate Id")

                expect("returns an empty 200 response") {
                    result.response.status.value shouldBe 200
                }

                expect("updates the one reservation exactly once and reads it never") {
                    // The query string lists the id twice, but Set binding collapses it before the
                    // fan-out, so the reservation URL receives one
                    // PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId} carrying the same
                    // fully pinned external-reference body, not two.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera rejection of the reservation update is mapped to the change-reservation error") {
                val booking = externalRefBooking(reservationIds = listOf("6158004"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationFailure(booking))

                val result =
                    ohipApi.updateReservationsWithExternalRef(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        externalReference = EXTERNAL_REFERENCE,
                        testId = testId,
                        featureFlagOverrides = updateExternalRefFlagPins,
                    )

                result.attachEvidence("Update Reservations With External Ref Opera Rejection")

                expect("returns the mapped change-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 958
                }

                expect("attempts the one update and takes no compensating action") {
                    // One rejected PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}: the
                    // Internal Server Error envelope is not retryable, so the single attempt is the
                    // whole Opera traffic. The installed reservation GET default proves the failure
                    // triggers no read and no undo.
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

private fun externalRefBooking(
    reservationIds: List<String>,
    externalReferenceAfterUpdate: String? = null,
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
                    externalReferenceAfterUpdate = externalReferenceAfterUpdate,
                )
            },
    )
}
