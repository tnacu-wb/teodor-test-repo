package uk.co.whitbread.integrationtests.stubs.opera

import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StringValuePattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom

const val OPERA_RESERVATION_DEPOSITS_STUB_ID = "booking.opera.reservation-deposits"

/**
 * Builds Opera deposit reads for reservations, preserving the canonical empty-deposit response
 * unless a room declares a posted deposit payment reference.
 *
 * The `id` query selects the reservation and is pinned. The response retains one folio-info row
 * because OHIP indexes it without a null or empty check. Its `deposits` collection is also always
 * present because OHIP iterates it directly. A posted deposit derives its reference from the room,
 * its amount from `amountAlreadyPaid`, and its currency from the hotel.
 */
fun reservationDeposits(
    booking: Booking,
    rooms: List<BookingRoom>,
): PlannedStub =
    PlannedStub(
        id = OPERA_RESERVATION_DEPOSITS_STUB_ID,
        target = WireMockTarget.OPERA,
        mappings = rooms.map { room -> reservationDepositsMapping(booking, room) },
    )

private fun reservationDepositsMapping(
    booking: Booking,
    room: BookingRoom,
): StubMapping {
    val reservationId =
        requireNotNull(room.reservationId) {
            "BookingRoom.reservationId must be configured for reservation-deposit stubs"
        }
    val deposits =
        room.depositPaymentReference?.let { paymentReference ->
            listOf(
                mapOf(
                    "reference" to paymentReference,
                    "postedAmount" to
                        mapOf(
                            "amount" to room.amountAlreadyPaid,
                            "currencyCode" to booking.hotel.currency,
                        ),
                ),
            )
        } ?: emptyList()

    return StubMapping(
        request =
            RequestPattern(
                method = "GET",
                urlPath = "/csh/v1/hotels/${booking.hotel.hotelId}/depositFolio",
                headers = mapOf("x-hotelid" to StringValuePattern(equalTo = booking.hotel.hotelId)),
                queryParameters = mapOf("id" to StringValuePattern(equalTo = reservationId)),
            ),
        response =
            jsonResponse(
                jsonBody =
                    stubJsonObject(
                        "reservationDepositFoliosInfo" to
                            listOf(
                                mapOf(
                                    "deposits" to deposits,
                                    "depositType" to "None",
                                    "depositMaturityType" to "None",
                                ),
                            ),
                        "trxCodesInfo" to emptyList<Any>(),
                        "links" to emptyList<Any>(),
                        "warnings" to emptyList<Any>(),
                    ),
            ),
    )
}
