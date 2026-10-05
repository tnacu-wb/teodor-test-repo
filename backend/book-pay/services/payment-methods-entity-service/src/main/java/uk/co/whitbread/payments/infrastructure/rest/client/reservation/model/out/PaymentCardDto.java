package uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCardDto {

  private String cardHolderName;
  private String cardNumberMasked;
  private String token;
  private String expirationDate;
  private String cardType;
  private String paymentMethod;
  private Integer folioView;
}