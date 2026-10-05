package uk.co.whitbread.integrationtests.stubs.cdh

import kotlinx.serialization.json.JsonObject
import uk.co.whitbread.integrationtests.framework.wiremock.WireMockTarget
import uk.co.whitbread.integrationtests.framework.wiremock.model.BodyPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.RequestPattern
import uk.co.whitbread.integrationtests.framework.wiremock.model.StubMapping
import uk.co.whitbread.integrationtests.stubs.PlannedStub
import uk.co.whitbread.integrationtests.stubs.jsonPathStringLiteral
import uk.co.whitbread.integrationtests.stubs.jsonResponse
import uk.co.whitbread.integrationtests.stubs.stubJsonObject
import uk.co.whitbread.integrationtests.testkit.model.Booker
import uk.co.whitbread.integrationtests.testkit.model.Booking
import uk.co.whitbread.integrationtests.testkit.model.BookingRoom
import uk.co.whitbread.integrationtests.testkit.model.ReservationStatus

const val CDH_RESERVATION_SEARCH_STUB_ID = "booking.cdh.reservation-search"

private const val CDH_RESERVATION_SEARCH_V2_PATH = "/BookingServices/V2/ReservationSearch"
private const val CDH_RESERVATION_SEARCH_V3_PATH = "/BookingServices/V3/ReservationSearch"

/**
 * Builds CDH reservation-search mappings for the legacy reference declared by [booking].
 *
 * Both V2 and V3 are installed because cdh-adapter-service selects between them with the
 * infrastructure-owned `release_pi_cdh_api_deprecation` flag. A journey cannot pin that flag,
 * so both routes must describe the same deterministic world.
 *
 * The matcher pins only fields that select this response: the CDH booking reference and the
 * bookings-database mode used by confirmation lookup. Pagination is deliberately not matched;
 * cdh-adapter-service currently rewrites it to page 1 with size 50, which is caller plumbing.
 *
 * One `Results` entry is emitted per [BookingRoom], carrying that room's status, so a booking of
 * several rooms describes a page of several results in different states. A cancelled room's
 * [BookingRoom.cancellationDate] is reported as `CancellationDate`; consumers drop a cancelled
 * result that carries none, so the fact is emitted only when the world states it.
 *
 * Every result reports [Booking.booker] as its `Booker` block when the world states one, so a
 * booker-lastname search has an identity to filter on that is independent of the rooms' guests.
 * A booking without that fact emits no `Booker` at all, which is what CDH returns for a booking
 * whose booker it does not hold.
 *
 * Dates include an offset because hotel-reservation-entity-service parses them as ISO offset date
 * times. Every room includes a non-null guests collection because its CDH mapper streams the
 * collection without a null check.
 */
fun reservationSearch(booking: Booking): PlannedStub {
    val reference =
        requireNotNull(booking.cdhBookingReference) {
            "booking.cdhBookingReference must be configured for CDH reservation-search stubs"
        }
    require(booking.hotels.size == 1) {
        "exactly one booking hotel must be configured for CDH reservation-search stubs"
    }
    requireNotNull(booking.arrival) {
        "booking.arrival must be configured for CDH reservation-search stubs"
    }
    requireNotNull(booking.departure) {
        "booking.departure must be configured for CDH reservation-search stubs"
    }
    require(booking.rooms.isNotEmpty()) {
        "at least one booking room must be configured for CDH reservation-search stubs"
    }
    booking.rooms.forEach { room ->
        requireNotNull(room.reservationId) {
            "every BookingRoom.reservationId must be configured for CDH reservation-search stubs"
        }
    }

    val response = reservationSearchResponse(booking, reference)
    return PlannedStub(
        id = CDH_RESERVATION_SEARCH_STUB_ID,
        target = WireMockTarget.CDH,
        mappings =
            listOf(CDH_RESERVATION_SEARCH_V2_PATH, CDH_RESERVATION_SEARCH_V3_PATH).map { path ->
                reservationSearchMapping(path, reference, response)
            },
    )
}

private fun reservationSearchMapping(
    path: String,
    reference: String,
    response: JsonObject,
): StubMapping =
    StubMapping(
        request =
            RequestPattern(
                method = "POST",
                urlPath = path,
                bodyPatterns =
                    listOf(
                        BodyPattern(
                            matchesJsonPath =
                                "$[?(@.BookingReference == ${jsonPathStringLiteral(reference)})]",
                        ),
                        BodyPattern(matchesJsonPath = "$[?(@.BookingsDatabaseSearch == true)]"),
                    ),
            ),
        response = jsonResponse(jsonBody = response),
    )

private fun reservationSearchResponse(
    booking: Booking,
    reference: String,
) = stubJsonObject(
    "TotalResults" to booking.rooms.size,
    "SearchResults" to booking.rooms.size,
    "TotalSize" to booking.rooms.size,
    "Results" to booking.rooms.map { room -> reservationSearchResult(booking, reference, room) },
)

private fun reservationSearchResult(
    booking: Booking,
    reference: String,
    room: BookingRoom,
): JsonObject {
    val fields =
        buildList<Pair<String, Any?>> {
            add("SourceSystem" to "OPERA")
            add("BookingReference" to reference)
            booking.booker?.let { add("Booker" to bookerBlock(it)) }
            add("HotelName" to booking.hotel.name)
            add("HotelCode" to booking.hotel.hotelId)
            add("ArrivalDate" to "${booking.arrival}T00:00:00Z")
            add("DepartureDate" to "${booking.departure}T00:00:00Z")
            add("Status" to room.status.toCdhStatus())
            room.cancellationDate?.let { add("CancellationDate" to "${it}T00:00:00Z") }
            add("Amendable" to false)
            add("Cancellable" to false)
            add("WalkIn" to false)
            add("IsPackage" to false)
            add("PrePaid" to false)
            add("Rooms" to listOf(reservationSearchRoom(room)))
        }
    return stubJsonObject(*fields.toTypedArray())
}

private fun bookerBlock(booker: Booker): JsonObject {
    val fields =
        buildList<Pair<String, Any?>> {
            booker.title?.let { add("Title" to it) }
            add("FirstName" to booker.firstName)
            add("LastName" to booker.lastName)
            booker.email?.let { add("EmailAddress" to it) }
        }
    return stubJsonObject(*fields.toTypedArray())
}

private fun reservationSearchRoom(room: BookingRoom) =
    stubJsonObject(
        "ReservationId" to room.reservationId,
        "Status" to room.status.toCdhStatus(),
        "Cot" to false,
        "CarDetails" to false,
        "Guests" to
            listOfNotNull(
                room.guestProfile?.let { guest ->
                    stubJsonObject(
                        "LeadGuest" to true,
                        "FirstName" to guest.firstName,
                        "LastName" to guest.lastName,
                        "EmailAddress" to guest.email,
                    )
                },
            ),
    )

private fun ReservationStatus.toCdhStatus(): String =
    when (this) {
        ReservationStatus.CANCELLED -> "CANCELLED"
        ReservationStatus.CHECKED_IN -> "INHOUSE"
        ReservationStatus.CHECKED_OUT -> "CHECKEDOUT"
        ReservationStatus.NO_SHOW -> "NO_SHOW"
        else -> "RESERVED"
    }
