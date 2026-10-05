package uk.co.whitbread.basket.domain.model.basket.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.basket.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.basket.utils.TestUtils;

class CreateBasketRequestTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(() -> CreateBasketRequest.builder()
        .hotelId("LONEUS")
        .userId("userId")
        .build());
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSet() {

    String[] errors = {"hotelId: must not be empty"};

    TestUtils.checkErrorThrown(() -> CreateBasketRequest.builder()
        .hotelId("")
        .userId("userId")
        .build(), errors);
  }

}
