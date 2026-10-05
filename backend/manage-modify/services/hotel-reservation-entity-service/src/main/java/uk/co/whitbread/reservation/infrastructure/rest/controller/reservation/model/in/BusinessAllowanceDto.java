package uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessAllowanceDto {

  private String allowance;
  private BigDecimal budget;
  private Boolean isAuthorised;
}
