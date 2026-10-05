package uk.co.whitbread.reservation.domain.model.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationOverrideReason {

  private String reasonCode;
  private String reasonName;
  private String callerName;
  private String managerName;

}
