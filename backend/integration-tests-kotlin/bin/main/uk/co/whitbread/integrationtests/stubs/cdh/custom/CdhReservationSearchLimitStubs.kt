package uk.co.whitbread.integrationtests.stubs.cdh.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.answering
import uk.co.whitbread.integrationtests.stubs.cdh.reservationSearch
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Booking

const val CDH_RESERVATION_SEARCH_LIMIT_EXCEEDED_STUB_ID = "booking.cdh.reservation-search.limit-exceeded"

/**
 * Models the Customer Data Hub reporting more matches than one search page can carry: the page
 * comes back with a total above the fifty-result ceiling the caller's mapper enforces.
 *
 * Both the V2 and the V3 route answer, because cdh-adapter-service picks between them with the
 * infrastructure-owned `release_pi_cdh_api_deprecation` flag that a journey cannot pin. The
 * default's request matchers are kept, so the over-limit page lands on exactly the search the
 * default would otherwise have answered. Install with [CDH_RESERVATION_SEARCH_STUB_ID] excluded.
 */
fun cdhReservationSearchLimitExceeded(booking: Booking): PlannedStub =
    reservationSearch(booking).answering(
        id = CDH_RESERVATION_SEARCH_LIMIT_EXCEEDED_STUB_ID,
        response =
            jsonResponse(
                jsonBody =
                    stubJsonObject(
                        "TotalResults" to OVER_LIMIT_TOTAL_RESULTS,
                        "SearchResults" to OVER_LIMIT_TOTAL_RESULTS,
                        "TotalSize" to 0,
                        "Results" to emptyList<String>(),
                    ),
            ),
    )

/** One past the fifty-result ceiling hotel-reservation-entity-service maps a page against. */
private const val OVER_LIMIT_TOTAL_RESULTS = 51
