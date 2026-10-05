package uk.co.whitbread.infrastructure.rest.controller.availability.model.out;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelAvailabilityResponseDto {

  private Instant timestamp;
  private String hotelId;
  private String startDate;
  private String endDate;
  private boolean available;
  private boolean limitedAvailability;
  private boolean mlos;
  private List<RoomRateDto> roomRates;

}
