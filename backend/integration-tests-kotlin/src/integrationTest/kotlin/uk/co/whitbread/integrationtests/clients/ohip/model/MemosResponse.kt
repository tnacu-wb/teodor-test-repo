package uk.co.whitbread.integrationtests.clients.ohip.model

import kotlinx.serialization.Serializable

/**
 * Response of `GET /ohip/v1/reservations/memos`: the Opera reservation comments held against the
 * requested reservations, folded by description and memo type and sorted newest-modified first.
 */
@Serializable
data class MemosResponse(
    val memos: List<Memo> = emptyList(),
)

/** One memo: its description, its classification, and the comment ids it was folded from. */
@Serializable
data class Memo(
    val ids: List<MemoIds> = emptyList(),
    val description: String? = null,
    val createdOn: String? = null,
    val createdBy: String? = null,
    val modifiedOn: String? = null,
    val modifiedBy: String? = null,
    val memoType: String? = null,
)

/** The Opera comment ids one reservation contributed to a memo. */
@Serializable
data class MemoIds(
    val reservationId: String? = null,
    val memoIds: List<String> = emptyList(),
)
