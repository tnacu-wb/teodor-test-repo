package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom

const val OPERA_DEPOSIT_FOLIOS_STUB_ID = "booking.opera.deposit-folios"

/**
 * Builds the Opera cashiering mappings that acknowledge a successful deposit-folio posting.
 *
 * One mapping is produced for each reservation room. The request body is deliberately not
 * matched: callers build different payment and charge combinations, while the hotel and
 * reservation path identifies the downstream capability. Request-body mapping belongs in the
 * adapter's service or contract tests rather than in a shared journey stub.
 *
 * The default response represents a successful posting for a caller that does not need Opera's
 * returned posting details. Every collection is present because other adapter paths iterate the
 * `deposits` collection without a null check. A future journey that needs returned deposit details
 * should add the reusable posted-deposit world fact to Booking and make this builder data-driven.
 */
fun depositFolios(
    booking: Booking,
    room: BookingRoom,
): PlannedStub = depositFolios(booking, listOf(room))

fun depositFolios(
    booking: Booking,
    rooms: List<BookingRoom>,
): PlannedStub =
    PlannedStub(
        id = OPERA_DEPOSIT_FOLIOS_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = rooms.map { room -> depositFoliosMapping(booking, room) },
    )

private fun depositFoliosMapping(
    booking: Booking,
    room: BookingRoom,
): StubMapping {
    val reservationId =
        room.reservationId
            ?: error("BookingRoom.reservationId must be configured for deposit-folio stubs")

    return StubMapping(
        request =
            RequestPattern(
                method = "POST",
                urlPath = "/csh/v1/hotels/${booking.hotel.hotelId}/reservations/$reservationId/depositFolios",
            ),
        response =
            jsonResponse(
                status = 201,
                jsonBody = successfulDepositFoliosBody(),
            ),
    )
}

private fun successfulDepositFoliosBody() =
    stubJsonObject(
        "folioWindow" to emptyList<Any>(),
        "deposits" to emptyList<Any>(),
        "trxCodesInfo" to emptyList<Any>(),
        "links" to emptyList<Any>(),
        "warnings" to emptyList<Any>(),
    )
