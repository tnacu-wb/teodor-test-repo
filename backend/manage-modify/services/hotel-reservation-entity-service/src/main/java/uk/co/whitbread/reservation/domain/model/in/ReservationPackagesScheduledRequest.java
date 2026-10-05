package uk.co.whitbread.reservation.domain.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationPackagesScheduledRequest {

  @Schema(example = "FRAMTI")
  private String hotelId;

  private List<RoomReservationPackagesScheduledRequest> reservations;
}
