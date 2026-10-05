package uk.co.whitbread.integrationtests.journeys.hotelreservation

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeBlank
import uk.co.whitbread.integrationtests.clients.hotelreservation.HotelReservationApi
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CancelReservationPaymentOption
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CancelReservationRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationBookingChannel
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRoomRateRequest
import uk.co.whitbread.integrationtests.clients.hotelreservation.model.CreateReservationRoomRequest
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_CANCEL_RESERVATION_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.cancelReservationFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.cancelReservationWithoutCancellationId
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
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
// created basket on the plain pay-on-arrival shape whose reservation ids this endpoint rolls back.
// The endpoint under test itself reads no feature flag at all — `rollbackReservation` and the
// out-port's `cancelReservation` evaluate none — so there is no ON/OFF pair to write here.
private val createReservationFlagPins: Map<FeatureFlag, Boolean> =
    mapOf(
        OhipFeatureFlag.SET_DEFAULT_PAYMENT_METHOD_DS to false,
        HotelReservationFeatureFlag.CITY_TAX_UK to false,
        HotelReservationFeatureFlag.APPLY_OCCUPANCY_SUPPLEMENT to false,
    )

/**
 * Proves the reservation-cancellation rollback: `POST /v1/reservations/cancellations/rollback`
 * cancels every reservation id the body names in Opera and echoes the basket reference back
 * without reading or changing any basket, does the reservation, card and folio work only when the
 * body carries a payment option, answers a null reference when Opera reports no cancellation id,
 * and surfaces a rejected downstream call as `500` with the adapter's error code intact.
 *
 * Flow: backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/RollbackReservationCancellations.md
 */
