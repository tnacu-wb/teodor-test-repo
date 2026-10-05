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

class MaxArrivalDateRuleValidationTest {

  private static final LocalDateTime TIME = LocalDateTime.now();

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());

  @Test
  void validate_nullRuleId_shouldValidateAndThrow() {
    String expectedMessage = "ruleId: must not be null";
    var maxArrivalDateRule = createValidMaxArrivalDateRule();
    maxArrivalDateRule.setRuleId(null);

    checkErrorThrown(maxArrivalDateRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullStatus_shouldValidateAndThrow() {
    String expectedMessage = "status: must not be null";
    var maxArrivalDateRule = createValidMaxArrivalDateRule();
    maxArrivalDateRule.setStatus(null);

    checkErrorThrown(maxArrivalDateRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullCreatedAt_shouldValidateAndThrow() {
    String expectedMessage = "createdAt: must not be null";
    var maxArrivalDateRule = createValidMaxArrivalDateRule();
    maxArrivalDateRule.setCreatedAt(null);

    checkErrorThrown(maxArrivalDateRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullLastModifiedAt_shouldValidateAndThrow() {
    String expectedMessage = "lastModifiedAt: must not be null";
    var maxArrivalDateRule = createValidMaxArrivalDateRule();
    maxArrivalDateRule.setLastModifiedAt(null);

    checkErrorThrown(maxArrivalDateRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullMaxArrivalDate_shouldValidateAndThrow() {
    String expectedMessage = "maxArrivalDate: must not be null";

    checkErrorThrown(() -> createValidMaxArrivalDateRule().toBuilder().maxArrivalDate(null).build(),
        expectedMessage);
  }

  @Test
  void validate_emptyChannelId_shouldValidateAndThrow() {
    String expectedMessage = "channelId: must not be empty";

    checkErrorThrown(() -> createValidMaxArrivalDateRule().toBuilder().channelId(null).build(),
        expectedMessage);
  }

  @Test
  void validate__shouldValidateOk() {
    assertDoesNotThrow(() -> createValidMaxArrivalDateRule().validateSelf());
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

  MaxArrivalDateRule createValidMaxArrivalDateRule() {
    return MaxArrivalDateRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(LocalDateTime.now())
        .lastModifiedAt(TIME)
        .channelId("PI")
        .maxArrivalDate(9)
        .build();
  }
}

