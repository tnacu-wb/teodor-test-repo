package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.basket.domain.model.validation.ValidatorFactory;

class BillingDtoTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void verifyNoExceptionIsThrown() {
    assertDoesNotThrow(() -> BillingDto.builder()
        .build());
  }

}
