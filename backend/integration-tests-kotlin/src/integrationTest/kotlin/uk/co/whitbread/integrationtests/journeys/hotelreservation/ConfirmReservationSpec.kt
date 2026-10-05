package uk.co.whitbread.integrationtests.journeys.hotelreservation

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeBlank
import uk.co.whitbread.integrationtests.clients.basket.BasketApi
import uk.co.whitbread.integrationtests.clients.hotelreservation.HotelReservationApi
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.ConfirmReservationPaymentCard
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.ConfirmReservationPaymentOption
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.ConfirmReservationRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationBookingChannel
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRoomRateRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRoomRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.putReservationBadRequest
import uk.co.whitbread.integrationtests.stubs.opera.getReservations
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.HotelReservationFeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.model.RoutingInstruction
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

private val confirmReservationFeatureFlags: Map<FeatureFlag, Boolean> =
    mapOf(
        HotelReservationFeatureFlag.MOBILE_PRE_REGISTERED_REPURPOSE to false,
        OhipFeatureFlag.DEPOSIT_FOLIO_POST_AFTER_DISABLE_ON_HOLD to false,
        OhipFeatureFlag.SET_CNP_BOOKING_ALERTS to false,
        OhipFeatureFlag.SET_DEFAULT_PAYMENT_METHOD_DS to false,
    )
private val confirmReservationWithCnpAlertsFeatureFlags: Map<FeatureFlag, Boolean> =
    mapOf(
        HotelReservationFeatureFlag.MOBILE_PRE_REGISTERED_REPURPOSE to false,
        OhipFeatureFlag.DEPOSIT_FOLIO_POST_AFTER_DISABLE_ON_HOLD to false,
        OhipFeatureFlag.SET_CNP_BOOKING_ALERTS to true,
        OhipFeatureFlag.SET_DEFAULT_PAYMENT_METHOD_DS to false,
    )

private val confirmPayNowReservationFeatureFlags: Map<FeatureFlag, Boolean> =
    mapOf(
        HotelReservationFeatureFlag.MOBILE_PRE_REGISTERED_REPURPOSE to false,
        OhipFeatureFlag.MOBILE_ACCEPTS_OTA_BOOKING to false,
        OhipFeatureFlag.DEPOSIT_FOLIO_POST_AFTER_DISABLE_ON_HOLD to false,
        OhipFeatureFlag.SET_CNP_BOOKING_ALERTS to false,
        OhipFeatureFlag.SET_DEFAULT_PAYMENT_METHOD_DS to false,
    )

/**
 * Flags pinned on the setup create so the basket this journey confirms against is built by the
 * same path every run: CA payment method to match the Opera create stubs, no city-tax content
 * lookup, and the plain single-room occupancy path.
 */
private val createReservationFlagPins: Map<FeatureFlag, Boolean> =
    mapOf(
        OhipFeatureFlag.SET_DEFAULT_PAYMENT_METHOD_DS to false,
        HotelReservationFeatureFlag.CITY_TAX_UK to false,
        HotelReservationFeatureFlag.APPLY_OCCUPANCY_SUPPLEMENT to false,
    )

