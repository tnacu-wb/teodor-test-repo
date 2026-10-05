package uk.co.whitbread.reservation.domain.model.in;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
public class PreCheckInRequest {

  private String hotelId;
  private String reservationId;
  private LocalDate arrivalTime;
  private String language;
}