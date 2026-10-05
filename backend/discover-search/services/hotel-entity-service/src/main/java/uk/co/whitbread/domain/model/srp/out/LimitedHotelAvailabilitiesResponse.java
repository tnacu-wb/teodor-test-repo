package uk.co.whitbread.domain.model.srp.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LimitedHotelAvailabilitiesResponse {

  List<LimitedHotelAvailabilityResponse> limitedAvailabilities;

}
