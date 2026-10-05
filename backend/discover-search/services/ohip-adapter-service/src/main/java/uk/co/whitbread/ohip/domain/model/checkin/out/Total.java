package uk.co.whitbread.ohip.domain.model.checkin.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Total {

  private BigDecimal amountBeforeTax;

}
