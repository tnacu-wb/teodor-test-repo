package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.BodyPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.absentJsonPath
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.literal
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.BillingAddress
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Company
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile

// Mappings for `GET` and `PUT /crm/v1/profiles/{id}`.
//
// Opera serves a guest profile and a company profile from this one path, so the guest and company
// builders live here together. They are told apart by the ID in the URL, not by the endpoint.

const val OPERA_PROFILE_GET_STUB_ID = "booking.opera.get-profile"
const val OPERA_PROFILE_UPDATE_STUB_ID = "booking.opera.update-profile"

/** Builds the test-ID-scoped Opera profile lookup mapping for [room]. */
fun profile(room: BookingRoom): PlannedStub = profiles(listOf(room))

/**
 * Builds the Opera profile reads for every guest profile the rooms state Opera holds: each room's
 * own guest profile plus, when stated, its [BookingRoom.accompanyingGuestProfile] — the second
 * guest already on the reservation, whose profile a pre-check-in read-back resolves from the same
 * URL shape.
 */
fun profiles(rooms: List<BookingRoom>): PlannedStub =
    PlannedStub(
        id = OPERA_PROFILE_GET_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = rooms.flatMap(::profileMappings),
    )

private fun profileMappings(room: BookingRoom): List<StubMapping> =
    statedGuestProfiles(room).map { profile ->
        StubMapping(
            request =
                RequestPattern(
                    method = "GET",
                    urlPath = "/crm/v1/profiles/${profile.profileId}",
                ),
            response =
                jsonResponse(
                    jsonBody = profileResponse(profile),
                ),
        )
    }

/**
 * Every guest profile the room states Opera holds: its own guest plus any accompanying guest,
 * de-duplicated by profile id so one URL never receives two mappings (a pinned mapping paired
 * with a permissive twin would let a wrong body fall through and match).
 */
private fun statedGuestProfiles(room: BookingRoom): List<GuestProfile> =
    listOfNotNull(room.guestProfile, room.accompanyingGuestProfile).distinctBy { it.profileId }

/**
 * Builds the Booking-scoped Opera profile-amend capability: the rooms' guest-profile update
 * mappings plus one body-permissive `PUT /crm/v1/profiles/{companyId}` per [Booking.companies]
 * entry — the amend capability behind a company rename, which sends a name-only body to the
 * company's own profile id.
 *
 * A [Booking.booker] stating a billingAddress pins each room's own guest-profile PUT — and each
 * company profile's PUT — to the BILLING-typed address body a billing-address update writes.
 * A room's [BookingRoom.bookerEmailAfterUpdate] pins the same own-profile PUT; `Booking.init`
 * forbids stating both facts on one Booking, so the pins never compete.
 */
fun updateProfiles(
    booking: Booking,
    rooms: List<BookingRoom>,
): PlannedStub =
    updateProfileStub(
        rooms.flatMap { room -> updateProfileMappings(room, booking.booker?.billingAddress) } +
            booking.companies.map { company ->
                companyProfileUpdateMapping(company, booking.booker?.billingAddress)
            },
    )

private fun updateProfileStub(mappings: List<StubMapping>): PlannedStub =
    PlannedStub(
        id = OPERA_PROFILE_UPDATE_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = mappings,
    )

private fun updateProfileMappings(
    room: BookingRoom,
    billingAddress: BillingAddress?,
): List<StubMapping> =
    statedGuestProfiles(room).map { profile ->
        // Identity by profile id, not structural equality: an accompanying profile sharing the
        // guest profile's id is the same Opera record, and must receive the pinned mapping
        // rather than a permissive twin on the same URL.
        val ownProfile = profile.profileId == room.guestProfile?.profileId
        val bodyPatterns =
            when {
                // Booking.init forbids stating both facts, so the branches never compete.
                ownProfile && room.bookerEmailAfterUpdate != null ->
                    bookerEmailProfilePatterns(profile.profileId, room.bookerEmailAfterUpdate)
                ownProfile && billingAddress != null -> billingAddressProfilePatterns(billingAddress)
                else -> null
            }
        StubMapping(
            request =
                RequestPattern(
                    method = "PUT",
                    urlPath = "/crm/v1/profiles/${profile.profileId}",
                    bodyPatterns = bodyPatterns,
                ),
            response =
                jsonResponse(
                    jsonBody = profileLinkResponse(profile.profileId),
                ),
        )
    }

/**
 * Models the amend capability for [company]'s own Opera profile. A Booking whose booker states a
 * billingAddress pins the PUT to the same BILLING-typed body as the guest-profile amend — the
 * billing-address route writes the identical address to the company profile; otherwise the
 * mapping stays body-permissive so a name-only company rename keeps matching.
 */
private fun companyProfileUpdateMapping(
    company: Company,
    billingAddress: BillingAddress?,
): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "PUT",
                urlPath = "/crm/v1/profiles/${company.companyId}",
                bodyPatterns = billingAddress?.let(::billingAddressProfilePatterns),
            ),
        response =
            jsonResponse(
                jsonBody = profileLinkResponse(company.companyId),
            ),
    )

/**
 * Requires the minimal emails-only CRM body a booker-email update writes: the profile's own id
 * typed `Profile`, exactly one email entry carrying the new address, and no `customer`,
 * `addresses`, or `telephones` echo of the profile that was read.
 */
