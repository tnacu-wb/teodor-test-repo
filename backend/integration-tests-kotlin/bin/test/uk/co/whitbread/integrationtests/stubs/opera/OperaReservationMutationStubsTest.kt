package uk.co.whitbread.integrationtests.stubs.opera

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.framework.wiremock.model.BodyPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.defaultStubsFor
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.CharacterUdf
import uk.co.whitbread.integrationtests.testkit.model.Company
import uk.co.whitbread.integrationtests.testkit.model.CompanyAddress
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationAlert
import uk.co.whitbread.integrationtests.testkit.model.ReservationAttachedProfiles
import uk.co.whitbread.integrationtests.testkit.model.ReservationComment
import uk.co.whitbread.integrationtests.testkit.model.ReservationPreferenceCollection
import uk.co.whitbread.integrationtests.testkit.model.ReservationPreferenceValue
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.model.RoutingFolioInstruction
import uk.co.whitbread.integrationtests.testkit.model.RoutingInstruction
import java.time.LocalDate
import io.kotest.matchers.string.shouldContain as shouldContainText

/**
 * Pins the strengthened generic Opera reservation read and update capabilities: the read's
 * required headers and known caller fetch-instruction variants, the preference collections it
 * renders, and the exact Opera bodies each after-update Booking fact makes the update accept.
 */
