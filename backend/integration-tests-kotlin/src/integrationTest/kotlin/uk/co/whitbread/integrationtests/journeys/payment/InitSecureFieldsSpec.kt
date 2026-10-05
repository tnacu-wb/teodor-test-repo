package uk.co.whitbread.integrationtests.journeys.payment

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotBeBlank
import uk.co.whitbread.integrationtests.clients.hotelreservation.HotelReservationApi
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationBookingChannel
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRoomRateRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRoomRequest
import uk.co.whitbread.integrationtests.clients.payment.PaymentOrchestrationApi
import uk.co.whitbread.integrationtests.clients.payment.model.SecureFieldsInitRequest
import uk.co.whitbread.integrationtests.stubs.datatrans.DATATRANS_SECURE_FIELDS_INIT_STUB_ID
import uk.co.whitbread.integrationtests.stubs.datatrans.custom.secureFieldsInitFailure
import uk.co.whitbread.integrationtests.stubs.datatrans.secureFieldsInit
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.HotelReservationFeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.CardPayment
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate
import java.util.UUID

/**
 * Flags pinned on every setup create so the exercised path never depends on a deployed default:
 * DS payment method off (Opera create stubs match CA), city tax off (no content-entity fixtures
 * in these scenarios), occupancy supplement off (plain multi-room path).
 */
private val createReservationFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.SET_DEFAULT_PAYMENT_METHOD_DS to false,
        HotelReservationFeatureFlag.CITY_TAX_UK to false,
        HotelReservationFeatureFlag.APPLY_OCCUPANCY_SUPPLEMENT to false,
    )

private val arrival: LocalDate = LocalDate.now().plusDays(21)
private val departure: LocalDate = arrival.plusDays(2)
private const val RETURN_URL = "https://www.premierinn.com/payments/3ds-return"

/**
 * Locale and caller identity the orchestrator forwards to payment-methods-entity-service. That
 * service decides whether new-card payment is offered at all, so a session only opens when these
 * resolve to a CARD/NEW_CARD method.
 */
private const val COUNTRY = "gb"
private const val LANGUAGE = "en"
private const val USER_TYPE = "LEISURE"
private const val CLIENT_CHANNEL = "PI"

/**
 * Opening a web card payment session for a held booking.
 *
 * The scenario spans two deployed services and a Temporal workflow: it books a room through the
 * reservation entity service, then asks the payment orchestrator to open a Secure Fields session
 * for that basket. The gateway mapping matches on the converted minor-unit amount, so a session is
 * only returned when the stay total survives the reservation lookup and the major-to-minor
 * conversion intact.
 */
