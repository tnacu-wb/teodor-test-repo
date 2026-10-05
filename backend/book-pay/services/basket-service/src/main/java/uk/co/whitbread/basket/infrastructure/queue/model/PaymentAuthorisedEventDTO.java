package uk.co.whitbread.basket.infrastructure.queue.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentAuthorisedEventDTO {

  private String basketId;
  private String transactionId;
  private String paymentProvider;
  private String paymentMethod;
  private String cardAlias;
  private String last4Digits;
  private String expiry;
  private Integer authorizedAmount;
  private String currency;
  private String paymentOption;
  private String paymentStatus;
  private String language;

}
