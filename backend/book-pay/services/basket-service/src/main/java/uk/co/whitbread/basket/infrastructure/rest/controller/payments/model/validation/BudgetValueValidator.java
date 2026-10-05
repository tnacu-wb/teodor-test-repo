package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.validation;

import static uk.co.whitbread.basket.domain.model.basket.BasketConstant.MAX_BUDGET_VALUE;
import static uk.co.whitbread.basket.domain.model.basket.BasketConstant.MIN_BUDGET_VALUE;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.basket.domain.model.payments.in.Allowance;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.BusinessAllowanceDto;

@Slf4j
public class BudgetValueValidator implements ConstraintValidator<BudgetValue, BusinessAllowanceDto> {
  @Override
  public boolean isValid(BusinessAllowanceDto businessAllowance,
                           ConstraintValidatorContext constraintValidatorContext) {
    if (businessAllowance == null) {
      return true;
    }
    if (Allowance.DINNER.name().equalsIgnoreCase(businessAllowance.getAllowance())
        && businessAllowance.getIsAuthorised()) {
      if (businessAllowance.getBudget() == null) {
        return false;
      }
      try {
        BigDecimal bd = new BigDecimal(businessAllowance.getBudget().toString());
        int intVal = bd.intValue();
        double diff = bd.doubleValue() - intVal;
        if (diff > 0) {
          return false;
        }
      } catch (NumberFormatException nfe) {
        return false;
      }
      if (businessAllowance.getBudget().compareTo(new BigDecimal(MIN_BUDGET_VALUE)) < 0) {
        return false;
      } else {
        return businessAllowance.getBudget().compareTo(new BigDecimal(MAX_BUDGET_VALUE)) <= 0;
      }
    }
    return true;
  }

  @Override
  public void initialize(BudgetValue constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }
}