package uk.co.whitbread.rules.agent.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class MaxArrivalDateRuleRequestSelfValidationTest {

  @Test
  void constructor_emptyChannelId_shouldSelfValidateAndThrow() {
    String expectedMessage = "channelId: must not be empty";

    checkErrorThrown(MaxArrivalDateRuleRequest.builder().channelId("")::build,
        expectedMessage);
  }

  @Test
  void constructor__shouldSelfValidateOk() {

    assertDoesNotThrow(() -> {
      MaxArrivalDateRuleRequest.builder()
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
