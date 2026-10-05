package uk.co.whitbread.basket.domain.model.reservation.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class RateInfoDetails {

  String summaryDate;
  BigDecimal revenue;
  BigDecimal packageDetails;
  BigDecimal tax;
  BigDecimal gross;
  BigDecimal net;
  String ratePlanCode;
  String currencyCode;

}
