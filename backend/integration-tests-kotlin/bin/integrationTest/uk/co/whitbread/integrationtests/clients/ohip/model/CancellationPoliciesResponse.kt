package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

@Serializable
data class CancellationPoliciesResponse(
    val time: String? = null,
    val text: String? = null,
)
