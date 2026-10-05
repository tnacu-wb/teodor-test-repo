package uk.co.whitbread.kiosk.infrastructure.rest.controller.allocate.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import jakarta.validation.Validation;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.kiosk.domain.model.validation.ValidatorFactory;
import uk.co.whitbread.kiosk.infrastructure.rest.controller.kiosk.model.in.RoomIdsDto;

class RoomIdsDtoTest {

  private final ValidatorFactory validatorFactory = ValidatorFactory.getInstance(
      Validation.buildDefaultValidatorFactory().getValidator()
  );

  @Test
  void verifyMandatoryFields() {
    assertDoesNotThrow(() -> RoomIdsDto.builder()
        .roomIds(Arrays.asList("100", "102", "103", "104"))
        .build());
  }

}
