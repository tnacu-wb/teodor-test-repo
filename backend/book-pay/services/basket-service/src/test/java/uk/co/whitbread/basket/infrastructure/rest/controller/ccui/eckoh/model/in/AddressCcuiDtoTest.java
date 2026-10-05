package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.eckoh.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.validation.ValidBillingAddressValidator.INVALID_POSTCODE;
import static uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.validation.ValidBillingAddressValidator.NULL_POSTCODE;
import static uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.validation.ValidBillingAddressValidator.POSTCODE_LONG;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.basket.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in.AddressDto;
import uk.co.whitbread.basket.utils.TestUtils;


class AddressCcuiDtoTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void errorCasePostalCode() {
    TestUtils.checkErrorThrown(AddressDto.builder()
        .addressLine1("193-197 High Holborn")
        .country("GB")
        .postalCode("bla bla")
        .build()::validateSelf, INVALID_POSTCODE);
  }

  @Test
  void errorCasePostalCodeDE() {
    TestUtils.checkErrorThrown(AddressDto.builder()
        .addressLine1("193-197 High Holborn")
        .country("GB")
        .postalCode("20038")
        .build()::validateSelf, INVALID_POSTCODE);
  }

  @Test
  void errorCasePostalCodeGB() {
    TestUtils.checkErrorThrown(AddressDto.builder()
        .addressLine4("Berlin")
        .addressLine1("193-197 High Holborn")
        .country("DE")
        .postalCode("L1 8JQ")
        .build()::validateSelf, INVALID_POSTCODE);
  }

  @Test
  void errorPostalCodeLength() {
    TestUtils.checkErrorThrown(AddressDto.builder()
        .addressLine1("193-197 High Holborn")
        .country("IE")
        .postalCode("askdjslkajdakl")
        .build()::validateSelf, POSTCODE_LONG);
  }

  @Test
  void verifyPostalCodeLength() {
    assertDoesNotThrow(() -> AddressDto.builder()
        .addressLine1("193-197 High Holborn")
        .country("IE")
        .postalCode("ds")
        .build());
  }

  @Test
  void verifyNullGBPostalCode() {
    TestUtils.checkErrorThrown(AddressDto.builder()
        .addressLine1("adasdsad")
        .country("GB")
        .build()::validateSelf, NULL_POSTCODE);
  }

  @Test
  void verifyNullDEPostalCode() {
    TestUtils.checkErrorThrown(AddressDto.builder()
        .addressLine4("Berlin")
        .addressLine1("adasdsad")
        .country("DE")
        .build()::validateSelf, NULL_POSTCODE);
  }

  @Test
  void verifyMandatoryFieldsDE() {
    assertDoesNotThrow(() -> AddressDto.builder()
        .addressLine1("Berlin")
        .addressLine4("Location")
        .country("DE")
        .postalCode("10115")
        .build());
  }
}
