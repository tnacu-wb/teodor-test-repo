package uk.co.whitbread.integrationtests.stubs.opera

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.defaultStubsFor
import uk.co.whitbread.integrationtests.testkit.model.BillingAddress
import uk.co.whitbread.integrationtests.testkit.model.Booker
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Company
import uk.co.whitbread.integrationtests.testkit.model.CompanyAddress
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import java.time.LocalDate

/**
 * Characterizes the Opera profile lookup response: the identity blocks it always reports and the
 * privacy block it reports only for a profile whose marketing preference was recorded, which is
 * the only place a consumer reads a contact opt-in state from. Also pins the profile-amend
 * capability: the accompanying-guest and company mappings it serves and the exact CRM bodies the
 * booker-email and billing-address facts make the update accept.
 */
class OperaProfileByIdStubsTest :
    FunSpec({
        val arrival: LocalDate = LocalDate.of(2026, 9, 1)
        val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)
        val guest =
            GuestProfile(
                profileId = "PROF-PRIV-1",
                firstName = "Amelia",
                lastName = "Wright",
                email = "amelia.wright@test.com",
                phone = "+447700900001",
                addressLine = "1 High Street",
                city = "London",
                postcode = "SW1A 1AA",
            )
        val booking =
            Booking(
                hotels =
                    listOf(
                        Hotel(
                            hotelId = "PRIV01",
                            shortId = "privacy-hotel",
                            name = "Privacy Hotel",
                            addressLine = "1 Gate Street",
                            city = "London",
                            postcode = "SW1A 1AA",
                            phone = "02079460000",
                            availableRates = listOf(rate),
                        ),
                    ),
                arrival = arrival,
                departure = arrival.plusDays(2),
                rooms =
                    listOf(
                        BookingRoom(
                            reservationId = "RSV-PRIV-1",
                            roomType = rate.roomType,
                            adults = rate.adults,
                            status = ReservationStatus.RESERVED,
                            guestProfile = guest,
                        ),
                    ),
            )

        fun profileDetailsFor(guestProfile: GuestProfile) =
            profile(BookingRoom(guestProfile = guestProfile))
                .mappings
                .single()
                .response.jsonBody!!
                .jsonObject
                .getValue("profileDetails")
                .jsonObject

        test("the profile lookup matches the guest's own Opera profile URL") {
            val request = profile(booking.room).mappings.single().request

            request.method shouldBe "GET"
            request.urlPath shouldBe "/crm/v1/profiles/PROF-PRIV-1"
        }

        test("a profile whose marketing preference was never recorded carries no privacy block") {
            val details = profileDetailsFor(guest)

            details.containsKey("privacyInfo") shouldBe false
            // The identity blocks consumers read alongside it are unaffected.
            details.containsKey("customer") shouldBe true
            details.containsKey("addresses") shouldBe true
            details.containsKey("telephones") shouldBe true
            details.containsKey("emails") shouldBe true
        }

        test("a profile opted in to marketing email reports that state in the privacy block") {
            profileDetailsFor(guest.copy(marketingOptIn = true))
                .getValue("privacyInfo")
                .jsonObject
                .getValue("optInEmail")
                .jsonPrimitive
                .content shouldBe "true"
        }

        test("a profile opted out of marketing email reports the opt-out, not an absent block") {
            profileDetailsFor(guest.copy(marketingOptIn = false))
                .getValue("privacyInfo")
                .jsonObject
                .getValue("optInEmail")
                .jsonPrimitive
                .content shouldBe "false"
        }

        val accompanying =
            GuestProfile(
                profileId = "PROF-PRIV-2",
                firstName = "Robin",
                lastName = "Wright",
                email = "robin.wright@test.com",
                phone = "+447700900002",
                addressLine = "1 High Street",
                city = "London",
                postcode = "SW1A 1AA",
            )

        val company =
            Company(
                name = "Neill Technical Services",
                corpId = "104937",
                companyId = "2569623",
                telephoneNumber = "02079460001",
                address = CompanyAddress(addressLine1 = "2 Gate Street", postalCode = "SW1A 1AB"),
            )

        fun getMappings(target: Booking): List<StubMapping> = defaultStubsFor(target).single { it.id == OPERA_PROFILE_GET_STUB_ID }.mappings

        fun updateMappings(target: Booking): List<StubMapping> =
            defaultStubsFor(target).single { it.id == OPERA_PROFILE_UPDATE_STUB_ID }.mappings

        fun jsonPaths(mapping: StubMapping): List<String> =
            mapping.request.bodyPatterns
                .orEmpty()
                .mapNotNull { pattern -> pattern.matchesJsonPath }

        fun absentJsonPaths(mapping: StubMapping): List<String> =
            mapping.request.bodyPatterns
                .orEmpty()
                .mapNotNull { pattern -> pattern.not?.matchesJsonPath }

        fun withRoom(configure: BookingRoom.() -> BookingRoom): Booking = booking.copy(rooms = listOf(booking.room.configure()))

        test("a booking stating none of the new profile facts keeps the prior read and permissive update") {
            val reads = getMappings(booking)
            val updates = updateMappings(booking)

            reads.map { it.request.urlPath } shouldContainExactly listOf("/crm/v1/profiles/PROF-PRIV-1")
            updates.map { it.request.urlPath } shouldContainExactly listOf("/crm/v1/profiles/PROF-PRIV-1")
            updates.single().request.method shouldBe "PUT"
            updates.single().request.bodyPatterns shouldBe null
            updates.single().request.queryParameters shouldBe null
            updates.single().request.headers shouldBe null
        }

        test("an accompanying guest's profile is served for read and permissive update under the same ids") {
            val target = withRoom { copy(accompanyingGuestProfile = accompanying) }

            val reads = getMappings(target)
            val updates = updateMappings(target)

            reads.map { it.request.urlPath } shouldContainExactly
                listOf("/crm/v1/profiles/PROF-PRIV-1", "/crm/v1/profiles/PROF-PRIV-2")
            reads
                .single { it.request.urlPath == "/crm/v1/profiles/PROF-PRIV-2" }
                .response.jsonBody!!
                .jsonObject
                .getValue("profileIdList")
                .toString() shouldBe """[{"id":"PROF-PRIV-2","type":"Profile"}]"""
            updates.map { it.request.urlPath } shouldContainExactly
                listOf("/crm/v1/profiles/PROF-PRIV-1", "/crm/v1/profiles/PROF-PRIV-2")
            updates.forEach { it.request.bodyPatterns shouldBe null }
        }

        test("the booker-email fact pins the guest profile update to the emails-only CRM body") {
            val target = withRoom { copy(bookerEmailAfterUpdate = "new@example.com") }

            val mapping = updateMappings(target).single()

            mapping.request.urlPath shouldBe "/crm/v1/profiles/PROF-PRIV-1"
            jsonPaths(mapping) shouldContainExactly
                listOf(
                    "$.profileIdList[?(@.id == \"PROF-PRIV-1\" && @.type == \"Profile\")]",
                    "$.profileDetails.emails.emailInfo[?(@.email.emailAddress == \"new@example.com\")]",
                )
            absentJsonPaths(mapping) shouldContainExactly
                listOf(
                    "$.profileDetails.emails.emailInfo[1]",
                    "$.profileDetails.customer",
                    "$.profileDetails.addresses",
                    "$.profileDetails.telephones",
                )
        }

        test("the billing-address fact pins the BILLING body marked as the profile's primary address") {
            val target =
                booking.copy(
                    booker =
                        Booker(
                            firstName = "Pat",
                            lastName = "Booker",
                            billingAddress =
                                BillingAddress(
                                    addressLines = listOf("1 High Street", "Westminster"),
                                    cityName = "London",
                                    postalCode = "SW1A 1AA",
                                    countryCode = "GB",
                                    primaryAddress = true,
                                ),
                        ),
                )

            val mapping = updateMappings(target).single()

            jsonPaths(mapping) shouldContainExactly
                listOf(
                    "$.profileDetails.addresses.addressInfo[?(@.address.type == \"BILLING\")]",
                    "$.profileDetails.addresses.addressInfo[?(@.address.addressLine[0] == \"1 High Street\")]",
                    "$.profileDetails.addresses.addressInfo[?(@.address.addressLine[1] == \"Westminster\")]",
                    "$.profileDetails.addresses.addressInfo[?(@.address.cityName == \"London\")]",
                    "$.profileDetails.addresses.addressInfo[?(@.address.postalCode == \"SW1A 1AA\")]",
                    "$.profileDetails.addresses.addressInfo[?(@.address.country.value == \"GB\")]",
                    "$.profileDetails.addresses.addressInfo[?(@.address.primaryInd == true)]",
                )
            absentJsonPaths(mapping) shouldContainExactly
                listOf(
                    "$.profileDetails.addresses.addressInfo[0].type",
                    "$.profileDetails.addresses.addressInfo[0].id",
                    "$.profileDetails.addresses.addressInfo[0].address.addressLine[2]",
                )
        }

        test("a billing address not stated primary requires primaryInd absent or false") {
            val target =
                booking.copy(
                    booker =
                        Booker(
                            firstName = "Pat",
                            lastName = "Booker",
                            billingAddress =
                                BillingAddress(
                                    addressLines = listOf("1 High Street"),
                                    cityName = "London",
                                    postalCode = "SW1A 1AA",
                                    countryCode = "GB",
                                ),
                        ),
                )

            val mapping = updateMappings(target).single()

            jsonPaths(mapping).none { path -> path.contains("primaryInd") } shouldBe true
            absentJsonPaths(mapping) shouldContain
                "$.profileDetails.addresses.addressInfo[?(@.address.primaryInd == true)]"
        }

        test("every Booking company gets a body-permissive amend mapping under the existing update id") {
            val target = booking.copy(companies = listOf(company))

            val mapping =
                updateMappings(target).single { it.request.urlPath == "/crm/v1/profiles/2569623" }

            mapping.request.method shouldBe "PUT"
            mapping.request.bodyPatterns shouldBe null
            mapping.response.jsonBody!!
                .jsonObject
                .containsKey("links") shouldBe true
        }

        test("a stated billing address pins the company amend to the same BILLING body") {
            val target =
                booking.copy(
                    companies = listOf(company),
                    booker =
                        Booker(
                            firstName = "Pat",
                            lastName = "Booker",
                            billingAddress =
                                BillingAddress(
                                    addressLines = listOf("1 High Street"),
                                    cityName = "London",
                                    postalCode = "SW1A 1AA",
                                    countryCode = "GB",
                                    primaryAddress = true,
                                ),
                        ),
                )

            val mapping =
                updateMappings(target).single { it.request.urlPath == "/crm/v1/profiles/2569623" }

            jsonPaths(mapping) shouldContain
                "$.profileDetails.addresses.addressInfo[?(@.address.type == \"BILLING\")]"
            jsonPaths(mapping) shouldContain
                "$.profileDetails.addresses.addressInfo[?(@.address.postalCode == \"SW1A 1AA\")]"
        }

        test("an accompanying profile sharing the guest profile's id gets one mapping, the pinned one") {
            val sharedIdRoom =
                booking.room.copy(
                    guestProfile = guest,
                    accompanyingGuestProfile = guest.copy(firstName = "Second"),
                    bookerEmailAfterUpdate = "new@example.com",
                )
            val target = booking.copy(rooms = listOf(sharedIdRoom))

            val putMappings = updateMappings(target)
            putMappings.map { it.request.urlPath } shouldContainExactly
                listOf("/crm/v1/profiles/PROF-PRIV-1")
            putMappings
                .single()
                .request.bodyPatterns
                .orEmpty()
                .isNotEmpty() shouldBe true

            // The read capability collapses the same way: one GET per distinct profile id.
            profiles(listOf(sharedIdRoom)).mappings.map { it.request.urlPath } shouldContainExactly
                listOf("/crm/v1/profiles/PROF-PRIV-1")
        }

        test("a Booking may not state both the billing-address and booker-email pins") {
            shouldThrow<IllegalArgumentException> {
                booking.copy(
                    booker =
                        Booker(
                            firstName = "Pat",
                            lastName = "Booker",
                            billingAddress =
                                BillingAddress(
                                    addressLines = listOf("1 High Street"),
                                    cityName = "London",
                                    postalCode = "SW1A 1AA",
                                    countryCode = "GB",
                                ),
                        ),
                    rooms = listOf(booking.room.copy(bookerEmailAfterUpdate = "new@example.com")),
                )
            }.message.orEmpty().contains("state one per Booking") shouldBe true
        }

        test("a Booking with companies and no guest profiles still plans the amend capability") {
            val companiesOnly = Booking(companies = listOf(company))

            val ids = defaultStubsFor(companiesOnly).map { it.id }

            ids shouldContain OPERA_PROFILE_UPDATE_STUB_ID
            ids shouldNotContain OPERA_PROFILE_GET_STUB_ID
            updateMappings(companiesOnly).map { it.request.urlPath } shouldContainExactly
                listOf("/crm/v1/profiles/2569623")
        }

        test("a Booking with neither guest profiles nor companies plans no profile capability") {
            val ids = defaultStubsFor(Booking()).map { it.id }

            ids shouldNotContain OPERA_PROFILE_GET_STUB_ID
            ids shouldNotContain OPERA_PROFILE_UPDATE_STUB_ID
        }

        test("the billing-address fact rejects blank members") {
            shouldThrow<IllegalArgumentException> {
                BillingAddress(addressLines = emptyList(), cityName = "London", postalCode = "SW1A 1AA", countryCode = "GB")
            }.message.orEmpty() shouldBe "billingAddress.addressLines must not be empty"
            shouldThrow<IllegalArgumentException> {
                BillingAddress(addressLines = listOf(" "), cityName = "London", postalCode = "SW1A 1AA", countryCode = "GB")
            }.message.orEmpty() shouldBe "billingAddress.addressLines must not contain blanks"
            shouldThrow<IllegalArgumentException> {
                BillingAddress(addressLines = listOf("1 High Street"), cityName = " ", postalCode = "SW1A 1AA", countryCode = "GB")
            }.message.orEmpty() shouldBe "billingAddress.cityName must not be blank"
            shouldThrow<IllegalArgumentException> {
                BillingAddress(addressLines = listOf("1 High Street"), cityName = "London", postalCode = " ", countryCode = "GB")
            }.message.orEmpty() shouldBe "billingAddress.postalCode must not be blank"
            shouldThrow<IllegalArgumentException> {
                BillingAddress(addressLines = listOf("1 High Street"), cityName = "London", postalCode = "SW1A 1AA", countryCode = " ")
            }.message.orEmpty() shouldBe "billingAddress.countryCode must not be blank"
        }

        test("the marketing fact rides the guest-profile gate rather than adding one of its own") {
            val optedIn =
                booking.copy(
                    rooms = booking.rooms.map { it.copy(guestProfile = guest.copy(marketingOptIn = true)) },
                )

            defaultStubsFor(optedIn).map { it.id } shouldContain OPERA_PROFILE_GET_STUB_ID
            defaultStubsFor(optedIn)
                .single { it.id == OPERA_PROFILE_GET_STUB_ID }
                .mappings
                .single()
                .response
                .jsonBody!!
                .jsonObject
                .getValue("profileDetails")
                .jsonObject
                .getValue("privacyInfo")
                .jsonObject
                .getValue("optInEmail")
                .jsonPrimitive
                .content shouldBe "true"
        }
    })
