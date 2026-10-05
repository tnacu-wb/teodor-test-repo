package uk.co.whitbread.kiosk.domain.model.checkin.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.validation.Validation;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.kiosk.domain.model.validation.ValidatorFactory;

class PaymentDetailsTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(() -> PaymentDetails.builder()
        .amount(BigDecimal.ONE)
        .card(new CardRequest())
        .offline(true)
        .provider("4IP")
        .build());
  }
}
