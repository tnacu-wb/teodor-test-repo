package uk.co.whitbread.rules.agent.domain.model.out;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.co.whitbread.rules.agent.domain.model.validation.ValidatorFactory;

class RateSuppressionRuleResponseSelfValidationTest {

  private static final LocalDateTime TIME = LocalDateTime.now();

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());

  @Test
  void constructor_nullRateSuppressionList_shouldSelfValidateAndThrow() {
    String expectedMessage = "rateSuppressionList: must not be empty";

    checkErrorThrown(() -> RateSuppressionRuleResponse.builder()
        .generatedAt(TIME)
        .expiryDate(TIME.plusDays(1)).build(), expectedMessage);
  }

  @Test
  void constructor_nullgeneratedAt_shouldSelfValidateAndThrow() {
    String expectedMessage = "generatedAt: must not be null";

    checkErrorThrown(() -> RateSuppressionRuleResponse.builder()
        .rateSuppressionList(List.of("FLEX", "SEMI-FLEX"))
        .expiryDate(TIME.plusDays(1)).build(), expectedMessage);
  }

  @Test
  void constructor_nullExpiryDate_shouldSelfValidateAndThrow() {
    String expectedMessage = "expiryDate: must not be null";

    checkErrorThrown(() -> RateSuppressionRuleResponse.builder()
        .rateSuppressionList(List.of("FLEX", "SEMI-FLEX", "ADVANCE", "STANDARD", "NON-FLEX"))
        .generatedAt(TIME)
        .build(), expectedMessage);
  }

  @Test
  void constructor__shouldSelfValidateOk() {
    assertDoesNotThrow(() -> {
      RateSuppressionRuleResponse.builder()
          .rateSuppressionList(List.of("FLEX", "SEMI-FLEX", "ADVANCE", "STANDARD", "NON-FLEX"))
          .generatedAt(TIME)
          .expiryDate(TIME.plusDays(1))
          .build();
    });
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }
}