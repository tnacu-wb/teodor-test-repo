package uk.co.whitbread.rules.agent.domain.model.out;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.LocalDateTime;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.co.whitbread.rules.agent.domain.model.validation.ValidatorFactory;

class MaxNightsRuleValidationTest {

  private static final LocalDateTime TIME = LocalDateTime.now();

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());

  @Test
  void validate_nullRuleId_shouldValidateAndThrow() {
    String expectedMessage = "ruleId: must not be null";
    var maxNightsRule = createValidMaxNightsRule();
    maxNightsRule.setRuleId(null);

    checkErrorThrown(maxNightsRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullStatus_shouldValidateAndThrow() {
    String expectedMessage = "status: must not be null";
    var maxNightsRule = createValidMaxNightsRule();
    maxNightsRule.setStatus(null);

    checkErrorThrown(maxNightsRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullCreatedAt_shouldValidateAndThrow() {
    String expectedMessage = "createdAt: must not be null";
    var maxNightsRule = createValidMaxNightsRule();
    maxNightsRule.setCreatedAt(null);

    checkErrorThrown(maxNightsRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullLastModifiedAt_shouldValidateAndThrow() {
    String expectedMessage = "lastModifiedAt: must not be null";
    var maxNightsRule = createValidMaxNightsRule();
    maxNightsRule.setLastModifiedAt(null);

    checkErrorThrown(maxNightsRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullMaxNights_shouldValidateAndThrow() {
    String expectedMessage = "maxNights: must not be null";

    checkErrorThrown(() -> createValidMaxNightsRule().toBuilder().maxNights(null).build(),
        expectedMessage);
  }

  @Test
  void validate_emptyChannelId_shouldValidateAndThrow() {
    String expectedMessage = "channelId: must not be empty";

    checkErrorThrown(() -> createValidMaxNightsRule().toBuilder().channelId(null).build(),
        expectedMessage);
  }

  @Test
  void validate__shouldValidateOk() {
    assertDoesNotThrow(() -> createValidMaxNightsRule().validateSelf());
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

  MaxNightsRule createValidMaxNightsRule() {
    return MaxNightsRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(LocalDateTime.now())
        .lastModifiedAt(TIME)
        .channelId("PI")
        .maxNights(9)
        .build();
  }
}

