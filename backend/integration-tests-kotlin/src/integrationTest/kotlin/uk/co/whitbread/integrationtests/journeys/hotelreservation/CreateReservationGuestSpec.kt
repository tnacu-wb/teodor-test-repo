package uk.co.whitbread.integrationtests.journeys.hotelreservation

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.hotelreservation.HotelReservationApi
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationBookingChannel
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRoomRateRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRoomRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.ReservationGuestBooker
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.ReservationGuestBookerAddress
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.ReservationGuestDetails
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.ReservationGuestRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.ReservationGuestStayingGuest
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.HotelReservationFeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booker
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

private val arrival: LocalDate = LocalDate.now().plusDays(14)
private val departure: LocalDate = arrival.plusDays(1)

/** Pins for the create call that mints the basket; see CreateReservationSpec for the reasoning. */
private val createReservationFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.SET_DEFAULT_PAYMENT_METHOD_DS to false,
        HotelReservationFeatureFlag.CITY_TAX_UK to false,
        HotelReservationFeatureFlag.APPLY_OCCUPANCY_SUPPLEMENT to false,
    )

/**
 * Pins for the guest call: city tax off, so no content-entity hotel-information lookup is needed to
 * resolve the reason for stay, and the pre-registered repurpose flag pinned so the reservation read
 * behaves the same after any environment default flip.
 *
 * The endpoint's two remaining flags (ohip-adapter's OAuth token flags) are not pinned. Overrides
 * for downstream-owned flags do propagate — the create call above pins an ohip flag — but only to
 * flag reads inside the scenario's request, and OAuth token acquisition is evaluated outside that
 * context, so no override can reach those two.
 */
private val createReservationGuestFlagPins =
    mapOf<FeatureFlag, Boolean>(
        HotelReservationFeatureFlag.CITY_TAX_UK to false,
        HotelReservationFeatureFlag.MOBILE_PRE_REGISTERED_REPURPOSE to false,
    )

