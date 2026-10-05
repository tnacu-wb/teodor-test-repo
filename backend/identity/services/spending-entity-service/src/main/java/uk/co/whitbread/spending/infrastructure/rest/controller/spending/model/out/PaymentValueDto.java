package uk.co.whitbread.spending.infrastructure.rest.controller.spending.model.out;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PaymentValueDto {

  private String value;

  private String currencyCode;

  private String currencySymbol;
}
