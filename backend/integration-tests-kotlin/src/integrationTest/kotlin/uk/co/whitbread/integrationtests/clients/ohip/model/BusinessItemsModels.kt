package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/** Request body for `PUT /ohip/v1/reservations/business`. */
@Serializable
data class BusinessItemsUpdateRequest(
    val reservationIds: List<String>,
    val hotelId: String,
    val companyId: String? = null,
    val businessItems: BusinessItems? = null,
    val channel: String? = null,
    val pibaCardPresent: Boolean? = null,
)

/** Business allowances applied to each reservation; presence selects the allowances branch. */
@Serializable
data class BusinessItems(
    val purchaseOrderNumber: String? = null,
    val customReferenceNumber: String? = null,
    val businessAllowances: List<BusinessAllowance>,
    val businessNotes: String,
)

/** One business allowance keyed by the rules-agent allowance id, e.g. mealDeal. */
@Serializable
data class BusinessAllowance(
    val budget: Double,
    val allowance: String,
    val isAuthorised: Boolean,
)
