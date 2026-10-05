package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.clients.ohip.model.ConfirmReservationPaymentCard
import uk.co.whitbread.integrationtests.clients.ohip.model.ConfirmReservationPaymentOption
import uk.co.whitbread.integrationtests.clients.ohip.model.ConfirmReservationRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.model.RoutingInstruction
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

private val arrival: LocalDate = LocalDate.now().plusDays(14)
private val departure: LocalDate = arrival.plusDays(2)
private val confirmReservationFeatureFlags: Map<FeatureFlag, Boolean> =
    mapOf(
        OhipFeatureFlag.DEPOSIT_FOLIO_POST_AFTER_DISABLE_ON_HOLD to false,
        OhipFeatureFlag.SET_CNP_BOOKING_ALERTS to false,
        OhipFeatureFlag.SET_DEFAULT_PAYMENT_METHOD_DS to false,
    )
private val confirmReservationWithDepositPolicyPollingFeatureFlags: Map<FeatureFlag, Boolean> =
    mapOf(
        OhipFeatureFlag.DEPOSIT_FOLIO_POST_AFTER_DISABLE_ON_HOLD to true,
        OhipFeatureFlag.SET_CNP_BOOKING_ALERTS to false,
        OhipFeatureFlag.SET_DEFAULT_PAYMENT_METHOD_DS to false,
    )
private val confirmReservationWithCnpAlertsFeatureFlags: Map<FeatureFlag, Boolean> =
    mapOf(
        OhipFeatureFlag.DEPOSIT_FOLIO_POST_AFTER_DISABLE_ON_HOLD to false,
        OhipFeatureFlag.SET_CNP_BOOKING_ALERTS to true,
        OhipFeatureFlag.SET_DEFAULT_PAYMENT_METHOD_DS to false,
    )

