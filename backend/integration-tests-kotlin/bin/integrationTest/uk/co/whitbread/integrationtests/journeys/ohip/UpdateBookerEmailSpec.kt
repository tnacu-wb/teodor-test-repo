package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateBookerEmailRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_PROFILE_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_PROFILE_UPDATE_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_PUT_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.externalReservationProfileFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationEmpty
import uk.co.whitbread.integrationtests.stubs.opera.custom.putReservationFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.updateProfileBadRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.updateProfileFailure
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

// Both flow-listed flags are the fixed-false token-service invariants: USE_TOKEN_SERVICE is
// environment-pinned OFF and USE_TOKEN_REFRESH_SKEW is evaluated once at client construction.
private val updateBookerEmailFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
        OhipFeatureFlag.USE_TOKEN_REFRESH_SKEW to false,
    )

/**
 * Proves `PUT /ohip/v1/reservations/email`: one full Opera reservation read per distinct id,
 * then exactly one CRM profile read and one emails-only CRM profile update for the globally
 * resolved primary-guest profile id, then one ReservationContact restamp PUT per distinct id —
 * with the empty-read skip, the read/profile-read/profile-update/reservation-update failure
 * mappings, the profile-update retry exhaustion, and the no-rollback boundary.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/UpdateBookerEmail.md
 */