class CreateReservationGuestSpec :
    JourneySpec(
        "guest details can be saved against a held hotel reservation",
        {
            val hotelReservationApi = HotelReservationApi()

            scenario("a booker who is also the staying guest is saved onto the held reservation") {
                val booking = singleRoomBooking()
                val basketReference = holdReservation(hotelReservationApi, booking)

                val result =
                    hotelReservationApi.createReservationGuest(
                        request = reservationGuestRequest(booking, basketReference),
                        testId = testId,
                        featureFlagOverrides = createReservationGuestFlagPins,
                    )
                result.attachEvidence("Save Reservation Guest")

                expect("returns the basket the guest details were saved against") {
                    result.response.status.value shouldBe 201
                    result.body.basketReference shouldBe basketReference
                }

                expect("writes the guest profile and links it to the reservation in Opera") {
                    // The response only echoes the basket reference, so Opera traffic is the only
                    // evidence the guest details were written. The guest call costs seven Opera
                    // requests on top of the setup create:
                    //
                    // reading the arrival date through ohip-adapter's basket lookup
                    //   1 GET  /rsv/v1/hotels/{hotelId}/reservations/{reservationId}
                    //   1 GET  /ent/config/v1/hotels/{hotelId}   (uncached: CACHE_TYPE=none)
                    //   1 GET  /crm/v1/profiles/{profileId}      (profile attached to the reservation)
                    // saving the guest through ohip-adapter's guest endpoint
                    //   1 GET  /rsv/v1/hotels/{hotelId}/reservations/{reservationId}  (temp profile id)
                    //   1 GET  /crm/v1/profiles/{profileId}
                    //   1 PUT  /crm/v1/profiles/{profileId}      (booker details onto the profile)
                    //   1 PUT  /rsv/v1/hotels/{hotelId}/reservations/{reservationId}  (link profiles)
                    //
                    // No content-entity call: city tax is pinned off. No CDH call: the request
                    // carries no company account id. No hotel-account call: updateProfileConsent
                    // is unset, and the environment points that host at an invalid name.
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE + OPERA_CALLS_FOR_GUEST
                    expectNoTrafficBeyondOpera()
                }
            }

            scenario("a booker who is not staying gets their own profile alongside the guest's") {
                val booking =
                    singleRoomBooking(
                        booker =
                            Booker(
                                firstName = "Alex",
                                lastName = "Payne",
                                email = "alex.payne@example.test",
                            ),
                    )
                val basketReference = holdReservation(hotelReservationApi, booking)

                val result =
                    hotelReservationApi.createReservationGuest(
                        request = thirdPartyBookerGuestRequest(booking, basketReference),
                        testId = testId,
                        featureFlagOverrides = createReservationGuestFlagPins,
                    )
                result.attachEvidence("Save Reservation Guest For Third-Party Booker")

                expect("returns the basket the guest details were saved against") {
                    result.response.status.value shouldBe 201
                    result.body.basketReference shouldBe basketReference
                }

                expect("creates a booker profile instead of reusing the guest's temporary one") {
                    // With no staying guest flagged sameAsBooker, there is no temporary profile to
                    // reuse for the booker, so ohip-adapter posts a new one and still reads and
                    // updates the guest's own. Eight Opera requests on top of the setup create:
                    //
                    // reading the arrival date through ohip-adapter's basket lookup
                    //   1 GET  /rsv/v1/hotels/{hotelId}/reservations/{reservationId}
                    //   1 GET  /ent/config/v1/hotels/{hotelId}   (uncached: CACHE_TYPE=none)
                    //   1 GET  /crm/v1/profiles/{profileId}      (profile attached to the reservation)
                    // saving the guest through ohip-adapter's guest endpoint
                    //   1 POST /crm/v1/profiles                  (booker profile, newly created)
                    //   1 GET  /rsv/v1/hotels/{hotelId}/reservations/{reservationId}  (temp profile id)
                    //   1 GET  /crm/v1/profiles/{profileId}
                    //   1 PUT  /crm/v1/profiles/{profileId}      (guest details onto the temp profile)
                    //   1 PUT  /rsv/v1/hotels/{hotelId}/reservations/{reservationId}  (link both)
                    //
                    // One more than the sameAsBooker scenario above, and the extra call is a POST:
                    // that difference is the whole point of this branch.
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE + OPERA_CALLS_FOR_THIRD_PARTY_BOOKER
                    expectNoTrafficBeyondOpera()
                }
            }

            scenario("pre-check-in saves a lead guest and an accompanying guest onto one reservation") {
                val booking = singleRoomBooking(adults = 2)
                val basketReference = holdReservation(hotelReservationApi, booking)

                val result =
                    hotelReservationApi.createReservationGuest(
                        request = preCheckInGuestRequest(booking, basketReference),
                        testId = testId,
                        featureFlagOverrides = createReservationGuestFlagPins,
                    )
                result.attachEvidence("Save Pre-Check-In Reservation Guests")

                expect("returns the basket the guest details were saved against") {
                    result.response.status.value shouldBe 201
                    result.body.basketReference shouldBe basketReference
                }

                expect("updates the known guest profile, creates the accompanying one, and links both") {
                    // Pre-check-in is a different OHIP path from the standard flow above: instead of
                    // reusing the booker's temporary profile it reads the saved reservation, validates
                    // occupancy, then updates or creates one profile per staying guest. Opera traffic is
                    // the only evidence, and this costs eight requests on top of the setup create:
                    //
                    // reading the arrival date through ohip-adapter's basket lookup
                    //   1 GET  /rsv/v1/hotels/{hotelId}/reservations/{reservationId}
                    //   1 GET  /ent/config/v1/hotels/{hotelId}   (uncached: CACHE_TYPE=none)
                    //   1 GET  /crm/v1/profiles/{profileId}      (profile attached to the reservation)
                    // saving the guests through ohip-adapter's pre-check-in path
                    //   1 GET  /rsv/v1/hotels/{hotelId}/reservations/{reservationId}  (occupancy check:
                    //          guestCounts.adults + children caps the accompanying guests at one)
                    //        (no stayer-profile GET: the reservation payload reports the lead guest's
                    //         profile as already attached, so the profile-id set to fetch is empty)
                    //   1 PUT  /crm/v1/profiles/{profileId}      (lead guest: profile already exists)
                    //   1 POST /crm/v1/profiles                  (accompanying guest: no profile yet)
                    //   1 PUT  /rsv/v1/hotels/{hotelId}/reservations/{reservationId}  (link both)
                    //
                    // One reservation PUT, not one per guest: the pre-check-in path groups the staying
                    // guests by reservation id and links each reservation once.
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE + OPERA_CALLS_FOR_PRE_CHECK_IN
                    expectNoTrafficBeyondOpera()
                }
            }

            scenario("pre-check-in rejects more accompanying guests than the reservation sleeps") {
                val booking = singleRoomBooking(adults = 2)
                val basketReference = holdReservation(hotelReservationApi, booking)

                // A two-adult reservation sleeps one lead guest plus one companion, so a second
                // companion exceeds its occupancy. The whole request is rejected, not trimmed.
                val result =
                    hotelReservationApi.createReservationGuest(
                        request =
                            preCheckInGuestRequest(
                                booking,
                                basketReference,
                                accompanyingGuestNames = listOf("Sam", "Robin"),
                            ),
                        testId = testId,
                        featureFlagOverrides = createReservationGuestFlagPins,
                    )
                result.attachEvidence("Rejected Pre-Check-In Reservation Guests")

                expect("names the occupancy rule that rejected the request") {
                    result.response.status.value shouldBe PRE_CHECK_IN_REJECTION_STATUS

                    val error = result.errorBody.shouldNotBeNull()
                    error.errCode shouldBe OHIP_GUEST_REJECTION_ERR_CODE
                    // Both pre-check-in validations carry err code 952, so the code alone cannot say
                    // which rule fired. The message is what distinguishes this scenario from the
                    // lead-guest rule, and the derived allowance -- two adults minus the lead -- is
                    // the behaviour under test.
                    error.debugMessage shouldBe "Max Accompanying Guests allowed is: 1"
                }

                expect("writes nothing to Opera before rejecting") {
                    // The value of this scenario is where the count stops. Occupancy is validated
                    // straight after the reservation read and before any profile or link write, so
                    // a rejected request must leave Opera untouched:
                    //
                    //   1 GET  /rsv/v1/hotels/{hotelId}/reservations/{reservationId}  (arrival date)
                    //   1 GET  /ent/config/v1/hotels/{hotelId}
                    //   1 GET  /crm/v1/profiles/{profileId}
                    //   1 GET  /rsv/v1/hotels/{hotelId}/reservations/{reservationId}  (occupancy)
                    //
                    // No profile GET, PUT or POST, and no reservation PUT. If validation ever moved
                    // after the writes, this count would rise and a partially-updated reservation
                    // would be left behind.
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE + OPERA_CALLS_BEFORE_REJECTION
                    expectNoTrafficBeyondOpera()
                }
            }
        },
    )