private fun bookerEmailProfilePatterns(
    profileId: String,
    email: String,
): List<BodyPattern> =
    listOf(
        BodyPattern(
            matchesJsonPath =
                "$.profileIdList[?(@.id == ${literal(profileId)} && @.type == \"Profile\")]",
        ),
        BodyPattern(
            matchesJsonPath =
                "$.profileDetails.emails.emailInfo[?(@.email.emailAddress == ${literal(email)})]",
        ),
        absentJsonPath("$.profileDetails.emails.emailInfo[1]"),
        absentJsonPath("$.profileDetails.customer"),
        absentJsonPath("$.profileDetails.addresses"),
        absentJsonPath("$.profileDetails.telephones"),
    )

/**
 * Requires the BILLING-address CRM body a billing-address update writes: `addressInfo[0]` with no
 * `type` and no `id` member (Opera adds a billing address rather than editing the current one),
 * the address typed `BILLING` with exactly the stated lines, city, postal code, and country
 * value, and `primaryInd` true when the stated address is primary — absent or false otherwise.
 */
private fun billingAddressProfilePatterns(billingAddress: BillingAddress): List<BodyPattern> =
    buildList {
        add(absentJsonPath("$.profileDetails.addresses.addressInfo[0].type"))
        add(absentJsonPath("$.profileDetails.addresses.addressInfo[0].id"))
        add(
            BodyPattern(
                matchesJsonPath =
                    "$.profileDetails.addresses.addressInfo[?(@.address.type == \"BILLING\")]",
            ),
        )
        billingAddress.addressLines.forEachIndexed { index, line ->
            add(
                BodyPattern(
                    matchesJsonPath =
                        "$.profileDetails.addresses.addressInfo" +
                            "[?(@.address.addressLine[$index] == ${literal(line)})]",
                ),
            )
        }
        add(
            BodyPattern(
                not =
                    BodyPattern(
                        matchesJsonPath =
                            "$.profileDetails.addresses.addressInfo[0]" +
                                ".address.addressLine[${billingAddress.addressLines.size}]",
                    ),
            ),
        )
        add(
            BodyPattern(
                matchesJsonPath =
                    "$.profileDetails.addresses.addressInfo" +
                        "[?(@.address.cityName == ${literal(billingAddress.cityName)})]",
            ),
        )
        add(
            BodyPattern(
                matchesJsonPath =
                    "$.profileDetails.addresses.addressInfo" +
                        "[?(@.address.postalCode == ${literal(billingAddress.postalCode)})]",
            ),
        )
        add(
            BodyPattern(
                matchesJsonPath =
                    "$.profileDetails.addresses.addressInfo" +
                        "[?(@.address.country.value == ${literal(billingAddress.countryCode)})]",
            ),
        )
        val primaryPin = "$.profileDetails.addresses.addressInfo[?(@.address.primaryInd == true)]"
        add(if (billingAddress.primaryAddress) BodyPattern(matchesJsonPath = primaryPin) else absentJsonPath(primaryPin))
    }

/**
 * Builds the structured Opera profile lookup response.
 *
 * A profile stating a marketing opt-in also carries Opera's `privacyInfo` block, the only place
 * consumers read a contact-preference state from. Left unstated the block is absent entirely,
 * which is how Opera reports a profile whose preferences were never recorded.
 */
private fun profileResponse(profile: GuestProfile) =
    stubJsonObject(
        "profileIdList" to listOf(mapOf("id" to profile.profileId, "type" to "Profile")),
        "profileDetails" to
            buildMap<String, Any?> {
                put(
                    "customer",
                    buildMap<String, Any?> {
                        put(
                            "personName",
                            listOf(
                                mapOf(
                                    "givenName" to profile.firstName,
                                    "surname" to profile.lastName,
                                    "language" to profile.language,
                                    "nameType" to "PRIMARY",
                                ),
                            ),
                        )
                        put("language", profile.language)
                        put("nationality", profile.nationality)
                        profile.vipStatus?.let { put("vipStatus", it) }
                    },
                )
                put(
                    "addresses",
                    mapOf(
                        "addressInfo" to
                            listOf(
                                mapOf(
                                    "address" to
                                        mapOf(
                                            "addressLine" to listOf(profile.addressLine, "", "", ""),
                                            "cityName" to profile.city,
                                            "postalCode" to profile.postcode,
                                            "country" to mapOf("code" to profile.country),
                                        ),
                                    "type" to "HOME",
                                    "primary" to true,
                                ),
                            ),
                    ),
                )
                put(
                    "telephones",
                    mapOf(
                        "telephoneInfo" to
                            listOf(
                                mapOf(
                                    "telephone" to mapOf("phoneNumber" to profile.phone),
                                    "type" to "PHONE",
                                    "primary" to true,
                                ),
                            ),
                    ),
                )
                put(
                    "emails",
                    mapOf(
                        "emailInfo" to
                            listOf(
                                mapOf(
                                    "email" to mapOf("emailAddress" to profile.email),
                                    "type" to "EMAIL",
                                    "primary" to true,
                                ),
                            ),
                    ),
                )
                profile.marketingOptIn?.let { optIn ->
                    put("privacyInfo", mapOf("optInEmail" to optIn))
                }
            },
        "links" to emptyList<Any>(),
    )
