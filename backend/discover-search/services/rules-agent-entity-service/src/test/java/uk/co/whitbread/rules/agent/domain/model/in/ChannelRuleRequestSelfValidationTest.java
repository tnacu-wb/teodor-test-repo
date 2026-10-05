package uk.co.whitbread.rules.agent.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class ChannelRuleRequestSelfValidationTest {

  @Test
  void constructor_emptyChannel_shouldSelfValidateAndThrow() {
    String expectedMessage = "channel: must not be empty";

    checkErrorThrown(() -> ChannelRuleRequest.builder()
        .channel("")
        .subchannel("WEB")
        .language("EN")
        .pms("OP")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptySubChannel_shouldSelfValidateAndThrow() {
    String expectedMessage = "subchannel: must not be empty";

    checkErrorThrown(() -> ChannelRuleRequest.builder()
        .channel("PI")
        .subchannel("")
        .language("EN")
        .pms("OP")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyPms_shouldSelfValidateAndThrow() {
    String expectedMessage = "pms: must not be empty";

    checkErrorThrown(() -> ChannelRuleRequest.builder()
        .channel("PI")
        .subchannel("WEB")
        .language("EN")
        .pms("")
        .build(), expectedMessage);
  }

  @Test
  void constructor__shouldSelfValidateOk() {
    assertDoesNotThrow(() -> {
      ChannelRuleRequest.builder()
          .channel("PI")
          .subchannel("WEB")
          .language("EN")
          .pms("OP")
          .build();
    });
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }
}
