package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

@Serializable
data class RatePlansResponse(
    val ratePlans: List<RatePlan> = emptyList(),
)

@Serializable
data class RatePlan(
    val ratePlanCode: String? = null,
    val hotelId: String? = null,
    val primaryDetails: RatePlanPrimaryDetails? = null,
    val classifications: RatePlanClassifications? = null,
)

@Serializable
data class RatePlanPrimaryDetails(
    val description: RatePlanDescription? = null,
)

@Serializable
data class RatePlanDescription(
    val defaultText: String? = null,
)

@Serializable
data class RatePlanClassifications(
    val rateCategory: String? = null,
    val displaySet: String? = null,
    val marketCode: String? = null,
)
