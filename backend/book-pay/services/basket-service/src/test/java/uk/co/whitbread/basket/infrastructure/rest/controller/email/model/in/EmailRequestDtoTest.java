package uk.co.whitbread.basket.infrastructure.rest.controller.email.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.basket.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.basket.utils.TestUtils;

class EmailRequestDtoTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(() -> EmailRequestDto.builder()
        .bookingReference("BKR-ace3a445-ebd1-4404-9315-d3df37a0b464")
        .email("test@whitbread.com")
        .emailRequestType("AMEND")
        .build());
  }

  @Test
  void verifyErrorMessageWhenMandatoryFieldsAreNotSet() {

    String[] errors = {"bookingReference: must not be null"};

    TestUtils.checkErrorThrown(() -> EmailRequestDto.builder().build(), errors);

  }
}