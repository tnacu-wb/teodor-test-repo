package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.hotelprice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.PriceDto;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class LocationPrice {

  private String placeId;

  private String hotelCode;

  private PriceDto bestPrice;

}
