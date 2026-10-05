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
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.LinkReservationToLeisureCustomerRequestDto;


@Slf4j
public class LinkReservationToLeisureCustomerRequestDtoTest {

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
    var linkReservationToLeisureCustomerRequestDto =
        LinkReservationToLeisureCustomerRequestDto.builder()
            .basketReference("BKS-1234a567-qq7b-2024-123s-aa5ssbn557gh")
            .customerAccountId("CUST-12234")
            .build();

    Set<ConstraintViolation<LinkReservationToLeisureCustomerRequestDto>> violations = validator.validate(
        linkReservationToLeisureCustomerRequestDto);
    assertTrue(violations.isEmpty());
  }

  @Test
  void verifyErrorMessageWhenCustomerAccountIdIsNotSet() {
    var linkReservationToLeisureCustomerRequestDto =
        LinkReservationToLeisureCustomerRequestDto.builder()
            .basketReference("BKS-1234a567-qq7b-2024-123s-aa5ssbn557gh")
            .build();

    Set<ConstraintViolation<LinkReservationToLeisureCustomerRequestDto>> violations = validator.validate(
        linkReservationToLeisureCustomerRequestDto);

    assertEquals(1, violations.size());
    assertTrue(violations.stream().anyMatch(
        violation -> violation.getPropertyPath().toString().equals("customerAccountId")
            && violation.getMessage()
            .equals("must not be empty")));
  }

  @Test
  void verifyErrorMessageWhenBasketReferenceIsNotSet() {
    var linkReservationToLeisureCustomerRequestDto =
        LinkReservationToLeisureCustomerRequestDto.builder()
            .customerAccountId("CUST-12234")
            .build();

    Set<ConstraintViolation<LinkReservationToLeisureCustomerRequestDto>> violations = validator.validate(
        linkReservationToLeisureCustomerRequestDto);

    assertEquals(1, violations.size());
    assertTrue(violations.stream().anyMatch(
        violation -> violation.getPropertyPath().toString().equals("basketReference")
            && violation.getMessage()
            .equals("must not be empty")));
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSet() {
    var linkReservationToLeisureCustomerRequestDto =
        LinkReservationToLeisureCustomerRequestDto.builder().build();

    Set<ConstraintViolation<LinkReservationToLeisureCustomerRequestDto>> violations = validator.validate(
        linkReservationToLeisureCustomerRequestDto);

    assertEquals(2, violations.size());
    assertTrue(violations.stream().anyMatch(
        violation -> violation.getPropertyPath().toString().equals("basketReference")
            && violation.getMessage()
            .equals("must not be empty")));
    assertTrue(violations.stream().anyMatch(
        violation -> violation.getPropertyPath().toString().equals("customerAccountId")
            && violation.getMessage()
            .equals("must not be empty")));
  }
}
