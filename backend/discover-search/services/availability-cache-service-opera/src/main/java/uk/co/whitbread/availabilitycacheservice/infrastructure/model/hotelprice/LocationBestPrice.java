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
public class LocationBestPrice {


  private String hotelCode;
  private String placeId;
  private BigDecimal price;
  private String currency;
}
