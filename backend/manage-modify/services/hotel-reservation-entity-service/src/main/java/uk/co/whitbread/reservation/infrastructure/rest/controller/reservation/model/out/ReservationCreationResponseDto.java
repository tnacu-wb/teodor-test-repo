package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ReservationCreationResponseDto {

  private String createDateTime;
  private ReservationCreationRoomStayDto roomStay;
}
