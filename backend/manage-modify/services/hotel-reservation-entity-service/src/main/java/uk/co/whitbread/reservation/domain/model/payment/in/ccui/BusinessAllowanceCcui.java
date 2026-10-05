package uk.co.whitbread.reservation.domain.model.payment.in.ccui;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessAllowanceCcui {

  private String allowance;
  private BigDecimal budget;
  private Boolean isAuthorised;
}
