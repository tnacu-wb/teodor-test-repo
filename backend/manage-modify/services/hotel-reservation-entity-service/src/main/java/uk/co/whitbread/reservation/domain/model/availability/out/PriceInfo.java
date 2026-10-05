package uk.co.whitbread.reservation.domain.model.availability.out;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceInfo {

  private LocalDate stayDate;
  private BigDecimal amountBeforeTax;
  private BigDecimal amountAfterTax;
}
