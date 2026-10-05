package uk.co.whitbread.reservation.domain.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CopyReservationResponse {

  private String reservationId;
  private String createDateTime;
}
