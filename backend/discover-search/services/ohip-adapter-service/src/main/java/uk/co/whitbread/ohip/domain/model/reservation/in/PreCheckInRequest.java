package uk.co.whitbread.ohip.domain.model.reservation.in;

import java.util.Date;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class PreCheckInRequest {

  private String hotelId;
  private String reservationId;
  private Date arrivalTime;
  private String language;
}