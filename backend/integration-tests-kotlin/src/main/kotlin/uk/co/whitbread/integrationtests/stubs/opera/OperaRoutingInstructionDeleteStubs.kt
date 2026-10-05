package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.ResponseDefinition
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.RoutingFolioInstruction
import uk.co.whitbread.integrationtests.testkit.model.RoutingInstruction

const val OPERA_ROUTING_INSTRUCTION_DELETE_STUB_ID = "booking.opera.delete-routing-instruction"

/**
 * Models Opera's cashiering API removing one charge-routing instruction from a reservation
 * folio.
 *
 * Opera identifies the doomed instruction entirely through query parameters — the folio's
 * payee and window plus the instruction's duration flags and optional routing identifiers —
 * so each instruction a reservation's routing-instruction facts carry gets its own mapping
 * pinning that exact parameter shape. A caller that drops a required parameter stops
 * matching. Opera acknowledges each removal with an empty 200.
 */
fun deleteRoutingInstructions(
    booking: Booking,
    rooms: List<BookingRoom>,
): PlannedStub =
    PlannedStub(
        id = OPERA_ROUTING_INSTRUCTION_DELETE_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            rooms.flatMap { room ->
                room.routingInstructions.flatMap { routingInstruction ->
                    routingInstruction.instructions.map { instruction ->
                        deleteRoutingInstructionMapping(booking, room, routingInstruction, instruction)
                    }
                }
            },
    )

private fun deleteRoutingInstructionMapping(
    booking: Booking,
    room: BookingRoom,
    routingInstruction: RoutingInstruction,
    instruction: RoutingFolioInstruction,
): StubMapping {
    val hotelId = booking.hotel.hotelId
    val reservationId =
        requireNotNull(room.reservationId) { "routing-instruction rooms need a reservationId" }
    return StubMapping(
        request =
            RequestPattern(
                method = "DELETE",
                urlPath =
                    "/csh/v1/hotels/$hotelId/reservations/$reservationId/routingInstructions/folio",
                headers = hotelHeaders(hotelId),
                queryParameters =
                    buildMap {
                        put("payeeId", StringValuePattern(equalTo = routingInstruction.payeeProfileId))
                        put(
                            "folioWindowNo",
                            StringValuePattern(equalTo = routingInstruction.folioWindowNumber.toString()),
                        )
                        put("daily", StringValuePattern(equalTo = instruction.daily.toString()))
                        listOf("sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday")
                            .forEach { day -> put(day, StringValuePattern(equalTo = "true")) }
                        if (instruction.daily) {
                            put("startDate", StringValuePattern(equalTo = booking.arrival.toString()))
                            put("endDate", StringValuePattern(equalTo = booking.departure.toString()))
                        }
                        put("retrievePostingsForRoomRouting", StringValuePattern(equalTo = "false"))
                        instruction.creditLimit?.let {
                            put("creditLimit", StringValuePattern(equalTo = it))
                        }
                        instruction.routingLinkId?.let {
                            put("routingLinkId", StringValuePattern(equalTo = it))
                        }
                        instruction.transactionCodes.firstOrNull()?.let {
                            put("transactionCode", StringValuePattern(equalTo = it))
                        }
                        instruction.billingCodes.firstOrNull()?.let {
                            put("billingCode", StringValuePattern(equalTo = it))
                        }
                    },
            ),
        response = ResponseDefinition(status = 200),
    )
}
