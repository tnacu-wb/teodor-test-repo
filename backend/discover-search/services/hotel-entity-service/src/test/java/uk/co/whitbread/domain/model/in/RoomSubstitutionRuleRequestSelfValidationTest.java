package uk.co.whitbread.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.co.whitbread.domain.model.rulesagent.in.RoomSubstitutionRuleRequest;

class RoomSubstitutionRuleRequestSelfValidationTest {

  @Test
  void constructor_nullAdults_shouldSelfValidateAndThrow() {
    String expectedMessage = "adults: must not be null";

    checkErrorThrown(() -> RoomSubstitutionRuleRequest.builder()
        .children(0)
        .roomType("DB")
        .pms("OP")
        .channel("PI")
        .build(), expectedMessage
    );
  }

  @Test
  void constructor_nullChildren_shouldSelfValidateAndThrow() {
    String expectedMessage = "children: must not be null";

    checkErrorThrown(() -> RoomSubstitutionRuleRequest.builder()
        .adults(1)
        .roomType("DB")
        .pms("OP")
        .channel("PI")
        .build(), expectedMessage
    );
  }

  @Test
  void constructor_emptyRoomType_shouldSelfValidateAndThrow() {
    String expectedMessage = "roomType: must not be empty";

    checkErrorThrown(() -> RoomSubstitutionRuleRequest.builder()
        .adults(1)
        .children(0)
        .roomType("")
        .pms("OP")
        .channel("PI")
        .build(), expectedMessage
    );
  }

  @Test
  void constructor_emptyPms_shouldSelfValidateAndThrow() {
    String expectedMessage = "pms: must not be empty";

    checkErrorThrown(() -> RoomSubstitutionRuleRequest.builder()
        .adults(1)
        .children(0)
        .roomType("DB")
        .channel("PI")
        .pms("")
        .build(), expectedMessage
    );
  }

  @Test
  void constructor_emptyChannel_shouldSelfValidateAndThrow() {
    String expectedMessage = "channel: must not be empty";

    checkErrorThrown(() -> RoomSubstitutionRuleRequest.builder()
        .adults(1)
        .children(0)
        .roomType("DB")
        .channel("")
        .pms("OP")
        .build(), expectedMessage
    );
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }
}
