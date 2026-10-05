package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

@Serializable
data class ChangeLogResponse(
    val activityLog: ChangeLogList? = null,
)

@Serializable
data class ChangeLogList(
    val activityLog: List<ChangeLogRow>? = null,
    val totalResults: Int? = null,
    val count: Int? = null,
    val hasMore: Boolean = false,
)

@Serializable
data class ChangeLogRow(
    val logDate: String? = null,
    val logUserName: String? = null,
    val actionType: String? = null,
    val actionDescription: String? = null,
)
