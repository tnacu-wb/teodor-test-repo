package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
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

// Both flow-listed flags are fixed-false Opera authentication invariants: the token service is
// environment-pinned OFF and token refresh skew is evaluated once when the client is constructed.
private val fetchReservationWithPreferenceFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
        OhipFeatureFlag.USE_TOKEN_REFRESH_SKEW to false,
    )

/**
 * Proves `GET /ohip/v1/rooms/fetchReservationWithPreferences`: the adapter maps a reservation's
 * Opera preference collections into the kiosk response and maps an Opera rejection to error 936.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/FetchReservationWithPreference.md
 */
class FetchReservationWithPreferenceSpec :
    JourneySpec(
        "OHIP adapter fetches a reservation's preferences",
        {
            val ohipApi = OhipApi()

            scenario("a reservation's preference collections are returned to the kiosk") {
                val booking = fetchReservationWithPreferenceBooking(reservationId = "6007601")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.fetchReservationWithPreferences(
                        hotelId = booking.hotel.hotelId,
                        reservationId = reservationId,
                        testId = testId,
                        featureFlagOverrides = fetchReservationWithPreferenceFlagPins,
                    )

                result.attachEvidence("Fetch Reservation Preferences")

                expect("returns the mapped preference collections") {
                    result.response.status.value shouldBe 200
                    result.body.kioskPreferenceCollection.size shouldBe 2
                    result.body.kioskPreferenceCollection[0].preferenceType shouldBe "SPECIALS"
                    result.body.kioskPreferenceCollection[0].preferenceTypeDescription shouldBe "Special requests"
                    result.body.kioskPreferenceCollection[0]
                        .kioskPreference[0]
                        .preferenceValue shouldBe "HIFLR"
                    result.body.kioskPreferenceCollection[0]
                        .kioskPreference[0]
                        .description shouldBe "High floor"
                    result.body.kioskPreferenceCollection[1]
                        .kioskPreference
                        .map { it.preferenceValue } shouldBe
                        listOf("NSMOKE", "AWAYLIFT")
                }

                expect("reads the reservation exactly once and touches no other upstream") {
                    // One Opera call: GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera reservation rejection is mapped to the preferences retrieval error") {
                val booking = fetchReservationWithPreferenceBooking(reservationId = "invalid-reservation-id")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(
                    getReservationBadRequest(
                        hotelId = booking.hotel.hotelId,
                        reservationId = reservationId,
                    ),
                )

                val result =
                    ohipApi.fetchReservationWithPreferences(
                        hotelId = booking.hotel.hotelId,
                        reservationId = reservationId,
                        testId = testId,
                        featureFlagOverrides = fetchReservationWithPreferenceFlagPins,
                    )

                result.attachEvidence("Fetch Reservation Preferences Opera Error")

                expect("returns the mapped reservation-preferences retrieval error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 936
                }

                expect("attempts the reservation read exactly once and touches no other upstream") {
                    // One Opera call: the rejected GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun fetchReservationWithPreferenceBooking(reservationId: String): Booking {
    val arrival = LocalDate.now().plusDays(21)
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
                    reservationPreferences =
                        listOf(
                            ReservationPreferenceCollection(
                                preferenceType = "SPECIALS",
                                preferenceTypeDescription = "Special requests",
                                values =
                                    listOf(
                                        ReservationPreferenceValue(
                                            value = "HIFLR",
                                            description = "High floor",
                                        ),
                                    ),
                            ),
                            ReservationPreferenceCollection(
                                preferenceType = "ROOM",
                                preferenceTypeDescription = "Room preferences",
                                values =
                                    listOf(
                                        ReservationPreferenceValue(value = "NSMOKE"),
                                        ReservationPreferenceValue(value = "AWAYLIFT"),
                                    ),
                            ),
                        ),
                ),
            ),
    )
}
