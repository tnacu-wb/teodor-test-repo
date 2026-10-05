package uk.co.whitbread.reservation.infrastructure.rest.controller.model.in;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.Set;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.RoomRateDto;

@Slf4j
public class RoomRateDtoTest {

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
    var roomRateDto = RoomRateDto.builder()
        .endDate(LocalDate.parse("2026-10-12"))
        .ratePlanCode("FLEXRATE")
        .rateDisplaySet("NEG")
        .pmsRoomType("DB")
        .startDate(LocalDate.parse("2026-10-10"))
        .build();

    Set<ConstraintViolation<RoomRateDto>> violations = validator.validate(roomRateDto);
    assertTrue(violations.isEmpty());
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSet() {
    var roomRateDto = RoomRateDto.builder()
        .ratePlanCode("")
        .pmsRoomType("")
        .build();

    Set<ConstraintViolation<RoomRateDto>> violations = validator.validate(roomRateDto);

    assertEquals(4, violations.size());
    assertTrue(violations.stream().anyMatch(
        violation -> violation.getPropertyPath().toString().equals("endDate")
            && violation.getMessage().equals("must not be null")));
    assertTrue(violations.stream().anyMatch(
        violation -> violation.getPropertyPath().toString().equals("ratePlanCode")
            && violation.getMessage().equals("must not be empty")));
    assertTrue(violations.stream().anyMatch(
        violation -> violation.getPropertyPath().toString().equals("pmsRoomType")
            && violation.getMessage().equals("must not be empty")));
    assertTrue(violations.stream().anyMatch(
        violation -> violation.getPropertyPath().toString().equals("startDate")
            && violation.getMessage().equals("must not be null")));
  }

}
