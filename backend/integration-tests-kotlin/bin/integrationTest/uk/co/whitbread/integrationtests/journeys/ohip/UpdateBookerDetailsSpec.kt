package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.BookerAddressCnp
import uk.co.whitbread.integrationtests.clients.ohip.model.BookerDetailsCnp
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateBookerDetailsRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_COMPANY_PROFILE_BY_ID_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_PROFILE_CREATE_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_PROFILE_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_PROFILE_UPDATE_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_PUT_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.companyProfileByIdEmptyBody
import uk.co.whitbread.integrationtests.stubs.opera.custom.createProfileFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.externalReservationProfileFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.putReservationBadRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.putReservationFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.updateProfileFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booker
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Company
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationAttachedProfiles
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Companies
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

// Both flow-listed flags are the fixed-false token-service invariants: USE_TOKEN_SERVICE is
// environment-pinned OFF and USE_TOKEN_REFRESH_SKEW is evaluated once at client construction.
private val updateBookerDetailsFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
        OhipFeatureFlag.USE_TOKEN_REFRESH_SKEW to false,
    )

/**
 * Proves `PUT /ohip/v1/reservations/booker`: one full Opera reservation read per distinct id,
 * then one merged CRM profile amend for the first ReservationContact profile id, then the
 * three-way company decision — rename the attached company profile in place (no reservation
 * write), detach it with one body-pinned reservation PUT per requested id, or create a name-only
 * company profile and attach it — with the no-contact absence proof and the read, profile-read,
 * profile-update, company-create, reservation-update, retry-exhaustion, and empty-company-read
 * failure mappings.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/UpdateBookerDetails.md
 */
