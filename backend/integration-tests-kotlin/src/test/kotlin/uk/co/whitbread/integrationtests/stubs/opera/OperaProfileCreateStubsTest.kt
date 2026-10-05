package uk.co.whitbread.integrationtests.stubs.opera

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.defaultStubsFor
import uk.co.whitbread.integrationtests.testkit.model.Booker
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Company
import uk.co.whitbread.integrationtests.testkit.model.CompanyAddress
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile
import uk.co.whitbread.integrationtests.testkit.model.Hotel

/** Pins the shared Guest, Contact, and Company CRM profile-create capability and its Booking gate. */
class OperaProfileCreateStubsTest :
    FunSpec({
        val profile =
            GuestProfile(
                profileId = "5008801",
                firstName = "Jamie",
                lastName = "Booker",
                email = "jamie.booker@example.com",
                phone = "+441110000001",
                addressLine = "1 Profile Street",
                city = "London",
                postcode = "PR1 1AA",
                language = "E",
                reservationContact = true,
            )
        val company =
            Company(
                name = "Profile Test Limited",
                corpId = "1370",
                companyId = "2569623",
                telephoneNumber = "+441234567890",
                address =
                    CompanyAddress(
                        addressLine1 = "2 Company Street",
                        postalCode = "CO1 2BB",
                    ),
            )
        val hotel =
            Hotel(
                hotelId = "HEAPTI",
                shortId = "HAP",
                name = "Profile Hotel",
                addressLine = "1 Hotel Street",
                city = "London",
                postcode = "SW1A 1AA",
                phone = "02079460000",
            )
        val room = BookingRoom(guestProfile = profile)
        val booking =
            Booking(
                hotels = listOf(hotel),
                companies = listOf(company),
                rooms = listOf(room),
                booker =
                    Booker(
                        firstName = profile.firstName,
                        lastName = profile.lastName,
                        email = profile.email,
                    ),
            )

        test("profile creates share the exact authenticated hotel-scoped request shape") {
            val mappings = createProfiles(booking, listOf(room)).mappings

            mappings.size shouldBe 4
            mappings.forEach { mapping ->
                mapping.request.method shouldBe "POST"
                mapping.request.urlPath shouldBe "/crm/v1/profiles"
                mapping.request.queryParameters shouldBe null
                val headers = mapping.request.headers.orEmpty()
                headers.getValue("Authorization").matches shouldBe "Bearer .+"
                headers.getValue("x-app-key").matches shouldBe ".+"
                headers.getValue("x-hotelid").equalTo shouldBe "HEAPTI"
                headers.containsKey("x-hubid") shouldBe false
            }

            mappingFor(mappings, "Guest")
                .request
                .bodyPatterns
                .orEmpty()
                .mapNotNull { it.matchesJsonPath } shouldBe
                listOf(
                    "$[?(@.profileDetails.profileType == \"Guest\")]",
                    "$[?(@.profileDetails.requestForHotel == \"HEAPTI\")]",
                )
            mappingFor(mappings, "Contact")
                .request
                .bodyPatterns
                .orEmpty()
                .mapNotNull { it.matchesJsonPath } shouldBe
                listOf(
                    "$[?(@.profileDetails.profileType == \"Contact\")]",
                    "$[?(@.profileDetails.customer.personName[0].givenName == \"Jamie\")]",
                    "$[?(@.profileDetails.customer.personName[0].surname == \"Booker\")]",
                    "$[?(@.profileDetails.customer.language == \"E\")]",
                    "$[?(@.profileDetails.emails.emailInfo[0].email.emailAddress == \"jamie.booker@example.com\")]",
                )
            addressCompanyMapping(mappings)
                .request
                .bodyPatterns
                .orEmpty()
                .mapNotNull { it.matchesJsonPath } shouldBe
                listOf(
                    "$[?(@.profileDetails.profileType == \"Company\")]",
                    "$[?(@.profileDetails.company.companyName == \"Profile Test Limited\")]",
                    "$[?(@.profileDetails.addresses.addressInfo[0].address.addressLine[0] == \"2 Company Street\")]",
                    "$[?(@.profileDetails.addresses.addressInfo[0].address.postalCode == \"CO1 2BB\")]",
                    "$[?(@.profileDetails.addresses.addressInfo[0].address.country.value == \"GB\")]",
                )
        }

        test("the two Company mappings are mutually exclusive on the address block") {
            val mappings = createProfiles(booking, listOf(room)).mappings
            val nameOnly = nameOnlyCompanyMapping(mappings).request.bodyPatterns.orEmpty()

            // The name-only body (ReservationCompanyRequestOhipMapper.toDto(String)) matches only
            // this mapping: the address-carrying mapping pins addresses.addressInfo[0] paths the
            // name-only body does not carry.
            nameOnly.mapNotNull { it.matchesJsonPath } shouldBe
                listOf(
                    "$[?(@.profileDetails.profileType == \"Company\")]",
                    "$[?(@.profileDetails.company.companyName == \"Profile Test Limited\")]",
                )
            // The address-carrying body (row 89's company+address create) matches only the old
            // mapping: this pin rejects any body carrying an addresses block.
            nameOnly.mapNotNull { it.not?.matchesJsonPath } shouldBe
                listOf("$.profileDetails.addresses")
        }

        test("the name-only Company mapping answers with the company profile id") {
            val mappings = createProfiles(booking, listOf(room)).mappings
            val nameOnly = nameOnlyCompanyMapping(mappings)

            nameOnly.response.status shouldBe 201
            nameOnly.response.headers
                .orEmpty()
                .getValue("Location") shouldBe "/crm/v1/profiles/${company.companyId}"
            responseHref(nameOnly) shouldBe "/crm/v1/profiles/${company.companyId}"
        }

        test("profile-create responses keep the Contact and Company identities independent") {
            val mappings = createProfiles(booking, listOf(room)).mappings
            val contact = mappingFor(mappings, "Contact")
            val companyMapping = addressCompanyMapping(mappings)

            contact.response.status shouldBe 201
            contact.response.headers
                .orEmpty()
                .getValue("Location") shouldBe "/crm/v1/profiles/${profile.profileId}"
            responseHref(contact) shouldBe "/crm/v1/profiles/${profile.profileId}"
            companyMapping.response.status shouldBe 201
            companyMapping.response.headers
                .orEmpty()
                .getValue("Location") shouldBe "/crm/v1/profiles/${company.companyId}"
            responseHref(companyMapping) shouldBe "/crm/v1/profiles/${company.companyId}"
        }

        test("the default gate requires a hotel and a guest profile or a company") {
            val companyOnly = booking.copy(rooms = listOf(BookingRoom()))
            val withoutProfileOrCompany = booking.copy(rooms = listOf(BookingRoom()), companies = emptyList())
            val withoutHotel = booking.copy(hotels = emptyList())

            defaultStubsFor(booking).map { it.id }.contains(OPERA_PROFILE_CREATE_STUB_ID) shouldBe true
            defaultStubsFor(companyOnly).map { it.id }.contains(OPERA_PROFILE_CREATE_STUB_ID) shouldBe true
            defaultStubsFor(withoutProfileOrCompany).map { it.id } shouldNotContain OPERA_PROFILE_CREATE_STUB_ID
            defaultStubsFor(withoutHotel).map { it.id } shouldNotContain OPERA_PROFILE_CREATE_STUB_ID
        }

        test("a company-only booking models exactly the two company create bodies") {
            val companyOnly = booking.copy(rooms = listOf(BookingRoom()), booker = null)

            val mappings =
                defaultStubsFor(companyOnly)
                    .single { it.id == OPERA_PROFILE_CREATE_STUB_ID }
                    .mappings

            // No guest, contact, or untyped mapping: nothing states a guest profile or booker.
            companyMappings(mappings) shouldBe mappings
            mappings.size shouldBe 2
        }
    })