class ConfirmReservationSpec :
    JourneySpec(
        "OHIP adapter reservations can be confirmed",
        {
            val ohipApi = OhipApi()

            scenario("a pay-on-arrival request returns the confirmed reservation details") {
                val cardToken = "confirm-reservation-card-token"
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
                                    reservationId = "6004001",
                                    roomType = "LOWDBL",
                                    adults = 2,
                                    status = ReservationStatus.RESERVED,
                                    guestProfile =
                                        GuestProfile(
                                            profileId = "PROF-6401",
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
                val room = booking.room

                installFor(booking)

                val result =
                    ohipApi.confirmReservation(
                        request =
                            ConfirmReservationRequest(
                                reservationId = room.reservationId!!,
                                hotelId = booking.hotel.hotelId,
                                paymentOption = ConfirmReservationPaymentOption.PAY_ON_ARRIVAL,
                                paymentType = paymentType,
                                paymentCard =
                                    ConfirmReservationPaymentCard(
                                        cardType = paymentType,
                                        token = cardToken,
                                        expirationDate = "2030-12-31",
                                        cardHolderName = "Amelia Wright",
                                        cardNumberLast4Digits = "1111",
                                    ),
                            ),
                        testId = testId,
                        featureFlagOverrides = confirmReservationFeatureFlags,
                    )

                result.attachEvidence("Confirm Pay On Arrival Reservation")

                expect("returns the confirmed reservation from Opera") {
                    result.response.status.value shouldBe 200
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.reservationStatus shouldBe room.status.name
                    result.body.reservationIdList
                        ?.first()
                        ?.id shouldBe room.reservationId
                    result.body.roomStay?.arrivalDate shouldBe booking.arrival
                    result.body.roomStay?.departureDate shouldBe booking.departure
                    result.body.reservationGuest?.givenName shouldBe room.guestProfile?.firstName
                    result.body.reservationGuest?.surName shouldBe room.guestProfile?.lastName
                }

                expect("updates and reloads only the Opera reservation") {
                    // Three Opera calls: initial reservation GET, confirmation PUT, and the
                    // post-confirmation GET used to detect CNP routing.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a fully outstanding reservation can be confirmed with immediate payment") {
                val paymentMethod = "DVA"
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
                                    reservationId = "6004002",
                                    roomType = "LOWDBL",
                                    adults = 2,
                                    status = ReservationStatus.RESERVED,
                                ),
                            ),
                    )
                val room = booking.room

                installFor(booking)

                val result =
                    ohipApi.confirmReservation(
                        request =
                            ConfirmReservationRequest(
                                reservationId = room.reservationId!!,
                                hotelId = booking.hotel.hotelId,
                                paymentOption = ConfirmReservationPaymentOption.PAY_NOW,
                                paymentMethod = paymentMethod,
                                paymentType = "VA",
                                paymentCard =
                                    ConfirmReservationPaymentCard(
                                        cardType = "VA",
                                        token = "confirm-pay-now-card-token",
                                        expirationDate = "2030-12-31",
                                        cardHolderName = "Taylor Morgan",
                                        cardNumberLast4Digits = "1111",
                                    ),
                                paymentId = "confirm-pay-now-${room.reservationId}",
                            ),
                        testId = testId,
                        featureFlagOverrides = confirmReservationFeatureFlags,
                    )

                result.attachEvidence("Confirm Pay Now Reservation")

                expect("returns the confirmed reservation after posting its deposit") {
                    result.response.status.value shouldBe 200
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.reservationStatus shouldBe room.status.name
                    result.body.reservationIdList
                        ?.first()
                        ?.id shouldBe room.reservationId
                    result.body.roomStay?.arrivalDate shouldBe booking.arrival
                    result.body.roomStay?.departureDate shouldBe booking.departure
                }

                expect("uses the legacy deposit-posting sequence only") {
                    // Five Opera calls: initial reservation GET, legacy reservation reread,
                    // summary amounts GET, deposit-folio POST, and the final CNP-check GET.
                    callCount(Upstream.OPERA) shouldBe 5
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 3
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a CNP reservation receives a check-in alert when confirmation alerts are enabled") {
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
                                    reservationId = "6004004",
                                    roomType = "LOWDBL",
                                    adults = 2,
                                    status = ReservationStatus.RESERVED,
                                    guestProfile =
                                        GuestProfile(
                                            profileId = "PROF-6404",
                                            firstName = "Morgan",
                                            lastName = "Reed",
                                            email = "morgan.reed@test.com",
                                            phone = "+447700900004",
                                            addressLine = "4 High Street",
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
                    ohipApi.confirmReservation(
                        request =
                            ConfirmReservationRequest(
                                reservationId = room.reservationId!!,
                                hotelId = booking.hotel.hotelId,
                                paymentOption = ConfirmReservationPaymentOption.PAY_ON_ARRIVAL,
                                paymentType = paymentType,
                                paymentCard =
                                    ConfirmReservationPaymentCard(
                                        cardType = paymentType,
                                        token = "confirm-cnp-card-token",
                                        expirationDate = "2030-12-31",
                                        cardHolderName = "Morgan Reed",
                                        cardNumberLast4Digits = "1111",
                                    ),
                            ),
                        testId = testId,
                        featureFlagOverrides = confirmReservationWithCnpAlertsFeatureFlags,
                    )

                result.attachEvidence("Confirm CNP Reservation With Check-in Alert")

                expect("returns the confirmed CNP reservation") {
                    result.response.status.value shouldBe 200
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.reservationStatus shouldBe room.status.name
                    result.body.reservationIdList
                        ?.first()
                        ?.id shouldBe room.reservationId
                    result.body.roomStay?.arrivalDate shouldBe booking.arrival
                    result.body.roomStay?.departureDate shouldBe booking.departure
                }

                expect("detects CNP routing and adds the enabled check-in alert") {
                    // Five Opera calls: initial reservation GET, confirmation PUT, CNP-detection
                    // GET, payment-details GET, and the feature-gated CNP alert PUT.
                    callCount(Upstream.OPERA) shouldBe 5
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 3
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 2
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a CNP reservation does not receive a check-in alert when confirmation alerts are disabled") {
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
                                    reservationId = "6004005",
                                    roomType = "LOWDBL",
                                    adults = 2,
                                    status = ReservationStatus.RESERVED,
                                    guestProfile =
                                        GuestProfile(
                                            profileId = "PROF-6405",
                                            firstName = "Casey",
                                            lastName = "Ellis",
                                            email = "casey.ellis@test.com",
                                            phone = "+447700900005",
                                            addressLine = "5 High Street",
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
                    ohipApi.confirmReservation(
                        request =
                            ConfirmReservationRequest(
                                reservationId = room.reservationId!!,
                                hotelId = booking.hotel.hotelId,
                                paymentOption = ConfirmReservationPaymentOption.PAY_ON_ARRIVAL,
                                paymentType = paymentType,
                                paymentCard =
                                    ConfirmReservationPaymentCard(
                                        cardType = paymentType,
                                        token = "confirm-cnp-alerts-disabled-card-token",
                                        expirationDate = "2030-12-31",
                                        cardHolderName = "Casey Ellis",
                                        cardNumberLast4Digits = "1111",
                                    ),
                            ),
                        testId = testId,
                        featureFlagOverrides = confirmReservationFeatureFlags,
                    )

                result.attachEvidence("Confirm CNP Reservation Without Check-in Alert")

                expect("returns the confirmed CNP reservation") {
                    result.response.status.value shouldBe 200
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.reservationStatus shouldBe room.status.name
                    result.body.reservationIdList
                        ?.first()
                        ?.id shouldBe room.reservationId
                    result.body.roomStay?.arrivalDate shouldBe booking.arrival
                    result.body.roomStay?.departureDate shouldBe booking.departure
                }

                expect("detects CNP routing without adding the disabled check-in alert") {
                    // Four Opera calls: initial reservation GET, confirmation PUT, CNP-detection
                    // GET, and payment-details GET. The disabled CNP alert adds no second PUT.
                    callCount(Upstream.OPERA) shouldBe 4
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 3
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("immediate payment waits for Opera's updated deposit policy before posting the deposit") {
                val paymentMethod = "DVA"
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
                                    reservationId = "6004003",
                                    roomType = "LOWDBL",
                                    adults = 2,
                                    status = ReservationStatus.RESERVED,
                                    depositPolicyCode = "DEP",
                                    depositPolicyCodeAfterUpdate = "ADV",
                                ),
                            ),
                    )
                val room = booking.room

                installFor(booking)

                val result =
                    ohipApi.confirmReservation(
                        request =
                            ConfirmReservationRequest(
                                reservationId = room.reservationId!!,
                                hotelId = booking.hotel.hotelId,
                                paymentOption = ConfirmReservationPaymentOption.PAY_NOW,
                                paymentMethod = paymentMethod,
                                paymentType = "VA",
                                paymentCard =
                                    ConfirmReservationPaymentCard(
                                        cardType = "VA",
                                        token = "confirm-pay-now-polling-card-token",
                                        expirationDate = "2030-12-31",
                                        cardHolderName = "Jordan Riley",
                                        cardNumberLast4Digits = "1111",
                                    ),
                                paymentId = "confirm-pay-now-polling-${room.reservationId}",
                            ),
                        testId = testId,
                        featureFlagOverrides = confirmReservationWithDepositPolicyPollingFeatureFlags,
                    )

                result.attachEvidence("Confirm Pay Now Reservation After Deposit Policy Update")

                expect("returns the confirmed reservation after Opera exposes the updated policy") {
                    result.response.status.value shouldBe 200
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.reservationStatus shouldBe room.status.name
                    result.body.reservationIdList
                        ?.first()
                        ?.id shouldBe room.reservationId
                    result.body.roomStay?.arrivalDate shouldBe booking.arrival
                    result.body.roomStay?.departureDate shouldBe booking.departure
                }

                expect("changes the reservation and observes the policy update before posting the deposit") {
                    // Six Opera calls: initial reservation GET, summary amounts GET, confirmation
                    // PUT, one successful policy-poll GET, deposit-folio POST, and final CNP-check GET.
                    callCount(Upstream.OPERA) shouldBe 6
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 3
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a partially paid reservation is confirmed without posting another immediate payment") {
                val paymentMethod = "DVA"
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
                                    reservationId = "6004006",
                                    roomType = "LOWDBL",
                                    adults = 2,
                                    status = ReservationStatus.RESERVED,
                                    depositPolicyCode = "DEP",
                                    depositPolicyCodeAfterUpdate = "ADV",
                                    amountAlreadyPaid = 50.0,
                                ),
                            ),
                    )
                val room = booking.room

                installFor(booking)

                val result =
                    ohipApi.confirmReservation(
                        request =
                            ConfirmReservationRequest(
                                reservationId = room.reservationId!!,
                                hotelId = booking.hotel.hotelId,
                                paymentOption = ConfirmReservationPaymentOption.PAY_NOW,
                                paymentMethod = paymentMethod,
                                paymentType = "VA",
                                paymentCard =
                                    ConfirmReservationPaymentCard(
                                        cardType = "VA",
                                        token = "confirm-part-paid-card-token",
                                        expirationDate = "2030-12-31",
                                        cardHolderName = "Alex Parker",
                                        cardNumberLast4Digits = "1111",
                                    ),
                                paymentId = "confirm-part-paid-${room.reservationId}",
                            ),
                        testId = testId,
                        featureFlagOverrides = confirmReservationWithDepositPolicyPollingFeatureFlags,
                    )

                result.attachEvidence("Confirm Part-paid Reservation")

                expect("returns the confirmed reservation without taking another payment") {
                    result.response.status.value shouldBe 200
                    result.body.hotelId shouldBe booking.hotel.hotelId
                    result.body.reservationStatus shouldBe room.status.name
                    result.body.reservationIdList
                        ?.first()
                        ?.id shouldBe room.reservationId
                    result.body.roomStay?.arrivalDate shouldBe booking.arrival
                    result.body.roomStay?.departureDate shouldBe booking.departure
                }

                expect("skips the update, policy poll, and deposit posting sequence") {
                    // Three Opera calls: initial reservation GET, summary amounts GET, and final
                    // CNP-check GET. The existing payment prevents PUT, polling, and deposit POST.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera reservation lookup error is returned as an adapter error") {
                val hotelId = Hotels.HEAPTI.hotelId
                val reservationId = "invalid-reservation-id"

                // A downstream failure is exceptional behavior, not a normal Booking world state.
                installStub(getReservationBadRequest(hotelId = hotelId, reservationId = reservationId))

                val result =
                    ohipApi.confirmReservation(
                        request =
                            ConfirmReservationRequest(
                                reservationId = reservationId,
                                hotelId = hotelId,
                                paymentOption = ConfirmReservationPaymentOption.PAY_ON_ARRIVAL,
                            ),
                        testId = testId,
                        featureFlagOverrides = confirmReservationFeatureFlags,
                    )

                result.attachEvidence("Confirm Reservation Opera Lookup Error")

                expect("returns the mapped Opera lookup error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 960
                    result.errorBody?.debugMessage shouldBe
                        "Error while trying to get reservations by ids for hotelId=$hotelId and $reservationId ids"
                    result.errorBody?.globalErrTextTemplate shouldBe "internal.server.exception"
                }

                expect("stops after the failed Opera reservation lookup") {
                    // One Opera call: the initial reservation GET, which returns the error.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )
