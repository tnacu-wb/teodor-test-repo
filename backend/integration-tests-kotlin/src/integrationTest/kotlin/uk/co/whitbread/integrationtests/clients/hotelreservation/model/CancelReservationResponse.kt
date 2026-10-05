package uk.co.whitbread.integrationtests.clients.hotelreservation.model

import kotlinx.serialization.Serializable

/**
 * Response of `POST /v1/reservations/cancellations` and
 * `POST /v1/reservations/cancellations/rollback`; the DTO carries the basket reference only.
 *
 * `basketReference` is nullable: both endpoints answer `200` with a null reference when
 * ohip-adapter returned no cancellation ids.
 */
@Serializable
data class CancelReservationResponse(
    val basketReference: String? = null,
)
