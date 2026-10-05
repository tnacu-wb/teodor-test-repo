package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/**
 * Request for `POST /ohip/v1/reservations/guests`.
 *
 * [preCheckIn] selects the orchestration: the service tests `Boolean.FALSE.equals(preCheckIn)`,
 * so the default `false` (always serialized) takes the booker/company/per-guest path and `true`
 * takes the pre-check-in path. The accepted scenarios never need the explicit-JSON-null variant,
 * so the field is non-nullable.
 */
@Serializable
data class CreateReservationGuestRequest(
    val hotelId: String,
    val reasonForStay: String,
    val booker: ReservationGuestBooker,
    val stayingGuests: List<ReservationStayingGuest>,
    val preCheckIn: Boolean = false,
)

@Serializable
data class ReservationGuestBooker(
    val firstName: String,
    val lastName: String,
    val emailAddress: String? = null,
    val language: String? = null,
    val address: ReservationGuestBookerAddress? = null,
)

@Serializable
data class ReservationGuestBookerAddress(
    val addressLine1: String? = null,
    val postalCode: String? = null,
    val countryCode: String? = null,
    val cityName: String? = null,
    val companyName: String? = null,
)

@Serializable
data class ReservationStayingGuest(
    val reservationId: String,
    val sameAsBooker: Boolean? = null,
    val stayingGuestDetails: ReservationStayingGuestDetails? = null,
    val accompanyingGuestDetails: ReservationAccompanyingGuestDetails? = null,
    val isAccompanyingGuest: Boolean? = null,
)

@Serializable
data class ReservationStayingGuestDetails(
    val firstName: String,
    val lastName: String,
    val emailAddress: String? = null,
    val profileId: String? = null,
)

@Serializable
data class ReservationAccompanyingGuestDetails(
    val firstName: String? = null,
    val lastName: String? = null,
)

@Serializable
data class CreateReservationGuestResponse(
    val hotelId: String? = null,
    val reservationIds: List<String>? = null,
)
