package uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder;

import java.util.SortedSet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalendarPriceFinderOperaHotelAvailabilities {

  private String locationId;
  private Integer milesRadius;
  private Integer month;
  private String currency;
  private SortedSet<LowestRate> lowestRates;

}
