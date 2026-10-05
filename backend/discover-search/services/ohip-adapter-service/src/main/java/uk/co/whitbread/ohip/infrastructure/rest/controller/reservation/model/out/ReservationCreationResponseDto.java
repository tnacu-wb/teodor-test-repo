package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
public class ReservationCreationResponseDto {

  private String reservationId;
  private String createDateTime;
  private RoomStayByIdDto roomStay;
}
