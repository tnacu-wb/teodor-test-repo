package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.BillingAddressBooker
import uk.co.whitbread.integrationtests.clients.ohip.model.BillingAddressBookerAddress
import uk.co.whitbread.integrationtests.clients.ohip.model.UpdateBillingAddressRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_PROFILE_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_PROFILE_UPDATE_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.externalReservationProfileFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationEmpty
import uk.co.whitbread.integrationtests.stubs.opera.custom.updateProfileBadRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.updateProfileFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.BillingAddress
import uk.co.whitbread.integrationtests.testkit.model.Booker
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Companies
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

// release_bb_capture_billing_address is this endpoint's only real ON/OFF axis, evaluated twice
// on the request thread through the override-aware Unleash wrapper; the two token-service flags
// are the fixed-false invariants. Every scenario passes one of these complete maps.
private val billingAddressFlagOffPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.CAPTURE_BILLING_ADDRESS_BB to false,
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
        OhipFeatureFlag.USE_TOKEN_REFRESH_SKEW to false,
    )

private val billingAddressFlagOnPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.CAPTURE_BILLING_ADDRESS_BB to true,
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
        OhipFeatureFlag.USE_TOKEN_REFRESH_SKEW to false,
    )

/**
 * Proves `PUT /ohip/v1/reservation/updateBillingAddress`: one Opera reservation read per
 * distinct id, then sequential CRM profile GET/PUT pairs for the profiles selected by the
 * request indicators (flag on and channel BB), by `booker.address.addressType` (flag off,
 * channel not BB, or the `ACCOUNT_COMPANY` CCUI variant) — plus the flag's channel-independent
 * BILLING/primary update body, the empty-read skip, and the read/profile-read/profile-update
 * failure mappings including the profile-update retry exhaustion. No reservation is written.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/UpdateBillingAddress.md
 */
