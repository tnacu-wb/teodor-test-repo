package uk.co.whitbread.basket.processor.domain.model.out;

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
