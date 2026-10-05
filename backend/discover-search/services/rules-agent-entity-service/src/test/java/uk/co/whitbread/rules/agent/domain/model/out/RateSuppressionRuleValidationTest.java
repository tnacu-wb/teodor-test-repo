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

class RateSuppressionRuleValidationTest {

  private static final LocalDateTime TIME = LocalDateTime.now();

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());

  @Test
  void constructor_nullRateSuppressionList_shouldSelfValidateAndThrow() {
    String expectedMessage = "rateType: must not be null";

    checkErrorThrown(() -> createValidRateSuppressionRule().toBuilder()
        .rateType(null).build(), expectedMessage);
  }

  @Test
  void constructor_nullgeneratedAt_shouldSelfValidateAndThrow() {
    String expectedMessage = "priority: must not be null";

    checkErrorThrown(() -> createValidRateSuppressionRule()
            .toBuilder()
            .priority(null)
            .build(),
        expectedMessage);
  }

  @Test
  void validate__shouldValidateOk() {
    assertDoesNotThrow(() -> createValidRateSuppressionRule().validateSelf());
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }


  RateSuppressionRule createValidRateSuppressionRule() {
    return RateSuppressionRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(LocalDateTime.now())
        .lastModifiedAt(TIME)
        .rateType("FLEX")
        .priority((short) 10)
        .build();
  }
}