class UpdateBookerDetailsSpec :
    JourneySpec(
        "OHIP adapter updates the booker and company details on reservations",
        {
            val ohipApi = OhipApi()

            scenario("a booker-only update amends the reservation-contact profile and writes nothing else") {
                val booking = bookerDetailsBooking(reservationIds = listOf("6700101"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.updateBookerDetails(
                        request =
                            UpdateBookerDetailsRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                booker = requestBooker(companyName = null),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateBookerDetailsFlagPins,
                    )

                result.attachEvidence("Update Booker Details")

                expect("returns 200 with an empty body") {
                    result.response.status.value shouldBe 200
                }

                expect("reads the reservation then amends only the contact profile") {
                    // One GET /rsv/.../reservations/{id}, one GET and one merged
                    // PUT /crm/v1/profiles/P-6700101; no company decision fires, so no
                    // profile POST and no reservation PUT.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a non-blank company name renames the attached company profile in place") {
                val booking =
                    bookerDetailsBooking(
                        reservationIds = listOf("6700201"),
                        attachedCompanyProfileId = Companies.NEILL_TECHNICAL_SERVICES.companyId,
                        companies = listOf(Companies.NEILL_TECHNICAL_SERVICES),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.updateBookerDetails(
                        request =
                            UpdateBookerDetailsRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                booker = requestBooker(companyName = "Neill Technical Services Group"),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateBookerDetailsFlagPins,
                    )

                result.attachEvidence("Update Booker Details Company Rename")

                expect("returns 200 with an empty body") {
                    result.response.status.value shouldBe 200
                }

                expect("amends the booker then the company profile with no reservation write") {
                    // One GET /rsv/.../reservations/{id}; then GET+PUT /crm/v1/profiles for the
                    // booker P-6700201 and for the attached company 2569623 (the GET_PROFILE and
                    // UPDATE_PROFILE counters aggregate both ids; a wrong id would be an
                    // unmatched 404). The rename is purely a CRM operation: reservation PUT 0.
                    callCount(Upstream.OPERA) shouldBe 5
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 2
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a blank company name detaches the shared company from both requested reservations") {
                val booking =
                    bookerDetailsBooking(
                        reservationIds = listOf("6700301", "6700302"),
                        reservationContact = listOf(true, false),
                        attachedCompanyProfileId = Companies.NEILL_TECHNICAL_SERVICES.companyId,
                        attachedProfilesAfterUpdate =
                            ReservationAttachedProfiles(
                                bookerProfileId = "P-6700301",
                                companyProfileId = null,
                            ),
                        companies = listOf(Companies.NEILL_TECHNICAL_SERVICES),
                    )

                installFor(booking)

                val result =
                    ohipApi.updateBookerDetails(
                        request =
                            UpdateBookerDetailsRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf("6700301", "6700302"),
                                booker = requestBooker(companyName = ""),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateBookerDetailsFlagPins,
                    )

                result.attachEvidence("Update Booker Details Company Detach")

                expect("returns 200 with an empty body") {
                    result.response.status.value shouldBe 200
                }

                expect("amends one booker profile then sends one pinned detach PUT per reservation") {
                    // Two GET /rsv/.../reservations/{id} (one per distinct id, unordered); one
                    // GET and one PUT /crm/v1/profiles/P-6700301 — one booker per request, not
                    // per reservation, and the company profile itself is never read or written;
                    // then one PUT /rsv/.../reservations/{id} per request entry, each matching
                    // the ReservationContact-restated, profile-id-free Company detach pin
                    // (counts only — serialized at maxConcurrency 1, order not asserted).
                    callCount(Upstream.OPERA) shouldBe 6
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a company name with no company attached creates a name-only profile and attaches it") {
                val company = Companies.NEILL_TECHNICAL_SERVICES
                val booking =
                    bookerDetailsBooking(
                        reservationIds = listOf("6700401"),
                        attachedProfilesAfterUpdate =
                            ReservationAttachedProfiles(
                                bookerProfileId = "P-6700401",
                                companyProfileId = company.companyId,
                            ),
                        companies = listOf(company),
                        booker = Booker(firstName = REQUEST_FIRST_NAME, lastName = REQUEST_LAST_NAME),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.updateBookerDetails(
                        request =
                            UpdateBookerDetailsRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                booker = requestBooker(companyName = company.name),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateBookerDetailsFlagPins,
                    )

                result.attachEvidence("Update Booker Details Company Create And Attach")

                expect("returns 200 with an empty body") {
                    result.response.status.value shouldBe 200
                }

                expect("creates the name-only company profile then attaches booker and company") {
                    // One GET /rsv/.../reservations/{id}; one GET and one merged
                    // PUT /crm/v1/profiles/P-6700401; one POST /crm/v1/profiles matching the
                    // name-only Company mapping (profileType Company, companyName, no address
                    // block); then one PUT /rsv/.../reservations/{id} matching the pin that
                    // attaches booker P-6700401 and the created company id together.
                    callCount(Upstream.OPERA) shouldBe 5
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("no reservation-contact profile and no company decision writes nothing at all") {
                val booking =
                    bookerDetailsBooking(
                        reservationIds = listOf("6700501"),
                        reservationContact = listOf(false),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.updateBookerDetails(
                        request =
                            UpdateBookerDetailsRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                booker = requestBooker(companyName = null),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateBookerDetailsFlagPins,
                    )

                result.attachEvidence("Update Booker Details No-Op")

                expect("returns 200 with an empty body on the do-nothing path") {
                    result.response.status.value shouldBe 200
                }

                expect("read the reservation once and touched no installed write capability") {
                    // One GET /rsv/.../reservations/{id}; the room's guestProfile installs the
                    // profile-read, profile-amend and reservation-update capabilities, which
                    // stay provably uncalled — the booker part is silently dropped and
                    // the company decision is a no-op.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a duplicated reservation id is read once and detached twice") {
                val booking =
                    bookerDetailsBooking(
                        reservationIds = listOf("6700601"),
                        attachedCompanyProfileId = Companies.NEILL_TECHNICAL_SERVICES.companyId,
                        attachedProfilesAfterUpdate =
                            ReservationAttachedProfiles(
                                bookerProfileId = "P-6700601",
                                companyProfileId = null,
                            ),
                        companies = listOf(Companies.NEILL_TECHNICAL_SERVICES),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.updateBookerDetails(
                        request =
                            UpdateBookerDetailsRequest(
                                hotelId = booking.hotel.hotelId,
                                // The same id twice: reads dedupe through the HashSet, the
                                // detach PUT fan-out iterates the list as supplied.
                                reservationIds = listOf(reservationId, reservationId),
                                booker = requestBooker(companyName = null),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateBookerDetailsFlagPins,
                    )

                result.attachEvidence("Update Booker Details Duplicate Detach")

                expect("returns 200 with an empty body") {
                    result.response.status.value shouldBe 200
                }

                expect("read the distinct id once and sent one pinned detach PUT per list entry") {
                    // One GET /rsv/.../reservations/{id}; one GET and one
                    // PUT /crm/v1/profiles/P-6700601; then two PUT /rsv/.../reservations/{id}
                    // to the same URL, both matching the single detach body pin.
                    callCount(Upstream.OPERA) shouldBe 5
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected reservation read maps to the get-reservation error before any write") {
                val booking = bookerDetailsBooking(reservationIds = listOf("6700701"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationBadRequest(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.updateBookerDetails(
                        request =
                            UpdateBookerDetailsRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                // A would-be company create is also proved absent.
                                booker = requestBooker(companyName = "Neill Technical Services"),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateBookerDetailsFlagPins,
                    )

                result.attachEvidence("Update Booker Details Read Rejected")

                expect("returns the mapped get-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 960
                }

                expect("attempted only the single unretried reservation read") {
                    // One rejected GET /rsv/.../reservations/{id}; the read carries no retry
                    // spec and neither profile block runs.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected booker profile read stops the flow before the company block") {
                val booking =
                    bookerDetailsBooking(
                        reservationIds = listOf("6700801"),
                        attachedCompanyProfileId = Companies.NEILL_TECHNICAL_SERVICES.companyId,
                        companies = listOf(Companies.NEILL_TECHNICAL_SERVICES),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_PROFILE_GET_STUB_ID))
                installStub(externalReservationProfileFailure(booking.room))

                val result =
                    ohipApi.updateBookerDetails(
                        request =
                            UpdateBookerDetailsRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                booker = requestBooker(companyName = "Neill Technical Services Group"),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateBookerDetailsFlagPins,
                    )

                result.attachEvidence("Update Booker Details Profile Read Rejected")

                expect("returns the mapped get-profiles error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 912
                }

                expect("attempted only the booker read so the rename branch never started") {
                    // One GET /rsv/.../reservations/{id}, then the rejected
                    // GET /crm/v1/profiles/P-6700801 with no retry. The world is the rename
                    // branch, so GET_PROFILE staying at 1 proves the company profile was
                    // never read and nothing was written.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a non-retryable booker profile-update rejection never reaches the attach branch") {
                val booking = bookerDetailsBooking(reservationIds = listOf("6700901"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_PROFILE_UPDATE_STUB_ID))
                installStub(updateProfileFailure(booking))

                val result =
                    ohipApi.updateBookerDetails(
                        request =
                            UpdateBookerDetailsRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                booker = requestBooker(companyName = "Neill Technical Services"),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateBookerDetailsFlagPins,
                    )

                result.attachEvidence("Update Booker Details Profile Update Rejected")

                expect("returns the mapped update-profile error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 961
                }

                expect("attempted the booker amend once and never started the company create") {
                    // One GET /rsv/.../reservations/{id}, one GET /crm/v1/profiles/P-6700901,
                    // then the rejected PUT /crm/v1/profiles/P-6700901 — its Internal Server
                    // Error envelope is outside the retry predicate. The world is the attach
                    // branch, so profile POST 0 and reservation PUT 0 prove the company
                    // decision never ran.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected company profile create leaves the booker amend applied with no attach") {
                val company = Companies.NEILL_TECHNICAL_SERVICES
                val booking =
                    bookerDetailsBooking(
                        reservationIds = listOf("6701001"),
                        companies = listOf(company),
                        booker = Booker(firstName = REQUEST_FIRST_NAME, lastName = REQUEST_LAST_NAME),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_PROFILE_CREATE_STUB_ID))
                installStub(createProfileFailure(booking))

                val result =
                    ohipApi.updateBookerDetails(
                        request =
                            UpdateBookerDetailsRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                booker = requestBooker(companyName = company.name),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateBookerDetailsFlagPins,
                    )

                result.attachEvidence("Update Booker Details Company Create Rejected")

                expect("returns the mapped post-profile error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 953
                }

                expect("applied the booker amend then attempted only the company POST") {
                    // One GET /rsv/.../reservations/{id}; one GET and one already-applied
                    // PUT /crm/v1/profiles/P-6701001 (the branch's partial-completion shape);
                    // then the rejected POST /crm/v1/profiles with no retry and no
                    // reservation PUT.
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a non-retryable Opera rejection of the detach PUT maps to the change-reservation error") {
                val booking =
                    bookerDetailsBooking(
                        reservationIds = listOf("6701101"),
                        attachedCompanyProfileId = Companies.NEILL_TECHNICAL_SERVICES.companyId,
                        companies = listOf(Companies.NEILL_TECHNICAL_SERVICES),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationFailure(booking))

                val result =
                    ohipApi.updateBookerDetails(
                        request =
                            UpdateBookerDetailsRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                booker = requestBooker(companyName = null),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateBookerDetailsFlagPins,
                    )

                result.attachEvidence("Update Booker Details Detach Rejected")

                expect("returns the mapped change-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 958
                }

                expect("applied the booker amend then attempted the detach PUT once") {
                    // One GET /rsv/.../reservations/{id}; one GET and one already-applied
                    // PUT /crm/v1/profiles/P-6701101; then the rejected
                    // PUT /rsv/.../reservations/{id} whose Internal Server Error envelope is
                    // outside the retry predicate.
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a retryable Opera rejection of the detach PUT exhausts the retries") {
                val booking =
                    bookerDetailsBooking(
                        reservationIds = listOf("6701201"),
                        attachedCompanyProfileId = Companies.NEILL_TECHNICAL_SERVICES.companyId,
                        companies = listOf(Companies.NEILL_TECHNICAL_SERVICES),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationBadRequest(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.updateBookerDetails(
                        request =
                            UpdateBookerDetailsRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                booker = requestBooker(companyName = null),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateBookerDetailsFlagPins,
                    )

                result.attachEvidence("Update Booker Details Detach Retry Exhausted")

                expect("returns the mapped exhausted-retries error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 971
                }

                expect("applied the booker amend then attempted the detach PUT four times") {
                    // One GET /rsv/.../reservations/{id}; one GET and one
                    // PUT /crm/v1/profiles/P-6701201; then the rejected
                    // PUT /rsv/.../reservations/{id} once plus the adapter's three retries of
                    // the retryable Bad Request body (~20-30s of backoff by design).
                    callCount(Upstream.OPERA) shouldBe 7
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 4
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an empty company profile read on the rename branch trips the null-profile guard") {
                val company = Companies.NEILL_TECHNICAL_SERVICES
                val booking =
                    bookerDetailsBooking(
                        reservationIds = listOf("6701301"),
                        attachedCompanyProfileId = company.companyId,
                        companies = listOf(company),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_COMPANY_PROFILE_BY_ID_STUB_ID))
                installStub(companyProfileByIdEmptyBody(company))

                val result =
                    ohipApi.updateBookerDetails(
                        request =
                            UpdateBookerDetailsRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                booker = requestBooker(companyName = "Neill Technical Services Group"),
                            ),
                        testId = testId,
                        featureFlagOverrides = updateBookerDetailsFlagPins,
                    )

                result.attachEvidence("Update Booker Details Company Read Empty")

                expect("returns the adapter's own get-profile rejection") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 51
                }

                expect("applied the booker amend then stopped at the empty company read") {
                    // One GET /rsv/.../reservations/{id}; one GET and one already-applied
                    // PUT /crm/v1/profiles/P-6701301; then the zero-length 200
                    // GET /crm/v1/profiles/2569623 — Opera's own answer, not an error status —
                    // so the company PUT is never sent and no reservation is written.
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private const val REQUEST_FIRST_NAME = "Priya"
private const val REQUEST_LAST_NAME = "Booker"

/**
 * The request-side booker. `booker.address` is always supplied because the booker merge
 * ModelMapper-converts it unguarded on every path that reaches the contact profile (the
 * null-address 500 is routine request validation and belongs in service tests).
 */
private fun requestBooker(companyName: String?): BookerDetailsCnp =
    BookerDetailsCnp(
        title = "Mrs",
        firstName = REQUEST_FIRST_NAME,
        lastName = REQUEST_LAST_NAME,
        mobile = "+447700900222",
        landline = "+442071234567",
        emailAddress = "priya.booker@test.com",
        companyName = companyName,
        address =
            BookerAddressCnp(
                postalCode = "SW1A 1AA",
                addressLine1 = "1 High Street",
            ),
    )

private fun bookerDetailsBooking(
    reservationIds: List<String>,
    reservationContact: List<Boolean> = List(reservationIds.size) { true },
    attachedCompanyProfileId: String? = null,
    attachedProfilesAfterUpdate: ReservationAttachedProfiles? = null,
    companies: List<Company> = emptyList(),
    booker: Booker? = null,
): Booking {
    val arrival = LocalDate.now().plusDays(5)
    val rates =
        listOf(
            Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2),
            Rate(ratePlan = "SEMIFLEX", roomType = "TWINRM", adults = 1),
        ).take(reservationIds.size)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = rates)),
        arrival = arrival,
        departure = arrival.plusDays(2),
        companies = companies,
        booker = booker,
        rooms =
            reservationIds.mapIndexed { index, reservationId ->
                BookingRoom(
                    reservationId = reservationId,
                    roomType = rates[index].roomType,
                    adults = rates[index].adults,
                    status = ReservationStatus.RESERVED,
                    guestProfile =
                        GuestProfile(
                            profileId = "P-$reservationId",
                            firstName = "Existing",
                            lastName = "Contact",
                            email = "existing.contact@test.com",
                            phone = "+447700900100",
                            addressLine = "22 Old Lane",
                            city = "London",
                            postcode = "N1 9GU",
                            reservationContact = reservationContact[index],
                        ),
                    attachedCompanyProfileId = attachedCompanyProfileId,
                    attachedProfilesAfterUpdate = attachedProfilesAfterUpdate,
                )
            },
    )
}
