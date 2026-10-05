package uk.co.whitbread.rules.agent.domain.model.out;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.co.whitbread.rules.agent.domain.model.validation.ValidatorFactory;

class OccupancySupplementValidationTest {

  private static final LocalDateTime TIME = LocalDateTime.now();

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());

  @Test
  void validate_nullRuleId_shouldValidateAndThrow() {
    String expectedMessage = "ruleId: must not be null";
    var occupancySupplementRule = createValidOccupancySupplementRule();
    occupancySupplementRule.setRuleId(null);

    checkErrorThrown(occupancySupplementRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullStatus_shouldValidateAndThrow() {
    String expectedMessage = "status: must not be null";
    var occupancySupplementRule = createValidOccupancySupplementRule();
    occupancySupplementRule.setStatus(null);

    checkErrorThrown(occupancySupplementRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullCreatedAt_shouldValidateAndThrow() {
    String expectedMessage = "createdAt: must not be null";
    var occupancySupplementRule = createValidOccupancySupplementRule();
    occupancySupplementRule.setCreatedAt(null);

    checkErrorThrown(occupancySupplementRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullLastModifiedAt_shouldValidateAndThrow() {
    String expectedMessage = "lastModifiedAt: must not be null";
    var occupancySupplementRule = createValidOccupancySupplementRule();
    occupancySupplementRule.setLastModifiedAt(null);

    checkErrorThrown(occupancySupplementRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullPricing_shouldValidateAndThrow() {
    String expectedMessage = "pricing: must not be null";

    checkErrorThrown(() -> createValidOccupancySupplementRule().toBuilder().pricing(null).build(),
        expectedMessage);
  }

  @Test
  void validate_emptyHotelId_shouldValidateAndThrow() {
    String expectedMessage = "hotelId: must not be empty";

    checkErrorThrown(() -> createValidOccupancySupplementRule().toBuilder().hotelId("").build(),
        expectedMessage);
  }

  @Test
  void validate__shouldValidateOk() {
    assertDoesNotThrow(() -> createValidOccupancySupplementRule().validateSelf());
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

  OccupancySupplement createValidOccupancySupplementRule() {
    return OccupancySupplement.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(LocalDateTime.now())
        .lastModifiedAt(TIME)
        .hotelId("testHotel")
        .pricing(BigDecimal.TEN)
        .build();
  }
}
