package uk.co.whitbread.reservation.domain.model.out;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RateInfoSummary {

  private List<RateInfoDetails> details;
  private BigDecimal gross;
  private BigDecimal net;
  private BigDecimal deposit;
  private BigDecimal totalCostOfStay;
  private BigDecimal outStandingCostOfStay;
  private BigDecimal guestPay;
  private BigDecimal routing;
  private String currencyCode;
  private String start;
  private String end;
  private String hasSuppressedRate;

}
