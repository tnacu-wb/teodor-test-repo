package uk.co.whitbread.basket.domain.model.business.in;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;
import uk.co.whitbread.basket.domain.model.validation.SelfValidation;

@Data
@Builder
public class BusinessAllowance implements
        SelfValidation<BusinessAllowance> {

  private String allowance;
  private BigDecimal budget;
  private Boolean isAuthorised;

  public  BusinessAllowance(String allowance, BigDecimal budget, Boolean isAuthorised) {
    this.allowance = allowance;
    this.budget = budget;
    this.isAuthorised = isAuthorised;
    this.validateSelf();
  }

}
