package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/** One entry of the bare JSON array returned by `GET /ohip/promotions`. */
@Serializable
data class PromotionResponse(
    val promotionName: String? = null,
    val bookingStartDate: String? = null,
    val bookingEndDate: String? = null,
    val stayStartDate: String? = null,
    val stayEndDate: String? = null,
    val promotionCode: String? = null,
)
