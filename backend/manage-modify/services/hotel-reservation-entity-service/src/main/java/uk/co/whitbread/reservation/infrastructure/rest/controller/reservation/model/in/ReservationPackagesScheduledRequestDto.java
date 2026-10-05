package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationPackagesScheduledRequestDto {

  @Schema(example = "FRAMTI")
  private String hotelId;

  @NotEmpty
  @Valid
  private List<RoomReservationPackagesScheduledRequestDto> reservations;
}
