package uk.co.whitbread.basket.domain.model.payments.in;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
public class Amount implements SelfValidation<Amount> {

  @NotEmpty
  private String currency;
  @NotNull
  private BigDecimal minorUnits;

  public Amount(String currency, BigDecimal minorUnits) {
    this.currency = currency;
    this.minorUnits = minorUnits;
    this.validateSelf();
  }
}
