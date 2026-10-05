package uk.co.whitbread.payment.orchestrator.domain.model.payment.out;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Response returned after successful payment initialization.
 *
 * @param paymentMethod the Datatrans integration type used for this payment
 * @param transactionId the Datatrans transaction identifier
 */
@Schema(description = "Response after successful payment initialization")
@JsonPropertyOrder({"paymentMethod", "transactionId"})
public record PaymentInitResponse(

    @Schema(description = "The Datatrans integration type", example = "NEW_CARD_WEB")
    PaymentMethod paymentMethod,

    @Schema(description = "The Datatrans transaction identifier",
        example = "190410112056083383")
    String transactionId
) {}
