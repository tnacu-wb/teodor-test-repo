package uk.co.whitbread.basket.infrastructure.rest.controller.basket.model.in;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Collections;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.basket.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.basket.utils.TestUtils;

class AddBasketItemRequestDtoTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(() -> AddBasketItemRequestDto.builder()
        .reference("reference")
        .lockingTime("something")
        .itemTypes(of(AddBasketItemTypeDto.builder()
            .confirmationData(of("First element"))
            .type("type")
            .build()))
        .items(of(AddBasketItemDto.builder()
            .type("type")
            .sourceId("sourceId")
            .details(Collections.emptyMap())
            .build()))
        .build());
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSet() {

    String[] errors = {"reference: must not be null",
        "itemTypes: must not be empty",
        "items: must not be empty",
    };

    TestUtils.checkErrorThrown(() -> AddBasketItemRequestDto.builder().build(), errors);

  }

}
