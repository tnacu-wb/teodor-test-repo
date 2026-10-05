package uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.kiosk.domain.model.validation.ValidatorFactory;

class CheckInRequestDtoTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(() -> CheckInRequestDto.builder()
        .hotelId("MANOLD")
        .roomId("102")
        .reservationNumber("123456")
        .roomType("PPLDBL")
        .build());
  }

}
