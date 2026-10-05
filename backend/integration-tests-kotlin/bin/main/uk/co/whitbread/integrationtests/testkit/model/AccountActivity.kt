package uk.co.whitbread.integrationtests.testkit.model

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
@JsonSchema.Description(
    "Activity on the tethered account. Each part gates its own stub: worldlineAccount gates " +
        "Worldline account-info, non-empty transactionAggregates gate CDH transaction-details, " +
        "and paymentHistory gates Worldline payment-info.",
)
data class AccountActivity(
    val worldlineAccount: WorldlineAccount? = null,
    val transactionAggregates: List<TransactionAggregate> = emptyList(),
    val paymentHistory: PaymentHistory? = null,
)

@Serializable
@JsonSchema.Description("Worldline account state the mocked account-info endpoint returns.")
data class WorldlineAccount(
    val billingFrequency: BillingFrequency,
    val status: String,
    val currency: String,
) {
    init {
        require(status.isNotBlank()) { "worldlineAccount.status must not be blank" }
        require(currency.isNotBlank()) { "worldlineAccount.currency must not be blank" }
    }
}

@Serializable
@JsonSchema.Description("Billing frequencies with their Worldline wire values.")
enum class BillingFrequency(
    val worldlineValue: String,
) {
    WEEKLY("Weekly"),
    MONTHLY("Monthly"),
    FORTNIGHT("Fortnight"),
}

@Serializable
@JsonSchema.Description("Total booking value on the account over a date range.")
data class TransactionAggregate(
    @Serializable(with = IsoLocalDateSerializer::class)
    val fromDate: LocalDate,
    @Serializable(with = IsoLocalDateSerializer::class)
    val toDate: LocalDate,
    val totalBookingValue: Double,
) {
    init {
        require(!toDate.isBefore(fromDate)) { "transactionAggregate.toDate must be on or after fromDate" }
        require(totalBookingValue >= 0) { "transactionAggregate.totalBookingValue must not be negative" }
    }
}

@Serializable
@JsonSchema.Description("Payments the mocked Worldline payment-info endpoint returns.")
data class PaymentHistory(
    val payments: List<PaymentInfoItem>,
)

@Serializable
@JsonSchema.Description("One payment row in the account's payment history.")
data class PaymentInfoItem(
    val paymentDate: String,
    val paymentDescription: String,
    @JsonSchema.Description("Why the payment failed; N/A for successful payments.")
    val failureReason: String = "N/A",
    val value: String,
    @JsonSchema.Description("Numeric ISO 4217 currency code; 826 is GBP.")
    val currencyCode: String = "826",
) {
    init {
        require(paymentDate.isNotBlank()) { "paymentInfoItem.paymentDate must not be blank" }
        require(paymentDescription.isNotBlank()) { "paymentInfoItem.paymentDescription must not be blank" }
        require(failureReason.isNotBlank()) { "paymentInfoItem.failureReason must not be blank" }
        require(value.isNotBlank()) { "paymentInfoItem.value must not be blank" }
        require(currencyCode.isNotBlank()) { "paymentInfoItem.currencyCode must not be blank" }
    }
}
