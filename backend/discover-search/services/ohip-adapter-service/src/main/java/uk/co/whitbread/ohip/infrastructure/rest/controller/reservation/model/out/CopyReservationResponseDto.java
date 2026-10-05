package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import lombok.Data;

@Data
public class CopyReservationResponseDto {

  private String reservationId;
  private String createDateTime;
}
