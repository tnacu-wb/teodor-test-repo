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
import uk.co.whitbread.integrationtests.clients.payment.model.MobileSdkInitRequest
import uk.co.whitbread.integrationtests.clients.payment.model.SecureFieldsInitRequest
import uk.co.whitbread.integrationtests.stubs.datatrans.DATATRANS_MOBILE_SDK_INIT_STUB_ID
import uk.co.whitbread.integrationtests.stubs.datatrans.custom.mobileSdkInitFailure
import uk.co.whitbread.integrationtests.stubs.datatrans.mobileSdkInit
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
private const val CLIENT_CHANNEL_WEB = "PI"
private const val CLIENT_CHANNEL_NATIVE = "APPS_IOS"

/**
 * Opening a native app card payment session for a held booking.
 *
 * The scenario spans two deployed services and a Temporal workflow: it books a room through the
 * reservation entity service, then asks the payment orchestrator to open a Mobile SDK session for
 * that basket. Unlike the web channel there is no return URL, because the native SDK carries
 * 3-D Secure itself. The gateway mapping matches on the converted minor-unit amount, so a session
 * is only returned when the stay total survives the reservation lookup and the major-to-minor
 * conversion intact.
 */
class InitMobileSdkSpec :
    JourneySpec(
        "native app card payment session initialization",
        {
            val hotelReservationApi = HotelReservationApi()
            val paymentApi = PaymentOrchestrationApi()

            scenario("a held one-room booking opens a Mobile SDK session for the native app") {
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
                    paymentApi.initMobileSdk(
                        request =
                            MobileSdkInitRequest(
                                basketId = reservation.body.basketReference,
                                country = COUNTRY,
                                language = LANGUAGE,
                                userType = USER_TYPE,
                                clientChannel = CLIENT_CHANNEL_NATIVE,
                            ),
                        testId = testId,
                    )

                payment.attachEvidence("Initialize Mobile SDK Session")

                expect("returns the gateway transaction the native SDK is handed") {
                    payment.response.status.value shouldBe 201
                    payment.body.transactionId shouldBe booking.cardPayment?.transactionId
                }

                expect("initializes the gateway once and leaves the other upstreams alone") {
                    // One POST /v2/transactions from the payment workflow's init activity. The
                    // reconciliation poller only wakes two minutes after init, so it adds nothing
                    // inside a scenario.
                    callCount(Upstream.WORLDLINE) shouldBe 1
                    // Nine of the Opera calls belong to the setup create above - hotel config,
                    // availability, rate and package lookups, profile create, and the reservation
                    // create. The rest come from the payment endpoint's payment-method check,
                    // which reads the basket reservation back through the reservation service.
                    callCount(Upstream.OPERA) shouldBe 14
                    // The same check resolves the hotel's payment configuration through the
                    // content service, which reads it from AEM.
                    callCount(Upstream.AEM) shouldBe 1
                    callCount(Upstream.CDH) shouldBe 0
                }
            }

            scenario("reinitializing a Mobile SDK session for the same basket returns the new transaction") {
                val firstTransactionId = "240412093042123472"
                val secondTransactionId = "240412093042123473"
                val booking =
                    paidBooking(
                        reservationId = "6005102",
                        transactionId = firstTransactionId,
                    )

                installFor(booking)

                val reservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                reservation.attachEvidence("Create Booking For Mobile SDK Reinitialization")

                expect("the booking is held and has a basket to pay for") {
                    reservation.response.status.value shouldBe 201
                    reservation.body.basketReference.shouldNotBeBlank()
                }

                val firstPayment =
                    paymentApi.initMobileSdk(
                        request =
                            MobileSdkInitRequest(
                                basketId = reservation.body.basketReference,
                                country = COUNTRY,
                                language = LANGUAGE,
                                userType = USER_TYPE,
                                clientChannel = CLIENT_CHANNEL_NATIVE,
                            ),
                        testId = testId,
                    )

                firstPayment.attachEvidence("First Mobile SDK Initialization")

                expect("the first initialization returns the first transaction") {
                    firstPayment.response.status.value shouldBe 201
                    firstPayment.body.transactionId shouldBe firstTransactionId
                }

                // Datatrans allocates a new transaction for the next initialization of the same
                // basket. Both requests are identical, so this must be installed here rather than
                // up front: it matches what the default does and wins by being the newer mapping.
                installStub(mobileSdkInit(booking.copy(cardPayment = CardPayment(secondTransactionId))))

                val secondPayment =
                    paymentApi.initMobileSdk(
                        request =
                            MobileSdkInitRequest(
                                basketId = reservation.body.basketReference,
                                country = COUNTRY,
                                language = LANGUAGE,
                                userType = USER_TYPE,
                                clientChannel = CLIENT_CHANNEL_NATIVE,
                            ),
                        testId = testId,
                    )

                secondPayment.attachEvidence("Second Mobile SDK Initialization")

                expect("the second initialization returns the new transaction") {
                    secondPayment.response.status.value shouldBe 201
                    secondPayment.body.transactionId shouldBe secondTransactionId
                }

                expect("Datatrans initializes two payment sessions") {
                    callCount(Upstream.WORLDLINE) shouldBe 2
                }
            }

            scenario("a Datatrans failure returns a service error instead of opening a Mobile SDK session") {
                val booking =
                    paidBooking(
                        reservationId = "6005103",
                        transactionId = "240412093042123474",
                    )

                installFor(booking, excluded = setOf(DATATRANS_MOBILE_SDK_INIT_STUB_ID))
                installStub(mobileSdkInitFailure(booking))

                val reservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                reservation.attachEvidence("Create Booking For Rejected Mobile SDK Payment")

                expect("the booking is held and has a basket to pay for") {
                    reservation.response.status.value shouldBe 201
                    reservation.body.basketReference.shouldNotBeBlank()
                }

                val payment =
                    paymentApi.initMobileSdk(
                        request =
                            MobileSdkInitRequest(
                                basketId = reservation.body.basketReference,
                                country = COUNTRY,
                                language = LANGUAGE,
                                userType = USER_TYPE,
                                clientChannel = CLIENT_CHANNEL_NATIVE,
                            ),
                        testId = testId,
                    )

                payment.attachEvidence("Mobile SDK Initialization Rejected By Datatrans")

                expect("retries the rejected initialization") {
                    callCount(Upstream.WORLDLINE) shouldBe 3
                }

                // The gateway error reaches the caller as SERVICE_UNAVAILABLE rather than the
                // GATEWAY_ERROR the web channel returns: this channel's workflow rethrows the
                // activity failure, whose Temporal failure type is the exception class name, and
                // the service's error-code mapping does not recognise it.
                expect("returns a service-unavailable error") {
                    payment.response.status.value shouldBe 503
                    payment.bodyText shouldContain "\"code\":\"SERVICE_UNAVAILABLE\""
                }
            }

            scenario("a basket that already opened a web payment session cannot open a native one") {
                val booking =
                    paidBooking(
                        reservationId = "6005104",
                        transactionId = "240412093042123475",
                    )

                installFor(booking)

                val reservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                reservation.attachEvidence("Create Booking For Cross-Channel Payment")

                expect("the booking is held and has a basket to pay for") {
                    reservation.response.status.value shouldBe 201
                    reservation.body.basketReference.shouldNotBeBlank()
                }

                val webPayment =
                    paymentApi.initSecureFields(
                        request =
                            SecureFieldsInitRequest(
                                basketId = reservation.body.basketReference,
                                returnUrl = RETURN_URL,
                                country = COUNTRY,
                                language = LANGUAGE,
                                userType = USER_TYPE,
                                clientChannel = CLIENT_CHANNEL_WEB,
                            ),
                        testId = testId,
                    )

                webPayment.attachEvidence("Initialize Secure Fields Session First")

                expect("the web session opens") {
                    webPayment.response.status.value shouldBe 201
                    webPayment.body.transactionId shouldBe booking.cardPayment?.transactionId
                }

                val nativePayment =
                    paymentApi.initMobileSdk(
                        request =
                            MobileSdkInitRequest(
                                basketId = reservation.body.basketReference,
                                country = COUNTRY,
                                language = LANGUAGE,
                                userType = USER_TYPE,
                                clientChannel = CLIENT_CHANNEL_NATIVE,
                            ),
                        testId = testId,
                    )

                nativePayment.attachEvidence("Initialize Mobile SDK Session For The Same Basket")

                // Both channels address the same Temporal workflow ID, payment-{basketId}, and the
                // running Secure Fields workflow defines no initMobileSdk update handler, so the
                // native request cannot be applied to it.
                expect("the native request is rejected without a second gateway call") {
                    nativePayment.response.status.value shouldBe 503
                    callCount(Upstream.WORLDLINE) shouldBe 1
                }
            }

            scenario("an unknown basket is rejected without initializing a payment") {
                val booking = paidBooking(reservationId = "6005105", transactionId = "240412093042123476")
                val unknownBasketId = "AQN-${UUID.randomUUID()}"

                // Install the working downstream mappings so the request journal can prove that
                // the missing basket stops the flow before reservation enrichment and payment.
                installFor(booking)

                val payment =
                    paymentApi.initMobileSdk(
                        request =
                            MobileSdkInitRequest(
                                basketId = unknownBasketId,
                                country = COUNTRY,
                                language = LANGUAGE,
                                userType = USER_TYPE,
                                clientChannel = CLIENT_CHANNEL_NATIVE,
                            ),
                        testId = testId,
                    )

                payment.attachEvidence("Initialize Mobile SDK For Unknown Basket")

                expect("does not load Opera reservation details or initialize Datatrans") {
                    callCount(Upstream.OPERA) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }

                // The basket-not-found detail is lost on this channel: the workflow rethrows the
                // activity failure and its Temporal failure type, the exception class name, is not
                // in the service's error-code mapping, so 404 BASKET_NOT_FOUND never surfaces.
                expect("returns a service-unavailable error") {
                    payment.response.status.value shouldBe 503
                    payment.bodyText shouldContain "\"code\":\"SERVICE_UNAVAILABLE\""
                }
            }

            scenario("a malformed basket identifier is rejected before any dependency is called") {
                val booking = paidBooking(reservationId = "6005106", transactionId = "240412093042123477")

                installFor(booking)

                val payment =
                    paymentApi.initMobileSdk(
                        request =
                            MobileSdkInitRequest(
                                basketId = "not-a-basket-id",
                                country = COUNTRY,
                                language = LANGUAGE,
                                userType = USER_TYPE,
                                clientChannel = CLIENT_CHANNEL_NATIVE,
                            ),
                        testId = testId,
                    )

                payment.attachEvidence("Initialize Mobile SDK With Malformed Basket Id")

                expect("returns the public validation error") {
                    payment.response.status.value shouldBe 400
                    payment.bodyText shouldContain "\"code\":\"INVALID_REQUEST\""
                }

                expect("reaches no dependency at all") {
                    callCount(Upstream.OPERA) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

/** A one-room stay, held in Opera, that the guest is about to pay for by card. */
private fun paidBooking(
    currency: String = "GBP",
    nightlyRate: Double = 59.0,
    reservationId: String = "6005101",
    transactionId: String = "240412093042123471",
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
        bookingFlowId = "mobile-sdk-payment",
    )
}
