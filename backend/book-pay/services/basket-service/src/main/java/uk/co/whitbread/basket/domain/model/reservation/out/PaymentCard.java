package uk.co.whitbread.basket.domain.model.reservation.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PaymentCard {

  private String cardHolderName;
  private String cardNumberMasked;
  private String token;
  private String expirationDate;
  private String cardType;
}
