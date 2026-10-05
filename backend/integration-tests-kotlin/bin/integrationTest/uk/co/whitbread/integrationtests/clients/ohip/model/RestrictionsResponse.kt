package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/** Response of `GET /ohip/hotels/{hotelId}/restrictions`; the multi-hotel variant returns a list of these. */
@Serializable
data class RestrictionsByDateRangeResponse(
    val restrictionSets: List<RestrictionSet>? = null,
    val hotelId: String? = null,
    val hasMore: Boolean = false,
)

@Serializable
data class RestrictionSet(
    val restrictionControl: RestrictionControl? = null,
    val restrictionStatus: RestrictionStatus? = null,
    val onRequest: Boolean = false,
    val start: String? = null,
    val end: String? = null,
)

@Serializable
data class RestrictionControl(
    val house: Boolean = false,
    val roomType: String? = null,
    val roomClass: String? = null,
    val ratePlanCode: String? = null,
    val ratePlanCategory: String? = null,
)

@Serializable
data class RestrictionStatus(
    val code: String? = null,
    val unit: Int = 0,
)
