package uk.co.whitbread.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.co.whitbread.domain.model.distance.in.DistanceFromSearchRequest;
import uk.co.whitbread.domain.model.validation.ValidatorFactory;

class DistanceFromSearchRequestSelfValidationTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator());

  @Test
  void constructor_emptyLocation_shouldSelfValidateAndThrow() {
    String expectedMessage = "location: must not be empty";

    checkErrorThrown(() -> DistanceFromSearchRequest.builder()
        .hotelId("London")
        .locationFormat("format")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyLocationFormat_shouldSelfValidateAndThrow() {
    String expectedMessage = "locationFormat: must not be empty";

    checkErrorThrown(() -> DistanceFromSearchRequest.builder()
        .hotelId("LONEUS")
        .location("London")
        .build(), expectedMessage);
  }

  @Test
  void constructor_shouldValidateSelfOk() {
    assertDoesNotThrow(() -> {
      DistanceFromSearchRequest.builder()
          .hotelId("London")
          .location("London")
          .locationFormat("format")
          .build();
    });
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

}
