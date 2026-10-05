package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

@Serializable
data class CancelInformationResponse(
    val isCancellable: Boolean? = null,
)
