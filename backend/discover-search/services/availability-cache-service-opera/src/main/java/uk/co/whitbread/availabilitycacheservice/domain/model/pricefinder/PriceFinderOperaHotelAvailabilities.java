package uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PriceFinderOperaHotelAvailabilities {

  private String hotelCode;
  private String hotelName;
  private Double distanceFromSearchLocation;
  private Set<Availabilities> availabilities;
}
