package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RateInfoDetailsDto {

  String summaryDate;
  BigDecimal revenue;
  BigDecimal packageDetails;
  BigDecimal tax;
  BigDecimal gross;
  BigDecimal net;
  String ratePlanCode;
  String currencyCode;

}
