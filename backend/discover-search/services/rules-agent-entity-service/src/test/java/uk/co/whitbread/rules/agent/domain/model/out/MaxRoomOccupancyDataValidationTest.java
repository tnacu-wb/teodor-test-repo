package uk.co.whitbread.rules.agent.domain.model.out;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.util.Arrays;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.co.whitbread.rules.agent.domain.model.validation.ValidatorFactory;

class MaxRoomOccupancyDataValidationTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());

  @Test
  void constructor_nullAdults_shouldSelfValidateAndThrow() {
    String expectedMessage = "adultsNumber: must not be null";

    checkErrorThrown(() -> MaxRoomOccupancyData.builder()
            .adultsNumber(null).childrenNumber(0).acceptedRoomTypes(Arrays.asList("DB", "SB", "DIS"))
            .build(),
        expectedMessage);
  }

  @Test
  void constructor_nullChildren_shouldSelfValidateAndThrow() {
    String expectedMessage = "childrenNumber: must not be null";

    checkErrorThrown(() -> MaxRoomOccupancyData.builder()
            .adultsNumber(1).childrenNumber(null).acceptedRoomTypes(Arrays.asList("DB", "SB", "DIS"))
            .build(),
        expectedMessage);
  }

  @Test
  void constructor_nullRooms_shouldSelfValidateAndThrow() {
    String expectedMessage = "acceptedRoomTypes: must not be null";

    checkErrorThrown(() -> MaxRoomOccupancyData.builder()
            .adultsNumber(1).childrenNumber(0).acceptedRoomTypes(null)
            .build(),
        expectedMessage);
  }

  @Test
  void constructor__shouldSelfValidateOk() {

    assertDoesNotThrow(() -> {
      MaxRoomOccupancyData.builder()
          .adultsNumber(1)
          .childrenNumber(0)
          .acceptedRoomTypes(Arrays.asList("DB", "SB", "DIS"))
          .build();
    });
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {

    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

}
