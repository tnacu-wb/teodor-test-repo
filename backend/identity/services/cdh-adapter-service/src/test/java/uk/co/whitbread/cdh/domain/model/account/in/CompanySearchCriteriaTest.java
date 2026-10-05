package uk.co.whitbread.cdh.domain.model.account.in;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.cdh.infrastructure.rest.controller.account.model.in.CompanySearchCriteriaDto;

class CompanySearchCriteriaTest {

  private static ValidatorFactory validatorFactory;
  private static Validator validator;

  @BeforeAll
  static void createValidator() {
    validatorFactory = Validation.buildDefaultValidatorFactory();
    validator = validatorFactory.getValidator();
  }

  @AfterAll
  static void close() {
    validatorFactory.close();
  }

  @Test
  void verifyMandatoryFields() {
    var companySearchCriteriaDto = CompanySearchCriteriaDto.builder()
        .accessedBy("test-user")
        .accessContext("test-application")
        .build();

    Set<ConstraintViolation<CompanySearchCriteriaDto>> violations = validator.validate(
        companySearchCriteriaDto);
    assertTrue(violations.isEmpty());
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSet() {
    var companySearchCriteriaDto = CompanySearchCriteriaDto.builder()
        .build();

    Set<ConstraintViolation<CompanySearchCriteriaDto>> violations = validator.validate(
        companySearchCriteriaDto);

    assertEquals(2, violations.size());
    assertTrue(violations.stream().anyMatch(
        violation -> violation.getPropertyPath().toString().equals("accessContext")
            && violation.getMessage().equals("must not be empty")));
    assertTrue(violations.stream().anyMatch(
        violation -> violation.getPropertyPath().toString().equals("accessedBy")
            && violation.getMessage().equals("must not be empty")));
  }

}
