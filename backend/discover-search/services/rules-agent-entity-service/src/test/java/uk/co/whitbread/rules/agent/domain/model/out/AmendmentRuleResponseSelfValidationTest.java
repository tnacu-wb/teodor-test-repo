package uk.co.whitbread.rules.agent.domain.model.out;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.co.whitbread.rules.agent.domain.model.validation.ValidatorFactory;

class AmendmentRuleResponseSelfValidationTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());
  private final LocalDateTime time = LocalDateTime.now();

  @Test
  void constructor_nullIsAmendable_shouldSelfValidateAndThrow() {
    String expectedMessage = "isAmendable: must not be null";

    checkErrorThrown(() -> AmendmentRuleResponse.builder()
        .isAmendable(null)
        .requestDetails(createRequestDetails())
        .generatedAt(time)
        .build(), expectedMessage);
  }

  @Test
  void constructor_nullRequestDetails_shouldSelfValidateAndThrow() {
    String expectedMessage = "requestDetails: must not be null";

    checkErrorThrown(() -> AmendmentRuleResponse.builder()
        .isAmendable(true)
        .requestDetails(null)
        .generatedAt(time)
        .build(), expectedMessage);
  }

  @Test
  void constructor_nullGeneratedAt_shouldSelfValidateAndThrow() {
    String expectedMessage = "generatedAt: must not be null";

    checkErrorThrown(() -> AmendmentRuleResponse.builder()
        .isAmendable(true)
        .requestDetails(createRequestDetails())
        .generatedAt(null)
        .build(), expectedMessage);
  }

  @Test
  void constructor__shouldSelfValidateOk() {
    assertDoesNotThrow(() -> {
      AmendmentRuleResponse.builder()
          .isAmendable(true)
          .requestDetails(createRequestDetails())
          .generatedAt(time)
          .build();
    });
  }

  private AmendmentRequestDetails createRequestDetails() {
    return AmendmentRequestDetails.builder()
        .rateType("Flex")
        .arrivalDate(LocalDate.parse("20220501", DateTimeFormatter.BASIC_ISO_DATE))
        .hotelLocalDateTime(LocalDateTime.parse("20220501T102230",
            DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss")))
        .hotelCountryCode("GB")
        .build();
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }
}
