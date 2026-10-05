package uk.co.whitbread.integrationtests.journeys.hotelreservation

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.hotelreservation.HotelReservationApi
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_GET_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_RESERVATION_PUT_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.OPERA_ROUTING_INSTRUCTION_DELETE_STUB_ID
import uk.co.whitbread.integrationtests.stubs.opera.custom.deleteRoutingInstructionFailure
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationBadRequest
import uk.co.whitbread.integrationtests.stubs.opera.custom.getReservationRoutingEmpty
import uk.co.whitbread.integrationtests.stubs.opera.custom.putReservationFailure
import uk.co.whitbread.integrationtests.testkit.JourneySpec
import uk.co.whitbread.integrationtests.testkit.featureflags.FeatureFlag
import uk.co.whitbread.integrationtests.testkit.featureflags.OhipFeatureFlag
import uk.co.whitbread.integrationtests.testkit.mocks.OperaEndpoint
import uk.co.whitbread.integrationtests.testkit.mocks.Upstream
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.Rate
import uk.co.whitbread.integrationtests.testkit.model.ReservationComment
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus
import uk.co.whitbread.integrationtests.testkit.model.RoutingFolioInstruction
import uk.co.whitbread.integrationtests.testkit.model.RoutingInstruction
import uk.co.whitbread.integrationtests.testkit.presets.Hotels
import java.time.LocalDate

// No request-scoped flag gates this chain; only the environment-pinned OFF opera
// token-service flags apply, and they are evaluated outside the request context.
private val deleteRoutingInstructionsFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
    )

private const val BUSINESS_NOTES_TITLE = "BUSINESS NOTES"

/**
 * Proves `DELETE /v1/reservations/routingInstructions`: hotel-reservation-entity-service
 * passes hotelId and the reservationIds set straight through to ohip-adapter-service, which
 * fetches each reservation, issues one Opera cashiering DELETE per charge-routing instruction
 * and one change-reservation PUT per Business Notes comment, and answers 204 with no body;
 * any Opera rejection surfaces as a 500 envelope carrying the adapter's errCode.
 *
 * Flow: backend/integration-tests-kotlin/flows/hotel-reservation-entity-service/DeleteRoutingInstructions.md
 */
