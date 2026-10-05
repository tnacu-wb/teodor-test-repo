package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RateInfoSummarySingleCall {
  private List<RateInfoDetailsSingleCall> details;
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