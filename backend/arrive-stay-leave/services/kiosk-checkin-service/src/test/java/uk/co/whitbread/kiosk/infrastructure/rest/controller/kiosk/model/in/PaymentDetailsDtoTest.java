package uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.in;

import jakarta.validation.Validation;
import java.math.BigDecimal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.kiosk.domain.model.validation.ValidatorFactory;

class PaymentDetailsDtoTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void verifyMandatoryFields() {
    Assertions.assertDoesNotThrow(() -> PaymentDetailsDto.builder()
        .amount(BigDecimal.valueOf(2647.64))
        .offline(false)
        .provider("Provider")
        .build());
  }

}
