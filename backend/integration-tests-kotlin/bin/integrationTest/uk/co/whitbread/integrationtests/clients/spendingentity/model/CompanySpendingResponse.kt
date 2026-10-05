package uk.co.whitbread.integrationtests.clients.spendingentity.model

import kotlinx.serialization.Serializable

@Serializable
data class CompanySpendingResponse(
    val companySpendingDtoList: List<CompanySpending> = emptyList(),
)

@Serializable
data class CompanySpending(
    val companyAccountId: String? = null,
    val year: Int? = null,
    val month: Int? = null,
    val noOfBookings: Int? = null,
    val bookingValue: Double? = null,
    val bookingCurrency: String? = null,
)
