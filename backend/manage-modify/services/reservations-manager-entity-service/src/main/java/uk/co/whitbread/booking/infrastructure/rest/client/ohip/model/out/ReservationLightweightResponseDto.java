package uk.co.whitbread.booking.infrastructure.rest.client.ohip.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationLightweightResponseDto {
  private List<LightweightReservationByIdDto> reservationByIdList;
}
