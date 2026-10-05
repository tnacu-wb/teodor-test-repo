package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationRoutingEmpty
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationComment
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.model.RoutingFolioInstruction
import uk.co.whitbread.integrationtests.testkit.model.RoutingInstruction
import uk.co.whitbread.integrationtests.testkit.model.SelectedPackage
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

// No feature flag gates this chain: the flow doc lists none, and the Opera token-service flag
// (release_ohip_use_token_service) is evaluated outside the request context, so it is unpinnable.
private val bookingAllowancesFlagPins = mapOf<FeatureFlag, Boolean>()

/**
 * Proves `GET /ohip/v1/reservations/bookingAllowances`: the adapter reads the reservation's
 * routing instructions from Opera once, translates their transaction and billing codes into
 * business allowance names against the rules-agent rule set, carries the instruction credit
 * limit as each allowance budget, and returns the reservation's `BUSINESS NOTES` comment.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/GetBookingAllowances.md
 */
class GetBookingAllowancesSpec :
    JourneySpec(
        "OHIP adapter reads the business allowances routed on a reservation",
        {
            val ohipApi = OhipApi()

            scenario("reservation routing instructions resolve to business allowances with business notes") {
                val booking =
                    bookingAllowancesBooking(
                        reservationId = "6006601",
                        instruction =
                            RoutingFolioInstruction(
                                daily = true,
                                creditLimit = "250.00",
                                transactionCodes = listOf("PARK"),
                                billingCodes = listOf("WIFI"),
                            ),
                        comments =
                            listOf(
                                ReservationComment(
                                    title = BUSINESS_NOTES_TITLE,
                                    text = BUSINESS_NOTES_TEXT,
                                ),
                            ),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.getBookingAllowances(
                        hotelId = booking.hotel.hotelId,
                        reservationId = reservationId,
                        testId = testId,
                        featureFlagOverrides = bookingAllowancesFlagPins,
                    )

                result.attachEvidence("Get Booking Allowances")

                expect("returns both routed allowances with the instruction budget and the business notes") {
                    result.response.status.value shouldBe 200
                    result.body.bookingAllowances.size shouldBe 2
                    result.body.bookingAllowances
                        .map { it.allowance }
                        .toSet() shouldBe setOf("carParking", "ultimateWifi")
                    result.body.bookingAllowances
                        .map { it.budget }
                        .toSet() shouldBe setOf(250.00)
                    result.body.businessNotes shouldBe BUSINESS_NOTES_TEXT
                }

                expect("reads the reservation from Opera exactly once") {
                    // One Opera call: GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a routing code matching a package on the reservation resolves as that package allowance") {
                val booking =
                    bookingAllowancesBooking(
                        reservationId = "6006602",
                        instruction =
                            RoutingFolioInstruction(
                                daily = true,
                                creditLimit = "120.00",
                                transactionCodes = listOf("FOOD"),
                            ),
                        selectedPackages = listOf(SelectedPackage(code = MEAL_DEAL_PACKAGE_CODE)),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.getBookingAllowances(
                        hotelId = booking.hotel.hotelId,
                        reservationId = reservationId,
                        testId = testId,
                        featureFlagOverrides = bookingAllowancesFlagPins,
                    )

                result.attachEvidence("Get Booking Allowances Package Precedence")

                expect("returns the package allowance instead of the plain allowance rule for the code") {
                    result.response.status.value shouldBe 200
                    result.body.bookingAllowances.size shouldBe 1
                    result.body.bookingAllowances
                        .first()
                        .allowance shouldBe MEAL_DEAL_PACKAGE_CODE
                    result.body.bookingAllowances
                        .first()
                        .budget shouldBe 120.00
                }

                expect("reads the reservation from Opera exactly once") {
                    // One Opera call: GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("supplied bookingAllowanceIds return every matching allowance for a code instead of the first") {
                val booking =
                    bookingAllowancesBooking(
                        reservationId = "6006603",
                        instruction =
                            RoutingFolioInstruction(
                                daily = true,
                                creditLimit = "75.00",
                                billingCodes = listOf("BREAK"),
                            ),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.getBookingAllowances(
                        hotelId = booking.hotel.hotelId,
                        reservationId = reservationId,
                        testId = testId,
                        bookingAllowanceIds = listOf("premierInnBreakfast", "continentalBreakfast"),
                        featureFlagOverrides = bookingAllowancesFlagPins,
                    )

                result.attachEvidence("Get Booking Allowances Narrowed By Ids")

                expect("returns one allowance per supplied id rather than the single first match") {
                    result.response.status.value shouldBe 200
                    result.body.bookingAllowances.size shouldBe 2
                    result.body.bookingAllowances
                        .map { it.allowance }
                        .toSet() shouldBe setOf("premierInnBreakfast", "continentalBreakfast")
                    result.body.bookingAllowances
                        .map { it.budget }
                        .toSet() shouldBe setOf(75.00)
                }

                expect("reads the reservation from Opera exactly once") {
                    // One Opera call: GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an alcohol routing code is expanded into a paired dinner allowance carrying the budget") {
                val booking =
                    bookingAllowancesBooking(
                        reservationId = "6006604",
                        instruction =
                            RoutingFolioInstruction(
                                daily = true,
                                creditLimit = "80.00",
                                transactionCodes = listOf("FB"),
                            ),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.getBookingAllowances(
                        hotelId = booking.hotel.hotelId,
                        reservationId = reservationId,
                        testId = testId,
                        featureFlagOverrides = bookingAllowancesFlagPins,
                    )

                result.attachEvidence("Get Booking Allowances Alcohol Expansion")

                expect("returns alcohol at zero and an appended dinner allowance holding the budget") {
                    result.response.status.value shouldBe 200
                    result.body.bookingAllowances.size shouldBe 2
                    result.body.bookingAllowances
                        .map { it.allowance }
                        .toSet() shouldBe setOf("alcohol", "dinner")
                    result.body.bookingAllowances
                        .first { it.allowance == "alcohol" }
                        .budget shouldBe 0.0
                    result.body.bookingAllowances
                        .first { it.allowance == "dinner" }
                        .budget shouldBe 80.00
                }

                expect("reads the reservation from Opera exactly once") {
                    // One Opera call: GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            // rules-agent-entity-service is a real deployed collaborator, not one of the four
            // WireMock upstreams, so "no routing folio means rules-agent is never called" cannot be
            // proven with a callCount; the empty allowance list is the indirect proof. Logged in
            // data_model_issues/ohip-adapter-data-model.md.
            scenario("a routing instruction starting on a later date than the first has its codes dropped") {
                val booking =
                    bookingAllowancesBooking(
                        reservationId = "6006608",
                        instruction =
                            RoutingFolioInstruction(
                                daily = true,
                                creditLimit = "250.00",
                                transactionCodes = listOf("PARK"),
                            ),
                        additionalInstruction =
                            RoutingFolioInstruction(
                                daily = true,
                                creditLimit = "300.00",
                                billingCodes = listOf("WIFI"),
                                startDateOffset = 1,
                            ),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.getBookingAllowances(
                        hotelId = booking.hotel.hotelId,
                        reservationId = reservationId,
                        testId = testId,
                        featureFlagOverrides = bookingAllowancesFlagPins,
                    )

                result.attachEvidence("Get Booking Allowances Differing Start Dates")

                expect("returns only the first instruction's allowance and budget") {
                    result.response.status.value shouldBe 200
                    result.body.bookingAllowances.size shouldBe 1
                    result.body.bookingAllowances
                        .first()
                        .allowance shouldBe "carParking"
                    result.body.bookingAllowances
                        .first()
                        .budget shouldBe 250.00
                }

                expect("reads the reservation from Opera exactly once") {
                    // One Opera call: GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a reservation without routing instructions returns no allowances and no business notes") {
                val booking =
                    bookingAllowancesBooking(
                        reservationId = "6006605",
                        instruction = null,
                        comments =
                            listOf(
                                ReservationComment(
                                    title = "GUEST REQUEST",
                                    text = "Late arrival expected",
                                ),
                            ),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    ohipApi.getBookingAllowances(
                        hotelId = booking.hotel.hotelId,
                        reservationId = reservationId,
                        testId = testId,
                        featureFlagOverrides = bookingAllowancesFlagPins,
                    )

                result.attachEvidence("Get Booking Allowances No Routing")

                expect("returns an empty allowance list and leaves the non-business-notes comment out") {
                    result.response.status.value shouldBe 200
                    result.body.bookingAllowances.size shouldBe 0
                    result.body.businessNotes shouldBe null
                }

                expect("still reads the reservation from Opera exactly once") {
                    // One Opera call: GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a reservation id Opera holds nothing for returns an empty allowances payload") {
                val booking =
                    bookingAllowancesBooking(
                        reservationId = "6006606",
                        instruction =
                            RoutingFolioInstruction(
                                daily = true,
                                creditLimit = "50.00",
                                transactionCodes = listOf("PARK"),
                            ),
                    )
                val unknownReservationId = "6006699"

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationRoutingEmpty(booking.hotel.hotelId, unknownReservationId))

                val result =
                    ohipApi.getBookingAllowances(
                        hotelId = booking.hotel.hotelId,
                        reservationId = unknownReservationId,
                        testId = testId,
                        featureFlagOverrides = bookingAllowancesFlagPins,
                    )

                result.attachEvidence("Get Booking Allowances Unknown Reservation")

                expect("returns 200 with nothing resolved rather than an error") {
                    result.response.status.value shouldBe 200
                    result.body.bookingAllowances.size shouldBe 0
                    result.body.businessNotes shouldBe null
                }

                expect("attempts exactly one reservation read against Opera") {
                    // One Opera call: GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera rejection on the reservation read maps to the routing-instruction error") {
                val booking =
                    bookingAllowancesBooking(
                        reservationId = "6006607",
                        instruction =
                            RoutingFolioInstruction(
                                daily = true,
                                creditLimit = "60.00",
                                transactionCodes = listOf("PARK"),
                            ),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationBadRequest(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.getBookingAllowances(
                        hotelId = booking.hotel.hotelId,
                        reservationId = reservationId,
                        testId = testId,
                        featureFlagOverrides = bookingAllowancesFlagPins,
                    )

                result.attachEvidence("Get Booking Allowances Opera Rejection")

                expect("returns the mapped get-reservation-routing-instruction error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 939
                }

                expect("attempts exactly one reservation read against Opera") {
                    // One Opera call: the rejected GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }
        },
    )

private const val BUSINESS_NOTES_TITLE = "BUSINESS NOTES"
private const val BUSINESS_NOTES_TEXT = "Parking and wifi charged to the company"
private const val MEAL_DEAL_PACKAGE_CODE = "MD2DIN"

private fun bookingAllowancesBooking(
    reservationId: String,
    instruction: RoutingFolioInstruction?,
    additionalInstruction: RoutingFolioInstruction? = null,
    comments: List<ReservationComment> = emptyList(),
    selectedPackages: List<SelectedPackage> = emptyList(),
): Booking {
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
                    routingInstructions =
                        instruction
                            ?.let {
                                listOf(
                                    RoutingInstruction(
                                        folioWindowNumber = 1,
                                        instructions = listOfNotNull(it, additionalInstruction),
                                    ),
                                )
                            }.orEmpty(),
                    reservationComments = comments,
                    selectedPackages = selectedPackages,
                ),
            ),
    )
}
