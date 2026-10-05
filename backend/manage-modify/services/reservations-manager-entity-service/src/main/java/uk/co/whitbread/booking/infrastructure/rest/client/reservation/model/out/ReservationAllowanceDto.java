package uk.co.whitbread.booking.infrastructure.rest.client.reservation.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationAllowanceDto {

  private String allowance;
  private BigDecimal budget;
}
