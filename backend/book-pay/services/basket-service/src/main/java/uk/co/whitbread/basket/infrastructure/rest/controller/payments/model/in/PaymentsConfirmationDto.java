package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class PaymentsConfirmationDto implements SelfValidation<PaymentsConfirmationDto> {

  @NotNull
  private String reference;
  @NotNull
  private String paymentId;
  @NotNull
  private String paymentStatus;
  private String returnCode;
  private String cardSchemeId;
  private String fraudCheckDecision;
  private String token;
  private String expiry;
  private String last4Digits;
  @NotNull
  private String bookingReference;
  @NotNull
  private String countryCode;
  @NotNull
  private String language;
  private String lastName;
  private String firstName;
  @NotNull
  private String channel;
  private PaymentErrorDto paymentError;
  private String threeDSIndicator;

  public PaymentsConfirmationDto(String reference, String paymentId, String paymentStatus, String returnCode,
      String cardSchemeId, String fraudCheckDecision, String token, String expiry, String last4Digits,
      String bookingReference, String countryCode, String language, String lastName, String firstName, String channel,
      PaymentErrorDto paymentError, String threeDSIndicator) {
    this.reference = reference;
    this.paymentId = paymentId;
    this.paymentStatus = paymentStatus;
    this.returnCode = returnCode;
    this.cardSchemeId = cardSchemeId;
    this.fraudCheckDecision = fraudCheckDecision;
    this.token = token;
    this.expiry = expiry;
    this.last4Digits = last4Digits;
    this.bookingReference = bookingReference;
    this.countryCode = countryCode;
    this.language = language;
    this.lastName = lastName;
    this.firstName = firstName;
    this.channel = channel;
    this.paymentError = paymentError;
    this. threeDSIndicator = threeDSIndicator;
    this.validateSelf();
  }
}
