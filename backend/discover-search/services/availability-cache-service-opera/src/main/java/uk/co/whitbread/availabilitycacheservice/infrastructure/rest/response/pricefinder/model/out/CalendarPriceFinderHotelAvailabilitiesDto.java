package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.pricefinder.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CalendarPriceFinderHotelAvailabilitiesDto {

  private CalendarPriceFinderOperaHotelAvailabilitiesDto calendarPriceFinderOperaHotelAvailabilitiesDtoList;

}
