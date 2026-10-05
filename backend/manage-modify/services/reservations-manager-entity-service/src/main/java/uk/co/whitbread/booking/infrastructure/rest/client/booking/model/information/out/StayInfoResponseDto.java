package uk.co.whitbread.booking.infrastructure.rest.client.booking.model.information.out;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StayInfoResponseDto {

  private StayDetailsDto reservationDetails;
  private LocalTime checkInTime;
  private LocalTime checkOutTime;
  private String sessionId;
}
