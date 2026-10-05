package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
@NoArgsConstructor
public class AmountDto implements SelfValidation<AmountDto> {

  private String currency;
  private BigDecimal minorUnits;

  public AmountDto(String currency, BigDecimal minorUnits) {
    this.currency = currency;
    this.minorUnits = minorUnits;
    this.validateSelf();
  }
}
