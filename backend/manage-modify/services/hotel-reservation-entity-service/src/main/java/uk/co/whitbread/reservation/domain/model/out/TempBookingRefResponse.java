package uk.co.whitbread.reservation.domain.model.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TempBookingRefResponse {

  private String tempBookingRef;
  private String tempReservationId;
}
