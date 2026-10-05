package uk.co.whitbread.payments.domain.model.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PriceCapLocations {

  private Price ukWide;

  private Price greaterLondon;

  private Price ireland;

}
