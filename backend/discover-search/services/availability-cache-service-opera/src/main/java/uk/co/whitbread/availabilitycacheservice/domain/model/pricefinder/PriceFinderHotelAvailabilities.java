package uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PriceFinderHotelAvailabilities {

  private PriceFinderRate lowestMonthlyRate;
  private List<PriceFinderOperaHotelAvailabilities> priceFinderOperaHotelAvailabilitiesDtoList;
  private Integer page;
  private Integer pageSize;
  private Integer total;
}