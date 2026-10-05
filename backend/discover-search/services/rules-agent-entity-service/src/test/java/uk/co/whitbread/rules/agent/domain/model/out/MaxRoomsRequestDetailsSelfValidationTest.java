package uk.co.whitbread.rules.agent.domain.model.out;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.co.whitbread.rules.agent.domain.model.validation.ValidatorFactory;

class MaxRoomsRequestDetailsSelfValidationTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());

  @Test
  void constructor_emptyChannelId_shouldSelfValidateAndThrow() {
    String expectedMessage = "channelId: must not be empty";

    checkErrorThrown(() -> MaxRoomsRequestDetails.builder().channelId("").build(),
        expectedMessage);
  }

  @Test
  void constructor__shouldSelfValidateOk() {

    assertDoesNotThrow(() -> {
      MaxRoomsRequestDetails.builder()
          .channelId("CCUI")
          .build();
    });
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {

    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }
}
