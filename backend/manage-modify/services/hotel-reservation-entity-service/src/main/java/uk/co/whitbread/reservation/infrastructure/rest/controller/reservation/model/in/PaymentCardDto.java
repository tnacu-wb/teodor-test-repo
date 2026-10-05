package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCardDto {

  private String cardType;
  private String token;
  private String expirationDate;
  private String cardHolderName;
  private String cardNumberLast4Digits;
  private String citId;

}
