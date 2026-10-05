package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.hotelprice;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class HotelPriceResponseDto {

  private int total;

  private List<BestPricedHotelDto> bestPricedHotels;

}
