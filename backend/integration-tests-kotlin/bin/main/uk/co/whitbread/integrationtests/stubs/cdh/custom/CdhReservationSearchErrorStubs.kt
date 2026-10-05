package uk.co.whitbread.integrationtests.stubs.cdh.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.answering
import uk.co.whitbread.integrationtests.stubs.cdh.reservationSearch
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Booking

const val CDH_RESERVATION_SEARCH_SERVER_ERROR_STUB_ID = "booking.cdh.reservation-search.server-error"

/**
 * Models the Customer Data Hub booking store failing the reservation search.
 *
 * Both the V2 and the V3 route answer, because cdh-adapter-service picks between them with the
 * infrastructure-owned `release_pi_cdh_api_deprecation` flag that a journey cannot pin. The
 * default's request matchers are kept, so the failure lands on exactly the search the default
 * would otherwise have answered. Install with [CDH_RESERVATION_SEARCH_STUB_ID] excluded.
 */
fun cdhReservationSearchServerError(booking: Booking): PlannedStub =
    reservationSearch(booking).answering(
        id = CDH_RESERVATION_SEARCH_SERVER_ERROR_STUB_ID,
        response =
            jsonResponse(
                status = 500,
                jsonBody =
                    stubJsonObject(
                        "Code" to "InternalServerError",
                        "Message" to "Reservation search is unavailable.",
                    ),
            ),
    )
