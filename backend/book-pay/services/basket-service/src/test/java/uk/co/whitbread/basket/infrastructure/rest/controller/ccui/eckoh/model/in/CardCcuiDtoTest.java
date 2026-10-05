package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.eckoh.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.basket.domain.model.validation.ValidatorFactory;

class CardCcuiDtoTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void verifyNoExceptionIsThrown() {
    assertDoesNotThrow(() -> CardCcuiDto.builder()
        .build());
  }

  @Test
  void verifyFields() {
    assertDoesNotThrow(() -> CardCcuiDto.builder()
        .cardHolderLastName("lastName")
        .cardHolderFirstName("firstName")
        .cardHolderAddress(AddressCcuiDto.builder()
              .addressLine1("address1")
              .addressLine2("address2")
              .country("GB")
              .postalCode("postalCode")
              .build())
        .build());
  }
}
