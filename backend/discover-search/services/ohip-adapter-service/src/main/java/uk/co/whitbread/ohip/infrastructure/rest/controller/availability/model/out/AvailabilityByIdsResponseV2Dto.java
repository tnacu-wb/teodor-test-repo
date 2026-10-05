package uk.co.whitbread.ohip.infrastructure.rest.controller.availability.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvailabilityByIdsResponseV2Dto {
  List<AvailabilityResultV2Dto> hotelAvailability;
}
