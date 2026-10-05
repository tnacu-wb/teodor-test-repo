package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.validation.BudgetValue;

@Data
@Builder
@NoArgsConstructor
@BudgetValue
public class BusinessAllowanceDto implements SelfValidation<BusinessAllowanceDto> {

  private String allowance;

  private BigDecimal budget;
  private Boolean isAuthorised;

  public BusinessAllowanceDto(String allowance, BigDecimal budget, Boolean isAuthorised) {
    this.allowance = allowance;
    this.budget = budget;
    this.isAuthorised = isAuthorised;
    this.validateSelf();
  }

}
