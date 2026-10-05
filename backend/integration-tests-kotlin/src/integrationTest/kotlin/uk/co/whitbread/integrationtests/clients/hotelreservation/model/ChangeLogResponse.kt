package uk.co.whitbread.integrationtests.clients.hotelreservation.model

import kotlinx.serialization.Serializable

/**
 * hotel-reservation-entity-service's reshaped change-log response. The controller mapper drops
 * the Opera-side `logDate`/`logUserName` and exposes `date`, `time` and `user` instead, so this
 * is deliberately not the ohip-adapter `ChangeLogResponse`.
 */
@Serializable
data class ChangeLogResponse(
    val activityLog: ChangeLogList? = null,
)

@Serializable
data class ChangeLogList(
    val activityLog: List<ChangeLogRow>? = null,
    val totalPages: Int? = null,
    val offset: Int? = null,
    val limit: Int? = null,
    val hasMore: Boolean = false,
    val totalResults: Int? = null,
    val count: Int? = null,
)

@Serializable
data class ChangeLogRow(
    val date: String? = null,
    val time: String? = null,
    val actionType: String? = null,
    val actionDescription: String? = null,
    val user: String? = null,
)
