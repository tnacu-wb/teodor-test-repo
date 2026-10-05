package uk.co.whitbread.ohip.domain.model.amend.out;

import java.math.BigDecimal;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmendSummaryResponse {
  private BigDecimal net;
  private Map<String, BigDecimal> deposit;
  private BigDecimal totalCostOfStay;
  private BigDecimal outStandingCostOfStay;
  private Map<String, BigDecimal> guestPay;
}
