package uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelprice;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BestPricedHotel {

  private String hotelCode;

  private BigDecimal bestPrice;

  private String currency;

  private BigDecimal priceWithCityTax;
}
