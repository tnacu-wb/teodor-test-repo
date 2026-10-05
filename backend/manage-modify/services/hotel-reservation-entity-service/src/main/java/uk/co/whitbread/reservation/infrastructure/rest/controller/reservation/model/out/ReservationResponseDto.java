package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationResponseDto {

  private String basketReference;
  private List<ReservationCreationResponseDto> reservations;
  private BigDecimal totalCost;
  private String hotelId;
  private String currencyCode;
}
