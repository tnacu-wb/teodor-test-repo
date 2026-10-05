package uk.co.whitbread.domain.model.srp.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LimitedHotelAvailabilityResponse {

  private String hotelId;
  private Boolean available;
  private Boolean limitedAvailability;
  private Cost lowestRoomRate;
}