class UpdateBookerEmailSpec :
    JourneySpec(
        "OHIP adapter updates the booker email on the guest profile and reservations",
        {
            val ohipApi = OhipApi()

            scenario("two reservations of one guest profile get one profile write and two restamps") {
                val booking =
                    bookerEmailBooking(
                        reservationIds = listOf("6152106", "6152107"),
                        bookerEmailAfterUpdate = NEW_BOOKER_EMAIL,
                    )

                installFor(booking)

                val result =
                    ohipApi.updateBookerEmail(
                        request =
                            UpdateBookerEmailRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf("6152106", "6152107"),
                                emailAddress = NEW_BOOKER_EMAIL,
                            ),
                        testId = testId,
                        featureFlagOverrides = updateBookerEmailFlagPins,
                    )

                result.attachEvidence("Update Booker Email")

                expect("returns 200 with an empty body") {
                    result.response.status.value shouldBe 200
                }

                expect("reads both reservations, writes the one profile once, restamps both reservations") {
                    // Two GET /rsv/v1/hotels/{hotelId}/reservations/{id}; then exactly ONE
                    // GET /crm/v1/profiles/P-100 and ONE emails-only-pinned
                    // PUT /crm/v1/profiles/P-100 for the globally resolved primary-guest id;
                    // then one ReservationContact-pinned PUT /rsv/.../reservations/{id} per
                    // distinct id (order not asserted — serialized at maxConcurrency=1).
                    callCount(Upstream.OPERA) shouldBe 6
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an empty reservation read resolves no profile so nothing is written") {
                val booking = bookerEmailBooking(reservationIds = listOf("6152106"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationEmpty(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.updateBookerEmail(
                        request =
                            UpdateBookerEmailRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                emailAddress = NEW_BOOKER_EMAIL,
                            ),
                        testId = testId,
                        featureFlagOverrides = updateBookerEmailFlagPins,
                    )

                result.attachEvidence("Update Booker Email Empty Read")

                expect("returns 200 with an empty body on the do-nothing path") {
                    result.response.status.value shouldBe 200
                }

                expect("read the reservation once and touched no CRM or reservation write") {
                    // One empty-bodied 200 GET /rsv/.../reservations/{id}; the installed
                    // profile-read, profile-update and reservation-update capabilities stay
                    // provably untouched.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected reservation read maps to the get-reservation error before any write") {
                val booking = bookerEmailBooking(reservationIds = listOf("6152106"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationBadRequest(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.updateBookerEmail(
                        request =
                            UpdateBookerEmailRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                emailAddress = NEW_BOOKER_EMAIL,
                            ),
                        testId = testId,
                        featureFlagOverrides = updateBookerEmailFlagPins,
                    )

                result.attachEvidence("Update Booker Email Read Rejected")

                expect("returns the mapped get-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 960
                }

                expect("attempted only the single unretried reservation read") {
                    // One rejected GET /rsv/.../reservations/{id}; the read carries no retry
                    // spec and no write of any kind reaches Opera.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected profile read maps to the get-profiles error before any write") {
                val booking = bookerEmailBooking(reservationIds = listOf("6152106"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_PROFILE_GET_STUB_ID))
                installStub(externalReservationProfileFailure(booking.room))

                val result =
                    ohipApi.updateBookerEmail(
                        request =
                            UpdateBookerEmailRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                emailAddress = NEW_BOOKER_EMAIL,
                            ),
                        testId = testId,
                        featureFlagOverrides = updateBookerEmailFlagPins,
                    )

                result.attachEvidence("Update Booker Email Profile Read Rejected")

                expect("returns the mapped get-profiles error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 912
                }

                expect("read the reservation then attempted only the unretried profile read") {
                    // One GET /rsv/.../reservations/{id}, then one rejected
                    // GET /crm/v1/profiles/P-100 with no retry and no write.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a non-retryable profile-update rejection stops the flow before any restamp") {
                val booking = bookerEmailBooking(reservationIds = listOf("6152106"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_PROFILE_UPDATE_STUB_ID))
                installStub(updateProfileFailure(booking))

                val result =
                    ohipApi.updateBookerEmail(
                        request =
                            UpdateBookerEmailRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                emailAddress = NEW_BOOKER_EMAIL,
                            ),
                        testId = testId,
                        featureFlagOverrides = updateBookerEmailFlagPins,
                    )

                result.attachEvidence("Update Booker Email Profile Update Rejected")

                expect("returns the mapped update-profile error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 961
                }

                expect("attempted the profile update once and never started the restamp fan-out") {
                    // One GET /rsv/.../reservations/{id}, one GET /crm/v1/profiles/P-100, then
                    // the rejected PUT /crm/v1/profiles/P-100 — its Internal Server Error
                    // envelope is outside the retry predicate, so no retry and no
                    // reservation PUT.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a retryable profile-update rejection exhausts the retries with no restamp") {
                val booking = bookerEmailBooking(reservationIds = listOf("6152106"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_PROFILE_UPDATE_STUB_ID))
                installStub(updateProfileBadRequest(booking))

                val result =
                    ohipApi.updateBookerEmail(
                        request =
                            UpdateBookerEmailRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                emailAddress = NEW_BOOKER_EMAIL,
                            ),
                        testId = testId,
                        featureFlagOverrides = updateBookerEmailFlagPins,
                    )

                result.attachEvidence("Update Booker Email Profile Update Retry Exhausted")

                expect("returns the mapped exhausted-retries error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 971
                }

                expect("attempted the profile update four times and never restamped") {
                    // One GET /rsv/.../reservations/{id}, one GET /crm/v1/profiles/P-100, then
                    // the rejected PUT /crm/v1/profiles/P-100 once plus the adapter's three
                    // retries of the retryable Bad Request body; exhaustion stops the flow
                    // before any reservation PUT.
                    callCount(Upstream.OPERA) shouldBe 6
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 4
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected restamp maps to the change-reservation error with no profile rollback") {
                val booking = bookerEmailBooking(reservationIds = listOf("6152106"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationFailure(booking))

                val result =
                    ohipApi.updateBookerEmail(
                        request =
                            UpdateBookerEmailRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                emailAddress = NEW_BOOKER_EMAIL,
                            ),
                        testId = testId,
                        featureFlagOverrides = updateBookerEmailFlagPins,
                    )

                result.attachEvidence("Update Booker Email Restamp Rejected")

                expect("returns the mapped change-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 958
                }

                expect("wrote the profile exactly once and attempted the restamp once, no rollback") {
                    // One GET /rsv/.../reservations/{id}, one GET and exactly one accepted,
                    // unrepeated PUT /crm/v1/profiles/P-100 (the documented no-rollback
                    // boundary), then the rejected PUT /rsv/.../reservations/{id} whose
                    // Internal Server Error envelope is outside the retry predicate.
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private const val NEW_BOOKER_EMAIL = "new.booker@test.com"

private fun bookerEmailBooking(
    reservationIds: List<String>,
    bookerEmailAfterUpdate: String? = null,
): Booking {
    val arrival = LocalDate.now().plusDays(21)
    val rates =
        listOf(
            Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2),
            Rate(ratePlan = "SEMIFLEX", roomType = "TWINRM", adults = 1),
        ).take(reservationIds.size)

    // Both rooms carry the SAME guest profile so the concurrently resolved first primary
    // guest id is deterministic; the flow resolves it from reservationGuests/primary, so
    // reservationContact is deliberately not set.
    val guestProfile =
        GuestProfile(
            profileId = "P-100",
            firstName = "Priya",
            lastName = "Booker",
            email = "old.booker@test.com",
            phone = "+447700900100",
            addressLine = "1 High Street",
            city = "London",
            postcode = "SW1A 1AA",
        )

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
                    guestProfile = guestProfile,
                    bookerEmailAfterUpdate = bookerEmailAfterUpdate,
                )
            },
    )
}