class RollbackReservationCancellationsSpec :
    JourneySpec(
        "created reservations can be rolled back",
        {
            val hotelReservationApi = HotelReservationApi()

            scenario("a rollback without a payment option cancels the reservation in Opera and echoes the basket reference") {
                val booking = rollbackBooking(reservationId = "6003201")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Reservation To Roll Back")

                expect("creates the basket whose reference the rollback echoes") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE
                }

                val result =
                    hotelReservationApi.rollbackReservationCancellations(
                        request =
                            CancelReservationRequest(
                                basketReference = createdReservation.body.basketReference,
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Roll Back Reservation")

                expect("echoes the basket reference the create minted") {
                    result.response.status.value shouldBe 200
                    result.body.basketReference shouldBe createdReservation.body.basketReference
                }

                expect("posts only the Opera cancellation on top of the setup create") {
                    // One Opera call beyond the setup create.
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE + 1
                    // POST /rsv/v1/hotels/{hotelId}/reservations/{reservationId}/cancellations.
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 1
                    // With no payment option the deposit branch is never entered: the folio read,
                    // the Front Desk card lookup and the deposit-folio POST all have default
                    // mappings installed by installFor and none of them is called.
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 0
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 0
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a pay-on-arrival rollback reads the reservation, its card and its folios before cancelling") {
                val booking = rollbackBooking(reservationId = "6003202")
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Reservation To Roll Back Pay On Arrival")

                expect("creates the basket whose reference the rollback echoes") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE
                }

                val result =
                    hotelReservationApi.rollbackReservationCancellations(
                        request =
                            CancelReservationRequest(
                                basketReference = createdReservation.body.basketReference,
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                paymentOption = CancelReservationPaymentOption.PAY_ON_ARRIVAL,
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Roll Back Reservation Pay On Arrival")

                expect("echoes the basket reference the create minted") {
                    result.response.status.value shouldBe 200
                    result.body.basketReference shouldBe createdReservation.body.basketReference
                }

                expect("reads the reservation, the card and the folios before the Opera cancellation") {
                    // The request's payment option is the only difference from the scenario above,
                    // and it costs four Opera calls instead of one: GET reservation, GET
                    // creditCardInfo (the room carries an Opera payment card), GET folios, POST
                    // cancellations. The VAT hop is rules-agent-entity-service, not a WireMock
                    // upstream, so it is invisible here.
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE + 4
                    // GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}; the setup
                    // create never reads the reservation back, so this one is the rollback's own.
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    // GET /fof/config/v1/creditCardInfo.
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 1
                    // GET /csh/v1/hotels/{hotelId}/reservations/{reservationId}/folios.
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 1
                    // POST /rsv/v1/hotels/{hotelId}/reservations/{reservationId}/cancellations.
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 1
                    // Nothing was prepaid, so the computed deposit is not negative and the folio
                    // ACI amount is zero: the deposit-folio POST is never made. The rate-info
                    // reads are the setup create's own; this Booking has no CITYTAX package and no
                    // migration reference, so the cancellation adds none.
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 0
                    callCount(OperaEndpoint.GET_RATE_INFO) shouldBe GET_RATE_INFO_CALLS_AFTER_CREATE
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a cancellation Opera answers without a cancellation id returns no basket reference") {
                val booking = rollbackBooking(reservationId = "6003203")
                val reservationId = requireNotNull(booking.room.reservationId)

                // An Opera cancellation that reports no Cancellation identifier is exceptional
                // behavior, not a normal Booking world state.
                installFor(booking, excluded = setOf(OPERA_CANCEL_RESERVATION_STUB_ID))
                installStub(cancelReservationWithoutCancellationId(booking))

                val createdReservation =
                    hotelReservationApi.createReservation(
                        request = createReservationRequest(booking),
                        testId = testId,
                        featureFlagOverrides = createReservationFlagPins,
                    )

                createdReservation.attachEvidence("Create Reservation Without Cancellation Id")

                expect("creates the basket whose reference the rollback would echo") {
                    createdReservation.response.status.value shouldBe 201
                    createdReservation.body.basketReference.shouldNotBeBlank()
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE
                }

                val result =
                    hotelReservationApi.rollbackReservationCancellations(
                        request =
                            CancelReservationRequest(
                                basketReference = createdReservation.body.basketReference,
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Roll Back Reservation Without Cancellation Id")

                expect("answers success with no basket reference at all") {
                    // The branch that only exists at this layer: an empty cancellation-id list is
                    // mapped to a null basket reference rather than the echo of the scenario above.
                    result.response.status.value shouldBe 200
                    result.body.basketReference.shouldBeNull()
                }

                expect("still posts the Opera cancellation exactly once") {
                    callCount(Upstream.OPERA) shouldBe OPERA_CALLS_AFTER_CREATE + 1
                    // POST /rsv/v1/hotels/{hotelId}/reservations/{reservationId}/cancellations.
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 0
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 0
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera rejection of the cancellation post is returned as internal error 950") {
                val booking = rollbackBooking(reservationId = "6003204")
                val reservationId = requireNotNull(booking.room.reservationId)

                // A downstream failure is exceptional behavior, not a normal Booking world state.
                installFor(booking, excluded = setOf(OPERA_CANCEL_RESERVATION_STUB_ID))
                installStub(cancelReservationFailure(booking))

                val result =
                    hotelReservationApi.rollbackReservationCancellations(
                        request =
                            CancelReservationRequest(
                                // No basket is created: the endpoint never looks one up, so a
                                // literal reference keeps the setup create's Opera traffic out of
                                // the counts below.
                                basketReference = "hre-rollback-cancel-rejected",
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Roll Back Reservation Opera Rejects")

                expect("returns the adapter's cancellation error unchanged") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe OHIP_CANCEL_RESERVATION_ERR_CODE
                }

                expect("attempts the Opera cancellation once and stops") {
                    // One Opera call: the rejected POST cancellations, not retried.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a pay-on-arrival rollback whose reservation lookup Opera rejects cancels nothing") {
                val booking = rollbackBooking(reservationId = "6003205")
                val reservationId = requireNotNull(booking.room.reservationId)

                // A downstream failure is exceptional behavior, not a normal Booking world state.
                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(
                    getReservationBadRequest(
                        hotelId = booking.hotel.hotelId,
                        reservationId = reservationId,
                    ),
                )

                val result =
                    hotelReservationApi.rollbackReservationCancellations(
                        request =
                            CancelReservationRequest(
                                // No basket is created, for the same reason as the scenario above.
                                basketReference = "hre-rollback-lookup-rejected",
                                hotelId = booking.hotel.hotelId,
                                reservationIds = listOf(reservationId),
                                paymentOption = CancelReservationPaymentOption.PAY_ON_ARRIVAL,
                            ),
                        testId = testId,
                    )

                result.attachEvidence("Roll Back Reservation Lookup Rejected")

                expect("returns the adapter's reservation-lookup error unchanged") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe OHIP_GET_RESERVATION_ERR_CODE
                }

                expect("cancels nothing in Opera") {
                    // One Opera call: the rejected reservation GET.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    // The point of the scenario: the cancellation mapping installFor really did
                    // install is never reached, so a failed preamble leaves Opera uncancelled.
                    callCount(OperaEndpoint.CANCEL_RESERVATION) shouldBe 0
                    callCount(OperaEndpoint.GET_FOLIOS) shouldBe 0
                    callCount(OperaEndpoint.GET_CREDIT_CARD_INFO) shouldBe 0
                    callCount(OperaEndpoint.CREATE_DEPOSIT_FOLIO) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

/**
 * Opera cost of the setup create, measured rather than derived. Itemizing it belongs to
 * CreateReservationSpec; these scenarios only need it fixed so the rollback's cost is the delta.
 */
private const val OPERA_CALLS_AFTER_CREATE = 4

/**
 * Rate-info reads the setup create makes. The pay-on-arrival rollback adds none: this Booking has
 * no CITYTAX package and no migration external reference, the two facts that would make the
 * adapter read rate-info before cancelling.
 */
private const val GET_RATE_INFO_CALLS_AFTER_CREATE = 1

/** ohip-adapter's `OHIP_CANCEL_RESERVATION_EXCEPTION`, declared as code 950 in its `ErrorCode`. */
private const val OHIP_CANCEL_RESERVATION_ERR_CODE = 950

/** ohip-adapter's `OHIP_GET_RESERVATION_EXCEPTION`, declared as code 960 in its `ErrorCode`. */
private const val OHIP_GET_RESERVATION_ERR_CODE = 960

/**
 * A single pay-on-arrival room carrying an Opera payment card.
 *
 * `amountAlreadyPaid` stays at its `0.0` default, which keeps the computed deposit non-negative
 * and the deposit-folio POST off every path here; the card is what makes the pay-on-arrival
 * scenario read Front Desk card details.
 */
private fun rollbackBooking(reservationId: String): Booking {
    val arrival = LocalDate.now().plusDays(14)
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
                    sourceCode = "44",
                    // Required by the create setup: without it the Opera reservation stub invents
                    // a TEMP profile id that no profile gate installs.
                    guestProfile =
                        GuestProfile(
                            profileId = "7003$reservationId",
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
 * The create that mints the basket the rollback echoes. The channel is `PI` because the deployed
 * rules service answers no channel rule for a `DISTR` creation.
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
        bookingFlowId = "hre-rollback-cancellations",
    )
}
