package uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalendarPriceFinderHotelAvailabilities {

  private CalendarPriceFinderOperaHotelAvailabilities calendarPriceFinderOperaHotelAvailabilitiesDtoList;

}
