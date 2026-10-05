package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.eckoh.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.basket.domain.model.validation.ValidatorFactory;

class EckohAmountDtoTest {
  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void verifyFields() {
    assertDoesNotThrow(() -> EckohAmountDto.builder()
        .currency("GBP")
        .minorUnits(3)
        .build());
  }
}
