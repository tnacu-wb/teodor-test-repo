package uk.co.whitbread.content.domain.model.pricefinder.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PriceFinderGlobalConfig {
  private PriceFinderConfig priceFinderConfig;
}
