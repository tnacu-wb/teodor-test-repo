package uk.co.whitbread.integrationtests.clients.hotelreservation.model

import kotlinx.serialization.Serializable

/**
 * Body of `PUT /v1/reservations/amend/editRoom`: the room-level edit applied to one Opera
 * reservation held by the basket named in [tempBookingRef].
 *
 * [token] is mandatory for unauthenticated callers unless `release_amend_distribution_single_call`
 * is enabled and [bookingChannel]'s channel is `DISTR`; [ratePlanCode] and [specialRequests] are
 * accepted by the DTO but never read on this path.
 */
@Serializable
data class EditRoomRequest(
    val tempBookingRef: String,
    val reservationId: String,
    val roomType: String,
    val roomOccupancy: EditRoomOccupancy,
    val leadGuest: EditRoomLeadGuest,
    val bookingChannel: CreateReservationBookingChannel? = null,
    val token: String? = null,
    val ratePlanCode: String? = null,
    val specialRequests: List<String>? = null,
)

/** New occupancy for the edited room. A decrease in adults resets the reservation's meals. */
@Serializable
data class EditRoomOccupancy(
    val adultsNumber: Int,
    val childrenNumber: Int,
    val cotRequired: Boolean? = null,
)

/** New lead guest for the edited room; only title, first name and last name are always mapped. */
@Serializable
data class EditRoomLeadGuest(
    val title: String,
    val firstName: String,
    val lastName: String,
    val emailAddress: String? = null,
    val language: String? = null,
)

/** The amend basket reference every amend step answers with. */
@Serializable
data class TempBookingRefResponse(
    val tempBookingRef: String,
)
