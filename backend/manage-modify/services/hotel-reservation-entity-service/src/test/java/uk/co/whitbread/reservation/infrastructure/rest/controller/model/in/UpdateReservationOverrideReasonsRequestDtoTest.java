package uk.co.whitbread.reservation.infrastructure.rest.controller.model.in;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.UpdateReservationOverrideReasonsRequestDto;

@Slf4j
public class UpdateReservationOverrideReasonsRequestDtoTest {

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
    var updateReservationOverrideReasonsRequestDto =
        UpdateReservationOverrideReasonsRequestDto.builder()
            .basketReference("GBM6919649")
            .hotelId("HOTELCODE")
            .reasonCode("ILL")
            .reasonName("Medical Appointments")
            .callerName("John Doe")
            .build();

    Set<ConstraintViolation<UpdateReservationOverrideReasonsRequestDto>> violations = validator.validate(
        updateReservationOverrideReasonsRequestDto);
    assertTrue(violations.isEmpty());
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSet() {
    var updateReservationOverrideReasonsRequestDto =
        UpdateReservationOverrideReasonsRequestDto.builder().build();

    Set<ConstraintViolation<UpdateReservationOverrideReasonsRequestDto>> violations = validator.validate(
        updateReservationOverrideReasonsRequestDto);

    assertEquals(5, violations.size());
    assertTrue(violations.stream().anyMatch(
        violation -> violation.getPropertyPath().toString().equals("basketReference")
            && violation.getMessage()
            .equals("must not be empty")));
    assertTrue(violations.stream().anyMatch(
        violation -> violation.getPropertyPath().toString().equals("hotelId")
            && violation.getMessage()
            .equals("must not be null")));
    assertTrue(violations.stream().anyMatch(
        violation -> violation.getPropertyPath().toString().equals("reasonCode")
            && violation.getMessage()
            .equals("must not be empty")));
    assertTrue(violations.stream().anyMatch(
        violation -> violation.getPropertyPath().toString().equals("reasonName")
            && violation.getMessage()
            .equals("must not be empty")));
    assertTrue(violations.stream().anyMatch(
        violation -> violation.getPropertyPath().toString().equals("callerName")
            && violation.getMessage()
            .equals("must not be empty")));
  }

}
