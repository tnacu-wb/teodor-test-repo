package uk.co.whitbread.ohip.domain.model.reservation.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationGuests {
  private ProfileInfo profileInfo;
}
