package uk.co.whitbread.integrationtests.testkit.model

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

@Serializable
@JsonSchema.Description(
    "The authenticated Business Booker user for this scenario. Nested data gates account " +
        "stubs: companySpend and employeeSpend gate their CDH spend stubs; tetheredAccount " +
        "gates the CDH registration and Worldline account stubs.",
)
data class LoggedUser(
    @JsonSchema.Description("Business Booker access level, e.g. SUPER.")
    val accessLevel: String,
    val companyAccountId: String,
    val companyId: String,
    val employeeId: String,
    val email: String,
    @JsonSchema.Description("Company-level spend data. Presence gates the CDH company-spend stub.")
    val companySpend: CompanySpend? = null,
    @JsonSchema.Description("Employee-level spend data. Presence gates the CDH employee-spend stub.")
    val employeeSpend: EmployeeSpend? = null,
    @JsonSchema.Description(
        "Payment account tethered to the user. Presence gates the CDH registration-details " +
            "and Worldline tethered-user stubs.",
    )
    val tetheredAccount: TetheredAccount? = null,
) {
    init {
        require(accessLevel.isNotBlank()) { "loggedUser.accessLevel must not be blank" }
        require(companyAccountId.isNotBlank()) { "loggedUser.companyAccountId must not be blank" }
        require(companyId.isNotBlank()) { "loggedUser.companyId must not be blank" }
        require(employeeId.isNotBlank()) { "loggedUser.employeeId must not be blank" }
        require(email.isNotBlank()) { "loggedUser.email must not be blank" }
    }
}

@Serializable
@JsonSchema.Description("The Worldline/PIBA payment account tethered to the logged-in user.")
data class TetheredAccount(
    val pibaAccountId: String,
    val tetheredUserGuid: String,
    val scheme: String = "GB",
    @JsonSchema.Description("Role the user holds on the account; maps to the Worldline role vocabulary.")
    val userRole: TetheredUserRole = TetheredUserRole.ACCOUNT_HOLDER,
    val countMyCards: Int = 1,
    @JsonSchema.Description("Account spend data. Presence gates the CDH account-spend stub.")
    val accountSpend: AccountSpend? = null,
    @JsonSchema.Description(
        "Account activity. Its transaction aggregates, Worldline account, and payment history " +
            "each gate their CDH or Worldline stub.",
    )
    val accountActivity: AccountActivity? = null,
) {
    init {
        require(pibaAccountId.isNotBlank()) { "tetheredAccount.pibaAccountId must not be blank" }
        require(tetheredUserGuid.isNotBlank()) { "tetheredAccount.tetheredUserGuid must not be blank" }
        require(scheme.isNotBlank()) { "tetheredAccount.scheme must not be blank" }
        require(countMyCards >= 0) { "tetheredAccount.countMyCards must not be negative" }
    }
}

@Serializable
@JsonSchema.Description("Roles a user can hold on a tethered account, with their Worldline wire values.")
enum class TetheredUserRole(
    val worldlineValue: String,
) {
    ACCOUNT_HOLDER("AccountHolder"),
    ACCOUNT_CARD_HOLDER("AccountCardHolder"),
    FINANCE_USER("ReportsAndInvoices"),
    FINANCE_USER_CARD_HOLDER("AccountCardHolderWithReportsAndInvoices"),
    COST_CENTRE_USER("CostCentreHolder"),
}
