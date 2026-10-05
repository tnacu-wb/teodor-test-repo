package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.validation.ValidBillingAddressValidator.INVALID_POSTCODE;
import static uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.validation.ValidBillingAddressValidator.NULL_POSTCODE;
import static uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.validation.ValidBillingAddressValidator.POSTCODE_LONG;

import jakarta.validation.Validation;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.basket.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.basket.utils.TestUtils;

class AddressDtoTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void verifyMandatoryFieldsGB() {
    assertDoesNotThrow(() -> AddressDto.builder()
        .addressLine1("120 Holborn")
        .country("GB")
        .postalCode("L1 8JQ")
        .build());
  }

  @Test
  void AddressLine_Special_Charecters_Should_Ok() {
    assertDoesNotThrow(() -> AddressDto.builder()
        .addressLine1("08908 HOSPITALET DE LLOBREGAT&#xd;&#xa;")
        .addressLine2("REISEBUERO GMBH &amp; CO. KG")
        .addressLine3("Forto TeͶst .CO Ö12345")
        .addressLine4("Cling )#(Ͷ-ͷͻ-ͽvΑ-Ͽἀ-ῼЀ-ӿễấßÜÖÄü")
        .country("GB")
        .postalCode("L1 8JQQ")
        .build());
  }

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

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSet() {
    var addressDto = AddressDto.builder().build();
    assertThrows(ValidationException.class, addressDto::validateSelf);
  }
}
