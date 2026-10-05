package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

@Serializable
data class RatePlanInfoResponse(
    val ratePlanInfo: List<RatePlanInfo> = emptyList(),
)

@Serializable
data class RatePlanInfo(
    val hotelId: String? = null,
    val ratePlanCode: String? = null,
    val ratePlanBasedOnRates: List<RatePlanBasedOnRate> = emptyList(),
)

@Serializable
data class RatePlanBasedOnRate(
    val dynamicBaseRate: DynamicBaseRate? = null,
)

@Serializable
data class DynamicBaseRate(
    val dynamicBasedOnRatePlan: String? = null,
)
