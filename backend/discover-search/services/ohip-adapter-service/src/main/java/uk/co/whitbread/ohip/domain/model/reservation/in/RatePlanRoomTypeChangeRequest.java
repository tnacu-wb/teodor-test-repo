package uk.co.whitbread.ohip.domain.model.reservation.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatePlanRoomTypeChangeRequest {

  private String basketReferenceId;
  private String hotelId;
  private List<String> reservationIds;
  private String rateCode;
  private String startDate;
  private String endDate;
  private List<String> roomTypes;
  private String currency;
  private List<Integer> adultsNumber;
  private List<Integer> childrenNumber;
}
