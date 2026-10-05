package uk.co.whitbread.payment.orchestrator.domain.model.payment.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Request to finalize a payment authorization (post-3DS, web only).
 *
 * @param basketId the basket/reservation identifier
 */
@Schema(description = "Request to finalize payment authorization after 3-D Secure")
public record AuthorizePaymentRequest(
    @Schema(description = "The basket/reservation identifier",
        example = "AQN-147756bb-bb71-4842-959a-2efe87e378ed")
    @NotBlank(message = "basketId is required")
    @Pattern(regexp = "^[A-Z]{3}-[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$",
        message = "basketId must match format XXX-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx")
    String basketId
) {}
