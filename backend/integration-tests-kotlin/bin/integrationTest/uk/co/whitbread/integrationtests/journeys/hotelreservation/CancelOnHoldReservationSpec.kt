package uk.co.whitbread.integrationtests.journeys.hotelreservation

import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import io.kotest.matchers.string.shouldNotBeBlank
import uk.co.whitbread.integrationtests.clients.basket.BasketApi
import uk.co.whitbread.integrationtests.clients.basket.model.ChangeBasketStatusRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.HotelReservationApi
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CancelReservationRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationBookingChannel
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRoomRateRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRoomRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_CANCEL_RESERVATION_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.cancelReservationFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.cancelledReservationLookup
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.HotelReservationFeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
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

// The two flags the flow doc lists on this path, pinned OFF. `mobile_preRegistered_repurpose`
// only forces `deRegCardCompleted` to false on a DTO field the on-hold response does not carry,
// and `mobile_accepts_ota_booking` only excuses non-unique deposit policy codes across two or more
// reservations — unreachable on a single-room basket. The ON pair below proves that.
private val cancelOnHoldFlagPins: Map<FeatureFlag, Boolean> =
    mapOf(
        HotelReservationFeatureFlag.MOBILE_PRE_REGISTERED_REPURPOSE to false,
        OhipFeatureFlag.MOBILE_ACCEPTS_OTA_BOOKING to false,
    )

private val enabledCancelOnHoldFlagPins: Map<FeatureFlag, Boolean> =
    mapOf(
        HotelReservationFeatureFlag.MOBILE_PRE_REGISTERED_REPURPOSE to true,
        OhipFeatureFlag.MOBILE_ACCEPTS_OTA_BOOKING to true,
    )

/**
 * Proves the abandon-the-hold cancellation: `PUT /v1/reservations/cancellations/on-hold` cancels a
 * created basket's Opera reservations and closes the basket only when every one of them is still
 * on hold in Opera, answers `200` with an empty body and no Opera write when they are not, and
 * rejects the request when the basket is no longer OPEN, when a reservation is already cancelled,
 * or when Opera refuses the cancellation — never touching the refund, deposit or email legs.
 *
 * Flow: backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/CancelOnHoldReservations.md
 */
