package uk.co.whitbread.ohip.domain.model.reservation.in;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.ohip.domain.model.validation.SelfValidation;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
public class BusinessAllowance implements SelfValidation<BusinessAllowance> {

  @DecimalMin(value = "0.0")
  private BigDecimal budget;

  @NotEmpty
  private String allowance;

  @NotNull
  private Boolean isAuthorised;

  public BusinessAllowance(BigDecimal budget, String allowance, Boolean isAuthorised) {
    this.budget = budget;
    this.allowance = allowance;
    this.isAuthorised = isAuthorised;
    this.validateSelf();
  }
}
