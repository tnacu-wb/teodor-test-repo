package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.hotelprice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.PriceDto;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BestPricedHotelDto {

  private String hotelCode;

  private String hotelBrand;

  private PriceDto bestPrice;

}
