package uk.co.whitbread.integrationtests.journeys.hotelreservation

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeBlank
import io.kotest.matchers.string.shouldStartWith
import uk.co.whitbread.integrationtests.clients.hotelreservation.HotelReservationApi
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationBookingChannel
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRoomRateRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRoomRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_HOTEL_CONFIG_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_POLICY_SCHEDULES_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationEmpty
import uk.co.whitbread.integrationtests.stubs.opera.custom.hotelConfigFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.policySchedulesFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.HotelReservationFeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.CancellationPolicyAmountPercent
import uk.co.whitbread.integrationtests.testkit.model.GuestProfile
import uk.co.whitbread.integrationtests.testkit.model.HotelCancellationPolicyRule
import uk.co.whitbread.integrationtests.testkit.model.OperaPaymentCard
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationCancellationPolicy
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

// Setup-only pins, copied from ConfirmReservationSpec / CreateReservationGuestSpec: they hold the
// created basket on the plain pay-on-arrival shape the by-reservation scenarios read from. The
// endpoint itself evaluates no feature flag in either service, so no pins are passed on the call
// under test.
private val createReservationFlagPins: Map<FeatureFlag, Boolean> =
    mapOf(
        OhipFeatureFlag.SET_DEFAULT_PAYMENT_METHOD_DS to false,
        HotelReservationFeatureFlag.CITY_TAX_UK to false,
        HotelReservationFeatureFlag.APPLY_OCCUPANCY_SUPPLEMENT to false,
    )

/**
 * Proves both resolution modes of `GET /v1/reservations/cancellationPolicies`. With an empty
 * `basketReference` hotel-reservation-entity-service skips basket-service entirely and has
 * ohip-adapter-service resolve the deadline from the hotel's Opera policy schedule for the rate
 * plan plus its cancel-policy configuration, applied to `arrivalDate`; a rate plan with no
 * schedule answers 200 with null time and text. With the reference of a real created basket the
 * service instead turns the basket items' `sourceId`s into reservation ids and answers from the
 * Opera reservation's own first cancellation policy, ignoring the rate plan and arrival date it
 * was still sent. A rejected Opera hotel-config or policy-schedules read, and a basket whose
 * reservation Opera holds nothing for, all surface as a 500 carrying the adapter's errCode.
 *
 * Flow: backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/GetCancellationPolicies.md
 */
