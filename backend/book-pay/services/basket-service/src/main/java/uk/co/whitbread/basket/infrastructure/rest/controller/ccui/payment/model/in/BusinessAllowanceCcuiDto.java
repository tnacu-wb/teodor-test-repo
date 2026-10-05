package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.model.in;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessAllowanceCcuiDto {

  private String allowance;
  private BigDecimal budget;
  private Boolean isAuthorised;
}
