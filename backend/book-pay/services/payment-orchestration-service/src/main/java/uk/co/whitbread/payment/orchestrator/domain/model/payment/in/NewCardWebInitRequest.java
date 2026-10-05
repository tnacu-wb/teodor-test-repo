package uk.co.whitbread.payment.orchestrator.domain.model.payment.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentMethod;

/**
 * Request to initialize a Secure Fields payment session for web clients.
 *
 * <p>Requires a {@code returnUrl} for 3-D Secure redirect after authentication.
 * The returnUrl is validated for HTTPS protocol and against a configurable host allowlist
 * via the class-level {@link ConditionalValidation} constraint.
 *
 * @param basketId      the basket/reservation identifier
 * @param returnUrl     the 3-D Secure redirect URL provided by the frontend (HTTPS only)
 * @param country       country code (e.g. "gb")
 * @param language      language code (e.g. "en")
 * @param userType      user type (e.g. "LEISURE")
 * @param clientChannel client channel identifier (e.g. "PI")
 */
@Schema(description = "Request to initialize a Secure Fields payment session for web clients")
public record NewCardWebInitRequest(
    @Schema(description = "The basket/reservation identifier",
        example = "AQN-147756bb-bb71-4842-959a-2efe87e378ed")
    @NotBlank(message = "basketId is required")
    String basketId,

    @Schema(description = "3-D Secure return URL for redirect after authentication (HTTPS only)",
        example = "https://www.premierinn.com/payments/3ds-return")
    @NotBlank(message = "returnUrl is required")
    String returnUrl,

    @Schema(description = "Country code", example = "gb")
    @NotBlank(message = "country is required")
    String country,

    @Schema(description = "Language code", example = "en")
    @NotBlank(message = "language is required")
    String language,

    @Schema(description = "User type", example = "LEISURE")
    @NotBlank(message = "userType is required")
    String userType,

    @Schema(description = "Client channel", example = "PI")
    @NotBlank(message = "clientChannel is required")
    String clientChannel
) implements PaymentInitRequest {

  @Override
  public PaymentMethod paymentMethod() {
    return PaymentMethod.NEW_CARD_WEB;
  }
}
