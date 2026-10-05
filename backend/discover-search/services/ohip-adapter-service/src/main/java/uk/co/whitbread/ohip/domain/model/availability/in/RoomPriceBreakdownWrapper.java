package uk.co.whitbread.ohip.domain.model.availability.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.domain.model.availability.out.AvailabilityRoom;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomPriceBreakdownWrapper {
  private String hotelId;
  private String ratePlanCode;
  private String arrivalDate;
  private String departureDate;
  private Integer adults;
  private Integer children;
  private AvailabilityRoom room;
}
