package uk.co.whitbread.booking.infrastructure.rest.controller.booking.model.information.out;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BookingInfoResponseDto {

  private BookingDetailsDto reservationDetails;
  private LocalTime checkInTime;
  private LocalTime checkOutTime;
  private String sessionId;
}
