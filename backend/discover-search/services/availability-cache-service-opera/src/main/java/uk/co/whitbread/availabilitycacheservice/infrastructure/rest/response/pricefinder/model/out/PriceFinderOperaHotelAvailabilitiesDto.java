package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.response.pricefinder.model.out;

import com.fasterxml.jackson.annotation.JsonView;
import java.util.SortedSet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PriceFinderOperaHotelAvailabilitiesDto {

  @JsonView({HotelNameView.class, HotelCodeView.class})
  private String hotelCode;
  @JsonView(HotelNameView.class)
  private String hotelName;

  @JsonView({HotelNameView.class, HotelCodeView.class})
  private SortedSet<AvailabilitiesDto> availabilities;
}
