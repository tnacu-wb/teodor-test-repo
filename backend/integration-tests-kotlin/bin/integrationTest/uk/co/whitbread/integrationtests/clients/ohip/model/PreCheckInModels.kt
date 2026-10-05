package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/**
 * Body shared by `POST /ohip/v1/reservations/pre-checkin` and
 * `POST /ohip/v1/reservations/pre-register`.
 */
@Serializable
data class PreCheckInRequest(
    val hotelId: String,
    val reservationId: String,
    /** ISO-8601 date, e.g. 2026-09-10. */
    val arrivalTime: String,
    /** EN or DE; selects the pre-check-in alert language. */
    val language: String? = null,
)

/**
 * Response of the pre-checkin, pre-register, and attachment-upload endpoints:
 * `status` is `Success` or `Error` with a matching message.
 */
@Serializable
data class PreCheckInResponse(
    val status: String? = null,
    val message: String? = null,
)
