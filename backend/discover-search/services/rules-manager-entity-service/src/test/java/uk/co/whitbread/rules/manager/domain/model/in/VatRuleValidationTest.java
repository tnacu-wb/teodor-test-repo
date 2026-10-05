package uk.co.whitbread.rules.manager.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.LocalDateTime;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class VatRuleValidationTest {

  private static final LocalDateTime TIME = LocalDateTime.now();

  @Test
  void validate_nullRuleId_shouldValidateAndThrow() {
    String expectedMessage = "ruleId: must not be null";
    var vatRule = createValidVatRule();
    vatRule.setRuleId(null);

    checkErrorThrown(vatRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullStatus_shouldValidateAndThrow() {
    String expectedMessage = "status: must not be null";
    var vatRule = createValidVatRule();
    vatRule.setStatus(null);

    checkErrorThrown(vatRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullCreatedAt_shouldValidateAndThrow() {
    String expectedMessage = "createdAt: must not be null";
    var vatRule = createValidVatRule();
    vatRule.setCreatedAt(null);

    checkErrorThrown(vatRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullLastModifiedAt_shouldValidateAndThrow() {
    String expectedMessage = "lastModifiedAt: must not be null";
    var vatRule = createValidVatRule();
    vatRule.setLastModifiedAt(null);

    checkErrorThrown(vatRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullVatRegion_shouldValidateAndThrow() {
    String expectedMessage = "vatRegion: must not be empty";

    checkErrorThrown(
        () -> createValidVatRule().toBuilder().vatRegion(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate_nullTranCode_shouldValidateAndThrow() {
    String expectedMessage = "tranCode: must not be empty";

    checkErrorThrown(
        () -> createValidVatRule().toBuilder().tranCode(null).build().validateSelf(),
        expectedMessage);
  }

  @Test
  void validate__shouldValidateOk() {
    assertDoesNotThrow(() -> createValidVatRule().validateSelf());
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

  VatRule createValidVatRule() {
    return VatRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(TIME)
        .lastModifiedAt(TIME)
        .vatRegion("UK")
        .tranCode("9026")
        .description("Prepayment (9% VAT) Breakfast")
        .build();
  }
}
