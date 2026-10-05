package uk.co.whitbread.integrationtests.clients.hotelreservation.model

import kotlinx.serialization.Serializable

/**
 * hotel-reservation-entity-service's basket-scoped reservation read: every reservation the basket
 * holds, plus the basket-level context the service stamps onto the response after ohip-adapter
 * has enriched each reservation from Opera.
 *
 * Only the fields journeys assert are modelled; the real DTO carries the full reservation graph
 * (rate info, folios, packages, profiles) that journeys deliberately leave out of stable
 * response assertions.
 */
@Serializable
data class ReservationsByBasketResponse(
    val reservationByIdList: List<BasketReservation> = emptyList(),
    val hotelId: String? = null,
    val channel: String? = null,
    val bookingReference: String? = null,
    val basketReference: String? = null,
    val basketStatus: String? = null,
    val idContext: String? = null,
    val policyCode: String? = null,
    val paymentOption: String? = null,
    val hasCityTax: Boolean? = null,
    val upsellsAddonsEnabled: Boolean? = null,
)

/**
 * One reservation inside the basket read. `deRegCardCompleted` is the field
 * `mobile_preRegistered_repurpose` gates: ohip-adapter derives it from the Opera reservation's
 * alerts and hotel-reservation-entity-service forces it to `false` when the flag is off.
 */
@Serializable
data class BasketReservation(
    val reservationId: String? = null,
    val reservationStatus: String? = null,
    val deRegCardCompleted: Boolean? = null,
)
