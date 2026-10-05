package uk.co.whitbread.basket.domain.model.payments.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.basket.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.basket.utils.TestUtils;

class AddressTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(() -> Address.builder()
        .line1("120 Holborn")
        .line2("")
        .line3("")
        .line4("")
        .cityName("Big Smoke")
        .countryCode("GB")
        .postalCode("1MB4")
        .build());
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSet() {
    String[] errors = {"must not be empty"};
    TestUtils.checkErrorThrown(() -> Address.builder().build().validateAddress(), errors);
  }

}
