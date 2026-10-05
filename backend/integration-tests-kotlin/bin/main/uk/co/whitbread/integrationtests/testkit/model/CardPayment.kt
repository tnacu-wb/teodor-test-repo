package uk.co.whitbread.integrationtests.testkit.model

import io.ktor.openapi.JsonSchema
import kotlinx.serialization.Serializable

@Serializable
@JsonSchema.Description(
    "The card payment the guest is about to make against this booking. Present only for " +
        "scenarios that take a payment: its presence selects the payment gateway's mocks. The " +
        "amount is not modelled here — it is the stay total the reservation already carries.",
)
data class CardPayment(
    @JsonSchema.Description("Datatrans transaction ID the mocked gateway returns.")
    val transactionId: String,
) {
    init {
        require(transactionId.isNotBlank()) { "cardPayment.transactionId must not be blank" }
    }
}
