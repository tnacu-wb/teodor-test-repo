package uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.kiosk.domain.model.validation.ValidatorFactory;

class CardRequestDtoTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(() -> CardRequestDto.builder()
        .cardholderName("Test")
        .token("jhefgjkaskjfhads")
        .expiryMonth("05")
        .expiryYear("2025")
        .build());
  }

}
