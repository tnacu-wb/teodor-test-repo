package uk.co.whitbread.rules.agent.domain.model.out;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class PaypalRuleResponseSelfValidationTest {

  private final LocalDateTime time = LocalDateTime.now();

  @Test
  void constructor_nullIsPayPalPaymentEnabled_shouldSelfValidateAndThrow() {
    String expectedMessage = "isPayPalPaymentEnabled: must not be null";

    checkErrorThrown(() -> PaypalRuleResponse.builder()
        .isPayPalPaymentEnabled(null).generatedAt(time).build(), expectedMessage);
  }

  @Test
  void constructor__shouldSelfValidateOk() {
    assertDoesNotThrow(() -> {
      PaypalRuleResponse.builder()
          .generatedAt(time)

          .isPayPalPaymentEnabled(true)
          .build();
    });
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {

    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }
}
