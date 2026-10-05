package uk.co.whitbread.booking.domain.model.information.out;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BookingInfoResponse {

  private BookingDetails reservationDetails;
  private LocalTime checkInTime;
  private LocalTime checkOutTime;
  private String sessionId;
}
