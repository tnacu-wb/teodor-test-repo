package uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.in;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.kiosk.domain.model.validation.ValidatorFactory;

class StayingGuestDetailsDtoTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void verifyMandatoryFields() {
    Assertions.assertDoesNotThrow(() -> StayingGuestDetailsDto.builder()
        .title("Primary")
        .firstName("Test")
        .lastName("Name")
        .nationality("UK")
        .nextDestination("US")
        .passport("yes")
        .build());
  }

}
