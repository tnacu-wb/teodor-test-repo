package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.CreateProfileKioskRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.KioskGuestDetails
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_PROFILE_CREATE_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_PUT_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.createProfileFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.putReservationFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

// The opera token-service flags are environment-pinned OFF; no other flag gates this chain.
private val createProfileFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
    )

/**
 * Proves `POST /ohip/v1/profile/createProfile` (kiosk): the adapter loads the reservation's
 * existing guest profile ids, creates one Opera CRM profile per supplied staying guest, and
 * PUTs the reservation with the combined profile list. The handler is a void method, so the
 * runtime success is 200 with an empty body.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/CreateProfileKiosk.md
 */
class CreateProfileKioskSpec :
    JourneySpec(
        "OHIP adapter creates kiosk guest profiles on a reservation",
        {
            val ohipApi = OhipApi()

            scenario("one staying guest gets a CRM profile and joins the reservation") {
                val booking = kioskBooking(reservationId = "6007301")

                installFor(booking)

                val result =
                    ohipApi.createProfileKiosk(
                        hotelId = booking.hotel.hotelId,
                        reservationId = requireNotNull(booking.room.reservationId),
                        request =
                            CreateProfileKioskRequest(
                                guestDetails =
                                    listOf(
                                        KioskGuestDetails(
                                            givenName = "Kiosk",
                                            surname = "Guest",
                                            nationality = "GB",
                                        ),
                                    ),
                            ),
                        testId = testId,
                        featureFlagOverrides = createProfileFlagPins,
                    )

                result.attachEvidence("Create Profile Kiosk One Guest")

                expect("returns 200 with an empty body") {
                    result.response.status.value shouldBe 200
                    result.bodyText shouldBe ""
                }

                expect("reads the reservation, creates one CRM profile, and updates the reservation") {
                    // Three Opera calls: GET reservation (GuestLastStay), POST /crm/v1/profiles,
                    // PUT reservation with the combined profile list.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an empty guest list still re-attaches the existing profiles") {
                val booking = kioskBooking(reservationId = "6007302")

                // The CRM create-profile stub is installed by the room's guestProfile fact, so
                // the count below proves the service made no profile POST rather than lacking
                // a mock for one.
                installFor(booking)

                val result =
                    ohipApi.createProfileKiosk(
                        hotelId = booking.hotel.hotelId,
                        reservationId = requireNotNull(booking.room.reservationId),
                        request = CreateProfileKioskRequest(guestDetails = emptyList()),
                        testId = testId,
                        featureFlagOverrides = createProfileFlagPins,
                    )

                result.attachEvidence("Create Profile Kiosk No Guests")

                expect("returns 200 with an empty body") {
                    result.response.status.value shouldBe 200
                    result.bodyText shouldBe ""
                }

                expect("skips profile creation and still updates the reservation") {
                    // Two Opera calls: GET reservation then PUT reservation; no CRM POST.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera reservation lookup error stops before any profile is created") {
                val booking = kioskBooking(reservationId = "6007303")
                val reservationId = requireNotNull(booking.room.reservationId)

                // The CRM create and reservation PUT stubs stay installed, so the counts
                // below prove the adapter stopped rather than lacking a mock to call.
                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(
                    getReservationBadRequest(
                        hotelId = booking.hotel.hotelId,
                        reservationId = reservationId,
                    ),
                )

                val result =
                    ohipApi.createProfileKiosk(
                        hotelId = booking.hotel.hotelId,
                        reservationId = reservationId,
                        request = oneGuestRequest(),
                        testId = testId,
                        featureFlagOverrides = createProfileFlagPins,
                    )

                result.attachEvidence("Create Profile Kiosk Reservation Lookup Error")

                expect("returns the mapped profile-id lookup error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 926
                }

                expect("stops after the failed reservation GET") {
                    // One Opera call: the failed reservation GET; no CRM POST, no PUT.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a CRM profile-create error stops before the reservation update") {
                val booking = kioskBooking(reservationId = "6007304")

                installFor(booking, excluded = setOf(OPERA_PROFILE_CREATE_STUB_ID))
                installStub(createProfileFailure(booking))

                val result =
                    ohipApi.createProfileKiosk(
                        hotelId = booking.hotel.hotelId,
                        reservationId = requireNotNull(booking.room.reservationId),
                        request = oneGuestRequest(),
                        testId = testId,
                        featureFlagOverrides = createProfileFlagPins,
                    )

                result.attachEvidence("Create Profile Kiosk CRM Create Error")

                expect("returns the mapped profile-create error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 922
                }

                expect("stops after the rejected CRM POST without updating the reservation") {
                    // Two Opera calls: reservation GET, then the rejected CRM POST; no PUT.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a reservation update rejection surfaces after the profile is created") {
                val booking = kioskBooking(reservationId = "6007305")

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationFailure(booking))

                val result =
                    ohipApi.createProfileKiosk(
                        hotelId = booking.hotel.hotelId,
                        reservationId = requireNotNull(booking.room.reservationId),
                        request = oneGuestRequest(),
                        testId = testId,
                        featureFlagOverrides = createProfileFlagPins,
                    )

                result.attachEvidence("Create Profile Kiosk Reservation Update Error")

                expect("returns the mapped add-profile reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 927
                }

                expect("fails on the reservation PUT after the GET and CRM create succeeded") {
                    // Three Opera calls: reservation GET, CRM POST, then the rejected PUT.
                    // The rejection body is non-retryable, so the PUT is attempted once.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun oneGuestRequest(): CreateProfileKioskRequest =
    CreateProfileKioskRequest(
        guestDetails =
            listOf(
                KioskGuestDetails(
                    givenName = "Kiosk",
                    surname = "Guest",
                    nationality = "GB",
                ),
            ),
    )

private fun kioskBooking(reservationId: String): Booking {
    val arrival = LocalDate.now().plusDays(3)
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
                    guestProfile =
                        GuestProfile(
                            profileId = "5008801",
                            firstName = "Primary",
                            lastName = "Guest",
                            email = "primary.guest@example.com",
                            phone = "+441110000001",
                            addressLine = "1 Kiosk Street",
                            city = "London",
                            postcode = "KI1 TEST",
                            country = "GB",
                        ),
                ),
            ),
    )
}
