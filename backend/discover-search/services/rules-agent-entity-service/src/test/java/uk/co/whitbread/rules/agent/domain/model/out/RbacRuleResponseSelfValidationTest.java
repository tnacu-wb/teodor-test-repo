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

class RbacRuleResponseSelfValidationTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());

  private final LocalDateTime time = LocalDateTime.now();

  @Test
  void constructor_nulllHasAccess_shouldSelfValidateAndThrow() {
    String expectedMessage = "hasAccess: must not be null";

    checkErrorThrown(() -> RbacRuleResponse.builder()
        .hasAccess(null).generatedAt(time).build(), expectedMessage);
  }

  @Test
  void constructor_nulllGeneratedAt_shouldSelfValidateAndThrow() {
    String expectedMessage = "generatedAt: must not be null";

    checkErrorThrown(() -> RbacRuleResponse.builder()
        .hasAccess(true).generatedAt(null).build(), expectedMessage);
  }

  @Test
  void constructor__shouldSelfValidateOk() {

    assertDoesNotThrow(() -> {
      RbacRuleResponse.builder()
          .generatedAt(time)
          .hasAccess(true)
          .build();
    });
  }


  private void checkErrorThrown(Executable executable, String expectedMessage) {

    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }
}
