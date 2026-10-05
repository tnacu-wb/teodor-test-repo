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

class AmendmentRuleValidationTest {

  private static final LocalDateTime TIME = LocalDateTime.now();

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());

  @Test
  void validate_nullRuleId_shouldValidateAndThrow() {
    String expectedMessage = "ruleId: must not be null";
    var amendmentRule = createValidAmendmentRule();
    amendmentRule.setRuleId(null);

    checkErrorThrown(amendmentRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullStatus_shouldValidateAndThrow() {
    String expectedMessage = "status: must not be null";
    var amendmentRule = createValidAmendmentRule();
    amendmentRule.setStatus(null);

    checkErrorThrown(amendmentRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullCreatedAt_shouldValidateAndThrow() {
    String expectedMessage = "createdAt: must not be null";
    var amendmentRule = createValidAmendmentRule();
    amendmentRule.setCreatedAt(null);

    checkErrorThrown(amendmentRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullLastModifiedAt_shouldValidateAndThrow() {
    String expectedMessage = "lastModifiedAt: must not be null";
    var amendmentRule = createValidAmendmentRule();
    amendmentRule.setLastModifiedAt(null);

    checkErrorThrown(amendmentRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_emptyRateType_shouldValidateAndThrow() {
    String expectedMessage = "rateType: must not be empty";

    checkErrorThrown(() -> createValidAmendmentRule().toBuilder().rateType(null).build(),
        expectedMessage);
  }

  @Test
  void validate_emptyCountryCode_shouldValidateAndThrow() {
    String expectedMessage = "countryCode: must not be empty";

    checkErrorThrown(() -> createValidAmendmentRule().toBuilder().countryCode("").build(),
        expectedMessage);
  }

  @Test
  void validate__shouldValidateOk() {
    assertDoesNotThrow(() -> createValidAmendmentRule().validateSelf());
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

  AmendmentRule createValidAmendmentRule() {
    return AmendmentRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(LocalDateTime.now())
        .lastModifiedAt(TIME)
        .rateType("Flex")
        .countryCode("GB")
        .build();
  }
}
