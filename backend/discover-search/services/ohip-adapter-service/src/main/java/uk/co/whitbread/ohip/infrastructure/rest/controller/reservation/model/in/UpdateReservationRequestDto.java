package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
public class UpdateReservationRequestDto {

  @NotEmpty
  private String hotelId;

  @NotEmpty
  private String reservationId;
  private UpdateRoomStayDto roomStay;
  @Schema
  private List<ReservationGuestsDto> reservationGuests;
}
