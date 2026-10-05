package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.opera.deleteRoutingInstructions
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom

const val OPERA_ROUTING_INSTRUCTION_DELETE_ERROR_STUB_ID = "custom.opera.delete-routing-instruction-error"
const val OPERA_GET_RESERVATION_ROUTING_EMPTY_STUB_ID = "custom.opera.get-reservation-routing-empty"

/**
 * Builds an Opera 500 rejection for the cashiering routing-instruction DELETE, installed
 * with `booking.opera.delete-routing-instruction` excluded. Keeps the default matchers so
 * the failure hits exactly the per-instruction requests the adapter would otherwise succeed
 * with; the delete client has no retry spec, so the rejection surfaces on the first call.
 */
fun deleteRoutingInstructionFailure(
    booking: Booking,
    rooms: List<BookingRoom> = booking.rooms,
): PlannedStub =
    deleteRoutingInstructions(booking, rooms).rejectedByOpera(
        id = OPERA_ROUTING_INSTRUCTION_DELETE_ERROR_STUB_ID,
        detail = "Routing instruction could not be deleted.",
        status = 500,
    )

/**
 * Builds an Opera 200 reservation payload whose `reservations.reservation` array is empty,
 * for a reservation id Opera holds nothing for, installed with
 * `booking.opera.get-reservation` excluded.
 *
 * This is Opera's real not-found shape on the reservation GET: a success envelope carrying
 * no reservation, which drives the adapter's reservation-absent branches rather than any
 * error-status mapping.
 */
fun getReservationRoutingEmpty(
    hotelId: String,
    reservationId: String,
): PlannedStub =
    PlannedStub(
        id = OPERA_GET_RESERVATION_ROUTING_EMPTY_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            listOf(
                StubMapping(
                    request =
                        RequestPattern(
                            method = "GET",
                            urlPath = "/rsv/v1/hotels/$hotelId/reservations/$reservationId",
                        ),
                    response =
                        jsonResponse(
                            jsonBody =
                                stubJsonObject(
                                    "reservations" to
                                        stubJsonObject("reservation" to emptyList<Any>()),
                                ),
                        ),
                ),
            ),
    )
