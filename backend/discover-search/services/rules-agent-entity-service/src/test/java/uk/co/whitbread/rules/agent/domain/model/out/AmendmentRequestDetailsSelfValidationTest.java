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

class AmendmentRequestDetailsSelfValidationTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
      "yyyyMMdd'T'HHmmss");

  @Test
  void constructor_emptyRateType_shouldSelfValidateAndThrow() {
    String expectedMessage = "rateType: must not be empty";

    checkErrorThrown(() -> AmendmentRequestDetails.builder()
        .rateType("")
        .arrivalDate(LocalDate.parse("20220501", DATE_FORMATTER))
        .hotelLocalDateTime(LocalDateTime.parse("20220501T102230", DATE_TIME_FORMATTER))
        .hotelCountryCode("GB")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyHotelCountryCode_shouldSelfValidateAndThrow() {
    String expectedMessage = "hotelCountryCode: must not be empty";

    checkErrorThrown(() -> AmendmentRequestDetails.builder()
        .rateType("Flex")
        .arrivalDate(LocalDate.parse("20220501", DATE_FORMATTER))
        .hotelLocalDateTime(LocalDateTime.parse("20220501T102230", DATE_TIME_FORMATTER))
        .hotelCountryCode("")
        .build(), expectedMessage);
  }

  @Test
  void constructor_nullArrivalDate_shouldSelfValidateAndThrow() {
    String expectedMessage = "arrivalDate: must not be null";

    checkErrorThrown(() -> AmendmentRequestDetails.builder()
        .rateType("Flex")
        .arrivalDate(null)
        .hotelLocalDateTime(LocalDateTime.parse("20220501T102230", DATE_TIME_FORMATTER))
        .hotelCountryCode("GB")
        .build(), expectedMessage);
  }

  @Test
  void constructor_nullHotelLocalDateTime_shouldSelfValidateAndThrow() {
    String expectedMessage = "hotelLocalDateTime: must not be null";

    checkErrorThrown(() -> AmendmentRequestDetails.builder()
        .rateType("Flex")
        .arrivalDate(LocalDate.parse("20220501", DATE_FORMATTER))
        .hotelLocalDateTime(null)
        .hotelCountryCode("GB")
        .build(), expectedMessage);
  }

  @Test
  void constructor__shouldSelfValidateOk() {
    assertDoesNotThrow(() -> {
      AmendmentRequestDetails.builder()
          .rateType("Flex")
          .arrivalDate(LocalDate.parse("20220501", DATE_FORMATTER))
          .hotelLocalDateTime(LocalDateTime.parse("20220501T102230", DATE_TIME_FORMATTER))
          .hotelCountryCode("GB")
          .build();
    });
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }
}
