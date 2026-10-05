package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/**
 * The basket reservation lookup response from `GET /ohip/v1/reservations/basket`.
 *
 * Models only the fields journeys assert; the decoder ignores the rest of the payload.
 */
@Serializable
data class ReservationByBasketRefResponse(
    val reservationByIdList: List<BasketReservationById> = emptyList(),
    val policyCode: String? = null,
    val hotelId: String? = null,
    val currencyCode: String? = null,
    val totalCost: Double? = null,
    val amountPaid: Double? = null,
    val balanceOutstanding: Double? = null,
)

@Serializable
data class BasketReservationById(
    val reservationId: String? = null,
    val reservationStatus: String? = null,
)
