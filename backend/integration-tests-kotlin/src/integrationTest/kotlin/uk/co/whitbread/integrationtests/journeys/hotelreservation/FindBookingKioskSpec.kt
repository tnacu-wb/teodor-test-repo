package uk.co.whitbread.integrationtests.journeys.hotelreservation

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import io.kotest.matchers.string.shouldNotBeBlank
import uk.co.whitbread.integrationtests.clients.basket.BasketApi
import uk.co.whitbread.integrationtests.clients.basket.model.ChangeBasketStatusRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.HotelReservationApi
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationBookingChannel
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRoomRateRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRoomRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_EXTERNAL_REFERENCE_SEARCH_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.externalReservationSearchUnavailable
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.HotelReservationFeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Aem
import uk.co.whitbread.integrationtests.testkit.model.AemIndexHeaderData
import uk.co.whitbread.integrationtests.testkit.model.AemIndexHeaderFavicon
import uk.co.whitbread.integrationtests.testkit.model.AemIndexHeaderSeo
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate
import java.util.concurrent.ThreadLocalRandom

/**
 * Flag pins shared by every kiosk scenario, all three off.
 *
 * `mobile_preRegistered_repurpose` stays pinned false in every scenario of this spec: it only
 * forces `deRegCardCompleted` to false on the OHIP reservation-by-ids results, and the kiosk
 * response carries no such field, so its two states are observably identical here and need no
 * duplicate scenarios. The two flags that do change the outcome get their ON variants
 * below.
 */
private val findBookingKioskFlagPins: Map<FeatureFlag, Boolean> =
    mapOf(
        HotelReservationFeatureFlag.PI_SEARCH_BY_OPERA_CONFIRMATION to false,
        OhipFeatureFlag.MOBILE_ACCEPTS_OTA_BOOKING to false,
        HotelReservationFeatureFlag.MOBILE_PRE_REGISTERED_REPURPOSE to false,
    )

/** `release_pi_search_opera_conf_number` ON: every reference reaches the basket and CDH searches. */
private val confirmationSearchKioskFlagPins: Map<FeatureFlag, Boolean> =
    findBookingKioskFlagPins + (HotelReservationFeatureFlag.PI_SEARCH_BY_OPERA_CONFIRMATION to true)

/** `mobile_accepts_ota_booking` ON: third-party reservations become importable for KIOSK.WEB. */
private val otaKioskFlagPins: Map<FeatureFlag, Boolean> =
    findBookingKioskFlagPins + (OhipFeatureFlag.MOBILE_ACCEPTS_OTA_BOOKING to true)

/** Flags the create-reservation setup of the basket scenario pins, copied from `FindBookingSpec`. */
private val createReservationFlagPins: Map<FeatureFlag, Boolean> =
    mapOf(
        OhipFeatureFlag.SET_DEFAULT_PAYMENT_METHOD_DS to false,
        HotelReservationFeatureFlag.CITY_TAX_UK to false,
        HotelReservationFeatureFlag.APPLY_OCCUPANCY_SUPPLEMENT to false,
    )

/**
 * Proves `GET /v1/reservations/find/kiosk`: the controller fixes the channel to `KIOSK.WEB`, which
 * makes `ManageBookingLogic.shouldBypassMatchesOpera` true, so a reference alone — with no arrival
 * date and no surname — finds a completed basket, imports a migrated Opera reservation, imports an
 * eligible OTA reservation as a third-party basket, and falls back to the CDH confirmation search;
 * with `release_pi_search_opera_conf_number` off a numeric reference answers 200 with no body, an
 * OTA reservation is rejected with errCode 291 when `mobile_accepts_ota_booking` is off, and an
 * Opera external-reference search failure surfaces as a 500.
 *
 * Flow: backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/FindBookingKiosk.md
 */
