package uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceFinderRate {

  private BigDecimal price;
  private String currency;
}
