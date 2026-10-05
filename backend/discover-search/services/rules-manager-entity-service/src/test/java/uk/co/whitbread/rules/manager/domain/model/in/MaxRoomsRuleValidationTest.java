package uk.co.whitbread.rules.manager.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.LocalDateTime;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class MaxRoomsRuleValidationTest {

  private static final LocalDateTime TIME = LocalDateTime.now();

  @Test
  void validate_nullRuleId_shouldValidateAndThrow() {
    String expectedMessage = "ruleId: must not be null";
    var maxRoomsRule = createValidMaxRoomsRule();
    maxRoomsRule.setRuleId(null);

    checkErrorThrown(maxRoomsRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullStatus_shouldValidateAndThrow() {
    String expectedMessage = "status: must not be null";
    var maxRoomsRule = createValidMaxRoomsRule();
    maxRoomsRule.setStatus(null);

    checkErrorThrown(maxRoomsRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullCreatedAt_shouldValidateAndThrow() {
    String expectedMessage = "createdAt: must not be null";
    var maxRoomsRule = createValidMaxRoomsRule();
    maxRoomsRule.setCreatedAt(null);

    checkErrorThrown(maxRoomsRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullLastModifiedAt_shouldValidateAndThrow() {
    String expectedMessage = "lastModifiedAt: must not be null";
    var maxRoomsRule = createValidMaxRoomsRule();
    maxRoomsRule.setLastModifiedAt(null);

    checkErrorThrown(maxRoomsRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullChannelId_shouldValidateAndThrow() {
    String expectedMessage = "channelId: must not be empty";

    checkErrorThrown(
        () -> createValidMaxRoomsRule().toBuilder().channelId(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate_nullMaxRooms_shouldValidateAndThrow() {
    String expectedMessage = "maxRooms: must not be null";

    checkErrorThrown(
        () -> createValidMaxRoomsRule().toBuilder().maxRooms(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate__shouldValidateOk() {
    assertDoesNotThrow(() -> createValidMaxRoomsRule().validateSelf());
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

  MaxRoomsRule createValidMaxRoomsRule() {
    return MaxRoomsRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(TIME)
        .lastModifiedAt(TIME)
        .channelId("channel")
        .maxRooms(9)
        .build();
  }
}
