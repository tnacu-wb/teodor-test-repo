package uk.co.whitbread.rules.manager.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.LocalDateTime;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class BusinessAllowanceRuleValidationTest {

  private static final LocalDateTime TIME = LocalDateTime.now();

  @Test
  void validate_nullRuleId_shouldValidateAndThrow() {
    String expectedMessage = "ruleId: must not be null";
    var businessAllowanceRule = createValidBusinessAllowanceRule();
    businessAllowanceRule.setRuleId(null);

    checkErrorThrown(businessAllowanceRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullStatus_shouldValidateAndThrow() {
    String expectedMessage = "status: must not be null";
    var businessAllowanceRule = createValidBusinessAllowanceRule();
    businessAllowanceRule.setStatus(null);

    checkErrorThrown(businessAllowanceRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullCreatedAt_shouldValidateAndThrow() {
    String expectedMessage = "createdAt: must not be null";
    var businessAllowanceRule = createValidBusinessAllowanceRule();
    businessAllowanceRule.setCreatedAt(null);

    checkErrorThrown(businessAllowanceRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullLastModifiedAt_shouldValidateAndThrow() {
    String expectedMessage = "lastModifiedAt: must not be null";
    var businessAllowanceRule = createValidBusinessAllowanceRule();
    businessAllowanceRule.setLastModifiedAt(null);

    checkErrorThrown(businessAllowanceRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullPms_shouldValidateAndThrow() {
    String expectedMessage = "pms: must not be empty";

    checkErrorThrown(
        () -> createValidBusinessAllowanceRule().toBuilder().pms(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate_nullSourceId_shouldValidateAndThrow() {
    String expectedMessage = "sourceId: must not be empty";

    checkErrorThrown(
        () -> createValidBusinessAllowanceRule().toBuilder().sourceId(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate_nullTargetId_shouldValidateAndThrow() {
    String expectedMessage = "targetId: must not be empty";

    checkErrorThrown(
        () -> createValidBusinessAllowanceRule().toBuilder().targetId(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate__shouldValidateOk() {
    assertDoesNotThrow(() -> createValidBusinessAllowanceRule().validateSelf());
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

  BusinessAllowanceRule createValidBusinessAllowanceRule() {
    return BusinessAllowanceRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(TIME)
        .lastModifiedAt(TIME)
        .pms("OP")
        .sourceId("dinner")
        .targetId("156")
        .aemId("dinner")
        .isApplicableDaily(true)
        .build();
  }
}
