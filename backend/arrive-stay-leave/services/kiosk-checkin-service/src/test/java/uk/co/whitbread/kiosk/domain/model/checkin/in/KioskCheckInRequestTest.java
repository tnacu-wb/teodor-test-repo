package uk.co.whitbread.kiosk.domain.model.checkin.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.kiosk.domain.model.validation.ValidatorFactory;

class KioskCheckInRequestTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(() -> KioskCheckInRequest.builder()
        .roomId("102")
        .hotelId("MANOLD")
        .reservationNumber("1635423")
        .build());
  }
}
