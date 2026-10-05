package uk.co.whitbread.integrationtests.clients.hotelreservation.model

import kotlinx.serialization.Serializable

@Serializable
data class FindBookingResponse(
    val sourcePms: String? = null,
    val cookieName: String? = null,
    val ref: String? = null,
    val basketReference: String? = null,
    val token: String? = null,
    val redirectBase: String? = null,
    val minutesTillExpiry: String? = null,
    val operaConfNumber: String? = null,
    val hotelId: String? = null,
    val idContext: String? = null,
)