class UpdateBillingAddressSpec :
    JourneySpec(
        "OHIP adapter writes the booker billing address onto reservation profiles",
        {
            val ohipApi = OhipApi()
            scenario("flag off: a duplicated-id two-reservation request writes exactly one contact profile") {
                val booking = billingAddressBooking(reservationIds = listOf("6006511", "6006512"))
                val r1 = requireNotNull(booking.rooms[0].reservationId)
                val r2 = requireNotNull(booking.rooms[1].reservationId)

                installFor(booking)

                val result =
                    ohipApi.updateBillingAddress(
                        request =
                            UpdateBillingAddressRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(r1, r2, r1),
                                booker = billingBooker(addressType = "HOME"),
                                channel = "BB",
                            ),
                        testId = testId,
                        featureFlagOverrides = billingAddressFlagOffPins,
                    )

                result.attachEvidence("Update Billing Address Flag Off Dedup")

                expect("returns 204 with an empty body") {
                    result.response.status.value shouldBe 204
                }

                expect("reads each distinct reservation once and writes exactly one contact profile") {
                    // Two GET /rsv/v1/hotels/{hotelId}/reservations/{id} (the HashSet dedups the
                    // repeated id), then ONE GET plus ONE PUT /crm/v1/profiles/{id} for the one
                    // Set-collapsed contact pick — which of the two contact ids is written is
                    // unspecified, so assert counts only, never the chosen URL. Channel BB alone
                    // (flag off) never reaches the indicator branch.
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.GET_COMPANY) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
            scenario("flag on and channel BB: the contact indicator alone selects the contact profile") {
                val booking = billingAddressBooking(reservationIds = listOf("6006511"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.updateBillingAddress(
                        request =
                            UpdateBillingAddressRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                // Deliberately non-BUSINESS: the indicator, not the address
                                // type, must drive the selection.
                                booker = billingBooker(addressType = "HOME"),
                                channel = "BB",
                                updateContactProfile = true,
                            ),
                        testId = testId,
                        featureFlagOverrides = billingAddressFlagOnPins,
                    )

                result.attachEvidence("Update Billing Address Indicator Contact")

                expect("returns 204 with an empty body") {
                    result.response.status.value shouldBe 204
                }

                expect("reads the reservation and writes exactly the contact profile") {
                    // One GET /rsv/.../reservations/{id}, then one GET plus one PUT
                    // /crm/v1/profiles/{contactId} selected by the indicator branch.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.GET_COMPANY) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
            scenario("flag on and channel BB with all indicators false: no profile is touched") {
                val booking = billingAddressBooking(reservationIds = listOf("6006511"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.updateBillingAddress(
                        request =
                            UpdateBillingAddressRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                booker = billingBooker(addressType = "HOME"),
                                channel = "BB",
                            ),
                        testId = testId,
                        featureFlagOverrides = billingAddressFlagOnPins,
                    )

                result.attachEvidence("Update Billing Address Indicators All False")

                expect("returns 204 with an empty body on the skip path") {
                    result.response.status.value shouldBe 204
                }

                expect("read the reservation once and provably touched no profile") {
                    // One GET /rsv/.../reservations/{id}; the profile read and update
                    // capabilities are installed by the guestProfile fact, so their zero counts
                    // prove the false indicators skip every profile role — even though a
                    // ReservationContact profile the address-type branch would write is present.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
            scenario("flag on but channel not BB: the address-type path writes the contact profile anyway") {
                val booking = billingAddressBooking(reservationIds = listOf("6006511"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.updateBillingAddress(
                        request =
                            UpdateBillingAddressRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                booker = billingBooker(addressType = "HOME"),
                                channel = "WEB",
                            ),
                        testId = testId,
                        featureFlagOverrides = billingAddressFlagOnPins,
                    )

                result.attachEvidence("Update Billing Address Flag On Non BB")

                expect("returns 204 with an empty body") {
                    result.response.status.value shouldBe 204
                }

                expect("writes the contact profile despite every indicator being false") {
                    // One GET /rsv/.../reservations/{id}, then one GET plus one PUT
                    // /crm/v1/profiles/{contactId}: the channel==BB conjunct is load-bearing,
                    // so a non-BB channel ignores the indicators and runs the unconditional
                    // contact update — the exact counter-case to the all-false-indicators skip.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
            scenario("paymentOption ACCOUNT_COMPANY routes to the CCUI address-type variant") {
                val booking = billingAddressBooking(reservationIds = listOf("6006511"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.updateBillingAddress(
                        request =
                            UpdateBillingAddressRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                booker = billingBooker(addressType = "HOME"),
                                paymentOption = "ACCOUNT_COMPANY",
                                channel = "BB",
                            ),
                        testId = testId,
                        featureFlagOverrides = billingAddressFlagOnPins,
                    )

                result.attachEvidence("Update Billing Address Account Company CCUI")

                expect("returns 204 with an empty body") {
                    result.response.status.value shouldBe 204
                }

                expect("writes the contact profile through the CCUI variant's address-type selection") {
                    // One GET /rsv/.../reservations/{id}, then one GET plus one PUT
                    // /crm/v1/profiles/{contactId}: the in-port's paymentOption rule is
                    // evaluated before the flag, so flag-on plus channel BB plus all-false
                    // indicators — which would write nothing — is overridden by ACCOUNT_COMPANY.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.GET_COMPANY) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
            // Documented service bug: reading the attached company profile — which, like any real Opera
            // profile with no email on file, carries no profileDetails.emails block — crashes
            // the adapter in BookerProfileOhipMapper.injectEmail (unguarded getEmails()),
            // producing an unmapped HTTP 500 instead of the designed 204 with three profile
            // GET/PUT pairs. See bug/update-billing-address-company-profile-no-email-npe.md.
            // Re-enable when fixed.
            scenario("!flag off with a BUSINESS address: guest, company and contact profiles are all written") {
                val booking = billingAddressBooking(reservationIds = listOf("6006511"), withCompany = true)
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.updateBillingAddress(
                        request =
                            UpdateBillingAddressRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                booker = billingBooker(addressType = "BUSINESS"),
                                channel = "BB",
                            ),
                        testId = testId,
                        featureFlagOverrides = billingAddressFlagOffPins,
                    )

                result.attachEvidence("Update Billing Address Business All Roles")

                expect("returns 204 with an empty body") {
                    result.response.status.value shouldBe 204
                }

                expect("writes the guest, company and contact profiles as three GET/PUT pairs") {
                    // One GET /rsv/.../reservations/{id}, then three sequential
                    // GET+PUT /crm/v1/profiles/{id} pairs — guest, company profile id, and the
                    // unconditional contact (the guest's id again): two distinct URLs, three
                    // calls each way. Order is not asserted, only counts. No
                    // /crm/v1/profiles search and no /crm/v1/companies read on this route.
                    callCount(Upstream.OPERA) shouldBe 7
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 3
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 3
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.GET_PROFILES) shouldBe 0
                    callCount(OperaEndpoint.GET_COMPANY) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
            // Documented service bug: same company-profile email-block NPE as the disabled scenario above, reached through
            // the indicator branch (updateCompanyProfile=true).
            // See bug/update-billing-address-company-profile-no-email-npe.md. Re-enable when
            // fixed.
            scenario("!flag on and channel BB: guest and company indicators leave the contact untouched") {
                val booking = billingAddressBooking(reservationIds = listOf("6006511"), withCompany = true)
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.updateBillingAddress(
                        request =
                            UpdateBillingAddressRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                booker = billingBooker(addressType = "HOME"),
                                channel = "BB",
                                updateGuestProfile = true,
                                updateCompanyProfile = true,
                            ),
                        testId = testId,
                        featureFlagOverrides = billingAddressFlagOnPins,
                    )

                result.attachEvidence("Update Billing Address Indicator Guest Company")

                expect("returns 204 with an empty body") {
                    result.response.status.value shouldBe 204
                }

                expect("writes the guest and company profiles and skips the contact pass") {
                    // One GET /rsv/.../reservations/{id}, then two GET+PUT
                    // /crm/v1/profiles/{id} pairs (guest and company profile ids). The false
                    // contact indicator skips the third pair — the only path where the contact
                    // is NOT written while other roles are, proving the indicator branch
                    // addresses each profile role independently.
                    callCount(Upstream.OPERA) shouldBe 5
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 0
                    callCount(OperaEndpoint.GET_COMPANY) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("flag on: the profile update body is a BILLING primary address") {
                val billingAddress =
                    BillingAddress(
                        addressLines = listOf("12 Billing Way", "Billing Park"),
                        cityName = "Leeds",
                        postalCode = "LS1 4AP",
                        countryCode = "GB",
                        primaryAddress = true,
                    )
                val booking =
                    billingAddressBooking(
                        reservationIds = listOf("6006511"),
                        billingAddress = billingAddress,
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.updateBillingAddress(
                        request =
                            UpdateBillingAddressRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                booker =
                                    BillingAddressBooker(
                                        firstName = "Priya",
                                        lastName = "Booker",
                                        // addressLine4 is deliberately unset and cityName
                                        // stated, keeping the mapper's written line set
                                        // deterministic (the addressLine4 city-name drop
                                        // rule never engages).
                                        address =
                                            BillingAddressBookerAddress(
                                                addressType = "HOME",
                                                addressLine1 = billingAddress.addressLines[0],
                                                addressLine2 = billingAddress.addressLines[1],
                                                cityName = billingAddress.cityName,
                                                postalCode = billingAddress.postalCode,
                                                countryCode = billingAddress.countryCode,
                                            ),
                                    ),
                                // Non-BB channel: the flag's selection effect is off, so only
                                // its channel-independent body effect (primaryInd) is under test.
                                channel = "WEB",
                            ),
                        testId = testId,
                        featureFlagOverrides = billingAddressFlagOnPins,
                    )

                result.attachEvidence("Update Billing Address Pinned BILLING Body")

                expect("returns 204 with an empty body") {
                    result.response.status.value shouldBe 204
                }

                expect("writes the contact profile through the body-pinned BILLING/primary mapping only") {
                    // One GET /rsv/.../reservations/{id}, one GET /crm/v1/profiles/{id}, then
                    // one PUT /crm/v1/profiles/{id} matched ONLY by the Booker.billingAddress
                    // body pin: addressInfo[0] with no type/id member, address.type BILLING,
                    // exactly the stated lines, cityName, country.value, and primaryInd true —
                    // the permissive mapping is omitted for a pinned Booking, so a wrong body
                    // cannot fall through.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
            scenario("a rejected reservation read maps to the get-reservation error before any profile call") {
                val booking = billingAddressBooking(reservationIds = listOf("6006511"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationBadRequest(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.updateBillingAddress(
                        request =
                            UpdateBillingAddressRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                booker = billingBooker(addressType = "HOME"),
                                channel = "BB",
                            ),
                        testId = testId,
                        featureFlagOverrides = billingAddressFlagOffPins,
                    )

                result.attachEvidence("Update Billing Address Read Rejected")

                expect("returns the mapped get-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 960
                }

                expect("attempted only the single unretried reservation read") {
                    // One rejected GET /rsv/.../reservations/{id}; the read carries no retry
                    // spec, and the installed profile read/update capabilities stay provably
                    // untouched.
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
            scenario("an empty reservation read returns 204 with no profile work") {
                val booking = billingAddressBooking(reservationIds = listOf("6006511"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationEmpty(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.updateBillingAddress(
                        request =
                            UpdateBillingAddressRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                booker = billingBooker(addressType = "HOME"),
                                channel = "BB",
                            ),
                        testId = testId,
                        featureFlagOverrides = billingAddressFlagOffPins,
                    )

                result.attachEvidence("Update Billing Address Empty Read")

                expect("returns 204 with an empty body on the early-return branch") {
                    result.response.status.value shouldBe 204
                }

                expect("read the reservation once and skipped profile discovery entirely") {
                    // One empty-bodied 200 GET /rsv/.../reservations/{id} emits no reservation,
                    // so the collected list is empty and the method returns before any profile
                    // work; the installed profile capabilities' zero counts prove the absence.
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
            scenario("a rejected profile read maps to the get-profiles error before any update") {
                val booking = billingAddressBooking(reservationIds = listOf("6006511"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_PROFILE_GET_STUB_ID))
                installStub(externalReservationProfileFailure(booking.room))

                val result =
                    ohipApi.updateBillingAddress(
                        request =
                            UpdateBillingAddressRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                booker = billingBooker(addressType = "HOME"),
                                channel = "BB",
                            ),
                        testId = testId,
                        featureFlagOverrides = billingAddressFlagOffPins,
                    )

                result.attachEvidence("Update Billing Address Profile Read Rejected")

                expect("returns the mapped get-profiles error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 912
                }

                expect("read the reservation then attempted only the unretried profile read") {
                    // One GET /rsv/.../reservations/{id}, then one rejected
                    // GET /crm/v1/profiles/{contactId} with no retry; the leg blocks, so the
                    // installed update capability stays provably untouched.
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
            scenario("a non-retryable profile-update rejection maps to the update-profile error") {
                val booking = billingAddressBooking(reservationIds = listOf("6006511"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_PROFILE_UPDATE_STUB_ID))
                installStub(updateProfileFailure(booking))

                val result =
                    ohipApi.updateBillingAddress(
                        request =
                            UpdateBillingAddressRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                booker = billingBooker(addressType = "HOME"),
                                channel = "BB",
                            ),
                        testId = testId,
                        featureFlagOverrides = billingAddressFlagOffPins,
                    )

                result.attachEvidence("Update Billing Address Update Rejected")

                expect("returns the mapped update-profile error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 961
                }

                expect("attempted the profile update exactly once with no retry") {
                    // One GET /rsv/.../reservations/{id}, one GET /crm/v1/profiles/{id}, then
                    // the rejected PUT /crm/v1/profiles/{id} — its Internal Server Error
                    // envelope is outside the retry predicate, so a single attempt.
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
            scenario("a retryable profile-update rejection exhausts the retries") {
                val booking = billingAddressBooking(reservationIds = listOf("6006511"))
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_PROFILE_UPDATE_STUB_ID))
                installStub(updateProfileBadRequest(booking))

                val result =
                    ohipApi.updateBillingAddress(
                        request =
                            UpdateBillingAddressRequest(
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                booker = billingBooker(addressType = "HOME"),
                                channel = "BB",
                            ),
                        testId = testId,
                        featureFlagOverrides = billingAddressFlagOffPins,
                    )

                result.attachEvidence("Update Billing Address Update Retry Exhausted")

                expect("returns the mapped exhausted-retries error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 971
                }

                expect("attempted the profile update four times before exhausting") {
                    // One GET /rsv/.../reservations/{id}, one unrepeated GET
                    // /crm/v1/profiles/{id}, then the rejected PUT /crm/v1/profiles/{id} once
                    // plus the adapter's three retries of the Bad Request-typed body
                    // (Retry.backoff(3, 3s)).
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
        },
    )

/** Booker with the fixed non-pinned journey address; only its addressType varies per scenario. */
private fun billingBooker(addressType: String): BillingAddressBooker =
    BillingAddressBooker(
        firstName = "Priya",
        lastName = "Booker",
        address =
            BillingAddressBookerAddress(
                addressType = addressType,
                addressLine1 = "1 High Street",
                cityName = "London",
                postalCode = "SW1A 1AA",
            ),
    )

private fun billingAddressBooking(
    reservationIds: List<String>,
    withCompany: Boolean = false,
    billingAddress: BillingAddress? = null,
): Booking {
    val arrival = LocalDate.now().plusDays(14)
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)
    val company = Companies.NEILL_TECHNICAL_SERVICES

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        companies = if (withCompany) listOf(company) else emptyList(),
        arrival = arrival,
        departure = arrival.plusDays(2),
        booker =
            billingAddress?.let {
                Booker(firstName = "Priya", lastName = "Booker", billingAddress = it)
            },
        rooms =
            reservationIds.map { reservationId ->
                BookingRoom(
                    reservationId = reservationId,
                    roomType = rate.roomType,
                    adults = rate.adults,
                    status = ReservationStatus.RESERVED,
                    // Each room's own guest profile is also the reservation's contact, which is
                    // the profile role the unconditional address-type contact update selects.
                    guestProfile =
                        GuestProfile(
                            profileId = "PROF-${reservationId.takeLast(4)}",
                            firstName = "Priya",
                            lastName = "Booker",
                            email = "priya.booker@test.com",
                            phone = "+447700900200",
                            addressLine = "1 High Street",
                            city = "London",
                            postcode = "SW1A 1AA",
                            reservationContact = true,
                        ),
                    attachedCompanyProfileId = company.companyId.takeIf { withCompany },
                )
            },
    )
}
