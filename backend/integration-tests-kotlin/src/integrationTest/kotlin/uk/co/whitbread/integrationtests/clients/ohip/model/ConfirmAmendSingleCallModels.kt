package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/** Request body for `PUT /ohip/v1/reservations/confirmAmendSingleCall`. */
@Serializable
data class ConfirmAmendSingleCallRequest(
    val stayDateUpdateRequest: SingleCallUpdateReservations? = null,
    val bookerDetailsCnpRequest: BookerDetailsCnpRequest? = null,
)

/** One change group of per-reservation updates plus the current reservation state. */
@Serializable
data class SingleCallUpdateReservations(
    val bookingChannel: UpdateBookingChannel,
    val reservations: List<SingleCallReservationUpdate>,
    val tempReservations: SingleCallTempReservations,
    val linkAmendReservations: Map<String, String> = emptyMap(),
    val companyId: String? = null,
)

/** One reservation's requested change; no `roomStay.roomRates` selects amend-stay-dates. */
@Serializable
data class SingleCallReservationUpdate(
    val hotelId: String,
    val reservationId: String,
    val roomStay: UpdateRoomStay,
)

/** The current (original) reservations the change is built from. */
@Serializable
data class SingleCallTempReservations(
    val reservationByIdList: List<SingleCallTempReservation>,
)

/** Current state of one reservation. */
@Serializable
data class SingleCallTempReservation(
    val reservationId: String,
    val roomStay: SingleCallTempRoomStay,
)

/** Room-stay facts the single-call amend mapping reads. */
@Serializable
data class SingleCallTempRoomStay(
    val adultsNumber: Int,
    val childrenNumber: Int = 0,
    val roomType: String,
    val ratePlanCode: String,
    val sourceCode: String,
    val arrivalDate: String,
    val departureDate: String,
)

/** Booker contact details applied to the booker CRM profile. */
@Serializable
data class BookerDetailsCnpRequest(
    val hotelId: String,
    val reservationIds: List<String>,
    val title: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val emailAddress: String? = null,
    val phoneNumber: String? = null,
    val companyName: String? = null,
)
