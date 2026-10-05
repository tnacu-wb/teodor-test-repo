package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.validation;

import static uk.co.whitbread.basket.domain.model.basket.BasketConstant.MAX_BUDGET_VALUE;
import static uk.co.whitbread.basket.domain.model.basket.BasketConstant.MIN_BUDGET_VALUE;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.BusinessAccountDto;

@Slf4j
public class AllowanceValueValidator implements ConstraintValidator<AllowanceValue, BusinessAccountDto> {

  @Override
  public boolean isValid(BusinessAccountDto businessAllowance,
                           ConstraintValidatorContext constraintValidatorContext) {
    if (businessAllowance == null || businessAllowance.getDinnerAllowance() == null) {
      return true;
    } else {
      try {
        BigDecimal bd = new BigDecimal(businessAllowance.getDinnerAllowance().toString());
        int intVal = bd.intValue();
        double diff = bd.doubleValue() - intVal;
        if (diff > 0) {
          return false;
        }
      } catch (NumberFormatException nfe) {
        return false;
      }
      if (businessAllowance.getDinnerAllowance().compareTo(new BigDecimal(MIN_BUDGET_VALUE)) < 0) {
        return false;
      } else {
        return businessAllowance.getDinnerAllowance().compareTo(new BigDecimal(MAX_BUDGET_VALUE)) <= 0;
      }
    }
  }

  @Override
  public void initialize(AllowanceValue constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }
}