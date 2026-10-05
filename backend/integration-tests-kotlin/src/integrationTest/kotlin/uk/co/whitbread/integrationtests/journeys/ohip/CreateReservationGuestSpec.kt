package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.CreateReservationGuestRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.ReservationAccompanyingGuestDetails
import uk.co.whitbread.integrationtests.clients.ohip.model.ReservationGuestBooker
import uk.co.whitbread.integrationtests.clients.ohip.model.ReservationGuestBookerAddress
import uk.co.whitbread.integrationtests.clients.ohip.model.ReservationStayingGuest
import uk.co.whitbread.integrationtests.clients.ohip.model.ReservationStayingGuestDetails
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_PROFILE_CREATE_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_PROFILE_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_PROFILE_UPDATE_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_PUT_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.createProfileFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.externalReservationProfileFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.getProfileEmptyBody
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.putReservationBadRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.putReservationEmptyBody
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
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Companies
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

// Both flow-listed flags are the fixed-false token-service invariants: USE_TOKEN_SERVICE is
// environment-pinned OFF and USE_TOKEN_REFRESH_SKEW is evaluated once at client construction.
private val createReservationGuestFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
        OhipFeatureFlag.USE_TOKEN_REFRESH_SKEW to false,
    )

/**
 * Proves `POST /ohip/v1/reservations/guests`: two mutually exclusive orchestrations selected by
 * `preCheckIn`. The default path optionally creates a Company profile, resolves the booker
 * language (reading the reservation and the real rules-agent when it is missing), creates or
 * reuses the booker and per-guest CRM profiles, and fans one unordered reservation update out per
 * staying-guest entry — with the empty-profile-read skip, the empty-update-acknowledgement
 * response, and the reservation-read, profile-read, profile-create, profile-update,
 * reservation-update, and retry-exhaustion failure mappings. The pre-check-in path reads every
 * distinct reservation, validates the group against Opera's guest counts, creates or updates one
 * Guest profile per flagged entry, and closes with one sequential reservation update per group.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/CreateReservationGuest.md
 */
