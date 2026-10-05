package uk.co.whitbread.reservation.domain.model.out.aem;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LocationsResponse {
  private List<ZonalUuidResponse> locations;
}
