package uk.co.whitbread.payments.infrastructure.rest.client.reservation.model.out;

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
public class RateInfoSummaryDto {

  List<RateInfoDetailsDto> details;
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