/**
 * Holds a reservation via the create endpoint and returns its basket reference.
 *
 * Asserts the setup's Opera cost here so each guest call's own Opera cost is the delta between
 * [OPERA_CALLS_AFTER_CREATE] and the final count rather than hidden inside a total.
 */
private suspend fun JourneySpec.ScenarioScope.holdReservation(
    api: HotelReservationApi,
    booking: Booking,
): String {
    installFor(booking)

    val created =
        api.createReservation(
            request = createReservationRequest(booking),
            testId = testId,
            featureFlagOverrides = createReservationFlagPins,
        )
    created.attachEvidence("Create Held Reservation")

    expect("holds the reservation in Opera before any guest details are sent") {
        created.response.status.value shouldBe 201
        callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE
    }

    return created.body.basketReference
}

/** The guest endpoint has no route to any upstream but Opera, so everything else stays untouched. */
private suspend fun JourneySpec.ScenarioScope.expectNoTrafficBeyondOpera() {
    callCount(Upstream.AEM) shouldBe 0
    callCount(Upstream.CDH) shouldBe 0
    callCount(Upstream.WORLDLINE) shouldBe 0
}

/**
 * Opera cost of the setup create, measured rather than derived. Itemizing it belongs to
 * CreateReservationSpec; this scenario only needs it fixed so the guest call's cost is the delta.
 */
private const val OPERA_CALLS_AFTER_CREATE = 4

/** Opera cost of the endpoint under test; the assertion comment itemizes all seven requests. */
private const val OPERA_CALLS_FOR_GUEST = 7

/**
 * Opera cost of the pre-check-in path; the assertion comment itemizes the requests. Since the
 * reservation lookup started reporting the guest's attached reservation profile (as real Opera
 * does), the save path recognizes the lead guest's profile as already attached and skips its
 * separate stayer-profile GET, so the path costs seven requests rather than eight.
 */
private const val OPERA_CALLS_FOR_PRE_CHECK_IN = 7

/** Opera cost when the booker is not a staying guest; the assertion comment itemizes all eight. */
private const val OPERA_CALLS_FOR_THIRD_PARTY_BOOKER = 8

/**
 * Opera cost of a pre-check-in request rejected for occupancy: the arrival-date read plus the one
 * reservation read the validation itself needs, and nothing else.
 */
