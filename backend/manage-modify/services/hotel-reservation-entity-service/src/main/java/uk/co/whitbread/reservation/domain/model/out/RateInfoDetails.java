package uk.co.whitbread.reservation.domain.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RateInfoDetails {

  private String summaryDate;
  private BigDecimal revenue;
  private BigDecimal packageDetails;
  private BigDecimal tax;
  private BigDecimal gross;
  private BigDecimal net;
  private String ratePlanCode;
  private String currencyCode;

}
