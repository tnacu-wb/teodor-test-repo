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
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.BookingCardInformationRequestDto;

@Slf4j
public class BookingCardInformationRequestDtoTest {

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
    var bookingCardInformationRequestDto = BookingCardInformationRequestDto.builder()
        .bookingReference("AFTR745756")
        .bookerLastName("Doe")
        .arrivalDate("2023-01-23")
        .build();

    Set<ConstraintViolation<BookingCardInformationRequestDto>> violations = validator.validate(
        bookingCardInformationRequestDto);
    assertTrue(violations.isEmpty());
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSet() {
    var bookingCardInformationRequestDto = BookingCardInformationRequestDto.builder()
        .build();

    Set<ConstraintViolation<BookingCardInformationRequestDto>> violations = validator.validate(
        bookingCardInformationRequestDto);

    assertEquals(3, violations.size());
    assertTrue(violations.stream().anyMatch(
        violation -> violation.getPropertyPath().toString().equals("bookingReference")
            && violation.getMessage().equals("must not be empty")));
    assertTrue(violations.stream().anyMatch(
        violation -> violation.getPropertyPath().toString().equals("bookerLastName")
            && violation.getMessage().equals("must not be empty")));
    assertTrue(violations.stream().anyMatch(
        violation -> violation.getPropertyPath().toString().equals("arrivalDate")
            && violation.getMessage().equals("must not be empty")));
  }

}
