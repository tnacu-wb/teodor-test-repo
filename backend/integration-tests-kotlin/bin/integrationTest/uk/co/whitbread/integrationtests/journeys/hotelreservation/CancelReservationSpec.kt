package uk.co.whitbread.integrationtests.journeys.hotelreservation

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeBlank
import uk.co.whitbread.integrationtests.clients.basket.BasketApi
import uk.co.whitbread.integrationtests.clients.basket.model.ChangeBasketStatusRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.HotelReservationApi
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CancelReservationRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationBookingChannel
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRoomRateRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRoomRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.FindBookingRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_CANCEL_RESERVATION_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.cancelReservationFailure
import uk.co.whitbread.integrationtests.stubs.opera.getReservation
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
import uk.co.whitbread.integrationtests.testkit.model.OperaPaymentCard
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

// Setup-only pins, copied from ConfirmReservationSpec / CreateReservationGuestSpec: they hold the
// created basket on the plain pay-on-arrival shape this endpoint acts on.
private val createReservationFlagPins: Map<FeatureFlag, Boolean> =
    mapOf(
        OhipFeatureFlag.SET_DEFAULT_PAYMENT_METHOD_DS to false,
        HotelReservationFeatureFlag.CITY_TAX_UK to false,
        HotelReservationFeatureFlag.APPLY_OCCUPANCY_SUPPLEMENT to false,
    )

// Setup-only pins, copied from FindBookingSpec: the manage-booking lookup is the only in-suite
// source of the encrypted `basketReference|epochSeconds` token the cancel path validates.
private val findBookingFlagPins: Map<FeatureFlag, Boolean> =
    mapOf(
        HotelReservationFeatureFlag.PI_SEARCH_BY_OPERA_CONFIRMATION to true,
        OhipFeatureFlag.MOBILE_ACCEPTS_OTA_BOOKING to false,
        HotelReservationFeatureFlag.MOBILE_PRE_REGISTERED_REPURPOSE to false,
    )

// The two-room mismatched-policy Booking is the subject of the flag pair below, and the same
// policy guard also runs inside the setup lookup. The setup therefore always accepts OTA bookings
// so that the guard is exercised by the call under test, not by the setup.
private val otaFindBookingFlagPins: Map<FeatureFlag, Boolean> =
    findBookingFlagPins + (OhipFeatureFlag.MOBILE_ACCEPTS_OTA_BOOKING to true)

// `mobile_preRegistered_repurpose` is pinned false on every scenario: it only forces
// `deRegCardCompleted` to false inside the reservation read and is never read afterwards, so both
// states produce the identical response and downstream call set.
private val cancelReservationFlagPins: Map<FeatureFlag, Boolean> =
    mapOf(
        HotelReservationFeatureFlag.MOBILE_PRE_REGISTERED_REPURPOSE to false,
        OhipFeatureFlag.MOBILE_ACCEPTS_OTA_BOOKING to false,
    )

private val otaCancelReservationFlagPins: Map<FeatureFlag, Boolean> =
    cancelReservationFlagPins + (OhipFeatureFlag.MOBILE_ACCEPTS_OTA_BOOKING to true)

/**
 * Proves the manage-booking cancellation: `POST /v1/reservations/cancellations` authorizes the
 * caller with the manage-booking token, reads the created basket's Opera reservations, cancels
 * each of them in Opera and cancels the basket — and rejects the request before any Opera write
 * when a reservation is no longer cancellable, when its reservations report differing deposit policy codes, or
 * when Opera refuses the cancellation.
 *
 * Flow: backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/CreateReservationCancellations.md
 */
