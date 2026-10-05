package uk.co.whitbread.basket.domain.model.basket.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentCard {
  private String cardType;
  private String token;
  private String expirationDate;
  private String cardHolderName;
  private String cardNumberLast4Digits;
  private String citId;
}