class CreateReservationGuestSpec :
    JourneySpec(
        "OHIP adapter attaches guest, booker, and company identities to reservations",
        {
            val ohipApi = OhipApi()

            scenario("a company name and a null-sameAsBooker guest create two profiles and one update") {
                val company = Companies.NEILL_TECHNICAL_SERVICES
                val booking =
                    reservationGuestBooking(
                        reservationId = "6301101",
                        companies = listOf(company),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.createReservationGuest(
                        request =
                            CreateReservationGuestRequest(
                                hotelId = booking.hotel.hotelId,
                                reasonForStay = "LEI",
                                booker =
                                    requestBooker(
                                        address =
                                            ReservationGuestBookerAddress(
                                                addressLine1 = company.address.addressLine1,
                                                postalCode = company.address.postalCode,
                                                countryCode = company.address.country,
                                                companyName = company.name,
                                            ),
                                    ),
                                stayingGuests =
                                    listOf(
                                        ReservationStayingGuest(
                                            reservationId = reservationId,
                                            stayingGuestDetails = requestGuestDetails(),
                                        ),
                                    ),
                            ),
                        testId = testId,
                        featureFlagOverrides = createReservationGuestFlagPins,
                    )

                result.attachEvidence("Create Reservation Guest With Company")

                expect("returns 201 with the hotel id and the request reservation id") {
                    result.response.status.value shouldBe 201
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.reservationIds shouldBe listOf(reservationId)
                }

                expect("creates the Company then Contact profiles and updates the reservation once") {
                    // Two POST /crm/v1/profiles (the pinned Company body then the pinned booker
                    // Contact body) and one PUT /rsv/.../reservations/{id}; a supplied language
                    // and a null sameAsBooker touch no reservation or profile read at all.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.GET_PROFILES) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("two entries on one reservation with an accompanying guest send two updates") {
                val booking = reservationGuestBooking(reservationId = "6301102", withBooker = false)
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.createReservationGuest(
                        request =
                            CreateReservationGuestRequest(
                                hotelId = booking.hotel.hotelId,
                                reasonForStay = "LEI",
                                booker = requestBooker(),
                                stayingGuests =
                                    listOf(
                                        ReservationStayingGuest(
                                            reservationId = reservationId,
                                            stayingGuestDetails = requestGuestDetails(),
                                        ),
                                        ReservationStayingGuest(
                                            reservationId = reservationId,
                                            stayingGuestDetails = requestGuestDetails(),
                                            accompanyingGuestDetails =
                                                ReservationAccompanyingGuestDetails(
                                                    firstName = "Alex",
                                                    lastName = "Companion",
                                                ),
                                        ),
                                    ),
                            ),
                        testId = testId,
                        featureFlagOverrides = createReservationGuestFlagPins,
                    )

                result.attachEvidence("Create Reservation Guest Duplicated Reservation")

                expect("returns 201 with one reservation id per staying-guest entry") {
                    result.response.status.value shouldBe 201
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.reservationIds shouldBe listOf(reservationId, reservationId)
                }

                expect("creates the booker and accompanying profiles and updates the reservation twice") {
                    // Two POST /crm/v1/profiles (booker Contact plus the accompanying Guest,
                    // both served by the untyped create mapping — the Booking states no booker)
                    // and two PUT /rsv/.../reservations/{id} on the same URL: List semantics
                    // send one update per entry, unordered, so only counts are asserted.
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a sameAsBooker guest reuses the reservation's temporary profile as the booker") {
                val booking = reservationGuestBooking(reservationId = "6301103")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.createReservationGuest(
                        request = singleGuestRequest(booking.hotel.hotelId, reservationId, sameAsBooker = true),
                        testId = testId,
                        featureFlagOverrides = createReservationGuestFlagPins,
                    )

                result.attachEvidence("Create Reservation Guest Same As Booker")

                expect("returns 201 with the request reservation id") {
                    result.response.status.value shouldBe 201
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.reservationIds shouldBe listOf(reservationId)
                }

                expect("updates the existing temporary profile in place and never creates one") {
                    // One GET /rsv/.../reservations/{id} (ten-instruction variant), one
                    // GET and one PUT /crm/v1/profiles/P-6300, then one reservation PUT; the
                    // booker-create leg is provably skipped with the pinned Contact create
                    // capability installed.
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an explicitly non-booker guest reuses its own temporary profile as a Guest") {
                val booking = reservationGuestBooking(reservationId = "6301104")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.createReservationGuest(
                        request =
                            CreateReservationGuestRequest(
                                hotelId = booking.hotel.hotelId,
                                reasonForStay = "LEI",
                                booker = requestBooker(),
                                stayingGuests =
                                    listOf(
                                        ReservationStayingGuest(
                                            reservationId = reservationId,
                                            sameAsBooker = false,
                                            stayingGuestDetails = requestGuestDetails(),
                                        ),
                                    ),
                            ),
                        testId = testId,
                        featureFlagOverrides = createReservationGuestFlagPins,
                    )

                result.attachEvidence("Create Reservation Guest Same As Booker False")

                expect("returns 201 with the request reservation id") {
                    result.response.status.value shouldBe 201
                    result.body.reservationIds shouldBe listOf(reservationId)
                }

                expect("creates the booker Contact and updates the guest's own temporary profile") {
                    // One pinned Contact POST /crm/v1/profiles for the booker, then the guest
                    // leg: one GET /rsv/.../reservations/{id}, one GET and one Guest-bodied
                    // PUT /crm/v1/profiles/P-6300, then one reservation PUT — the mirror image
                    // of the sameAsBooker-true counts.
                    callCount(Upstream.OPERA) shouldBe 5
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a missing booker language is resolved from the reservation source and rules-agent") {
                val booking = reservationGuestBooking(reservationId = "6301105", withBooker = false)
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.createReservationGuest(
                        request =
                            CreateReservationGuestRequest(
                                hotelId = booking.hotel.hotelId,
                                reasonForStay = "LEI",
                                booker = requestBooker(language = null),
                                stayingGuests =
                                    listOf(
                                        ReservationStayingGuest(
                                            reservationId = reservationId,
                                            stayingGuestDetails = requestGuestDetails(),
                                        ),
                                    ),
                            ),
                        testId = testId,
                        featureFlagOverrides = createReservationGuestFlagPins,
                    )

                result.attachEvidence("Create Reservation Guest Missing Language")

                expect("returns 201 with the request reservation id") {
                    result.response.status.value shouldBe 201
                    result.body.reservationIds shouldBe listOf(reservationId)
                }

                expect("adds exactly the language-source reservation read before create and update") {
                    // One GET /rsv/.../reservations/{id} whose roomRates[0].sourceCode 44 feeds
                    // the real rules-agent GET /v1/rules/source-info?sourceId=44 (a real
                    // collaborator that is never stubbed or counted), then one untyped
                    // POST /crm/v1/profiles and one reservation PUT.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an empty temporary-profile read skips the profile update and still reuses the id") {
                val booking = reservationGuestBooking(reservationId = "6301106")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_PROFILE_GET_STUB_ID))
                installStub(getProfileEmptyBody(booking.room))

                val result =
                    ohipApi.createReservationGuest(
                        request = singleGuestRequest(booking.hotel.hotelId, reservationId, sameAsBooker = true),
                        testId = testId,
                        featureFlagOverrides = createReservationGuestFlagPins,
                    )

                result.attachEvidence("Create Reservation Guest Empty Profile Read")

                expect("returns 201 with the request reservation id") {
                    result.response.status.value shouldBe 201
                    result.body.reservationIds shouldBe listOf(reservationId)
                }

                expect("read the profile once, wrote no profile, and still updated the reservation") {
                    // One GET /rsv/.../reservations/{id}, one zero-length 200
                    // GET /crm/v1/profiles/P-6300 (the read-returned-no-profile guard), then
                    // one reservation PUT; the profile update is provably skipped with its
                    // capability installed.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an empty update acknowledgement still answers 201 with no ids") {
                val booking = reservationGuestBooking(reservationId = "6301107")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationEmptyBody(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.createReservationGuest(
                        request = singleGuestRequest(booking.hotel.hotelId, reservationId),
                        testId = testId,
                        featureFlagOverrides = createReservationGuestFlagPins,
                    )

                result.attachEvidence("Create Reservation Guest Empty Update Acknowledgement")

                expect("returns 201 with a null hotel id and null reservation ids") {
                    result.response.status.value shouldBe 201
                    result.body.hotelId shouldBe null
                    result.body.reservationIds shouldBe null
                }

                expect("created the booker profile and sent the one acknowledged update") {
                    // One pinned Contact POST /crm/v1/profiles and one empty-bodied 200
                    // PUT /rsv/.../reservations/{id} — the degenerate response mapping is
                    // driven purely by Opera's answer.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected reservation update maps to the guest-update error with no rollback") {
                val booking = reservationGuestBooking(reservationId = "6301108")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationFailure(booking))

                val result =
                    ohipApi.createReservationGuest(
                        request = singleGuestRequest(booking.hotel.hotelId, reservationId),
                        testId = testId,
                        featureFlagOverrides = createReservationGuestFlagPins,
                    )

                result.attachEvidence("Create Reservation Guest Update Rejected")

                expect("returns the mapped put-reservations-guest error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 952
                }

                expect("kept the created booker profile and attempted the update once") {
                    // One pinned Contact POST /crm/v1/profiles that stands after the failure
                    // (the documented no-rollback boundary), then the rejected
                    // PUT /rsv/.../reservations/{id} whose Internal Server Error envelope is
                    // outside the retry predicate.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a retryable reservation-update rejection exhausts the retries") {
                val booking = reservationGuestBooking(reservationId = "6301109")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationBadRequest(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.createReservationGuest(
                        request = singleGuestRequest(booking.hotel.hotelId, reservationId),
                        testId = testId,
                        featureFlagOverrides = createReservationGuestFlagPins,
                    )

                result.attachEvidence("Create Reservation Guest Update Retry Exhausted")

                expect("returns the mapped exhausted-retries error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 971
                }

                expect("attempted the update four times after the one profile create") {
                    // One pinned Contact POST /crm/v1/profiles, then the rejected
                    // PUT /rsv/.../reservations/{id} once plus the adapter's three retries of
                    // the retryable Bad Request body (~20-30s of backoff before the 971 maps).
                    callCount(Upstream.OPERA) shouldBe 5
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 4
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected booker profile create stops the flow before any update") {
                val booking = reservationGuestBooking(reservationId = "6301110")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_PROFILE_CREATE_STUB_ID))
                installStub(createProfileFailure(booking))

                val result =
                    ohipApi.createReservationGuest(
                        request = singleGuestRequest(booking.hotel.hotelId, reservationId),
                        testId = testId,
                        featureFlagOverrides = createReservationGuestFlagPins,
                    )

                result.attachEvidence("Create Reservation Guest Profile Create Rejected")

                expect("returns the mapped post-profile error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 953
                }

                expect("attempted only the single unretried profile create") {
                    // One rejected POST /crm/v1/profiles on exactly the pinned Contact body the
                    // default would have accepted; no retry and no reservation write follows.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected reservation read on the sameAsBooker leg maps to the read error") {
                val booking = reservationGuestBooking(reservationId = "6301111")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationBadRequest(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.createReservationGuest(
                        request = singleGuestRequest(booking.hotel.hotelId, reservationId, sameAsBooker = true),
                        testId = testId,
                        featureFlagOverrides = createReservationGuestFlagPins,
                    )

                result.attachEvidence("Create Reservation Guest Read Rejected")

                expect("returns the mapped get-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 960
                }

                expect("attempted only the single unretried reservation read") {
                    // One rejected GET /rsv/.../reservations/{id}; the read has no status retry
                    // and every write capability stays provably untouched.
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

            scenario("a rejected temporary-profile read maps to the get-profiles error") {
                val booking = reservationGuestBooking(reservationId = "6301112")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_PROFILE_GET_STUB_ID))
                installStub(externalReservationProfileFailure(booking.room))

                val result =
                    ohipApi.createReservationGuest(
                        request = singleGuestRequest(booking.hotel.hotelId, reservationId, sameAsBooker = true),
                        testId = testId,
                        featureFlagOverrides = createReservationGuestFlagPins,
                    )

                result.attachEvidence("Create Reservation Guest Profile Read Rejected")

                expect("returns the mapped get-profiles error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 912
                }

                expect("read the reservation then attempted only the profile read") {
                    // One GET /rsv/.../reservations/{id} then one rejected
                    // GET /crm/v1/profiles/P-6300 — the profile-read mapping is distinct from
                    // the reservation-read mapping; no write follows.
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

            scenario("a rejected in-place profile update maps to the update-profile error") {
                val booking = reservationGuestBooking(reservationId = "6301113")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_PROFILE_UPDATE_STUB_ID))
                installStub(updateProfileFailure(booking))

                val result =
                    ohipApi.createReservationGuest(
                        request = singleGuestRequest(booking.hotel.hotelId, reservationId, sameAsBooker = true),
                        testId = testId,
                        featureFlagOverrides = createReservationGuestFlagPins,
                    )

                result.attachEvidence("Create Reservation Guest Profile Update Rejected")

                expect("returns the mapped update-profile error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 961
                }

                expect("attempted the profile update once and sent no reservation update") {
                    // One GET /rsv/.../reservations/{id}, one GET /crm/v1/profiles/P-6300, then
                    // the rejected PUT /crm/v1/profiles/P-6300 — its Internal Server Error
                    // envelope is outside the retry predicate, so no retry (the 971 mapping
                    // stays out of this row) and no reservation PUT.
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

            scenario("a pre-check-in group creates both flagged guests and skips the null entry") {
                val booking = reservationGuestBooking(reservationId = "6301114", withBooker = false)
                val reservationId = requireNotNull(booking.room.reservationId)
                val leadProfileId = requireNotNull(booking.room.guestProfile).profileId

                installFor(booking)

                val result =
                    ohipApi.createReservationGuest(
                        request =
                            CreateReservationGuestRequest(
                                hotelId = booking.hotel.hotelId,
                                reasonForStay = "LEI",
                                booker = requestBooker(),
                                preCheckIn = true,
                                stayingGuests =
                                    listOf(
                                        ReservationStayingGuest(
                                            reservationId = reservationId,
                                            isAccompanyingGuest = false,
                                            stayingGuestDetails = requestGuestDetails(profileId = leadProfileId),
                                        ),
                                        ReservationStayingGuest(
                                            reservationId = reservationId,
                                            isAccompanyingGuest = true,
                                            stayingGuestDetails =
                                                ReservationStayingGuestDetails(
                                                    firstName = "Alex",
                                                    lastName = "Companion",
                                                ),
                                        ),
                                        ReservationStayingGuest(
                                            reservationId = reservationId,
                                            stayingGuestDetails =
                                                ReservationStayingGuestDetails(
                                                    firstName = "Uma",
                                                    lastName = "Undecided",
                                                ),
                                        ),
                                    ),
                            ),
                        testId = testId,
                        featureFlagOverrides = createReservationGuestFlagPins,
                    )

                result.attachEvidence("Create Reservation Guest Pre-Check-In")

                expect("returns 201 with the single grouped reservation id") {
                    result.response.status.value shouldBe 201
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.reservationIds shouldBe listOf(reservationId)
                }

                expect("reads the group once, creates both flagged profiles, updates once") {
                    // One GET /rsv/.../reservations/{id} (ten-instruction variant), two untyped
                    // POST /crm/v1/profiles (lead and accompanying Guest creates), one
                    // PUT /rsv/.../reservations/{id}. The lead's request profile id is also
                    // Opera's attached reservation profile, so it is dropped rather than read
                    // back — GET_PROFILE stays 0 with the read capability installed —
                    // and the null-flagged entry adds no profile call at all.
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.GET_PROFILES) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a pre-check-in group over Opera's guest allowance is rejected after the read") {
                val booking =
                    reservationGuestBooking(
                        reservationId = "6301115",
                        withBooker = false,
                        adults = 1,
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.createReservationGuest(
                        request =
                            CreateReservationGuestRequest(
                                hotelId = booking.hotel.hotelId,
                                reasonForStay = "LEI",
                                booker = requestBooker(),
                                preCheckIn = true,
                                stayingGuests =
                                    listOf(
                                        ReservationStayingGuest(
                                            reservationId = reservationId,
                                            isAccompanyingGuest = false,
                                            stayingGuestDetails = requestGuestDetails(),
                                        ),
                                        ReservationStayingGuest(
                                            reservationId = reservationId,
                                            isAccompanyingGuest = true,
                                            stayingGuestDetails =
                                                ReservationStayingGuestDetails(
                                                    firstName = "Alex",
                                                    lastName = "Companion",
                                                ),
                                        ),
                                    ),
                            ),
                        testId = testId,
                        featureFlagOverrides = createReservationGuestFlagPins,
                    )

                result.attachEvidence("Create Reservation Guest Pre-Check-In Rejected")

                expect("returns the guest-allowance rejection") {
                    // Opera reports guestCounts adults 1 / children 0, so zero accompanying
                    // guests are allowed — the rejection is decided by Opera-reported counts,
                    // not by the request alone.
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 952
                }

                expect("read the group once and touched no profile or update capability") {
                    // One GET /rsv/.../reservations/{id}; every write leg is provably untouched
                    // with its capability installed.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.GET_PROFILES) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a pre-check-in guest Opera already holds is read back and updated in place") {
                val accompanyingProfile =
                    GuestProfile(
                        profileId = "P-6301",
                        firstName = "Alex",
                        lastName = "Companion",
                        email = "alex.companion@test.com",
                        phone = "+447700900631",
                        addressLine = "2 High Street",
                        city = "London",
                        postcode = "SW1A 1AA",
                    )
                val booking =
                    reservationGuestBooking(
                        reservationId = "6301116",
                        withBooker = false,
                        accompanyingGuestProfile = accompanyingProfile,
                    )
                val reservationId = requireNotNull(booking.room.reservationId)
                val leadProfileId = requireNotNull(booking.room.guestProfile).profileId

                installFor(booking)

                val result =
                    ohipApi.createReservationGuest(
                        request =
                            CreateReservationGuestRequest(
                                hotelId = booking.hotel.hotelId,
                                reasonForStay = "LEI",
                                booker = requestBooker(),
                                preCheckIn = true,
                                stayingGuests =
                                    listOf(
                                        ReservationStayingGuest(
                                            reservationId = reservationId,
                                            isAccompanyingGuest = false,
                                            stayingGuestDetails = requestGuestDetails(profileId = leadProfileId),
                                        ),
                                        ReservationStayingGuest(
                                            reservationId = reservationId,
                                            isAccompanyingGuest = true,
                                            stayingGuestDetails =
                                                ReservationStayingGuestDetails(
                                                    firstName = accompanyingProfile.firstName,
                                                    lastName = accompanyingProfile.lastName,
                                                    profileId = accompanyingProfile.profileId,
                                                ),
                                        ),
                                    ),
                            ),
                        testId = testId,
                        featureFlagOverrides = createReservationGuestFlagPins,
                    )

                result.attachEvidence("Create Reservation Guest Pre-Check-In Read-Back")

                expect("returns 201 with the single grouped reservation id") {
                    result.response.status.value shouldBe 201
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.reservationIds shouldBe listOf(reservationId)
                }

                expect("reads back only the surviving profile, updates it, and creates the lead") {
                    // One GET /rsv/.../reservations/{id}; the lead's id matches Opera's
                    // attached reservation profile and is dropped (create leg: one untyped
                    // POST /crm/v1/profiles), while the accompanying id is reported only as a
                    // reservation guest, so it alone is read back
                    // (GET /crm/v1/profiles/P-6301) and updated in place
                    // (PUT /crm/v1/profiles/P-6301); one reservation PUT closes the group.
                    callCount(Upstream.OPERA) shouldBe 5
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun requestBooker(
    language: String? = "en",
    address: ReservationGuestBookerAddress? = null,
): ReservationGuestBooker =
    ReservationGuestBooker(
        firstName = "Priya",
        lastName = "Guest",
        emailAddress = "priya.guest@test.com",
        language = language,
        address = address,
    )

private fun requestGuestDetails(profileId: String? = null): ReservationStayingGuestDetails =
    ReservationStayingGuestDetails(
        firstName = "Priya",
        lastName = "Guest",
        emailAddress = "priya.guest@test.com",
        profileId = profileId,
    )

/**
 * One staying-guest entry: `sameAsBooker = true` is the temporary-profile-reuse request, while
 * the null default is the booker-create-only request.
 */
private fun singleGuestRequest(
    hotelId: String,
    reservationId: String,
    sameAsBooker: Boolean? = null,
): CreateReservationGuestRequest =
    CreateReservationGuestRequest(
        hotelId = hotelId,
        reasonForStay = "LEI",
        booker = requestBooker(),
        stayingGuests =
            listOf(
                ReservationStayingGuest(
                    reservationId = reservationId,
                    sameAsBooker = sameAsBooker,
                    stayingGuestDetails = requestGuestDetails(),
                ),
            ),
    )

private fun reservationGuestBooking(
    reservationId: String,
    companies: List<Company> = emptyList(),
    withBooker: Boolean = true,
    adults: Int = 2,
    accompanyingGuestProfile: GuestProfile? = null,
): Booking {
    val arrival = LocalDate.now().plusDays(14)
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "DOUBLE", adults = adults)

    // The guest profile doubles as the reservation's temporary/booker profile: language "E" is
    // what a request language of "en" normalizes to, and reservationContact installs the pinned
    // Contact create mapping when the Booking states a booker.
    val guestProfile =
        GuestProfile(
            profileId = "P-6300",
            firstName = "Priya",
            lastName = "Guest",
            email = "priya.guest@test.com",
            phone = "+447700900630",
            addressLine = "1 High Street",
            city = "London",
            postcode = "SW1A 1AA",
            language = "E",
            reservationContact = true,
        )

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        companies = companies,
        arrival = arrival,
        departure = arrival.plusDays(2),
        booker =
            if (withBooker) {
                Booker(
                    firstName = guestProfile.firstName,
                    lastName = guestProfile.lastName,
                    email = guestProfile.email,
                )
            } else {
                // Without a booker the create-profile builder installs its untyped any-body
                // mapping — the only mapping that serves Guest bodies without requestForHotel.
                null
            },
        rooms =
            listOf(
                BookingRoom(
                    reservationId = reservationId,
                    roomType = rate.roomType,
                    ratePlan = rate.ratePlan,
                    adults = adults,
                    status = ReservationStatus.RESERVED,
                    // The read renders roomRates[0].sourceCode from this fact; 44 is the source
                    // that the real rules-agent seed answers with a language.
                    sourceCode = "44",
                    guestProfile = guestProfile,
                    accompanyingGuestProfile = accompanyingGuestProfile,
                ),
            ),
    )
}
