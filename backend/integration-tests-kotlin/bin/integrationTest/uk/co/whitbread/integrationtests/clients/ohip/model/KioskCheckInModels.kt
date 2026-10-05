package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/** Request body for `POST /ohip/v1/kiosk/checkIn`. */
@Serializable
data class KioskCheckInRequest(
    val hotelId: String,
    val reservationNumber: String,
    val roomId: String,
)

/** Response of `POST /ohip/v1/kiosk/checkIn`: Opera's checked-in reservation snapshot. */
@Serializable
data class KioskCheckInResponse(
    val reservation: List<KioskCheckInReservation> = emptyList(),
)

/** The checked-in reservation fields the journeys assert on. */
@Serializable
data class KioskCheckInReservation(
    val hotelId: String? = null,
    val reservationStatus: String? = null,
    val roomStay: KioskCheckInRoomStay? = null,
)

/** Room-stay slice of the check-in snapshot carrying the current room. */
@Serializable
data class KioskCheckInRoomStay(
    val currentRoomInfo: KioskCurrentRoomInfo? = null,
    val arrivalDate: String? = null,
    val departureDate: String? = null,
)

/** The room the guest is checked into. */
@Serializable
data class KioskCurrentRoomInfo(
    val roomType: String? = null,
    val roomId: String? = null,
)
