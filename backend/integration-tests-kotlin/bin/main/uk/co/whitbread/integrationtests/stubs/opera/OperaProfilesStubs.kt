package uk.co.whitbread.integrationtests.stubs.opera

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.BodyPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.ResponseDefinition
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.absentJsonPath
import uk.co.whitbread.integrationtests.stubs.jsonPathStringLiteral
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.testkit.model.Booker
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Company
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile

// Mappings for `/crm/v1/profiles`.
//
// Guest-profile creation posts here and company search reads here, so both builders live together.
// They are told apart by method, not by endpoint.

const val OPERA_PROFILE_CREATE_STUB_ID = "booking.opera.create-profile"
const val OPERA_COMPANY_PROFILE_SEARCH_STUB_ID = "booking.opera.company-profile-search"

/** Models the Opera CRM profile-create capabilities needed by one [room] in [booking]. */
fun createProfile(
    booking: Booking,
    room: BookingRoom,
): PlannedStub = createProfiles(booking, listOf(room))

/**
 * Models authenticated Opera CRM profile creation for [rooms] and their Booking world.
 *
 * Guest, reservation-contact, and company creates share one Opera URL, so their mappings are
 * distinguished by the real profile type and reusable identity facts in each request body.
 *
 * Each Company in the Booking gets two mutually exclusive mappings, telling apart the two real
 * company-create bodies Opera can receive:
 * - the address-carrying mapping serves the guest-reservation create flow, whose
 *   `ReservationCompanyRequestOhipMapper.toDto(ReservationGuestRequest)` sends the company name
 *   plus the booker's address block;
 * - the name-only mapping serves the booker-details company attach flow, whose
 *   `ReservationCompanyRequestOhipMapper.toDto(String)` sends `company.companyName` alone —
 *   it requires the absence of `profileDetails.addresses`, so an address-carrying body can
 *   never fall through to it and a name-only body never matches the address-carrying pins.
 */
fun createProfiles(
    booking: Booking,
    rooms: List<BookingRoom>,
): PlannedStub =
    PlannedStub(
        id = OPERA_PROFILE_CREATE_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            buildList {
                addAll(rooms.map { room -> guestProfileCreateMapping(booking, room) })

                val booker = booking.booker
                if (booker != null) {
                    addAll(
                        rooms.mapNotNull { room ->
                            room.guestProfile
                                ?.takeIf(GuestProfile::reservationContact)
                                ?.let { profile -> contactProfileCreateMapping(booking, booker, profile) }
                        },
                    )
                } else if (rooms.isNotEmpty()) {
                    // Some established reservation journeys do not put the submitted booker or
                    // accompanying guest into Booking. Preserve their former POST capability when
                    // those identity facts are unavailable; Wave 2 bookings carry a booker and use
                    // the exact Contact/Company mappings above.
                    addAll(rooms.map(::untypedProfileCreateMapping))
                }
                // The company creates depend only on the companies the Booking knows: a
                // company-only Booking (no guest profiles, no booker) still models Opera
                // creating its company profiles.
                booking.companies.forEach { company ->
                    add(companyProfileCreateMapping(booking, company))
                    add(nameOnlyCompanyProfileCreateMapping(booking, company))
                }
            },
    )

private fun untypedProfileCreateMapping(room: BookingRoom): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "POST",
                urlPath = "/crm/v1/profiles",
            ),
        response = profileCreateResponse(room.guestProfile().profileId),
    )

private fun guestProfileCreateMapping(
    booking: Booking,
    room: BookingRoom,
): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "POST",
                urlPath = "/crm/v1/profiles",
                headers = authenticatedHotelHeaders(booking.hotel.hotelId),
                bodyPatterns =
                    listOf(
                        profileTypePattern("Guest"),
                        BodyPattern(
                            matchesJsonPath =
                                "$[?(@.profileDetails.requestForHotel == " +
                                    "${jsonPathStringLiteral(booking.hotel.hotelId)})]",
                        ),
                    ),
            ),
        response = profileCreateResponse(room.guestProfile().profileId),
    )

private fun contactProfileCreateMapping(
    booking: Booking,
    booker: Booker,
    profile: GuestProfile,
): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "POST",
                urlPath = "/crm/v1/profiles",
                headers = authenticatedHotelHeaders(booking.hotel.hotelId),
                bodyPatterns =
                    listOf(
                        profileTypePattern("Contact"),
                        profileValuePattern("customer.personName[0].givenName", booker.firstName),
                        profileValuePattern("customer.personName[0].surname", booker.lastName),
                        profileValuePattern("customer.language", profile.language),
                    ) +
                        booker.email
                            ?.let { email ->
                                listOf(profileValuePattern("emails.emailInfo[0].email.emailAddress", email))
                            }.orEmpty(),
            ),
        response = profileCreateResponse(profile.profileId),
    )

