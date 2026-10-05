package uk.co.whitbread.domain.model.srp.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class HotelAvailabilitiesResponse {

  List<HotelAvailabilityResponse> hotelAvailabilityList;
  Integer page;
  Integer pageSize;
  Integer total;
}
