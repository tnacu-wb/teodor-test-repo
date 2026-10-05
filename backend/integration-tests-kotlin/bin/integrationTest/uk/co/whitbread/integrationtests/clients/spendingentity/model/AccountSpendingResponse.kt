package uk.co.whitbread.integrationtests.clients.spendingentity.model

import kotlinx.serialization.Serializable

@Serializable
data class AccountSpendingResponse(
    val accountSpendingDtoList: List<AccountSpending> = emptyList(),
)

@Serializable
data class AccountSpending(
    val pibaAccountId: String? = null,
    val year: Int? = null,
    val month: Int? = null,
    val noOfBookings: Int? = null,
    val bookingValue: Double? = null,
)
