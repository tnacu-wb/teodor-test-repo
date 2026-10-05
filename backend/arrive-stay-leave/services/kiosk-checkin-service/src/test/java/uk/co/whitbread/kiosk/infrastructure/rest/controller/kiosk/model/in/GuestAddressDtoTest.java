package uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.kiosk.domain.model.validation.ValidatorFactory;

class GuestAddressDtoTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(() -> GuestAddressDto.builder()
        .addressLine1("")
        .addressLine2("")
        .addressLine3("")
        .addressLine4("")
        .addressType("")
        .cityName("MANOLD")
        .countryCode("gb")
        .emailAddress("jkfadg@email.com")
        .postalCode("NR1 YN")
        .telephoneNumber("8745682475")
        .build());
  }

}
