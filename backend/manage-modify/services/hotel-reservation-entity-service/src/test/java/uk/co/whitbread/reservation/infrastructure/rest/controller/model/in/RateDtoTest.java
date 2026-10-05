package uk.co.whitbread.reservation.infrastructure.rest.controller.model.in;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.RateDto;

@Slf4j
public class RateDtoTest {

  private static ValidatorFactory validatorFactory;
  private static Validator validator;

  @BeforeAll
  public static void createValidator() {
    validatorFactory = Validation.buildDefaultValidatorFactory();
    validator = validatorFactory.getValidator();
  }

  @AfterAll
  public static void close() {
    validatorFactory.close();
  }

  @Test
  void verifyMandatoryFields() {
    var rateDto = RateDto.builder()
        .amountBeforeTax(BigDecimal.valueOf(1998))
        .currencyCode("GBP")
        .endDate("2022-10-12")
        .startDate("2022-10-10")
        .build();

    Set<ConstraintViolation<RateDto>> violations = validator.validate(rateDto);
    assertTrue(violations.isEmpty());
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSet() {
    var rateDto = RateDto.builder()
        .currencyCode("")
        .endDate("")
        .startDate("")
        .build();

    Set<ConstraintViolation<RateDto>> violations = validator.validate(rateDto);

    assertEquals(4, violations.size());
    assertTrue(violations.stream().anyMatch(
        violation -> violation.getPropertyPath().toString().equals("amountBeforeTax")
            && violation.getMessage().equals("must not be null")));
    assertTrue(violations.stream().anyMatch(
        violation -> violation.getPropertyPath().toString().equals("currencyCode")
            && violation.getMessage().equals("must not be empty")));
    assertTrue(violations.stream().anyMatch(
        violation -> violation.getPropertyPath().toString().equals("endDate")
            && violation.getMessage().equals("must not be empty")));
    assertTrue(violations.stream().anyMatch(
        violation -> violation.getPropertyPath().toString().equals("startDate")
            && violation.getMessage().equals("must not be empty")));
  }

}