class DeleteRoutingInstructionsSpec :
    JourneySpec(
        "Hotel reservation deletes reservation routing instructions",
        {
            val hotelReservationApi = HotelReservationApi()

            scenario("a routed reservation loses its instruction and its Business Notes comment through HRE") {
                val booking =
                    deleteRoutingBooking(
                        routedRoom(
                            reservationId = "6107701",
                            instructions = listOf(RoutingFolioInstruction(daily = true)),
                            comments =
                                listOf(
                                    ReservationComment(
                                        title = BUSINESS_NOTES_TITLE,
                                        commentId = "9201",
                                    ),
                                ),
                        ),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking)

                val result =
                    hotelReservationApi.deleteRoutingInstructions(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                        featureFlagOverrides = deleteRoutingInstructionsFlagPins,
                    )

                result.attachEvidence("Delete Routing Instructions")

                expect("returns 204 with no body") {
                    result.response.status.value shouldBe 204
                }

                expect("calls Opera to fetch, delete the instruction, and remove the note") {
                    // Three Opera calls: GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId},
                    // DELETE /csh/v1/hotels/{hotelId}/reservations/{reservationId}/routingInstructions/folio,
                    // and PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.DELETE_ROUTING_INSTRUCTION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a two-reservation batch fans out per instruction and issues no cashiering delete for the unrouted reservation") {
                val routedRoom =
                    routedRoom(
                        reservationId = "6107702",
                        instructions =
                            listOf(
                                RoutingFolioInstruction(daily = true),
                                RoutingFolioInstruction(
                                    daily = false,
                                    creditLimit = "250.00",
                                    routingLinkId = "RL-6107702",
                                    transactionCodes = listOf("1000"),
                                    billingCodes = listOf("BC1"),
                                ),
                            ),
                        comments = emptyList(),
                    )
                val unroutedRoom =
                    routedRoom(
                        reservationId = "6107703",
                        instructions = null,
                        comments =
                            listOf(
                                ReservationComment(
                                    title = BUSINESS_NOTES_TITLE,
                                    commentId = "9203",
                                ),
                            ),
                    )
                val booking = deleteRoutingBooking(routedRoom, unroutedRoom)

                installFor(booking)

                val result =
                    hotelReservationApi.deleteRoutingInstructions(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf("6107702", "6107703"),
                        testId = testId,
                        featureFlagOverrides = deleteRoutingInstructionsFlagPins,
                    )

                result.attachEvidence("Delete Routing Instructions Batch")

                expect("returns 204 with no body") {
                    result.response.status.value shouldBe 204
                }

                expect("calls Opera once per instruction and never for the unrouted reservation") {
                    // Five Opera calls: two reservation GETs, two cashiering DELETEs on
                    // /csh/v1/hotels/{hotelId}/reservations/{reservationId}/routingInstructions/folio
                    // for the routed reservation's two instructions, and one change-reservation
                    // PUT for the sibling's Business Notes. The cashiering delete default is
                    // installed for both rooms, so the zero deletes for the unrouted
                    // reservation are proven against a live mock.
                    callCount(Upstream.OPERA) shouldBe 5
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.DELETE_ROUTING_INSTRUCTION) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                }
            }

            scenario("an Opera rejection of the reservation fetch surfaces as errCode 939") {
                val booking =
                    deleteRoutingBooking(
                        routedRoom(
                            reservationId = "6107711",
                            instructions = listOf(RoutingFolioInstruction(daily = true)),
                            comments = emptyList(),
                        ),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                // A downstream failure is exceptional behavior, not a normal Booking world
                // state. The cashiering delete and put-reservation defaults stay installed as
                // absence mocks.
                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationBadRequest(booking.hotel.hotelId, reservationId))

                val result =
                    hotelReservationApi.deleteRoutingInstructions(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                        featureFlagOverrides = deleteRoutingInstructionsFlagPins,
                    )

                result.attachEvidence("Delete Routing Instructions Fetch Error")

                expect("returns the mapped routing-fetch error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 939
                    result.errorBody?.globalErrTextTemplate shouldBe "internal.server.exception"
                }

                expect("calls Opera once and stops at the rejected reservation GET") {
                    // One Opera call: the rejected GET /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.DELETE_ROUTING_INSTRUCTION) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("an Opera rejection of the instruction delete surfaces as errCode 937 and aborts the Business Notes removal") {
                val booking =
                    deleteRoutingBooking(
                        routedRoom(
                            reservationId = "6107712",
                            instructions = listOf(RoutingFolioInstruction(daily = true)),
                            comments =
                                listOf(
                                    ReservationComment(
                                        title = BUSINESS_NOTES_TITLE,
                                        commentId = "9211",
                                    ),
                                ),
                        ),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_ROUTING_INSTRUCTION_DELETE_STUB_ID))
                installStub(deleteRoutingInstructionFailure(booking))

                val result =
                    hotelReservationApi.deleteRoutingInstructions(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                        featureFlagOverrides = deleteRoutingInstructionsFlagPins,
                    )

                result.attachEvidence("Delete Routing Instructions Delete Error")

                expect("returns the mapped instruction-delete error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 937
                }

                expect("calls Opera twice and never issues the Business Notes PUT") {
                    // Two Opera calls: the reservation GET and the rejected cashiering DELETE.
                    // The put-reservation default stays installed, so the zero PUTs prove the
                    // abort rather than a missing mapping.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.DELETE_ROUTING_INSTRUCTION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                }
            }

            scenario("an Opera rejection of the Business Notes put surfaces as errCode 958") {
                val booking =
                    deleteRoutingBooking(
                        routedRoom(
                            reservationId = "6107713",
                            instructions = null,
                            comments =
                                listOf(
                                    ReservationComment(
                                        title = BUSINESS_NOTES_TITLE,
                                        commentId = "9213",
                                    ),
                                ),
                        ),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                // The failure envelope's type is `Internal Server Error`, which the adapter's
                // shared retry spec does not retry, so the mapped exception surfaces
                // immediately instead of after the backoff window.
                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationFailure(booking))

                val result =
                    hotelReservationApi.deleteRoutingInstructions(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                        featureFlagOverrides = deleteRoutingInstructionsFlagPins,
                    )

                result.attachEvidence("Delete Routing Instructions Notes Put Error")

                expect("returns the mapped change-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 958
                }

                expect("calls Opera twice with no cashiering delete on the unrouted reservation") {
                    // Two Opera calls: the reservation GET and the rejected change-reservation
                    // PUT /rsv/v1/hotels/{hotelId}/reservations/{reservationId}.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.DELETE_ROUTING_INSTRUCTION) shouldBe 0
                }
            }

            // Documented service bug: a reservation Opera holds nothing for crashes the adapter with an
            // unhandled IndexOutOfBoundsException (deleteBusinessNotes calls .get(0) on the
            // empty reservation list) and hotel-reservation-entity-service surfaces a raw 500
            // instead of a graceful no-op. See
            // bug/delete-routing-instructions-empty-reservation-500.md. Re-enable when the
            // adapter handles the empty reservation payload.
            scenario("!a reservation Opera holds nothing for is skipped without crashing") {
                val booking =
                    deleteRoutingBooking(
                        routedRoom(
                            reservationId = "6107714",
                            instructions = listOf(RoutingFolioInstruction(daily = true)),
                            comments = emptyList(),
                        ),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationRoutingEmpty(booking.hotel.hotelId, reservationId))

                val result =
                    hotelReservationApi.deleteRoutingInstructions(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                        featureFlagOverrides = deleteRoutingInstructionsFlagPins,
                    )

                result.attachEvidence("Delete Routing Instructions Empty Reservation")

                expect("degrades to a no-op 204 for the absent reservation") {
                    result.response.status.value shouldBe 204
                }

                expect("calls Opera once and stops after the empty reservation GET") {
                    // One Opera call: the GET. No cashiering DELETE and no notes PUT.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.DELETE_ROUTING_INSTRUCTION) shouldBe 0
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 0
                }
            }
        },
    )

private fun routedRoom(
    reservationId: String,
    instructions: List<RoutingFolioInstruction>?,
    comments: List<ReservationComment>,
): BookingRoom =
    BookingRoom(
        reservationId = reservationId,
        roomType = "LOWDBL",
        adults = 2,
        status = ReservationStatus.RESERVED,
        routingInstructions =
            instructions?.let {
                listOf(
                    RoutingInstruction(
                        folioWindowNumber = 1,
                        payeeProfileId = "5001$reservationId",
                        instructions = it,
                    ),
                )
            } ?: emptyList(),
        reservationComments = comments,
    )

private fun deleteRoutingBooking(vararg rooms: BookingRoom): Booking {
    val arrival = LocalDate.now().plusDays(4)
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms = rooms.toList(),
    )
}
