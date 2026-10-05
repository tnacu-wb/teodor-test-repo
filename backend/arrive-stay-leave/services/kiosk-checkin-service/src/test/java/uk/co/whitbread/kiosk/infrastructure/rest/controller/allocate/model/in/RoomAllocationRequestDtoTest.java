package uk.co.whitbread.kiosk.infrastructure.rest.controller.allocate.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.kiosk.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.in.RoomAllocationRequestDto;

class RoomAllocationRequestDtoTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(() -> RoomAllocationRequestDto.builder()
        .roomType("PPLDBL")
        .hotelId("MANOLD")
        .reservationId("123456")
        .build());
  }


}
