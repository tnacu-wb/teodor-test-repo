package uk.co.whitbread.payment.orchestrator.domain.model.payment.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import uk.co.whitbread.payment.orchestrator.domain.model.payment.out.PaymentMethod;

/**
 * Request to initialize a Mobile SDK payment session for native apps.
 *
 * <p>Does not require a {@code returnUrl} — the Datatrans Mobile SDK handles
 * 3-D Secure natively within the app.
 *
 * @param basketId      the basket/reservation identifier
 * @param country       country code (e.g. "gb")
 * @param language      language code (e.g. "en")
 * @param userType      user type (e.g. "LEISURE")
 * @param clientChannel client channel identifier (e.g. "APPS_IOS")
 */
@Schema(description = "Request to initialize a Mobile SDK payment session for native apps")
public record NewCardMobileInitRequest(
    @Schema(description = "The basket/reservation identifier",
        example = "AQN-147756bb-bb71-4842-959a-2efe87e378ed")
    @NotBlank(message = "basketId is required")
    String basketId,

    @Schema(description = "Country code", example = "gb")
    @NotBlank(message = "country is required")
    String country,

    @Schema(description = "Language code", example = "en")
    @NotBlank(message = "language is required")
    String language,

    @Schema(description = "User type", example = "LEISURE")
    @NotBlank(message = "userType is required")
    String userType,

    @Schema(description = "Client channel", example = "APPS_IOS")
    @NotBlank(message = "clientChannel is required")
    String clientChannel
) implements PaymentInitRequest {

  @Override
  public PaymentMethod paymentMethod() {
    return PaymentMethod.NEW_CARD_MOBILE;
  }
}