class GetCancellationPoliciesSpec :
    JourneySpec(
        "Hotel reservation returns a cancellation deadline resolved from a basket or a rate plan",
        {
            val hotelReservationApi = HotelReservationApi()

            scenario("an empty basket reference resolves the deadline from rate plan and arrival date") {
                val rule = semiflexRule()
                val booking = cancellationPoliciesBooking(reservationId = "6206301", rule = rule)
                val arrivalDate = requireNotNull(booking.arrival)

                installFor(booking)

                // The empty `basketReference` leaves the reservation id set null, which Spring's
                // URI builder serializes as a valueless `reservationIds` parameter that
                // ohip-adapter binds to an empty Set — the fragile link that selects the
                // rate-plan branch downstream.
                val result =
                    hotelReservationApi.getCancellationPolicies(
                        hotelId = booking.hotel.hotelId,
                        ratePlanCode = rule.ratePlanCode,
                        arrivalDate = arrivalDate.toString(),
                        testId = testId,
                        basketReference = "",
                    )

                result.attachEvidence("Get Cancellation Policies By Rate Plan")

                expect("returns the deadline computed from arrival date and policy offsets") {
                    result.response.status.value shouldBe 200
                    requireNotNull(result.body.time).shouldStartWith(
                        arrivalDate.minusDays(rule.offsetFromArrivalDays.toLong()).toString(),
                    )
                    result.body.text shouldBe rule.description
                }

                expect("reads hotel config, policy schedules and cancel-policy configs from Opera only") {
                    // Three Opera calls: GET /ent/config/v1/hotels/{hotelId},
                    // GET /rsv/config/v1/hotels/{hotelId}/policyschedules,
                    // GET /rsv/config/v1/cancelpolicies.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 1
                    callCount(OperaEndpoint.GET_POLICY_SCHEDULES) shouldBe 1
                    callCount(OperaEndpoint.GET_CANCEL_POLICIES) shouldBe 1
                    // basket-service is not a WireMock upstream, so the skipped basket hop is
                    // proven against the installed get-reservation default: the by-reservation
                    // branch is the only thing a basket lookup could have produced.
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rate plan without a policy schedule returns 200 with null time and text") {
                val rule = semiflexRule()
                val booking = cancellationPoliciesBooking(reservationId = "6206302", rule = rule)

                // The policy-schedules default installs a catch-all empty-list mapping per hotel,
                // so an unconfigured rate plan is a normal world state, not a custom stub.
                installFor(booking)

                val result =
                    hotelReservationApi.getCancellationPolicies(
                        hotelId = booking.hotel.hotelId,
                        ratePlanCode = "NOPOLICY",
                        arrivalDate = requireNotNull(booking.arrival).toString(),
                        testId = testId,
                        basketReference = "",
                    )

                result.attachEvidence("Get Cancellation Policies No Schedule")

                expect("returns an empty cancellation policy") {
                    result.response.status.value shouldBe 200
                    result.body.time shouldBe null
                    result.body.text shouldBe null
                }

                expect("stops after the empty policy-schedules read") {
                    // Two Opera calls: GET /ent/config/v1/hotels/{hotelId},
                    // GET /rsv/config/v1/hotels/{hotelId}/policyschedules; cancelpolicies skipped.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 1
                    callCount(OperaEndpoint.GET_POLICY_SCHEDULES) shouldBe 1
                    callCount(OperaEndpoint.GET_CANCEL_POLICIES) shouldBe 0
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a real basket reference resolves the deadline from the basket's Opera reservation") {
                val rule = semiflexRule()
                val booking = basketCancellationPoliciesBooking(reservationId = "6206305", rule = rule)
                val arrivalDate = requireNotNull(booking.arrival)
                val policy = booking.room.cancellationPolicies.single()

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Reservation For Cancellation Policies")

                expect("creates the OPEN basket whose item carries the Opera reservation id") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE
                }

                // The rate plan and arrival date below are the same ones the rate-plan branch
                // answers with `rule.description` and `arrival - 2 days`; the basket reference is
                // the only thing that redirects the lookup to the reservation's own policy.
                val result =
                    hotelReservationApi.getCancellationPolicies(
                        hotelId = booking.hotel.hotelId,
                        ratePlanCode = rule.ratePlanCode,
                        arrivalDate = arrivalDate.toString(),
                        testId = testId,
                        basketReference = createdReservation.body.basketReference,
                    )

                result.attachEvidence("Get Cancellation Policies By Reservation")

                expect("returns the reservation's own policy deadline and comment, not the rate plan's") {
                    result.response.status.value shouldBe 200
                    requireNotNull(result.body.time).shouldStartWith(policy.deadline)
                    result.body.text shouldBe policy.comments
                }

                expect("reads the basket's reservation from Opera and never the rate-plan policy configs") {
                    // Two Opera calls beyond the setup create: GET /ent/config/v1/hotels/{hotelId}
                    // and GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE + 2
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe OPERA_HOTEL_CONFIG_AFTER_CREATE + 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe OPERA_GET_RESERVATION_AFTER_CREATE + 1
                    // The hotel still carries its cancellation policy rule, so both rate-plan
                    // mappings really are installed — the branch simply is not taken.
                    callCount(OperaEndpoint.GET_POLICY_SCHEDULES) shouldBe 0
                    callCount(OperaEndpoint.GET_CANCEL_POLICIES) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected Opera policy-schedules read maps to the policy-schedules failure") {
                val rule = semiflexRule()
                val booking = cancellationPoliciesBooking(reservationId = "6206303", rule = rule)

                // A downstream rejection is exceptional behavior, not a Booking world state, so
                // the default is excluded and a custom stub with its own id answers instead.
                installFor(booking, excluded = setOf(OPERA_POLICY_SCHEDULES_STUB_ID))
                installStub(policySchedulesFailure(booking.hotel))

                val result =
                    hotelReservationApi.getCancellationPolicies(
                        hotelId = booking.hotel.hotelId,
                        ratePlanCode = rule.ratePlanCode,
                        arrivalDate = requireNotNull(booking.arrival).toString(),
                        testId = testId,
                        basketReference = "",
                    )

                result.attachEvidence("Get Cancellation Policies Schedules Rejected")

                expect("returns the adapter's policy-schedules failure code") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 943
                }

                expect("stops after the rejected policy-schedules read") {
                    // Two Opera calls: GET /ent/config/v1/hotels/{hotelId},
                    // GET /rsv/config/v1/hotels/{hotelId}/policyschedules.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 1
                    callCount(OperaEndpoint.GET_POLICY_SCHEDULES) shouldBe 1
                    callCount(OperaEndpoint.GET_CANCEL_POLICIES) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a rejected Opera hotel-config read fails the request before any policy read") {
                val rule = semiflexRule()
                val booking = cancellationPoliciesBooking(reservationId = "6206304", rule = rule)

                installFor(booking, excluded = setOf(OPERA_HOTEL_CONFIG_STUB_ID))
                installStub(hotelConfigFailure(booking.hotel))

                val result =
                    hotelReservationApi.getCancellationPolicies(
                        hotelId = booking.hotel.hotelId,
                        ratePlanCode = rule.ratePlanCode,
                        arrivalDate = requireNotNull(booking.arrival).toString(),
                        testId = testId,
                        basketReference = "",
                    )

                result.attachEvidence("Get Cancellation Policies Hotel Config Rejected")

                expect("returns the adapter's hotel-config failure code") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 949
                }

                expect("stops on the first Opera read, common to both branches") {
                    // One Opera call: GET /ent/config/v1/hotels/{hotelId}.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe 1
                    callCount(OperaEndpoint.GET_POLICY_SCHEDULES) shouldBe 0
                    callCount(OperaEndpoint.GET_CANCEL_POLICIES) shouldBe 0
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a basket whose reservation Opera holds nothing for maps to the no-policies failure") {
                val rule = semiflexRule()
                val booking = basketCancellationPoliciesBooking(reservationId = "6206306", rule = rule)
                val reservationId = requireNotNull(booking.room.reservationId)

                // A reservation Opera answers empty for is exceptional behavior, not a Booking
                // world state. The setup create never reads the reservation back, so the override
                // can be installed up front.
                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationEmpty(booking.hotel.hotelId, reservationId))

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Reservation For Empty Cancellation Policies")

                expect("creates the OPEN basket the lookup reads from") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE
                }

                val result =
                    hotelReservationApi.getCancellationPolicies(
                        hotelId = booking.hotel.hotelId,
                        ratePlanCode = rule.ratePlanCode,
                        arrivalDate = requireNotNull(booking.arrival).toString(),
                        testId = testId,
                        basketReference = createdReservation.body.basketReference,
                    )

                result.attachEvidence("Get Cancellation Policies Empty Reservation")

                expect("returns the adapter's no-cancellation-policies failure code") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 39
                }

                expect("stops after the empty reservation read without falling back to the rate plan") {
                    // The same two Opera calls as the happy path; the rate plan and arrival date
                    // are still sent, and are still never used.
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE + 2
                    callCount(OperaEndpoint.GET_HOTEL_CONFIGURATION) shouldBe OPERA_HOTEL_CONFIG_AFTER_CREATE + 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe OPERA_GET_RESERVATION_AFTER_CREATE + 1
                    callCount(OperaEndpoint.GET_POLICY_SCHEDULES) shouldBe 0
                    callCount(OperaEndpoint.GET_CANCEL_POLICIES) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

/**
 * Opera cost of the setup create, measured rather than derived. Itemizing it belongs to
 * CreateReservationSpec; these scenarios only need it fixed so the lookup's cost is the delta.
 */
private const val OPERA_CALLS_AFTER_CREATE = 4

/**
 * How that setup cost splits per Opera endpoint, so each per-endpoint assertion states the
 * lookup's own delta rather than a bare total. The setup create never reads the reservation back.
 */
private const val OPERA_GET_RESERVATION_AFTER_CREATE = 0
private const val OPERA_HOTEL_CONFIG_AFTER_CREATE = 1

private fun semiflexRule(): HotelCancellationPolicyRule =
    HotelCancellationPolicyRule(
        ratePlanCode = "SEMIFLEX",
        policyCode = "DOA",
        offsetFromArrivalDays = 2,
        offsetDropTime = "18:00:00",
        description = "Free cancellation until two days before arrival",
    )

/**
 * The hotel carries the cancellation policy rule that gates the Opera policy-schedules and
 * cancel-policy-configs defaults; the reserved room installs the get-reservation default, so the
 * by-reservation branch this endpoint must not take can be counted at zero.
 */
private fun cancellationPoliciesBooking(
    reservationId: String,
    rule: HotelCancellationPolicyRule,
): Booking {
    val arrival = LocalDate.now().plusDays(14)
    val rate = Rate(ratePlan = rule.ratePlanCode, roomType = "LOWDBL", adults = 2)

    return Booking(
        hotels =
            listOf(
                Hotels.HEAPTI.copy(
                    availableRates = listOf(rate),
                    cancellationPolicyRules = listOf(rule),
                ),
            ),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms =
            listOf(
                BookingRoom(
                    reservationId = reservationId,
                    roomType = rate.roomType,
                    adults = rate.adults,
                    status = ReservationStatus.RESERVED,
                ),
            ),
    )
}

/**
 * The same hotel as the rate-plan Booking above — it still carries the cancellation policy rule,
 * so the policy-schedules and cancel-policy-configs defaults stay installed and their absence can
 * be counted — plus a room whose Opera reservation carries its own cancellation policy, which is
 * what the by-reservation branch answers from.
 *
 * The room is pay-on-arrival: `amountAlreadyPaid` stays at its `0.0` default and the Opera
 * payment card is the create setup's plain shape, keeping the refund/ThreeC leg off the path. The
 * guest profile is required by the create setup, which otherwise mints a TEMP profile id no
 * profile gate installs.
 */
private fun basketCancellationPoliciesBooking(
    reservationId: String,
    rule: HotelCancellationPolicyRule,
): Booking {
    val arrival = LocalDate.now().plusDays(14)
    val rate = Rate(ratePlan = rule.ratePlanCode, roomType = "LOWDBL", adults = 2)

    return Booking(
        hotels =
            listOf(
                Hotels.HEAPTI.copy(
                    availableRates = listOf(rate),
                    cancellationPolicyRules = listOf(rule),
                ),
            ),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms =
            listOf(
                BookingRoom(
                    reservationId = reservationId,
                    roomType = rate.roomType,
                    adults = rate.adults,
                    status = ReservationStatus.RESERVED,
                    sourceCode = "44",
                    guestProfile =
                        GuestProfile(
                            profileId = "8006$reservationId",
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
                    // Five days before arrival, deliberately different from the two days before
                    // arrival the rate-plan branch computes from `semiflexRule()`.
                    cancellationPolicies =
                        listOf(
                            ReservationCancellationPolicy(
                                policyId = "129722",
                                deadline = arrival.minusDays(5).toString(),
                                revenueType = "Rooms",
                                amountPercent =
                                    CancellationPolicyAmountPercent(
                                        basisType = "FlatAmount",
                                        nights = 1,
                                        percent = 0.0,
                                        amount = 50.0,
                                    ),
                                policyCode = rule.policyCode,
                                manual = false,
                                effective = true,
                                percentageDue = 100.0,
                                comments = "Cancel free of charge until five days before arrival",
                            ),
                        ),
                ),
            ),
    )
}

/**
 * The create that mints the OPEN basket the by-reservation branch reads. The channel is `PI`
 * because the deployed rules service answers no channel rule for a `DISTR` creation.
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
        bookingFlowId = "hre-cancellation-policies",
    )
}
