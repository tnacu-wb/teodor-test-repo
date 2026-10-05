package uk.co.whitbread.rules.agent.domain.model.out;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import jakarta.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class PaypalRuleValidationTest {

  private static final LocalDateTime TIME = LocalDateTime.now();

  @Test
  void validate_nullRuleId_shouldValidateAndThrow() {
    String expectedMessage = "ruleId: must not be null";
    var paypalRule = createValidPaypalRule();
    paypalRule.setRuleId(null);

    checkErrorThrown(paypalRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullStatus_shouldValidateAndThrow() {
    String expectedMessage = "status: must not be null";
    var paypalRule = createValidPaypalRule();
    paypalRule.setStatus(null);

    checkErrorThrown(paypalRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullCreatedAt_shouldValidateAndThrow() {
    String expectedMessage = "createdAt: must not be null";
    var paypalRule = createValidPaypalRule();
    paypalRule.setCreatedAt(null);

    checkErrorThrown(paypalRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullLastModifiedAt_shouldValidateAndThrow() {
    String expectedMessage = "lastModifiedAt: must not be null";
    var paypalRule = createValidPaypalRule();
    paypalRule.setLastModifiedAt(null);

    checkErrorThrown(paypalRule::validateSelf, expectedMessage);
  }

  @Test
  void validate_nullCountry_shouldValidateAndThrow() {
    String expectedMessage = "countryCode: must not be empty";

    checkErrorThrown(() -> createValidPaypalRule().toBuilder().countryCode(null).build(),
        expectedMessage);
  }

  @Test
  void validate_emptyChannelId_shouldValidateAndThrow() {
    String expectedMessage = "channelId: must not be empty";

    checkErrorThrown(() -> createValidPaypalRule().toBuilder().channelId(null).build(),
        expectedMessage);
  }

  @Test
  void validate__shouldValidateOk() {
    assertDoesNotThrow(() -> createValidPaypalRule().validateSelf());
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

  PaypalRule createValidPaypalRule() {
    return PaypalRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(LocalDateTime.now())
        .lastModifiedAt(TIME)
        .channelId("PI")
        .countryCode("GB")
        .hotelId("testHotel")
        .build();
  }
}

