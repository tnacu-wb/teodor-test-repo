package uk.co.whitbread.basket.domain.model.basket.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Collections;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.basket.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.basket.utils.TestUtils;

class AddBasketItemTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(() -> AddBasketItem.builder()
        .details(Collections.emptyMap())
        .sourceId("sourceId")
        .type("type")
        .build());
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSet() {

    String[] errors = {"type: must not be empty",
        "sourceId: must not be empty"};

    TestUtils.checkErrorThrown(() -> AddBasketItem.builder().build(), errors);

  }

}
