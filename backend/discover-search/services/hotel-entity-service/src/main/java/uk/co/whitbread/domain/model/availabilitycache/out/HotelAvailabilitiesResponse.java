package uk.co.whitbread.domain.model.availabilitycache.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class HotelAvailabilitiesResponse {

  private List<HotelResponse> hotelAvailabilities;
  private int page;
  private int pageSize;
  private int total;
}
