package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.CreateProfilesBooker
import uk.co.whitbread.integrationtests.clients.ohip.model.CreateProfilesBookerAddress
import uk.co.whitbread.integrationtests.clients.ohip.model.CreateProfilesRequest
import uk.co.whitbread.integrationtests.clients.ohip.model.CreateProfilesStayingGuest
import uk.co.whitbread.integrationtests.clients.ohip.model.CreateProfilesStayingGuestDetails
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_PROFILE_CREATE_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.createProfileFailure
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

// Both Opera token-service paths are environment-pinned OFF for this endpoint.
private val createProfilesFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
        OhipFeatureFlag.USE_TOKEN_REFRESH_SKEW to false,
    )

/**
 * Proves POST /ohip/v1/reservations/create/profile creates the optional Company profile before
 * the Contact profile, resolves missing language through the reservation source, and maps Opera
 * profile-create failures.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/CreateProfiles.md
 */
class CreateProfilesSpec :
    JourneySpec(
        "OHIP adapter creates reservation booker and company profiles",
        {
            val ohipApi = OhipApi()

            scenario("a supplied-language booker without a company creates only a Contact profile") {
                val booking = createProfilesBooking(reservationId = "6008901")
                val profile = requireNotNull(booking.room.guestProfile)

                installFor(booking)

                val result =
                    ohipApi.createProfiles(
                        request = createProfilesRequest(booking, language = "en"),
                        testId = testId,
                        featureFlagOverrides = createProfilesFlagPins,
                    )

                result.attachEvidence("Create Contact Profile")

                expect("returns only the created booker profile id") {
                    result.response.status.value shouldBe 200
                    result.body.bookerProfileId shouldBe profile.profileId
                    result.body.companyProfileId shouldBe null
                }

                expect("creates one Contact without reading the reservation") {
                    // One Opera call: POST /crm/v1/profiles for Contact.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a supplied-language business booker creates Company and Contact profiles") {
                val company = Companies.NEILL_TECHNICAL_SERVICES
                val booking = createProfilesBooking(reservationId = "6008902", company = company)
                val profile = requireNotNull(booking.room.guestProfile)

                installFor(booking)

                val result =
                    ohipApi.createProfiles(
                        request = createProfilesRequest(booking, language = "en", company = company),
                        testId = testId,
                        featureFlagOverrides = createProfilesFlagPins,
                    )

                result.attachEvidence("Create Company And Contact Profiles")

                expect("returns the independent Company and Contact profile ids") {
                    result.response.status.value shouldBe 200
                    result.body.companyProfileId shouldBe company.companyId
                    result.body.bookerProfileId shouldBe profile.profileId
                }

                expect("creates Company and Contact without reading the reservation") {
                    // Two Opera calls: Company POST then Contact POST to /crm/v1/profiles.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a missing booker language is resolved from the reservation before Contact creation") {
                val booking = createProfilesBooking(reservationId = "6008903")
                val profile = requireNotNull(booking.room.guestProfile)

                installFor(booking)

                val result =
                    ohipApi.createProfiles(
                        request = createProfilesRequest(booking, language = null),
                        testId = testId,
                        featureFlagOverrides = createProfilesFlagPins,
                    )

                result.attachEvidence("Create Contact With Resolved Language")

                expect("returns the Contact profile created with the resolved language") {
                    result.response.status.value shouldBe 200
                    result.body.bookerProfileId shouldBe profile.profileId
                    result.body.companyProfileId shouldBe null
                }

                expect("reads the reservation once before creating the Contact") {
                    // Opera calls: GET reservation source, then POST /crm/v1/profiles for Contact.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera Contact rejection maps to the create-profile error") {
                val booking = createProfilesBooking(reservationId = "6008904")

                installFor(booking, excluded = setOf(OPERA_PROFILE_CREATE_STUB_ID))
                installStub(createProfileFailure(booking))

                val result =
                    ohipApi.createProfiles(
                        request = createProfilesRequest(booking, language = "en"),
                        testId = testId,
                        featureFlagOverrides = createProfilesFlagPins,
                    )

                result.attachEvidence("Create Contact Profile Opera Error")

                expect("returns the mapped create-profile error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 953
                }

                expect("attempts one rejected Contact create and no reservation read") {
                    // One rejected Opera call: POST /crm/v1/profiles for Contact.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.CREATE_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private fun createProfilesBooking(
    reservationId: String,
    company: Company? = null,
): Booking {
    val arrival = LocalDate.now().plusDays(14)
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "DOUBLE", adults = 2)
    val profile =
        GuestProfile(
            profileId = "5008900",
            firstName = "Jamie",
            lastName = "Booker",
            email = "jamie.booker@example.test",
            phone = "07123456789",
            addressLine = "1 Booker Street",
            city = "London",
            postcode = "SW1A 1AA",
            language = "E",
            reservationContact = true,
        )
    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        companies = listOfNotNull(company),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms =
            listOf(
                BookingRoom(
                    reservationId = reservationId,
                    roomType = rate.roomType,
                    ratePlan = rate.ratePlan,
                    adults = rate.adults,
                    status = ReservationStatus.RESERVED,
                    sourceCode = "44",
                    guestProfile = profile,
                ),
            ),
        booker =
            Booker(
                firstName = profile.firstName,
                lastName = profile.lastName,
                email = profile.email,
            ),
    )
}

private fun createProfilesRequest(
    booking: Booking,
    language: String?,
    company: Company? = null,
): CreateProfilesRequest {
    val profile = requireNotNull(booking.room.guestProfile)
    val companyAddress = company?.address
    return CreateProfilesRequest(
        hotelId = booking.hotel.hotelId,
        reasonForStay = "LEI",
        booker =
            CreateProfilesBooker(
                firstName = profile.firstName,
                lastName = profile.lastName,
                emailAddress = profile.email,
                language = language,
                address =
                    CreateProfilesBookerAddress(
                        addressType = "BUSINESS",
                        postalCode = companyAddress?.postalCode ?: profile.postcode,
                        addressLine1 = companyAddress?.addressLine1 ?: profile.addressLine,
                        addressLine2 = companyAddress?.addressLine2,
                        addressLine3 = companyAddress?.addressLine3,
                        addressLine4 = companyAddress?.addressLine4,
                        countryCode = companyAddress?.country ?: profile.country,
                        cityName = profile.city,
                        companyName = company?.name,
                    ),
            ),
        stayingGuests =
            listOf(
                CreateProfilesStayingGuest(
                    reservationId = requireNotNull(booking.room.reservationId),
                    sameAsBooker = true,
                    stayingGuestDetails =
                        CreateProfilesStayingGuestDetails(
                            firstName = profile.firstName,
                            lastName = profile.lastName,
                            emailAddress = profile.email,
                        ),
                ),
            ),
    )
}
