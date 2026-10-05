package uk.co.whitbread.basket.domain.model.payments.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentsConfirmation {

  private String reference;
  private String paymentId;
  private String paymentStatus;
  private String returnCode;
  private String cardSchemeId;
  private String fraudCheckDecision;
  private String token;
  private String expiry;
  private String last4Digits;
  private String bookingReference;
  private String channel;
  private String countryCode;
  private String language;
  private String lastName;
  private String firstName;
  private PaymentError paymentError;
  private String paymentOptionSelected;
  private String emailAddress;
  private String ccAgentId;
  private String threeDSIndicator;
}