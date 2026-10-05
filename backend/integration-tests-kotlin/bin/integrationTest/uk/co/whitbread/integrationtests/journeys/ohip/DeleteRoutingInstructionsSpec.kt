package uk.co.whitbread.integrationtests.journeys.ohip

import io.kotest.matchers.shouldBe
import uk.co.whitbread.integrationtests.clients.ohip.OhipApi
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

// The opera token-service flags are environment-pinned OFF; no other flag gates this chain.
private val deleteRoutingFlagPins =
    mapOf<FeatureFlag, Boolean>(
        OhipFeatureFlag.USE_TOKEN_SERVICE to false,
    )

private const val BUSINESS_NOTES_TITLE = "BUSINESS NOTES"

/**
 * Proves `DELETE /ohip/v1/reservations/routingInstructions`: for each reservation the
 * adapter fetches routing instructions and comments, issues one Opera cashiering DELETE per
 * charge-routing instruction, and removes each Business Notes comment with a
 * change-reservation PUT, returning 204 with no body.
 *
 * Flow: backend/integration-tests-kotlin/flows/ohip-adapter-service/DeleteRoutingInstructions.md
 */
class DeleteRoutingInstructionsSpec :
    JourneySpec(
        "OHIP adapter deletes reservation routing instructions",
        {
            val ohipApi = OhipApi()

            scenario("a routed reservation loses its instruction and its Business Notes comment") {
                val booking =
                    routingBooking(
                        routedRoom(
                            reservationId = "6007701",
                            instructions = listOf(RoutingFolioInstruction(daily = true)),
                            comments =
                                listOf(
                                    ReservationComment(
                                        title = BUSINESS_NOTES_TITLE,
                                        commentId = "9101",
                                    ),
                                ),
                        ),
                    )

                installFor(booking)

                val result =
                    ohipApi.deleteRoutingInstructions(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf("6007701"),
                        testId = testId,
                        featureFlagOverrides = deleteRoutingFlagPins,
                    )

                result.attachEvidence("Delete Routing Instructions")

                expect("returns 204 with no body") {
                    result.response.status.value shouldBe 204
                }

                expect("fetches the reservation, deletes the instruction, and removes the note") {
                    // Three Opera calls: reservation GET, cashiering routing-instruction
                    // DELETE, and the Business Notes change-reservation PUT.
                    callCount(Upstream.OPERA) shouldBe 3
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.DELETE_ROUTING_INSTRUCTION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                    callCount(Upstream.AEM) shouldBe 0
                    callCount(Upstream.CDH) shouldBe 0
                    callCount(Upstream.WORLDLINE) shouldBe 0
                }
            }

            scenario("a two-reservation batch fans out per instruction and skips absent shapes") {
                val routedRoom =
                    routedRoom(
                        reservationId = "6007702",
                        instructions =
                            listOf(
                                RoutingFolioInstruction(daily = true),
                                RoutingFolioInstruction(
                                    daily = false,
                                    creditLimit = "250.00",
                                    routingLinkId = "RL-6007702",
                                    transactionCodes = listOf("1000"),
                                    billingCodes = listOf("BC1"),
                                ),
                            ),
                        comments = emptyList(),
                    )
                val notesOnlyRoom =
                    routedRoom(
                        reservationId = "6007703",
                        instructions = null,
                        comments =
                            listOf(
                                ReservationComment(
                                    title = BUSINESS_NOTES_TITLE,
                                    commentId = "9103",
                                ),
                            ),
                    )
                val booking = routingBooking(routedRoom, notesOnlyRoom)

                installFor(booking)

                val result =
                    ohipApi.deleteRoutingInstructions(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf("6007702", "6007703"),
                        testId = testId,
                        featureFlagOverrides = deleteRoutingFlagPins,
                    )

                result.attachEvidence("Delete Routing Instructions Batch")

                expect("returns 204 with no body") {
                    result.response.status.value shouldBe 204
                }

                expect("issues exactly the per-instruction deletes and the single notes PUT") {
                    // Five Opera calls: two reservation GETs, two cashiering DELETEs for the
                    // routed reservation's instructions, and one Business Notes PUT for the
                    // notes-only sibling. No DELETE for the instruction-less reservation and
                    // no PUT for the comment-less one.
                    callCount(Upstream.OPERA) shouldBe 5
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 2
                    callCount(OperaEndpoint.DELETE_ROUTING_INSTRUCTION) shouldBe 2
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                }
            }

            scenario("an Opera rejection of the reservation fetch maps to internal error 939") {
                val booking =
                    routingBooking(
                        routedRoom(
                            reservationId = "6007711",
                            instructions = listOf(RoutingFolioInstruction(daily = true)),
                            comments = emptyList(),
                        ),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                // A downstream failure is exceptional behavior, not a normal Booking world
                // state. The routing-instruction DELETE and put-reservation defaults stay
                // installed as absence mocks.
                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationBadRequest(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.deleteRoutingInstructions(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                        featureFlagOverrides = deleteRoutingFlagPins,
                    )

                result.attachEvidence("Delete Routing Instructions Fetch Error")

                expect("returns the mapped routing-fetch error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 939
                }

                expect("stops after the rejected reservation GET") {
                    // One Opera call: the rejected GET. No cashiering DELETE, no notes PUT.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                }
            }

            scenario("an Opera rejection of the instruction delete maps to internal error 937") {
                val booking =
                    routingBooking(
                        routedRoom(
                            reservationId = "6007712",
                            instructions = listOf(RoutingFolioInstruction(daily = true)),
                            comments =
                                listOf(
                                    ReservationComment(
                                        title = BUSINESS_NOTES_TITLE,
                                        commentId = "9111",
                                    ),
                                ),
                        ),
                    )

                installFor(booking, excluded = setOf(OPERA_ROUTING_INSTRUCTION_DELETE_STUB_ID))
                installStub(deleteRoutingInstructionFailure(booking))

                val result =
                    ohipApi.deleteRoutingInstructions(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf("6007712"),
                        testId = testId,
                        featureFlagOverrides = deleteRoutingFlagPins,
                    )

                result.attachEvidence("Delete Routing Instructions Delete Error")

                expect("returns the mapped instruction-delete error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 937
                }

                expect("aborts before the Business Notes PUT") {
                    // Two Opera calls: the GET and the rejected cashiering DELETE. The
                    // default put-reservation stub stays installed as the absence mock for
                    // the never-sent Business Notes removal.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.DELETE_ROUTING_INSTRUCTION) shouldBe 1
                }
            }

            scenario("an Opera rejection of the Business Notes put maps to internal error 958") {
                val booking =
                    routingBooking(
                        routedRoom(
                            reservationId = "6007713",
                            instructions = null,
                            comments =
                                listOf(
                                    ReservationComment(
                                        title = BUSINESS_NOTES_TITLE,
                                        commentId = "9113",
                                    ),
                                ),
                        ),
                    )

                // The failure envelope's type is `Internal Server Error`, which the
                // adapter's shared retry spec does not retry, so the mapped exception
                // surfaces immediately instead of after the backoff window.
                installFor(booking, excluded = setOf(OPERA_RESERVATION_PUT_STUB_ID))
                installStub(putReservationFailure(booking))

                val result =
                    ohipApi.deleteRoutingInstructions(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf("6007713"),
                        testId = testId,
                        featureFlagOverrides = deleteRoutingFlagPins,
                    )

                result.attachEvidence("Delete Routing Instructions Notes Put Error")

                expect("returns the mapped change-reservation error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 958
                }

                expect("fails on the single Business Notes PUT without retrying") {
                    // Two Opera calls: the GET and the rejected change-reservation PUT.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.UPDATE_RESERVATION) shouldBe 1
                }
            }

            scenario("a failing first reservation aborts the rest of the batch") {
                val booking =
                    routingBooking(
                        routedRoom(
                            reservationId = "6007714",
                            instructions = listOf(RoutingFolioInstruction(daily = true)),
                            comments = emptyList(),
                        ),
                        routedRoom(
                            reservationId = "6007715",
                            instructions = listOf(RoutingFolioInstruction(daily = true)),
                            comments = emptyList(),
                        ),
                    )

                // Both reservations' cashiering DELETEs are swapped for rejections; the
                // second reservation's GET default stays installed as the absence mock
                // proving the batch stopped at the first failure.
                installFor(booking, excluded = setOf(OPERA_ROUTING_INSTRUCTION_DELETE_STUB_ID))
                installStub(deleteRoutingInstructionFailure(booking))

                val result =
                    ohipApi.deleteRoutingInstructions(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf("6007714", "6007715"),
                        testId = testId,
                        featureFlagOverrides = deleteRoutingFlagPins,
                    )

                result.attachEvidence("Delete Routing Instructions Batch Abort")

                expect("returns the mapped instruction-delete error") {
                    result.response.status.value shouldBe 500
                    result.errorBody?.errCode shouldBe 937
                }

                expect("never touches the second reservation") {
                    // Two Opera calls: the first reservation's GET and its rejected
                    // cashiering DELETE. No GET for the second reservation.
                    callCount(Upstream.OPERA) shouldBe 2
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
                    callCount(OperaEndpoint.DELETE_ROUTING_INSTRUCTION) shouldBe 1
                }
            }

            // Documented service bug: a reservation Opera holds nothing for crashes the adapter with an
            // unhandled IndexOutOfBoundsException (deleteBusinessNotes calls .get(0) on the
            // empty reservation list) and surfaces a raw 500 instead of degrading
            // gracefully. See bug/delete-routing-instructions-empty-reservation-500.md.
            // Re-enable when the adapter handles the empty reservation payload.
            scenario("!a reservation Opera holds nothing for is skipped without crashing") {
                val booking =
                    routingBooking(
                        routedRoom(
                            reservationId = "6007716",
                            instructions = listOf(RoutingFolioInstruction(daily = true)),
                            comments = emptyList(),
                        ),
                    )
                val reservationId = requireNotNull(booking.room.reservationId)

                installFor(booking, excluded = setOf(OPERA_RESERVATION_GET_STUB_ID))
                installStub(getReservationRoutingEmpty(booking.hotel.hotelId, reservationId))

                val result =
                    ohipApi.deleteRoutingInstructions(
                        hotelId = booking.hotel.hotelId,
                        reservationIds = listOf(reservationId),
                        testId = testId,
                        featureFlagOverrides = deleteRoutingFlagPins,
                    )

                result.attachEvidence("Delete Routing Instructions Empty Reservation")

                expect("degrades to a no-op 204 for the absent reservation") {
                    result.response.status.value shouldBe 204
                }

                expect("stops after the empty reservation GET") {
                    // One Opera call: the GET. No cashiering DELETE and no notes PUT.
                    callCount(Upstream.OPERA) shouldBe 1
                    callCount(OperaEndpoint.GET_RESERVATION) shouldBe 1
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

private fun routingBooking(vararg rooms: BookingRoom): Booking {
    val arrival = LocalDate.now().plusDays(4)
    val rate = Rate(ratePlan = "SEMIFLEX", roomType = "LOWDBL", adults = 2)

    return Booking(
        hotels = listOf(Hotels.HEAPTI.copy(availableRates = listOf(rate))),
        arrival = arrival,
        departure = arrival.plusDays(2),
        rooms = rooms.toList(),
    )
}
