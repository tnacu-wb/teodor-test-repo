package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.ResponseDefinition
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom

const val OPERA_ATTACHMENT_DELETE_ANY_STUB_ID = "custom.opera.delete-reservation-attachment"

/**
 * Builds a permissive Opera acknowledgement for *any* attachment DELETE on a reservation,
 * regardless of attachment id.
 *
 * Absence-proof install: a scenario expecting the adapter to skip attachment
 * deletion installs this stub directly so a mistaken DELETE would still be served, counted,
 * and visible in `callCount`, instead of failing to match and hiding in a 404.
 */
fun deleteAnyReservationAttachment(
    booking: Booking,
    room: BookingRoom = booking.room,
): PlannedStub {
    val hotelId = booking.hotel.hotelId
    val reservationId =
        requireNotNull(room.reservationId) { "attachment-absence rooms need a reservationId" }
    return PlannedStub(
        id = OPERA_ATTACHMENT_DELETE_ANY_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings =
            listOf(
                StubMapping(
                    request =
                        RequestPattern(
                            method = "DELETE",
                            urlPattern =
                                "/rsv/v1/hotels/$hotelId/reservations/$reservationId/attachments/.*",
                        ),
                    response = ResponseDefinition(status = 204),
                ),
            ),
    )
}
