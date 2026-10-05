package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
public class CopyReservationsResponseDto {

  private List<CopyReservationResponseDto> reservations;
  private Map<String, String> linkBetweenReservations;
}