private fun companyProfileCreateMapping(
    booking: Booking,
    company: Company,
): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "POST",
                urlPath = "/crm/v1/profiles",
                headers = authenticatedHotelHeaders(booking.hotel.hotelId),
                bodyPatterns =
                    listOf(
                        profileTypePattern("Company"),
                        profileValuePattern("company.companyName", company.name),
                        profileValuePattern("addresses.addressInfo[0].address.addressLine[0]", company.address.addressLine1),
                        profileValuePattern("addresses.addressInfo[0].address.postalCode", company.address.postalCode),
                        profileValuePattern("addresses.addressInfo[0].address.country.value", company.address.country),
                    ),
            ),
        response = profileCreateResponse(company.companyId),
    )

/**
 * Models Opera creating a company profile from a name alone (no address block), answering with
 * [company]'s profile id. Mutually exclusive with [companyProfileCreateMapping] via the
 * `not(profileDetails.addresses)` pin.
 */
private fun nameOnlyCompanyProfileCreateMapping(
    booking: Booking,
    company: Company,
): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "POST",
                urlPath = "/crm/v1/profiles",
                headers = authenticatedHotelHeaders(booking.hotel.hotelId),
                bodyPatterns =
                    listOf(
                        profileTypePattern("Company"),
                        profileValuePattern("company.companyName", company.name),
                        absentJsonPath("$.profileDetails.addresses"),
                    ),
            ),
        response = profileCreateResponse(company.companyId),
    )

private fun profileTypePattern(profileType: String): BodyPattern = profileValuePattern("profileType", profileType)

private fun profileValuePattern(
    path: String,
    value: String,
): BodyPattern =
    BodyPattern(
        matchesJsonPath =
            "$[?(@.profileDetails.$path == ${jsonPathStringLiteral(value)})]",
    )

private fun profileCreateResponse(profileId: String): ResponseDefinition =
    ResponseDefinition(
        status = 201,
        headers =
            mapOf(
                "Content-Type" to "application/json",
                "Location" to "/crm/v1/profiles/$profileId",
            ),
        jsonBody = profileLinkResponse(profileId),
    )

/** Models exact name and AR-number profile-summary searches for every company in [booking]. */
fun companiesProfileSearch(booking: Booking): PlannedStub =
    PlannedStub(
        id = OPERA_COMPANY_PROFILE_SEARCH_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            booking.companies.flatMap { company ->
                listOf(
                    companiesProfileSearchMapping(
                        companies = listOf(company),
                        hotelId = booking.hotel.hotelId,
                        companyName = company.name,
                        positiveLimit = true,
                    ),
                ) +
                    company.arNumber
                        ?.let { arNumber ->
                            listOf(
                                companiesProfileSearchMapping(
                                    companies = listOf(company),
                                    hotelId = booking.hotel.hotelId,
                                    arNumber = arNumber,
                                    positiveLimit = true,
                                ),
                            )
                        }.orEmpty()
            },
    )

/**
 * Builds one profile-summary search mapping, optionally pinned to an exact query shape.
 *
 * The generic default passes only [companies]; the pin parameters exist for the custom
 * override builder, because a pinned default would silently stop matching other scenarios'
 * queries.
 */
internal fun companiesProfileSearchMapping(
    companies: List<Company>,
    hotelId: String? = null,
    limit: Int? = null,
    companyName: String? = null,
    arNumber: String? = null,
    positiveLimit: Boolean = false,
): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/crm/v1/profiles",
                queryParameters = companiesProfileSearchQuery(limit, companyName, arNumber, positiveLimit),
                headers = companiesProfileSearchHeaders(hotelId),
            ),
        response =
            jsonResponse(
                jsonBody = companiesProfileSearchResponse(companies, limit ?: companies.size.coerceAtLeast(1)),
            ),
    )

/**
 * Returns null rather than an empty map when this search matches no hotel.
 *
 * Mappings serialize with `encodeDefaults = false` and `RequestPattern.headers` defaults to null,
 * so an empty map would emit `"headers": {}` where the field was previously absent entirely.
 */
private fun companiesProfileSearchHeaders(hotelId: String?): Map<String, StringValuePattern>? =
    hotelId?.let {
        authenticatedHotelHeaders(it) +
            mapOf(
                "x-hubid" to StringValuePattern(absent = true),
            )
    }

