package uk.co.whitbread.address.lookup.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.address.lookup.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.address.lookup.utils.TestUtils;

class AddressSearchRequestTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory()
          .getValidator());

  @Test
  void constructor_shouldSelfValidateOk() {
    assertDoesNotThrow(() -> {
      AddressSearchRequest.builder()
          .searchTerm("LU1 1BN")
          .build();
    });
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSet() {
    String[] errors = {"searchTerm: must not be empty"};
    TestUtils.checkErrorThrown(() -> AddressSearchRequest.builder().build(), errors);
  }
}
