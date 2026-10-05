package uk.co.whitbread.rules.agent.domain.model.out;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import jakarta.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class OccupancySupplementSelfValidationTest {


  @Test
  void constructor_nullPricing_shouldSelfValidateAndThrow() {
    String expectedMessage = "pricing: must not be null";

    checkErrorThrown(() -> OccupancySupplement.builder()
        .ruleId(1)
        .lastModifiedAt(LocalDateTime.now())
        .createdAt(LocalDateTime.now())
        .status(RuleStatus.ACTIVE)
        .hotelId("FRAMTI")
        .pricing(null)
        .build(), expectedMessage);
  }

  @Test
  void constructor__shouldSelfValidateOk() {
    assertDoesNotThrow(() -> {
      OccupancySupplement.builder()
          .ruleId(1)
          .lastModifiedAt(LocalDateTime.now())
          .createdAt(LocalDateTime.now())
          .status(RuleStatus.ACTIVE)
          .pricing(BigDecimal.ONE)
          .hotelId("FRAMTI")
          .build();
    });
  }


  private void checkErrorThrown(Executable executable, String expectedMessage) {

    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }
}