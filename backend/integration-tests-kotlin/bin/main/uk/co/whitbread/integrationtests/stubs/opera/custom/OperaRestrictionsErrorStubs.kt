package uk.co.whitbread.integrationtests.stubs.opera.custom

import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.opera.restrictionsByDateRange
import uk.co.whitbread.integrationtests.testkit.model.Booking
import java.time.LocalDate

const val OPERA_RESTRICTIONS_REJECTED_CHUNK_STUB_ID = "opera.restrictions.rejected-chunk"

/**
 * Rebuilds the default restrictions stub with one (hotel, chunk) mapping swapped for an Opera
 * rejection, keeping every sibling chunk and hotel serving normally. Install with the default
 * restrictions stub excluded.
 */
fun restrictionsWithRejectedChunk(
    booking: Booking,
    failingHotelId: String,
    failingChunkStart: LocalDate,
): PlannedStub {
    val default = restrictionsByDateRange(booking)
    val target = "/par/v1/hotels/$failingHotelId/restrictions"
    val start = failingChunkStart.toString()
    var rejected = 0
    val mappings =
        default.mappings.map { mapping ->
            val matchesHotel = mapping.request.urlPath == target
            val matchesChunk =
                mapping.request.queryParameters
                    ?.get("restrictionSearchCriteriaStartDate")
                    ?.equalTo == start
            if (matchesHotel && matchesChunk) {
                rejected += 1
                mapping.rejectedByOpera("Restrictions could not be fetched.")
            } else {
                mapping
            }
        }
    require(rejected == 1) {
        "expected exactly one restrictions mapping for hotel $failingHotelId starting $start, found $rejected"
    }
    return default.copy(id = OPERA_RESTRICTIONS_REJECTED_CHUNK_STUB_ID, mappings = mappings)
}
