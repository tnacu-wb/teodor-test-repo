package uk.co.whitbread.rules.manager.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class ChannelRuleValidationTest {

  private static final LocalDateTime TIME = LocalDateTime.now();

  @Test
  void validate_nullRuleId_shouldValidateAndThrow() {
    String expectedMessage = "ruleId: must not be null";
    var channelRule = createValidChannelRule();
    channelRule.setRuleId(null);

    checkErrorThrown(channelRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullStatus_shouldValidateAndThrow() {
    String expectedMessage = "status: must not be null";
    var channelRule = createValidChannelRule();
    channelRule.setStatus(null);

    checkErrorThrown(channelRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullCreatedAt_shouldValidateAndThrow() {
    String expectedMessage = "createdAt: must not be null";
    var channelRule = createValidChannelRule();
    channelRule.setCreatedAt(null);

    checkErrorThrown(channelRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullLastModifiedAt_shouldValidateAndThrow() {
    String expectedMessage = "lastModifiedAt: must not be null";
    var channelRule = createValidChannelRule();
    channelRule.setLastModifiedAt(null);

    checkErrorThrown(channelRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullChannel_shouldValidateAndThrow() {
    String expectedMessage = "channel: must not be empty";

    checkErrorThrown(
        () -> createValidChannelRule().toBuilder().channel(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate_nullSubChannel_shouldValidateAndThrow() {
    String expectedMessage = "subchannel: must not be empty";

    checkErrorThrown(
        () -> createValidChannelRule().toBuilder().subchannel(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate_nullLanguage_shouldValidateAndThrow() {
    String expectedMessage = "language: must not be empty";

    checkErrorThrown(
        () -> createValidChannelRule().toBuilder().language(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate_nullPms_shouldValidateAndThrow() {
    String expectedMessage = "pms: must not be empty";

    checkErrorThrown(
        () -> createValidChannelRule().toBuilder().pms(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate__shouldValidateOk() {
    assertDoesNotThrow(() -> createValidChannelRule().validateSelf());
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

  ChannelRule createValidChannelRule() {
    return ChannelRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(LocalDateTime.now())
        .lastModifiedAt(TIME)
        .channel("PI")
        .subchannel("WEB")
        .language("EN")
        .pms("OP")
        .sourceId("11")
        .ratePlanSets(List.of("PBN"))
        .build();
  }
}