class CancelOnHoldReservationSpec :
    JourneySpec(
        "on-hold baskets can be abandoned",
        {
            val hotelReservationApi = HotelReservationApi()
            val basketApi = BasketApi()

            scenario("an OPEN basket whose Opera reservations are all on hold is cancelled and the basket closed") {
                val booking = onHoldBooking(reservationId = "6004801", heldInOpera = true)

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Reservation To Abandon")

                expect("creates the OPEN basket the abandon acts on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE
                }

                val result =
                    hotelReservationApi.cancelOnHoldReservation(
                        request =
                            CancelReservationRequest(
                                basketReference = createdReservation.body.basketReference,
                                hotelId = booking.hotel.hotelId,
                            ),
                        testId = testId,
                        featureFlagOverrides = cancelOnHoldFlagPins,
                    )

                result.attachEvidence("Cancel On Hold Reservation")

                expect("returns the abandoned basket reference") {
                    result.response.status.value shouldBe 200
                    result.body.basketReference shouldBe createdReservation.body.basketReference
                }

                expect("reads the reservation and cancels it in Opera without any payment work") {
                    // Five Opera calls beyond the setup create: the reservations read (reservation,
                    // hotel config, profile, Front Desk card details) and the cancellation POST.
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE + 5
                    // GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}, once for the
                    // setup create and once for the abandon's own read.
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe OPERA_GET_RESERVATION_AFTER_CREATE + 1
                    // GET /ent/config/v1/hotels/{hotelId}?fetchInstructions=General.
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe OPERA_HOTEL_CONFIG_AFTER_CREATE + 1
                    // GET /crm/v1/profiles/{profileId}.
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    // GET /fof/config/v1/creditCardInfo; the card is read but never used, because
                    // the cancel runs with a null payment option.
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 1
                    // POST /rsv/v1/hotels/{hotelId}/reservations/{reservationId}/cancellations.
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 1
                    // The reservations read runs with rateInfoNeeded=false and the cancel with a
                    // null payment option, so the rate-info, deposit-folio, deposit and
                    // reservation-PUT mappings installFor really did install stay untouched beyond
                    // the setup create.
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe OPERA_GET_RATE_INFO_AFTER_CREATE
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe OPERA_UPDATE_RESERVATION_AFTER_CREATE
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 0
                    callCount(OperaEndpoint.GET_RESERVATION_DEPOSITS) shouldBe 0
                    // The refund and email legs are unreachable here: no processRefund call, and
                    // the basket cancel is sent with sendMail=false.
                    callCount(Upstream.WORLDLINE) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                }
            }

            scenario("the same on-hold basket is cancelled with both path flags enabled") {
                val booking = onHoldBooking(reservationId = "6004802", heldInOpera = true)

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Reservation To Abandon Flags On")

                expect("creates the OPEN basket the abandon acts on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE
                }

                val result =
                    hotelReservationApi.cancelOnHoldReservation(
                        request =
                            CancelReservationRequest(
                                basketReference = createdReservation.body.basketReference,
                                hotelId = booking.hotel.hotelId,
                            ),
                        testId = testId,
                        featureFlagOverrides = enabledCancelOnHoldFlagPins,
                    )

                result.attachEvidence("Cancel On Hold Reservation Flags On")

                expect("returns the abandoned basket reference exactly as with the flags off") {
                    result.response.status.value shouldBe 200
                    result.body.basketReference shouldBe createdReservation.body.basketReference
                }

                expect("makes exactly the same Opera calls as with the flags off") {
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE + 5
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe OPERA_GET_RESERVATION_AFTER_CREATE + 1
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe OPERA_HOTEL_CONFIG_AFTER_CREATE + 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 1
                    // POST /rsv/v1/hotels/{hotelId}/reservations/{reservationId}/cancellations.
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 1
                    callCount(Upstream.WORLDLINE) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                }
            }

            scenario("a basket whose Opera reservations are not on hold is a silent no-op") {
                val booking = onHoldBooking(reservationId = "6004803")

                // No on-hold override: the frozen default reservation read emits
                // roomStay.guarantee.onHold = false, which is exactly the state this proves.
                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Reservation Not On Hold")

                expect("creates the OPEN basket the abandon would act on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE
                }

                val result =
                    hotelReservationApi.cancelOnHoldReservationNoContent(
                        request =
                            CancelReservationRequest(
                                basketReference = createdReservation.body.basketReference,
                                hotelId = booking.hotel.hotelId,
                            ),
                        testId = testId,
                        featureFlagOverrides = cancelOnHoldFlagPins,
                    )

                result.attachEvidence("Cancel On Hold Reservation Not On Hold")

                expect("answers success with no body at all") {
                    result.response.status.value shouldBe 200
                    result.bodyText.shouldBeEmpty()
                }

                expect("reads the reservations but cancels nothing in Opera") {
                    // Four Opera calls beyond the setup create: the reservations read still runs
                    // in full, and only the cancellation is skipped.
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE + 4
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe OPERA_GET_RESERVATION_AFTER_CREATE + 1
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe OPERA_HOTEL_CONFIG_AFTER_CREATE + 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 1
                    // The point of the scenario: the cancellation mapping installFor really did
                    // install is never called, so the hold gate really is what drives the cancel.
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                }
            }

            scenario("a basket that is no longer OPEN is rejected before any Opera call") {
                val booking = onHoldBooking(reservationId = "6004804", heldInOpera = true)

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Reservation To Complete")

                expect("creates the basket the abandon is refused on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE
                }

                val basket =
                    basketApi.getBasket(
                        basketReference = createdReservation.body.basketReference,
                        testId = testId,
                    )

                basket.attachEvidence("Read Basket To Complete")

                val completedBasket =
                    basketApi.changeStatus(
                        bookingReference = basket.body.bookingReference.shouldNotBeNull(),
                        request = ChangeBasketStatusRequest(status = "COMPLETED"),
                        testId = testId,
                    )

                completedBasket.attachEvidence("Complete Basket Before Abandon")

                expect("moves the basket out of OPEN") {
                    completedBasket.response.status.value shouldBe 200
                    completedBasket.body.status shouldBe "COMPLETED"
                }

                val result =
                    hotelReservationApi.cancelOnHoldReservation(
                        request =
                            CancelReservationRequest(
                                basketReference = createdReservation.body.basketReference,
                                hotelId = booking.hotel.hotelId,
                            ),
                        testId = testId,
                        featureFlagOverrides = cancelOnHoldFlagPins,
                    )

                result.attachEvidence("Cancel On Hold Reservation Completed Basket")

                expect("rejects the abandon with the basket-status error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe DIGITAL_BASKET_RIGHT_STATUS_ERR_CODE
                }

                expect("never reaches Opera at all") {
                    // The basket read is the first thing the in-port does, so the Opera traffic is
                    // still exactly the setup create's.
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe OPERA_GET_RESERVATION_AFTER_CREATE
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                }
            }

            scenario("an already-cancelled Opera reservation is rejected after the reservations read") {
                val booking = onHoldBooking(reservationId = "6004805")

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Reservation Already Cancelled")

                expect("creates the OPEN basket the abandon is refused on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE
                }

                // Exceptional temporal override: the setup create must see a live reservation,
                // while the abandon must see that Opera has since cancelled it.
                installStub(cancelledReservationLookup(booking))

                val result =
                    hotelReservationApi.cancelOnHoldReservation(
                        request =
                            CancelReservationRequest(
                                basketReference = createdReservation.body.basketReference,
                                hotelId = booking.hotel.hotelId,
                            ),
                        testId = testId,
                        featureFlagOverrides = cancelOnHoldFlagPins,
                    )

                result.attachEvidence("Cancel On Hold Reservation Already Cancelled")

                expect("rejects the abandon of an already-cancelled reservation") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe DIGITAL_CANCELLED_ERR_CODE
                }

                expect("cancels nothing in Opera") {
                    // The same four-call reservations read as the happy path: the adapter maps
                    // the cancelled reservation in full before the in-port's status guard sees it.
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE + 4
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe OPERA_GET_RESERVATION_AFTER_CREATE + 1
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe OPERA_HOTEL_CONFIG_AFTER_CREATE + 1
                    callCount(OperaEndpoint.GET_PROFILE) shouldBe 1
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 1
                    // The status guard runs after the read and before every Opera write.
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                }
            }

            scenario("an Opera cancellation rejection leaves the basket uncancelled") {
                val booking = onHoldBooking(reservationId = "6004806", heldInOpera = true)

                // A downstream failure is exceptional behavior, not a normal Booking world state.
                installFor(booking, excluded = setOf(OPERA_CANCEL_RESERVATION_STUB_ID))
                installStub(cancelReservationFailure(booking))

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Reservation Opera Rejects Abandon")

                expect("creates the OPEN basket the abandon acts on") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE
                }

                val result =
                    hotelReservationApi.cancelOnHoldReservation(
                        request =
                            CancelReservationRequest(
                                basketReference = createdReservation.body.basketReference,
                                hotelId = booking.hotel.hotelId,
                            ),
                        testId = testId,
                        featureFlagOverrides = cancelOnHoldFlagPins,
                    )

                result.attachEvidence("Cancel On Hold Reservation Opera Rejects")

                expect("propagates the adapter's cancellation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe OHIP_CANCEL_RESERVATION_ERR_CODE
                }

                expect("attempts the Opera cancellation once and stops") {
                    // The same five calls as the happy path: the read completes, the cancellation
                    // is posted, and Opera's rejection ends the journey before the basket cancel.
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE + 5
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe OPERA_GET_RESERVATION_AFTER_CREATE + 1
                    // The rejected POST
                    // /rsv/v1/hotels/{hotelId}/reservations/{reservationId}/cancellations.
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                }
            }
        },
    )

