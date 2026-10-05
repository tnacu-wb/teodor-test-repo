package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

// No request-scoped feature flag gates this chain: the flow doc lists none, and the two Opera
// authentication flags on the path (release_ohip_use_token_service,
// release_ohip_use_token_refresh_skew) are evaluated outside the request context, so
// featureFlagOverrides cannot reach them.
private val marketingPreferencesFlagPins = mapOf<FeatureFlag, Boolean>()

/**
 * Proves `GET /ohip/v1/reservations/marketingPreferences`: the adapter reads the Opera
 * reservation, follows its `ReservationContact` profile to the Opera CRM profile API and returns
 * that profile's marketing opt-in and contact email, answers 200 with an empty body when the
 * reservation carries no `ReservationContact` profile (never touching the profile API), and maps
 * an Opera reservation-read rejection to a 500 without reaching the profile leg.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/GetMarketingPreferences.md
 */
class GetMarketingPreferencesSpec :
    JourneySpec(
        "OHIP adapter reads the marketing preferences of a reservation's booker profile",
        {
            val ohipApi = OhipApi()

            // The booker's recorded opt-in state is a straight pass-through of
            // profileDetails.privacyInfo.optInEmail, so only the recorded/never-recorded split is a
            // materially different path: the opposite boolean value exercises no further
            // branch and gets no scenario of its own.
            scenario("a reservation whose contact profile records a marketing opt-in returns it with the contact email") {
                val booking =
                    marketingPreferencesBooking(
                        reservationId = "6006503",
                        reservationContact = true,
                        marketingOptIn = true,
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.getMarketingPreferences(
                        hotelId = booking.hotel.hotelId,
                        reservationId = reservationId,
                        testId = testId,
                        featureFlagOverrides = marketingPreferencesFlagPins,
                    )

                result.attachEvidence("Get Marketing Preferences Opted In")

                expect("returns 200 with the booker's opt-in state and contact email") {
                    result.response.status.value shouldBe 200
                    // Parsed rather than substring-matched, so serializer formatting changes
                    // cannot fail the scenario while behavior is correct.
                    val body = Json.parseToJsonElement(result.body).jsonObject
                    body.getValue("optIn").jsonPrimitive.boolean shouldBe true
                    body.getValue("contactValue").jsonPrimitive.content shouldBe "amelia.wright@test.com"
                }

                expect("reads the reservation once and its contact profile once") {
                    // Two Opera calls: GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}
                    // then GET /crm/v1/profiles/{profileId} for the ReservationContact profile.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            // No reservationContact fact, so the reservation carries only its Guest profile and the
            // service's ReservationContact selection yields an empty id set.
            scenario("a reservation without a reservation-contact profile returns 200 without reading a profile") {
                val booking = marketingPreferencesBooking(reservationId = "6006501")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.getMarketingPreferences(
                        hotelId = booking.hotel.hotelId,
                        reservationId = reservationId,
                        testId = testId,
                        featureFlagOverrides = marketingPreferencesFlagPins,
                    )

                result.attachEvidence("Get Marketing Preferences")

                expect("returns 200 with an empty body") {
                    result.response.status.value shouldBe 200
                    result.body.isBlank() shouldBe true
                }

                expect("reads the reservation once and never reads the booker profile") {
                    // One Opera call: GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    // /crm/v1/profiles/{profileId} is stubbed by the guestProfile fact and never called.
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a reservation read rejected by Opera surfaces a server error before the profile leg") {
                val booking = marketingPreferencesBooking(reservationId = "6006502")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationBadRequest(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.getMarketingPreferences(
                        hotelId = booking.hotel.hotelId,
                        reservationId = reservationId,
                        testId = testId,
                        featureFlagOverrides = marketingPreferencesFlagPins,
                    )

                result.attachEvidence("Get Marketing Preferences Opera Rejection")

                expect("returns the mapped get-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 960
                }

                expect("attempts one reservation read and never reads the booker profile") {
                    // One Opera call: the rejected GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    // /crm/v1/profiles/{profileId} is stubbed by the guestProfile fact and never called.
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun marketingPreferencesBooking(
    reservationId: String,
    reservationContact: Boolean = false,
    marketingOptIn: Boolean? = null,
): Booking {
    val arrival = LocalDate.now().plusDays(14)
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
                    // Serves the booker profile on the opted-in path and, on the absence paths,
                    // installs booking.opera.get-profile so the zero GET_PROFILE count is a real
                    // absence proof rather than an unstubbed upstream.
                    guestProfile =
                        GuestProfile(
                            profileId = "PROF-6501",
                            firstName = "Amelia",
                            lastName = "Wright",
                            email = "amelia.wright@test.com",
                            phone = "+447700900001",
                            addressLine = "1 High Street",
                            city = "London",
                            postcode = "SW1A 1AA",
                            reservationContact = reservationContact,
                            marketingOptIn = marketingOptIn,
                        ),
                ),
            ),
    )
}