private const val OPERA_CALLS_BEFORE_REJECTION = 4

/**
 * Status returned when ohip-adapter rejects the guest request. `OHIP_PUT_RESERVATIONS_GUEST_EXCEPTION`
 * is classified `internal.server.exception` in ohip-adapter's `ErrorCode`, and hotel-reservation
 * turns any error from the guest call into a `HotelReservationOhipException`, so a caller-side
 * mistake surfaces as a server error rather than a 4xx. Pinned here so a future change to that
 * mapping fails loudly instead of silently changing the endpoint's contract.
 */
private const val PRE_CHECK_IN_REJECTION_STATUS = 500

/** ohip-adapter's `OHIP_PUT_RESERVATIONS_GUEST_EXCEPTION`, declared as code 952 in its `ErrorCode`. */
private const val OHIP_GUEST_REJECTION_ERR_CODE = 952

/**
 * A held one-room reservation for [adults] guests.
 *
 * The occupancy matters to the pre-check-in path: Opera's `guestCounts` is what caps the number of
 * accompanying guests a reservation accepts, and the reservation stub serves it from this value.
 */
private fun singleRoomBooking(
    adults: Int = 1,
    booker: Booker? = null,
): Booking {
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "VPPDBL", adults = adults)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = departure,
        rooms =
            listOf(
                BookingRoom(
                    reservationId = "6004001",
                    roomType = rate.roomType,
                    adults = rate.adults,
                    children = rate.children,
                    // Required: without it the Opera reservation stub invents a TEMP profile id that
                    // no profile gate installs, and the guest call fails reading it back.
                    guestProfile =
                        GuestProfile(
                            profileId = "7004001",
                            firstName = "Debbie",
                            lastName = "Doe",
                            email = "debbie.doe@example.test",
                            phone = "+441234567890",
                            addressLine = "1 Test Street",
                            city = "Dunstable",
                            postcode = "LU5 5XE",
                            reservationContact = booker != null,
                        ),
                ),
            ),
        booker = booker,
    )
}

private fun createReservationRequest(booking: Booking): CreateReservationRequest {
    val rate = booking.hotel.availableRates.single()
    val arrivalDate = requireNotNull(booking.arrival).toString()
    val departureDate = requireNotNull(booking.departure).toString()

    return CreateReservationRequest(
        reservations =
            booking.rooms.map { room ->
                CreateReservationRoomRequest(
                    hotelId = booking.hotel.hotelId,
                    arrival = arrivalDate,
                    departure = departureDate,
                    adultsNumber = requireNotNull(room.adults),
                    childrenNumber = room.children,
                    cotRequired = false,
                    roomRates =
                        CreateReservationRoomRateRequest(
                            ratePlanCode = rate.ratePlan,
                            pmsRoomType = requireNotNull(room.roomType),
                            specialRequests = listOf("SING"),
                            startDate = arrivalDate,
                            endDate = departureDate,
                        ),
                )
            },
        bookingChannel =
            CreateReservationBookingChannel(
                channel = "PI",
                subchannel = "WEB",
                language = "EN",
            ),
        bookingFlowId = "create-reservation-guest",
    )
}

/**
 * Projects the [GuestProfile] the Opera stubs serve onto the request's booker shape.
 *
 * `language` is supplied deliberately: when it is absent, ohip-adapter resolves the language from
 * an extra Opera reservation read plus a rules-agent channel lookup.
 */
private fun bookerFrom(guest: GuestProfile): ReservationGuestBooker =
    ReservationGuestBooker(
        firstName = guest.firstName,
        lastName = guest.lastName,
        emailAddress = guest.email,
        mobile = guest.phone,
        language = "en",
        address =
            ReservationGuestBookerAddress(
                addressLine1 = guest.addressLine,
                cityName = guest.city,
                postalCode = guest.postcode,
                countryCode = guest.country,
                addressType = "HOME",
            ),
    )

/** Builds the guest request from the same [Booking] guest profile the Opera stubs serve. */
private fun reservationGuestRequest(
    booking: Booking,
    basketReference: String,
): ReservationGuestRequest {
    val room = booking.room
    val guest = requireNotNull(room.guestProfile)

    return ReservationGuestRequest(
        basketReference = basketReference,
        hotelId = booking.hotel.hotelId,
        reasonForStay = "LEI",
        booker = bookerFrom(guest),
        stayingGuests =
            listOf(
                ReservationGuestStayingGuest(
                    sameAsBooker = true,
                    reservationId = room.reservationId,
                    stayingGuestDetails =
                        ReservationGuestDetails(
                            firstName = guest.firstName,
                            lastName = guest.lastName,
                            emailAddress = guest.email,
                            profileId = guest.profileId,
                        ),
                ),
            ),
    )
}