class InitSecureFieldsSpec :
    JourneySpec(
        "web card payment session initialization",
        {
            val hotelReservationApi = HotelReservationApi()
            val paymentApi = PaymentOrchestrationApi()

            scenario("POST /api/payments/secure-fields opens a session for a one-room booking") {
                val booking = paidBooking()

                installFor(booking)

                val reservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                reservation.attachEvidence("Create Booking To Pay For")

                expect("the booking is held and has a basket to pay for") {
                    reservation.response.status.value shouldBe 201
                    reservation.body.basketReference.shouldNotBeBlank()
                }

                val payment =
                    paymentApi.initSecureFields(
                        request =
                            SecureFieldsInitRequest(
                                basketId = reservation.body.basketReference,
                                returnUrl = RETURN_URL,
                                country = COUNTRY,
                                language = LANGUAGE,
                                userType = USER_TYPE,
                                clientChannel = CLIENT_CHANNEL,
                            ),
                        testId = testId,
                    )

                payment.attachEvidence("Initialize Secure Fields Session")

                expect("returns the gateway transaction the browser mounts card fields with") {
                    payment.response.status.value shouldBe 201
                    payment.body.transactionId shouldBe booking.cardPayment?.transactionId
                }
            }

            scenario("a booking priced in JPY initializes payment without multiplying the stay total by 100") {
                val booking =
                    paidBooking(
                        currency = "JPY",
                        nightlyRate = 9000.0,
                        reservationId = "6005002",
                        transactionId = "240412093042123457",
                    )

                // The strict Datatrans mapping accepts 18000 for this two-night stay. A request
                // multiplied by 100 does not match the mapping and cannot return the transaction.
                installFor(booking)

                val reservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                reservation.attachEvidence("Create JPY Booking To Pay For")

                expect("the JPY booking is held and has a basket to pay for") {
                    reservation.response.status.value shouldBe 201
                    reservation.body.basketReference.shouldNotBeBlank()
                }

                val payment =
                    paymentApi.initSecureFields(
                        request =
                            SecureFieldsInitRequest(
                                basketId = reservation.body.basketReference,
                                returnUrl = RETURN_URL,
                                country = COUNTRY,
                                language = LANGUAGE,
                                userType = USER_TYPE,
                                clientChannel = CLIENT_CHANNEL,
                            ),
                        testId = testId,
                    )

                payment.attachEvidence("Initialize Secure Fields For JPY Booking")

                expect("returns the transaction created from the whole-unit JPY amount") {
                    payment.response.status.value shouldBe 201
                    payment.body.transactionId shouldBe booking.cardPayment?.transactionId
                }
            }

            scenario("reinitializing payment for the same basket returns the new Datatrans transaction") {
                val firstTransactionId = "240412093042123461"
                val secondTransactionId = "240412093042123462"
                val booking =
                    paidBooking(
                        reservationId = "6005004",
                        transactionId = firstTransactionId,
                    )

                installFor(booking)

                val reservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                reservation.attachEvidence("Create Booking For Payment Reinitialization")

                expect("the booking is held and has a basket to pay for") {
                    reservation.response.status.value shouldBe 201
                    reservation.body.basketReference.shouldNotBeBlank()
                }

                val firstPayment =
                    paymentApi.initSecureFields(
                        request =
                            SecureFieldsInitRequest(
                                basketId = reservation.body.basketReference,
                                returnUrl = RETURN_URL,
                                country = COUNTRY,
                                language = LANGUAGE,
                                userType = USER_TYPE,
                                clientChannel = CLIENT_CHANNEL,
                            ),
                        testId = testId,
                    )

                firstPayment.attachEvidence("First Secure Fields Initialization")

                expect("the first initialization returns the first transaction") {
                    firstPayment.response.status.value shouldBe 201
                    firstPayment.body.transactionId shouldBe firstTransactionId
                }

                // Datatrans allocates a new transaction for the next initialization of the same
                // basket. Both requests are identical, so this must be installed here rather than
                // up front: it matches what the default does and wins by being the newer mapping.
                installStub(secureFieldsInit(booking.copy(cardPayment = CardPayment(secondTransactionId))))

                val secondPayment =
                    paymentApi.initSecureFields(
                        request =
                            SecureFieldsInitRequest(
                                basketId = reservation.body.basketReference,
                                returnUrl = RETURN_URL,
                                country = COUNTRY,
                                language = LANGUAGE,
                                userType = USER_TYPE,
                                clientChannel = CLIENT_CHANNEL,
                            ),
                        testId = testId,
                    )

                secondPayment.attachEvidence("Second Secure Fields Initialization")

                expect("Datatrans initializes two payment sessions") {
                    callCount(Upstream.WORLDLINE) shouldBe 2
                }

                expect("the second initialization returns the new transaction") {
                    secondPayment.response.status.value shouldBe 201
                    secondPayment.body.transactionId shouldBe secondTransactionId
                }
            }

            scenario("a Datatrans failure returns a gateway error instead of opening a payment session") {
                val booking =
                    paidBooking(
                        reservationId = "6005003",
                        transactionId = "240412093042123458",
                    )

                installFor(booking, excluded = setOf(DATATRANS_SECURE_FIELDS_INIT_STUB_ID))
                installStub(secureFieldsInitFailure(booking))

                val reservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                reservation.attachEvidence("Create Booking For Rejected Payment")

                expect("the booking is held and has a basket to pay for") {
                    reservation.response.status.value shouldBe 201
                    reservation.body.basketReference.shouldNotBeBlank()
                }

                val payment =
                    paymentApi.initSecureFields(
                        request =
                            SecureFieldsInitRequest(
                                basketId = reservation.body.basketReference,
                                returnUrl = RETURN_URL,
                                country = COUNTRY,
                                language = LANGUAGE,
                                userType = USER_TYPE,
                                clientChannel = CLIENT_CHANNEL,
                            ),
                        testId = testId,
                    )

                payment.attachEvidence("Initialize Secure Fields Rejected By Datatrans")

                expect("retries the rejected initialization") {
                    callCount(Upstream.WORLDLINE) shouldBe 3
                }

                expect("returns the public gateway error") {
                    payment.response.status.value shouldBe 502
                    payment.bodyText shouldContain "\"code\":\"GATEWAY_ERROR\""
                }
            }

            /**
             * Disabled: the workflow folds the missing basket into its blanket gateway-error
             * handler, so this returns `502 GATEWAY_ERROR` instead of the `404 BASKET_NOT_FOUND`
             * asserted below. The assertions stay correct; re-enable when the bug is fixed.
             *
             * Bug: backend/integration-tests-kotlin/bug/init-secure-fields-unknown-basket-gateway-error.md
             */
            scenario("!an unknown basket returns basket not found without initializing payment") {
                val booking = paidBooking()
                val unknownBasketId = "AQN-${UUID.randomUUID()}"

                // Install the working downstream mappings so the request journal can prove that
                // the missing basket stops the flow before reservation enrichment and payment.
                installFor(booking)

                val payment =
                    paymentApi.initSecureFields(
                        request =
                            SecureFieldsInitRequest(
                                basketId = unknownBasketId,
                                returnUrl = RETURN_URL,
                                country = COUNTRY,
                                language = LANGUAGE,
                                userType = USER_TYPE,
                                clientChannel = CLIENT_CHANNEL,
                            ),
                        testId = testId,
                    )

                payment.attachEvidence("Initialize Secure Fields For Unknown Basket")

                expect("does not load Opera reservation details or initialize Datatrans") {
                    callCount(Upstream.OPERA) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }

                expect("returns the public basket-not-found error") {
                    payment.response.status.value shouldBe 404
                    payment.bodyText shouldContain "\"code\":\"BASKET_NOT_FOUND\""
                }
            }
        },
    )

