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

class MaxNightsRuleResponseSelfValidationTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());

  private final LocalDateTime time = LocalDateTime.now();

  @Test
  void constructor_nulllMaxNights_shouldSelfValidateAndThrow() {
    String expectedMessage = "maxNights: must not be null";

    checkErrorThrown(() -> MaxNightsRuleResponse.builder().requestDetails(createMaxNightsDetails())
        .maxNights(null)
        .generatedAt(time).build(), expectedMessage);
  }

  @Test
  void constructor_nulllRequestDetails_shouldSelfValidateAndThrow() {
    String expectedMessage = "requestDetails: must not be null";

    checkErrorThrown(() -> MaxNightsRuleResponse.builder().requestDetails(null)
        .maxNights(9).generatedAt(time).build(), expectedMessage);
  }

  @Test
  void constructor_nulllGeneratedAt_shouldSelfValidateAndThrow() {
    String expectedMessage = "generatedAt: must not be null";

    checkErrorThrown(() -> MaxNightsRuleResponse.builder().requestDetails(
            createMaxNightsDetails())
        .maxNights(9).generatedAt(null).build(), expectedMessage);
  }

  @Test
  void constructor__shouldSelfValidateOk() {
    assertDoesNotThrow(() -> {
      MaxNightsRuleResponse.builder()
          .generatedAt(time)
          .requestDetails(createMaxNightsDetails())
          .maxNights(9)
          .build();
    });
  }

  private MaxNightsRequestDetails createMaxNightsDetails() {
    return MaxNightsRequestDetails.builder()
        .channelId("CCUI")
        .build();
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {

    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }
}
