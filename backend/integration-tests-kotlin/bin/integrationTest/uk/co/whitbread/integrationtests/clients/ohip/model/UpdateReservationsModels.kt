package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/** Request body for `PUT /ohip/v1/reservations` (multi-part reservation update). */
@Serializable
data class UpdateReservationsRequest(
    val bookingChannel: UpdateBookingChannel? = null,
    val companyId: String? = null,
    val updateReservationsRequest: List<UpdateReservationEntry>,
    val tempReservations: TempReservations,
    val linkAmendReservations: Map<String, String> = emptyMap(),
    val distributionIATANumber: String? = null,
)

/** Booking channel identity stamped on the change reservation. */
@Serializable
data class UpdateBookingChannel(
    val channel: String,
    val subchannel: String? = null,
    val language: String? = null,
)

/** One per-reservation update; no `roomStay.roomRates` selects the amend-stay-dates mode. */
@Serializable
data class UpdateReservationEntry(
    val hotelId: String,
    val reservationId: String,
    val roomStay: UpdateRoomStay,
)

/** The new stay the update applies. */
@Serializable
data class UpdateRoomStay(
    val arrivalDate: String,
    val departureDate: String,
)

/** Caller-supplied current reservation state, shaped like the basket lookup response. */
@Serializable
data class TempReservations(
    val reservationByIdList: List<TempReservationById>,
)

/** Current state of one reservation, keyed by its Opera reservation id. */
@Serializable
data class TempReservationById(
    val reservationId: String,
    val roomStay: TempRoomStay,
)

/** The room-stay facts the update mapping reads from the temp reservation. */
@Serializable
data class TempRoomStay(
    val adults: Int,
    val children: Int = 0,
    val roomType: String,
    val ratePlanCode: String,
    val sourceCode: String,
    val arrivalDate: String,
    val departureDate: String,
)
