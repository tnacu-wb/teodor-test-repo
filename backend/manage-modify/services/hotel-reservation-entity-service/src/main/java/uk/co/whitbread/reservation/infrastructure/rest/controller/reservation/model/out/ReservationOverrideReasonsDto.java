package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationOverrideReasonsDto {

  private String reasonCode;
  private String reasonName;
  private String callerName;
  private String managerName;

}