class OperaReservationMutationStubsTest :
    FunSpec({
        val arrival: LocalDate = LocalDate.of(2026, 9, 1)
        val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)
        val booking =
            Booking(
                hotels =
                    listOf(
                        Hotel(
                            hotelId = "MUTA01",
                            shortId = "mutation-hotel",
                            name = "Mutation Hotel",
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
                            reservationId = "RSV-MUT-1",
                            roomType = rate.roomType,
                            adults = rate.adults,
                            status = ReservationStatus.RESERVED,
                        ),
                    ),
            )

        val company =
            Company(
                name = "Neill Technical Services",
                corpId = "104937",
                companyId = "2569623",
                telephoneNumber = "02079460001",
                address = CompanyAddress(addressLine1 = "2 Gate Street", postalCode = "SW1A 1AB"),
            )

        fun bookingWith(vararg rooms: BookingRoom): Booking =
            booking.copy(
                rooms = rooms.toList(),
                // The attached-company render fact requires the company this world knows.
                companies =
                    if (rooms.any { it.attachedCompanyProfileId != null }) listOf(company) else emptyList(),
            )

        fun room(configure: BookingRoom.() -> BookingRoom): BookingRoom = booking.room.configure()

        // The booker-email fact requires a stated guest profile (its pins target that id).
        val bookerGuest =
            GuestProfile(
                profileId = "P-MUT-1",
                firstName = "Pat",
                lastName = "Guest",
                email = "pat@example.com",
                phone = "02079460002",
                addressLine = "1 Gate Street",
                city = "London",
                postcode = "SW1A 1AA",
            )

        fun readMappings(target: Booking): List<StubMapping> =
            defaultStubsFor(target).single { it.id == OPERA_RESERVATION_GET_STUB_ID }.mappings

        fun updateMappings(target: Booking): List<StubMapping> =
            defaultStubsFor(target).single { it.id == OPERA_RESERVATION_PUT_STUB_ID }.mappings

        fun bodyPatternsOf(mapping: StubMapping): List<BodyPattern> = mapping.request.bodyPatterns.orEmpty()

        fun jsonPaths(mapping: StubMapping): List<String> = bodyPatternsOf(mapping).mapNotNull { pattern -> pattern.matchesJsonPath }

        fun absentJsonPaths(mapping: StubMapping): List<String> =
            bodyPatternsOf(mapping).mapNotNull { pattern -> pattern.not?.matchesJsonPath }

        fun reservationReadBody(target: Booking) =
            readMappings(target)
                .first()
                .response
                .jsonBody!!
                .jsonObject
                .getValue("reservations")
                .jsonObject
                .getValue("reservation")
                .jsonArray
                .single()
                .jsonObject

        test("the reservation read requires the Opera bearer token, app key, and hotel headers") {
            val headers =
                readMappings(booking)
                    .first()
                    .request.headers
                    .orEmpty()

            headers.getValue("Authorization").matches shouldBe "Bearer .+"
            headers.getValue("x-app-key").matches shouldBe ".+"
            headers.getValue("x-hotelid").equalTo shouldBe "MUTA01"
        }

        test("the reservation read models exactly the known caller fetch-instruction variants") {
            val mappings = readMappings(booking)
            val variants =
                mappings.map { mapping ->
                    mapping.request.queryParameters
                        .orEmpty()
                        .getValue("fetchInstructions")
                        .hasExactly
                        .orEmpty()
                        .mapNotNull { value -> value.equalTo }
                }

            variants shouldContainExactly
                listOf(
                    listOf(
                        "Reservation",
                        "InventoryItems",
                        "ReservationPolicies",
                        "Packages",
                        "ReservationPaymentMethods",
                        "RoutingInstructions",
                        "Comments",
                        "Preferences",
                        "LinkedReservations",
                        "Alerts",
                    ),
                    listOf(
                        "Reservation",
                        "ReservationPaymentMethods",
                        "ReservationPolicies",
                        "Attachments",
                        "Alerts",
                    ),
                    listOf("Reservation", "ReservationPaymentMethods"),
                    listOf("Reservation", "RoutingInstructions", "Comments"),
                    listOf("Reservation", "Preferences"),
                    listOf("GuestLastStay"),
                )
            // One mapping per known variant and nothing else, so a caller variant that is added or
            // dropped shows up here rather than silently widening or narrowing the read.
            mappings shouldHaveSize RESERVATION_READ_FETCH_INSTRUCTION_VARIANTS.size
            variants shouldContainExactly RESERVATION_READ_FETCH_INSTRUCTION_VARIANTS
            // No variant is modelled twice, so no mapping shadows another for the same query.
            RESERVATION_READ_FETCH_INSTRUCTION_VARIANTS.distinct() shouldHaveSize
                RESERVATION_READ_FETCH_INSTRUCTION_VARIANTS.size
            // Every variant answers with the same reservation world: the other stub tests read the
            // first variant's response only, and this is what makes that read representative.
            mappings.map { it.response }.distinct() shouldHaveSize 1
        }

        test("every room's reservation read models the full variant set") {
            val twoRooms =
                bookingWith(
                    booking.room,
                    room { copy(reservationId = "RSV-MUT-2") },
                )

            readMappings(twoRooms) shouldHaveSize 2 * RESERVATION_READ_FETCH_INSTRUCTION_VARIANTS.size
        }

        test("a room stating no preferences reports nothing preference-related") {
            reservationReadBody(booking).containsKey("preferenceCollection") shouldBe false
        }

        test("a room stating preferences reports them as Opera's preference collections") {
            val preferences =
                listOf(
                    ReservationPreferenceCollection(
                        preferenceType = "SPECIALS",
                        preferenceTypeDescription = "Specials",
                        values =
                            listOf(
                                ReservationPreferenceValue(value = "HIFLR", description = "High floor"),
                                ReservationPreferenceValue(value = "QUIET"),
                            ),
                    ),
                )
            val collection =
                reservationReadBody(bookingWith(room { copy(reservationPreferences = preferences) }))
                    .getValue("preferenceCollection")
                    .jsonArray
                    .single()
                    .jsonObject

            collection.getValue("preferenceType").jsonPrimitive.content shouldBe "SPECIALS"
            collection.getValue("preferenceTypeDescription").jsonPrimitive.content shouldBe "Specials"
            val values = collection.getValue("preference").jsonArray.map { it.jsonObject }
            values.map { it.getValue("preferenceValue").jsonPrimitive.content } shouldContainExactly
                listOf("HIFLR", "QUIET")
            values[0].getValue("description").jsonPrimitive.content shouldBe "High floor"
            // A value without a description reports none, as Opera does.
            values[1].containsKey("description") shouldBe false
        }

        test("the preferences fact changes the read body and the update body, not the planned stubs") {
            val withPreferences =
                bookingWith(
                    room {
                        copy(
                            reservationPreferences =
                                listOf(
                                    ReservationPreferenceCollection(
                                        preferenceType = "SPECIALS",
                                        values = listOf(ReservationPreferenceValue(value = "HIFLR")),
                                    ),
                                ),
                        )
                    },
                )

            defaultStubsFor(withPreferences).map { it.id } shouldContainExactly
                defaultStubsFor(booking).map { it.id }
            bodyPatternsOf(updateMappings(withPreferences).single()).shouldNotBeEmpty()
        }

        test("a room stating no after-update fact keeps the permissive any-body update") {
            val mapping = updateMappings(booking).single()

            mapping.request.method shouldBe "PUT"
            mapping.request.bodyPatterns shouldBe null
        }

        test("the question-and-answer facts pin one instruction, identity, comments, reference, and UDFs") {
            val questionAndAnswer =
                bookingWith(
                    room {
                        copy(
                            reservationCommentsAfterUpdate =
                                listOf(
                                    ReservationComment(
                                        title = "Purchase order?|Purchase order",
                                        text = "PO-42",
                                        type = "PUR_ORD_QNA",
                                    ),
                                ),
                            customReferenceAfterUpdate = "CUST-REF-7",
                            characterUdfsAfterUpdate = listOf(CharacterUdf(name = "UDFC11", value = "PO-42")),
                        )
                    },
                )
            val mapping = updateMappings(questionAndAnswer).single()

            jsonPaths(mapping) shouldContainExactly
                listOf(
                    "$.reservations[?(@.hotelId == \"MUTA01\")]",
                    "$.reservations[*].reservationIdList[?(@.id == \"RSV-MUT-1\" && @.type == \"Reservation\")]",
                    "$.reservations[*].comments",
                    "$.reservations[*].comments[?(@.comment.type == \"PUR_ORD_QNA\" && " +
                        "@.comment.commentTitle == \"Purchase order?|Purchase order\" && " +
                        "@.comment.text.value == \"PO-42\")]",
                    "$.reservations[?(@.customReference == \"CUST-REF-7\")]",
                    "$.reservations[*].userDefinedFields.characterUDFs" +
                        "[?(@.name == \"UDFC11\" && @.value == \"PO-42\")]",
                )
            absentJsonPaths(mapping) shouldContainExactly
                listOf(
                    "$.reservations[1]",
                    "$.reservations[*].reservationIdList[1]",
                    "$.reservations[*].comments[1]",
                    "$.reservations[*].userDefinedFields.characterUDFs[1]",
                )
        }

        test("an explicitly empty comments fact requires a body whose comments collection is empty") {
            val noComments = bookingWith(room { copy(reservationCommentsAfterUpdate = emptyList()) })
            val mapping = updateMappings(noComments).single()

            jsonPaths(mapping) shouldContain "$.reservations[*].comments"
            absentJsonPaths(mapping) shouldContainExactly
                listOf(
                    "$.reservations[1]",
                    "$.reservations[*].reservationIdList[1]",
                    "$.reservations[*].comments[0]",
                )
        }

        test("a character-UDF-only fact pins the hotel and UDFs without requiring body identity") {
            val linkedCustomer =
                bookingWith(
                    room {
                        copy(
                            characterUdfsAfterUpdate =
                                listOf(
                                    CharacterUdf(name = "UDFC35", value = "CDH-1234"),
                                    CharacterUdf(name = "UDFC09", value = "PI"),
                                ),
                        )
                    },
                )
            val mapping = updateMappings(linkedCustomer).single()

            jsonPaths(mapping) shouldContainExactly
                listOf(
                    "$.reservations[?(@.hotelId == \"MUTA01\")]",
                    "$.reservations[*].userDefinedFields.characterUDFs" +
                        "[?(@.name == \"UDFC35\" && @.value == \"CDH-1234\")]",
                    "$.reservations[*].userDefinedFields.characterUDFs" +
                        "[?(@.name == \"UDFC09\" && @.value == \"PI\")]",
                )
            // The reservation is identified by the URL alone on a UDF-only body, and a third
            // UDF entry is rejected while the order of the two stated ones stays unpinned.
            absentJsonPaths(mapping) shouldContainExactly
                listOf(
                    "$.reservations[1]",
                    "$.reservations[*].userDefinedFields.characterUDFs[2]",
                )
        }

        test("the purpose-of-stay fact pins one instruction carrying the hotel and that purpose") {
            val mapping =
                updateMappings(bookingWith(room { copy(purposeOfStayAfterUpdate = "Medical") })).single()

            jsonPaths(mapping) shouldContainExactly
                listOf(
                    "$.reservations[?(@.hotelId == \"MUTA01\")]",
                    "$.reservations[?(@.additionalGuestInfo.purposeOfStay == \"Medical\")]",
                )
            absentJsonPaths(mapping) shouldContainExactly listOf("$.reservations[1]")
        }

        test("the agent-id fact replaces the permissive update with a set and a clear body") {
            val mappings = updateMappings(bookingWith(room { copy(ccAgentIdAfterUpdate = "AGENT-9") }))

            mappings.size shouldBe 2
            jsonPaths(mappings[0]) shouldContainExactly
                listOf(
                    "$.reservations[?(@.hotelId == \"MUTA01\")]",
                    "$.reservations[*].userDefinedFields.characterUDFs" +
                        "[?(@.name == \"UDFC08\" && @.value == \"AGENT-9\")]",
                )
            // A cleared value omits the JSON member entirely: the Opera client encodes NON_NULL.
            jsonPaths(mappings[1]) shouldContainExactly
                listOf(
                    "$.reservations[?(@.hotelId == \"MUTA01\")]",
                    "$.reservations[*].userDefinedFields.characterUDFs" +
                        "[?(@.name == \"UDFC08\" && !(@.value))]",
                )
            mappings.forEach { mapping ->
                absentJsonPaths(mapping) shouldContainExactly
                    listOf(
                        "$.reservations[1]",
                        "$.reservations[*].userDefinedFields.characterUDFs[1]",
                    )
            }
        }

        test("the external-reference fact pins identity and the configured Opera id context") {
            val mapping =
                updateMappings(
                    bookingWith(room { copy(externalReferenceAfterUpdate = "BKG-77") }),
                ).single()

            jsonPaths(mapping) shouldContainExactly
                listOf(
                    "$.reservations[?(@.hotelId == \"MUTA01\")]",
                    "$.reservations[*].reservationIdList[?(@.id == \"RSV-MUT-1\" && @.type == \"Reservation\")]",
                    "$.reservations[*].externalReferences" +
                        "[?(@.id == \"BKG-77\" && @.idContext == \"WB_DIGITAL\")]",
                )
            absentJsonPaths(mapping) shouldContainExactly
                listOf(
                    "$.reservations[1]",
                    "$.reservations[*].reservationIdList[1]",
                    "$.reservations[*].externalReferences[1]",
                )
        }

        test("the update requires the deployed digital id context whatever context the Booking states") {
            val migrated =
                bookingWith(room { copy(externalReferenceAfterUpdate = "BKG-77") })
                    .copy(bookingReferenceIdContext = "BART_OHIP")

            // The write always sends config.service.ohip.contextId, so a Booking whose existing
            // reference sits under a migrated context still requires WB_DIGITAL on the PUT body.
            val paths = jsonPaths(updateMappings(migrated).single())
            paths shouldContain
                "$.reservations[*].externalReferences" +
                "[?(@.id == \"BKG-77\" && @.idContext == \"WB_DIGITAL\")]"
            paths.none { path -> path.contains("BART_OHIP") } shouldBe true
        }

        test("the preferences fact pins each value and rejects a sibling reservation's id") {
            val preferences =
                listOf(
                    ReservationPreferenceCollection(
                        preferenceType = "SPECIALS",
                        values = listOf(ReservationPreferenceValue(value = "HIFLR")),
                    ),
                    ReservationPreferenceCollection(
                        preferenceType = "ROOM",
                        values =
                            listOf(
                                ReservationPreferenceValue(value = "NSMOKE"),
                                ReservationPreferenceValue(value = "AWAYLIFT"),
                            ),
                    ),
                )
            val twoRooms =
                bookingWith(
                    room { copy(reservationPreferences = preferences) },
                    room { copy(reservationId = "RSV-MUT-2", reservationPreferences = preferences) },
                )
            val mapping = updateMappings(twoRooms).first()

            jsonPaths(mapping) shouldContainExactly
                listOf(
                    "$.reservations[?(@.hotelId == \"MUTA01\")]",
                    "$.reservations[*].reservationIdList[?(@.id == \"RSV-MUT-1\" && @.type == \"Reservation\")]",
                    "$.reservations[*].preferenceCollection",
                    "$.reservations[*].preferenceCollection[?(@.preferenceType == \"SPECIALS\")]",
                    "$.reservations[*].preferenceCollection[?(@.preferenceType == \"SPECIALS\")]" +
                        ".preference[?(@.preferenceValue == \"HIFLR\")]",
                    "$.reservations[*].preferenceCollection[?(@.preferenceType == \"ROOM\")]",
                    "$.reservations[*].preferenceCollection[?(@.preferenceType == \"ROOM\")]" +
                        ".preference[?(@.preferenceValue == \"NSMOKE\")]",
                    "$.reservations[*].preferenceCollection[?(@.preferenceType == \"ROOM\")]" +
                        ".preference[?(@.preferenceValue == \"AWAYLIFT\")]",
                )
            absentJsonPaths(mapping) shouldContainExactly
                listOf(
                    "$.reservations[1]",
                    "$.reservations[*].reservationIdList[1]",
                    "$.reservations[*].preferenceCollection[?(@.preferenceType == \"SPECIALS\")].preference[1]",
                    "$.reservations[*].preferenceCollection[?(@.preferenceType == \"ROOM\")].preference[2]",
                    "$.reservations[*].preferenceCollection[2]",
                    // The sibling reservation must not be the reservation this body identifies.
                    "$.reservations[*].reservationIdList[?(@.id == \"RSV-MUT-2\")]",
                )
            bodyPatternsOf(mapping).mapNotNull { it.not?.contains }.shouldBeEmpty()
        }

        test("the sibling exclusion rejects the sibling's identity, not a preference value quoting it") {
            // The sibling id's characters can legitimately appear inside a preference value; only
            // the body's reservation identity distinguishes one room's PUT from its sibling's, so
            // the exclusion is structural rather than a whole-body substring match.
            val preferences =
                listOf(
                    ReservationPreferenceCollection(
                        preferenceType = "SPECIALS",
                        values = listOf(ReservationPreferenceValue(value = "NOTE-RSV-MUT-2")),
                    ),
                )
            val twoRooms =
                bookingWith(
                    room { copy(reservationPreferences = preferences) },
                    room { copy(reservationId = "RSV-MUT-2", reservationPreferences = preferences) },
                )
            val mapping = updateMappings(twoRooms).first()

            // The value quoting the sibling id is still required, so such a body still matches.
            jsonPaths(mapping) shouldContain
                "$.reservations[*].preferenceCollection[?(@.preferenceType == \"SPECIALS\")]" +
                ".preference[?(@.preferenceValue == \"NOTE-RSV-MUT-2\")]"
            // A body carrying the sibling id as its reservation identity is the one rejected.
            absentJsonPaths(mapping) shouldContain
                "$.reservations[*].reservationIdList[?(@.id == \"RSV-MUT-2\")]"
            bodyPatternsOf(mapping).mapNotNull { it.not?.contains }.shouldBeEmpty()
        }

        test("the alerts fact pins identity and exactly the stated alerts") {
            val alerts =
                listOf(
                    ReservationAlert(
                        id = "ALERT-1",
                        code = "ECNP",
                        description = "Payment check required",
                        area = "CheckIn",
                        screenNotification = true,
                        printerNotification = false,
                    ),
                )
            val mapping = updateMappings(bookingWith(room { copy(reservationAlertsAfterUpdate = alerts) })).single()

            jsonPaths(mapping) shouldContainExactly
                listOf(
                    "$.reservations[?(@.hotelId == \"MUTA01\")]",
                    "$.reservations[*].reservationIdList[?(@.id == \"RSV-MUT-1\" && @.type == \"Reservation\")]",
                    "$.reservations[*].alerts",
                    // The area is Opera's wire value, not the request's constant name.
                    "$.reservations[*].alerts[?(@.id == \"ALERT-1\" && @.code == \"ECNP\" && " +
                        "@.description == \"Payment check required\" && @.area == \"CheckIn\" && " +
                        "@.screenNotification == true && @.printerNotification == false)]",
                )
            absentJsonPaths(mapping) shouldContainExactly
                listOf(
                    "$.reservations[1]",
                    "$.reservations[*].reservationIdList[1]",
                    "$.reservations[*].alerts[1]",
                )
        }

        test("an explicitly empty alerts fact requires a body whose alerts collection is empty") {
            val mapping =
                updateMappings(bookingWith(room { copy(reservationAlertsAfterUpdate = emptyList()) })).single()

            absentJsonPaths(mapping) shouldContain "$.reservations[*].alerts[0]"
        }

        test("a pinned update keeps the reservation response every update caller reads back") {
            val pinned = bookingWith(room { copy(purposeOfStayAfterUpdate = "Medical") })
            val response = updateMappings(pinned).single().response

            response.status shouldBe 200
            response.jsonBody shouldNotBe null
            response.jsonBody!!
                .jsonObject
                .getValue("reservations")
                .jsonObject
                .getValue("reservation")
                .jsonArray
                .single()
                .jsonObject
                .getValue("reservationIdList")
                .jsonArray
                .map {
                    it.jsonObject
                        .getValue("type")
                        .jsonPrimitive.content
                } shouldContainExactly
                listOf("Reservation", "Confirmation")
        }

        test("an after-update fact changes the update body, not which default stubs are planned") {
            val pinned = bookingWith(room { copy(reservationAlertsAfterUpdate = emptyList()) })

            defaultStubsFor(pinned).map { it.id } shouldContainExactly defaultStubsFor(booking).map { it.id }
        }

        test("two after-update facts against one reservation PUT are rejected per room") {
            shouldThrow<IllegalArgumentException> {
                booking.room.copy(
                    purposeOfStayAfterUpdate = "Medical",
                    ccAgentIdAfterUpdate = "AGENT-9",
                )
            }.message.orEmpty() shouldContainText
                "purposeOfStayAfterUpdate and ccAgentIdAfterUpdate, which pin the same reservation PUT"
        }

        test("the question-and-answer facts count as one update and stay combinable") {
            val questionAndAnswer =
                booking.room.copy(
                    reservationCommentsAfterUpdate = emptyList(),
                    customReferenceAfterUpdate = "CUST-REF-7",
                    characterUdfsAfterUpdate = listOf(CharacterUdf(name = "UDFC11", value = "PO-42")),
                )

            questionAndAnswer.customReferenceAfterUpdate shouldBe "CUST-REF-7"
            shouldThrow<IllegalArgumentException> {
                questionAndAnswer.copy(reservationAlertsAfterUpdate = emptyList())
            }.message.orEmpty() shouldContainText "which pin the same reservation PUT"
        }

        test("a body-pinned after-update fact requires the reservation it pins") {
            shouldThrow<IllegalArgumentException> {
                BookingRoom(externalReferenceAfterUpdate = "BKG-77")
            }.message.orEmpty() shouldBe "bookingRoom.externalReferenceAfterUpdate requires reservationId"
        }

        test("the attached-profiles fact pins identity and exactly the two stated profile links") {
            val mapping =
                updateMappings(
                    bookingWith(
                        room {
                            copy(
                                attachedProfilesAfterUpdate =
                                    ReservationAttachedProfiles(
                                        bookerProfileId = "P-100",
                                        companyProfileId = "2569623",
                                    ),
                            )
                        },
                    ),
                ).single()

            jsonPaths(mapping) shouldContainExactly
                listOf(
                    "$.reservations[?(@.hotelId == \"MUTA01\")]",
                    "$.reservations[*].reservationIdList[?(@.id == \"RSV-MUT-1\" && @.type == \"Reservation\")]",
                    "$.reservations[*].reservationProfiles.reservationProfile" +
                        "[?(@.reservationProfileType == \"ReservationContact\" && @.profileIdList[0].id == \"P-100\")]",
                    "$.reservations[*].reservationProfiles.reservationProfile" +
                        "[?(@.reservationProfileType == \"Company\" && @.profileIdList[0].id == \"2569623\")]",
                )
            absentJsonPaths(mapping) shouldContainExactly
                listOf(
                    "$.reservations[1]",
                    "$.reservations[*].reservationIdList[1]",
                    "$.reservations[*].reservationProfiles.reservationProfile[2]",
                )
        }

        test("a cleared attached-profile link requires an entry carrying no profileIdList member") {
            // The Opera client encodes NON_NULL, so a cleared link omits the member entirely.
            val mapping =
                updateMappings(
                    bookingWith(
                        room {
                            copy(
                                attachedProfilesAfterUpdate =
                                    ReservationAttachedProfiles(bookerProfileId = "P-100", companyProfileId = null),
                            )
                        },
                    ),
                ).single()

            jsonPaths(mapping) shouldContain
                "$.reservations[*].reservationProfiles.reservationProfile" +
                "[?(@.reservationProfileType == \"Company\" && !(@.profileIdList))]"
        }

        test("the booker-email fact pins identity and one ReservationContact entry with the new address") {
            val guest =
                GuestProfile(
                    profileId = "P-100",
                    firstName = "Pat",
                    lastName = "Booker",
                    email = "old@example.com",
                    phone = "07000000000",
                    addressLine = "1 Gate Street",
                    city = "London",
                    postcode = "SW1A 1AA",
                )
            val mapping =
                updateMappings(
                    bookingWith(
                        room { copy(guestProfile = guest, bookerEmailAfterUpdate = "new@example.com") },
                    ),
                ).single()

            jsonPaths(mapping) shouldContainExactly
                listOf(
                    "$.reservations[?(@.hotelId == \"MUTA01\")]",
                    "$.reservations[*].reservationIdList[?(@.id == \"RSV-MUT-1\" && @.type == \"Reservation\")]",
                    "$.reservations[*].reservationProfiles.reservationProfile" +
                        "[?(@.reservationProfileType == \"ReservationContact\" && " +
                        "@.profileIdList[0].id == \"P-100\" && " +
                        "@.profileIdList[0].type == \"Profile\" && " +
                        "@.profile.emails.emailInfo[0].email.emailAddress == \"new@example.com\")]",
                )
            absentJsonPaths(mapping) shouldContainExactly
                listOf(
                    "$.reservations[1]",
                    "$.reservations[*].reservationIdList[1]",
                    "$.reservations[*].reservationProfiles.reservationProfile[1]",
                )
        }

        test("the company-attach fact pins its own hotel-free condition set") {
            val mapping =
                updateMappings(
                    bookingWith(room { copy(attachedCompanyProfileIdAfterUpdate = "2569623") }),
                ).single()

            // The attach body carries no hotelId — the hotel travels in URL and header alone.
            jsonPaths(mapping) shouldContainExactly
                listOf(
                    "$.reservations[*].reservationIdList[?(@.id == \"RSV-MUT-1\" && @.type == \"Reservation\")]",
                    "$.reservations[*].reservationProfiles.reservationProfile" +
                        "[?(@.reservationProfileType == \"Company\")]",
                    "$.reservations[*].reservationProfiles.reservationProfile[*].profileIdList" +
                        "[?(@.id == \"2569623\" && @.type == \"Profile\")]",
                )
            jsonPaths(mapping).none { path -> path.contains("hotelId") } shouldBe true
            absentJsonPaths(mapping) shouldContainExactly
                listOf(
                    "$.reservations[1]",
                    "$.reservations[*].reservationIdList[1]",
                    "$.reservations[*].reservationProfiles.reservationProfile[1]",
                    "$.reservations[*].reservationProfiles.reservationProfile[*].profileIdList[1]",
                )
            mapping.response.status shouldBe 200
            mapping.response.jsonBody shouldNotBe null
        }

        test("the routing-payee fact with an attached company pins the re-pointed first folio") {
            val mapping =
                updateMappings(
                    bookingWith(
                        room {
                            copy(
                                attachedCompanyProfileId = "2569623",
                                routingInstructions =
                                    listOf(
                                        RoutingInstruction(
                                            folioWindowNumber = 2,
                                            payeeProfileId = "5008101",
                                            instructions = listOf(RoutingFolioInstruction(daily = true)),
                                        ),
                                    ),
                                routingPayeeAfterUpdate = true,
                            )
                        },
                    ),
                ).single()

            // No hotelId and no reservationIdList: payee bodies identify the reservation by URL.
            jsonPaths(mapping) shouldContainExactly
                listOf(
                    "$.reservations[?(@.routingInstructions[0].folio.payeeInfo.payeeId.id == \"2569623\" && " +
                        "@.routingInstructions[0].folio.payeeInfo.payeeId.type == \"Profile\")]",
                    "$.reservations[?(@.routingInstructions[0].folio.folioWindowNo == 2)]",
                    "$.reservations[*].routingInstructions[0].folio.instructions[0]",
                )
            jsonPaths(mapping).none { path ->
                path.contains("hotelId") || path.contains("reservationIdList")
            } shouldBe true
            absentJsonPaths(mapping) shouldContainExactly
                listOf(
                    "$.reservations[*].routingInstructions[0].folio.instructions[1]",
                    "$.reservations[*].routingInstructions[1]",
                    "$.reservations[1]",
                )
        }

        test("the routing-payee fact with no attached company requires a body with no reservations member") {
            val mapping =
                updateMappings(
                    bookingWith(
                        room {
                            copy(
                                routingInstructions =
                                    listOf(RoutingInstruction(folioWindowNumber = 2, payeeProfileId = "5008102")),
                                routingPayeeAfterUpdate = true,
                            )
                        },
                    ),
                ).single()

            jsonPaths(mapping).shouldBeEmpty()
            absentJsonPaths(mapping) shouldContainExactly listOf("$.reservations")
        }

        test("the routing-payee fact with an attached company requires a routing instruction") {
            shouldThrow<IllegalArgumentException> {
                room { copy(attachedCompanyProfileId = "2569623", routingPayeeAfterUpdate = true) }
            }.message.orEmpty() shouldContainText
                "routingPayeeAfterUpdate with an attached company requires a routing instruction"
        }

        test("the routing-payee fact refuses an empty folio at construction, which would pin an unsatisfiable body") {
            shouldThrow<IllegalArgumentException> {
                room {
                    copy(
                        attachedCompanyProfileId = "2569623",
                        routingInstructions = listOf(RoutingInstruction(folioWindowNumber = 2)),
                        routingPayeeAfterUpdate = true,
                    )
                }
            }.message.orEmpty() shouldContainText "at least one inner instruction"
        }

        test("the booker-email fact requires the room's own guest profile") {
            shouldThrow<IllegalArgumentException> {
                room { copy(bookerEmailAfterUpdate = "new@example.com") }
            }.message.orEmpty() shouldContainText "requires guestProfile"
        }

        test("the booker-email fact requires every stated guest profile to share one id") {
            shouldThrow<IllegalArgumentException> {
                bookingWith(
                    room { copy(guestProfile = bookerGuest, bookerEmailAfterUpdate = "new@example.com") },
                    room {
                        copy(
                            reservationId = "RSV-MUT-2",
                            guestProfile = bookerGuest.copy(profileId = "P-MUT-2"),
                        )
                    },
                )
            }.message.orEmpty() shouldContainText "share one profileId"
        }

        test("each new after-update fact changes the update body, not which default stubs are planned") {
            // Each pair states the same world with and without the after-update fact, so the
            // comparison isolates the fact itself from its prerequisite render facts.
            listOf<Pair<BookingRoom, BookingRoom>>(
                booking.room to
                    room {
                        copy(attachedProfilesAfterUpdate = ReservationAttachedProfiles(bookerProfileId = "P-100"))
                    },
                booking.room to room { copy(attachedCompanyProfileIdAfterUpdate = "2569623") },
                room {
                    copy(
                        attachedCompanyProfileId = "2569623",
                        routingInstructions =
                            listOf(
                                RoutingInstruction(
                                    folioWindowNumber = 1,
                                    instructions = listOf(RoutingFolioInstruction(daily = true)),
                                ),
                            ),
                    )
                }.let { base -> base to base.copy(routingPayeeAfterUpdate = true) },
                room { copy(guestProfile = bookerGuest) } to
                    room { copy(guestProfile = bookerGuest, bookerEmailAfterUpdate = "new@example.com") },
            ).forEach { (baseRoom, pinnedRoom) ->
                defaultStubsFor(bookingWith(pinnedRoom)).map { it.id } shouldContainExactly
                    defaultStubsFor(bookingWith(baseRoom)).map { it.id }
                bodyPatternsOf(updateMappings(bookingWith(pinnedRoom)).single()).shouldNotBeEmpty()
            }
        }

        test("the new after-update facts join the one-fact-per-room exclusivity guard") {
            listOf<BookingRoom.() -> BookingRoom>(
                { copy(attachedProfilesAfterUpdate = ReservationAttachedProfiles(bookerProfileId = "P-100")) },
                { copy(attachedCompanyProfileIdAfterUpdate = "2569623") },
                {
                    copy(
                        attachedCompanyProfileId = "2569623",
                        routingInstructions =
                            listOf(
                                RoutingInstruction(
                                    folioWindowNumber = 1,
                                    instructions = listOf(RoutingFolioInstruction(daily = true)),
                                ),
                            ),
                        routingPayeeAfterUpdate = true,
                    )
                },
                { copy(guestProfile = bookerGuest, bookerEmailAfterUpdate = "new@example.com") },
            ).forEach { stateFact ->
                shouldThrow<IllegalArgumentException> {
                    room(stateFact).copy(purposeOfStayAfterUpdate = "Medical")
                }.message.orEmpty() shouldContainText "which pin the same reservation PUT"
            }
        }

        test("each new after-update fact requires the reservation it pins") {
            shouldThrow<IllegalArgumentException> {
                BookingRoom(attachedProfilesAfterUpdate = ReservationAttachedProfiles(bookerProfileId = "P-100"))
            }.message.orEmpty() shouldBe "bookingRoom.attachedProfilesAfterUpdate requires reservationId"
            shouldThrow<IllegalArgumentException> {
                BookingRoom(attachedCompanyProfileIdAfterUpdate = "2569623")
            }.message.orEmpty() shouldBe "bookingRoom.attachedCompanyProfileIdAfterUpdate requires reservationId"
            shouldThrow<IllegalArgumentException> {
                BookingRoom(routingPayeeAfterUpdate = true)
            }.message.orEmpty() shouldBe "bookingRoom.routingPayeeAfterUpdate requires reservationId"
            shouldThrow<IllegalArgumentException> {
                BookingRoom(bookerEmailAfterUpdate = "new@example.com")
            }.message.orEmpty() shouldBe "bookingRoom.bookerEmailAfterUpdate requires reservationId"
        }

        test("the new string-valued after-update facts reject blank values") {
            shouldThrow<IllegalArgumentException> {
                room { copy(attachedCompanyProfileIdAfterUpdate = " ") }
            }.message.orEmpty() shouldBe "bookingRoom.attachedCompanyProfileIdAfterUpdate must not be blank"
            shouldThrow<IllegalArgumentException> {
                room { copy(bookerEmailAfterUpdate = " ") }
            }.message.orEmpty() shouldBe "bookingRoom.bookerEmailAfterUpdate must not be blank"
            shouldThrow<IllegalArgumentException> {
                ReservationAttachedProfiles(bookerProfileId = " ")
            }.message.orEmpty() shouldBe "reservationAttachedProfiles.bookerProfileId must not be blank"
            shouldThrow<IllegalArgumentException> {
                ReservationAttachedProfiles(companyProfileId = " ")
            }.message.orEmpty() shouldBe "reservationAttachedProfiles.companyProfileId must not be blank"
        }
    })
