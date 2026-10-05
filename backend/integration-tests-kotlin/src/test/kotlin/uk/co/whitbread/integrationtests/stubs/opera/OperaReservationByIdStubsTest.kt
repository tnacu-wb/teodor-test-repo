package uk.co.whitbread.integrationtests.stubs.opera

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldNotBeEmpty
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import uk.co.whitbread.integrationtests.stubs.defaultStubsFor
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Company
import uk.co.whitbread.integrationtests.testkit.model.CompanyAddress
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile
import uk.co.whitbread.integrationtests.testkit.model.Hotel
import uk.co.whitbread.integrationtests.testkit.model.PreRegistration
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationComment
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.model.RoutingFolioInstruction
import uk.co.whitbread.integrationtests.testkit.model.RoutingInstruction
import uk.co.whitbread.integrationtests.testkit.model.SelectedPackage
import java.time.LocalDate
import io.kotest.matchers.string.shouldContain as shouldContainText

/**
 * Characterizes the reservation-lookup response blocks Opera always reports and the
 * adapter's mappers dereference unguarded: attached reservation profiles, the additional
 * guest-info block, and the guest name type's exact wire value.
 */
class OperaReservationByIdStubsTest :
    FunSpec({
        val arrival: LocalDate = LocalDate.of(2026, 9, 1)
        val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)
        val booking =
            Booking(
                hotels =
                    listOf(
                        Hotel(
                            hotelId = "BYID01",
                            shortId = "by-id-hotel",
                            name = "By Id Hotel",
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
                            reservationId = "RSV-BYID-1",
                            roomType = rate.roomType,
                            adults = rate.adults,
                            status = ReservationStatus.RESERVED,
                            guestProfile =
                                GuestProfile(
                                    profileId = "PROF-BYID-1",
                                    firstName = "Amelia",
                                    lastName = "Wright",
                                    email = "amelia.wright@test.com",
                                    phone = "+447700900001",
                                    addressLine = "1 High Street",
                                    city = "London",
                                    postcode = "SW1A 1AA",
                                ),
                        ),
                    ),
            )

        fun reservationBody() =
            getReservation(booking, booking.room)
                .mappings
                .first()
                .response.jsonBody!!
                .jsonObject
                .getValue("reservations")
                .jsonObject
                .getValue("reservation")
                .jsonArray
                .single()
                .jsonObject

        test("reservation lookup always reports the guest's attached reservation profile") {
            val profile =
                reservationBody()
                    .getValue("reservationProfiles")
                    .jsonObject
                    .getValue("reservationProfile")
                    .jsonArray
                    .single()
                    .jsonObject

            profile.getValue("reservationProfileType").jsonPrimitive.content shouldBe "Guest"
            profile
                .getValue("profileIdList")
                .jsonArray
                .single()
                .jsonObject
                .getValue("id")
                .jsonPrimitive
                .content shouldBe "PROF-BYID-1"
        }

        test("reservation lookup always reports the additional guest-info block") {
            reservationBody().containsKey("additionalGuestInfo") shouldBe true
        }

        fun reservationBodyFor(room: BookingRoom) =
            getReservation(booking.copy(rooms = listOf(room)), room)
                .mappings
                .first()
                .response.jsonBody!!
                .jsonObject
                .getValue("reservations")
                .jsonObject
                .getValue("reservation")
                .jsonArray
                .single()
                .jsonObject

        fun roomRatesFor(rateBooking: Booking) =
            getReservation(rateBooking, rateBooking.room)
                .mappings
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
                .getValue("roomStay")
                .jsonObject
                .getValue("roomRates")
                .jsonArray
                .map { it.jsonObject }

        fun roomRatesFor(rate: Rate) =
            roomRatesFor(
                booking.copy(hotels = listOf(booking.hotel.copy(availableRates = listOf(rate)))),
            )

        test("reservation lookup always reports the comments block, empty when the room has none") {
            reservationBody().getValue("comments").jsonArray.size shouldBe 0
        }

        test("reservation comments carry their data-driven type at both Opera levels") {
            val comment =
                reservationBodyFor(
                    booking.room.copy(
                        reservationComments =
                            listOf(
                                ReservationComment(
                                    title = "Purchase order?|Purchase order",
                                    commentId = "9001",
                                    type = "PUR_ORD_QNA",
                                ),
                            ),
                    ),
                ).getValue("comments").jsonArray.single().jsonObject

            comment.getValue("id").jsonPrimitive.content shouldBe "9001"
            comment.getValue("type").jsonPrimitive.content shouldBe "PUR_ORD_QNA"
            val nestedComment =
                comment
                    .getValue("comment")
                    .jsonObject
            nestedComment.getValue("type").jsonPrimitive.content shouldBe "PUR_ORD_QNA"
            nestedComment.getValue("commentTitle").jsonPrimitive.content shouldBe "Purchase order?|Purchase order"
        }

        fun nestedCommentsFor(comments: List<ReservationComment>) =
            reservationBodyFor(booking.room.copy(reservationComments = comments))
                .getValue("comments")
                .jsonArray
                .map { it.jsonObject.getValue("comment").jsonObject }

        test("a comment stating no audit facts takes deterministic stamps derived from arrival") {
            val comments =
                nestedCommentsFor(
                    listOf(
                        ReservationComment(title = "BUSINESS NOTES", text = "First note"),
                        ReservationComment(title = "BUSINESS NOTES", text = "Second note", commentId = "1002"),
                    ),
                )

            // Opera's only fully parsed timestamp shape; an ISO instant is silently truncated.
            comments[0].getValue("createDateTime").jsonPrimitive.content shouldBe "2026-09-01 08:00:00"
            comments[0].getValue("lastModifyDateTime").jsonPrimitive.content shouldBe "2026-09-01 09:00:00"
            comments[0].getValue("creatorId").jsonPrimitive.content shouldBe "OPERAUSER"
            comments[0].getValue("lastModifierId").jsonPrimitive.content shouldBe "OPERAUSER"
            // Distinct per comment, so a consumer ordering by last modification has a stable order.
            comments[1].getValue("createDateTime").jsonPrimitive.content shouldBe "2026-09-01 08:01:00"
            comments[1].getValue("lastModifyDateTime").jsonPrimitive.content shouldBe "2026-09-01 09:01:00"
        }

        test("a comment carries exactly the fields Opera reports on one, and no others") {
            nestedCommentsFor(listOf(ReservationComment(title = "BUSINESS NOTES")))
                .single()
                .keys
                .toList() shouldContainExactly
                listOf(
                    "type",
                    "commentTitle",
                    "text",
                    "createDateTime",
                    "creatorId",
                    "lastModifyDateTime",
                    "lastModifierId",
                )
        }

        test("the same comment on two reservations is stamped in a stable order across them") {
            val rooms =
                listOf("RSV-BYID-1", "RSV-BYID-2").map { reservationId ->
                    booking.room.copy(
                        reservationId = reservationId,
                        reservationComments = listOf(ReservationComment(title = "BUSINESS NOTES")),
                    )
                }

            getReservations(booking.copy(rooms = rooms), rooms)
                .mappings
                // One mapping per known caller fetch-instruction variant; all share the room's body.
                .distinctBy { mapping -> mapping.request.urlPath }
                .map { mapping ->
                    mapping.response.jsonBody!!
                        .jsonObject
                        .getValue("reservations")
                        .jsonObject
                        .getValue("reservation")
                        .jsonArray
                        .single()
                        .jsonObject
                        .getValue("comments")
                        .jsonArray
                        .single()
                        .jsonObject
                        .getValue("comment")
                        .jsonObject
                        .getValue("lastModifyDateTime")
                        .jsonPrimitive
                        .content
                }.shouldContainExactly("2026-09-01 09:00:00", "2026-09-01 09:10:00")
        }

        test("a comment stating audit facts reports exactly them") {
            val comment =
                nestedCommentsFor(
                    listOf(
                        ReservationComment(
                            title = "BUSINESS NOTES",
                            createdOn = "2026-08-20 11:22:33",
                            createdBy = "CREATOR1",
                            modifiedOn = "2026-08-21 14:15:16",
                            modifiedBy = "MODIFIER1",
                        ),
                    ),
                ).single()

            comment.getValue("createDateTime").jsonPrimitive.content shouldBe "2026-08-20 11:22:33"
            comment.getValue("creatorId").jsonPrimitive.content shouldBe "CREATOR1"
            comment.getValue("lastModifyDateTime").jsonPrimitive.content shouldBe "2026-08-21 14:15:16"
            comment.getValue("lastModifierId").jsonPrimitive.content shouldBe "MODIFIER1"
        }

        test("an audit stamp must be stated in the only shape the consumer parses in full") {
            shouldThrow<IllegalArgumentException> {
                ReservationComment(title = "BUSINESS NOTES", modifiedOn = "2026-09-10T09:10:00Z")
            }.message.orEmpty() shouldBe
                "reservationComment.modifiedOn must be an Opera timestamp, e.g. 2026-09-10 09:10:00"
        }

        test("a derived modification stamp never precedes an explicitly stated creation") {
            val comment =
                nestedCommentsFor(
                    listOf(
                        // createdOn is after the derived arrival-day 09:00 modification lane.
                        ReservationComment(title = "BUSINESS NOTES", createdOn = "2026-09-02 10:00:00"),
                    ),
                ).single()

            comment.getValue("createDateTime").jsonPrimitive.content shouldBe "2026-09-02 10:00:00"
            // Clamped to createdOn instead of the impossible earlier derived stamp.
            comment.getValue("lastModifyDateTime").jsonPrimitive.content shouldBe "2026-09-02 10:00:00"
        }

        test("a derived audit stamp requires the room to be one of the Booking's rooms") {
            shouldThrow<IllegalArgumentException> {
                getReservation(
                    booking,
                    booking.room.copy(
                        reservationId = "9999999",
                        reservationComments = listOf(ReservationComment(title = "BUSINESS NOTES")),
                    ),
                )
            }.message.orEmpty() shouldContainText "is not in Booking.rooms"
        }

        test("a room cannot carry more unstamped comments than the per-room stamp lane holds") {
            val unstamped = List(11) { index -> ReservationComment(title = "NOTE $index", commentId = "9$index") }

            shouldThrow<IllegalArgumentException> {
                reservationBodyFor(booking.room.copy(reservationComments = unstamped))
            }.message.orEmpty() shouldContainText "unstamped"
        }

        fun bookingWithPackageUpdate(
            selectedPackages: List<SelectedPackage> = emptyList(),
            selectedPackagesAfterUpdate: List<SelectedPackage>,
        ): Booking =
            booking.copy(
                rooms =
                    booking.rooms.map { room ->
                        room.copy(
                            selectedPackages = selectedPackages,
                            selectedPackagesAfterUpdate = selectedPackagesAfterUpdate,
                        )
                    },
            )

        test("a zero-quantity package is a read-only fact and cannot be added to a reservation") {
            val zeroQuantityAdd =
                bookingWithPackageUpdate(
                    selectedPackagesAfterUpdate = listOf(SelectedPackage(code = "BRKFST", quantity = 0)),
                )

            shouldThrow<IllegalArgumentException> {
                defaultStubsFor(zeroQuantityAdd)
            }.message.orEmpty() shouldContainText "read-only facts"
        }

        test("a package update fact swaps the permissive PUT for package-pinned mappings") {
            val added = SelectedPackage(code = "BFADBF", quantity = 2)
            val removed = SelectedPackage(code = "CARPRK")
            val update =
                bookingWithPackageUpdate(
                    selectedPackages = listOf(removed),
                    selectedPackagesAfterUpdate = listOf(added),
                )

            val mappings =
                defaultStubsFor(update)
                    .single { it.id == OPERA_RESERVATION_PUT_STUB_ID }
                    .mappings

            // Basket-create, removal, and addition bodies; no permissive any-body mapping remains,
            // so a PUT carrying the wrong packages has nothing to fall through to.
            mappings.size shouldBe 3
            mappings.forEach { mapping ->
                mapping.request.method shouldBe "PUT"
                (mapping.request.bodyPatterns ?: emptyList()).shouldNotBeEmpty()
            }
        }

        test("a package update fact restating the selected packages is rejected") {
            val unchanged = SelectedPackage(code = "CARPRK")
            val noOpUpdate =
                bookingWithPackageUpdate(
                    selectedPackages = listOf(unchanged),
                    selectedPackagesAfterUpdate = listOf(unchanged),
                )

            shouldThrow<IllegalArgumentException> {
                defaultStubsFor(noOpUpdate)
            }.message.orEmpty() shouldContainText "omit it when no package changes"
        }

        test("a package update fact cannot restate a selected package with a different quantity") {
            val requantified =
                bookingWithPackageUpdate(
                    selectedPackages = listOf(SelectedPackage(code = "CARPRK", quantity = 1)),
                    selectedPackagesAfterUpdate = listOf(SelectedPackage(code = "CARPRK", quantity = 2)),
                )

            shouldThrow<IllegalArgumentException> {
                defaultStubsFor(requantified)
            }.message.orEmpty() shouldContainText "different quantity"
        }

        test("a room stating no absence fact reports the full reservation body") {
            val body = reservationBody()

            body.containsKey("reservationIdList") shouldBe true
            body.containsKey("roomStay") shouldBe true
        }

        test("a reservation Opera holds nothing for reports an envelope with no reservation member") {
            val envelope =
                getReservation(
                    booking,
                    booking.room.copy(reservationAbsentInOpera = true),
                ).mappings
                    .first()
                    .response.jsonBody!!
                    .jsonObject

            // The exact Opera "nothing here" shape: reservations present, reservation member absent.
            envelope.keys.toList() shouldContainExactly listOf("reservations")
            envelope
                .getValue("reservations")
                .jsonObject.keys
                .shouldBeEmpty()
        }

        test("the absence fact changes only the read body, not which default stubs are planned") {
            val absent =
                booking.copy(rooms = booking.rooms.map { it.copy(reservationAbsentInOpera = true) })

            defaultStubsFor(absent).map { it.id } shouldContainExactly
                defaultStubsFor(booking).map { it.id }
            defaultStubsFor(absent)
                .single { it.id == OPERA_RESERVATION_GET_STUB_ID }
                .mappings
                .first()
                .response
                .jsonBody!!
                .jsonObject
                .getValue("reservations")
                .jsonObject
                .keys
                .shouldBeEmpty()
        }

        test("the absence fact requires the reservation id whose absence it states") {
            shouldThrow<IllegalArgumentException> {
                BookingRoom(reservationAbsentInOpera = true)
            }.message.orEmpty() shouldBe "bookingRoom.reservationAbsentInOpera requires reservationId"
        }

        fun attachedProfilesFor(room: BookingRoom) =
            reservationBodyFor(room)
                .getValue("reservationProfiles")
                .jsonObject
                .getValue("reservationProfile")
                .jsonArray
                .map { it.jsonObject }

        test("a guest who is not the reservation contact is the reservation's only attached profile") {
            attachedProfilesFor(booking.room)
                .map { it.getValue("reservationProfileType").jsonPrimitive.content }
                .shouldContainExactly("Guest")
        }

        test("a guest who is also the reservation contact adds a second profile of that type") {
            val profiles =
                attachedProfilesFor(
                    booking.room.copy(
                        guestProfile = requireNotNull(booking.room.guestProfile).copy(reservationContact = true),
                    ),
                )

            // The Guest entry stays: Opera never drops it and consumers read both types.
            profiles
                .map { it.getValue("reservationProfileType").jsonPrimitive.content }
                .shouldContainExactly("Guest", "ReservationContact")
            profiles.forEach { profile ->
                profile
                    .getValue("profileIdList")
                    .jsonArray
                    .single()
                    .jsonObject
                    .getValue("id")
                    .jsonPrimitive
                    .content shouldBe "PROF-BYID-1"
            }
        }

        val attachedCompany =
            Company(
                name = "Attached Company",
                corpId = "1370",
                companyId = "2569623",
                telephoneNumber = "02079460000",
                address = CompanyAddress(addressLine1 = "1 Synthetic Street", postalCode = "SW1A 1AA"),
            )

        fun reservationBodyFor(
            bodyBooking: Booking,
            room: BookingRoom,
        ) = getReservation(bodyBooking, room)
            .mappings
            .first()
            .response.jsonBody!!
            .jsonObject
            .getValue("reservations")
            .jsonObject
            .getValue("reservation")
            .jsonArray
            .single()
            .jsonObject

        test("a room stating an attached company appends a Company reservation profile") {
            val room = booking.room.copy(attachedCompanyProfileId = attachedCompany.companyId)
            val profiles =
                reservationBodyFor(
                    booking.copy(companies = listOf(attachedCompany), rooms = listOf(room)),
                    room,
                ).getValue("reservationProfiles")
                    .jsonObject
                    .getValue("reservationProfile")
                    .jsonArray
                    .map { it.jsonObject }

            profiles
                .map { it.getValue("reservationProfileType").jsonPrimitive.content }
                .shouldContainExactly("Guest", "Company")
            val companyProfileId =
                profiles
                    .last()
                    .getValue("profileIdList")
                    .jsonArray
                    .single()
                    .jsonObject
            companyProfileId.getValue("id").jsonPrimitive.content shouldBe "2569623"
            companyProfileId.getValue("type").jsonPrimitive.content shouldBe "Profile"
        }

        test("an attached company must name a company the booking knows") {
            shouldThrow<IllegalArgumentException> {
                booking.copy(
                    rooms = listOf(booking.room.copy(attachedCompanyProfileId = "9999999")),
                )
            }.message.orEmpty() shouldContainText "must name a Company in booking.companies"
        }

        test("an attached company profile id must not be blank") {
            shouldThrow<IllegalArgumentException> {
                BookingRoom(attachedCompanyProfileId = " ")
            }.message.orEmpty() shouldBe "bookingRoom.attachedCompanyProfileId must not be blank"
        }

        test("every reservation guest's profile reports Opera's profileType Guest") {
            reservationBody()
                .getValue("reservationGuests")
                .jsonArray
                .single()
                .jsonObject
                .getValue("profileInfo")
                .jsonObject
                .getValue("profile")
                .jsonObject
                .getValue("profileType")
                .jsonPrimitive
                .content shouldBe "Guest"
        }

        test("the update acknowledgement keeps its guest shape without a profile type") {
            putReservation(booking, booking.room)
                .mappings
                .single()
                .response.jsonBody!!
                .jsonObject
                .getValue("reservations")
                .jsonObject
                .getValue("reservation")
                .jsonArray
                .single()
                .jsonObject
                .getValue("reservationGuests")
                .jsonArray
                .single()
                .jsonObject
                .getValue("profileInfo")
                .jsonObject
                .getValue("profile")
                .jsonObject
                .containsKey("profileType") shouldBe false
        }

        test("an accompanying guest is a second non-primary reservation guest, never a profile") {
            val body =
                reservationBodyFor(
                    booking.room.copy(
                        accompanyingGuestProfile =
                            requireNotNull(booking.room.guestProfile).copy(
                                profileId = "PROF-BYID-2",
                                firstName = "Second",
                                lastName = "Guest",
                            ),
                    ),
                )

            val guests = body.getValue("reservationGuests").jsonArray.map { it.jsonObject }
            guests
                .map { it.getValue("primary").jsonPrimitive.content }
                .shouldContainExactly("true", "false")
            val accompanying = guests.last().getValue("profileInfo").jsonObject
            accompanying
                .getValue("profileIdList")
                .jsonArray
                .single()
                .jsonObject
                .getValue("id")
                .jsonPrimitive
                .content shouldBe "PROF-BYID-2"
            accompanying
                .getValue("profile")
                .jsonObject
                .getValue("profileType")
                .jsonPrimitive
                .content shouldBe "Guest"
            // The accompanying guest is deliberately not an attached reservation profile:
            // that is the world in which its profile id survives the read-back filter.
            body
                .getValue("reservationProfiles")
                .jsonObject
                .getValue("reservationProfile")
                .jsonArray
                .single()
                .jsonObject
                .getValue("reservationProfileType")
                .jsonPrimitive
                .content shouldBe "Guest"
        }

        test("the company and accompanying-guest facts change only the read body, not the planned stubs") {
            val withCompanies = booking.copy(companies = listOf(attachedCompany))
            val stated =
                withCompanies.copy(
                    rooms =
                        withCompanies.rooms.map {
                            it.copy(
                                attachedCompanyProfileId = attachedCompany.companyId,
                                accompanyingGuestProfile =
                                    requireNotNull(it.guestProfile).copy(profileId = "PROF-BYID-2"),
                            )
                        },
                )

            defaultStubsFor(stated).map { it.id } shouldContainExactly
                defaultStubsFor(withCompanies).map { it.id }
        }

        fun instructionTimeSpanFor(instruction: RoutingFolioInstruction) =
            reservationBodyFor(
                booking.room.copy(
                    routingInstructions =
                        listOf(
                            RoutingInstruction(folioWindowNumber = 1, instructions = listOf(instruction)),
                        ),
                ),
            ).getValue("routingInstructions")
                .jsonArray
                .single()
                .jsonObject
                .getValue("folio")
                .jsonObject
                .getValue("instructions")
                .jsonArray
                .single()
                .jsonObject
                .getValue("duration")
                .jsonObject
                .getValue("timeSpan")
                .jsonObject

        test("a daily instruction without an offset is routed from the booking's arrival date") {
            instructionTimeSpanFor(RoutingFolioInstruction(daily = true))
                .getValue("startDate")
                .jsonPrimitive
                .content shouldBe arrival.toString()
        }

        test("a daily instruction stating an offset is routed from its own start date") {
            val timeSpan = instructionTimeSpanFor(RoutingFolioInstruction(daily = true, startDateOffset = 1))

            timeSpan.getValue("startDate").jsonPrimitive.content shouldBe arrival.plusDays(1).toString()
            // Only the start date moves; the span still ends with the stay.
            timeSpan
                .getValue("endDate")
                .jsonPrimitive
                .content shouldBe booking.departure.toString()
        }

        test("an offset requires the daily instruction shape that carries a time span at all") {
            shouldThrow<IllegalArgumentException> {
                RoutingFolioInstruction(daily = false, startDateOffset = 1)
            }.message.orEmpty() shouldBe
                "routingFolioInstruction.startDateOffset requires a daily instruction"
        }

        test("nightly room rates are discountable by default") {
            roomRatesFor(rate)
                .map { roomRate -> roomRate.getValue("discountAllowed").jsonPrimitive.content }
                .shouldContainExactly("true", "true")
        }

        test("nightly room rates carry an explicit non-discountable fact") {
            roomRatesFor(rate.copy(discountAllowed = false))
                .map { roomRate -> roomRate.getValue("discountAllowed").jsonPrimitive.content }
                .shouldContainExactly("false", "false")
        }

        test("the booked rate plan selects the exact catalogue rate when room facts match several") {
            val decoyRate =
                rate.copy(
                    ratePlan = "FLEX",
                    nightlyRate = 1.0,
                    discountAllowed = false,
                )
            val bookedRate =
                rate.copy(
                    ratePlan = "SEMIFLEX",
                    nightlyRate = 79.0,
                    discountAllowed = true,
                )
            val rateBooking =
                booking.copy(
                    hotels = listOf(booking.hotel.copy(availableRates = listOf(decoyRate, bookedRate))),
                    rooms = listOf(booking.room.copy(ratePlan = bookedRate.ratePlan)),
                )

            roomRatesFor(rateBooking).forEach { roomRate ->
                roomRate.getValue("ratePlanCode").jsonPrimitive.content shouldBe "SEMIFLEX"
                roomRate.getValue("discountAllowed").jsonPrimitive.content shouldBe "true"
                roomRate
                    .getValue("rates")
                    .jsonObject
                    .getValue("rate")
                    .jsonArray
                    .single()
                    .jsonObject
                    .getValue("base")
                    .jsonObject
                    .getValue("amountBeforeTax")
                    .jsonPrimitive
                    .content shouldBe "79.0"
            }
        }

        test("matching catalogue rates require a booked rate plan instead of depending on list order") {
            val ambiguousBooking =
                booking.copy(
                    hotels =
                        listOf(
                            booking.hotel.copy(
                                availableRates =
                                    listOf(
                                        rate.copy(ratePlan = "FLEX"),
                                        rate.copy(ratePlan = "SEMIFLEX"),
                                    ),
                            ),
                        ),
                )

            shouldThrow<IllegalArgumentException> {
                getReservation(ambiguousBooking, ambiguousBooking.room)
            }.message.orEmpty() shouldBe
                "bookingRoom.ratePlan must identify at most one available rate for " +
                "roomType=LOWDBL, adults=2, children=0; matched rate plans=FLEX, SEMIFLEX"
        }

        test("a booked rate plan must still identify a unique catalogue rate") {
            val ambiguousBooking =
                booking.copy(
                    hotels =
                        listOf(
                            booking.hotel.copy(
                                availableRates =
                                    listOf(
                                        rate.copy(nightlyRate = 59.0),
                                        rate.copy(nightlyRate = 79.0),
                                    ),
                            ),
                        ),
                    rooms = listOf(booking.room.copy(ratePlan = rate.ratePlan)),
                )

            shouldThrow<IllegalArgumentException> {
                getReservation(ambiguousBooking, ambiguousBooking.room)
            }.message.orEmpty() shouldBe
                "bookingRoom.ratePlan must identify at most one available rate for " +
                "roomType=LOWDBL, adults=2, children=0; matched rate plans=SEMIFLEX, SEMIFLEX"
        }

        test("a reservation without the pre-registration fact reports no pre-registration blocks") {
            val body = reservationBody()
            body.containsKey("preRegistered") shouldBe false
            body.containsKey("attachments") shouldBe false
            body.containsKey("alerts") shouldBe false
        }

        test("a pre-registered reservation reports its reg-card attachment and Pre-Check-In alert") {
            val body =
                reservationBodyFor(
                    booking.room.copy(
                        preRegistration =
                            PreRegistration(
                                regCardAttachmentId = "ATT-1",
                                preCheckInAlertId = "ALERT-1",
                            ),
                    ),
                )

            body.getValue("preRegistered").jsonPrimitive.content shouldBe "true"
            val attachment =
                body
                    .getValue("attachments")
                    .jsonArray
                    .single()
                    .jsonObject
            attachment.getValue("id").jsonPrimitive.content shouldBe "ATT-1"
            attachment
                .getValue("fileName")
                .jsonPrimitive
                .content shouldBe "REG_RES_ATT-1.pdf"
            val alert =
                body
                    .getValue("alerts")
                    .jsonArray
                    .single()
                    .jsonObject
            alert.getValue("id").jsonPrimitive.content shouldBe "ALERT-1"
            alert.getValue("code").jsonPrimitive.content shouldBe "Reservation"
            alert.getValue("description").jsonPrimitive.content shouldBe "Pre-Check-In alert"
        }

        test("a reservation whose pre-check-in is completed reports the de-reg-card alert wording") {
            val alert =
                reservationBodyFor(
                    booking.room.copy(
                        preRegistration =
                            PreRegistration(
                                preCheckInAlertId = "ALERT-1",
                                deRegCardCompleted = true,
                            ),
                    ),
                ).getValue("alerts").jsonArray.single().jsonObject

            alert.getValue("id").jsonPrimitive.content shouldBe "ALERT-1"
            alert.getValue("code").jsonPrimitive.content shouldBe "Reservation"
            // The adapter equality-matches this description to derive deRegCardCompleted.
            alert
                .getValue("description")
                .jsonPrimitive
                .content shouldBe "Do not print registration card, Pre-Check-In completed"
        }

        test("the completion fact alone reports the alert under a reservation-derived id") {
            val alert =
                reservationBodyFor(
                    booking.room.copy(preRegistration = PreRegistration(deRegCardCompleted = true)),
                ).getValue("alerts").jsonArray.single().jsonObject

            alert.getValue("id").jsonPrimitive.content shouldBe "ALERT-DEREG-RSV-BYID-1"
            alert.getValue("code").jsonPrimitive.content shouldBe "Reservation"
            alert
                .getValue("description")
                .jsonPrimitive
                .content shouldBe "Do not print registration card, Pre-Check-In completed"
        }

        test("a pre-registered reservation without the completion fact reports no alert at all") {
            reservationBodyFor(
                booking.room.copy(preRegistration = PreRegistration(regCardAttachmentId = "ATT-1")),
            ).containsKey("alerts") shouldBe false
        }

        test("the de-reg-card fact does not change which default stubs are planned, only the read body") {
            val deRegCardCompleted =
                booking.copy(
                    rooms =
                        booking.rooms.map {
                            it.copy(preRegistration = PreRegistration(deRegCardCompleted = true))
                        },
                )

            defaultStubsFor(deRegCardCompleted).map { it.id } shouldContainExactly
                defaultStubsFor(booking).map { it.id }
            defaultStubsFor(deRegCardCompleted)
                .single { it.id == OPERA_RESERVATION_GET_STUB_ID }
                .mappings
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
                .getValue("alerts")
                .jsonArray
                .single()
                .jsonObject
                .getValue("description")
                .jsonPrimitive
                .content shouldBe "Do not print registration card, Pre-Check-In completed"
        }

        test("a routing-instruction folio reports its payee identity and daily instruction span") {
            val folio =
                reservationBodyFor(
                    booking.room.copy(
                        routingInstructions =
                            listOf(
                                RoutingInstruction(
                                    folioWindowNumber = 2,
                                    payeeProfileId = "500777",
                                    instructions = listOf(RoutingFolioInstruction(daily = true)),
                                ),
                            ),
                    ),
                ).getValue("routingInstructions")
                    .jsonArray
                    .single()
                    .jsonObject
                    .getValue("folio")
                    .jsonObject

            folio.getValue("folioWindowNo").jsonPrimitive.content shouldBe "2"
            folio
                .getValue("payeeInfo")
                .jsonObject
                .getValue("payeeId")
                .jsonObject
                .getValue("id")
                .jsonPrimitive
                .content shouldBe "500777"
            val duration =
                folio
                    .getValue("instructions")
                    .jsonArray
                    .single()
                    .jsonObject
                    .getValue("duration")
                    .jsonObject
            duration.getValue("daily").jsonPrimitive.content shouldBe "true"
            duration.getValue("saturday").jsonPrimitive.content shouldBe "true"
            duration
                .getValue("timeSpan")
                .jsonObject
                .getValue("startDate")
                .jsonPrimitive
                .content shouldBe arrival.toString()
        }

        test("a folio without instruction facts still reports an empty instructions list") {
            val folio =
                reservationBodyFor(
                    booking.room.copy(
                        routingInstructions = listOf(RoutingInstruction(folioWindowNumber = 1)),
                    ),
                ).getValue("routingInstructions")
                    .jsonArray
                    .single()
                    .jsonObject
                    .getValue("folio")
                    .jsonObject

            folio.getValue("instructions").jsonArray.size shouldBe 0
        }

        test("a non-daily instruction reports optional routing identifiers without a time span") {
            val instruction =
                reservationBodyFor(
                    booking.room.copy(
                        routingInstructions =
                            listOf(
                                RoutingInstruction(
                                    folioWindowNumber = 1,
                                    instructions =
                                        listOf(
                                            RoutingFolioInstruction(
                                                daily = false,
                                                creditLimit = "250.00",
                                                routingLinkId = "RL-1",
                                                transactionCodes = listOf("1000"),
                                                billingCodes = listOf("BC1"),
                                            ),
                                        ),
                                ),
                            ),
                    ),
                ).getValue("routingInstructions")
                    .jsonArray
                    .single()
                    .jsonObject
                    .getValue("folio")
                    .jsonObject
                    .getValue("instructions")
                    .jsonArray
                    .single()
                    .jsonObject

            instruction
                .getValue("duration")
                .jsonObject
                .containsKey("timeSpan") shouldBe false
            instruction.getValue("creditLimit").jsonPrimitive.content shouldBe "250.00"
            instruction
                .getValue("routingLinkId")
                .jsonObject
                .getValue("id")
                .jsonPrimitive
                .content shouldBe "RL-1"
            instruction
                .getValue("transactionCodes")
                .jsonArray
                .single()
                .jsonObject
                .getValue("transactionCode")
                .jsonPrimitive
                .content shouldBe "1000"
            instruction
                .getValue("billingInstructions")
                .jsonArray
                .single()
                .jsonObject
                .getValue("billingCode")
                .jsonPrimitive
                .content shouldBe "BC1"
        }

        fun packageBlockFor(selectedPackage: SelectedPackage) =
            reservationBodyFor(
                booking.room.copy(selectedPackages = listOf(selectedPackage)),
            ).getValue("reservationPackages")
                .jsonArray
                .single()
                .jsonObject

        test("a package consumed in no quantity still posts its schedule entry, at quantity zero") {
            val schedule =
                packageBlockFor(SelectedPackage(code = "BREAKFAST", quantity = 0))
                    .getValue("scheduleList")
                    .jsonArray
                    .single()
                    .jsonObject

            schedule.getValue("totalQuantity").jsonPrimitive.content shouldBe "0"
        }

        test("a package's default quantity is unchanged by the zero allowance") {
            packageBlockFor(SelectedPackage(code = "BREAKFAST"))
                .getValue("scheduleList")
                .jsonArray
                .single()
                .jsonObject
                .getValue("totalQuantity")
                .jsonPrimitive
                .content shouldBe "1"
        }

        test("a package quantity still cannot be negative") {
            shouldThrow<IllegalArgumentException> {
                SelectedPackage(code = "BREAKFAST", quantity = -1)
            }.message.orEmpty() shouldBe "selected package quantity must not be negative"
        }

        test("a package without a unit price posts on the rate line, which prices it at zero downstream") {
            val packageBlock = packageBlockFor(SelectedPackage(code = "BREAKFAST"))

            // ohip-adapter only carries a package price when addToRate is false *and*
            // printSeparateLine is true, so this shape reports the schedule amount but zero price.
            packageBlock
                .getValue("packageHeaderType")
                .jsonObject
                .getValue("postingAttributes")
                .jsonObject
                .getValue("printSeparateLine")
                .jsonPrimitive
                .content shouldBe "false"
            packageBlock
                .getValue("scheduleList")
                .jsonArray
                .single()
                .jsonObject
                .getValue("unitPrice")
                .jsonPrimitive
                .content shouldBe "12.00"
        }

        test("a priced package posts on its own line at that price for every consumption date") {
            val packageBlock =
                packageBlockFor(SelectedPackage(code = "CITYTAX", quantity = 2, unitPrice = 15.5))

            packageBlock.getValue("packageCode").jsonPrimitive.content shouldBe "CITYTAX"
            packageBlock
                .getValue("packageHeaderType")
                .jsonObject
                .getValue("postingAttributes")
                .jsonObject
                .getValue("printSeparateLine")
                .jsonPrimitive
                .content shouldBe "true"
            val schedule =
                packageBlock
                    .getValue("scheduleList")
                    .jsonArray
                    .single()
                    .jsonObject
            schedule.getValue("unitPrice").jsonPrimitive.content shouldBe "15.50"
            schedule.getValue("computedResvPrice").jsonPrimitive.content shouldBe "15.50"
            schedule.getValue("totalQuantity").jsonPrimitive.content shouldBe "2"
            schedule.getValue("consumptionDate").jsonPrimitive.content shouldBe arrival.toString()
        }

        test("a reservation without the held fact reports a guarantee that is not on hold") {
            reservationBody()
                .getValue("roomStay")
                .jsonObject
                .getValue("guarantee")
                .jsonObject
                .getValue("onHold")
                .jsonPrimitive
                .content shouldBe "false"
        }

        test("a reservation held in Opera reports its guarantee on hold") {
            reservationBodyFor(booking.room.copy(heldInOpera = true))
                .getValue("roomStay")
                .jsonObject
                .getValue("guarantee")
                .jsonObject
                .getValue("onHold")
                .jsonPrimitive
                .content shouldBe "true"
        }

        test("the held fact does not change which default stubs are planned, only the read body") {
            val held = booking.copy(rooms = booking.rooms.map { it.copy(heldInOpera = true) })

            defaultStubsFor(held).map { it.id } shouldContainExactly
                defaultStubsFor(booking).map { it.id }
            defaultStubsFor(held)
                .single { it.id == OPERA_RESERVATION_GET_STUB_ID }
                .mappings
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
                .getValue("roomStay")
                .jsonObject
                .getValue("guarantee")
                .jsonObject
                .getValue("onHold")
                .jsonPrimitive
                .content shouldBe "true"
        }

        test("a held room without a reservation id still plans no reservation read") {
            defaultStubsFor(booking).map { it.id } shouldContain OPERA_RESERVATION_GET_STUB_ID

            // Without a reservation id the Booking is availability-only, which is the shape that
            // requires its rates to declare a rate plan set.
            val notYetReserved =
                booking.copy(
                    hotels =
                        booking.hotels.map {
                            it.copy(availableRates = listOf(rate.copy(ratePlanSet = "PBN")))
                        },
                    rooms = booking.rooms.map { it.copy(reservationId = null, heldInOpera = true) },
                )

            defaultStubsFor(notYetReserved).map { it.id } shouldNotContain OPERA_RESERVATION_GET_STUB_ID
        }

        test("the primary guest name type uses Opera's exact wire value") {
            reservationBody()
                .getValue("reservationGuests")
                .jsonArray
                .single()
                .jsonObject
                .getValue("profileInfo")
                .jsonObject
                .getValue("profile")
                .jsonObject
                .getValue("customer")
                .jsonObject
                .getValue("personName")
                .jsonArray
                .single()
                .jsonObject
                .getValue("nameType")
                .jsonPrimitive
                .content shouldBe "Primary"
        }
    })
