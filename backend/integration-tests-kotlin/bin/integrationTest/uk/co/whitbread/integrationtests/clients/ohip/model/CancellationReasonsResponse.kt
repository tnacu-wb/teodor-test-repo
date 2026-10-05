package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

@Serializable
data class CancellationReasonsResponse(
    val cancellationReasons: List<CancellationReason>? = null,
)

@Serializable
data class CancellationReason(
    val code: String? = null,
    val name: String? = null,
    val description: String? = null,
    val active: Boolean = false,
    val managerApprovalNeeded: Boolean = false,
)
