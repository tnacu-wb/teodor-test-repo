package uk.co.whitbread.ohip.domain.model.reservation.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RateInfoDetailsSingleCall {
  private String summaryDate;
  private BigDecimal revenue;
  private BigDecimal packageDetails;
  private BigDecimal tax;
  private BigDecimal gross;
  private BigDecimal net;
  private String ratePlanCode;
  private String currencyCode;
}