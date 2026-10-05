package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ReservationCreationResponse {

  private String reservationId;
  private String createDateTime;
  private RoomStay roomStay;
  private List<DepositPolicies> depositPolicies;
}
