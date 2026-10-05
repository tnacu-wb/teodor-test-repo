package uk.co.whitbread.integrationtests.clients.basket.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Mirrors basket-service's `BasketDto`.
 *
 * Two identifiers matter and are easy to confuse: [reference] is the basket id that reservation
 * creation returns and the payment APIs take, while [bookingReference] is the shorter reference
 * other services hold as this booking's external reference. Only [bookingReference] resolves a
 * basket lookup by booking reference.
 *
 * Deliberate deviations from the Java DTO, both to keep this read model shallow rather than to
 * hide fields: [status], [paymentStatus] and [promoKind] are the DTO's enums kept as strings, so a
 * value the service adds cannot fail decoding, and [ccuiExtraItems] keeps the CCUI-only graph as
 * raw JSON instead of restating three nested types no journey reads yet.
 */
@Serializable
data class BasketResponse(
    val hotelId: String? = null,
    val bookingReference: String? = null,
    val reference: String? = null,
    val createdAt: String? = null,
    val lastModifiedAt: String? = null,
    val userId: String? = null,
    val originalBasketId: String? = null,
    val linkAmendReservations: Map<String, String>? = null,
    val status: String? = null,
    val paymentStatus: String? = null,
    val sendMail: Boolean? = null,
    val paymentID: String? = null,
    val paymentOption: String? = null,
    val channel: String? = null,
    val subChannel: String? = null,
    val lockingTime: String? = null,
    val itemTypes: Set<String>? = null,
    val items: List<BasketItem>? = null,
    val ccuiExtraItems: JsonElement? = null,
    val bookingAllowances: List<BasketBookingAllowance>? = null,
    val isErroredBooking: Boolean? = null,
    val basketError: BasketError? = null,
    val isCheckInOnlinePay: Boolean? = null,
    val isSecureBooking: Boolean? = null,
    val idContext: String? = null,
    val promotionCode: String? = null,
    val promoKind: String? = null,
)

/** Mirrors `BasketItemDto`. `sourceId` carries the reservation id for a room item. */
@Serializable
data class BasketItem(
    val type: String? = null,
    val sourceId: String? = null,
    val details: Map<String, String>? = null,
    val hasOccupancySup: Boolean? = null,
)

/** Mirrors `BookingAllowanceDto`. */
@Serializable
data class BasketBookingAllowance(
    val allowance: String? = null,
    val budget: Double? = null,
)

/** Mirrors `BasketError`. */
@Serializable
data class BasketError(
    val code: String? = null,
    val description: String? = null,
    val type: String? = null,
)