class CancelReservationSpec :
    JourneySpec(
        "manage-booking baskets can be cancelled",
        {
            val hotelReservationApi = HotelReservationApi()
            val basketApi = BasketApi()

            scenario("a pay-on-arrival basket is cancelled in Opera and the basket is cancelled") {
                val booking = cancellableBooking()

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Reservation To Cancel")

                expect("creates the basket the cancellation acts on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                }

                val basket =
                    basketApi.getBasket(
                        basketReference = createdReservation.body.basketReference,
                        testId = testId,
                    )

                basket.attachEvidence("Read Basket To Cancel")

                val bookingReference = basket.body.bookingReference.shouldNotBeNull()

                val completedBasket =
                    basketApi.changeStatus(
                        bookingReference = bookingReference,
                        request = ChangeBasketStatusRequest(status = "COMPLETED"),
                        testId = testId,
                    )

                completedBasket.attachEvidence("Complete Basket To Cancel")

                expect("makes the basket eligible for the manage-booking lookup") {
                    completedBasket.response.status.value shouldBe 200
                    completedBasket.body.status shouldBe "COMPLETED"
                }

                val foundBooking =
                    hotelReservationApi.findBooking(
                        request = findBookingRequest(booking, bookingReference),
                        testId = testId,
                        featureFlagOverrides = findBookingFlagPins,
                    )

                foundBooking.attachEvidence("Find Booking For Cancellation Token")

                val token = foundBooking.body.token.shouldNotBeNull()

                val result =
                    hotelReservationApi.cancelReservation(
                        request =
                            CancelReservationRequest(
                                basketReference = createdReservation.body.basketReference,
                                hotelId = booking.hotel.hotelId,
                                token = token,
                            ),
                        testId = testId,
                        featureFlagOverrides = cancelReservationFlagPins,
                    )

                result.attachEvidence("Cancel Reservation")

                expect("returns the cancelled basket reference") {
                    result.response.status.value shouldBe 200
                    result.body.basketReference shouldBe createdReservation.body.basketReference
                }

                expect("cancels the reservation in Opera without touching payments") {
                    // POST /rsv/v1/hotels/{hotelId}/reservations/{reservationId}/cancellations.
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 1
                    // The reservation PUT is a setup-create call only, and Opera's deposit-folio
                    // POST is never made: the cancellation adds neither, unlike the matrix claim.
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 0
                    // No total Opera count and no AEM count here: nothing paid means the refund
                    // step is reached and skipped, so the cancellation goes on to trigger
                    // basket-service's asynchronous cancellation email. That email re-reads the
                    // reservation from Opera after this response has returned, so the totals race
                    // the assertion while the counts above stay fixed.
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("the same two-room basket is cancelled when OTA bookings are accepted") {
                val booking = mismatchedPolicyCodeBooking()

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Two Room Reservation To Cancel")

                expect("creates the two-room basket the cancellation acts on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                }

                val basket =
                    basketApi.getBasket(
                        basketReference = createdReservation.body.basketReference,
                        testId = testId,
                    )

                basket.attachEvidence("Read Two Room Basket To Cancel")

                val bookingReference = basket.body.bookingReference.shouldNotBeNull()

                val completedBasket =
                    basketApi.changeStatus(
                        bookingReference = bookingReference,
                        request = ChangeBasketStatusRequest(status = "COMPLETED"),
                        testId = testId,
                    )

                completedBasket.attachEvidence("Complete Two Room Basket To Cancel")

                expect("makes the two-room basket eligible for the manage-booking lookup") {
                    completedBasket.response.status.value shouldBe 200
                    completedBasket.body.status shouldBe "COMPLETED"
                }

                val foundBooking =
                    hotelReservationApi.findBooking(
                        request = findBookingRequest(booking, bookingReference),
                        testId = testId,
                        featureFlagOverrides = otaFindBookingFlagPins,
                    )

                foundBooking.attachEvidence("Find Two Room Booking For Cancellation Token")

                val token = foundBooking.body.token.shouldNotBeNull()

                val result =
                    hotelReservationApi.cancelReservation(
                        request =
                            CancelReservationRequest(
                                basketReference = createdReservation.body.basketReference,
                                hotelId = booking.hotel.hotelId,
                                token = token,
                            ),
                        testId = testId,
                        featureFlagOverrides = otaCancelReservationFlagPins,
                    )

                result.attachEvidence("Cancel Two Room Reservation Ota Accepted")

                expect("returns the cancelled basket reference") {
                    result.response.status.value shouldBe 200
                    result.body.basketReference shouldBe createdReservation.body.basketReference
                }

                expect("cancels both Opera reservations") {
                    // One POST
                    // /rsv/v1/hotels/{hotelId}/reservations/{reservationId}/cancellations per
                    // reservation.
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 2
                    // Both reservation PUTs are setup-create calls only.
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 0
                    // The totals are left unasserted for the same reason as the single-room
                    // cancellation: the asynchronous cancellation email re-reads both reservations
                    // after this response returns.
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a two-room basket with mismatched deposit policy codes is rejected before any Opera cancellation") {
                val booking = mismatchedPolicyCodeBooking()

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Two Room Reservation")

                expect("creates the two-room basket the cancellation acts on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                }

                val basket =
                    basketApi.getBasket(
                        basketReference = createdReservation.body.basketReference,
                        testId = testId,
                    )

                basket.attachEvidence("Read Two Room Basket")

                val bookingReference = basket.body.bookingReference.shouldNotBeNull()

                val completedBasket =
                    basketApi.changeStatus(
                        bookingReference = bookingReference,
                        request = ChangeBasketStatusRequest(status = "COMPLETED"),
                        testId = testId,
                    )

                completedBasket.attachEvidence("Complete Two Room Basket")

                expect("makes the two-room basket eligible for the manage-booking lookup") {
                    completedBasket.response.status.value shouldBe 200
                    completedBasket.body.status shouldBe "COMPLETED"
                }

                val foundBooking =
                    hotelReservationApi.findBooking(
                        request = findBookingRequest(booking, bookingReference),
                        testId = testId,
                        featureFlagOverrides = otaFindBookingFlagPins,
                    )

                foundBooking.attachEvidence("Find Two Room Booking For Token")

                val token = foundBooking.body.token.shouldNotBeNull()

                val result =
                    hotelReservationApi.cancelReservation(
                        request =
                            CancelReservationRequest(
                                basketReference = createdReservation.body.basketReference,
                                hotelId = booking.hotel.hotelId,
                                token = token,
                            ),
                        testId = testId,
                        featureFlagOverrides = cancelReservationFlagPins,
                    )

                result.attachEvidence("Cancel Two Room Reservation Ota Refused")

                expect("rejects the cancellation with the policy-code mismatch error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 19
                }

                expect("cancels nothing in Opera") {
                    // Twenty-seven Opera calls: the setup create per room, the setup lookup per
                    // room, and the cancellation's own read per room. The rejection lands while
                    // the adapter maps that read, so it never reaches the Front Desk card lookup
                    // the completed setup lookup made.
                    callCount(Upstream.OPERA) shouldBe 27
                    // The rejection lands inside the reservation read, before any Opera write.
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 0
                    // Both reservation PUTs are setup-create calls only.
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a checked-in reservation is refused and nothing is cancelled in Opera") {
                val booking = cancellableBooking()
                val room = booking.room

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Reservation To Check In")

                expect("creates the basket the cancellation acts on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                }

                val basket =
                    basketApi.getBasket(
                        basketReference = createdReservation.body.basketReference,
                        testId = testId,
                    )

                basket.attachEvidence("Read Basket To Check In")

                val bookingReference = basket.body.bookingReference.shouldNotBeNull()

                val completedBasket =
                    basketApi.changeStatus(
                        bookingReference = bookingReference,
                        request = ChangeBasketStatusRequest(status = "COMPLETED"),
                        testId = testId,
                    )

                completedBasket.attachEvidence("Complete Basket To Check In")

                expect("makes the basket eligible for the manage-booking lookup") {
                    completedBasket.response.status.value shouldBe 200
                    completedBasket.body.status shouldBe "COMPLETED"
                }

                val foundBooking =
                    hotelReservationApi.findBooking(
                        request = findBookingRequest(booking, bookingReference),
                        testId = testId,
                        featureFlagOverrides = findBookingFlagPins,
                    )

                foundBooking.attachEvidence("Find Booking Before Check In")

                val token = foundBooking.body.token.shouldNotBeNull()

                // Exceptional temporal override: creation and the token lookup must see RESERVED,
                // while the cancellation must see that Opera has since checked the guest in.
                installStub(
                    getReservation(
                        booking = booking,
                        room = room.copy(status = ReservationStatus.CHECKED_IN),
                    ),
                )

                val result =
                    hotelReservationApi.cancelReservation(
                        request =
                            CancelReservationRequest(
                                basketReference = createdReservation.body.basketReference,
                                hotelId = booking.hotel.hotelId,
                                token = token,
                            ),
                        testId = testId,
                        featureFlagOverrides = cancelReservationFlagPins,
                    )

                result.attachEvidence("Cancel Checked In Reservation")

                expect("rejects the cancellation of a checked-in reservation") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 122
                }

                expect("cancels nothing in Opera") {
                    // Sixteen Opera calls: the setup create uses rate-info, hotel config, the
                    // reservation POST and the reservation PUT; the setup lookup and the
                    // cancellation's own read each load reservation, rate-info amounts, folios,
                    // hotel config, profile and Front Desk card details.
                    callCount(Upstream.OPERA) shouldBe 16
                    // The status guard runs after the read and before every Opera write.
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 0
                    // The reservation PUT is a setup-create call only, and Opera's deposit-folio
                    // POST is never made anywhere on this journey.
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera cancellation rejection is mapped and leaves the basket uncancelled") {
                val booking = cancellableBooking()

                // A downstream failure is exceptional behavior, not a normal Booking world state.
                installFor(booking, excluded = setOf(OPERA_CANCEL_RESERVATION_STUB_ID))
                installStub(cancelReservationFailure(booking))

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Reservation Opera Rejects")

                expect("creates the basket the cancellation acts on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                }

                val basket =
                    basketApi.getBasket(
                        basketReference = createdReservation.body.basketReference,
                        testId = testId,
                    )

                basket.attachEvidence("Read Basket Opera Rejects")

                val bookingReference = basket.body.bookingReference.shouldNotBeNull()

                val completedBasket =
                    basketApi.changeStatus(
                        bookingReference = bookingReference,
                        request = ChangeBasketStatusRequest(status = "COMPLETED"),
                        testId = testId,
                    )

                completedBasket.attachEvidence("Complete Basket Opera Rejects")

                expect("makes the basket eligible for the manage-booking lookup") {
                    completedBasket.response.status.value shouldBe 200
                    completedBasket.body.status shouldBe "COMPLETED"
                }

                val foundBooking =
                    hotelReservationApi.findBooking(
                        request = findBookingRequest(booking, bookingReference),
                        testId = testId,
                        featureFlagOverrides = findBookingFlagPins,
                    )

                foundBooking.attachEvidence("Find Booking Opera Rejects")

                val token = foundBooking.body.token.shouldNotBeNull()

                val result =
                    hotelReservationApi.cancelReservation(
                        request =
                            CancelReservationRequest(
                                basketReference = createdReservation.body.basketReference,
                                hotelId = booking.hotel.hotelId,
                                token = token,
                            ),
                        testId = testId,
                        featureFlagOverrides = cancelReservationFlagPins,
                    )

                result.attachEvidence("Cancel Reservation Opera Rejects")

                expect("propagates the adapter's cancellation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 950
                }

                expect("attempts the Opera cancellation once and stops") {
                    // Twenty Opera calls: the same legs as a successful cancellation — the
                    // adapter still completes its own reservation, folios and card reads before
                    // posting the cancellation Opera then rejects.
                    callCount(Upstream.OPERA) shouldBe 20
                    // The rejected POST
                    // /rsv/v1/hotels/{hotelId}/reservations/{reservationId}/cancellations.
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

/**
 * A single pay-on-arrival room: `amountAlreadyPaid` stays at its `0.0` default and the room
 * carries an Opera payment card, which keeps the unstubbed basket refund and ThreeC legs off the
 * cancellation path.
 */
private fun cancellableBooking(): Booking {
    val arrival = LocalDate.now().plusDays(28)
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms = listOf(cancellableRoom(reservationId = "6004301", profileId = "PROF-6431")),
        aem = cancelReservationAem(),
    )
}

/**
 * Two rooms reporting different Opera deposit policy codes, the state the adapter's cross-basket
 * policy guard rejects. The Booking carries no `bookingReference`, so the Opera reservations
 * report no `externalReferences` and the adapter reads a null `idContext` — the input that makes
 * the OTA branch of that guard treat the basket as third-party.
 */
private fun mismatchedPolicyCodeBooking(): Booking {
    val booking = cancellableBooking()

    return booking.copy(
        rooms =
            listOf(
                cancellableRoom(reservationId = "6004302", profileId = "PROF-6432"),
                cancellableRoom(
                    reservationId = "6004303",
                    profileId = "PROF-6433",
                    depositPolicyCode = "PREPAY",
                ),
            ),
    )
}

private fun cancellableRoom(
    reservationId: String,
    profileId: String,
    depositPolicyCode: String = "DEP",
): BookingRoom =
    BookingRoom(
        reservationId = reservationId,
        roomType = "LOWDBL",
        adults = 2,
        status = ReservationStatus.RESERVED,
        sourceCode = "44",
        depositPolicyCode = depositPolicyCode,
        guestProfile =
            GuestProfile(
                profileId = profileId,
                firstName = "Robin",
                lastName = "Fletcher",
                email = "robin.fletcher.$reservationId@test.com",
                phone = "+447700900031",
                addressLine = "31 High Street",
                city = "London",
                postcode = "SW1A 1AC",
            ),
        operaPaymentCard =
            OperaPaymentCard(
                cardId = "12345",
                cardType = "Va",
                cardNumber = "4764776852337921103",
                expirationDate = "2030-03-31",
                cardHolderName = "Robin Fletcher",
            ),
    )

private fun cancelReservationAem(): Aem =
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

/**
 * The basket creation mints carries a null `paymentOption`, and a null payment option over
 * reservations reporting nothing paid resolves to `PAY_ON_ARRIVAL`, keeping the pay-now
 * deposit-folio and content-service legs off the cancellation path. The channel is `PI` because
 * the deployed rules service answers no channel rule for a `DISTR` creation.
 */
private fun createReservationRequest(booking: Booking): CreateReservationRequest {
    val rate = booking.hotel.availableRates.single()
    val arrival = requireNotNull(booking.arrival).toString()
    val departure = requireNotNull(booking.departure).toString()

    return CreateReservationRequest(
        reservations =
            booking.rooms.map { room ->
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
                )
            },
        bookingChannel =
            CreateReservationBookingChannel(
                channel = "PI",
                subchannel = "WEB",
                language = "EN",
            ),
        bookingFlowId = "hre-cancel-reservation",
    )
}

private fun findBookingRequest(
    booking: Booking,
    bookingReference: String,
): FindBookingRequest =
    FindBookingRequest(
        resNo = bookingReference,
        arrivalDate = requireNotNull(booking.arrival).toString(),
        lastName = requireNotNull(booking.rooms.first().guestProfile).lastName,
        channel = "DISTR",
        subchannel = "WEB",
    )
