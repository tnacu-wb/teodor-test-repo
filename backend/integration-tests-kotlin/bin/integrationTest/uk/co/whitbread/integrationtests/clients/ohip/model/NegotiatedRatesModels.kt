package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

@Serializable
data class NegotiatedRatesResponse(
    val negotiatedRates: List<NegotiatedRate> = emptyList(),
)

@Serializable
data class NegotiatedRate(
    val hotelId: String? = null,
    val ratePlanCode: String? = null,
)
