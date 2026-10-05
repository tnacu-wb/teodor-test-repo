package uk.co.whitbread.rules.manager.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class BaseRateRuleValidationTest {

  private static final LocalDateTime TIME = LocalDateTime.now();

  @Test
  void validate_nullRuleId_shouldValidateAndThrow() {
    String expectedMessage = "ruleId: must not be null";
    var baseRateRule = createBaseRateRule();
    baseRateRule.setRuleId(null);

    checkErrorThrown(baseRateRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullStatus_shouldValidateAndThrow() {
    String expectedMessage = "status: must not be null";
    var baseRateRule = createBaseRateRule();
    baseRateRule.setStatus(null);

    checkErrorThrown(baseRateRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullCreatedAt_shouldValidateAndThrow() {
    String expectedMessage = "createdAt: must not be null";
    var baseRateRule = createBaseRateRule();
    baseRateRule.setCreatedAt(null);

    checkErrorThrown(baseRateRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullLastModifiedAt_shouldValidateAndThrow() {
    String expectedMessage = "lastModifiedAt: must not be null";
    var baseRateRule = createBaseRateRule();
    baseRateRule.setLastModifiedAt(null);

    checkErrorThrown(baseRateRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullBaseRate_shouldValidateAndThrow() {
    String expectedMessage = "baseRate: must not be empty";

    checkErrorThrown(
        () -> createBaseRateRule().toBuilder().baseRate(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate__shouldValidateOk() {

    assertDoesNotThrow(() -> createBaseRateRule().validateSelf());
  }

  BaseRateRule createBaseRateRule() {

    return BaseRateRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(LocalDateTime.now())
        .lastModifiedAt(TIME)
        .baseRate("FLEXRATE")
        .promoCode("BUSIFLEX")
        .ratePlanCode("BUSIFLEX")
        .build();
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }
}
