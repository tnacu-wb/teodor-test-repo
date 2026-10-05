package uk.co.whitbread.domain.model.availability.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MultiAvailabilityResponse {

  private String timestamp;
  private List<HotelAvailabilityResult> hotelAvailabilityResults;

}
