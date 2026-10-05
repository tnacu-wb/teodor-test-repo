package uk.co.whitbread.ohip.domain.model.availability.in;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode
public class AvailabilityByIdsSearchCriteriaV2 {

  private BookingChannel bookingChannel;
  private List<String> hotelIds;
  private LocalDate arrivalDate;
  private LocalDate departureDate;
  private List<Room> rooms;
  private RateV2 rates;
}
