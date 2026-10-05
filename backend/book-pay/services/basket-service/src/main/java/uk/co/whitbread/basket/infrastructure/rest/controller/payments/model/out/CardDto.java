package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardDto {

  private String token;
  private String expiryMonth;
  private String expiryYear;

}