class ConfirmReservationSpec :
    JourneySpec(
        "Hotel Reservation Entity reservations can be confirmed",
        {
            val hotelReservationApi = HotelReservationApi()
            val basketApi = BasketApi()

            scenario("a pay-on-arrival reservation is confirmed through OHIP") {
                val arrival = LocalDate.now().plusDays(14)
                val departure = arrival.plusDays(2)
                val paymentType = "VA"
                val booking =
                    Booking(
                        hotels =
                            listOf(
                                Hotels.HEAPTI.copy(
                                    availableRates =
                                        listOf(
                                            Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2),
                                        ),
                                ),
                            ),
                        arrival = arrival,
                        departure = departure,
                        rooms =
                            listOf(
                                BookingRoom(
                                    reservationId = "6004101",
                                    roomType = "LOWDBL",
                                    adults = 2,
                                    status = ReservationStatus.RESERVED,
                                    guestProfile =
                                        GuestProfile(
                                            profileId = "PROF-6411",
                                            firstName = "Ava",
                                            lastName = "Bennett",
                                            email = "ava.bennett@test.com",
                                            phone = "+447700900011",
                                            addressLine = "11 High Street",
                                            city = "London",
                                            postcode = "SW1A 1AA",
                                        ),
                                ),
                            ),
                    )
                val room = booking.room

                installFor(booking)

                val result =
                    hotelReservationApi.confirmReservation(
                        request =
                            ConfirmReservationRequest(
                                reservationId = room.reservationId!!,
                                hotelId = booking.hotel.hotelId,
                                paymentOption = ConfirmReservationPaymentOption.PAY_ON_ARRIVAL,
                                paymentType = paymentType,
                                paymentCard =
                                    ConfirmReservationPaymentCard(
                                        cardType = paymentType,
                                        token = "hre-confirm-reservation-card-token",
                                        expirationDate = "2030-12-31",
                                        cardHolderName = "Ava Bennett",
                                        cardNumberLast4Digits = "1111",
                                    ),
                            ),
                        testId = testId,
                        featureFlagOverrides = confirmReservationFeatureFlags,
                    )

                result.attachEvidence("Confirm Pay On Arrival Reservation Through HRE")

                expect("returns the non-partial confirmed reservation") {
                    result.response.status.value shouldBe 200
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.reservationStatus shouldBe room.status.name
                    result.body.isPartialPaid shouldBe false
                    result.body.reservationIdList
                        ?.first()
                        ?.id shouldBe room.reservationId
                    result.body.roomStay?.arrivalDate shouldBe booking.arrival
                    result.body.roomStay?.departureDate shouldBe booking.departure
                    result.body.reservationGuest?.givenName shouldBe room.guestProfile?.firstName
                    result.body.reservationGuest?.surName shouldBe room.guestProfile?.lastName
                }

                expect("delegates confirmation to OHIP without other upstream systems") {
                    // Three Opera calls through OHIP: initial reservation GET, confirmation PUT,
                    // and the post-confirmation GET used to detect CNP routing.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a CNP reservation receives a check-in alert through OHIP") {
                val arrival = LocalDate.now().plusDays(14)
                val departure = arrival.plusDays(2)
                val paymentType = "VA"
                val booking =
                    Booking(
                        hotels =
                            listOf(
                                Hotels.HEAPTI.copy(
                                    availableRates =
                                        listOf(
                                            Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2),
                                        ),
                                ),
                            ),
                        arrival = arrival,
                        departure = departure,
                        rooms =
                            listOf(
                                BookingRoom(
                                    reservationId = "6004102",
                                    roomType = "LOWDBL",
                                    adults = 2,
                                    status = ReservationStatus.RESERVED,
                                    guestProfile =
                                        GuestProfile(
                                            profileId = "PROF-6412",
                                            firstName = "Morgan",
                                            lastName = "Reed",
                                            email = "morgan.reed.hre@test.com",
                                            phone = "+447700900012",
                                            addressLine = "12 High Street",
                                            city = "London",
                                            postcode = "SW1A 1AA",
                                        ),
                                    routingInstructions =
                                        listOf(
                                            RoutingInstruction(folioWindowNumber = 2),
                                        ),
                                ),
                            ),
                    )
                val room = booking.room

                installFor(booking)

                val result =
                    hotelReservationApi.confirmReservation(
                        request =
                            ConfirmReservationRequest(
                                reservationId = room.reservationId!!,
                                hotelId = booking.hotel.hotelId,
                                paymentOption = ConfirmReservationPaymentOption.PAY_ON_ARRIVAL,
                                paymentType = paymentType,
                                paymentCard =
                                    ConfirmReservationPaymentCard(
                                        cardType = paymentType,
                                        token = "hre-confirm-cnp-card-token",
                                        expirationDate = "2030-12-31",
                                        cardHolderName = "Morgan Reed",
                                        cardNumberLast4Digits = "1111",
                                    ),
                            ),
                        testId = testId,
                        featureFlagOverrides = confirmReservationWithCnpAlertsFeatureFlags,
                    )

                result.attachEvidence("Confirm CNP Reservation With Check-in Alert Through HRE")

                expect("returns the confirmed CNP reservation") {
                    result.response.status.value shouldBe 200
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.reservationStatus shouldBe room.status.name
                    result.body.isPartialPaid shouldBe false
                    result.body.reservationIdList
                        ?.first()
                        ?.id shouldBe room.reservationId
                    result.body.roomStay?.arrivalDate shouldBe booking.arrival
                    result.body.roomStay?.departureDate shouldBe booking.departure
                }

                expect("propagates the enabled flag and adds the CNP check-in alert") {
                    // Five Opera calls through OHIP: initial reservation GET, confirmation PUT,
                    // CNP-detection GET, payment-details GET, and feature-gated alert PUT.
                    callCount(Upstream.OPERA) shouldBe 5
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a CNP reservation omits the disabled check-in alert through OHIP") {
                val arrival = LocalDate.now().plusDays(14)
                val departure = arrival.plusDays(2)
                val paymentType = "VA"
                val booking =
                    Booking(
                        hotels =
                            listOf(
                                Hotels.HEAPTI.copy(
                                    availableRates =
                                        listOf(
                                            Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2),
                                        ),
                                ),
                            ),
                        arrival = arrival,
                        departure = departure,
                        rooms =
                            listOf(
                                BookingRoom(
                                    reservationId = "6004103",
                                    roomType = "LOWDBL",
                                    adults = 2,
                                    status = ReservationStatus.RESERVED,
                                    guestProfile =
                                        GuestProfile(
                                            profileId = "PROF-6413",
                                            firstName = "Casey",
                                            lastName = "Ellis",
                                            email = "casey.ellis.hre@test.com",
                                            phone = "+447700900013",
                                            addressLine = "13 High Street",
                                            city = "London",
                                            postcode = "SW1A 1AA",
                                        ),
                                    routingInstructions =
                                        listOf(
                                            RoutingInstruction(folioWindowNumber = 2),
                                        ),
                                ),
                            ),
                    )
                val room = booking.room

                installFor(booking)

                val result =
                    hotelReservationApi.confirmReservation(
                        request =
                            ConfirmReservationRequest(
                                reservationId = room.reservationId!!,
                                hotelId = booking.hotel.hotelId,
                                paymentOption = ConfirmReservationPaymentOption.PAY_ON_ARRIVAL,
                                paymentType = paymentType,
                                paymentCard =
                                    ConfirmReservationPaymentCard(
                                        cardType = paymentType,
                                        token = "hre-confirm-cnp-alerts-disabled-card-token",
                                        expirationDate = "2030-12-31",
                                        cardHolderName = "Casey Ellis",
                                        cardNumberLast4Digits = "1111",
                                    ),
                            ),
                        testId = testId,
                        featureFlagOverrides = confirmReservationFeatureFlags,
                    )

                result.attachEvidence("Confirm CNP Reservation Without Check-in Alert Through HRE")

                expect("returns the confirmed CNP reservation") {
                    result.response.status.value shouldBe 200
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.reservationStatus shouldBe room.status.name
                    result.body.isPartialPaid shouldBe false
                    result.body.reservationIdList
                        ?.first()
                        ?.id shouldBe room.reservationId
                    result.body.roomStay?.arrivalDate shouldBe booking.arrival
                    result.body.roomStay?.departureDate shouldBe booking.departure
                }

                expect("propagates the disabled flag without adding the CNP check-in alert") {
                    // Four Opera calls through OHIP: initial reservation GET, confirmation PUT,
                    // CNP-detection GET, and payment-details GET. No alert PUT follows.
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a pay-now reservation stores its generated deposit folios before OHIP confirms it") {
                val arrival = LocalDate.now().plusDays(14)
                val departure = arrival.plusDays(2)
                val paymentType = "VA"
                val booking =
                    Booking(
                        hotels =
                            listOf(
                                Hotels.HEAPTI.copy(
                                    availableRates =
                                        listOf(
                                            Rate(
                                                ratePlan = "SEMIFLEX",
                                                roomType = "LOWDBL",
                                                adults = 2,
                                                nightlyRate = 82.50,
                                            ),
                                        ),
                                ),
                            ),
                        arrival = arrival,
                        departure = departure,
                        rooms =
                            listOf(
                                BookingRoom(
                                    reservationId = "6004104",
                                    roomType = "LOWDBL",
                                    adults = 2,
                                    status = ReservationStatus.RESERVED,
                                    guestProfile =
                                        GuestProfile(
                                            profileId = "PROF-6414",
                                            firstName = "Dana",
                                            lastName = "Whitfield",
                                            email = "dana.whitfield.hre@test.com",
                                            phone = "+447700900014",
                                            addressLine = "14 High Street",
                                            city = "London",
                                            postcode = "SW1A 1AA",
                                        ),
                                ),
                            ),
                    )
                val room = booking.room

                installFor(booking)

                val result =
                    hotelReservationApi.confirmReservation(
                        request =
                            ConfirmReservationRequest(
                                reservationId = room.reservationId!!,
                                hotelId = booking.hotel.hotelId,
                                paymentOption = ConfirmReservationPaymentOption.PAY_NOW,
                                paymentMethod = "DVA",
                                paymentType = paymentType,
                                paymentCard =
                                    ConfirmReservationPaymentCard(
                                        cardType = paymentType,
                                        token = "hre-confirm-pay-now-card-token",
                                        expirationDate = "2030-12-31",
                                        cardHolderName = "Dana Whitfield",
                                        cardNumberLast4Digits = "1111",
                                    ),
                                paymentId = "hre-confirm-pay-now-${room.reservationId}",
                            ),
                        testId = testId,
                        featureFlagOverrides = confirmPayNowReservationFeatureFlags,
                    )

                result.attachEvidence("Confirm Pay Now Reservation Through HRE")

                expect("returns the non-partial confirmed reservation") {
                    result.response.status.value shouldBe 200
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.reservationStatus shouldBe room.status.name
                    // The reservation carries no stored payment, so the partially paid branch that
                    // would answer without confirming through OHIP is not the one taken here.
                    result.body.isPartialPaid shouldBe false
                    result.body.reservationIdList
                        ?.first()
                        ?.id shouldBe room.reservationId
                    result.body.roomStay?.arrivalDate shouldBe booking.arrival
                    result.body.roomStay?.departureDate shouldBe booking.departure
                    result.body.reservationGuest?.givenName shouldBe room.guestProfile?.firstName
                    result.body.reservationGuest?.surName shouldBe room.guestProfile?.lastName
                }

                expect("reads the reservation, generates its deposit folios, and then confirms through OHIP") {
                    // Twelve Opera calls through OHIP, in the three stages PAY_NOW adds. The
                    // reservation lookup with rate information: reservation GET, amounts GET,
                    // folios GET, CRM profile GET, hotel config GET. Deposit-folio generation:
                    // reservation GET and amounts GET. Legacy confirmation: reservation GET,
                    // reservation PUT, amounts GET, deposit-folio POST, and CNP-detection GET.
                    // Hotel config is uncached only because the environment sets CACHE_TYPE=none.
                    // The basket lookup and the charge persistence that bracket the generation
                    // stage reach basket-service, which is a deployed service rather than a
                    // WireMock, so no scenario-owned request journal can count them.
                    callCount(Upstream.OPERA) shouldBe 12
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a pay-now reservation whose basket carries no payment option is confirmed without generating deposit folios") {
                val arrival = LocalDate.now().plusDays(14)
                val departure = arrival.plusDays(2)
                val paymentType = "VA"
                val rate =
                    Rate(
                        ratePlan = "SEMIFLEX",
                        roomType = "LOWDBL",
                        adults = 2,
                        nightlyRate = 82.50,
                    )
                val booking =
                    Booking(
                        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
                        arrival = arrival,
                        departure = departure,
                        rooms =
                            listOf(
                                BookingRoom(
                                    reservationId = "6004105",
                                    roomType = rate.roomType,
                                    adults = rate.adults,
                                    status = ReservationStatus.RESERVED,
                                    // A deposit plus an outstanding balance is what sends the service
                                    // down the partially paid branch, where the basket decides.
                                    amountAlreadyPaid = 50.0,
                                    guestProfile =
                                        GuestProfile(
                                            profileId = "PROF-6415",
                                            firstName = "Ira",
                                            lastName = "Lowell",
                                            email = "ira.lowell.hre@test.com",
                                            phone = "+447700900015",
                                            addressLine = "15 High Street",
                                            city = "London",
                                            postcode = "SW1A 1AA",
                                        ),
                                ),
                            ),
                    )
                val room = booking.room

                installFor(booking)

                val reservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                reservation.attachEvidence("Create Booking Whose Basket Has No Payment Option")

                expect("the booking is held and owns a basket to confirm against") {
                    reservation.response.status.value shouldBe 201
                    reservation.body.basketReference.shouldNotBeBlank()
                }

                val basket =
                    basketApi.getBasket(
                        basketReference = reservation.body.basketReference,
                        testId = testId,
                    )

                basket.attachEvidence("Read The Basket Backing The Confirmation")

                expect("the basket carries a booking reference and no payment option yet") {
                    basket.response.status.value shouldBe 200
                    // Two different identifiers: the create response returns the basket id, while the
                    // booking reference below is what other services hold as the external reference.
                    basket.body.bookingReference.shouldNotBeBlank()
                    basket.body.paymentOption shouldBe null
                }

                // Exceptional temporal override: basket-service generates the booking reference, so it
                // exists only after the create above and no Booking fact can carry it beforehand. Opera
                // holds it with the per-room suffix the service strips before the basket lookup, which
                // is what makes this install prove the normalization rather than assume it.
                installStub(
                    getReservations(
                        booking.copy(bookingReference = "${basket.body.bookingReference}-1"),
                        listOf(room),
                    ),
                )

                val result =
                    hotelReservationApi.confirmReservation(
                        request =
                            ConfirmReservationRequest(
                                reservationId = room.reservationId!!,
                                hotelId = booking.hotel.hotelId,
                                paymentOption = ConfirmReservationPaymentOption.PAY_NOW,
                                paymentMethod = "DVA",
                                paymentType = paymentType,
                                paymentCard =
                                    ConfirmReservationPaymentCard(
                                        cardType = paymentType,
                                        token = "hre-confirm-basket-found-card-token",
                                        expirationDate = "2030-12-31",
                                        cardHolderName = "Ira Lowell",
                                        cardNumberLast4Digits = "1111",
                                    ),
                                paymentId = "hre-confirm-basket-found-${room.reservationId}",
                            ),
                        testId = testId,
                        featureFlagOverrides = confirmPayNowReservationFeatureFlags,
                    )

                result.attachEvidence("Confirm Pay Now Reservation With Basket Found Through HRE")

                expect("returns the confirmed reservation rather than a partial-payment answer") {
                    result.response.status.value shouldBe 200
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.reservationStatus shouldBe room.status.name
                    // The basket was found and is partially paid, so only its missing payment option
                    // keeps this off the early partialPaid answer.
                    result.body.isPartialPaid shouldBe false
                    result.body.reservationIdList
                        ?.first()
                        ?.id shouldBe room.reservationId
                    result.body.roomStay?.arrivalDate shouldBe booking.arrival
                    result.body.roomStay?.departureDate shouldBe booking.departure
                }

                expect("resolves the basket and confirms through OHIP without generating deposit folios") {
                    // Thirteen Opera calls through OHIP: three for the setup create, then ten for the
                    // confirmation. The reservation lookup with rate information: reservation GET,
                    // amounts GET, folios GET, CRM profile GET, hotel config GET. Legacy confirmation:
                    // reservation GET, reservation PUT, amounts GET, deposit-folio POST, CNP-detection
                    // GET. The deposit-folio generation stage is absent, and its two calls are what
                    // separate this from the basket-absent scenario's twelve: the basket resolved, so
                    // the service does not generate or store folios before confirming.
                    callCount(Upstream.OPERA) shouldBe 13
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera reservation lookup failure is returned through OHIP") {
                val hotelId = Hotels.HEAPTI.hotelId
                val reservationId = "invalid-hre-reservation-id"

                // A downstream failure cannot coexist as a normal Booking-driven default.
                installStub(getReservationBadRequest(hotelId = hotelId, reservationId = reservationId))

                val result =
                    hotelReservationApi.confirmReservation(
                        request =
                            ConfirmReservationRequest(
                                reservationId = reservationId,
                                hotelId = hotelId,
                                paymentOption = ConfirmReservationPaymentOption.PAY_ON_ARRIVAL,
                            ),
                        testId = testId,
                        featureFlagOverrides = confirmReservationFeatureFlags,
                    )

                result.attachEvidence("Confirm Reservation Opera Lookup Error Through HRE")

                expect("returns the mapped downstream error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 960
                    result.errorBody?.debugMessage shouldBe
                        "Error while trying to get reservations by ids for hotelId=$hotelId and $reservationId ids"
                    result.errorBody?.globalErrTextTemplate shouldBe "internal.server.exception"
                }

                expect("stops after the failed Opera reservation lookup") {
                    // One Opera call through OHIP: the initial reservation GET returns the error.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera confirmation failure is returned through OHIP") {
                val arrival = LocalDate.now().plusDays(14)
                val departure = arrival.plusDays(2)
                val paymentType = "VA"
                val booking =
                    Booking(
                        hotels =
                            listOf(
                                Hotels.HEAPTI.copy(
                                    availableRates =
                                        listOf(
                                            Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2),
                                        ),
                                ),
                            ),
                        arrival = arrival,
                        departure = departure,
                        rooms =
                            listOf(
                                BookingRoom(
                                    reservationId = "6004106",
                                    roomType = "LOWDBL",
                                    adults = 2,
                                    status = ReservationStatus.RESERVED,
                                    guestProfile =
                                        GuestProfile(
                                            profileId = "PROF-6416",
                                            firstName = "Farah",
                                            lastName = "Osei",
                                            email = "farah.osei.hre@test.com",
                                            phone = "+447700900016",
                                            addressLine = "16 High Street",
                                            city = "London",
                                            postcode = "SW1A 1AA",
                                        ),
                                ),
                            ),
                    )
                val room = booking.room

                installFor(booking)

                // A downstream failure cannot coexist as a normal Booking-driven default. Installed
                // after the generic default so it overrides the reservation's normal PUT mapping.
                installStub(putReservationBadRequest(hotelId = booking.hotel.hotelId, reservationId = room.reservationId!!))

                val result =
                    hotelReservationApi.confirmReservation(
                        request =
                            ConfirmReservationRequest(
                                reservationId = room.reservationId,
                                hotelId = booking.hotel.hotelId,
                                paymentOption = ConfirmReservationPaymentOption.PAY_ON_ARRIVAL,
                                paymentType = paymentType,
                                paymentCard =
                                    ConfirmReservationPaymentCard(
                                        cardType = paymentType,
                                        token = "hre-confirm-put-failure-card-token",
                                        expirationDate = "2030-12-31",
                                        cardHolderName = "Farah Osei",
                                        cardNumberLast4Digits = "1111",
                                    ),
                            ),
                        testId = testId,
                        featureFlagOverrides = confirmReservationFeatureFlags,
                    )

                result.attachEvidence("Confirm Reservation Opera Confirmation Error Through HRE")

                expect("returns the mapped downstream error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 971
                    result.errorBody?.debugMessage shouldBe
                        "Error while trying to change reservation for hotelId=${booking.hotel.hotelId} " +
                        "and reservationId=${room.reservationId}. Max retries exhausted."
                    result.errorBody?.globalErrTextTemplate shouldBe "internal.server.exception"
                }

                expect("stops after the failed Opera confirmation") {
                    // Five Opera calls through OHIP: the initial reservation GET succeeds, then the
                    // confirmation PUT is attempted four times (the original request plus OHIP's
                    // three retries) before it reports the exhausted-retries error.
                    callCount(Upstream.OPERA) shouldBe 5
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

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
        bookingFlowId = "hre-confirm-reservation",
    )
}
