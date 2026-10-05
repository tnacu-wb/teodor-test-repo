package uk.co.whitbread.integrationtests.clients.basket.model

import kotlinx.serialization.Serializable

@Serializable
data class ChangeBasketStatusRequest(
    val status: String,
)
