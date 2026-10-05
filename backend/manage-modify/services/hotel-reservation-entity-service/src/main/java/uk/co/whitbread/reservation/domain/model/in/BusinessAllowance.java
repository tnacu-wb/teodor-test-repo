package uk.co.whitbread.reservation.domain.model.in;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessAllowance {

  @DecimalMin(value = "0.0")
  private BigDecimal budget;

  @NotEmpty
  private String allowance;

  @NotNull
  private Boolean isAuthorised;
}
