package uk.co.whitbread.reservation.domain.model.availability.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyPrice {

  private String date;
  private BigDecimal netPrice;
  private BigDecimal grossPrice;

}
