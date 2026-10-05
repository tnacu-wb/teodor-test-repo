package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.PreferencesCollection
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdatePreferencesRequest
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationPreferenceCollection
import uk.co.whitbread.integrationtests.testkit.model.ReservationPreferenceValue
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

// The flow lists only the two Opera transport-auth flags; both are environment-pinned OFF and
// evaluated outside request scope, so every scenario states them at that fixed value.
private val updatePreferencesFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
        OhipFeatureFlag.USE_TOKEN_REFRESH_SKEW to false,
    )

// The preference content the request writes to every reservation: one collection carrying a
// single value and one carrying two, so the per-string mapping into separate Opera preference
// entries is proved rather than assumed.
private val requestedPreferences =
    listOf(
        ReservationPreferenceCollection(
            preferenceType = "PILLOW",
            values = listOf(ReservationPreferenceValue(value = "FIRM")),
        ),
        ReservationPreferenceCollection(
            preferenceType = "SPECIALS",
            values =
                listOf(
                    ReservationPreferenceValue(value = "HIFLR"),
                    ReservationPreferenceValue(value = "QUIET"),
                ),
        ),
    )

/**
 * Proves `PUT /ohip/v1/reservations/preferences` at the adapter boundary: each requested
 * reservation of one hotel receives its own Opera update carrying the request hotel, only that
 * reservation's own id typed `Reservation`, and the shared preference collections with every
 * requested value emitted as its own Opera preference entry, with no reservation read anywhere on
 * the path and an empty `204 No Content` public response.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/UpdatePreferences.md
 */
class UpdatePreferencesSpec :
    JourneySpec(
        "OHIP adapter updates reservation preferences",
        {
            val ohipApi = OhipApi()

            scenario("two distinct reservations receive the same preference collections") {
                val booking = updatePreferencesBooking(reservationIds = listOf("6184001", "6184002"))
                val reservationIds = booking.rooms.map { requireNotNull(it.reservationId) }

                installFor(booking)

                val result =
                    ohipApi.updatePreferences(
                        request =
                            UpdatePreferencesRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationsIds = reservationIds,
                                preferencesCollections =
                                    requestedPreferences.map { collection ->
                                        PreferencesCollection(
                                            preferenceType = collection.preferenceType,
                                            preferences = collection.values.map { it.value },
                                        )
                                    },
                            ),
                        testId = testId,
                        featureFlagOverrides = updatePreferencesFlagPins,
                    )

                result.attachEvidence("Update Reservation Preferences")

                expect("returns an empty 204 response") {
                    result.response.status.value shouldBe 204
                }

                expect("updates both reservations and reads neither") {
                    // Two PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}, one per
                    // request list entry, each accepted only by the mapping pinning the request
                    // hotel, that reservation's own id typed Reservation with no sibling id
                    // anywhere in the body, and exactly those preference collections with each
                    // value on its own preference entry; the reservation GET default is installed
                    // by the same gate, so the zero proves the path has no read, guard, or
                    // rollback phase.
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

private fun updatePreferencesBooking(reservationIds: List<String>): Booking {
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
                    reservationPreferences = requestedPreferences,
                )
            },
    )
}
