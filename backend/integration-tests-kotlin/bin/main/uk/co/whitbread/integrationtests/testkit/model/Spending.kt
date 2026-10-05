package uk.co.whitbread.integrationtests.testkit.model

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

@Serializable
@JsonSchema.Description("Monthly spend the mocked CDH account-spend endpoint reports for the tethered account.")
data class AccountSpend(
    val period: ReportingPeriod,
    val items: List<AccountSpendItem>,
)

@Serializable
@JsonSchema.Description("One month's booking count and value on the account.")
data class AccountSpendItem(
    val year: Int,
    val month: Int,
    val noOfBookings: Int,
    val bookingValue: Double,
)

@Serializable
@JsonSchema.Description("Monthly spend the mocked CDH company-spend endpoint reports for the user's company.")
data class CompanySpend(
    val period: ReportingPeriod,
    val items: List<CompanySpendItem>,
)

@Serializable
@JsonSchema.Description("One month's booking count and value for the company.")
data class CompanySpendItem(
    val year: Int,
    val month: Int,
    val noOfBookings: Int,
    val bookingValue: Double,
    val bookingCurrency: String,
) {
    init {
        require(bookingCurrency.isNotBlank()) { "companySpendItem.bookingCurrency must not be blank" }
    }
}

@Serializable
@JsonSchema.Description("Monthly spend the mocked CDH employee-spend endpoint reports for the user.")
data class EmployeeSpend(
    val period: ReportingPeriod,
    val items: List<EmployeeSpendItem>,
)

@Serializable
@JsonSchema.Description("Month range a spend report covers.")
data class ReportingPeriod(
    val fromMonthYear: String,
    val toMonthYear: String,
) {
    init {
        require(fromMonthYear.isNotBlank()) { "reportingPeriod.fromMonthYear must not be blank" }
        require(toMonthYear.isNotBlank()) { "reportingPeriod.toMonthYear must not be blank" }
    }
}

@Serializable
@JsonSchema.Description("One month's booking count and value for the employee.")
data class EmployeeSpendItem(
    val year: Int,
    val month: Int,
    val noOfBookings: Int,
    val bookingValue: Double,
    val bookingCurrency: String,
) {
    init {
        require(bookingCurrency.isNotBlank()) { "employeeSpendItem.bookingCurrency must not be blank" }
    }
}
