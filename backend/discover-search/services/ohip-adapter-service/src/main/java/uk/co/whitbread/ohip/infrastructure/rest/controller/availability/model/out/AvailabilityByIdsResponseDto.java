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
public class AvailabilityByIdsResponseDto {

  private List<AvailabilityResponseDto> hotelAvailability;

}
