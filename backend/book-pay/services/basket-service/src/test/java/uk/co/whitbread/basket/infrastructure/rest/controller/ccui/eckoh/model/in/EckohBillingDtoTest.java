package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.eckoh.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.basket.domain.model.validation.ValidatorFactory;

class EckohBillingDtoTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void verifyFields() {
    assertDoesNotThrow(() -> {
      var address = new EckohAddressDto();
      address.setLine1("line1");
      address.setCountryCode("GB");
      address.setPostalCode("postalCode");

      var billing = new EckohBillingDto();
      billing.setAddress(address);
    });
  }

}
