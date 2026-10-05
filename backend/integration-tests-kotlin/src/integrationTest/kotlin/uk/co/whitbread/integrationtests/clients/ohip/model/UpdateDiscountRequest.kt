package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/**
 * Request body for `PUT /ohip/v1/reservations/discount` (`UpdateDiscountRequestDto`). The ids
 * bind to a `Set<String>` server-side, so duplicates collapse before any Opera call.
 */
@Serializable
data class UpdateDiscountRequest(
    val reservationIds: List<String>,
    val hotelId: String,
    val currency: String,
    val discountAmount: Double,
)
