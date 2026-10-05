package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
public class ReservationRequestDto {

  @NotEmpty
  @Size(min = 1)
  @Valid
  @Schema(required = true)
  private List<ReservationDto> reservations;

  @NotNull
  @Valid
  private BookingChannelDto bookingChannel;

  @Valid
  private boolean getReservationsByIds;

  private Boolean isOta;

  private String bookingFlowId;

}
