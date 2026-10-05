package uk.co.whitbread.reservation.domain.model.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationOverrideReasons {

  private String reasonCode;
  private String reasonName;
  private String callerName;
  private String managerName;

}
