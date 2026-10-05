package uk.co.whitbread.rules.agent.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.co.whitbread.rules.agent.domain.model.validation.ValidatorFactory;

class RbacRuleRequestSelfValidationTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());

  @Test
  void constructor_emptyRoleId_shouldSelfValidateAndThrow() {
    String expectedMessage = "roleId: must not be empty";

    checkErrorThrown(RbacRuleRequest.builder().roleId("").resourceId("CCUI_RES")::build,
        expectedMessage);
  }

  @Test
  void constructor_emptyResourceId_shouldSelfValidateAndThrow() {
    String expectedMessage = "resourceId: must not be empty";

    checkErrorThrown(() -> RbacRuleRequest.builder().roleId("AGENT").resourceId("").build(),
        expectedMessage);
  }

  @Test
  void constructor__shouldSelfValidateOk() {

    assertDoesNotThrow(() -> {
      RbacRuleRequest.builder()
          .resourceId("CCUI_RES")
          .roleId("AGENT")
          .build();
    });
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {

    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }
}
