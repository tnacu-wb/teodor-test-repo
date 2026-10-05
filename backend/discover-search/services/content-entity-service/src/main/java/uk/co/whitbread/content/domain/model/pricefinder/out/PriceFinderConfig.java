package uk.co.whitbread.content.domain.model.pricefinder.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PriceFinderConfig {
  private List<PriceFinderViews> priceFinderViews;
}
