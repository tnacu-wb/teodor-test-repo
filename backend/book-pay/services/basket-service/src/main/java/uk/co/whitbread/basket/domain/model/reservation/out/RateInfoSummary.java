package uk.co.whitbread.basket.domain.model.reservation.out;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class RateInfoSummary {

  List<RateInfoDetails> details;
  BigDecimal gross;
  BigDecimal net;
  BigDecimal deposit;
  BigDecimal totalCostOfStay;
  BigDecimal outStandingCostOfStay;
  BigDecimal guestPay;
  BigDecimal routing;
  String currencyCode;
  String start;
  String end;
  String hasSuppressedRate;

}
