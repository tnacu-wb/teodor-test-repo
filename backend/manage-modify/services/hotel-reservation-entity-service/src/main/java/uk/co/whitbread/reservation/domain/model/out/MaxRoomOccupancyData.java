package uk.co.whitbread.reservation.domain.model.out;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MaxRoomOccupancyData {

  private Integer adultsNumber;
  private Integer childrenNumber;
  private List<String> acceptedRoomTypes;
}
