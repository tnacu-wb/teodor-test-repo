package uk.co.whitbread.kiosk.domain.model.checkin.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.kiosk.domain.model.validation.ValidatorFactory;

class CardRequestTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(() -> CardRequest.builder()
        .cardholderName("Test")
        .token("LKsjfasgdfjyasgdjyf")
        .expiryMonth("today")
        .expiryYear("thisyear")
        .build());
  }
}
