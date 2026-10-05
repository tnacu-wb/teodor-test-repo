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
public class PriceFinderRoomRateInfo {

  private String availableDate;
  private List<HotelRoomRateInfo> hotelRoomRateInfo;
}
