package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.pricefinder.model.out;

import com.fasterxml.jackson.annotation.JsonView;
import java.util.SortedSet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalendarPriceFinderOperaHotelAvailabilitiesDto {

  @JsonView({HotelNameView.class, HotelCodeView.class})
  private String locationId;
  @JsonView(HotelNameView.class)
  private Integer milesRadius;
  @JsonView(HotelNameView.class)
  private Integer month;
  @JsonView(HotelNameView.class)
  private String currency;

  @JsonView({HotelNameView.class, HotelCodeView.class})
  private SortedSet<LowestRatesDto> lowestRates;

}