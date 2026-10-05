package uk.co.whitbread.infrastructure.rest.controller.availability.model.out;

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
public class PriceInfoDto {

  private LocalDate stayDate;
  private BigDecimal amountBeforeTax;
  private BigDecimal amountAfterTax;
}