private fun mappingFor(
    mappings: List<StubMapping>,
    profileType: String,
): StubMapping =
    mappings.single { mapping ->
        mapping.request.bodyPatterns.orEmpty().any { pattern ->
            pattern.matchesJsonPath == "$[?(@.profileDetails.profileType == \"$profileType\")]"
        }
    }

private fun companyMappings(mappings: List<StubMapping>): List<StubMapping> =
    mappings.filter { mapping ->
        mapping.request.bodyPatterns.orEmpty().any { pattern ->
            pattern.matchesJsonPath == "$[?(@.profileDetails.profileType == \"Company\")]"
        }
    }

private fun addressCompanyMapping(mappings: List<StubMapping>): StubMapping =
    companyMappings(mappings).single { mapping ->
        mapping.request.bodyPatterns
            .orEmpty()
            .none { it.not != null }
    }

private fun nameOnlyCompanyMapping(mappings: List<StubMapping>): StubMapping =
    companyMappings(mappings).single { mapping ->
        mapping.request.bodyPatterns
            .orEmpty()
            .any { it.not != null }
    }

private fun responseHref(mapping: StubMapping): String =
    mapping.response.jsonBody!!
        .jsonObject
        .getValue("links")
        .jsonArray
        .single()
        .jsonObject
        .getValue("href")
        .jsonPrimitive
        .content
