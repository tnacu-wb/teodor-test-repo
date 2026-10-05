package uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out;

import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AvailabilityByIdsSearchCriteriaV2Dto {

  private List<String> hotelIds;
  private LocalDate arrivalDate;
  private LocalDate departureDate;
  private List<RoomByIdsDto> rooms;
  private RateDto rates;
}