/** A one-room stay, held in Opera, that the guest is about to pay for by card. */
private fun paidBooking(
    currency: String = "GBP",
    nightlyRate: Double = 59.0,
    reservationId: String = "6005001",
    transactionId: String = "240412093042123456",
): Booking {
    val rate =
        Rate(
            ratePlan = "SEMIFLEX",
            roomType = "VPPDBL",
            adults = 1,
            nightlyRate = nightlyRate,
        )

    return Booking(
        hotels =
            listOf(
                Hotels.HEAPTI.copy(
                    currency = currency,
                    availableRates = listOf(rate),
                    // payment-methods-entity-service gates new-card payment on the hotel taking
                    // Datatrans; without this it resolves NEW_CARD to 3CP and the orchestrator
                    // refuses to open a session at all.
                    dataTransEnabled = true,
                ),
            ),
        arrival = arrival,
        departure = departure,
        rooms =
            listOf(
                BookingRoom(
                    reservationId = reservationId,
                    roomType = rate.roomType,
                    adults = rate.adults,
                    children = rate.children,
                    // The payment methods service maps the reservation's policy code onto its own
                    // policy set and rejects anything outside D1/OA/D1A/NL/RWC/DAX, so the model
                    // default DEP fails the payment-methods call before Datatrans is reached. D1 is
                    // the pay-now policy these card scenarios are about.
                    depositPolicyCode = "D1",
                    // The reservation lookup resolves the guest profile, so a booking without one
                    // fails on the profile fetch long before it reaches the payment gateway.
                    guestProfile =
                        GuestProfile(
                            profileId = "PROF-$reservationId",
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
        cardPayment = CardPayment(transactionId = transactionId),
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
        bookingFlowId = "secure-fields-payment",
    )
}
