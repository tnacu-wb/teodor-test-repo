package uk.co.whitbread.integrationtests.clients.hotelreservation.model

import kotlinx.serialization.Serializable

/**
 * hotel-reservation-entity-service's cancellation-policy response: the absolute cancellation
 * deadline in the hotel's time zone and the penalty text that goes with it. Both are null when
 * the hotel holds no policy schedule for the requested rate plan.
 */
@Serializable
data class CancellationPoliciesResponse(
    val time: String? = null,
    val text: String? = null,
)
