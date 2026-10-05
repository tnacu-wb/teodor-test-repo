package uk.co.whitbread.basket.domain.model.payments.in;

import static java.math.BigDecimal.valueOf;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.basket.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.basket.utils.TestUtils;

class AmountTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(() -> Amount.builder()
        .currency("GBP")
        .minorUnits(valueOf(1000))
        .build());
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSet() {

    String[] errors = {"currency: must not be empty",
        "minorUnits: must not be null"
    };

    TestUtils.checkErrorThrown(() -> Amount.builder().build(), errors);

  }

}
