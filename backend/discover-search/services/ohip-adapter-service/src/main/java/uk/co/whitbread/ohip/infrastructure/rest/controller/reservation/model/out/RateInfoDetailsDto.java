package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