/**
 * Opera cost of the setup create, measured rather than derived. Itemizing it belongs to
 * CreateReservationSpec; these scenarios only need it fixed so the abandon's cost is the delta.
 */
private const val OPERA_CALLS_AFTER_CREATE = 4

/**
 * How that setup cost splits per Opera endpoint, so each per-endpoint assertion states the
 * abandon's own delta rather than a bare total. The setup create never reads the reservation back
 * and never touches profiles or Front Desk card details.
 */
private const val OPERA_GET_RESERVATION_AFTER_CREATE = 0
private const val OPERA_HOTEL_CONFIG_AFTER_CREATE = 1
private const val OPERA_GET_RATE_INFO_AFTER_CREATE = 1
private const val OPERA_UPDATE_RESERVATION_AFTER_CREATE = 1

/**
 * hotel-reservation-entity-service's `DIGITAL_BASKET_RIGHT_STATUS_EXCEPTION`, raised as a
 * `GenericReservationException` and therefore surfacing as HTTP 500.
 */
private const val DIGITAL_BASKET_RIGHT_STATUS_ERR_CODE = 80

/** hotel-reservation-entity-service's `DIGITAL_CANCELLED_EXCEPTION`. */
private const val DIGITAL_CANCELLED_ERR_CODE = 83

/** ohip-adapter's `OHIP_CANCEL_RESERVATION_EXCEPTION`, declared as code 950 in its `ErrorCode`. */
private const val OHIP_CANCEL_RESERVATION_ERR_CODE = 950

/**
 * A single pay-on-arrival room carrying an Opera payment card.
 *
 * `amountAlreadyPaid` stays at its `0.0` default, which resolves the created basket to
 * `PAY_ON_ARRIVAL`; the endpoint forces `paymentOption` to null anyway, so this only keeps the
 * setup create on the plain shape. The card is what makes the reservations read fetch Front Desk
 * card details.
 *
 * [heldInOpera] is the fact the endpoint gates on: it sets `roomStay.guarantee.onHold` on the
 * Opera reservation read, which is what `isOnHold` filters the basket's reservations by. It is
 * unrelated to [ReservationStatus.ON_HOLD], which drives the Opera reservation *status*.
 */
private fun onHoldBooking(
    reservationId: String,
    heldInOpera: Boolean = false,
): Booking {
    val arrival = LocalDate.now().plusDays(21)
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms =
            listOf(
                BookingRoom(
                    reservationId = reservationId,
                    roomType = rate.roomType,
                    adults = rate.adults,
                    status = ReservationStatus.RESERVED,
                    heldInOpera = heldInOpera,
                    sourceCode = "44",
                    // Required by the create setup: without it the Opera reservation stub invents
                    // a TEMP profile id that no profile gate installs.
                    guestProfile =
                        GuestProfile(
                            profileId = "8004$reservationId",
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
                ),
            ),
    )
}

/**
 * The create that mints the OPEN basket the abandon acts on. The channel is `PI` because the
 * deployed rules service answers no channel rule for a `DISTR` creation.
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
        bookingFlowId = "hre-cancel-on-hold",
    )
}
