package uk.co.whitbread.kiosk.domain.model.checkin.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.kiosk.domain.model.validation.ValidatorFactory;

class GuestAddressTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(() -> GuestAddress.builder()
        .addressLine1("")
        .addressLine2("")
        .addressLine3("")
        .addressLine4("")
        .cityName("London")
        .companyName("Whitbread")
        .addressType("Primary")
        .countryCode("UK")
        .postalCode("NR1 YP")
        .emailAddress("sakdhgf@kfjads.com")
        .mobileNumber("21324325245")
        .telephoneNumber("3523452435")
        .build());
  }
}
