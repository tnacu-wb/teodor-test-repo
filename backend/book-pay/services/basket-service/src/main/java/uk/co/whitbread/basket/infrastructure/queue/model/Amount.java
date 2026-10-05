package uk.co.whitbread.basket.infrastructure.queue.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Amount implements SelfValidation<Amount> {

  private String currency;
  private BigDecimal minorUnits;
}
