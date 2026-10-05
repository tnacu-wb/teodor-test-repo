package uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.model.in;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
  void verifyHotelIdPatternViolations() {
    // invalid hotelId values: numeric-only, mixed alphanumeric, special char, whitespace, too short, too long
    String[] invalidHotelIds = {"123456", "LON12M", "LON$TM", "LON TM", "AB", "ABCDEFGH"};

    for (String hotelId : invalidHotelIds) {

      ReservationDto reservationDto = new ReservationDto();
      reservationDto.setAdults(2);
      reservationDto.setArrival("2026-10-10");
      reservationDto.setDeparture("2026-10-12");
      reservationDto.setChildren(0);
      reservationDto.setHotelId(hotelId);

      RoomRateDto roomRate = new RoomRateDto();
      roomRate.setEnd("2026-10-12");
      roomRate.setStart("2026-10-10");
      roomRate.setRatePlanCode("FLEXRATE");
      roomRate.setRoomType("DB");

      reservationDto.setRoomRates(roomRate);

      Set<ConstraintViolation<ReservationDto>> violations = validator.validate(reservationDto);

      assertTrue(violations.stream().anyMatch(
              violation -> violation.getPropertyPath().toString().equals("hotelId")
                      && violation.getMessage().equals("Hotel Id must be exactly 6 alphabetic characters long")),
              "Expected pattern violation for hotelId='" + hotelId + "' but got: " + violations);
    }
  }


}
