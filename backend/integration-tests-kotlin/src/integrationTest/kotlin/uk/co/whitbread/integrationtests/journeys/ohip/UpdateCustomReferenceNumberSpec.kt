package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateCustomReferenceNumberRequest
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
private val updateCustomReferenceNumberFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
        OhipFeatureFlag.USE_TOKEN_REFRESH_SKEW to false,
    )

private const val CUSTOM_REFERENCE_NUMBER = "CRN-73104"

/**
 * Proves `PUT /ohip/v1/reservations/customReferenceNumber` at the adapter boundary: each distinct
 * requested reservation of one hotel receives its own Opera update carrying that reservation's
 * identity, the request hotel, and the shared custom reference, with no reservation read anywhere
 * on the path.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/UpdateCustomReferenceNumber.md
 */
class UpdateCustomReferenceNumberSpec :
    JourneySpec(
        "OHIP adapter updates reservation custom reference numbers",
        {
            val ohipApi = OhipApi()

            scenario("two distinct reservations each receive the same custom reference") {
                val booking =
                    customReferenceNumberBooking(
                        reservationIds = listOf("6153101", "6153102"),
                        customReferenceAfterUpdate = CUSTOM_REFERENCE_NUMBER,
                    )

                installFor(booking)

                val result =
                    ohipApi.updateCustomReferenceNumber(
                        request =
                            UpdateCustomReferenceNumberRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = booking.rooms.map { requireNotNull(it.reservationId) },
                                customReferenceNumber = CUSTOM_REFERENCE_NUMBER,
                            ),
                        testId = testId,
                        featureFlagOverrides = updateCustomReferenceNumberFlagPins,
                    )

                result.attachEvidence("Update Custom Reference Number")

                expect("returns an empty 200 response") {
                    result.response.status.value shouldBe 200
                }

                expect("updates both reservations and reads neither") {
                    // Two PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}, each accepted
                    // only by the mapping pinning that room's identity, hotel, and custom
                    // reference; the reservation GET default is installed by the same gate, so the
                    // zero proves the path has no read before the write.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun customReferenceNumberBooking(
    reservationIds: List<String>,
    customReferenceAfterUpdate: String,
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
                    customReferenceAfterUpdate = customReferenceAfterUpdate,
                )
            },
    )
}
