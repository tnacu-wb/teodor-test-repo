package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
public class HotelReservationInstructionTypeDto {

  @NotEmpty
  @Size(min = 1)
  @Valid
  private List<UniqueIdTypeDto> reservationIdList;

  @NotNull
  @Valid
  private RoomStayTypeDto roomStay;

  @NotEmpty
  @Valid
  private String hotelId;

}
