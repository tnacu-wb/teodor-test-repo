package uk.co.whitbread.reservation.domain.model.payment.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Card {

  private String cardType;
  private String cardholderName;
  private Boolean cnpRequired;
  private String expiryMonth;
  private String expiryYear;
  private String logoUrl;
  private String token;
  private String type;

}
