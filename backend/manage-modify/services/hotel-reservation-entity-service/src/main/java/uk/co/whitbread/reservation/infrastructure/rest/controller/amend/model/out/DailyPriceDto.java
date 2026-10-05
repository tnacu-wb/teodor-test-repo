package uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyPriceDto {

  private String date;
  private BigDecimal netPrice;
  private BigDecimal grossPrice;
}
