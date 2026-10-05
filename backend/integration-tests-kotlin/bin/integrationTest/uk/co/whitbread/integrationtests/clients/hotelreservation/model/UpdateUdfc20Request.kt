package uk.co.whitbread.integrationtests.clients.hotelreservation.model

import kotlinx.serialization.Serializable

/** Request body for `PUT /v1/reservations/updateUdfc20`. */
@Serializable
data class UpdateUdfc20Request(
    val reservationIds: Set<String>,
    val hotelId: String,
    val ciolStatus: CiolStatus,
)

/** CIOL lifecycle values accepted by Hotel Reservation Entity. */
@Serializable
enum class CiolStatus {
    CIOL_STARTED,
    WALLET_PASS,
    DK_ISSUED,
    CIOL_COMPLETED,
}
