package uk.co.whitbread.infrastructure.rest.controller.srp.model.out;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CostDto {

  private BigDecimal netTotal;
  private String currencyCode;

}
