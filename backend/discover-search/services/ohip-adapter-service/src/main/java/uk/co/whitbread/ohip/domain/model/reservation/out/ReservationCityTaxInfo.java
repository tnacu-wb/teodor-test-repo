package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ReservationCityTaxInfo {

  private LocalDate referenceDate;
  private BigDecimal vatAmount;
}