private fun companiesProfileSearchQuery(
    limit: Int?,
    companyName: String?,
    arNumber: String?,
    positiveLimit: Boolean,
): Map<String, StringValuePattern>? {
    if (limit == null && companyName == null && arNumber == null && !positiveLimit) return null

    return buildMap {
        if (arNumber != null) {
            put("aRNumber", StringValuePattern(equalTo = arNumber))
            put("profileName", StringValuePattern(absent = true))
        } else if (companyName != null) {
            put("profileName", StringValuePattern(equalTo = "%25$companyName"))
            put("aRNumber", StringValuePattern(absent = true))
        }
        put("profileType", StringValuePattern(equalTo = "Company"))
        put("includePurgeProfiles", StringValuePattern(equalTo = "false"))
        put("accountsReceivables", StringValuePattern(equalTo = "true"))
        put("excludeInactive", StringValuePattern(equalTo = "true"))
        put("includeAnonymized", StringValuePattern(equalTo = "true"))
        put("fetchInstructions", StringValuePattern(equalTo = "SalesInfo"))
        if (limit != null) {
            put("limit", StringValuePattern(equalTo = limit.toString()))
        } else if (positiveLimit) {
            put("limit", StringValuePattern(matches = "[1-9][0-9]*"))
        }
    }
}

private fun companiesProfileSearchResponse(
    companies: List<Company>,
    limit: Int,
): JsonObject =
    JsonObject(
        mapOf(
            "profileSummaries" to
                JsonObject(
                    mapOf(
                        "profileInfo" to JsonArray(companies.map(::profileSummaryInfo)),
                        "totalPages" to JsonPrimitive(if (companies.isEmpty()) 0 else 1),
                        "offset" to JsonPrimitive(0),
                        "limit" to JsonPrimitive(limit),
                        "hasMore" to JsonPrimitive(false),
                        "totalResults" to JsonPrimitive(companies.size),
                    ),
                ),
        ),
    )

private fun profileSummaryInfo(company: Company): JsonObject =
    JsonObject(
        mapOf(
            "profileIdList" to
                JsonArray(
                    listOf(
                        uniqueId(company.companyId, "Profile"),
                        uniqueId(company.corpId, "CorporateId"),
                    ),
                ),
            "profile" to profileSummary(company),
        ),
    )

private fun profileSummary(company: Company): JsonObject {
    val profile =
        mutableMapOf<String, JsonElement>(
            "formerName" to
                JsonObject(
                    mapOf(
                        "fullName" to JsonPrimitive(company.name),
                        "language" to JsonPrimitive(company.language),
                    ),
                ),
            "addressInfo" to
                JsonObject(
                    mapOf(
                        "type" to JsonPrimitive("BUSINESS"),
                        "address" to
                            JsonObject(
                                mapOf(
                                    "addressLine" to
                                        JsonArray(
                                            listOf(
                                                JsonPrimitive(company.address.addressLine1),
                                                JsonPrimitive(company.address.addressLine2),
                                                JsonPrimitive(company.address.addressLine3),
                                                JsonPrimitive(company.address.addressLine4),
                                            ),
                                        ),
                                    "cityName" to JsonPrimitive(""),
                                    "postalCode" to JsonPrimitive(company.address.postalCode),
                                    "country" to
                                        JsonObject(
                                            mapOf(
                                                "value" to JsonPrimitive(company.address.country),
                                                "code" to JsonPrimitive(company.address.country),
                                            ),
                                        ),
                                ),
                            ),
                    ),
                ),
            "telephoneInfo" to
                JsonObject(
                    mapOf(
                        "type" to JsonPrimitive("BUSINESS"),
                        "telephone" to
                            JsonObject(
                                mapOf(
                                    "phoneNumber" to JsonPrimitive(company.telephoneNumber),
                                ),
                            ),
                    ),
                ),
            "profileRestrictions" to
                JsonObject(
                    mapOf(
                        "restricted" to JsonPrimitive(company.restricted),
                        "reason" to JsonPrimitive(company.restrictedReason),
                    ),
                ),
            "profileType" to JsonPrimitive(company.profileType),
            "statusCode" to JsonPrimitive(if (company.active) "Active" else "Inactive"),
        )

    company.arNumber?.let { arNumber ->
        profile["aRAccount"] =
            JsonObject(
                mapOf(
                    "hotelId" to JsonPrimitive("DUBSOU"),
                    "aRNumber" to JsonPrimitive(arNumber),
                ),
            )
    }

    return JsonObject(profile)
}
