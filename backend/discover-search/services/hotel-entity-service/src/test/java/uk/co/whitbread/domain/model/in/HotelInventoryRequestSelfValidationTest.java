package uk.co.whitbread.domain.model.in;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import uk.co.whitbread.domain.model.availability.in.HotelInventoryRequest;

class HotelInventoryRequestSelfValidationTest {

  @Test
  void constructor_emptyHotelId_shouldSelfValidateAndThrow() {
    String expectedMessage = "hotelId: must not be null";

    checkErrorThrown(() -> HotelInventoryRequest.builder()
        .dateRangeStart("2022-10-28")
        .dateRangeEnd("2022-10-30")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyDateRangeStart_constructor_emptyHotelId_shouldSelfValidateAndThrow() {
    String expectedMessage = "dateRangeStart: must not be blank";

    checkErrorThrown(() -> HotelInventoryRequest.builder()
        .hotelId("MANOLD")
        .dateRangeEnd("2022-10-30")
        .build(), expectedMessage);
  }

  @Test
  void constructor_emptyDateRangeEnd_constructor_emptyHotelId_shouldSelfValidateAndThrow() {
    String expectedMessage = "dateRangeEnd: must not be blank";

    checkErrorThrown(() -> HotelInventoryRequest.builder()
        .hotelId("MANOLD")
        .dateRangeStart("2022-10-28")
        .build(), expectedMessage);
  }

  @Test
  void constructor_shouldSelfValidateOk() {
    assertDoesNotThrow(() -> {
      HotelInventoryRequest.builder()
          .hotelId("MANOLD")
          .dateRangeStart("2022-10-28")
          .dateRangeEnd("2022-10-30")
          .build();
    });
  }

  private void checkErrorThrown(Executable executable, String expectedMessage) {
    var thrownException = assertThrowsExactly(ConstraintViolationException.class, executable);
    String actualMessage = thrownException.getMessage();

    assertEquals(expectedMessage, actualMessage);
  }

}
