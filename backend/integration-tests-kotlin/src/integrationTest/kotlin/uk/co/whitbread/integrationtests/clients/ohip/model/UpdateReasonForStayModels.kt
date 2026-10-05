package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/**
 * Request body for `PUT /ohip/v1/reservations/reasonForStay` (`UpdateReasonForStayRequestDto`).
 * The ids bind to a `List<String>` server-side, so duplicate entries survive to the fan-out and
 * each entry drives its own Opera reservation update carrying the one reason for stay.
 */
@Serializable
data class UpdateReasonForStayRequest(
    val hotelId: String,
    val reservationIds: List<String>,
    val reasonForStay: String,
)

/**
 * Success body of `PUT /ohip/v1/reservations/reasonForStay`
 * (`UpdateReasonForStayResponseDto`). Both fields are derived from the Opera update responses,
 * not echoed from the request, and the ids are filtered to Opera identifier type `Reservation`.
 */
@Serializable
data class UpdateReasonForStayResponse(
    val hotelId: String? = null,
    val reservationIds: List<String> = emptyList(),
)
