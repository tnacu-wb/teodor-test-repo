package uk.co.whitbread.rules.manager.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.LocalDateTime;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class RbacRuleValidationTest {

  private static final LocalDateTime TIME = LocalDateTime.now();

  @Test
  void validate_nullRuleId_shouldValidateAndThrow() {
    String expectedMessage = "ruleId: must not be null";
    var rbacRule = createValidRbacRule();
    rbacRule.setRuleId(null);

    checkErrorThrown(rbacRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullStatus_shouldValidateAndThrow() {
    String expectedMessage = "status: must not be null";
    var rbacRule = createValidRbacRule();
    rbacRule.setStatus(null);

    checkErrorThrown(rbacRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullCreatedAt_shouldValidateAndThrow() {
    String expectedMessage = "createdAt: must not be null";
    var rbacRule = createValidRbacRule();
    rbacRule.setCreatedAt(null);

    checkErrorThrown(rbacRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullLastModifiedAt_shouldValidateAndThrow() {
    String expectedMessage = "lastModifiedAt: must not be null";
    var rbacRule = createValidRbacRule();
    rbacRule.setLastModifiedAt(null);

    checkErrorThrown(rbacRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullResourceId_shouldValidateAndThrow() {
    String expectedMessage = "resourceId: must not be empty";

    checkErrorThrown(
        () -> createValidRbacRule().toBuilder().resourceId(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate_nullRoleId_shouldValidateAndThrow() {
    String expectedMessage = "roleId: must not be empty";

    checkErrorThrown(() -> createValidRbacRule().toBuilder().roleId(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate_nullHasAccess_shouldValidateAndThrow() {
    String expectedMessage = "hasAccess: must not be null";

    checkErrorThrown(() -> createValidRbacRule().toBuilder().hasAccess(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate__shouldValidateOk() {
    assertDoesNotThrow(() -> createValidRbacRule().validateSelf());
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

  RbacRule createValidRbacRule() {
    return RbacRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(LocalDateTime.now())
        .lastModifiedAt(TIME)
        .resourceId("resid")
        .roleId("role")
        .hasAccess(true)
        .build();
  }
}
