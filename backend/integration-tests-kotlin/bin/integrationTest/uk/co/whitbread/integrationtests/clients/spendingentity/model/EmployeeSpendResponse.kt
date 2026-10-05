package uk.co.whitbread.integrationtests.clients.spendingentity.model

import kotlinx.serialization.Serializable

@Serializable
data class EmployeeSpendResponse(
    val companyAccountId: String? = null,
    val employeeAccountId: String? = null,
    val year: Int? = null,
    val month: Int? = null,
    val noOfBookings: Int? = null,
    val bookingValue: Double? = null,
    val bookingCurrency: String? = null,
)
