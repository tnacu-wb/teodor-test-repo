package uk.co.whitbread.rules.manager.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.LocalDateTime;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class RateSuppressionRuleValidationTest {

  private static final LocalDateTime TIME = LocalDateTime.now();

  @Test
  void validate_nullRateType_shouldValidateAndThrow() {
    String expectedMessage = "rateType: must not be null";

    checkErrorThrown(
        () -> createValidSuppressionRule().toBuilder().rateType(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate_nullPriority_shouldValidateAndThrow() {
    String expectedMessage = "priority: must not be null";

    checkErrorThrown(
        () -> createValidSuppressionRule().toBuilder().priority(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate__shouldValidateOk() {
    assertDoesNotThrow(() -> createValidSuppressionRule().validateSelf());
  }

  private RateSuppressionRule createValidSuppressionRule() {
    return RateSuppressionRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(TIME)
        .lastModifiedAt(TIME)
        .rateType("STANDARD")
        .priority((short) 30)
        .build();
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

}
