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
public class HotelAvailabilityResult {

  private String hotelId;
  private String arrivalDate;
  private String departureDate;
  private List<MultiRoomTypeInfo> roomTypes;

}
