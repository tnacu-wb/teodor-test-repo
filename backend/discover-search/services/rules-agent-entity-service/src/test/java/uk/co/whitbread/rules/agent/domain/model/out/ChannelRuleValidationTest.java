package uk.co.whitbread.rules.agent.domain.model.out;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.co.whitbread.rules.agent.domain.model.validation.ValidatorFactory;

class ChannelRuleValidationTest {

  private static final LocalDateTime TIME = LocalDateTime.now();

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());

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
  void validate_emptyChannel_shouldValidateAndThrow() {
    String expectedMessage = "channel: must not be empty";

    checkErrorThrown(() -> createValidChannelRule().toBuilder().channel("").build(),
        expectedMessage);
  }

  @Test
  void validate_emptySubChannel_shouldValidateAndThrow() {
    String expectedMessage = "subchannel: must not be empty";

    checkErrorThrown(() -> createValidChannelRule().toBuilder().subchannel("").build(),
        expectedMessage);
  }

  @Test
  void validate_emptyLanguage_shouldValidateAndThrow() {
    String expectedMessage = "language: must not be empty";

    checkErrorThrown(() -> createValidChannelRule().toBuilder().language("").build(),
        expectedMessage);
  }

  @Test
  void validate_emptyPms_shouldValidateAndThrow() {
    String expectedMessage = "pms: must not be empty";

    checkErrorThrown(() -> createValidChannelRule().toBuilder().pms("").build(),
        expectedMessage);
  }

  @Test
  void validate_emptySourceId_shouldValidateAndThrow() {
    String expectedMessage = "sourceId: must not be empty";

    checkErrorThrown(() -> createValidChannelRule().toBuilder().sourceId("").build(),
        expectedMessage);
  }

  @Test
  void validate_emptyRatePlanSets_shouldValidateAndThrow() {
    String expectedMessage = "ratePlanSets: must not be empty";

    checkErrorThrown(() -> createValidChannelRule().toBuilder().ratePlanSets(emptyList()).build(),
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
        .ratePlanSets(List.of("PBF"))
        .build();
  }
}
