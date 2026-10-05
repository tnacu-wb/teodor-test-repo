/*

* Copyright (C) 2023, Whitbread PLC. - All Rights Reserved.

*/


/**
 * The {@code BudgetValidationAnnotationTest} class runs tests to
 * check constraints on budget values work correctly .
 *
 * @author  Mohammed Azam
 */

package uk.co.whitbread.basket.infrastructure.rest.controller.basket.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.basket.domain.model.payments.in.Allowance;
import uk.co.whitbread.basket.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.BusinessAllowanceDto;

import jakarta.validation.*;
import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BudgetValidationAnnotationTest {
  private Validator validator;
  @BeforeEach
  public void setUp() {
    ValidatorFactory factory = ValidatorFactory.getInstance(
            Validation.buildDefaultValidatorFactory().getValidator());;
    validator = factory.getValidator();
  }

  @Test
  public void testBudgeNegativeSuccess() {
    BusinessAllowanceDto allowance = BusinessAllowanceDto.builder().allowance(Allowance.ALLOW_ALCOHOL.name()).budget(BigDecimal.ZERO).build();
    Set<ConstraintViolation<BusinessAllowanceDto>> constraintViolations =
            validator.validate(allowance);
    assertEquals(0, constraintViolations.size());

  }
  @Test
  public void testBudgetCornerMinSuccess() {
    BusinessAllowanceDto allowance = BusinessAllowanceDto.builder().allowance(Allowance.MEAL_DEAL.name()).budget(BigDecimal.ZERO).build();
    Set<ConstraintViolation<BusinessAllowanceDto>> constraintViolations =
            validator.validate(allowance);
    assertEquals(0, constraintViolations.size());
  }

  @Test
  public void testBudgetNegPass() {
    BusinessAllowanceDto allowance = BusinessAllowanceDto.builder().allowance(Allowance.DINNER.name()).
              budget(BigDecimal.TEN.multiply(new BigDecimal(-1))).isAuthorised(false).build();
    Set<ConstraintViolation<BusinessAllowanceDto>> constraintViolations =
            validator.validate(allowance);
    assertEquals(0, constraintViolations.size());
  }

  @Test
  public void testBudgetNegFail() {
    try {
      BusinessAllowanceDto allowance = BusinessAllowanceDto.builder().allowance(Allowance.DINNER.name()).
              budget(BigDecimal.TEN.multiply(new BigDecimal(-1))).isAuthorised(true).build();
    } catch(ConstraintViolationException e) {
      assertEquals(": Please enter a whole number between 1 and 999.", e.getMessage());
    }
  }
  @Test
  public void testBudgetZeroFail() {
    try {
      BusinessAllowanceDto allowance = BusinessAllowanceDto.builder().allowance(Allowance.DINNER.name()).
              budget(BigDecimal.ZERO).isAuthorised(true).build();
    } catch(ConstraintViolationException e) {
      assertEquals(": Please enter a whole number between 1 and 999.", e.getMessage());
    }
  }
  @Test
  public void testBudgetSuccess() {
    BigDecimal bg = new BigDecimal(10.0);
    BusinessAllowanceDto allowance = BusinessAllowanceDto.builder().allowance(Allowance.DINNER.name()).
                                  budget(bg).isAuthorised(true).build();
    Set<ConstraintViolation<BusinessAllowanceDto>> constraintViolations =
            validator.validate(allowance);
    assertEquals(0, constraintViolations.size());
  }

  @Test
  public void testBudgetFail() {
    BigDecimal bg = new BigDecimal(10.5);
    try {
      BusinessAllowanceDto allowance = BusinessAllowanceDto.builder().allowance(Allowance.DINNER.name()).
              budget(bg).isAuthorised(true).build();
    } catch(ConstraintViolationException e)  {
      assertEquals(": Please enter a whole number between 1 and 999.", e.getMessage());
    }
  }

  @Test
  public void testEmptyBudgetSuccess() {
    try {
      BusinessAllowanceDto allowance = BusinessAllowanceDto.builder().allowance(Allowance.DINNER.name()).
          budget(null).isAuthorised(true).build();
    } catch (ConstraintViolationException e) {
      assertEquals(": Please enter a whole number between 1 and 999.", e.getMessage());
    }
  }

}
