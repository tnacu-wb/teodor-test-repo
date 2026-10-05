package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/** Request body for `POST /ohip/v1/reservations/memos` (`CreateMemoRequestDto`). */
@Serializable
data class CreateMemoRequest(
    val hotelId: String,
    val reservationIds: List<String>,
    val description: String,
)
