package uk.co.whitbread.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.util.List;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityRequest;
import uk.co.whitbread.domain.model.validation.ValidatorFactory;

class HotelAvailabilityRequestSelfValidationTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
          Validation.buildDefaultValidatorFactory()
                  .getValidator());
  @Test
  void constructor_emptyHotelId_shouldSelfValidateAndThrow() {
    String expectedMessage = "hotelId: must not be null";

    checkErrorThrown(() -> HotelAvailabilityRequest.builder()
        .adultsNumber(List.of(1))
        .roomTypes(List.of("DB"))
        .departureDate("2022-02-06")
        .arrivalDate("2022-02-02")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyAdultsNumber_shouldSelfValidateAndThrow() {
    String expectedMessage = "adultsNumber: must not be empty";

    checkErrorThrown(() -> HotelAvailabilityRequest.builder()
        .roomTypes(List.of("DB"))
        .hotelId("LONEUS")
        .departureDate("2022-02-06")
        .arrivalDate("2022-02-02")
        .build(), expectedMessage);
  }

  @Test
  void constructor_shouldSelfValidateOk() {
    assertDoesNotThrow(() -> {
      HotelAvailabilityRequest.builder()
          .adultsNumber(List.of(1))
          .roomTypes(List.of("DB"))
          .hotelId("LONEUS")
          .departureDate("2022-02-06")
          .arrivalDate("2022-02-02")
          .build();
    });
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

}
