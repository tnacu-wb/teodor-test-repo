package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReservationResponseDto {

  private List<ReservationCreationResponseDto> reservations;
  private BigDecimal totalCost;
  private String hotelId;
  private String currencyCode;
}
