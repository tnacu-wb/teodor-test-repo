package uk.co.whitbread.reservation.domain.model.availability.out;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelAvailability {

  private Instant timestamp;
  private String hotelId;
  private String startDate;
  private String endDate;
  private boolean available;
  private boolean limitedAvailability;

  @Singular
  private List<RoomRate> roomRates = new ArrayList<>();
}
