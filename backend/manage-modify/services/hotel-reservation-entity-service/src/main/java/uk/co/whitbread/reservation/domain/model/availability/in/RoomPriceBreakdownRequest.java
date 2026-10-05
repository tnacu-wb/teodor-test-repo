package uk.co.whitbread.reservation.domain.model.availability.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomPriceBreakdownRequest {
  private String arrivalDate;
  private String departureDate;
  private String ratePlanCode;
  private List<String> roomTypes;
  private List<Integer> adultsNo;
  private List<Integer> childrenNo;
}
