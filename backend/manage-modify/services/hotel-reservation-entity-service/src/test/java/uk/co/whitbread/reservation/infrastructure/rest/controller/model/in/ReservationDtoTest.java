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
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.ReservationDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.reservation.model.in.RoomRateDto;

@Slf4j
public class ReservationDtoTest {

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
    var reservationDto = ReservationDto.builder()
        .adultsNumber(2)
        .arrival(LocalDate.parse("2026-10-10"))
        .basketReferenceId("123456")
        .childrenNumber(0)
        .departure(LocalDate.parse("2026-10-12"))
        .hotelId("LONSTM")
        .roomRates(RoomRateDto.builder()
            .endDate(LocalDate.parse("2026-10-12"))
            .rateDisplaySet("NEG")
            .ratePlanCode("FLEXRATE")
            .pmsRoomType("DB")
            .startDate(LocalDate.parse("2026-10-10"))
            .build())
        .bookingNotes("bookingNotes")
        .gdsReferenceNumber("gdsReferenceNumber")
        .distributionUsername("distributionUsername")
        .distributionIATANumber("00000000123456")
        .build();

    Set<ConstraintViolation<ReservationDto>> violations = validator.validate(reservationDto);
    assertTrue(violations.isEmpty());
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSet() {
    var reservationDto = ReservationDto.builder()
        .adultsNumber(0)
        .basketReferenceId("123456")
        .childrenNumber(0)
        .hotelId("")
        .build();

    Set<ConstraintViolation<ReservationDto>> violations = validator.validate(reservationDto);

    assertEquals(6, violations.size());
    assertTrue(violations.stream().anyMatch(
        violation -> violation.getPropertyPath().toString().equals("arrival")
            && violation.getMessage().equals("must not be null")));
    assertTrue(violations.stream().anyMatch(
        violation -> violation.getPropertyPath().toString().equals("departure")
            && violation.getMessage().equals("must not be null")));
    assertTrue(violations.stream().anyMatch(
        violation -> violation.getPropertyPath().toString().equals("hotelId")
            && violation.getMessage().equals("must not be empty")));
    assertTrue(violations.stream().anyMatch(
        violation -> violation.getPropertyPath().toString().equals("roomRates")
            && violation.getMessage().equals("must not be null")));
    assertTrue(violations.stream().anyMatch(
        violation -> violation.getPropertyPath().toString().equals("adultsNumber")
            && violation.getMessage().equals("must be greater than or equal to 1")));
  }

  @Test
  void verifyHotelIdPatternViolations() {
    // invalid hotelId values: numeric-only, mixed alphanumeric, special char, whitespace, too short, too long
    String[] invalidHotelIds = {"123456", "LON12M", "LON$TM", "LON TM", "AB", "ABCDEFGH"};

    for (String hotelId : invalidHotelIds) {
      var reservationDto = ReservationDto.builder()
          .adultsNumber(2)
          .arrival(LocalDate.parse("2026-10-10"))
          .departure(LocalDate.parse("2026-10-12"))
          .basketReferenceId("123456")
          .childrenNumber(0)
          .hotelId(hotelId)
          .roomRates(RoomRateDto.builder()
              .endDate(LocalDate.parse("2026-10-12"))
              .rateDisplaySet("NEG")
              .ratePlanCode("FLEXRATE")
              .pmsRoomType("DB")
              .startDate(LocalDate.parse("2026-10-10"))
              .build())
          .build();

      Set<ConstraintViolation<ReservationDto>> violations = validator.validate(reservationDto);

      assertTrue(violations.stream().anyMatch(
          violation -> violation.getPropertyPath().toString().equals("hotelId")
              && violation.getMessage().equals("Hotel Id must be exactly 6 alphabetic characters long")),
          "Expected pattern violation for hotelId='" + hotelId + "' but got: " + violations);
    }
  }

}
