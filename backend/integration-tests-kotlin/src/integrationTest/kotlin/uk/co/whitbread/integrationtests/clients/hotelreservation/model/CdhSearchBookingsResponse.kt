package uk.co.whitbread.integrationtests.clients.hotelreservation.model

import kotlinx.serialization.Serializable

/**
 * hotel-reservation-entity-service's CDH booking-search page: the caller's slice of the re-sorted
 * results plus the counters that describe it. `cdhSearchResults` is the total CDH reported,
 * `searchResults` the number surviving filtering and the one-year windows, `pageResults` the size
 * of this page, and `responseLimitExceeded` marks the over-fifty short circuit that empties the
 * page. `operaConfNumber` echoes the requested reference when it was numeric or Opera-style.
 */
@Serializable
data class CdhSearchBookingsResponse(
    val results: List<CdhSearchResult> = emptyList(),
    val cdhSearchResults: Int = 0,
    val searchResults: Int = 0,
    val pageResults: Int = 0,
    val hasMore: Boolean = false,
    val responseLimitExceeded: Boolean = false,
    val operaConfNumber: String? = null,
)

@Serializable
data class CdhSearchResult(
    val bookingReference: String? = null,
    val status: String? = null,
    val hotelId: String? = null,
    val hotelName: String? = null,
    val sourceSystem: String? = null,
    val arrivalDate: String? = null,
    val departureDate: String? = null,
    val rooms: List<CdhSearchResultRoom> = emptyList(),
)

@Serializable
data class CdhSearchResultRoom(
    val reservationId: String? = null,
)
