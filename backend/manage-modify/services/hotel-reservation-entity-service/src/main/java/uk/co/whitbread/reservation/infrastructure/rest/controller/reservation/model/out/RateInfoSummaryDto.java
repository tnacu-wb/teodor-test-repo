package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
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
