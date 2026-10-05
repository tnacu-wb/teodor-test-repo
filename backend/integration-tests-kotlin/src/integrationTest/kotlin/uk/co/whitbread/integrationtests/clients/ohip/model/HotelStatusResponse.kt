package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/** One entry of the bare JSON array returned by `GET /ohip/hotels/status`. */
@Serializable
data class HotelStatusResponse(
    val hotelId: String? = null,
    val onSale: Boolean = false,
    val pmsSource: String? = null,
)