class FindBookingKioskSpec :
    JourneySpec(
        "Kiosk finds a hotel reservation by reference alone",
        {
            val hotelReservationApi = HotelReservationApi()
            val basketApi = BasketApi()

            scenario("a completed basket is found by reference without arrival date or surname") {
                val booking = kioskBasketBooking()

                installFor(booking)

                // Setup only: the same create-then-complete sequence FindBookingSpec uses to put a
                // real COMPLETED basket behind the reference the kiosk call then looks up.
                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = kioskCreateReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Reservation To Find From Kiosk")
                val basketReference = createdReservation.body.basketReference
                basketReference.shouldNotBeBlank()

                val basket = basketApi.getBasket(basketReference = basketReference, testId = testId)
                basket.attachEvidence("Read Basket To Find From Kiosk")
                val bookingReference = basket.body.bookingReference.shouldNotBeNull()
                bookingReference.shouldNotBeBlank()

                val completedBasket =
                    basketApi.changeStatus(
                        bookingReference = bookingReference,
                        request = ChangeBasketStatusRequest(status = "COMPLETED"),
                        testId = testId,
                    )

                completedBasket.attachEvidence("Complete Basket To Find From Kiosk")
                completedBasket.body.status shouldBe "COMPLETED"

                val result =
                    hotelReservationApi.findBookingForKiosk(
                        resNo = bookingReference,
                        testId = testId,
                        featureFlagOverrides = confirmationSearchKioskFlagPins,
                    )

                result.attachEvidence("Find Completed Booking From Kiosk")

                expect("returns the matching basket and hotel references from the reference alone") {
                    result.response.status.value shouldBe 200
                    result.body.sourcePms shouldBe "Opera"
                    result.body.ref shouldBe bookingReference
                    result.body.basketReference shouldBe basketReference
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.token
                        .shouldNotBeNull()
                        .shouldNotBeBlank()
                }

                expect("reads the complete reservation and its content redirect configuration") {
                    // Nine Opera calls. Setup: GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo,
                    // POST /rsv/v1/hotels/{hotelId}/reservations,
                    // PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    // Kiosk lookup: GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId},
                    // a second rateInfo for the amounts,
                    // GET /csh/v1/hotels/{hotelId}/reservations/{reservationId}/folios,
                    // GET /crm/v1/profiles/{profileId}. Each leg reads
                    // GET /ent/config/v1/hotels/{hotelId} once.
                    callCount(Upstream.OPERA) shouldBe 9
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 2
                    callCount(OperaEndpoint.CREATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 2
                    // Pay-on-arrival: no deposit folio is created and none is searched for.
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 0
                    // content-entity-service reads the dashboard redirect configuration from AEM.
                    callCount(Upstream.AEM) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a migrated Opera reservation is imported into a manageable basket") {
                val booking = kioskMigratedBooking()

                installFor(booking)

                val result =
                    hotelReservationApi.findBookingForKiosk(
                        resNo = requireNotNull(booking.bookingReference),
                        testId = testId,
                        featureFlagOverrides = findBookingKioskFlagPins,
                    )

                result.attachEvidence("Find Migrated Booking From Kiosk")

                expect("imports the Opera reservation and returns its new basket") {
                    result.response.status.value shouldBe 200
                    result.body.sourcePms shouldBe "Opera"
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.ref
                        .shouldNotBeNull()
                        .shouldNotBeBlank()
                    result.body.basketReference
                        .shouldNotBeNull()
                        .shouldNotBeBlank()
                    result.body.token
                        .shouldNotBeNull()
                        .shouldNotBeBlank()
                }

                expect("reads and enriches the external reservation without confirmation fallback") {
                    // Four Opera calls: GET /rsv/v1/reservations,
                    // GET /crm/v1/profiles/{profileId},
                    // GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo for the amounts, and
                    // GET /ent/config/v1/hotels/{hotelId}. Nothing is paid, so the import resolves
                    // the reservation as PAY_ON_ARRIVAL and never reads its deposits.
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(OperaEndpoint.GET_EXTERNAL_RESERVATIONS) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    // GET /csh/v1/hotels/{hotelId}/depositFolio belongs to the PAY_NOW branch only.
                    callCount(OperaEndpoint.GET_RESERVATION_DEPOSITS) shouldBe 0
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 1
                    // Confirmation search is pinned off, so the installed CDH search is untouched.
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an eligible OTA reservation is imported as a third-party basket") {
                val booking = kioskOtaBooking()

                installFor(booking)

                val result =
                    hotelReservationApi.findBookingForKiosk(
                        resNo = requireNotNull(booking.bookingReference),
                        testId = testId,
                        featureFlagOverrides = otaKioskFlagPins,
                    )

                result.attachEvidence("Find OTA Booking From Kiosk")

                expect("imports the eligible OTA reservation as a third-party basket") {
                    result.response.status.value shouldBe 200
                    result.body.sourcePms shouldBe "Opera"
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    // KIOSK.WEB is in the configured OTA subchannel set, so the import is allowed
                    // and the basket is stamped third-party.
                    result.body.idContext shouldBe "3rd Party"
                    result.body.basketReference
                        .shouldNotBeNull()
                        .shouldNotBeBlank()
                    result.body.token
                        .shouldNotBeNull()
                        .shouldNotBeBlank()
                }

                expect("reads and enriches the OTA reservation without confirmation fallback") {
                    // Four Opera calls: GET /rsv/v1/reservations,
                    // GET /crm/v1/profiles/{profileId},
                    // GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo for the amounts, and
                    // GET /ent/config/v1/hotels/{hotelId}. Nothing is paid, so the import resolves
                    // the reservation as PAY_ON_ARRIVAL and never reads its deposits.
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(OperaEndpoint.GET_EXTERNAL_RESERVATIONS) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    // GET /csh/v1/hotels/{hotelId}/depositFolio belongs to the PAY_NOW branch only.
                    callCount(OperaEndpoint.GET_RESERVATION_DEPOSITS) shouldBe 0
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera confirmation number found through CDH is imported into a manageable basket") {
                val booking = kioskCdhConfirmationBooking()

                installFor(booking)

                // The kiosk request carries no isOldBooking parameter, so a numeric resNo plus the
                // confirmation-search flag is the only route into the CDH fallback here.
                val result =
                    hotelReservationApi.findBookingForKiosk(
                        resNo = requireNotNull(booking.cdhBookingReference),
                        testId = testId,
                        featureFlagOverrides = confirmationSearchKioskFlagPins,
                    )

                result.attachEvidence("Find Opera Confirmation Through CDH From Kiosk")

                expect("imports the CDH reservation into a manageable basket") {
                    result.response.status.value shouldBe 200
                    result.body.sourcePms shouldBe "Opera"
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.basketReference
                        .shouldNotBeNull()
                        .shouldNotBeBlank()
                    result.body.token
                        .shouldNotBeNull()
                        .shouldNotBeBlank()
                    // The kiosk resNo is echoed back untouched: it carries no six-letter prefix
                    // to strip.
                    result.body.operaConfNumber shouldBe requireNotNull(booking.cdhBookingReference)
                }

                expect("falls back through CDH and links the resulting Opera reservation") {
                    // Eight Opera calls: the empty GET /rsv/v1/reservations,
                    // GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId},
                    // GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo for the amounts,
                    // GET /csh/v1/hotels/{hotelId}/reservations/{reservationId}/folios,
                    // GET /crm/v1/profiles/{profileId},
                    // GET /ent/config/v1/hotels/{hotelId} twice, and the external-reference
                    // PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    callCount(Upstream.OPERA) shouldBe 8
                    callCount(OperaEndpoint.GET_EXTERNAL_RESERVATIONS) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    // Pay-on-arrival: the imported reservation has no deposit to read.
                    callCount(OperaEndpoint.GET_RESERVATION_DEPOSITS) shouldBe 0
                    // One CDH call: POST /BookingServices/V2|V3/ReservationSearch.
                    callCount(Upstream.CDH) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 1
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a numeric reference returns an empty body when confirmation search is disabled") {
                val booking = kioskUnconfirmableBooking()

                installFor(booking)

                // Raw text, because a kiosk lookup with no eligible match answers 200 with no body
                // at all — there is no JSON document to decode.
                val result =
                    hotelReservationApi.findBookingForKioskText(
                        resNo = requireNotNull(booking.cdhBookingReference),
                        testId = testId,
                        featureFlagOverrides = findBookingKioskFlagPins,
                    )

                result.attachEvidence("Find Numeric Reference With Confirmation Search Disabled")

                expect("returns 200 with no booking at all") {
                    result.response.status.value shouldBe 200
                    result.body.shouldBeEmpty()
                }

                expect("stops after the empty external-reference search") {
                    // One Opera call: GET /rsv/v1/reservations returns no match.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_EXTERNAL_RESERVATIONS) shouldBe 1
                    // The CDH reference installs the CDH reservation-search default, so the
                    // untaken confirmation fallback is proven against an installed mapping.
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an OTA reservation is rejected when third-party imports are disabled") {
                val booking = kioskIneligibleOtaBooking()

                installFor(booking)

                val result =
                    hotelReservationApi.findBookingForKiosk(
                        resNo = requireNotNull(booking.bookingReference),
                        testId = testId,
                        featureFlagOverrides = findBookingKioskFlagPins,
                    )

                result.attachEvidence("Reject OTA Booking From Kiosk")

                expect("returns the third-party-not-allowed error without importing a basket") {
                    result.response.status.value shouldBe 400
                    result.errorBody
                        .shouldNotBeNull()
                        .errCode shouldBe 291
                }

                expect("stops after external-reservation enrichment") {
                    // Three Opera calls: GET /rsv/v1/reservations,
                    // GET /crm/v1/profiles/{profileId}, and the summary
                    // GET /rsv/v1/hotels/{hotelId}/reservations/rateInfo. The rejection happens
                    // before basket creation and the content lookup.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_EXTERNAL_RESERVATIONS) shouldBe 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera external-reference search failure is returned through OHIP") {
                val booking = kioskUnreachableBooking()

                // A downstream failure is exceptional behavior, not a Booking world state, so the
                // default external-reference search is excluded and a custom stub with its own id
                // answers instead.
                installFor(booking, excluded = setOf(OPERA_EXTERNAL_REFERENCE_SEARCH_STUB_ID))
                installStub(externalReservationSearchUnavailable(booking, booking.rooms))

                val result =
                    hotelReservationApi.findBookingForKiosk(
                        resNo = requireNotNull(booking.bookingReference),
                        testId = testId,
                        featureFlagOverrides = findBookingKioskFlagPins,
                    )

                result.attachEvidence("Find Booking From Kiosk Opera External Search Failure")

                expect("returns the mapped external-reference failure") {
                    result.response.status.value shouldBe 500
                    result.errorBody
                        .shouldNotBeNull()
                        .errCode shouldBe 959
                    result.errorBody.globalErrTextTemplate shouldBe "internal.server.exception"
                }

                expect("stops after the failed external-reference search") {
                    // One Opera call through OHIP: GET /rsv/v1/reservations fails before enrichment.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_EXTERNAL_RESERVATIONS) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

/**
 * A reservable hotel and room for the basket path: the kiosk scenario creates its own reservation
 * and looks it up under the reference basket-service generates for it.
 *
 * `bookingReference` is still set, because it is what puts the digital `WB_DIGITAL` external
 * reference on the Opera reservation. The kiosk channel has no `DISTR` short circuit, so the
 * basket path classifies every reservation through that idContext, and a reservation without one
 * would be treated as third-party and rejected.
 *
 * Every identity here is distinct from `FindBookingSpec`'s, because imported and completed baskets
 * persist in the shared integration environment.
 */
private fun kioskBasketBooking(): Booking {
    val arrival = LocalDate.now().plusDays(14)
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = arrival.plusDays(2),
        bookingReference = "AQN0006221",
        rooms =
            listOf(
                BookingRoom(
                    reservationId = "6004221",
                    roomType = rate.roomType,
                    adults = rate.adults,
                    status = ReservationStatus.RESERVED,
                    guestProfile =
                        kioskGuest(
                            profileId = "PROF-6441",
                            firstName = "Riley",
                            lastName = "Morgan",
                            email = "riley.morgan.kiosk@test.com",
                        ),
                ),
            ),
        aem = kioskAem(),
    )
}

/** A BART-migrated Opera reservation, importable on the external-reference path. */
private fun kioskMigratedBooking(): Booking =
    kioskExternalBooking(
        bookingReference = "BART7441",
        reservationId = "6004222",
        profileId = "PROF-6442",
        firstName = "Jamie",
        lastName = "Taylor",
        email = "jamie.taylor.kiosk-migrated@test.com",
    )

/** A third-party OTA reservation whose provider is not excluded from the kiosk import. */
private fun kioskOtaBooking(): Booking =
    kioskExternalBooking(
        bookingReference = "OTA7442",
        reservationId = "6004223",
        profileId = "PROF-6443",
        firstName = "Alex",
        lastName = "Walker",
        email = "alex.walker.kiosk-ota@test.com",
        bookingReferenceIdContext = "BOOKING.COM",
        sourceCode = "35",
    )

/**
 * An Opera reservation reachable only through the CDH confirmation search: it carries no external
 * reference, so the external-reference search answers empty and the fallback runs.
 *
 * The reservation id is randomized because the fallback persists a basket under the resulting
 * confirmation number, and a rerun that found that basket would take the basket path instead.
 */
private fun kioskCdhConfirmationBooking(): Booking {
    val reservationId =
        ThreadLocalRandom
            .current()
            .nextInt(7_000_000, 10_000_000)
            .toString()

    return kioskExternalBooking(
        bookingReference = null,
        cdhBookingReference = "0$reservationId",
        reservationId = reservationId,
        profileId = "PROF-$reservationId",
        firstName = "Jamie",
        lastName = "Taylor",
        email = "jamie.taylor.kiosk-cdh@test.com",
    )
}

/**
 * The same shape as the CDH booking, used with the confirmation search pinned off: the numeric
 * reference finds no external reservation and no fallback is permitted, so nothing is imported and
 * the CDH search default installed by `cdhBookingReference` stays untouched.
 */
private fun kioskUnconfirmableBooking(): Booking =
    kioskExternalBooking(
        bookingReference = null,
        cdhBookingReference = "06004224",
        reservationId = "6004224",
        profileId = "PROF-6444",
        firstName = "Jamie",
        lastName = "Taylor",
        email = "jamie.taylor.kiosk-no-confirmation@test.com",
    )

/** A third-party OTA reservation used with `mobile_accepts_ota_booking` pinned off. */
private fun kioskIneligibleOtaBooking(): Booking =
    kioskExternalBooking(
        bookingReference = "OTA7446",
        reservationId = "6004226",
        profileId = "PROF-6446",
        firstName = "Alex",
        lastName = "Walker",
        email = "alex.walker.kiosk-ineligible-ota@test.com",
        bookingReferenceIdContext = "BOOKING.COM",
        sourceCode = "35",
    )

/** A migrated reservation whose external-reference search is answered with an Opera failure. */
private fun kioskUnreachableBooking(): Booking =
    kioskExternalBooking(
        bookingReference = "BART7447",
        reservationId = "6004227",
        profileId = "PROF-6447",
        firstName = "Jamie",
        lastName = "Taylor",
        email = "jamie.taylor.kiosk-unreachable@test.com",
    )

private fun kioskExternalBooking(
    bookingReference: String?,
    reservationId: String,
    profileId: String,
    firstName: String,
    lastName: String,
    email: String,
    cdhBookingReference: String? = null,
    bookingReferenceIdContext: String = "BART_OHIP",
    sourceCode: String = "44",
): Booking {
    val arrival = LocalDate.now().plusDays(21)
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = arrival.plusDays(2),
        bookingReference = bookingReference,
        cdhBookingReference = cdhBookingReference,
        bookingReferenceIdContext = bookingReferenceIdContext,
        rooms =
            listOf(
                BookingRoom(
                    reservationId = reservationId,
                    roomType = rate.roomType,
                    adults = rate.adults,
                    status = ReservationStatus.RESERVED,
                    sourceCode = sourceCode,
                    guestProfile =
                        kioskGuest(
                            profileId = profileId,
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                        ),
                ),
            ),
        aem = kioskAem(),
    )
}

private fun kioskGuest(
    profileId: String,
    firstName: String,
    lastName: String,
    email: String,
): GuestProfile =
    GuestProfile(
        profileId = profileId,
        firstName = firstName,
        lastName = lastName,
        email = email,
        phone = "+447700900041",
        addressLine = "41 High Street",
        city = "London",
        postcode = "SW1A 1AA",
    )

/** The dashboard redirect configuration content-entity-service reads for `country=gb`, `language=en`. */
private fun kioskAem(): Aem =
    Aem(
        indexHeaderData =
            AemIndexHeaderData(
                country = "gb",
                language = "en",
                seo =
                    AemIndexHeaderSeo(
                        pageTitle = "Manage your booking",
                        pageDescription = "Find and manage an existing booking.",
                        cardImageUrl = "/content/dam/pi/manage-booking.jpg",
                    ),
                favicon =
                    AemIndexHeaderFavicon(
                        faviconUrl = "/content/dam/pi/favicon.ico",
                    ),
            ),
    )

private fun kioskCreateReservationRequest(booking: Booking): CreateReservationRequest {
    val room = booking.room
    val rate = booking.hotel.availableRates.single()
    val arrival = requireNotNull(booking.arrival).toString()
    val departure = requireNotNull(booking.departure).toString()

    return CreateReservationRequest(
        reservations =
            listOf(
                CreateReservationRoomRequest(
                    hotelId = booking.hotel.hotelId,
                    arrival = arrival,
                    departure = departure,
                    adultsNumber = requireNotNull(room.adults),
                    childrenNumber = room.children,
                    roomRates =
                        CreateReservationRoomRateRequest(
                            ratePlanCode = rate.ratePlan,
                            pmsRoomType = requireNotNull(room.roomType),
                            startDate = arrival,
                            endDate = departure,
                        ),
                ),
            ),
        bookingChannel =
            CreateReservationBookingChannel(
                channel = "PI",
                subchannel = "WEB",
                language = "EN",
            ),
        bookingFlowId = "find-booking-kiosk",
    )
}
