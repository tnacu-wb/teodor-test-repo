package uk.co.whitbread.payment.orchestrator.domain.model.payment.in;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Datatrans webhook payload received after a Mobile SDK payment event.
 *
 * <p>Every record here is annotated {@link JsonIgnoreProperties} with
 * {@code ignoreUnknown = true} so additional Datatrans fields — at the top level or nested — are
 * tolerated without a change here. The annotation is deliberate rather than relying on the
 * ambient mapper: forward-compatible deserialization is a property of this payload, not of the
 * mapper wiring, so it must hold even under a mapper with
 * {@code FAIL_ON_UNKNOWN_PROPERTIES} enabled.
 *
 * <p>The annotation package is {@code com.fasterxml.jackson.annotation} for both Jackson
 * generations — Jackson 3 ({@code tools.jackson}) still depends on the 2.x annotations
 * artifact — so these annotations apply to the Jackson 3 mapper Spring Boot 4 auto-configures
 * and reads this payload with. Card details are never logged.
 *
 * @param transactionId    the Datatrans transaction identifier
 * @param merchantId       the merchant identifier
 * @param type             the webhook event type
 * @param status           the transaction status from Datatrans
 * @param currency         the three-letter currency code
 * @param refno            the Datatrans reference number, set to the booking reference at init
 * @param paymentMethod    the payment method code (e.g. VIS, ECA)
 * @param authorizedAmount the authorized amount in minor units
 * @param card             the card details returned with the transaction
 * @param attempts         the authorization attempts, carrying the acquirer authorization code
 */
@Schema(description = "Datatrans webhook payload received after a Mobile SDK payment event")
@JsonIgnoreProperties(ignoreUnknown = true)
public record MobileSdkWebhookRequest(
    @Schema(description = "The Datatrans transaction identifier",
        example = "190410112056083383")
    String transactionId,
    @Schema(description = "The merchant identifier", example = "1100012345")
    String merchantId,
    @Schema(description = "The webhook event type", example = "payment")
    String type,
    @Schema(description = "The transaction status from Datatrans", example = "authorized")
    String status,
    @Schema(description = "The three-letter ISO 4217 currency code", example = "GBP")
    String currency,
    @Schema(description = "The Datatrans reference number (the booking reference set at init)",
        example = "PI-12345678")
    String refno,
    @Schema(description = "The payment method code", example = "VIS")
    String paymentMethod,
    @Schema(description = "The authorized amount in minor units (pence)", example = "9900")
    Integer authorizedAmount,
    @Schema(description = "The card details returned with the transaction")
    CardDetails card,
    @Schema(description = "The authorization attempts for the transaction")
    List<Attempt> attempts
) {

  /**
   * Card details returned by Datatrans with the transaction.
   *
   * @param alias       the tokenised card alias
   * @param masked      the masked card number (e.g. "424242xxxxxx4242")
   * @param expiryMonth the card expiry month (MM format)
   * @param expiryYear  the card expiry year (YY format)
   * @param info        additional card metadata
   */
  @Schema(description = "Card details returned by Datatrans with the transaction")
  @JsonIgnoreProperties(ignoreUnknown = true)
  public record CardDetails(
      @Schema(description = "The tokenised card alias")
      String alias,
      @Schema(description = "The masked card number")
      String masked,
      @Schema(description = "The card expiry month (MM)", example = "12")
      String expiryMonth,
      @Schema(description = "The card expiry year (YY)", example = "29")
      String expiryYear,
      @Schema(description = "Additional card metadata")
      CardInfo info
  ) {}

  /**
   * Additional card metadata supplied by Datatrans.
   *
   * @param brand   the card brand (e.g. "VISA_CREDIT")
   * @param type    the card type (e.g. "credit")
   * @param usage   the card usage (e.g. "consumer")
   * @param country the issuing country code
   * @param issuer  the issuing bank
   */
  @Schema(description = "Additional card metadata supplied by Datatrans")
  @JsonIgnoreProperties(ignoreUnknown = true)
  public record CardInfo(
      @Schema(description = "The card brand", example = "VISA_CREDIT")
      String brand,
      @Schema(description = "The card type", example = "credit")
      String type,
      @Schema(description = "The card usage", example = "consumer")
      String usage,
      @Schema(description = "The issuing country code", example = "GB")
      String country,
      @Schema(description = "The issuing bank")
      String issuer
  ) {}

  /**
   * A single authorization attempt on the transaction.
   *
   * @param attemptId                 the attempt identifier
   * @param type                      the attempt type (e.g. "authorize")
   * @param amount                    the attempted amount in minor units
   * @param acquirerAuthorizationCode the authorization code from the card acquirer
   * @param status                    the attempt status
   * @param paymentMethod             the payment method code used for the attempt
   */
  @Schema(description = "A single authorization attempt on the transaction")
  @JsonIgnoreProperties(ignoreUnknown = true)
  public record Attempt(
      @Schema(description = "The attempt identifier")
      String attemptId,
      @Schema(description = "The attempt type", example = "authorize")
      String type,
      @Schema(description = "The attempted amount in minor units (pence)", example = "9900")
      Integer amount,
      @Schema(description = "The authorization code from the card acquirer", example = "123456")
      String acquirerAuthorizationCode,
      @Schema(description = "The attempt status", example = "authorized")
      String status,
      @Schema(description = "The payment method code used for the attempt", example = "VIS")
      String paymentMethod
  ) {}
}