/**
 * Builds the variant where the booker is not staying: nobody is flagged `sameAsBooker`, so
 * ohip-adapter has no temporary profile to reuse for the booker and posts a new one.
 *
 * The same booker identity is also carried by [Booking], allowing the Contact-profile stub to
 * distinguish this POST from Guest and Company profile creates. Being a different person from
 * the room's guest is what makes this the third-party-booker branch.
 *
 * No `companyName` on the address: that would add an Opera company-profile POST and turn this into
 * a different scenario. `profileId` is deliberately omitted from the guest too — on this path
 * ohip-adapter reads the temporary profile id off the reservation rather than trusting the request,
 * and the reservation stub serves the same id the profile stubs are keyed on.
 */
private fun thirdPartyBookerGuestRequest(
    booking: Booking,
    basketReference: String,
): ReservationGuestRequest {
    val room = booking.room
    val guest = requireNotNull(room.guestProfile)

    return ReservationGuestRequest(
        basketReference = basketReference,
        hotelId = booking.hotel.hotelId,
        reasonForStay = "LEI",
        booker =
            ReservationGuestBooker(
                firstName = "Alex",
                lastName = "Payne",
                emailAddress = "alex.payne@example.test",
                mobile = "+441234567891",
                language = "en",
                address =
                    ReservationGuestBookerAddress(
                        addressLine1 = "9 Booker Row",
                        cityName = "Luton",
                        postalCode = "LU1 2AB",
                        countryCode = "GB",
                        addressType = "HOME",
                    ),
            ),
        stayingGuests =
            listOf(
                ReservationGuestStayingGuest(
                    sameAsBooker = false,
                    reservationId = room.reservationId,
                    stayingGuestDetails =
                        ReservationGuestDetails(
                            firstName = guest.firstName,
                            lastName = guest.lastName,
                            emailAddress = guest.email,
                        ),
                ),
            ),
    )
}

/**
 * Builds the pre-check-in variant: a lead guest who already has an Opera profile plus one
 * accompanying guest who does not, both against the same reservation.
 *
 * Three things this mode requires that the standard path does not:
 * - `reservationId` must be supplied on every guest. Pre-check-in uses the submitted ids as-is
 *   rather than re-keying them from the basket, and ohip-adapter groups the guests by that id.
 * - `isAccompanyingGuest` must be set explicitly. ohip-adapter counts a lead guest only when the
 *   flag is present and `false`, so omitting it fails validation with "A reservation must have
 *   exactly one lead guest" rather than defaulting to lead.
 * - the accompanying guest's details go in `stayingGuestDetails`, not `accompanyingGuestDetails`;
 *   this path reads every guest, lead or not, from the former.
 *
 * `sameAsBooker` is required by the request model but unused here — only the standard path reads it.
 */
private fun preCheckInGuestRequest(
    booking: Booking,
    basketReference: String,
    accompanyingGuestNames: List<String> = listOf("Sam"),
): ReservationGuestRequest {
    val room = booking.room
    val guest = requireNotNull(room.guestProfile)
    val reservationId = requireNotNull(room.reservationId)

    return ReservationGuestRequest(
        basketReference = basketReference,
        hotelId = booking.hotel.hotelId,
        reasonForStay = "LEI",
        preCheckIn = true,
        booker = bookerFrom(guest),
        stayingGuests =
            listOf(
                ReservationGuestStayingGuest(
                    sameAsBooker = true,
                    reservationId = reservationId,
                    isAccompanyingGuest = false,
                    stayingGuestDetails =
                        ReservationGuestDetails(
                            firstName = guest.firstName,
                            lastName = guest.lastName,
                            emailAddress = guest.email,
                            profileId = guest.profileId,
                        ),
                ),
            ) +
                accompanyingGuestNames.map { firstName ->
                    ReservationGuestStayingGuest(
                        sameAsBooker = false,
                        reservationId = reservationId,
                        isAccompanyingGuest = true,
                        stayingGuestDetails =
                            ReservationGuestDetails(
                                firstName = firstName,
                                lastName = "Doe",
                            ),
                    )
                },
    )
}
