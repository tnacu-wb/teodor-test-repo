package uk.co.whitbread.ohip.infrastructure.rest.client.eckoh.model.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEckohCardDto {

  private String cardType;
  private String paymentMethod;
  private String token;
  private String expirationDate;
  private String maskedPan;

}
