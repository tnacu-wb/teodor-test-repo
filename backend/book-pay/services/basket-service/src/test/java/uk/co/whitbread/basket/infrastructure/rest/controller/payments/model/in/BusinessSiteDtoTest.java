package uk.co.whitbread.basket.infrastructure.rest.controller.payments.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.basket.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.basket.utils.TestUtils;

class BusinessSiteDtoTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(() -> BusinessSiteDto.builder()
        .identifier("identifier")
        .location("location")
        .name("name")
        .type("type")
        .build());
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSet() {

    String[] errors = {"identifier: must not be empty",
        "type: must not be empty"
    };

    TestUtils.checkErrorThrown(() -> BusinessSiteDto.builder().build(), errors);

  }

}
