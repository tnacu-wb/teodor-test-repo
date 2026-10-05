package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.AttachReservationProfileRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_COMPANY_PROFILE_BY_ID_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_PUT_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.companyProfileByIdEmptyBody
import uk.co.whitbread.integrationtests.stubs.opera.custom.companyProfileByIdFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.putReservationBadRequest
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
import uk.co.whitbread.integrationtests.testkit.presets.Companies
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

// Both flow-listed flags are the fixed-false token-service invariants: USE_TOKEN_SERVICE is
// environment-pinned OFF and USE_TOKEN_REFRESH_SKEW is evaluated once at client construction.
private val attachProfileFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
        OhipFeatureFlag.USE_TOKEN_REFRESH_SKEW to false,
    )

/**
 * Proves `POST /ohip/v1/reservations/profiles`: exactly one Opera CRM profile read for the
 * requested profile id, then one body-pinned Company reservation-profile attach PUT per distinct
 * reservation id (duplicates collapse, an empty set sends no PUT), with the read-rejection,
 * empty-profile-guard, non-retryable-PUT, and retry-exhaustion failure mappings.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/AttachProfileToReservations.md
 */
class AttachProfileToReservationsSpec :
    JourneySpec(
        "OHIP adapter attaches a company profile to reservations",
        {
            val ohipApi = OhipApi()

            scenario("each distinct reservation gets one Company attach PUT after a single profile read") {
                val company = Companies.NEILL_TECHNICAL_SERVICES
                val booking =
                    attachProfileBooking(
                        reservationIds = listOf("6152106", "6152107"),
                        attachedCompanyProfileIdAfterUpdate = company.companyId,
                    )

                installFor(booking)

                val result =
                    ohipApi.attachProfileToReservations(
                        request =
                            AttachReservationProfileRequest(
                                // Three array entries, two distinct ids: the duplicate collapses.
                                reservationIds = listOf("6152106", "6152107", "6152106"),
                                hotelId = booking.hotel.hotelId,
                                profileId = company.companyId,
                            ),
                        testId = testId,
                        featureFlagOverrides = attachProfileFlagPins,
                    )

                result.attachEvidence("Attach Profile To Reservations")

                expect("returns 200 with an empty body") {
                    result.response.status.value shouldBe 200
                }

                expect("reads the profile once then sends one pinned attach PUT per distinct reservation") {
                    // One GET /crm/v1/profiles/{companyId}, then two
                    // PUT /rsv/v1/hotels/{hotelId}/reservations/{id} — the duplicate id sends
                    // no third PUT, and each PUT matched the Company reservationProfiles pin.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.GET_COMPANY) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected profile read maps to the profiles error before any reservation write") {
                val company = Companies.NEILL_TECHNICAL_SERVICES
                val booking = attachProfileBooking(reservationIds = listOf("6152106"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_COMPANY_PROFILE_BY_ID_STUB_ID))
                installStub(companyProfileByIdFailure(company))

                val result =
                    ohipApi.attachProfileToReservations(
                        request =
                            AttachReservationProfileRequest(
                                reservationIds = listOf(reservationId),
                                hotelId = booking.hotel.hotelId,
                                profileId = company.companyId,
                            ),
                        testId = testId,
                        featureFlagOverrides = attachProfileFlagPins,
                    )

                result.attachEvidence("Attach Profile Read Rejected")

                expect("returns the mapped get-profiles error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 912
                }

                expect("attempted only the single profile read") {
                    // One rejected GET /crm/v1/profiles/{companyId}, no retry, no write.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.GET_COMPANY) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an empty 2xx profile read trips the attach guard before any reservation write") {
                val company = Companies.NEILL_TECHNICAL_SERVICES
                val booking = attachProfileBooking(reservationIds = listOf("6152106"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_COMPANY_PROFILE_BY_ID_STUB_ID))
                installStub(companyProfileByIdEmptyBody(company))

                val result =
                    ohipApi.attachProfileToReservations(
                        request =
                            AttachReservationProfileRequest(
                                reservationIds = listOf(reservationId),
                                hotelId = booking.hotel.hotelId,
                                profileId = company.companyId,
                            ),
                        testId = testId,
                        featureFlagOverrides = attachProfileFlagPins,
                    )

                result.attachEvidence("Attach Profile Empty Read Body")

                expect("returns the adapter's own null-profile attach error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 50
                }

                expect("read the profile once and touched no reservation") {
                    // One empty-bodied 200 GET /crm/v1/profiles/{companyId}; the guard fires.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.GET_COMPANY) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a non-retryable Opera rejection of the attach PUT maps to the guest-update error") {
                val company = Companies.NEILL_TECHNICAL_SERVICES
                val booking = attachProfileBooking(reservationIds = listOf("6152106"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationFailure(booking))

                val result =
                    ohipApi.attachProfileToReservations(
                        request =
                            AttachReservationProfileRequest(
                                reservationIds = listOf(reservationId),
                                hotelId = booking.hotel.hotelId,
                                profileId = company.companyId,
                            ),
                        testId = testId,
                        featureFlagOverrides = attachProfileFlagPins,
                    )

                result.attachEvidence("Attach Profile Put Rejected")

                expect("returns the mapped put-reservations-guest error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 952
                }

                expect("read the profile then attempted the attach PUT once") {
                    // One GET /crm/v1/profiles/{companyId}, then the rejected
                    // PUT /rsv/v1/hotels/{hotelId}/reservations/{id} with no retry.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.GET_COMPANY) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a retryable Opera rejection of the attach PUT exhausts the retries") {
                val company = Companies.NEILL_TECHNICAL_SERVICES
                val booking = attachProfileBooking(reservationIds = listOf("6152106"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationBadRequest(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.attachProfileToReservations(
                        request =
                            AttachReservationProfileRequest(
                                reservationIds = listOf(reservationId),
                                hotelId = booking.hotel.hotelId,
                                profileId = company.companyId,
                            ),
                        testId = testId,
                        featureFlagOverrides = attachProfileFlagPins,
                    )

                result.attachEvidence("Attach Profile Put Retry Exhausted")

                expect("returns the mapped exhausted-retries error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 971
                }

                expect("read the profile then attempted the attach PUT four times") {
                    // One GET /crm/v1/profiles/{companyId}, then the rejected
                    // PUT /rsv/v1/hotels/{hotelId}/reservations/{id} once plus the adapter's
                    // three retries of the retryable Bad Request body.
                    callCount(Upstream.OPERA) shouldBe 5
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 4
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.GET_COMPANY) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun attachProfileBooking(
    reservationIds: List<String>,
    attachedCompanyProfileIdAfterUpdate: String? = null,
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
        companies = listOf(Companies.NEILL_TECHNICAL_SERVICES),
        rooms =
            reservationIds.mapIndexed { index, reservationId ->
                BookingRoom(
                    reservationId = reservationId,
                    roomType = rates[index].roomType,
                    adults = rates[index].adults,
                    status = ReservationStatus.RESERVED,
                    attachedCompanyProfileIdAfterUpdate = attachedCompanyProfileIdAfterUpdate,
                )
            },
    )
}
